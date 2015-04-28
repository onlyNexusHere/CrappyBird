package com.application.nick.crappybird.scene;

import android.hardware.SensorManager;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.Collectable;
import com.application.nick.crappybird.entity.CollectablePool;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.Obstacle;
import com.application.nick.crappybird.entity.ObstaclePlane;
import com.application.nick.crappybird.entity.ObstaclePool;
import com.application.nick.crappybird.entity.Target;
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
    private final int NUM_OBSTACLES = 20; //number of obstacles to allocate to pool
    private final int NUM_TARGETS = 5; //number of targets (people) to allocate
    private final int NUM_CRAPS = 15; //number of craps to allocate
    private final int NUM_COLLECTABLES = 9; //number of generic collectables (pizza) to allocate to pool


    private AutoParallaxBackground mAutoParallaxBackground;

    private Text mHudText;
    private int score;
    private int most;

    private AnimatedSprite mBird, mMachineCrapMeter;

    private Sprite mCrapMeter;

    private List<Obstacle> mObstacles;
    private ObstaclePool mObstaclePool;

    private List<Collectable> mCollectables;
    private CollectablePool mCollectablePool;

    private List<Target> mTargets;
    private TargetPool mTargetPool;

    private CrapPool mCrapPool;
    private List<Crap> mCraps = new ArrayList<Crap>();

    private boolean mGameOver, mOutOfCrap, mMachineCrapActivated = false, mMachineCrapping = false, mMachineCrapMeterBlinking = false;

    private float machineCrappingTime = 0, machineCrappingLastCrapTime;

    private PhysicsWorld mPhysicsWorld;

    private CameraScene mGameReadyScene;

    private CameraScene mGameOverScene;
    private Text scoreText;
    private Text mostText;
    private TiledSprite medalSprite;

    private float MAX_CRAP_METER_SIZE;


    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        setOnSceneTouchListener(this);

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
        final Rectangle ground = new Rectangle(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), SCREEN_WIDTH, mResourceManager.mParallaxLayerFront.getHeight(), mVertexBufferObjectManager);
        ground.setColor(Color.TRANSPARENT);
        final Rectangle roof = new Rectangle(0, 0, SCREEN_WIDTH, 1, mVertexBufferObjectManager);
        roof.setColor(Color.TRANSPARENT);

        mObstaclePool = new ObstaclePool(mResourceManager, mVertexBufferObjectManager, ground.getY());
        mObstaclePool.batchAllocatePoolItems(NUM_OBSTACLES);

        mObstacles = new ArrayList<Obstacle>();
        mObstacles.add(mObstaclePool.obtainPoolItem());

        mCollectablePool = new CollectablePool(mResourceManager, mVertexBufferObjectManager, ground.getY());
        mCollectablePool.batchAllocatePoolItems(NUM_COLLECTABLES);
        mCollectablePool.shufflePoolItems();

        mCollectables = new ArrayList<Collectable>();
        mCollectables.add(mCollectablePool.obtainPoolItem());

        mTargetPool = new TargetPool(mResourceManager, mVertexBufferObjectManager, ground.getY());
        mTargetPool.batchAllocatePoolItems(NUM_TARGETS);

        mTargets = new ArrayList<Target>();
        mTargets.add(mTargetPool.obtainPoolItem());

        mCrapPool = new CrapPool(mResourceManager.mCrapTextureRegion, mVertexBufferObjectManager);
        mCrapPool.batchAllocatePoolItems(NUM_CRAPS);
        mOutOfCrap = false;

        attachChild(ground);
        attachChild(roof);
        attachChild(mObstacles.get(mObstacles.size() - 1));
        attachChild(mCollectables.get(mCollectables.size() - 1));
        attachChild(mTargets.get(mTargets.size() - 1));

        mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH * 1.5f), false);
        mPhysicsWorld.setContactListener(createContactListener());

        //create body and fixture
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
        final Body groundBody = PhysicsFactory.createBoxBody(mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
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

                checkForTargetLeavingScreen();

                addNewTarget(SCREEN_WIDTH / 2); //add a new target if the earlist added on the screen passes x

                addNewCollectable(0); //add a new collectable if the earliest added on the screen passes x

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

                int obstaclesOnScreen = mObstacles.size();
                if(obstaclesOnScreen <= MAX_OBSTACLES) { //max obstacles on the screen (that haven't been passed) can't be more than x
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
                    checkForCrapGroundContact(ground);
                    checkForCrapLeavingScreen();

                }
                    //rotate bird with changing velocity
                if(mBird.getY() + mBird.getHeight() < ground.getY()) {
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
                }
                return true;
            }
        });

        setChildScene(mGameReadyScene, false, true, true);



        //create CameraScene for game over
        final float overX = (SCREEN_WIDTH - mResourceManager.mBoardTextureRegion.getWidth()) / 2;
        final float overY = labelY + mResourceManager.mStateTextureRegion.getHeight();

        final float playX = (SCREEN_WIDTH - mResourceManager.mButtonTextureRegion.getWidth()) / 2;
        final float playY = overY + mResourceManager.mBoardTextureRegion.getHeight();

        final float posX = SCREEN_WIDTH/2;
        final float posY = playY;

        final float medalX = overX + 46;
        final float medalY = overY + 46;

        final float scoreX = overX + 55;
        final float scoreY = overY + 40;

        final float mostX = overX + 165;
        final float mostY = scoreY;

        mGameOverScene = new CameraScene(mCamera);

        final TiledSprite labelSprite = new TiledSprite(readyX, readyY + mResourceManager.mStateTextureRegion.getHeight(), mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
        labelSprite.setCurrentTileIndex(1);
        mGameOverScene.attachChild(labelSprite);

        final Sprite boardSprite = new Sprite(overX, overY, mResourceManager.mBoardTextureRegion, mVertexBufferObjectManager);
        mGameOverScene.attachChild(boardSprite);

        scoreText = new Text(scoreX, scoreY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        scoreText.setText("0");
        mGameOverScene.attachChild(scoreText);

        mostText = new Text(mostX, mostY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mostText.setText(String.valueOf(most));
        mGameOverScene.attachChild(mostText);

        final TiledSprite playSprite = new TiledSprite(playX, playY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);
                }
                if (pSceneTouchEvent.isActionUp()) {
                    clearChildScene();
                    mSceneManager.setScene(SceneManager.SceneType.SCENE_GAME);
                }
                return true;
            }
        };
        playSprite.setCurrentTileIndex(0);
        playSprite.setScale(0.75f);
        mGameOverScene.registerTouchArea(playSprite);
        mGameOverScene.attachChild(playSprite);

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


        mCrapMeter = new Sprite((SCREEN_WIDTH - mResourceManager.mMeterTextureRegion.getWidth()) / 2, mResourceManager.mMeterTextureRegion.getHeight(), mResourceManager.mMeterTextureRegion, mVertexBufferObjectManager);
        mCrapMeter.setScale(0.75f);
        mCrapMeter.setX((SCREEN_WIDTH - mCrapMeter.getWidth()) / 2);
        MAX_CRAP_METER_SIZE = mCrapMeter.getWidth();
        mCrapMeter.setVisible(false);
        gameHUD.attachChild(mCrapMeter);

        mMachineCrapMeter = new AnimatedSprite((SCREEN_WIDTH - mResourceManager.mMeter2TextureRegion.getWidth()) / 2, mResourceManager.mMeter2TextureRegion.getHeight(), mResourceManager.mMeter2TextureRegion, mVertexBufferObjectManager);
        mMachineCrapMeter.setScale(0.75f);
        mMachineCrapMeter.setX((SCREEN_WIDTH - mMachineCrapMeter.getWidth()) / 2);
        mMachineCrapMeter.setVisible(false);
        gameHUD.attachChild(mMachineCrapMeter);

        mCamera.setHUD(gameHUD);
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
                score++;
                mHudText.setText(String.valueOf(score));
            }
        }
    }

    private void checkForTargetLeavingScreen() {
        for(int i = mTargets.size() - 1; i >= 0; i--) {
            Target target = mTargets.get(i);
            if (target.getX() <  -target.getWidth() || (target.getX() > SCREEN_WIDTH && mGameOver)) {
                detachChild(target);
                mTargetPool.recyclePoolItem(target);
                mTargetPool.shufflePoolItems();
                mTargets.remove(i);
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
     * adds a new obstacle when the earliest added obstacle still on the screen reaches a certain x value
     * @param x the x value to check
     */
    private void addNewObstacle(float x) {
        if(!mGameOver) {
            for (int i = mObstacles.size() - 1; i >= 0; i--) {
                Obstacle obstacle = mObstacles.get(i);
                if (obstacle.getX() < x && !obstacle.getPassedAddXValue()) {
                    obstacle.passedAddXValue();
                    mObstacles.add(mObstaclePool.obtainPoolItem());
                    Obstacle newObstacle = mObstacles.get(mObstacles.size() - 1);
                    newObstacle.setX(SCREEN_WIDTH);
                    //scale plane velocity according to number of passed obstacles
                    if(newObstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstaclePlane")) {
                        if(mObstaclePool.getObstacleIndex() < 100) {
                            newObstacle.setVelocity(-200f - mObstaclePool.getObstacleIndex(), 0);
                        } else {
                            newObstacle.setVelocity(-300, 0);
                        }
                        //if passed obstacle number x, have plane fly at random angles across the screen
                        if(mObstaclePool.getObstacleIndex() > 50) {
                            ((ObstaclePlane) newObstacle).randomizeYVelocity();
                        }
                    }
                    attachChild(newObstacle);
                    sortChildren();
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
            for(int i = mTargets.size() - 1; i >= 0; i--) {
                Target target = mTargets.get(i);
                if (target.getX() < x && !target.getPassedAddXValue()) {
                    target.passedAddXValue();
                    mTargets.add(mTargetPool.obtainPoolItem());
                    attachChild(mTargets.get(mTargets.size() - 1));
                    sortChildren();
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
                target.setVelocity(0,0);
            }
        }
    }

    /**
     * used when bird dies
     */
    private void killObstacles() {

        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if(!obstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstaclePlane")) {
                obstacle.die();
            } else { //plane keeps flying just at a lower velocity because scrolling stops
                obstacle.setVelocityX(-150f); //keep vertical velocity
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
                for(int j = mTargets.size() - 1; j >= 0; j--) {
                    Target target = mTargets.get(j);
                    if(target.collidesWith(crap)) {
                        target.hitByCrap();

                        detachChild(mCraps.get(i));
                        mCrapPool.recyclePoolItem(mCraps.get(i));
                        mCraps.remove(i);

                        score++;
                        mHudText.setText(String.valueOf(score));
                    }
                }
            }
        }
    }


    private void checkForObstacleBirdContact() {
        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if (!mGameOver && obstacle.collidesWith(mBird)) {
                if(mMachineCrapActivated) {
                    if(!obstacle.getCollidedWith()) {
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
                    }
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
                    mGameOver = true;
                    mBird.stopAnimation(0);
                    killObstacles();
                    killCollectables();
                    mAutoParallaxBackground.setParallaxChangePerSecond(0);

                    if (score > most) {
                        most = score;
                        mActivity.setMaxScore(most);
                    }

                    //hide score and crapMeter
                    mHudText.setVisible(false);
                    mCrapMeter.setVisible(false);
                    mMachineCrapMeter.setVisible(false);

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
        mHudText.setVisible(false);
        mMachineCrapMeter.setVisible(false);
        mCrapMeter.setVisible(false);

        /*if (!mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.play();
        }*/

        mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);

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
