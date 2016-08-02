package uk.ac.ncl.b3026640.authenticateme;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LandingActivity extends AppCompatActivity {

    private TwitterSession twitterSession;
    private SharedPreferences preferences;

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private String tUsername;
    private long tID;

    private String fID;
    private String fToken;
    private JSONObject feed = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_activitiy);

        // it's a singleton so you're able to access the active session
        twitterSession = Twitter.getSessionManager().getActiveSession();
        tUsername = twitterSession.getUserName();
        tID = twitterSession.getUserId();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        fToken = preferences.getString("fb_access_token", null);
        fID = preferences.getString("user_id", null);

        collectTweets();
        collectFBFeed();

        Log.d("fetched_info_2", feed.toString());
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

    private void collectFBFeed() {
        String sFeed = preferences.getString("fb_feed_data", null);
        try {
            feed = new JSONObject(sFeed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
