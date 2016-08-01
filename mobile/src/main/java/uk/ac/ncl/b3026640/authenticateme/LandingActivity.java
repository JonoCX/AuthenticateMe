package uk.ac.ncl.b3026640.authenticateme;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.applinks.FacebookAppLinkResolver;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

public class LandingActivity extends AppCompatActivity {

    private TwitterSession twitterSession;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_activitiy);

        // it's a singleton so you're able to access the active session
        twitterSession = Twitter.getSessionManager().getActiveSession();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString("fb_access_token", null);
        Log.d("Token", token);

    }
}
