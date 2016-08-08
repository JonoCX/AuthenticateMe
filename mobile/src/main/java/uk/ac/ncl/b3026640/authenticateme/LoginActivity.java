package uk.ac.ncl.b3026640.authenticateme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "jXi3FFWRBEFQp9jDXUH5vTZJ2";
    private static final String TWITTER_SECRET = "6ImY6NYymYOY8F3p5iAWwYWwXQMmSb00oTg8sm7Fv0dex8VFSr";

    private String twitterKey;
    private String twitterSecret;

    private TwitterLoginButton tLoginBtn;
    private LoginButton fLoginBtn;
    private CallbackManager callbackManager;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    JSONObject fFeed = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (isLoggedIn()) {
            LoginManager.getInstance().logOut();
        }
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setupViews();



        // Add code to print out the key hash
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "uk.ac.ncl.b3026640.authenticateme",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void setupViews() {
        setupTwitterLogin();
        setupFacebookLogin();
    }

    private void setupTwitterLogin() {
        tLoginBtn = (TwitterLoginButton) findViewById(R.id.login_button_twitter);
        tLoginBtn.setCallback(new LoginHandler());
        // try here instead
    }

    private void setupFacebookLogin() {
        fLoginBtn = (LoginButton) findViewById(R.id.login_button);
        fLoginBtn.setReadPermissions(Arrays.asList("email", "user_posts"));

        fLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private AccessTokenTracker mAccessTokenTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                editor = preferences.edit();
                editor.putString("fb_access_token", loginResult.getAccessToken().getToken());
                editor.putLong("fb_access_expires", loginResult.getAccessToken().getExpires().getTime());
                intent.putExtra("access_token", loginResult.getAccessToken());
                intent.putExtra("fb_user_id", loginResult.getAccessToken().getUserId());
                editor.apply();
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.d("Facebook Login", "Cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook Login", "Error: " + error);
                Toast.makeText(getApplicationContext(), "Failed to login, check your account", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tLoginBtn.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class LoginHandler extends Callback<TwitterSession> {

        ArrayList<String> feed = new ArrayList<>();

        @Override
        public void success(Result<TwitterSession> result) {
            Log.d("Success", "Twitter login has been successful by: " + result.data.getUserName());
            // redirect to the new activity
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            intent.putExtra("login_method", "twitter");
            intent.putExtra("twitter_user_id", result.data.getUserId());
            intent.putExtra("twitter_screen_name", result.data.getUserName());

            TwitterCore.getInstance().getApiClient(result.data).getStatusesService()
                    .userTimeline(result.data.getUserId(), result.data.getUserName(), 20,
                            null, null, null, null, null, null,
                            new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> result) {
                                    for (Tweet t : result.data)
                                        feed.add(t.text);
                                }

                                @Override
                                public void failure(TwitterException exception) {
                                    Log.e("User timeline failure", "Exception: " + exception);
                                }
                            });
            intent.putStringArrayListExtra("twitter_feed", feed);

            startActivity(intent);
            //new LaunchAsTwitter().execute(result.data);
        }

        @Override
        public void failure(TwitterException exception) {
            Log.d("Failure", "Twitter login has failed; " + exception);
        }
    }

    private class LaunchAsTwitter extends AsyncTask<TwitterSession, Void, ArrayList<String>> {

        ArrayList<String> feed = new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(TwitterSession... twitterSessions) {
            TwitterSession twitterSession = twitterSessions[0];
            TwitterCore.getInstance().getApiClient(twitterSession).getStatusesService()
                    .userTimeline(twitterSession.getUserId(), twitterSession.getUserName(), 20,
                            null, null, null, null, null, null,
                            new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> result) {
                                    for (Tweet t : result.data)
                                        feed.add(t.text);
                                }

                                @Override
                                public void failure(TwitterException exception) {
                                    Log.e("User timeline failure", "Exception: " + exception);
                                }
                            });
            return feed;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            intent.putExtra("twitter_feed", feed.toArray());
            intent.putStringArrayListExtra("twitter_feed", feed);
            intent.putExtra("login_method", "twitter");
            startActivity(intent);
        }
    }
}
