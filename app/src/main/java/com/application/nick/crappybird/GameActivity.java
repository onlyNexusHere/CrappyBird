package com.application.nick.crappybird;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.application.nick.crappybird.scene.GameScene;
import com.application.nick.crappybird.scene.MarketScene;
import com.application.nick.crappybird.util.IabHelper;
import com.application.nick.crappybird.util.IabResult;
import com.application.nick.crappybird.util.Inventory;
import com.application.nick.crappybird.util.Purchase;
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
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;

import java.util.ArrayList;
import java.util.List;


public class GameActivity extends LayoutGameActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Tph6xDindFcHhG1ppVMNoSbMj";
    private static final String TWITTER_SECRET = "3TSVql418kxQkN9vTzhfo9y0baprccJgCgYjBipu7pwGPX7Kqj";
    private final String SKU_1000_PIZZA = "1000pizza";
    private final String SKU_5000_PIZZA = "5000pizza";
    private final String SKU_10000_PIZZA = "10000pizza";

    private static final String[] SKU_PIZZA_PURCHASE = {
            "1000pizza",
            "5000pizza",
            "10000pizza"
    };

    public static final int CAMERA_WIDTH = 320;
    public static final int CAMERA_HEIGHT = 533;

    private Camera mCamera;
    private ResourceManager mResourceManager;
    private SceneManager mSceneManager;

    private AdView mAdView;

    private IabHelper mHelper;

    private String price1000Pizza;
    private String price5000Pizza;
    private String price10000Pizza;

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

        String base64EncodedPublicKey = "";
        base64EncodedPublicKey += "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAysK9OGJEGjJW9XTNZYqs1j9y4x";
        base64EncodedPublicKey += "Q0lIvtzuuqAvwZ74DiKCCQgjsp7/V8VEu0OQu0jsEVjTwPGf42JpaZJrETkivUQbfHSZcBEfQLx3QnJqoh1JO390nrsr6GmPYeXVkS6si5+RQjS8rblhYER5";
        base64EncodedPublicKey += "j9gguDb6idqt8O7SST8wucVV5rehUWFJ7uZ3wX8fuzeiQwsAOUJJks41VW8fyKrcUW7nBOGTfAbQEazXxZa3JLlsaVlhZPz+NgCf4fk6eqD0GmtQo3/";
        base64EncodedPublicKey += "eXOOnlcyaU72f8g9QZfINCYDu3bOz2A40ziqeus5MVaSGFJ90TDXLM1aVmwx47CqyA+S+9U6QYS4mhkHQIDAQAB";

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d("Crappy Bird", "Problem setting up In-app Billing: " + result);
                } else {
                    Log.i("In app Billing", "Successfully set up");

                    List<String> additionalSkuList = new ArrayList<String>();
                    additionalSkuList.add(SKU_1000_PIZZA);
                    additionalSkuList.add(SKU_5000_PIZZA);
                    additionalSkuList.add(SKU_10000_PIZZA);
                    mHelper.queryInventoryAsync(true, additionalSkuList,
                            mQueryFinishedListener);

                }
                // Hooray, IAB is fully set up!
            }
        });



    }

    /**
     * This is a callback for after querying IAPs from Google Play Dev Console
     */
    IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            if (result.isFailure()) {
                // handle error
                Log.e("QueryInventory Error", result.toString());
                return;
            }
            //get prices of available purchases
            price1000Pizza = inventory.getSkuDetails(SKU_1000_PIZZA).getPrice();
            price5000Pizza = inventory.getSkuDetails(SKU_5000_PIZZA).getPrice();
            price10000Pizza = inventory.getSkuDetails(SKU_10000_PIZZA).getPrice();

            //consume any unconsumed purchases
            if(ParseUser.getCurrentUser() != null) {
                for (int i = 0; i < GameActivity.SKU_PIZZA_PURCHASE.length; i++) {
                    Purchase purchase = inventory.getPurchase(SKU_PIZZA_PURCHASE[i]);
                    if (purchase != null) {
                        Log.d("CrappyBird", "User has pizza. Consuming it.");
                        mHelper.consumeAsync(inventory.getPurchase(SKU_PIZZA_PURCHASE[i]), mConsumeFinishedListener);
                        return;
                    }
                }
            }
            // update the UI
        }
    };

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
            alert("Could not launch Play Store.");
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

            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, intent)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                if (requestCode == 100) {
                    if(resultCode == 200){ //result code is only 200 if a user just logged in or signed up
                        syncUser();
                        mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);

                        displayLongToast("Signed in as " + ParseUser.getCurrentUser().getUsername());

                    }
                }
                super.onActivityResult(requestCode, resultCode, intent);
            } else {
                Log.d("Crappy Bird", "onActivityResult handled by IABUtil.");
            }

    }

    /**
     * this method syncs the local max score with the saved score on the user's account
     * Also adds all collected pizza to the account
     * Used when they log in or sign up
     */
    public void syncUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            int savedScore = currentUser.getInt("highScore");
            if(savedScore > getMaxScore()) {
                setMaxScore(savedScore);
            } else {
                currentUser.put("highScore", getMaxScore());
            }

            int pizzaOnAccount = currentUser.getInt("pizzaCollected");
            pizzaOnAccount += getPizza();
            setPizza(pizzaOnAccount);
            currentUser.put("pizzaCollected", pizzaOnAccount);
            currentUser.saveInBackground();

        }
    }

    public void logout() {
        //log the user out and set max score = 0. They can get it back by logging in again
        setMaxScore(0);
        setPizza(0);
        String username = ParseUser.getCurrentUser().getUsername();
        ParseUser.logOut();

        mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
        displayLongToast(username + " signed out.");

    }

    public void displayConnectionError() {
        alert("Please check your connection and try again.");
        /*final CharSequence text = "Please check your connection and try again";

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });*/
    }

    public void displayLongToast(String string) {
        final CharSequence text = string;

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void displayShortToast(String string) {
        final CharSequence text = string;

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * for launching the IAB form.
     * @param pizzaPurchaseIndex the index corresponding to the pizza purchase the user is getting (e.g. 0 = 1000 pizza)
     */
    public void purchasePizza(int pizzaPurchaseIndex) {
        if(isNetworkAvailable()) {
            if(!mHelper.getAsyncInProgress()) {
                mHelper.launchPurchaseFlow(this, SKU_PIZZA_PURCHASE[pizzaPurchaseIndex], 10001,
                        mPurchaseFinishedListener, ParseUser.getCurrentUser() + "-" + SKU_PIZZA_PURCHASE[pizzaPurchaseIndex] + "-" + getPurchaseNumber());
                incrementPurchaseNumber();

            } else {
                alert("Transaction already in progress. Please wait and try again later.");
            }
        } else {
            displayConnectionError();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            //if we were disposed of in the meantime, quit
            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.d("CrappyBird", "Error purchasing: " + result);
                return;
            }
            else {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };


    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if (result.isSuccess()) {
                        if (purchase.getSku().equals(SKU_1000_PIZZA)) {

                            addPizza(1000);
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.put("pizzaCollected", getPizza());
                            currentUser.saveInBackground();
                            alert("You have purchased 1000 pizza for " + getPrice1000Pizza() + ". You now have " + getPizza() + " pizza. Happy crapping!");

                        }
                        else if (purchase.getSku().equals(SKU_5000_PIZZA)) {

                            addPizza(5000);
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.put("pizzaCollected", getPizza());
                            currentUser.saveInBackground();
                            alert("You have purchased 5000 pizza for " + getPrice5000Pizza() + ". You now have " + getPizza() + " pizza. Happy crapping!");

                        }
                        else if (purchase.getSku().equals(SKU_10000_PIZZA)) {

                            addPizza(10000);
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.put("pizzaCollected", getPizza());
                            currentUser.saveInBackground();
                            alert("You have purchased 10000 pizza for " + getPrice10000Pizza() + ". You now have " + getPizza() + " pizza. Happy crapping!");

                        }

                        mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                    }
                    else {
                        alert("Something went wrong. Please try again or contact NJWIDMANN APPS for support.");
                    }
                }
            };

    public void alert(String string) {
        final String message = string;
        final Context context = this;
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder bld = new AlertDialog.Builder(context);
                bld.setMessage(message);
                bld.setNeutralButton("OK", null);
                Log.d("Crappy Bird", "Showing alert dialog: " + message);
                bld.create().show();
            }
        });


    }


    public int getMaxScore() {
        return getPreferences(Context.MODE_PRIVATE).getInt("maxScore", 0);
    }

    public void setMaxScore(int maxScore) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt("maxScore", maxScore).commit();
    }

    public void setPurchaseNumber(int purchaseNumber) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt("purchaseNumber", purchaseNumber).commit();

    }

    public int getPurchaseNumber() {
        return getPreferences(Context.MODE_PRIVATE).getInt("purchaseNumber", 0);

    }

    public void incrementPurchaseNumber() {
        int current = getPurchaseNumber();
        current++;
        setPurchaseNumber(current);
    }



    public int getSelectedBird() {
        return getPreferences(Context.MODE_PRIVATE).getInt("selectedBird", 0);
    }

    public void setSelectedBird(int birdNum) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt("selectedBird", birdNum).commit();
    }

    public int getPizza() {
        return getPreferences(Context.MODE_PRIVATE).getInt("pizza", 0);
    }

    public void setPizza(int pizza) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt("pizza", pizza).commit();
    }

    public void addPizza(int pizza) {
        int currentPizza = getPizza();
        currentPizza += pizza;
        setPizza(currentPizza);
    }

    public void subtractPizza(int pizza) {
        int currentPizza = getPizza();
        if(currentPizza > pizza) {
            currentPizza -= pizza;
            setPizza(currentPizza);
        } else {
            setPizza(0);
        }
    }

    public String getPrice10000Pizza() {
        return price10000Pizza;
    }

    public String getPrice5000Pizza() {
        return price5000Pizza;
    }

    public String getPrice1000Pizza() {
        return price1000Pizza;
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

        if (mHelper != null) mHelper.dispose();
        mHelper = null;
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
        /*
        if(mHelper != null && !mHelper.getAsyncInProgress()) {
            List<String> additionalSkuList = new ArrayList<String>();
            additionalSkuList.add(SKU_1000_PIZZA);
            additionalSkuList.add(SKU_5000_PIZZA);
            additionalSkuList.add(SKU_10000_PIZZA);
            mHelper.queryInventoryAsync(true, additionalSkuList,
                    mQueryFinishedListener);
        }
        */

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
