package com.application.nick.crappybird.scene;

import android.util.Log;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.BasicBird;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.Tutorial;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 4/5/2015.
 */
public class LeaderboardScene extends BaseScene {

    private final int NUM_HIGHSCORES_IN_LEADERBOARD = 10;

    private Sprite mLeaderboard;

    private CameraScene mLoginScene;

    private Text loadingText;


    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        mLeaderboard = new Sprite(0, 0, mResourceManager.mLeaderboardTextureRegion, mVertexBufferObjectManager);
        attachChild(mLeaderboard);

        if (!mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.play();
            mResourceManager.mMusic.setVolume(0.6f);
            mResourceManager.mMusic.setLooping(true);
        }

        mLoginScene = new CameraScene(mCamera);
        mLoginScene.setBackgroundEnabled(false);

        final float boardX = (SCREEN_WIDTH - mResourceManager.mTutorialBoardTextureRegion.getWidth()) / 2;
        final float boardY = boardX; //(SCREEN_HEIGHT - mResourceManager.mTutorialBoardTextureRegion.getHeight()) / 2;

        final float loginTextX = (SCREEN_WIDTH - mResourceManager.mLoginTextTextureRegion.getWidth()) / 2;
        final float loginTextY = boardY + 10;

        final float signUpButtonX = (SCREEN_WIDTH - mResourceManager.mSignUpButtonTextureRegion.getWidth()) / 2;
        final float signUpButtonY = loginTextY + mResourceManager.mLoginTextTextureRegion.getHeight();

        final float loginButtonX = signUpButtonX;
        final float loginButtonY = signUpButtonY + mResourceManager.mLoginButtonTextureRegion.getHeight();

        final float laterButtonX = boardX + mResourceManager.mTutorialBoardTextureRegion.getWidth() - mResourceManager.mNextButtonTextureRegion.getWidth();
        final float laterButtonY = boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight() - mResourceManager.mNextButtonTextureRegion.getHeight();

        final float backButtonX = 0;
        final float backButtonY = -5;

        final float logoutButtonX = SCREEN_WIDTH - mResourceManager.mLogoutButtonTextureRegion.getWidth();
        final float logoutButtonY = -5;

        final Sprite popUpBoard = new Sprite(boardX, boardY, mResourceManager.mTutorialBoardTextureRegion, mVertexBufferObjectManager);
        mLoginScene.attachChild(popUpBoard);

        final Sprite loginText = new Sprite(loginTextX, loginTextY, mResourceManager.mLoginTextTextureRegion, mVertexBufferObjectManager);
        mLoginScene.attachChild(loginText);

        final TiledSprite signUpButton = new TiledSprite(signUpButtonX, signUpButtonY, mResourceManager.mSignUpButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    mActivity.openSignUpActivity();
                }
                return true;
            }
        };
        signUpButton.setCurrentTileIndex(0);
        mLoginScene.registerTouchArea(signUpButton);
        mLoginScene.attachChild(signUpButton);

        final TiledSprite loginButton = new TiledSprite(loginButtonX, loginButtonY, mResourceManager.mLoginButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    mActivity.openLoginActivity();
                }
                return true;
            }
        };
        loginButton.setCurrentTileIndex(0);
        mLoginScene.registerTouchArea(loginButton);
        mLoginScene.attachChild(loginButton);

        final TiledSprite laterButton = new TiledSprite(laterButtonX, laterButtonY, mResourceManager.mLaterButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    clearChildScene(); //close popup
                }
                return true;
            }
        };
        laterButton.setCurrentTileIndex(0);
        laterButton.setScale(0.75f);
        mLoginScene.registerTouchArea(laterButton);
        mLoginScene.attachChild(laterButton);

        //if user is not logged in already, open login popup
        if(ParseUser.getCurrentUser() == null) {
            setChildScene(mLoginScene, false, true, true);
            mLoginScene.setBackgroundEnabled(false);
        } else {
            Log.i("username", ParseUser.getCurrentUser().getUsername());
            final TiledSprite logoutButton = new TiledSprite(logoutButtonX, logoutButtonY, mResourceManager.mLogoutButtonTextureRegion, mVertexBufferObjectManager) {

                @Override
                public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    if (pSceneTouchEvent.isActionDown()) {
                        setCurrentTileIndex(1);

                    }
                    if (pSceneTouchEvent.isActionUp()) {
                        setCurrentTileIndex(0);
                        //log the user out and set max score = 0. They can get it back by logging in again
                        mActivity.setMaxScore(0);
                        ParseUser.logOut();

                        mSceneManager.setScene(SceneManager.SceneType.SCENE_LEADERBOARD);
                    }
                    return true;
                }
            };
            logoutButton.setCurrentTileIndex(0);
            logoutButton.setScale(0.75f);
            registerTouchArea(logoutButton);
            attachChild(logoutButton);
        }

        final TiledSprite backButton = new TiledSprite(backButtonX, backButtonY, mResourceManager.mBackButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                }
                return true;
            }
        };
        backButton.setCurrentTileIndex(0);
        backButton.setScale(0.75f);
        registerTouchArea(backButton);
        attachChild(backButton);

        final float loadingTextY = 90;
        loadingText = (new Text(0, loadingTextY, mResourceManager.mFont3, "Loading...", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        loadingText.setX((SCREEN_WIDTH - loadingText.getWidth()) / 2);
        attachChild(loadingText);

        if(mActivity.isNetworkAvailable()) {
            populateLeaderboard();
        } else {
            mActivity.displayConnectionError();
        }
    }


    private void populateLeaderboard() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    List<ParseUser> highscoreUsers = new ArrayList<ParseUser>();

                    for(int i = 0; i < NUM_HIGHSCORES_IN_LEADERBOARD; i++) {
                        if(objects.size() > 0) {
                            int maxScore = -1;
                            int userIndex = -1;
                            for (int j = 0; j < objects.size(); j++) {
                                ParseUser user = objects.get(j);
                                int score = user.getInt("highScore");
                                if (score > maxScore) {
                                    maxScore = score;
                                    userIndex = j;
                                }
                            }
                            highscoreUsers.add(objects.get(userIndex));
                            objects.remove(userIndex);
                        } else { //if there are no more users
                            highscoreUsers.add(null);
                        }
                    }
                    List<Text> highscoreTextListings= new ArrayList<Text>();
                    final float textX = 40;
                    final float startingTextY = 90;
                    final float columnHeight = 35;

                    for(int i = 0; i < NUM_HIGHSCORES_IN_LEADERBOARD; i++) {
                        ParseUser user = highscoreUsers.get(i);
                        String text = (i + 1) + ". none";
                        if(user != null) {
                            text = (i + 1) + ". " + user.getUsername() + " - " + user.getInt("highScore");
                        }
                        float textY = startingTextY + columnHeight * i;

                        highscoreTextListings.add(new Text(textX, textY, mResourceManager.mFont3, text, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
                        //highscoreTextListings.get(i).setText(text);
                        attachChild(highscoreTextListings.get(i));
                    }
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if(currentUser != null) {
                        String text = "You: " + currentUser.getUsername() + " - " + currentUser.getInt("highScore");
                        float textY = startingTextY + columnHeight * NUM_HIGHSCORES_IN_LEADERBOARD;

                        highscoreTextListings.add(new Text(textX, textY, mResourceManager.mFont3, text, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
                        //highscoreTextListings.get(NUM_HIGHSCORES_IN_LEADERBOARD).setText(text);
                        attachChild(highscoreTextListings.get(NUM_HIGHSCORES_IN_LEADERBOARD));
                    } else {
                        String text = "You: Anonymous - " + mActivity.getMaxScore();
                        float textY = startingTextY + columnHeight * NUM_HIGHSCORES_IN_LEADERBOARD;

                        highscoreTextListings.add(new Text(textX, textY, mResourceManager.mFont3, text, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
                        //highscoreTextListings.get(NUM_HIGHSCORES_IN_LEADERBOARD).setText(text);
                        attachChild(highscoreTextListings.get(NUM_HIGHSCORES_IN_LEADERBOARD));
                    }

                    loadingText.setVisible(false);


                } else {
                    // Something went wrong.
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackKeyPressed() {
        mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_MENU;
    }

    @Override
    public void disposeScene() {
        //TODO
    }

}
