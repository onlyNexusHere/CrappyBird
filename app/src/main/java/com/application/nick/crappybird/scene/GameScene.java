package com.application.nick.crappybird.scene;

import android.content.Context;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.Collectable;
import com.application.nick.crappybird.entity.CollectablePool;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.MotherShip;
import com.application.nick.crappybird.entity.Obstacle;
import com.application.nick.crappybird.entity.ObstacleBalloon;
import com.application.nick.crappybird.entity.ObstaclePlane;
import com.application.nick.crappybird.entity.ObstaclePool;
import com.application.nick.crappybird.entity.Target;
import com.application.nick.crappybird.entity.TargetPerson1;
import com.application.nick.crappybird.entity.TargetPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.parse.ParseUser;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 4/5/2015.
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener {

    private final int MAX_OBSTACLES = 5;
    private final float MAX_MACHINE_CRAPPING_TIME = 10;
    private final float MACHINE_CRAPPING_TIME_BETWEEN_CRAPS = 0.1f;
    private final float MAX_DOUBLE_POINTS_TIME = 10;
    private final float MAX_TARGETS_ON_SCREEN = 10;
    private final float DOUBLE_POINTS_TIME_TO_SHOW_PLUS_TWO = 0.4f;
    private final float MAX_COLLECTABLES_ON_SCREEN = 3;
    private final int NUM_OBSTACLES = 20; //number of obstacles to allocate to pool
    private final int NUM_TARGETS = 10; //number of targets (people) to allocate
    private final int NUM_CRAPS = 15; //number of craps to allocate
    private final int NUM_COLLECTABLES = 20; //number of generic collectables (pizza) to allocate to pool
    private final int MOTHERSHIP_OBSTACLE_INDEX = 35; //the obstacle index to have the mothership fly across the screen


    private AutoParallaxBackground mAutoParallaxBackground;

    private Text mHudText;
    private int score;
    private int most;

    private AnimatedSprite mBird, mMachineCrapMeter;

    private TiledSprite mCrapMeter, mAlertSign;

    private MotherShip mMotherShip;

    private List<Obstacle> mObstacles;
    private ObstaclePool mObstaclePool;

    private List<Collectable> mCollectables;
    private CollectablePool mCollectablePool;

    private List<Target> mTargets;
    private TargetPool mTargetPool;

    private CrapPool mCrapPool;
    private List<Crap> mCraps = new ArrayList<Crap>();

    private boolean mGameOver, mOutOfCrap, mMachineCrapActivated = false, mMachineCrapping = false, mMachineCrapMeterBlinking = false,
            mDoublePointsActivated = false, mMotherShipIncoming = false, mMotherShipOnScreen = false, mMotherShipPassed = false, mSharingVisible = false;

    private float machineCrappingTime = 0, machineCrappingLastCrapTime, doublePointsTime = 0, doublePointsLastPointTime;

    private PhysicsWorld mPhysicsWorld;

    private CameraScene mGameReadyScene, mGameOverScene, mPauseScene;
    private Text scoreText;
    private Text mostText;
    private TiledSprite mPlusTwo, playButton, backButton, rateButton, shareButton, facebookButton, twitterButton, otherButton, leaderboardButton, pauseButton;

    private Rectangle mGround;

    private float MAX_CRAP_METER_SIZE;


    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        setOnSceneTouchListener(this);

        if (mResourceManager.mMusic != null && !mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.play();
        }

        mAutoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 10);
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-5.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerBack.getHeight(), mResourceManager.mParallaxLayerBack, mVertexBufferObjectManager)));
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), mResourceManager.mParallaxLayerFront, mVertexBufferObjectManager)));
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight() - mResourceManager.mParallaxLayerMiddle.getHeight(), mResourceManager.mParallaxLayerMiddle, mVertexBufferObjectManager)));
        setBackground(mAutoParallaxBackground);

        //create entities
        final float birdX = (SCREEN_WIDTH - mResourceManager.mBirdTextureRegion.getWidth()) / 2 - 50;
        final float birdY = (SCREEN_HEIGHT - mResourceManager.mBirdTextureRegion.getHeight()) / 2 - 30;

        mBird = new AnimatedSprite(birdX, birdY, mResourceManager.mBirdTextureRegion, mVertexBufferObjectManager);
        mBird.setZIndex(10);
        mBird.animate(200);

        attachChild(mBird);


        //create entities
        mGround = new Rectangle(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight() + 10, SCREEN_WIDTH, mResourceManager.mParallaxLayerFront.getHeight(), mVertexBufferObjectManager);
        mGround.setColor(Color.TRANSPARENT);
        final Rectangle roof = new Rectangle(0, 0, SCREEN_WIDTH, 1, mVertexBufferObjectManager);
        roof.setColor(Color.TRANSPARENT);

        mObstaclePool = new ObstaclePool(mResourceManager, mVertexBufferObjectManager, mGround.getY());
        mObstaclePool.batchAllocatePoolItems(NUM_OBSTACLES);

        mObstacles = new ArrayList<Obstacle>();
        mObstacles.add(mObstaclePool.obtainPoolItem());

        mCollectablePool = new CollectablePool(mResourceManager, mVertexBufferObjectManager, mGround.getY());
        mCollectablePool.batchAllocatePoolItems(NUM_COLLECTABLES);
        mCollectablePool.shufflePoolItems();

        mMotherShip = new MotherShip(mResourceManager.mObstacleMotherShipTextureRegion, mVertexBufferObjectManager);
        float alertX = (SCREEN_WIDTH - mResourceManager.mAlertTextureRegion.getWidth()) / 2;
        float alertY = (mMotherShip.getHeight() / 2) + 20;

        mAlertSign = new TiledSprite(alertX, alertY, mResourceManager.mAlertTextureRegion, mVertexBufferObjectManager);
        mAlertSign.setScale(2);
        mAlertSign.setVisible(false);

        mCollectables = new ArrayList<Collectable>();
        mCollectables.add(mCollectablePool.obtainPoolItem());

        mTargetPool = new TargetPool(mResourceManager, mVertexBufferObjectManager, mGround.getY());
        mTargetPool.batchAllocatePoolItems(NUM_TARGETS);

        mTargets = new ArrayList<Target>();
        mTargets.add(mTargetPool.obtainPoolItem());

        mCrapPool = new CrapPool(mResourceManager.mCrapTextureRegion, mVertexBufferObjectManager);
        mCrapPool.batchAllocatePoolItems(NUM_CRAPS);
        mOutOfCrap = false;

        attachChild(mGround);
        attachChild(roof);
        attachChild(mObstacles.get(mObstacles.size() - 1));
        attachChild(mCollectables.get(mCollectables.size() - 1));
        attachChild(mTargets.get(mTargets.size() - 1));
        attachChild(mAlertSign);

        mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH * 1.5f), false);
        mPhysicsWorld.setContactListener(createContactListener());

        //create body and fixture
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
        final Body groundBody = PhysicsFactory.createBoxBody(mPhysicsWorld, mGround, BodyDef.BodyType.StaticBody, wallFixtureDef);
        groundBody.setUserData("ground");

        final FixtureDef birdFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0);
        final Body birdBody = PhysicsFactory.createCircleBody(mPhysicsWorld, mBird, BodyDef.BodyType.DynamicBody, birdFixtureDef);
        birdBody.setUserData("bird");
        mBird.setUserData(birdBody);

        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mBird, birdBody, true, false));


        /* The actual collision-checking. */
        registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void reset() {}

            @Override
            public void onUpdate(float pSecondsElapsed) {


                checkForObstacleBirdContact();

                checkObstaclePosition(); //handle obstacles leaving screen and update score if bird passes them

                checkForCollectableBirdContact();

                checkForCollectableLeavingScreen();

                checkForTargetCrapContact();

                checkTargetPosition(); //check targets leaving screen and also handle falling people from balloons

                addNewTarget(SCREEN_WIDTH / 2); //add a new target if the earlist added on the screen passes x

                if(mCollectables.size() <= MAX_COLLECTABLES_ON_SCREEN) {
                    addNewCollectable(SCREEN_WIDTH * 3 / 4); //add a new collectable if the earliest added on the screen passes x
                }


                //Handle mother ship////////////////////////////////
                if(mObstaclePool.getObstacleIndex() >= MOTHERSHIP_OBSTACLE_INDEX && !mMotherShipIncoming && !mMotherShipOnScreen && !mMotherShipPassed) {
                    setMotherShipIncoming(true);
                }
                if(mMotherShipIncoming && mObstacles.size() == 0) {
                    setMotherShipOnScreen(true);
                }
                if(mMotherShipOnScreen) {
                    checkMotherShipPosition();
                    checkForMotherShipContact();
                }

                //Handle machine crap powerup/////////////////////////////////
                if(mMachineCrapActivated) {
                    if(mMachineCrapping) {
                        if(machineCrappingTime > machineCrappingLastCrapTime + MACHINE_CRAPPING_TIME_BETWEEN_CRAPS) {
                            jumpBird(mBird);
                            machineCrappingLastCrapTime = machineCrappingTime;
                        }
                    }

                    machineCrappingTime += pSecondsElapsed;
                    if(machineCrappingTime > MAX_MACHINE_CRAPPING_TIME) {
                        setMachineCrap(false);
                        machineCrappingTime = 0;
                        mMachineCrapMeterBlinking = false;
                    } else if(machineCrappingTime > MAX_MACHINE_CRAPPING_TIME - 2 && !mMachineCrapMeterBlinking) {
                        mMachineCrapMeter.animate(new long[]{400, 400}, 5, 6, true);
                        mMachineCrapMeterBlinking = true;
                    }

                }

                //Handle double points powerup//////////////////////////////////
                if(mDoublePointsActivated) {
                    if(mPlusTwo.isVisible()) { //flash "+2" next to score for every point scored while double points is activated
                        if(doublePointsTime > doublePointsLastPointTime + DOUBLE_POINTS_TIME_TO_SHOW_PLUS_TWO) {
                            mPlusTwo.setVisible(false);
                        }
                    }

                    doublePointsTime += pSecondsElapsed;
                    if(doublePointsTime > MAX_DOUBLE_POINTS_TIME) {
                        setDoublePoints(false);
                        mPlusTwo.setVisible(false);
                    }

                }

                //Add new obstacles///////////////////////////////////
                int obstaclesOnScreen = mObstacles.size();
                if(obstaclesOnScreen <= MAX_OBSTACLES && !mMotherShipIncoming && !mMotherShipOnScreen) { //max obstacles on the screen can't be more than x. Don't add new obstacles if mother ship is coming or on screen
                    if (mObstaclePool.getObstacleIndex() < 40) {
                        addNewObstacle(mObstaclePool.getObstacleIndex() * 5); //add a new obstacle if the earliest added obstacle on the screen passes x
                    } else {
                        addNewObstacle(200);
                    }
                }


                    //don't let bird leave top of screen
                if (mBird.getY() < 0) {
                    final Body faceBody = (Body)mBird.getUserData();
                    Vector2 newVelocity = Vector2Pool.obtain(0,0);

                    faceBody.setLinearVelocity(newVelocity);
                    Vector2Pool.recycle(newVelocity);
                }

                if(mCraps.size() > 0) {
                    checkForCrapGroundContact(mGround);
                    checkForCrapLeavingScreen();

                }
                    //rotate bird with changing velocity
                if(mBird.getY() + mBird.getHeight() < mGround.getY()) {
                    Body birdBody = (Body) mBird.getUserData();
                    mBird.setRotation(birdBody.getLinearVelocity().y * 2 - 10);
                }


                mPhysicsWorld.onUpdate(pSecondsElapsed);

            }
        });


        final float labelX = (SCREEN_WIDTH - mResourceManager.mHelpTextureRegion.getWidth()) / 2;
        final float labelY = 100;

        //create CameraScene for get ready
        final float readyX = (SCREEN_WIDTH - mResourceManager.mStateTextureRegion.getWidth()) / 2;
        final float readyY = labelY - mResourceManager.mStateTextureRegion.getHeight();

        mGameReadyScene = new CameraScene(mCamera);
        //"Get Ready" picture
        final TiledSprite label2Sprite = new TiledSprite(readyX, readyY, mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
        label2Sprite.setCurrentTileIndex(0);
        mGameReadyScene.attachChild(label2Sprite);
        //how to picture
        final Sprite resumedSprite = new Sprite(labelX, labelY, mResourceManager.mHelpTextureRegion, mVertexBufferObjectManager);
        mGameReadyScene.attachChild(resumedSprite);

        mGameReadyScene.setBackgroundEnabled(false);

        mGameReadyScene.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.isActionUp()) {
                    clearChildScene();
                    mHudText.setVisible(true);
                    mCrapMeter.setVisible(true);
                    pauseButton.setVisible(true);
                }
                return true;
            }
        });

        setChildScene(mGameReadyScene, false, true, true);



        //create CameraScene for game over
        final float overX = (SCREEN_WIDTH - mResourceManager.mBoardTextureRegion.getWidth()) / 2;
        final float overY = labelY + mResourceManager.mStateTextureRegion.getHeight();

        final float playX = SCREEN_WIDTH / 2 - mResourceManager.mPlayButtonTextureRegion.getWidth();
        final float playY = overY + mResourceManager.mBoardTextureRegion.getHeight();

        final float leaderboardX = SCREEN_WIDTH / 2;
        final float leaderboardY = overY + mResourceManager.mBoardTextureRegion.getHeight();

        final float shareX = leaderboardX;
        final float shareY = leaderboardY + mResourceManager.mBackButtonTextureRegion.getHeight();

        final float rateX = playX;
        final float rateY = shareY;

        final float twitterX = playX;
        final float twitterY = leaderboardY;

        final float scoreX = overX + 55;
        final float scoreY = overY + 40;

        final float mostX = overX + 165;
        final float mostY = scoreY;

        mGameOverScene = new CameraScene(mCamera);

        final TiledSprite gameOverTitle = new TiledSprite(readyX, readyY + mResourceManager.mStateTextureRegion.getHeight() / 2, mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
        gameOverTitle.setCurrentTileIndex(1);
        gameOverTitle.setScale(1.3f);
        mGameOverScene.attachChild(gameOverTitle);

        final Sprite boardSprite = new Sprite(overX, overY, mResourceManager.mBoardTextureRegion, mVertexBufferObjectManager);
        mGameOverScene.attachChild(boardSprite);

        scoreText = new Text(scoreX, scoreY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        scoreText.setText("0");
        mGameOverScene.attachChild(scoreText);

        mostText = new Text(mostX, mostY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mostText.setText(String.valueOf(most));
        mGameOverScene.attachChild(mostText);

        playButton = new TiledSprite(playX, playY, mResourceManager.mPlayButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                    } else { //if the twitter button is visible on top of the play button
                        twitterButton.setCurrentTileIndex(1);
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(!mSharingVisible) {
                        clearChildScene(); //start new game
                        mSceneManager.setScene(SceneManager.SceneType.SCENE_GAME);
                    } else { //if the twitter button is visible on top of the play button
                        twitterButton.setCurrentTileIndex(0);
                        mActivity.openTwitterShare(score);
                    }
                }
                return true;
            }
        };
        playButton.setCurrentTileIndex(0);
        playButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(playButton);
        mGameOverScene.attachChild(playButton);


        twitterButton = new TiledSprite(twitterX, twitterY, mResourceManager.mTweetButtonTextureRegion, mVertexBufferObjectManager);
        twitterButton.setCurrentTileIndex(0);
        twitterButton.setScale(0.75f);
        twitterButton.setVisible(false);
        mGameOverScene.attachChild(twitterButton);

        rateButton = new TiledSprite(rateX, rateY, mResourceManager.mRateButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                    } else {
                        facebookButton.setCurrentTileIndex(1);
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(0);
                        mActivity.openRate();
                    } else {
                        facebookButton.setCurrentTileIndex(0);
                        mActivity.openFacebookShare(score);
                    }
                }
                return true;
            }
        };
        rateButton.setCurrentTileIndex(0);
        rateButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(rateButton);
        mGameOverScene.attachChild(rateButton);

        facebookButton = new TiledSprite(rateX, rateY, mResourceManager.mFacebookButtonTextureRegion, mVertexBufferObjectManager);
        facebookButton.setCurrentTileIndex(0);
        facebookButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(facebookButton);
        mGameOverScene.attachChild(facebookButton);
        facebookButton.setVisible(false);


        leaderboardButton = new TiledSprite(leaderboardX, leaderboardY, mResourceManager.mLeaderboardButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                    } else { //if the other button is visible on top of the rate button
                        otherButton.setCurrentTileIndex(1);
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(0);
                        if(mActivity.isNetworkAvailable()) {
                            mSceneManager.setScene(SceneManager.SceneType.SCENE_LEADERBOARD);
                        } else {
                            mActivity.displayConnectionError();
                        }
                    } else { //if the other button is visible on top of the rate button
                        otherButton.setCurrentTileIndex(0);
                        mActivity.openOtherShare(score);
                    }

                }
                return true;
            }
        };
        leaderboardButton.setCurrentTileIndex(0);
        leaderboardButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(leaderboardButton);
        mGameOverScene.attachChild(leaderboardButton);

        otherButton = new TiledSprite(leaderboardX, leaderboardY, mResourceManager.mOtherButtonTextureRegion, mVertexBufferObjectManager);
        otherButton.setCurrentTileIndex(0);
        otherButton.setScale(0.75f);
        otherButton.setVisible(false);
        mGameOverScene.attachChild(otherButton);


        shareButton = new TiledSprite(shareX, shareY, mResourceManager.mShareButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                    } else { //if the back button is visible on top of the share button
                        backButton.setCurrentTileIndex(1);
                    }

                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(0);
                        setSharingVisible(true);
                    } else { //if the back button is visible on top of the share button
                        backButton.setCurrentTileIndex(0);
                        setSharingVisible(false);

                    }
                }
                return true;
            }
        };
        shareButton.setCurrentTileIndex(0);
        shareButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(shareButton);
        mGameOverScene.attachChild(shareButton);

        backButton = new TiledSprite(shareX, shareY, mResourceManager.mBackButtonTextureRegion, mVertexBufferObjectManager);
        backButton.setCurrentTileIndex(0);
        backButton.setScale(0.75f);
        backButton.setVisible(false);
        mGameOverScene.attachChild(backButton);


        mGameOverScene.setBackgroundEnabled(false);

        most = mActivity.getMaxScore();

        //create HUD for score
        HUD gameHUD = new HUD();
        // CREATE SCORE TEXT
        mHudText = new Text(SCREEN_WIDTH/2, 50, mResourceManager.mFont5, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mHudText.setText("0");
        mHudText.setX((SCREEN_WIDTH - mHudText.getWidth()) / 2);
        mHudText.setVisible(false);
        gameHUD.attachChild(mHudText);

        //"+2" popup for double points
        mPlusTwo = new TiledSprite((SCREEN_WIDTH/2 + mHudText.getWidth() * 2), 50, mResourceManager.mPlusTwoTextureRegion, mVertexBufferObjectManager);
        mPlusTwo.setVisible(false);
        gameHUD.attachChild(mPlusTwo);

        mCrapMeter = new TiledSprite((SCREEN_WIDTH - mResourceManager.mMeter2TextureRegion.getWidth()) / 2, mResourceManager.mMeter2TextureRegion.getHeight(), mResourceManager.mMeter2TextureRegion, mVertexBufferObjectManager);
        mCrapMeter.setCurrentTileIndex(6);
        mCrapMeter.setScale(0.75f);
        mCrapMeter.setX((SCREEN_WIDTH - mCrapMeter.getWidth()) / 2);
        MAX_CRAP_METER_SIZE = mCrapMeter.getWidth();
        mCrapMeter.setVisible(false);
        gameHUD.attachChild(mCrapMeter);

        //for machine crap power up (thunder taco)
        mMachineCrapMeter = new AnimatedSprite((SCREEN_WIDTH - mResourceManager.mMeter2TextureRegion.getWidth()) / 2, mResourceManager.mMeter2TextureRegion.getHeight(), mResourceManager.mMeter2TextureRegion, mVertexBufferObjectManager);
        mMachineCrapMeter.setScale(0.75f);
        mMachineCrapMeter.setX((SCREEN_WIDTH - mMachineCrapMeter.getWidth()) / 2);
        mMachineCrapMeter.setVisible(false);
        gameHUD.attachChild(mMachineCrapMeter);

        mCamera.setHUD(gameHUD);


        mPauseScene = new CameraScene(mCamera);

        final float playOnPauseX = (SCREEN_WIDTH - mResourceManager.mPlayButtonTextureRegion.getWidth()) / 2;
        final float playOnPauseY = SCREEN_HEIGHT / 2 - mResourceManager.mPlayButtonTextureRegion.getHeight();

        final float restartOnPauseX = playOnPauseX;
        final float restartOnPauseY = SCREEN_HEIGHT / 2;

        final TiledSprite playButtonOnPause = new TiledSprite(playOnPauseX, playOnPauseY, mResourceManager.mPlayButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);
                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    setPause(false);
                }
                return true;
            }
        };
        playButtonOnPause.setCurrentTileIndex(0);
        playButtonOnPause.setScale(0.75f);
        mPauseScene.registerTouchArea(playButtonOnPause);
        mPauseScene.attachChild(playButtonOnPause);

        final TiledSprite restartButtonOnPause = new TiledSprite(restartOnPauseX, restartOnPauseY, mResourceManager.mRestartButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);
                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    mSceneManager.setScene(SceneManager.SceneType.SCENE_GAME);

                }
                return true;
            }
        };
        restartButtonOnPause.setCurrentTileIndex(0);
        restartButtonOnPause.setScale(0.75f);
        mPauseScene.registerTouchArea(restartButtonOnPause);
        mPauseScene.attachChild(restartButtonOnPause);

        mPauseScene.setBackgroundEnabled(false);


        pauseButton = new TiledSprite(0, 0, mResourceManager.mPauseButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(isVisible()) {
                        setCurrentTileIndex(1);
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(isVisible()) {
                        setCurrentTileIndex(0);
                        setPause(true);
                    }
                }
                return true;
            }
        };
        pauseButton.setCurrentTileIndex(0);
        pauseButton.setScale(0.25f);
        pauseButton.setX(mCrapMeter.getX() + (mCrapMeter.getWidth() - mCrapMeter.getWidthScaled()) / 2 + mCrapMeter.getWidthScaled() - (pauseButton.getWidth() - pauseButton.getWidthScaled()) / 2);
        pauseButton.setY(mCrapMeter.getY() + (mCrapMeter.getHeight() - mCrapMeter.getHeightScaled()) / 2  - (pauseButton.getHeight() - pauseButton.getHeightScaled()) / 2 - (pauseButton.getHeightScaled() - mCrapMeter.getHeightScaled()) / 2);
        registerTouchArea(pauseButton);
        attachChild(pauseButton);
        pauseButton.setVisible(false);

    }


    private void setSharingVisible(boolean bool) {
        if (bool) {
            mSharingVisible = true;

            playButton.setVisible(false);
            shareButton.setVisible(false);
            rateButton.setVisible(false);

            facebookButton.setVisible(true);
            twitterButton.setVisible(true);
            otherButton.setVisible(true);
            backButton.setVisible(true);
        } else {
            mSharingVisible = false;

            facebookButton.setVisible(false);
            twitterButton.setVisible(false);
            otherButton.setVisible(false);
            backButton.setVisible(false);

            playButton.setVisible(true);
            shareButton.setVisible(true);
            rateButton.setVisible(true);

        }
    }
    /**
     * Checks if obstacles have left screen and recycles if they have. Also updates score if bird has passed them.
     */
    private void checkObstaclePosition() {
        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if (obstacle.getX() <  -obstacle.getWidth()) {
                detachChild(obstacle);
                mObstaclePool.recyclePoolItem(obstacle);
                mObstaclePool.shufflePoolItems();
                mObstacles.remove(i);
            } else if (!mGameOver && !obstacle.getScoreAdded() && mBird.getX() > (obstacle.getX() + obstacle.getWidth())) {
                obstacle.setScoreAdded();

                if(mDoublePointsActivated) {
                    addDoublePoints();
                } else {
                    score++;
                }

                mHudText.setText(String.valueOf(score));
            }
        }
    }

    public void setPause(boolean bool) {
        if (bool) {
            if(pauseButton.isVisible()) {
                pauseButton.setVisible(false);
                setIgnoreUpdate(true);
                setChildScene(mPauseScene, false, true, true);

                if (mResourceManager.mMusic!=null && mResourceManager.mMusic.isPlaying()) {
                    mResourceManager.mMusic.pause();
                }
            }
        } else {
            clearChildScene();
            setIgnoreUpdate(false);
            pauseButton.setVisible(true);

            if (mResourceManager.mMusic != null && !mResourceManager.mMusic.isPlaying()) {
                mResourceManager.mMusic.play();
            }
        }
    }

    /**
     * This method checks for targets leaving the screen... recycles if so
     * Also checks if a person who was falling has hit the ground
     */
    private void checkTargetPosition() {
        for(int i = mTargets.size() - 1; i >= 0; i--) {
            Target target = mTargets.get(i);
            if (target.getX() <  -target.getWidth() || (target.getX() > SCREEN_WIDTH && mGameOver)) {
                detachChild(target);
                mTargetPool.recyclePoolItem(target);
                mTargetPool.shufflePoolItems();
                mTargets.remove(i);
            } else if (target.getClass().getName().equals("com.application.nick.crappybird.entity.TargetPerson1")) {
                if(((TargetPerson1)target).getFalling() && (target.getY() + target.getHeight()) >= mGround.getY()) {
                    ((TargetPerson1)target).hitsGround(mGameOver);
                }
            }
        }
    }

    private void checkForCollectableLeavingScreen() {
        for(int i = mCollectables.size() - 1; i >= 0; i--) {
            Collectable collectable = mCollectables.get(i);
            if (collectable.getX() <  -collectable.getWidth()) {
                detachChild(collectable);
                mCollectablePool.recyclePoolItem(collectable);
                mCollectablePool.shufflePoolItems();
                mCollectables.remove(i);
            }
        }
    }

    /**
     * used to check if the mothership has left the screen wMotherShipOnScreen = true
     * Also adds score if leaves screen and not game over
     */
    private void checkMotherShipPosition() {
            if(mMotherShip.getX() < -mMotherShip.getWidth()) {
                setMotherShipOnScreen(false);
                if(!mGameOver) {
                    if (mDoublePointsActivated) {
                        addDoublePoints();
                    } else {
                        score++;
                    }

                    mHudText.setText(String.valueOf(score));
                }

            }

    }

    /**
     * this method checks for mothership contact with the bird and targets.
     * if contact with targets it adds to score
     * if contact with bird, gameover (unless machine crapping)
     */
    private void checkForMotherShipContact() {
        if (!mGameOver && mMotherShip.collidesWith(mBird) && !mMotherShip.getCollidedWith()) {
            if (mMachineCrapActivated) {
                final Body faceBody = (Body) mBird.getUserData();

                Vector2 birdVelocity = faceBody.getLinearVelocity();
                float birdYVelocity = birdVelocity.y;
                birdYVelocity = birdYVelocity * 30; //convert from m/s to px/sec
                float velocityY = birdYVelocity * 2;
                if (Math.abs(velocityY) < 500) {
                    velocityY = (velocityY / Math.abs(velocityY)) * 500;
                }
                mMotherShip.blastOff(velocityY);
                mMotherShip.setCollidedWith();
            } else {

                mGameOver = true;
                mResourceManager.mSound.play();
                mBird.stopAnimation(0);
                stopCrap();

                killObstacles();
                killCollectables();
                killTargets();

                mAutoParallaxBackground.setParallaxChangePerSecond(0);
            }
        }

        for(int i = 0; i < mTargets.size(); i++) {
            Target target = mTargets.get(i);
            if(mMotherShip.collidesWith(target) && !target.getHitValue() ) {
                target.hitByCrap(mGameOver);
                if(!mGameOver) {
                    if (mDoublePointsActivated) {
                        addDoublePoints();
                    } else {
                        score++;
                    }

                    mHudText.setText(String.valueOf(score));
                }
            }
        }
    }


    /**
     * used to set mother ship incoming. makes alert sign pop up
     * @param bool
     */
    private void setMotherShipIncoming(boolean bool) {
        if(bool) {
            mMotherShipIncoming = true;
            mAlertSign.setVisible(true);
        } else {
            mMotherShipIncoming = false;
            mAlertSign.setVisible(false);
        }
    }

    /**
     * This method is used to make the mother ship fly across the screen
     * @param bool if true: starts flight. if false: ends flight
     */
    private void setMotherShipOnScreen(boolean bool) {
        if (bool) {
            setMotherShipIncoming(false);
            mMotherShipOnScreen = true;
            mMotherShip.flyingPastScreen(true);
            attachChild(mMotherShip);
        } else {
            mMotherShipOnScreen = false;
            mMotherShipPassed = true;
            mMotherShip.flyingPastScreen(false);
            detachChild(mMotherShip);
        }
    }

    /**
     * adds a new obstacle when the earliest added obstacle still on the screen reaches a certain x value
     * @param x the x value to check
     */
    private void addNewObstacle(float x) {
        if(!mGameOver) {
            if(mObstacles.size() == 0) {
                mObstacles.add(mObstaclePool.obtainPoolItem());
                Obstacle newObstacle = mObstacles.get(mObstacles.size() - 1);
                newObstacle.setX(SCREEN_WIDTH);
                //scale plane velocity according to number of passed obstacles
                if (newObstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstaclePlane")) {
                    if (mObstaclePool.getObstacleIndex() < 100) {
                        newObstacle.setVelocity(-200f - mObstaclePool.getObstacleIndex(), 0);
                    } else {
                        newObstacle.setVelocity(-300, 0);
                    }
                    //if passed obstacle number x, have plane fly at random angles across the screen
                    if (mObstaclePool.getObstacleIndex() > 50) {
                        ((ObstaclePlane) newObstacle).randomizeYVelocity();
                    }
                } else if (newObstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstacleBalloon")) {
                    if (mObstaclePool.getObstacleIndex() > 0) {
                        ((ObstacleBalloon) newObstacle).randomizeYVelocity();
                    }
                }
                attachChild(newObstacle);
                sortChildren();
            } else {
                for (int i = mObstacles.size() - 1; i >= 0; i--) {
                    Obstacle obstacle = mObstacles.get(i);
                    if (obstacle.getX() < x && !obstacle.getPassedAddXValue()) {
                        obstacle.passedAddXValue();
                        mObstacles.add(mObstaclePool.obtainPoolItem());
                        Obstacle newObstacle = mObstacles.get(mObstacles.size() - 1);
                        newObstacle.setX(SCREEN_WIDTH);
                        //scale plane velocity according to number of passed obstacles
                        if (newObstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstaclePlane")) {
                            if (mObstaclePool.getObstacleIndex() < 100) {
                                newObstacle.setVelocity(-200f - mObstaclePool.getObstacleIndex(), 0);
                            } else {
                                newObstacle.setVelocity(-300, 0);
                            }
                            //if passed obstacle number x, have plane fly at random angles across the screen
                            if (mObstaclePool.getObstacleIndex() > 50) {
                                ((ObstaclePlane) newObstacle).randomizeYVelocity();
                            }
                        } else if (newObstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstacleBalloon")) {
                            if (mObstaclePool.getObstacleIndex() > 0) {
                                ((ObstacleBalloon) newObstacle).randomizeYVelocity();
                            }
                        }
                        attachChild(newObstacle);
                        sortChildren();
                    }
                }
            }

        }
    }

    /**
     * adds a new collectable when the earliest added collectable still on the screen reaches a certain x value
     * @param x the x value to check
     */
    private void addNewCollectable(float x) {
        if(!mGameOver) {
            for(int i = mCollectables.size() - 1; i >= 0; i--) {
                Collectable collectable = mCollectables.get(i);
                    if (collectable.getX() < x && !collectable.getPassedAddXValue()) {
                        collectable.passedAddXValue();
                        mCollectables.add(mCollectablePool.obtainPoolItem());
                        attachChild(mCollectables.get(mCollectables.size() - 1));
                        sortChildren();
                    }

            }
        }
    }

    /**
     * adds a new target when the earliest added target still on the screen reaches a certain x value
     * @param x the x value to check
     */
    private void addNewTarget(float x) {
        if(!mGameOver) {
            int numTargets = mTargets.size();
            for(int i = mTargets.size() - 1; i >= 0; i--) {
                Target target = mTargets.get(i);
                if (target.getX() < x && !target.getPassedAddXValue()) {
                    target.passedAddXValue();
                    if(numTargets <= MAX_TARGETS_ON_SCREEN) {
                        mTargets.add(mTargetPool.obtainPoolItem());
                        Target newTarget = mTargets.get(mTargets.size() - 1);
                        if(newTarget.getHitValue()) {
                            newTarget.reset();
                        }
                        attachChild(newTarget);
                        sortChildren();
                    }
                }

            }
        }
    }

    /**
     * used when bird dies
     */
    private void killTargets() {
        for(int i = mTargets.size() - 1; i >= 0; i--) {
            Target target = mTargets.get(i);
            if(!target.getHitValue()) {
                float xVelocity = target.getXVelocity();
                target.setVelocity(xVelocity + 150, 0);
            } else {
                target.setVelocity(0, 0);
            }
        }
    }

    /**
     * used when bird dies
     */
    private void killObstacles() {

        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if(obstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstaclePlane")) {
                if(obstacle.getVelocityX() < -210) {
                    obstacle.setVelocityX(obstacle.getVelocityX() + 150f); //keep vertical velocity //plane keeps flying just at a lower velocity because scrolling stops
                } else {
                    obstacle.setVelocityX(-60f);
                }
            } else if (obstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstacleBalloon")) {
                obstacle.setVelocityX(0);
            } else {
                obstacle.die();
            }

        }
    }

    /**
     * used when bird dies
     */
    private void killCollectables() {

        for(int i = mCollectables.size() - 1; i >= 0; i--) {
            Collectable collectable = mCollectables.get(i);
                collectable.setVisible(false);
                collectable.die();

        }
    }


    private void checkForTargetCrapContact() {
        for(int i = mCraps.size() - 1; i >= 0; i--) {
            Crap crap = mCraps.get(i);
            if(!crap.getFalling() || mGameOver) {
                break;
            } else {
                boolean crapContact = false;
                for(int j = mTargets.size() - 1; j >= 0; j--) {
                    Target target = mTargets.get(j);

                    //if target hits crap and the target isn't a person FALLING from a balloon...
                    if(target.collidesWith(crap) && !(target.getClass().getName().equals("com.application.nick.crappybird.entity.TargetPerson1") && ((TargetPerson1)target).getFalling())) {
                        target.hitByCrap();
                        crapContact = true;
                        if(mDoublePointsActivated) {
                            addDoublePoints();
                        } else {
                            score++;
                        }

                        mHudText.setText(String.valueOf(score));
                    }
                }
                if(crapContact) {
                    detachChild(mCraps.get(i));
                    mCrapPool.recyclePoolItem(mCraps.get(i));
                    mCraps.remove(i);
                }
            }
        }
    }


    private void checkForObstacleBirdContact() {
        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if (!mGameOver && obstacle.collidesWith(mBird) && !obstacle.getCollidedWith()) {
                if(mMachineCrapActivated) {
                    final Body faceBody = (Body) mBird.getUserData();

                    Vector2 birdVelocity = faceBody.getLinearVelocity();
                    float birdYVelocity = birdVelocity.y;
                    birdYVelocity = birdYVelocity * 30; //convert from m/s to px/sec
                    float velocityY = birdYVelocity * 2;
                    if (Math.abs(velocityY) < 500) {
                        velocityY = (velocityY / Math.abs(velocityY)) * 500;
                    }
                    obstacle.blastOff(velocityY);
                    obstacle.setCollidedWith();
                } else {

                    mGameOver = true;
                    mResourceManager.mSound.play();
                    mBird.stopAnimation(0);
                    stopCrap();

                    killObstacles();
                    killCollectables();
                    killTargets();

                    mAutoParallaxBackground.setParallaxChangePerSecond(0);
                }
                return;
            } else if //if this is a hot air balloon and the bird hits the basket... knock the person out
                    (!mGameOver &&  obstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstacleBalloon") &&
                            ((ObstacleBalloon)obstacle).collidesWithBasket(mBird) && !((ObstacleBalloon)obstacle).getBasketHit())
            {
                ((ObstacleBalloon) obstacle).birdHitsBasket();
                mTargets.add(mTargetPool.obtainPoolItem());
                ((TargetPerson1)(mTargets.get(mTargets.size() - 1))).setFalling(obstacle.getVelocityX(), obstacle.getX() + 28, obstacle.getY() + 110);
                attachChild(mTargets.get(mTargets.size() - 1));
                sortChildren();

                if(mDoublePointsActivated) {
                    addDoublePoints();
                } else {
                    score++;
                }

                mHudText.setText(String.valueOf(score));

            }


        }
    }

    private void checkForCollectableBirdContact() {
        for(int i = mCollectables.size() - 1; i >= 0; i--) {
            Collectable collectable = mCollectables.get(i);
            if (!mGameOver && collectable.collidesWith(mBird) && !collectable.getCollected()) {
                collectable.setVisible(false);
                collectable.collect();
                if(collectable.getClass().getName().equals("com.application.nick.crappybird.entity.CollectableTaco")) {
                    setMachineCrap(true);
                } else if(collectable.getClass().getName().equals("com.application.nick.crappybird.entity.CollectableHam")) {
                    setDoublePoints(true);
                }
                growCrapMeter(MAX_CRAP_METER_SIZE); //fill crap meter
            }


        }
    }

    private void checkForCrapGroundContact(IShape ground) {
        for (int i = mCraps.size() - 1; i >= 0; i--) {
            if (mCraps.get(i).getY() + mCraps.get(i).getHeight() > ground.getY()) {
                mCraps.get(i).hitsGround(mGameOver);
            }
        }
    }

    /**
     * Shrinks crap meter on jump/crap
     * @param amountToShrink
     */
    private void shrinkCrapMeter(float amountToShrink) {
        if(!mMachineCrapActivated) {
            float currentWidth = mCrapMeter.getWidth();
            if (currentWidth <= amountToShrink) {
                mOutOfCrap = true;
                mCrapMeter.setWidth(0);
            } else {
                mCrapMeter.setWidth(currentWidth - amountToShrink);
            }
        }
    }

    private void growCrapMeter(float amountToGrow) {
        float currentWidth = mCrapMeter.getWidth();
        if(currentWidth + amountToGrow > MAX_CRAP_METER_SIZE) {
            mCrapMeter.setWidth(MAX_CRAP_METER_SIZE);
        } else {
            mCrapMeter.setWidth(currentWidth + amountToGrow);
        }
        mOutOfCrap = false;
    }

    /**
     * Check if crap has left screen. If so, recycle
     */
    private void checkForCrapLeavingScreen() {
        for (int i = mCraps.size() - 1; i >= 0; i--) {
            if(mCraps.get(i).getX() < -mResourceManager.mCrapTextureRegion.getWidth()) {
                detachChild(mCraps.get(i));
                mCrapPool.recyclePoolItem(mCraps.get(i));
                mCraps.remove(i);
            }
        }
    }


    /**
     * Stops crap when gameover
     */
    private void stopCrap() {
        if(mCraps.size() > 0) {
                for (int i = mCraps.size() - 1; i >= 0; i--) {
                    mCraps.get(i).setXVelocity(0);
                }

        }
    }

    /**
     * for turning on and off the machine crap power up
     * @param bool
     */
    private void setMachineCrap(boolean bool) {
        if(bool) {
            mMachineCrapActivated = true;
            mMachineCrapMeter.setVisible(true);
            mMachineCrapMeter.animate(new long[]{100, 100, 100, 100, 100, 100}, 0 ,5, true);
            mCrapMeter.setVisible(false);
            machineCrappingLastCrapTime = 0;
            machineCrappingTime = 0;
        } else {
            mMachineCrapActivated = false;
            mMachineCrapMeter.setVisible(false);
            mCrapMeter.setVisible(true);
            growCrapMeter(MAX_CRAP_METER_SIZE);
        }
    }

    /**
     * for turning on and off the double points power up
     * @param bool
     */
    private void setDoublePoints(boolean bool) {
        if(bool) {
            mDoublePointsActivated = true;
            doublePointsTime = 0;
            doublePointsLastPointTime = 0;
        } else {
            mDoublePointsActivated = false;
        }
    }

    /**
     * for adding points to the score when double points is activated
     */
    private void addDoublePoints() {
        mPlusTwo.setVisible(true);
        score += 2;
        doublePointsLastPointTime = doublePointsTime;
    }

    private void dropCrap(float currentXPosition, float currentYPosition) {
        mCraps.add(mCrapPool.obtainPoolItem());

        Crap crap = mCraps.get(mCraps.size() - 1);

        crap.setPosition(currentXPosition, currentYPosition + (mBird.getHeight()));

        attachChild(crap);

        shrinkCrapMeter((float) Math.sqrt((double) mObstaclePool.getObstacleIndex() * 4));
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if(mPhysicsWorld != null) {
            if(!mGameOver && !mOutOfCrap && pSceneTouchEvent.isActionDown() && !mMachineCrapActivated) {
                jumpBird(mBird);
                return true;
            } else if(!mGameOver && mMachineCrapActivated && pSceneTouchEvent.isActionDown()) {
                mMachineCrapping = true;
                machineCrappingLastCrapTime = 0;
            } else if(mMachineCrapping && pSceneTouchEvent.isActionUp()) {
                mMachineCrapping = false;
            }
        }
        return false;
    }

    private void jumpBird(final AnimatedSprite face) {

        face.setRotation(-15);
        final Body faceBody = (Body)face.getUserData();

        float currentYPosition = face.getY();
        float currentXPosition = face.getX();
        Vector2 currentVelocity = faceBody.getLinearVelocity();
        float currentYVelocity = currentVelocity.y;


        Vector2 newVelocity;

        if(currentYVelocity < 0) {
            float newYVelocity = currentYVelocity -(20 + currentYVelocity) * (2.0f / 8.0f);
            newVelocity = Vector2Pool.obtain(0, newYVelocity);
        } else {
            newVelocity = Vector2Pool.obtain(0, -5f);
        }

        faceBody.setLinearVelocity(newVelocity);
        Vector2Pool.recycle(newVelocity);

        dropCrap(currentXPosition, currentYPosition);


    }

    private void displayScore() {

        scoreText.setText(String.valueOf(score)); //update values
        mostText.setText(String.valueOf(most));

        scoreText.setX(scoreText.getX() - (scoreText.getWidth() / 2)); //adjust margins
        mostText.setX(mostText.getX() - (mostText.getWidth() / 2));

        setChildScene(mGameOverScene, false, true, true);

    }

    private ContactListener createContactListener() {
        ContactListener contactListener = new ContactListener() {
            @Override
            public void beginContact(Contact pContact) {
                final Fixture fixtureA = pContact.getFixtureA();
                final Body bodyA = fixtureA.getBody();
                final String userDataA = (String) bodyA.getUserData();

                final Fixture fixtureB = pContact.getFixtureB();
                final Body bodyB = fixtureB.getBody();
                final String userDataB = (String) bodyB.getUserData();

                if (("bird".equals(userDataA) && "ground".equals(userDataB)) || ("ground".equals(userDataA) && "bird".equals(userDataB))) {

                    if(!mGameOver) { //if the bird hasn't already hit an obstacle
                        mResourceManager.mSound.play();
                        mGameOver = true;
                    }

                    mBird.stopAnimation(0);
                    killObstacles();
                    killCollectables();
                    mAutoParallaxBackground.setParallaxChangePerSecond(0);

                    if (score > most) {
                        most = score;
                        mActivity.setMaxScore(most);
                    }

                    //update user's account highscore if logged in
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null && mActivity.isNetworkAvailable()) {
                        if(currentUser.getInt("highScore") < most) {
                            currentUser.put("highScore", most);
                            currentUser.saveInBackground();
                        }
                    }

                    //hide score and crapMeter
                    mHudText.setVisible(false);
                    mCrapMeter.setVisible(false);
                    mMachineCrapMeter.setVisible(false);
                    mPlusTwo.setVisible(false);
                    mAlertSign.setVisible(false);
                    pauseButton.setVisible(false);

                    //display game over with score
                    displayScore();
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        };
        return contactListener;
    }

    @Override
    public void onBackKeyPressed() {
        if(mSharingVisible) {
            setSharingVisible(false);
        } else {

            mHudText.setVisible(false);
            mMachineCrapMeter.setVisible(false);
            mCrapMeter.setVisible(false);
            mPlusTwo.setVisible(false);
            mAlertSign.setVisible(false);

            mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);

        }
    }


    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene() {
        //TODO
    }



}
