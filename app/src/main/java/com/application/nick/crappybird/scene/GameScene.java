package com.application.nick.crappybird.scene;

import android.util.Log;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.Bird;
import com.application.nick.crappybird.entity.Collectable;
import com.application.nick.crappybird.entity.CollectableBurger;
import com.application.nick.crappybird.entity.CollectableHam;
import com.application.nick.crappybird.entity.CollectableMelon;
import com.application.nick.crappybird.entity.CollectableMuffin;
import com.application.nick.crappybird.entity.CollectablePool;
import com.application.nick.crappybird.entity.CollectableTaco;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.MegaCrap;
import com.application.nick.crappybird.entity.MegaCrapPool;
import com.application.nick.crappybird.entity.MotherShip;
import com.application.nick.crappybird.entity.Obstacle;
import com.application.nick.crappybird.entity.ObstacleBalloon;
import com.application.nick.crappybird.entity.ObstaclePlane;
import com.application.nick.crappybird.entity.ObstaclePoolAir;
import com.application.nick.crappybird.entity.ObstaclePoolGround;
import com.application.nick.crappybird.entity.Target;
import com.application.nick.crappybird.entity.TargetPerson1;
import com.application.nick.crappybird.entity.TargetPool;
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
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 4/5/2015.
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener {

    public static final float HYPER_SPEED_VELOCITY_SHIFT = 300;
    public static final float SCROLL_SPEED = 150;
    public static final float GRAVITY = 15f * 30;
    private final float HYPER_SPEED_Y_ACCELERATION = 5f * 30;
    private final float DEFAULT_MACHINE_CRAPPING_TIME = 5;
    private final float MAX_INVINCIBILITY_TIME = 2;
    private final float MACHINE_CRAPPING_TIME_BETWEEN_CRAPS = 0.1f;
    private final float HYPER_SPEED_TIME_BETWEEN_CRAPS = 0.1f;
    private final float DEFAULT_DOUBLE_POINTS_TIME = 5;
    private final float MAX_TARGETS_ON_SCREEN = 10;
    private final float DOUBLE_POINTS_TIME_TO_SHOW_PLUS_TWO = 0.4f;
    private final float MAX_COLLECTABLES_ON_SCREEN = 5;
    private final float MIN_PLANE_SPEED_ON_GAMEOVER = 60;
    private final int PARALLAX_CHANGE_PER_SECOND = 10;
    private final int NUM_OBSTACLES = 20; //number of obstacle to allocate to pool
    private final int NUM_TARGETS = 10; //number of targets (people) to allocate
    private final int NUM_CRAPS = 15; //number of craps to allocate
    private final int MOTHERSHIP_OBSTACLE_INDEX = 35; //the obstacle index to have the mothership fly across the screen
    private final int RESPAWN_PRICE = 200;
    private final int CRAP_METER_SHRINK_STOP_OBSTACLE_INDEX = 50; //the obstacle index to stop having the crap meter rate of shrinkage increase

    public static final int[] MAX_OBSTACLES_ON_SCREEN = {1, 2, 3, 4, 5, 6};

    public static final int[] MAX_OBSTACLES_ON_SCREEN_OBSTACLE_INDEX = {0, 3, 10, 100, 250, 450};

    public static final int[] NUM_COLLECTABLES = { //number of generic collectables (pizza) to allocate to pool
            20, //if player only has first 2 power-ups unlocked
            25, //if player has three power-ups
            30,  //if player has 4 power-ups
            35 //if player has 5 power-ups
    };

    private AutoParallaxBackground mAutoParallaxBackground;

    private Text mHudTextScore, mHudTextPizza, totalPizzaTextOnCountdownScene;
    private int score;
    private int most;
    private int pizzaCollected = 0;

    private Sprite pizzaCollectedSpriteOnGameOver, pizzaCollectedSprite;

    private Bird mBird;

    private AnimatedSprite mAnimatedCrapMeter, mCountdownSprite;

    private TiledSprite mCrapMeter, mAlertSign;

    private MotherShip mMotherShip;

    private List<Obstacle> mObstacles;
    private ObstaclePoolAir mObstaclePoolAir;
    private ObstaclePoolGround mObstaclePoolGround;

    private List<Collectable> mCollectables;
    private CollectablePool mCollectablePool;

    private List<Target> mTargets;
    private TargetPool mTargetPool;

    private CrapPool mCrapPool;
    private MegaCrapPool mMegaCrapPool;
    private List<Crap> mCraps = new ArrayList<Crap>();


    private boolean mGameOver, mOutOfCrap, mMachineCrapActivated = false, mMachineCrapping = false, mAnimatedCrapMeterBlinkingSlow = false, mAnimatedCrapMeterBlinkingFast = false,
            mDoublePointsActivated = false, mMotherShipIncoming = false, mMotherShipOnScreen = false, mMotherShipPassed = false, mSharingVisible = false,
            mRespawnUsed = false, mCountdownOnScreen = false, mRespawnButtonPressed = false, mMegaCrapActivated = false, mSlowMotionActivated = false, mHyperSpeedActivated = false,
            mHasMegaCrap, mHasSlowMotion, mHasHyperSpeed, mInvincibilityActivated = false;

    private float obstacleGroundSpawnCounter = 0, obstacleAirSpawnCounter = 0, machineCrappingTime = 0, machineCrappingLastCrapTime, doublePointsTime = 0, doublePointsLastPointTime, megaCrappingTime = 0, slowMotionTime = 0, hyperSpeedTime = 0,
            lastHyperSpeedCrapTime = 0, invincibilityTime = 0, maxMachineCrappingTime, maxDoublePointsTime, maxSlowMotionTime, maxMegaCrapTime, maxHyperSpeedTime;

    private CameraScene mGameReadyScene, mGameOverScene, mPauseScene, mCountdownScene;
    private Text scoreText, mostText, pizzaTextOnGameOver;
    private TiledSprite mPlusTwo, playButton, backButton, rateButton, shareButton, facebookButton,
            twitterButton, otherButton, leaderboardButton, pauseButton, volumeButton, menuButton, marketButton;

    private Rectangle mGround;

    private float MAX_CRAP_METER_SIZE, mostX;

    private int selectedBird, collectableAllocationIndex, numPlanesOnScreen = 0;


    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        setOnSceneTouchListener(this);

        if(ParseUser.getCurrentUser() != null && mActivity.isNetworkAvailable()) {
            mActivity.updateCurrentUser();
        }

        if (mResourceManager.mMusic != null && !mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.resume();
        }
        if (mResourceManager.mMariachiFast.isPlaying() || mResourceManager.mMariachiSlow.isPlaying()) {
            mResourceManager.mMariachiFast.pause();
            mResourceManager.mMariachiSlow.pause();
        }

        mAutoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 10);
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-5.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerBack.getHeight(), mResourceManager.mParallaxLayerBack, mVertexBufferObjectManager)));
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), mResourceManager.mParallaxLayerFront, mVertexBufferObjectManager)));
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight() - mResourceManager.mParallaxLayerMiddle.getHeight(), mResourceManager.mParallaxLayerMiddle, mVertexBufferObjectManager)));
        setBackground(mAutoParallaxBackground);

        //create entities
        final float birdX = (SCREEN_WIDTH - mResourceManager.mBirdTextureRegion.getWidth()) / 2 - 50;
        final float birdY = (SCREEN_HEIGHT - mResourceManager.mBirdTextureRegion.getHeight()) / 2 - 30;


        selectedBird = mActivity.getSelectedBird();

        if(ParseUser.getCurrentUser() != null) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if(currentUser.getInt("powerUp0") == 0) {
                currentUser.put("powerUp0", 1);
                currentUser.put("powerUp1", 1);
            }

            maxMachineCrappingTime = 3 + (currentUser.getInt("powerUp0") * 2);
            Log.i("maxMachineCrappingTime", String.valueOf(maxMachineCrappingTime));

            maxDoublePointsTime = 3 + (currentUser.getInt("powerUp1") * 2);
            Log.i("maxDoublePointsTime", String.valueOf(maxDoublePointsTime));

            maxSlowMotionTime = 3 + (currentUser.getInt("powerUp2") * 2);
            Log.i("maxSlowMotionTime", String.valueOf(maxSlowMotionTime));

            maxMegaCrapTime = 3 + (currentUser.getInt("powerUp3") * 2);
            Log.i("maxMegaCrapTime", String.valueOf(maxMegaCrapTime));

            maxHyperSpeedTime = 3 + (currentUser.getInt("powerUp4") * 2);
            Log.i("maxHyperSpeedTime", String.valueOf(maxHyperSpeedTime));

            collectableAllocationIndex = 0;

            if(maxSlowMotionTime > 3) {
                mHasSlowMotion = true;
                collectableAllocationIndex++; //increment collectable allocation index so that more generic pizza will be allocated to pool
                                                // to account for more power-ups
            } else {
                mHasSlowMotion = false;
            }

            if(maxMegaCrapTime > 3) {
                mHasMegaCrap = true;
                collectableAllocationIndex++;
            } else {
                mHasMegaCrap = false;
            }

            if(maxHyperSpeedTime > 3) {
                mHasHyperSpeed = true;
                collectableAllocationIndex++;
            } else {
                mHasHyperSpeed = false;
            }

        } else {
            maxMachineCrappingTime = DEFAULT_MACHINE_CRAPPING_TIME;
            maxDoublePointsTime = DEFAULT_DOUBLE_POINTS_TIME;
            mHasSlowMotion = false;
            mHasMegaCrap = false;
            mHasHyperSpeed = false;

            collectableAllocationIndex = 0;
        }

        mBird = new Bird(birdX, birdY, mResourceManager.mBirdsTextureRegion, mVertexBufferObjectManager);
        mBird.setZIndex(10);
        mBird.animate(selectedBird);
        mBird.setCurrentTileIndex(selectedBird * 3);
        attachChild(mBird);


        //create entities
        mGround = new Rectangle(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight() + 10, SCREEN_WIDTH, mResourceManager.mParallaxLayerFront.getHeight(), mVertexBufferObjectManager);
        mGround.setColor(Color.TRANSPARENT);
        final Rectangle roof = new Rectangle(0, 0, SCREEN_WIDTH, 1, mVertexBufferObjectManager);
        roof.setColor(Color.TRANSPARENT);

        mObstaclePoolAir = new ObstaclePoolAir(mResourceManager, mVertexBufferObjectManager, mGround.getY());
        mObstaclePoolAir.batchAllocatePoolItems(NUM_OBSTACLES / 2);

        mObstaclePoolGround = new ObstaclePoolGround(mResourceManager, mVertexBufferObjectManager, mGround.getY());
        mObstaclePoolGround.batchAllocatePoolItems(NUM_OBSTACLES / 2);

        mObstacles = new ArrayList<Obstacle>();
        addObstacleGround();

        mCollectablePool = new CollectablePool(mResourceManager, mVertexBufferObjectManager, mGround.getY());
        mCollectablePool.batchAllocatePoolItems(NUM_COLLECTABLES[collectableAllocationIndex]);
        //add special items to the pool
        //taco is for machine crap
        CollectableTaco taco = new CollectableTaco(mResourceManager.mCollectableTacoTextureRegion, mVertexBufferObjectManager, mGround.getY(), mResourceManager.mCollectableTacoTextureRegion.getHeight());
        mCollectablePool.addAdditionalPoolItem(taco);
        //ham is double points
        CollectableHam ham = new CollectableHam(mResourceManager.mCollectableHamTextureRegion, mVertexBufferObjectManager, mGround.getY(), mResourceManager.mCollectableHamTextureRegion.getHeight());
        mCollectablePool.addAdditionalPoolItem(ham);

        if(mHasMegaCrap) {
            //Mega melon gives mega massive craps
            CollectableMelon melon = new CollectableMelon(mResourceManager.mCollectableMelonTextureRegion, mVertexBufferObjectManager, mGround.getY(), mResourceManager.mCollectableMelonTextureRegion.getHeight());
            mCollectablePool.addAdditionalPoolItem(melon);
        }
        if(mHasSlowMotion) {
            //Slo-mo muffin slows down obstacles
            CollectableMuffin muffin = new CollectableMuffin(mResourceManager.mCollectableMuffinTextureRegion, mVertexBufferObjectManager, mGround.getY(), mResourceManager.mCollectableMuffinTextureRegion.getHeight());
            mCollectablePool.addAdditionalPoolItem(muffin);
        }
        if(mHasHyperSpeed) {
            //burger is for hyper speed
            CollectableBurger burger = new CollectableBurger(mResourceManager.mCollectableBurgerTextureRegion, mVertexBufferObjectManager, mGround.getY(), mResourceManager.mCollectableBurgerTextureRegion.getHeight());
            mCollectablePool.addAdditionalPoolItem(burger);
        }



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
        mMegaCrapPool = new MegaCrapPool(mResourceManager.mMegaCrapTextureRegion, mVertexBufferObjectManager);
        mMegaCrapPool.batchAllocatePoolItems(NUM_CRAPS);
        mOutOfCrap = false;

        attachChild(mGround);
        attachChild(roof);
        attachChild(mCollectables.get(mCollectables.size() - 1));
        attachChild(mTargets.get(mTargets.size() - 1));
        attachChild(mAlertSign);


        /* The actual collision-checking. */
        registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void reset() {}

            @Override
            public void onUpdate(float pSecondsElapsed) {

                checkForBirdGroundContact();

                checkObstaclePosition(); //handle obstacles leaving screen and update score if bird passes them

                checkCollectablePosition();

                checkTargetPosition(); //check targets leaving screen and also handle falling people from balloons

                if(mCraps.size() > 0) {
                    checkCrapPosition(mGround);
                }

                addNewTarget(SCREEN_WIDTH / 2); //add a new target if the earliest added on the screen passes x

                if(mCollectables.size() <= MAX_COLLECTABLES_ON_SCREEN) {
                    addNewCollectable(300); //add a new collectable if the earliest added on the screen passes x
                }

                if(!mGameOver) {
                    if(mSlowMotionActivated) {
                        obstacleAirSpawnCounter += pSecondsElapsed / 2;
                        obstacleGroundSpawnCounter += pSecondsElapsed / 2;
                    } else if (mHyperSpeedActivated) {
                        obstacleAirSpawnCounter += pSecondsElapsed * 3;
                        obstacleGroundSpawnCounter += pSecondsElapsed * 3;
                    } else {
                        obstacleAirSpawnCounter += pSecondsElapsed;
                        obstacleGroundSpawnCounter += pSecondsElapsed;
                    }

                    float airTimeMin = mResourceManager.mObstaclePlanesTextureRegion.getWidth() / getPlaneSpeed();
                    float groundTimeMin = mResourceManager.mObstacleHouseTextureRegion.getWidth() / SCROLL_SPEED;

                    double groundDistance = (SCREEN_WIDTH - mResourceManager.mObstacleHouseTextureRegion.getWidth()) / Math.pow(getObstacleIndex(), 1.0/6) + mResourceManager.mObstacleHouseTextureRegion.getWidth();
                    float groundTime = (float)groundDistance / SCROLL_SPEED;

                    double airDistance = (SCREEN_WIDTH - mResourceManager.mObstaclePlanesTextureRegion.getWidth()) / Math.pow(getObstacleIndex(), 1.0/6) + mResourceManager.mObstaclePlanesTextureRegion.getWidth();
                    float airTime = (float)airDistance / getPlaneSpeed();

                    if(!mMotherShipOnScreen && !mMotherShipIncoming) {
                        for(int i = GameScene.MAX_OBSTACLES_ON_SCREEN.length - 1; i >= 0; i--) {
                            if(getObstacleIndex() >= GameScene.MAX_OBSTACLES_ON_SCREEN_OBSTACLE_INDEX[i]) {
                                if (mObstacles.size() < MAX_OBSTACLES_ON_SCREEN[i]) {
                                    int rand = (int)(Math.random() * 2); //here random allows air and ground obstacles to have an equal chance of spawning
                                    if(rand == 0) {
                                        if (obstacleAirSpawnCounter > airTime) {
                                            addObstacleAir();
                                        } else if (obstacleGroundSpawnCounter > groundTime) {
                                            addObstacleGround();
                                        }
                                    } else {
                                        if (obstacleGroundSpawnCounter > groundTime) {
                                            addObstacleGround();
                                        } else if (obstacleAirSpawnCounter > airTime) {
                                            addObstacleAir();
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }


                //Handle mother ship////////////////////////////////
                if(getObstacleIndex() >= MOTHERSHIP_OBSTACLE_INDEX && !mMotherShipIncoming && !mMotherShipOnScreen && !mMotherShipPassed) {
                    setMotherShipIncoming(true);
                }
                if(mMotherShipIncoming && mObstacles.size() == 0) {
                    setMotherShipOnScreen(true);
                }
                if(mMotherShipOnScreen) {
                    checkMotherShipPosition();
                    checkForMotherShipContact();
                }
                if(!mGameOver) { //only advance powerup times if gameover is true. This way, you will still have them after a respawn

                    //Handle machine crap powerup/////////////////////////////////
                    if (mMachineCrapActivated) {
                        if (mMachineCrapping) {
                            if (machineCrappingTime > machineCrappingLastCrapTime + MACHINE_CRAPPING_TIME_BETWEEN_CRAPS) {
                                jumpBird();
                                machineCrappingLastCrapTime = machineCrappingTime;
                            }
                        }

                        machineCrappingTime += pSecondsElapsed;
                        if (machineCrappingTime > maxMachineCrappingTime) {
                            setMachineCrap(false);
                            machineCrappingTime = 0;
                        }

                    }

                    //Handle hyper speed powerup/////////////////////////////////
                    if (mHyperSpeedActivated) {
                        if (hyperSpeedTime > lastHyperSpeedCrapTime + HYPER_SPEED_TIME_BETWEEN_CRAPS) {
                            dropHyperSpeedCrap(mBird.getX(), mBird.getY());
                            lastHyperSpeedCrapTime = hyperSpeedTime;
                        }

                        hyperSpeedTime += pSecondsElapsed;
                        if (hyperSpeedTime > maxHyperSpeedTime) {
                            setHyperSpeed(false);
                            hyperSpeedTime = 0;
                        }

                    }

                    if(mInvincibilityActivated) {
                        invincibilityTime += pSecondsElapsed;
                        if (invincibilityTime > MAX_INVINCIBILITY_TIME) {
                            setInvincibility(false);
                        } else if (invincibilityTime > MAX_INVINCIBILITY_TIME - 0.5f && !mAnimatedCrapMeterBlinkingFast) {
                            mAnimatedCrapMeter.animate(new long[]{100, 100}, 5, 6, true);
                            mAnimatedCrapMeterBlinkingFast = true;
                            mAnimatedCrapMeterBlinkingSlow = false;
                        }
                    }

                    //Handle mega crap powerup/////////////////////////////////
                    if (mMegaCrapActivated) {
                        megaCrappingTime += pSecondsElapsed;
                        if (megaCrappingTime > maxMegaCrapTime) {
                            setMegaCrap(false);
                            megaCrappingTime = 0;
                        }
                    }

                    //Handle slow motion powerup/////////////////////////////////
                    if (mSlowMotionActivated) {
                        slowMotionTime += pSecondsElapsed;
                        if (slowMotionTime > maxSlowMotionTime) {
                            setSlowMotion(false);
                            slowMotionTime = 0;
                        }
                    }

                    //Handle double points powerup//////////////////////////////////
                    if (mDoublePointsActivated) {
                        if (mPlusTwo.isVisible()) { //flash "+2" next to score for every point scored while double points is activated
                            if (doublePointsTime > doublePointsLastPointTime + DOUBLE_POINTS_TIME_TO_SHOW_PLUS_TWO) {
                                mPlusTwo.setVisible(false);
                            }
                        }

                        doublePointsTime += pSecondsElapsed;
                        if (doublePointsTime > maxDoublePointsTime) {
                            setDoublePoints(false);
                            mPlusTwo.setVisible(false);
                        }

                    }
                }

                //Handle Countdown Scene////////////////////////
                if(mCountdownOnScreen) {
                    if(mCountdownSprite.getCurrentTileIndex() == 5) {
                        setCountDown(false);
                        displayScore();
                    }
                }

                //don't let bird leave top of screen
                if (mBird.getY() < 0) {
                    mBird.setVelocityY(0);
                }


                    //rotate bird with changing velocity
                if(mBird.getY() + mBird.getHeight() < mGround.getY()) {
                    mBird.setRotation(mBird.getVelocityY() / 15 - 10);
                }


                //mPhysicsWorld.onUpdate(pSecondsElapsed);

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
                    mHudTextScore.setVisible(true);
                    mHudTextPizza.setVisible(true);
                    pizzaCollectedSprite.setVisible(true);
                    mCrapMeter.setVisible(true);
                    pauseButton.setVisible(true);

                    if(numPlanesOnScreen > 0) {
                        mResourceManager.mPropellerSound.play();
                    }

                }
                return true;
            }
        });

        setChildScene(mGameReadyScene, false, true, true);
        if(numPlanesOnScreen > 0) {
            mResourceManager.mPropellerSound.stop();
        }


        //create CameraScene for game over
        final float overX = (SCREEN_WIDTH - mResourceManager.mScoreBoardTextureRegion.getWidth()) / 2;
        final float overY = labelY + mResourceManager.mStateTextureRegion.getHeight();

        final float playX = SCREEN_WIDTH / 2 - mResourceManager.mPlayButtonTextureRegion.getWidth();
        final float playY = overY + mResourceManager.mScoreBoardTextureRegion.getHeight();

        final float leaderboardX = SCREEN_WIDTH / 2;
        final float leaderboardY = overY + mResourceManager.mScoreBoardTextureRegion.getHeight();

        final float shareX = leaderboardX;
        final float shareY = leaderboardY + mResourceManager.mBackButtonTextureRegion.getHeight();

        final float menuX = playX;
        final float menuY = shareY + mResourceManager.mBackButtonTextureRegion.getHeight();

        final float marketX = playX;
        final float marketY = shareY;

        final float rateX = shareX;
        final float rateY = menuY;

        final float scoreX = overX + 55;
        final float scoreY = overY + 40;

        mostX = overX + 165;
        final float mostY = scoreY;

        mGameOverScene = new CameraScene(mCamera);

        final TiledSprite gameOverTitle = new TiledSprite(readyX, readyY + mResourceManager.mStateTextureRegion.getHeight() / 2, mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
        gameOverTitle.setCurrentTileIndex(1);
        gameOverTitle.setScale(1.3f);
        mGameOverScene.attachChild(gameOverTitle);

        final Sprite boardSprite = new Sprite(overX, overY, mResourceManager.mScoreBoardTextureRegion, mVertexBufferObjectManager);
        mGameOverScene.attachChild(boardSprite);

        scoreText = new Text(scoreX, scoreY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        scoreText.setText("0");
        mGameOverScene.attachChild(scoreText);

        mostText = new Text(mostX, mostY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mostText.setText(String.valueOf(most));
        mGameOverScene.attachChild(mostText);



        final float bottomOfBoardY = overY + boardSprite.getHeightScaled();
        final float pizzaY = (mostText.getY() + mostText.getHeight() + bottomOfBoardY) / 2 - mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2;
        final float pizzaTextY = (mostText.getY() + mostText.getHeight() + bottomOfBoardY) / 2 - mResourceManager.mFont3.getLineHeight() / 2;

        pizzaCollectedSpriteOnGameOver = new Sprite(0, pizzaY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mGameOverScene.attachChild(pizzaCollectedSpriteOnGameOver);
        pizzaCollectedSpriteOnGameOver.setScale(.5f);

        pizzaTextOnGameOver = new Text(0, pizzaTextY, mResourceManager.mFont3, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        pizzaTextOnGameOver.setText("0");
        mGameOverScene.attachChild(pizzaTextOnGameOver);


        playButton = new TiledSprite(playX, playY, mResourceManager.mPlayButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();

                    } else { //if the twitter button is visible on top of the play button
                        twitterButton.setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
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


        twitterButton = new TiledSprite(playX, playY, mResourceManager.mTweetButtonTextureRegion, mVertexBufferObjectManager);
        twitterButton.setCurrentTileIndex(0);
        twitterButton.setScale(0.75f);
        twitterButton.setVisible(false);
        mGameOverScene.attachChild(twitterButton);

        marketButton = new TiledSprite(marketX, marketY, mResourceManager.mMarketButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
                    } else {
                        facebookButton.setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(0);
                        mSceneManager.setScene(SceneManager.SceneType.SCENE_MARKET);
                        if(mResourceManager.mMariachiFast.isPlaying()) {
                            mResourceManager.mMariachiFast.pause();
                        }
                        if(mResourceManager.mMariachiSlow.isPlaying()) {
                            mResourceManager.mMariachiSlow.pause();
                        }
                    } else {
                        facebookButton.setCurrentTileIndex(0);
                        mActivity.openFacebookShare(score);
                    }
                }
                return true;
            }
        };
        marketButton.setCurrentTileIndex(0);
        marketButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(marketButton);
        mGameOverScene.attachChild(marketButton);

        facebookButton = new TiledSprite(marketX, marketY, mResourceManager.mFacebookButtonTextureRegion, mVertexBufferObjectManager);
        facebookButton.setCurrentTileIndex(0);
        facebookButton.setScale(0.75f);
        mGameOverScene.attachChild(facebookButton);
        facebookButton.setVisible(false);


        menuButton = new TiledSprite(menuX, menuY, mResourceManager.mMenuButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(0);
                        mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                        if(mResourceManager.mMariachiFast.isPlaying()) {
                            mResourceManager.mMariachiFast.pause();
                        }
                        if(mResourceManager.mMariachiSlow.isPlaying()) {
                            mResourceManager.mMariachiSlow.pause();
                        }
                    }
                }
                return true;
            }
        };
        menuButton.setCurrentTileIndex(0);
        menuButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(menuButton);
        mGameOverScene.attachChild(menuButton);


        leaderboardButton = new TiledSprite(leaderboardX, leaderboardY, mResourceManager.mLeaderboardButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
                    } else { //if the other button is visible on top of the rate button
                        otherButton.setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
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
                        mResourceManager.mButtonSound.play();
                    } else { //if the back button is visible on top of the share button
                        backButton.setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
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


        rateButton = new TiledSprite(rateX, rateY, mResourceManager.mRateButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(1);
                        mResourceManager.mButtonSound.play();
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    if(!mSharingVisible) {
                        setCurrentTileIndex(0);
                        mActivity.openRate();
                    }
                }
                return true;
            }
        };
        rateButton.setCurrentTileIndex(0);
        rateButton.setScale(0.75f);
        mGameOverScene.registerTouchArea(rateButton);
        mGameOverScene.attachChild(rateButton);


        mGameOverScene.setBackgroundEnabled(false);

        most = mActivity.getMaxScore();

        //create HUD for score
        HUD gameHUD = new HUD();
        // CREATE SCORE TEXT
        mHudTextScore = new Text(SCREEN_WIDTH/2, 50, mResourceManager.mFont5, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mHudTextScore.setText("0");
        mHudTextScore.setX((SCREEN_WIDTH - mHudTextScore.getWidth()) / 2);
        mHudTextScore.setVisible(false);
        gameHUD.attachChild(mHudTextScore);

        //"+2" popup for double points
        mPlusTwo = new TiledSprite((SCREEN_WIDTH/2 + mHudTextScore.getWidth() * 2), 50, mResourceManager.mPlusTwoTextureRegion, mVertexBufferObjectManager);
        mPlusTwo.setVisible(false);
        gameHUD.attachChild(mPlusTwo);

        mCrapMeter = new TiledSprite((SCREEN_WIDTH - mResourceManager.mMeter2TextureRegion.getWidth()) / 2, mResourceManager.mMeter2TextureRegion.getHeight(), mResourceManager.mMeter2TextureRegion, mVertexBufferObjectManager);
        mCrapMeter.setCurrentTileIndex(6);
        mCrapMeter.setScale(0.75f);
        mCrapMeter.setX((SCREEN_WIDTH - mCrapMeter.getWidth()) / 2);
        MAX_CRAP_METER_SIZE = mCrapMeter.getWidth();
        mCrapMeter.setVisible(false);
        gameHUD.attachChild(mCrapMeter);

        final float crapMeterBottom = mCrapMeter.getX() + (mCrapMeter.getHeight() - mCrapMeter.getHeightScaled()) / 2 + mCrapMeter.getHeightScaled();
        final float pizzaCollectedSpriteY = (crapMeterBottom + mHudTextScore.getY()) / 2 - mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2 + 5;
        final float pizzaCollectedTextY = (crapMeterBottom + mHudTextScore.getY()) / 2 - mResourceManager.mFont6.getLineHeight() / 2 + 5;

        pizzaCollectedSprite = new Sprite(0, pizzaCollectedSpriteY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        gameHUD.attachChild(pizzaCollectedSprite);
        pizzaCollectedSprite.setScale(.5f);
        pizzaCollectedSprite.setVisible(false);

        mHudTextPizza = new Text(0, pizzaCollectedTextY, mResourceManager.mFont6, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mHudTextPizza.setText("0");
        mHudTextPizza.setX((SCREEN_WIDTH - mHudTextPizza.getWidth()) / 2);
        mHudTextPizza.setVisible(false);
        gameHUD.attachChild(mHudTextPizza);

        pizzaCollectedSprite.setX((SCREEN_WIDTH - pizzaCollectedSprite.getWidth() - mHudTextPizza.getWidth()) / 2);
        mHudTextPizza.setX(pizzaCollectedSprite.getX() + pizzaCollectedSprite.getWidth());

        //for machine crap power up (thunder taco)
        mAnimatedCrapMeter = new AnimatedSprite((SCREEN_WIDTH - mResourceManager.mMeter2TextureRegion.getWidth()) / 2, mResourceManager.mMeter2TextureRegion.getHeight(), mResourceManager.mMeter2TextureRegion, mVertexBufferObjectManager);
        mAnimatedCrapMeter.setScale(0.75f);
        mAnimatedCrapMeter.setX((SCREEN_WIDTH - mAnimatedCrapMeter.getWidth()) / 2);
        mAnimatedCrapMeter.setVisible(false);
        gameHUD.attachChild(mAnimatedCrapMeter);

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
                    mResourceManager.mButtonSound.play();
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
                    mResourceManager.mButtonSound.play();
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
                        mResourceManager.mButtonSound.play();
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
        pauseButton.setY(mCrapMeter.getY() + (mCrapMeter.getHeight() - mCrapMeter.getHeightScaled()) / 2 - (pauseButton.getHeight() - pauseButton.getHeightScaled()) / 2 - (pauseButton.getHeightScaled() - mCrapMeter.getHeightScaled()) / 2);
        registerTouchArea(pauseButton);
        attachChild(pauseButton);
        pauseButton.setVisible(false);

        volumeButton = new TiledSprite(0, 0, mResourceManager.mVolumeButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (isVisible() && pSceneTouchEvent.isActionUp()) {
                    if(getCurrentTileIndex() == 0) {
                        setCurrentTileIndex(1);
                    } else {
                        setCurrentTileIndex(0);
                    }
                    mActivity.toggleSound();
                }
                return true;
            }
        };
        if(mActivity.getSoundOn()) {
            volumeButton.setCurrentTileIndex(0);
        } else {
            volumeButton.setCurrentTileIndex(1);
        }
        volumeButton.setScale(0.25f);
        volumeButton.setX(mCrapMeter.getX() + (mCrapMeter.getWidth() - mCrapMeter.getWidthScaled()) / 2 - (volumeButton.getWidth() - volumeButton.getWidthScaled()) / 2 - volumeButton.getWidthScaled());
        volumeButton.setY(mCrapMeter.getY() + (mCrapMeter.getHeight() - mCrapMeter.getHeightScaled()) / 2 - (volumeButton.getHeight() - volumeButton.getHeightScaled()) / 2 - (volumeButton.getHeightScaled() - mCrapMeter.getHeightScaled()) / 2);
        registerTouchArea(volumeButton);
        mPauseScene.registerTouchArea(volumeButton);
        mGameOverScene.registerTouchArea(volumeButton);
        mGameReadyScene.registerTouchArea(volumeButton);
        attachChild(volumeButton);


        mCountdownScene = new CameraScene(mCamera);
        mCountdownScene.setBackgroundEnabled(false);

        final float totalPizzaY = overY;
        final float totalPizzaX = overX;
        final float totalPizzaTextX = overX + mResourceManager.mCollectablePizzaTextureRegion.getWidth();
        final float totalPizzaTextY = (overY * 2 + mResourceManager.mCollectablePizzaTextureRegion.getHeight()) / 2 - mResourceManager.mFont3.getLineHeight() / 2;
        final float countDownX = (SCREEN_WIDTH - mResourceManager.mCountdownTextureRegion.getWidth()) / 2;
        final float countDownY = overY - mResourceManager.mCountdownTextureRegion.getHeight();
        final float yesButtonX = overX + mResourceManager.mBoardTextureRegion.getWidth() - mResourceManager.mPlayButtonTextureRegion.getWidth() * 0.85f;
        final float yesButtonY = overY + mResourceManager.mBoardTextureRegion.getHeight() - mResourceManager.mPlayButtonTextureRegion.getHeight() * 0.85f;
        final float noButtonX = overX - mResourceManager.mPlayButtonTextureRegion.getWidth() * 0.15f;
        final float noButtonY = yesButtonY;

        mCountdownSprite = new AnimatedSprite(countDownX, countDownY, mResourceManager.mCountdownTextureRegion, mVertexBufferObjectManager);
        mCountdownSprite.setCurrentTileIndex(0);
        mCountdownScene.attachChild(mCountdownSprite);

        final Sprite popUpBoard = new Sprite(overX, overY, mResourceManager.mBoardTextureRegion, mVertexBufferObjectManager);
        mCountdownScene.attachChild(popUpBoard);

        final Sprite totalPizzaSprite = new Sprite(totalPizzaX, totalPizzaY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mCountdownScene.attachChild(totalPizzaSprite);
        totalPizzaSprite.setScale(.5f);

        totalPizzaTextOnCountdownScene = (new Text(totalPizzaTextX, totalPizzaTextY, mResourceManager.mFont3, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        mCountdownScene.attachChild(totalPizzaTextOnCountdownScene);


        final TiledSprite yesRespawnButton = new TiledSprite(yesButtonX, yesButtonY, mResourceManager.mYesButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

                    if (pSceneTouchEvent.isActionDown()) {
                        if (pizzaCollected + mActivity.getPizza() >= RESPAWN_PRICE) {
                            setCurrentTileIndex(1);
                            mResourceManager.mButtonSound.play();
                        } else if(!mRespawnButtonPressed){
                            mActivity.alert("Not enough pizza! Get more in the market.");
                            mRespawnButtonPressed = true;
                            mResourceManager.mButtonSound.play();
                        }

                    }
                    if (pSceneTouchEvent.isActionUp()) {
                        if (pizzaCollected + mActivity.getPizza() >= RESPAWN_PRICE) {
                            respawn();
                        } else if(mRespawnButtonPressed){
                            mRespawnButtonPressed = false;
                        }

                    }
                return true;
            }
        };
        yesRespawnButton.setCurrentTileIndex(0);
        yesRespawnButton.setScale(0.5f);
        mCountdownScene.registerTouchArea(yesRespawnButton);
        mCountdownScene.attachChild(yesRespawnButton);

        final TiledSprite noRespawnButton = new TiledSprite(noButtonX, noButtonY, mResourceManager.mNoButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {

                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);
                    mResourceManager.mButtonSound.play();
                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    setCountDown(false);
                    displayScore();
                }
                return true;
            }
        };
        noRespawnButton.setCurrentTileIndex(0);
        noRespawnButton.setScale(0.5f);
        mCountdownScene.registerTouchArea(noRespawnButton);
        mCountdownScene.attachChild(noRespawnButton);



        final float respawnTextY = overY + mResourceManager.mFont2.getLineHeight();
        final float respawnPricePizzaSpriteY = respawnTextY + mResourceManager.mFont2.getLineHeight();
        final float respawnPriceY = respawnPricePizzaSpriteY + mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2 - mResourceManager.mFont3.getLineHeight() / 2;

        final Text respawnText = (new Text(0, respawnTextY, mResourceManager.mFont2, "Continue?", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        mCountdownScene.attachChild(respawnText);

        final Sprite respawnPricePizzaSprite = new Sprite(0, respawnPricePizzaSpriteY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mCountdownScene.attachChild(respawnPricePizzaSprite);
        respawnPricePizzaSprite.setScale(.5f);

        final Text respawnPriceText = (new Text(0, respawnPriceY, mResourceManager.mFont3, String.valueOf(RESPAWN_PRICE), new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        mCountdownScene.attachChild(respawnPriceText);

        respawnText.setX((SCREEN_WIDTH - respawnText.getWidth()) / 2);
        respawnPricePizzaSprite.setX((SCREEN_WIDTH - respawnPricePizzaSprite.getWidth() - respawnPriceText.getWidth()) / 2); //adjust margins
        respawnPriceText.setX(respawnPricePizzaSprite.getX() + respawnPricePizzaSprite.getWidth());

    }


    private void setSharingVisible(boolean bool) {
        if (bool) {
            mSharingVisible = true;

            playButton.setVisible(false);
            shareButton.setVisible(false);
            rateButton.setVisible(false);
            leaderboardButton.setVisible(false);
            menuButton.setVisible(false);
            marketButton.setVisible(false);

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
            leaderboardButton.setVisible(true);
            menuButton.setVisible(true);
            marketButton.setVisible(true);

        }
    }

    private void setCountDown(boolean bool) {
        if(bool) {
            mCountdownOnScreen = true;
            setChildScene(mCountdownScene);
            totalPizzaTextOnCountdownScene.setText(Integer.toString(mActivity.getPizza() + pizzaCollected));
            mCountdownSprite.animate(1000);
            pauseButton.setVisible(false);

        } else {
            mGameOver = false;
            mCountdownOnScreen = false;
            clearChildScene();
            mCountdownSprite.stopAnimation(0);
            pauseButton.setVisible(true);

        }
    }

    private void respawn() {
        mRespawnUsed = true;
        setCountDown(false);
        int pizzaToSubtract = RESPAWN_PRICE;
        if(pizzaCollected <= pizzaToSubtract) {
            pizzaToSubtract -= pizzaCollected;
            pizzaCollected = 0;
            mActivity.subtractPizza(pizzaToSubtract);
        } else {
            pizzaCollected -= pizzaToSubtract;
        }
        mHudTextPizza.setText(String.valueOf(pizzaCollected));
        //update margins
        pizzaCollectedSprite.setX((SCREEN_WIDTH - pizzaCollectedSprite.getWidth() - mHudTextPizza.getWidth()) / 2);
        mHudTextPizza.setX(pizzaCollectedSprite.getX() + pizzaCollectedSprite.getWidth());


        if(mCraps.size() > 0) {
            for(int i = mCraps.size() - 1; i >= 0; i--) {
                mCraps.get(i).moveForRespawn();
            }
        }

        if(mObstacles.size() > 0) {
            for (int i = mObstacles.size() - 1; i >= 0; i--) {
                Obstacle obstacle = mObstacles.get(i);
                detachChild(obstacle);

                if(obstacle.getObstacleType() == Obstacle.obstacleType.PLANE || obstacle.getObstacleType() == Obstacle.obstacleType.BALLOON) {
                    mObstaclePoolAir.recyclePoolItem(obstacle);
                    mObstaclePoolAir.shufflePoolItems();
                } else {
                    mObstaclePoolGround.recyclePoolItem(obstacle);
                    mObstaclePoolGround.shufflePoolItems();
                }

                mObstacles.remove(i);
            }
        }

        if(mTargets.size() > 0) {
            for (int i = mTargets.size() - 1; i >= 0; i--) {
                detachChild(mTargets.get(i));
                mTargetPool.recyclePoolItem(mTargets.get(i));
                mTargets.remove(i);
            }
        }
        if(mCollectables.size() > 0) {
            for (int i = mCollectables.size() - 1; i >= 0; i--) {
                detachChild(mCollectables.get(i));
                mCollectablePool.recyclePoolItem(mCollectables.get(i));
                mCollectables.remove(i);
            }
        }

        setGameOver(false);

        if(!mHyperSpeedActivated) {
            setInvincibility(true);

            mBird.setAccelerationY(GRAVITY);

            jumpBird();
            jumpBird();
            jumpBird();
            jumpBird();
            jumpBird();

        } else {
            mBird.setAccelerationY(HYPER_SPEED_Y_ACCELERATION);

            mBird.setVelocityY(-10f * 30);
        }

        mResourceManager.mRespawnSound.play();

    }


    public void setPause(boolean bool) {
        if (bool) {
            if(pauseButton.isVisible()) {
                pauseButton.setVisible(false);
                setIgnoreUpdate(true);
                setChildScene(mPauseScene, false, true, true);

                mMachineCrapping = false;

                mResourceManager.mMusic.pause();
                mResourceManager.mMariachiFast.pause();
                mResourceManager.mMariachiSlow.pause();

                if(numPlanesOnScreen > 0) {
                    mResourceManager.mPropellerSound.pause();
                }
                if(mMotherShipOnScreen) {
                    mResourceManager.mMotherShipSound.pause();
                }

            }
        } else {
            clearChildScene();
            setIgnoreUpdate(false);
            pauseButton.setVisible(true);

            if (mMachineCrapActivated) {
                if(mSlowMotionActivated) {
                    mResourceManager.mMariachiSlow.resume();
                } else {
                    mResourceManager.mMariachiFast.resume();
                }
            } else {
                mResourceManager.mMusic.resume();
            }

            if(numPlanesOnScreen > 0) {
                mResourceManager.mPropellerSound.resume();
            }
            if(mMotherShipOnScreen) {
                mResourceManager.mMotherShipSound.resume();
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
            } else if (target.getTargetType() == Target.targetType.PERSON1) {
                if(((TargetPerson1)target).getFalling() && (target.getY() + target.getHeight()) >= mGround.getY()) {
                    ((TargetPerson1)target).hitsGround(mGameOver);
                }
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
                    addPoint();
                }

            }

    }

    /**
     * this method checks for mothership contact with the bird and targets.
     * if contact with bird, gameover (unless machine crapping)
     */
    private void checkForMotherShipContact() {
        if (!mGameOver && mMotherShip.collidesWith(mBird) && !mMotherShip.getCollidedWith()) {
            if (mMachineCrapActivated || mHyperSpeedActivated || mInvincibilityActivated) {

                float birdYVelocity = mBird.getVelocityY();
                float velocityY = birdYVelocity * 2;
                if (velocityY != 0 && Math.abs(velocityY) < 500) {
                    velocityY = (velocityY / Math.abs(velocityY)) * 500;
                }
                mMotherShip.blastOff(velocityY);
                mMotherShip.setCollidedWith();
            } else {
                setGameOver();
            }
        }
    }

    private void setGameOver() {

            setGameOver(true);

    }

    private void setGameOver(boolean bool) {
        if(bool) {
            mGameOver = true;
            mResourceManager.mHitSound.play();
            mBird.stopAnimation(selectedBird * 3);
            mMachineCrapping = false; //stop current hold session
            stopCrap();

            if(mInvincibilityActivated) {
                setInvincibility(false);
            }

            killObstacles();
            killCollectables();
            killTargets();

            mAutoParallaxBackground.setParallaxChangePerSecond(0);

        } else {
            mGameOver = false;
            mBird.animate(selectedBird);

            addObstacleGround();
            mTargets.add(mTargetPool.obtainPoolItem());
            attachChild(mTargets.get(0));
            mCollectables.add(mCollectablePool.obtainPoolItem());
            attachChild(mCollectables.get(0));



            if(mSlowMotionActivated) {
                mAutoParallaxBackground.setParallaxChangePerSecond(PARALLAX_CHANGE_PER_SECOND / 2);
                mTargets.get(0).setSlowMotion(true);
            } else {
                mAutoParallaxBackground.setParallaxChangePerSecond(PARALLAX_CHANGE_PER_SECOND);
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
            mResourceManager.mAlertSound.play();

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
            mResourceManager.mMotherShipSound.play();
            if(mSlowMotionActivated) {
                mMotherShip.setSlowMotion(true);
                mResourceManager.mMotherShipSound.setRate(0.5f);
            }
            if (mHyperSpeedActivated) {
                mMotherShip.setHyperSpeed(true);
            }
        } else {
            mMotherShipOnScreen = false;
            mMotherShipPassed = true;
            mMotherShip.flyingPastScreen(false);
            detachChild(mMotherShip);
        }
    }


    private int getObstacleIndex() {
        return mObstaclePoolAir.getObstacleIndex() + mObstaclePoolGround.getObstacleIndex();
    }


    private void addObstacleAir() {

        obstacleAirSpawnCounter = 0;

        mObstacles.add(mObstaclePoolAir.obtainPoolItem());
        Obstacle newObstacle = mObstacles.get(mObstacles.size() - 1);
        newObstacle.setX(SCREEN_WIDTH);
        //scale plane velocity according to number of passed obstacles
        if (newObstacle.getObstacleType() == Obstacle.obstacleType.PLANE) {
            mResourceManager.mPropellerSound.play();
            mResourceManager.mPropellerSound.setVolume(1f);
            newObstacle.setVelocityX(-getPlaneSpeed());

            //handle sound effect
            if (getObstacleIndex() < 50) {
                mResourceManager.mPropellerSound.setRate((float) (1 + getObstacleIndex() / 100.0));
            } else {
                mResourceManager.mPropellerSound.setRate(1.5f);
            }

            numPlanesOnScreen++;

            //if passed obstacle number x, have plane fly at random angles across the screen
            if (getObstacleIndex() > 50) {
                ((ObstaclePlane) newObstacle).randomizeYVelocity();
            }
        } else if (newObstacle.getObstacleType() == Obstacle.obstacleType.BALLOON) {
            if (getObstacleIndex() > 0) {
                ((ObstacleBalloon) newObstacle).randomizeYVelocity();
            }
        }
        if(mSlowMotionActivated) {
            newObstacle.setSlowMotion(true);
        }
        if(mHyperSpeedActivated) {
            newObstacle.setHyperSpeed(true);
        }
        attachChild(newObstacle);
        sortChildren();
    }

    private float getPlaneSpeed() {
        if (getObstacleIndex() < 50) {
            return 200f + getObstacleIndex();
        } else {
            return 250;
        }
    }

    private void addObstacleGround() {

        obstacleGroundSpawnCounter = 0;

        mObstacles.add(mObstaclePoolGround.obtainPoolItem());
        Obstacle newObstacle = mObstacles.get(mObstacles.size() - 1);
        newObstacle.setX(SCREEN_WIDTH);

        if(mSlowMotionActivated) {
            newObstacle.setSlowMotion(true);
        }
        if(mHyperSpeedActivated) {
            newObstacle.setHyperSpeed(true);
        }
        attachChild(newObstacle);
        sortChildren();
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
                        if(mSlowMotionActivated) {
                            newTarget.setSlowMotion(true);
                        }
                        if(mHyperSpeedActivated) {
                            newTarget.setHyperSpeed(true);
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
                float xVelocity = target.getVelocityX();
                if(mSlowMotionActivated) {
                    target.setVelocity(xVelocity + SCROLL_SPEED / 2, 0);
                } else {
                    target.setVelocity(xVelocity + SCROLL_SPEED, 0);
                }
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
            if(obstacle.getObstacleType() == Obstacle.obstacleType.PLANE) {
                if(obstacle.getVelocityX() < -(SCROLL_SPEED + MIN_PLANE_SPEED_ON_GAMEOVER)) {
                    obstacle.setVelocityX(obstacle.getVelocityX() + SCROLL_SPEED); //keep vertical velocity //plane keeps flying just at a lower velocity because scrolling stops
                } else {
                    obstacle.setVelocityX(-MIN_PLANE_SPEED_ON_GAMEOVER);
                }
                if(!mSlowMotionActivated) {
                    mResourceManager.mPropellerSound.setRate(mResourceManager.mPropellerSound.getRate() / 2);
                }
            } else if (obstacle.getObstacleType() == Obstacle.obstacleType.BALLOON) {
                obstacle.setVelocityX(0);
            } else {
                //obstacle.die();
                obstacle.setVelocityX(0);
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
                //collectable.die();

        }
    }


    /**
     * This method check all of the obstacles for contact with bird. If machine crap, blasts off. If balloon basket, drops target.
     * Also checks for obstacles leaving screen.
     * Also checks for passing obstacles to give points.
     */
    private void checkObstaclePosition() {
        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if (!mGameOver && obstacle.collidesWith(mBird) && !obstacle.getCollidedWith()) {
                if(mMachineCrapActivated || mHyperSpeedActivated || mInvincibilityActivated) {

                    float birdYVelocity = mBird.getVelocityY(); //convert from m/s to px/sec
                    float velocityY = birdYVelocity * 2;
                    if (velocityY != 0 && Math.abs(velocityY) < 500) {
                        velocityY = (velocityY / Math.abs(velocityY)) * 500;
                    }
                    obstacle.blastOff(velocityY);
                    obstacle.setCollidedWith();


                } else {

                    setGameOver();
                }
                return;
            } else if //if this is a hot air balloon and the bird hits the basket... knock the person out
                    (!mGameOver &&  obstacle.getObstacleType() == Obstacle.obstacleType.BALLOON &&
                            ((ObstacleBalloon)obstacle).collidesWithBasket(mBird) && !((ObstacleBalloon) obstacle).getBasketHit()) {
                ((ObstacleBalloon) obstacle).birdHitsBasket();

                mTargets.add(mTargetPool.obtainPoolItem());

                ((TargetPerson1)(mTargets.get(mTargets.size() - 1))).setFalling(obstacle.getVelocityX(), obstacle.getX() + 28, obstacle.getY() + 110);
                attachChild(mTargets.get(mTargets.size() - 1));

                mResourceManager.mWilhelmScreamSound.play();

                if(mSlowMotionActivated) {
                    mTargets.get(mTargets.size() - 1).setSlowMotion(true);
                    mResourceManager.mWilhelmScreamSound.setRate(0.5f);
                } else {
                    mResourceManager.mWilhelmScreamSound.setRate(1);
                }

                if(mHyperSpeedActivated) {
                    mTargets.get(mTargets.size() - 1).setHyperSpeed(true);
                }
                sortChildren();

                addPoint();

            } else if (!mGameOver && !obstacle.getScoreAdded() && mBird.getX() > (obstacle.getX() + obstacle.getWidth())) {
                obstacle.setScoreAdded();

                addPoint();
            } else if (obstacle.getX() < -obstacle.getWidth() || obstacle.getY() < -obstacle.getHeight() || obstacle.getY() > SCREEN_HEIGHT) {
                detachChild(obstacle);
                if (obstacle.getObstacleType() == Obstacle.obstacleType.PLANE) {
                    numPlanesOnScreen--;
                }
                if(obstacle.getObstacleType() == Obstacle.obstacleType.PLANE || obstacle.getObstacleType() == Obstacle.obstacleType.BALLOON) {
                    mObstaclePoolAir.recyclePoolItem(obstacle);
                    mObstaclePoolAir.shufflePoolItems();
                } else {
                    mObstaclePoolGround.recyclePoolItem(obstacle);
                    mObstaclePoolGround.shufflePoolItems();
                }
                mObstacles.remove(i);
            }


        }
    }

    private void checkCollectablePosition() {
        for(int i = mCollectables.size() - 1; i >= 0; i--) {
            Collectable collectable = mCollectables.get(i);
            if (collectable.getX() <  -collectable.getWidth()) {
                detachChild(collectable);
                mCollectablePool.recyclePoolItem(collectable);
                mCollectablePool.shufflePoolItems();
                mCollectables.remove(i);
            } else if (!mGameOver && collectable.collidesWith(mBird) && !collectable.getCollected()) {
                collectable.setVisible(false);
                collectable.collect();
                if(collectable.getCollectableType() == Collectable.collectableType.TACO && !mHyperSpeedActivated) {
                    setMachineCrap(true);
                } else if(collectable.getCollectableType() == Collectable.collectableType.HAM) {
                    setDoublePoints(true);
                } else if(collectable.getCollectableType() == Collectable.collectableType.MELON && !mHyperSpeedActivated) {
                    setMegaCrap(true);
                } else if(collectable.getCollectableType() == Collectable.collectableType.MUFFIN && !mHyperSpeedActivated) {
                    setSlowMotion(true);
                } else if(collectable.getCollectableType() == Collectable.collectableType.BURGER) {
                    setHyperSpeed(true);
                } else if(collectable.getCollectableType() == Collectable.collectableType.PIZZA) {
                    pizzaCollected++;
                    mHudTextPizza.setText(String.valueOf(pizzaCollected));
                    //update margins
                    pizzaCollectedSprite.setX((SCREEN_WIDTH - pizzaCollectedSprite.getWidth() - mHudTextPizza.getWidth()) / 2);
                    mHudTextPizza.setX(pizzaCollectedSprite.getX() + pizzaCollectedSprite.getWidth());
                }
                growCrapMeter(MAX_CRAP_METER_SIZE); //fill crap meter

                mResourceManager.mCollectionSound.play();
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
     * Check if crap has left screen. If so, recycle.
     * Also check if crap hits ground.
     * @param ground ground object to check for contact with
     */
    private void checkCrapPosition(IShape ground) {
        for (int i = mCraps.size() - 1; i >= 0; i--) {
            Crap crap = mCraps.get(i);
            if(crap.getX() < -crap.getWidth()) {
                detachChild(crap);
                if(crap.getCrapType() == Crap.crapType.MEGA) {
                    mMegaCrapPool.recyclePoolItem(crap);
                } else {
                    mCrapPool.recyclePoolItem(crap);
                }
                mCraps.remove(i);
                continue;
            } else if (crap.getFalling() && crap.getY() + crap.getHeight() > ground.getY()) {
                crap.hitsGround(mGameOver, selectedBird);
            }
            if(crap.getFalling() && !mGameOver) {
                boolean crapContactWithTarget = false;
                for(int j = mTargets.size() - 1; j >= 0; j--) {
                    Target target = mTargets.get(j);

                    //if target hits crap, target is not hit yet, and the target isn't a person FALLING from a balloon...
                    if(target.collidesWith(crap) && !target.getHitValue() && !(target.getTargetType() == Target.targetType.PERSON1 && ((TargetPerson1)target).getFalling())) {
                        target.hitByCrap(mGameOver); //turn target to gravestone. sets velocity to scroll (unless gameover, then 0)

                        if(mHyperSpeedActivated) {
                            target.setHyperSpeed(true);
                        }

                        crapContactWithTarget = true; //boolean allows one crap to take out multiple targets

                        addPoint();

                    }
                }
                if(crapContactWithTarget && crap.getCrapType() == Crap.crapType.NORMAL) {
                    detachChild(mCraps.get(i)); //now we remove the crap
                    mCrapPool.recyclePoolItem(mCraps.get(i));
                    mCraps.remove(i);
                }
                if(crap.getCrapType() == Crap.crapType.MEGA) {
                    for(int j = mObstacles.size() - 1; j >= 0; j--) {
                        Obstacle obstacle = mObstacles.get(j);
                        if(obstacle.collidesWith(crap) && !obstacle.getHitByMegaCrap()) {
                            obstacle.hitWithMegaCrap((MegaCrap) crap);
                            obstacle.setScoreAdded();


                            addPoint(); //add one point for "passing" the obstacle
                            addPoint(); //and another for knocking it off the screen with mega crap
                        }
                    }
                }
            }

        }
    }


    private void addPoint() {
        mResourceManager.mCoinSound.play();
        mResourceManager.mCoinSound.setVolume(0.1f);
        if(mSlowMotionActivated) {
            mResourceManager.mCoinSound.setRate(0.5f);
        } else {
            mResourceManager.mCoinSound.setRate(1);
        }

        if(mDoublePointsActivated) {
            addDoublePoints();
        } else {
            score++;
        }

        mHudTextScore.setText(String.valueOf(score));
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

            setInvincibility(false);

            mMachineCrapActivated = true;
            mAnimatedCrapMeter.setVisible(true);
            mAnimatedCrapMeter.animate(new long[]{100, 100, 100, 100, 100, 100}, 0, 5, true);
            mCrapMeter.setVisible(false);
            machineCrappingLastCrapTime = 0;
            machineCrappingTime = 0;

            mAnimatedCrapMeterBlinkingFast = false;
            mAnimatedCrapMeterBlinkingSlow = false;

            mResourceManager.mMusic.pause();
            if(mSlowMotionActivated) {
                mResourceManager.mMariachiSlow.resume();
            } else {
                mResourceManager.mMariachiFast.resume();
            }

        } else {
            mMachineCrapActivated = false;

            setInvincibility(true);

            mResourceManager.mMariachiFast.pause();
            mResourceManager.mMariachiSlow.pause();
            mResourceManager.mMusic.resume();

        }
    }

    /**
     * for turning on and off the hyper speed power up
     * @param bool
     */
    private void setHyperSpeed(boolean bool) {
        if(bool) {

            if(mSlowMotionActivated) {
                setSlowMotion(false);
            }
            if(mMachineCrapActivated) {
                setMachineCrap(false);
            }
            if(mMegaCrapActivated) {
                setMegaCrap(false);
            }
            if(mInvincibilityActivated) {
                setInvincibility(false);
            }

            mHyperSpeedActivated = true;
            mAnimatedCrapMeter.setVisible(true);
            mAnimatedCrapMeter.animate(new long[]{100, 100, 100, 100, 100, 100}, 0, 5, true);
            mCrapMeter.setVisible(false);
            lastHyperSpeedCrapTime = 0;
            hyperSpeedTime = 0;

            mAnimatedCrapMeterBlinkingFast = false;
            mAnimatedCrapMeterBlinkingSlow = false;

            mBird.setVelocityY(0);
            mBird.setAccelerationY(HYPER_SPEED_Y_ACCELERATION);


            for (int i = 0; i < mObstacles.size(); i++) {
                mObstacles.get(i).setHyperSpeed(true);
            }
            for(int i = 0; i < mTargets.size(); i++) {
                mTargets.get(i).setHyperSpeed(true);
            }
            if(mMotherShipOnScreen) {
                mMotherShip.setHyperSpeed(true);
            }

        } else {

            mHyperSpeedActivated = false;

            setInvincibility(true);

            mBird.setAccelerationY(GRAVITY);

            for (int i = 0; i < mObstacles.size(); i++) {
                mObstacles.get(i).setHyperSpeed(false);
            }
            for(int i = 0; i < mTargets.size(); i++) {
                mTargets.get(i).setHyperSpeed(false);
            }
        }
    }

    private void setInvincibility(boolean bool) {
        if(bool) {
            mInvincibilityActivated = true;
            invincibilityTime = 0;
            mAnimatedCrapMeter.setVisible(true);
            mCrapMeter.setVisible(false);

            mAnimatedCrapMeter.animate(new long[]{400, 400}, 5, 6, true);
            mAnimatedCrapMeterBlinkingSlow = true;

        } else {
            mInvincibilityActivated = false;

            mAnimatedCrapMeter.setVisible(false);
            mCrapMeter.setVisible(true);
            growCrapMeter(MAX_CRAP_METER_SIZE);

            mAnimatedCrapMeterBlinkingSlow = false;
            mAnimatedCrapMeterBlinkingFast = false;
        }
    }


    /**
     * for turning on and off the machine crap power up
     * @param bool
     */
    private void setMegaCrap(boolean bool) {
        if (bool) {
            mMegaCrapActivated = true;
            megaCrappingTime = 0;
        } else {
            mMegaCrapActivated = false;
        }
    }

    private void setSlowMotion(boolean bool) {
        if(bool) {
            slowMotionTime = 0;
            if(!mSlowMotionActivated) {
                mSlowMotionActivated = true;
                for (int i = 0; i < mObstacles.size(); i++) {
                    if (!mObstacles.get(i).getHitByMegaCrap()) {
                        mObstacles.get(i).setSlowMotion(true);
                    }
                }
                for(int i = 0; i < mTargets.size(); i++) {
                    mTargets.get(i).setSlowMotion(true);
                    if(((TargetPerson1)mTargets.get(i)).getFalling()) {
                        mResourceManager.mWilhelmScreamSound.setRate(0.5f);
                    }
                }
                for(int i = 0; i < mCraps.size(); i++) {
                    mCraps.get(i).setSlowMotion(true);
                }
                if(mMotherShipOnScreen) {
                    mMotherShip.setSlowMotion(true);
                    mResourceManager.mMotherShipSound.setRate(0.5f);
                }
                mAutoParallaxBackground.setParallaxChangePerSecond(PARALLAX_CHANGE_PER_SECOND / 2);

                if(mMachineCrapActivated) {
                    mResourceManager.mMariachiFast.pause();
                    mResourceManager.mMariachiSlow.resume();
                }
                if (numPlanesOnScreen > 0) { //handle propeller sound slow down
                    mResourceManager.mPropellerSound.setRate(mResourceManager.mPropellerSound.getRate() / 2);
                }
            }

        } else {
            mSlowMotionActivated = false;
            for (int i = 0; i < mObstacles.size(); i++) {
                if (!mObstacles.get(i).getHitByMegaCrap()) {
                    mObstacles.get(i).setSlowMotion(false);
                }
            }
            for(int i = 0; i < mTargets.size(); i++) {
                mTargets.get(i).setSlowMotion(false);
                if(((TargetPerson1)mTargets.get(i)).getFalling()) {
                    mResourceManager.mWilhelmScreamSound.setRate(1);
                }
            }
            for(int i = 0; i < mCraps.size(); i++) {
                mCraps.get(i).setSlowMotion(false);
            }
            if(mMotherShipOnScreen) {
                mMotherShip.setSlowMotion(false);
                mResourceManager.mMotherShipSound.setRate(1f);

            }
            mAutoParallaxBackground.setParallaxChangePerSecond(PARALLAX_CHANGE_PER_SECOND);
            if(mMachineCrapActivated) {
                mResourceManager.mMariachiSlow.pause();
                mResourceManager.mMariachiFast.resume();
            }
            if (numPlanesOnScreen > 0) { //handle propeller sound speed up back to normal
                mResourceManager.mPropellerSound.setRate(mResourceManager.mPropellerSound.getRate() * 2);
            }
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
        if(mMegaCrapActivated) {
            mCraps.add(mMegaCrapPool.obtainPoolItem());
        } else {
            mCraps.add(mCrapPool.obtainPoolItem());
        }

        Crap crap = mCraps.get(mCraps.size() - 1);

        crap.setPosition(currentXPosition, currentYPosition + (mBird.getHeight()));

        attachChild(crap);

        crap.setCurrentTileIndex(selectedBird * 2);

        if(getObstacleIndex() < CRAP_METER_SHRINK_STOP_OBSTACLE_INDEX) {
            shrinkCrapMeter((float) Math.sqrt((double) getObstacleIndex() * 2));
        } else {
            shrinkCrapMeter((float) Math.sqrt((double) CRAP_METER_SHRINK_STOP_OBSTACLE_INDEX * 2));
        }

        if(selectedBird == 6) { //if ninja bird, spin throwing star crap
            crap.setAngularVelocity(600);
        }

        if(mSlowMotionActivated) {
            crap.setSlowMotion(true);
        }

        if(mMegaCrapActivated) {
            mResourceManager.mMegaCrapSound.play();
            mResourceManager.mMegaCrapSound.setVolume(2);
            if(mSlowMotionActivated) {
                mResourceManager.mMegaCrapSound.setRate(0.5f);
            } else {
                mResourceManager.mMegaCrapSound.setRate(1);
            }
        } else {
            mResourceManager.mJumpSound.play();
            mResourceManager.mJumpSound.setVolume(0.75f);
            if (mSlowMotionActivated) {
                mResourceManager.mJumpSound.setRate(0.5f);
            } else {
                mResourceManager.mJumpSound.setRate(1);
            }
        }

    }

    private void dropHyperSpeedCrap(float currentXPosition, float currentYPosition) {

        if(mMegaCrapActivated) {
            mCraps.add(mMegaCrapPool.obtainPoolItem());
        } else {
            mCraps.add(mCrapPool.obtainPoolItem());
        }

        Crap crap = mCraps.get(mCraps.size() - 1);

        crap.setPosition(currentXPosition, currentYPosition + (mBird.getHeight() - crap.getHeight()) / 2);

        attachChild(crap);

        crap.setHyperSpeed(mBird.getVelocityY());

        crap.setCurrentTileIndex(selectedBird * 2);

        if(selectedBird == 6) { //if ninja bird, spin throwing star crap
            crap.setAngularVelocity(600);
        }

        if(mMegaCrapActivated) {
            mResourceManager.mMegaCrapSound.play();
            mResourceManager.mMegaCrapSound.setVolume(2);
            mResourceManager.mMegaCrapSound.setRate(1);

        } else {
            mResourceManager.mJumpSound.play();
            mResourceManager.mJumpSound.setVolume(0.75f);
            mResourceManager.mJumpSound.setRate(1);

        }

    }


    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

        if(pSceneTouchEvent.isActionDown()) {
            if(!mGameOver) {
                if (!mOutOfCrap && !mMachineCrapActivated && !mHyperSpeedActivated) {
                    jumpBird();
                    return true;
                } else if (mMachineCrapActivated) {
                    mMachineCrapping = true;
                    machineCrappingLastCrapTime = 0;
                } else if (mHyperSpeedActivated) {
                    mBird.setAccelerationY(-HYPER_SPEED_Y_ACCELERATION);
                }
            }
        }
        if(pSceneTouchEvent.isActionUp()) {
            if(mMachineCrapping) {
                mMachineCrapping = false;
            } else if (mHyperSpeedActivated) {
                mBird.setAccelerationY(HYPER_SPEED_Y_ACCELERATION);
            }
        }


        if(!mGameOver && !mOutOfCrap && pSceneTouchEvent.isActionDown() && !mMachineCrapActivated && !mHyperSpeedActivated) {
                jumpBird();
                return true;
            } else if(!mGameOver && mMachineCrapActivated && pSceneTouchEvent.isActionDown()) {
                mMachineCrapping = true;
                machineCrappingLastCrapTime = 0;
            } else if(mMachineCrapping && pSceneTouchEvent.isActionUp()) {
                mMachineCrapping = false;
            }

        return false;
    }


    private void jumpBird() {
        mBird.setRotation(-15);
        float currentYPosition = mBird.getY();
        float currentXPosition = mBird.getX();

        mBird.jump(mMegaCrapActivated);

        dropCrap(currentXPosition, currentYPosition);

    }


    private void displayScore() {

        if (score > most) {
            most = score;
            mActivity.setMaxScore(most);
        }

        scoreText.setText(String.valueOf(score)); //update values
        mostText.setText(String.valueOf(most));

        scoreText.setX(scoreText.getX() - (scoreText.getWidth() / 2)); //adjust margins
        mostText.setX(mostText.getX() - (mostText.getWidth() / 2));

        mActivity.addPizza(pizzaCollected);

        //update user's account highscore if logged in
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            if(mActivity.isNetworkAvailable()) {
                mActivity.displayDailyReward(); //give the player their daily reward if they haven't gotten it yet.
                currentUser.put("pizzaCollected", mActivity.getPizza());
                mActivity.setPizzaSinceOffline(0);
            } else {
                mActivity.addPizzaSinceOffline(pizzaCollected);
            }
            if(currentUser.getInt("highScore") < most) {
                currentUser.put("highScore", most);
            }

            mActivity.saveCurrentUser();
        }



        //hide score and crapMeter
        mHudTextScore.setVisible(false);
        mHudTextPizza.setVisible(false);
        pizzaCollectedSprite.setVisible(false);
        mCrapMeter.setVisible(false);
        mAnimatedCrapMeter.setVisible(false);
        mPlusTwo.setVisible(false);
        mAlertSign.setVisible(false);
        pauseButton.setVisible(false);


        //Display pizza collected
        pizzaTextOnGameOver.setText(String.valueOf(mActivity.getPizza()));

        pizzaCollectedSpriteOnGameOver.setX((SCREEN_WIDTH - pizzaCollectedSpriteOnGameOver.getWidth() - pizzaTextOnGameOver.getWidth()) / 2); //adjust margins
        pizzaTextOnGameOver.setX(pizzaCollectedSpriteOnGameOver.getX() + pizzaCollectedSpriteOnGameOver.getWidth());

        setChildScene(mGameOverScene, false, true, true);

    }

    /**
     * updates the value of pizza collected and max score on gameover screen and adjusts margins
     */
    public void updateGameOverScreenAfterSync() {
        Log.i("updating gameover", "Pizza = " + String.valueOf(mActivity.getPizza()) + " & Most = " + mActivity.getMaxScore());

        mostText.setText(String.valueOf(mActivity.getMaxScore()));

        mostText.setX(mostX - mostText.getWidth() / 2);

        pizzaTextOnGameOver.setText(String.valueOf(mActivity.getPizza()));
        pizzaCollectedSpriteOnGameOver.setX((SCREEN_WIDTH - pizzaCollectedSpriteOnGameOver.getWidth() - pizzaTextOnGameOver.getWidth()) / 2); //adjust margins
        pizzaTextOnGameOver.setX(pizzaCollectedSpriteOnGameOver.getX() + pizzaCollectedSpriteOnGameOver.getWidth());

    }

    private void checkForBirdGroundContact() {
        if(mBird.getY() + mBird.getHeight() > mGround.getY()) {

            mBird.setVelocityY(0);
            mBird.setAcceleration(0,0);
            mBird.setY(mGround.getY() - mBird.getHeight() - 5);

            if(!mGameOver) { //if the bird hasn't already hit an obstacle
                setGameOver();
            }

            if(!mRespawnUsed) {
                //display countdown screen
                setCountDown(true);
            } else {
                //display game over with score
                displayScore();
            }
        }
    }

    @Override
    public void onBackKeyPressed() {
        if(mSharingVisible) {
            setSharingVisible(false);
        } else {

            mHudTextScore.setVisible(false);
            mHudTextPizza.setVisible(false);
            pizzaCollectedSprite.setVisible(false);
            mAnimatedCrapMeter.setVisible(false);
            mCrapMeter.setVisible(false);
            mPlusTwo.setVisible(false);
            mAlertSign.setVisible(false);
            mResourceManager.mMariachiFast.pause();
            mResourceManager.mMariachiSlow.pause();

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
