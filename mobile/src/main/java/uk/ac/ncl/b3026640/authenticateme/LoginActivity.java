package uk.ac.ncl.b3026640.authenticateme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
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
import com.twitter.sdk.android.core.models.User;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
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

    private void setupViews() {
        setupTwitterLogin();
        setupFacebookLogin();
    }

    private void setupTwitterLogin() {
        tLoginBtn = (TwitterLoginButton) findViewById(R.id.login_button_twitter);
        tLoginBtn.setCallback(new LoginHandler());
    }

    private void setupFacebookLogin() {
        fLoginBtn = (LoginButton) findViewById(R.id.login_button);
        fLoginBtn.setReadPermissions("email");
        fLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                editor = preferences.edit();
                editor.putString("fb_access_token", loginResult.getAccessToken().getToken());
                editor.putLong("fb_access_expires", loginResult.getAccessToken().getExpires().getTime());
                editor.apply();
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

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

        @Override
        public void success(Result<TwitterSession> result) {
            Log.d("Success", "Twitter login has been successful by: " + result.data.getUserName());
            // redirect to the new activity
            Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
            startActivity(intent);
        }

        @Override
        public void failure(TwitterException exception) {

        }
    }
}
