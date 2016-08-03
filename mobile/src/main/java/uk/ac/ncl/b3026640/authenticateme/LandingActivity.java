/**
 * @author Jonathan Carlton
 */

package uk.ac.ncl.b3026640.authenticateme;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LandingActivity extends AppCompatActivity {

    private TwitterSession twitterSession;
    private SharedPreferences preferences;

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private String tUsername;
    private long tID;

    private String fID;
    private String fToken;
    private JSONArray fbFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_activitiy);

        // it's a singleton so you're able to access the active session
        if (getIntent().getStringExtra("login_method").equals("twitter")) {
            twitterSession = Twitter.getSessionManager().getActiveSession();
            tUsername = twitterSession.getUserName();
            tID = twitterSession.getUserId();
            collectTweets();
        }
        else {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            fToken = preferences.getString("fb_access_token", null);
            fID = preferences.getString("user_id", null);
            fbFeed = new JSONArray();
            handleFB();
        }
    }

    private void collectTweets() {
        TwitterCore.getInstance().getApiClient(twitterSession).getStatusesService()
                .userTimeline(tID, tUsername, 20, null, null, null, null, null, null,
                        new Callback<List<Tweet>>() {
                            @Override
                            public void success(Result<List<Tweet>> result) {
                                for (Tweet t : result.data) {
                                    tweets.add(t);
                                }
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                Log.d("Tweet Fetching Excep", "Exception: " + exception);
                            }
                        });
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

    /**
     *
     */
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

    private void sendToServer() {

    }
}
