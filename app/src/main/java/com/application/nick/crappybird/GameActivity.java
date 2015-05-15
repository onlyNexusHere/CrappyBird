package com.application.nick.crappybird;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.nick.crappybird.scene.GameScene;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.parse.Parse;
import com.parse.ParseUser;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.CroppedResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.io.File;


public class GameActivity extends LayoutGameActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Tph6xDindFcHhG1ppVMNoSbMj";
    private static final String TWITTER_SECRET = "3TSVql418kxQkN9vTzhfo9y0baprccJgCgYjBipu7pwGPX7Kqj";


    public static final int CAMERA_WIDTH = 320;
    public static final int CAMERA_HEIGHT = 533;

    private Camera mCamera;
    private ResourceManager mResourceManager;
    private SceneManager mSceneManager;

   private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //twitter stuff
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Fabric.with(this, new TweetComposer());

        //create banner ad from admob
        createBannerAd();

        //Parse
        Parse.initialize(this, "YpJ9WQuoN4XQYw2y0YOQRLvzSBEsskGpWebUgWzf", "4VswpUtUtyVdWSWrtugliH1zdkTOY91uoXm6kjMF");
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new CroppedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        engineOptions.getAudioOptions().setNeedsSound(true).setNeedsMusic(true);
        return engineOptions;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
            throws Exception {
        mResourceManager = ResourceManager.getInstance();
        mResourceManager.prepare(this);
        mResourceManager.loadSplashResources();

        mSceneManager = SceneManager.getInstance();

        pOnCreateResourcesCallback.onCreateResourcesFinished();

    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
            throws Exception {

        mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                mResourceManager.loadGameResources();
                mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                mResourceManager.unloadSplashResources();
            }
        }));
        pOnCreateSceneCallback.onCreateSceneFinished(mSceneManager.createSplashScene());
    }

    @Override
    public void onPopulateScene(Scene pScene,
                                OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.SurfaceViewId;
    }


    public void createBannerAd() {

        mAdView = (AdView) findViewById(R.id.adViewId);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("B9BF21FBE22B0C2B4AC09A79D8D26A77")
                .build();
        mAdView.loadAd(adRequest);

    }

    public void openTwitterShare(int score) {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("I just scored " + score + " points in Crappy Bird! This game is awesome! #crappybird #addicting #craptastic https://goo.gl/eDWvTO");
        builder.show();

    }

    public void openFacebookShare(int score) {

        String application = "com.facebook.katana";

        String url = "https://goo.gl/eDWvTO";


        Intent intent = this.getPackageManager().getLaunchIntentForPackage(application);
        if (intent != null) {
            // The application exists
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(application);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);
            // Start the specific social application
            startActivity(shareIntent);
        } else {
            // The application does not exist

            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
            Intent shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            startActivity(shareIntent);

        }

    }

    public void openOtherShare(int score) {
        String message = "I just scored " + score + " points in Crappy Bird! This game is awesome. Get it on the Play Store and try to beat my score. https://goo.gl/eDWvTO";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, "Share"));
    }

    public void openRate() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            CharSequence text = "Could not launch Play Store";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 100);
    }

    public void openSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, 100);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 100) {
            if(resultCode == 200){ //result code is only 200 if a user logged in or signed up
                syncScores();
                mSceneManager.setScene(SceneManager.SceneType.SCENE_LEADERBOARD);
            }
        }
    }

    /**
     * this method syncs the local max score with the saved score on the user's account
     */
    public void syncScores() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            int savedScore = currentUser.getInt("highScore");
            if(savedScore > getMaxScore()) {
                setMaxScore(savedScore);
            } else {
                currentUser.put("highScore", getMaxScore());
                currentUser.saveInBackground();
            }
        }
    }

    public void displayConnectionError() {
        final CharSequence text = "Please check your connection and try again";

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public int getMaxScore() {
        return getPreferences(Context.MODE_PRIVATE).getInt("maxScore", 0);
    }

    public void setMaxScore(int maxScore) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt("maxScore", maxScore).commit();
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        if (mSceneManager.getCurrentScene() != null) {
            mSceneManager.getCurrentScene().onBackKeyPressed();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        if (mResourceManager.mMusic!=null && mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.pause();
        }

        if(mSceneManager.getCurrentSceneType() == SceneManager.SceneType.SCENE_GAME) {
            ((GameScene)mSceneManager.getCurrentScene()).setPause(true);
        }

        super.onPause();

    }

}
