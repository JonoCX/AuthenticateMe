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
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Map;
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

    private Button authBtn;

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
            processFeed();
        }

        authBtn = (Button) findViewById(R.id.auth_btn);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClick(view);
            }
        });
    }

    private void btnClick(View view) {
        // TODO
    }

    private void processFeed() {
        TopicDetection detection = new TopicDetection(getResources().getString(R.string.monkey_learn_api_key));
        Log.i("Feed", feed.toString());
        String[] arr = feed.toArray(new String[0]);
        Map<String, org.json.simple.JSONArray> result;
        result = detection.detectTopics(arr);
        Log.i("result", String.valueOf(result));
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
