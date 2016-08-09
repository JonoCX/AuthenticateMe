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
import com.monkeylearn.MonkeyLearn;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import uk.ac.ncl.b3026640.authenticateme.misc.TopicDetection;


public class LandingActivity extends AppCompatActivity {

    private TwitterSession twitterSession;
    private SharedPreferences preferences;

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private String tUsername;
    private long tID;

    private String fID;
    private String fToken;

    private ArrayList<String> feed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_activitiy);

        if (getIntent().hasExtra("login_method")) {
            twitterSession = Twitter.getSessionManager().getActiveSession();
            tUsername = twitterSession.getUserName();
            tID = twitterSession.getUserId();
            feed = getIntent().getStringArrayListExtra("twitter_feed");
            processFeed();
        } else {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            fToken = preferences.getString("fb_access_token", null);
            fID = preferences.getString("user_id", null);
            handleFB();
        }
        //processFeed();
    }

    private void processFeed() {
        TopicDetection detection = new TopicDetection(getResources().getString(R.string.monkey_learn_api_key));
        Log.i("Feed", feed.toString());
        String[] arr = feed.toArray(new String[0]);
        org.json.simple.JSONArray result = detection.detectTopics(arr);

        Log.i("result", result.toJSONString());
    }

    private boolean checkIfUserExists() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        //mDatabase.child("users").child("1").setValue(user);
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

    private void handleFB() {
        AccessToken token = getIntent().getExtras().getParcelable("access_token");
        JSONObject returnedJson;
        JSONArray feedArray;
        try {
            returnedJson = new FetchFBFeed().execute(token).get();
            feedArray = returnedJson.getJSONObject("feed").getJSONArray("data");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject obj = feedArray.getJSONObject(i);
                if (obj.has("message"))
                    feed.add((String) obj.get("message"));
            }
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
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
        }
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
