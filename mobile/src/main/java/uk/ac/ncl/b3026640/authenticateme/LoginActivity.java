package uk.ac.ncl.b3026640.authenticateme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {


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

        Stetho.initializeWithDefaults(this);

        twitterKey = getResources().getString(R.string.twitter_api_key);
        twitterSecret = getResources().getString(R.string.twitter_api_secret);
        // Twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitterKey, twitterSecret);
        Fabric.with(this, new Twitter(authConfig));

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setupViews();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, Firebase!");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("E_DATA_CHANGES", "Value is: " + value);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
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
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);

            @Override
            public void onSuccess(LoginResult loginResult) {
                editor = preferences.edit();
                editor.putString("fb_access_token", loginResult.getAccessToken().getToken());
                editor.putLong("fb_access_expires", loginResult.getAccessToken().getExpires().getTime());
                intent.putExtra("access_token", loginResult.getAccessToken());
                Log.i("FB_USER_ID", loginResult.getAccessToken().getUserId());
                //intent.putExtra("fb_user_id", loginResult.getAccessToken().getUserId());
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

    /**
     *
     */
    private class LoginHandler extends Callback<TwitterSession> {

        ArrayList<String> feed = new ArrayList<>();
        Intent intent = new Intent(LoginActivity.this, LandingActivity.class);

        @Override
        public void success(Result<TwitterSession> result) {
            Log.d("Success", "Twitter login has been successful by: " + result.data.getUserName());
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

                                    intent.putStringArrayListExtra("twitter_feed", feed);
                                    startActivity(intent);
                                }

                                @Override
                                public void failure(TwitterException exception) {
                                    Log.e("User timeline failure", "Exception: " + exception);
                                }
                            });
        }

        @Override
        public void failure(TwitterException exception) {
            Log.d("Failure", "Twitter login has failed; " + exception);
        }
    }
}
