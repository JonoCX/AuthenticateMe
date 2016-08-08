/**
 * @author Jonathan Carlton
 */

package uk.ac.ncl.b3026640.authenticateme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LandingActivity extends AppCompatActivity {

    private TwitterSession twitterSession;
    private SharedPreferences preferences;

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private String tUsername;
    private long tID;

    private String fID;
    private String fToken;
    private JSONArray fbFeed;

    private ArrayList<String> feed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_activitiy);

        User user;

        Intent intent = getIntent();
        if (getIntent().hasExtra("login_method")) {
            twitterSession = Twitter.getSessionManager().getActiveSession();
            tUsername = twitterSession.getUserName();
            tID = twitterSession.getUserId();
            //collectTweets();
            List<String> res = getIntent().getStringArrayListExtra("twitter_feed");
            for (String s : res)
                Log.i("feed passed!", s);
        } else {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            fToken = preferences.getString("fb_access_token", null);
            fID = preferences.getString("user_id", null);
            fbFeed = new JSONArray();
            handleFB();

        }


    }

    private boolean checkIfUserExists() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        User user = new User.Builder("1").build();
        mDatabase.child("users").child("1").setValue(user);
        mDatabase.child("users").child("1");

        mDatabase.child("users").child("1").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.i("it", child.toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Cancelled", "getUser:onCanceeled", databaseError.toException());
                    }
                }
        );
        return false;
    }

    private void databaseTesting() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("message");
        ref.setValue("Hello, World!");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("Firebase: Data Change", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase: Failure", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void collectTweets() {
        /*TwitterCore.getInstance().getApiClient(twitterSession).getStatusesService()
                .userTimeline(tID, tUsername, 20, null, null, null, null, null, null,
                        new Callback<List<Tweet>>() {
                            @Override
                            public void success(Result<List<Tweet>> result) {
                                for (Tweet t : result.data) {
                                    //Log.i("single tweet", t.text);
                                    //tweets.add(t);
                                    feed.add(t.text);
                                }
                                Log.d("Feed", feed.toString());
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                Log.d("Tweet Fetching Excep", "Exception: " + exception);
                            }
                        }
                );


        // convert into a string list
        List<String> list = new ArrayList<>();*/
        List<String> result;
        try {
            result = new FetchTwitterFeed().execute(twitterSession).get(10, TimeUnit.SECONDS);
            for (String s : result)
                Log.i("loop", s);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    private void handleFB() {
        AccessToken token = getIntent().getExtras().getParcelable("access_token");
        JSONObject returnedJson;
        try {
            returnedJson = new FetchFBFeed().execute(token).get();
            fbFeed = returnedJson.getJSONObject("feed").getJSONArray("data");
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    private class FetchFBFeed extends AsyncTask<AccessToken, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(AccessToken... accessTokens) {
            final JSONObject[] json = {new JSONObject()};
            AccessToken accessToken = accessTokens[0];
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            json[0] = object;
                        }
                    }
            );
            Bundle params = new Bundle();
            params.putString("fields", "feed");
            request.setParameters(params);
            request.executeAndWait();
            return json[0];
        }

        @Override
        protected void onPostExecute(JSONObject result) { super.onPostExecute(result); }
    }

    private class FetchTwitterFeed extends AsyncTask<TwitterSession, Void, List<String>> {

        List<String> result = new ArrayList<>();

        @Override
        protected List<String> doInBackground(TwitterSession... session) {
            //List<String> result = new ArrayList<>();
            TwitterSession twitterSession = session[0];
            TwitterCore.getInstance().getApiClient(twitterSession).getStatusesService()
                    .userTimeline(tID, tUsername, 20, null, null, null, null, null, null,
                    new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> res) {
                            for (Tweet t : res.data) {
                                Log.i("inner", t.text);
                                result.add(t.text);
                            }
                        }

                        @Override
                        public void failure(TwitterException exception) {
                            Log.e("User timeline failure", "Exception: " + exception);
                        }
                    });



            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) { super.onPostExecute(result); }
    }

    public void buildFeed() {
        Intent intent = getIntent();
        if (intent.getStringExtra("login_method").equals("twitter")) {
            long tID = intent.getLongExtra("twitter_user_id", 1L);
        } else {

        }
    }



    private void sendToServer() {

    }


    /**
     * Send a notification to the Pi to say that someone is trying to authenticate
     * Send the data, social media feeds to the firebase server, pull that onto the
     *      pi and then classify (handle it if they have previously authenticated.)
     * If their chatter on social media has drastically changed since the last time
     *      they authenticated, then it could indicate a potential problem. If so
     *      ask for further authentication?
     */
}
