package com.application.nick.crappybird.scene;

import android.hardware.SensorManager;
import android.util.Log;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.Obstacle;
import com.application.nick.crappybird.entity.ObstaclePool;
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

    private AutoParallaxBackground mAutoParallaxBackground;

    private Text mHudText;
    private int score;
    private int most;

    private AnimatedSprite mBird;

    private Sprite mCrapMeter;

    private List<Obstacle> mObstacles;
    private ObstaclePool mObstaclePool;

    private CrapPool mCrapPool;
    private List<Crap> mCraps = new ArrayList<Crap>();

    private boolean mGameOver;

    private PhysicsWorld mPhysicsWorld;

    private CameraScene mGameReadyScene;

    private CameraScene mGameOverScene;
    private Text scoreText;
    private Text mostText;
    private TiledSprite medalSprite;

    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        setOnSceneTouchListener(this);

        mAutoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 10);
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-5.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerBack.getHeight(), mResourceManager.mParallaxLayerBack, mVertexBufferObjectManager)));
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), mResourceManager.mParallaxLayerFront, mVertexBufferObjectManager)));
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
        mObstaclePool.batchAllocatePoolItems(80);

        mObstacles = new ArrayList<Obstacle>();
        mObstacles.add(mObstaclePool.obtainPoolItem());

        mCrapPool = new CrapPool(mResourceManager.mCrapTextureRegion, mVertexBufferObjectManager);
        mCrapPool.batchAllocatePoolItems(15);

        attachChild(ground);
        attachChild(roof);
        attachChild(mObstacles.get(mObstacles.size() - 1));

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

                checkForObstacleLeavingScreen();

                addNewObstacle(score * 10); //add a new obstacle if the earliest added obstacle on the screen passes x

                //Update Score (Take into account multiple obstacles on screen)
                int scoreDifference = mObstaclePool.getObstacleIndex() - score;
                int nextObstacleIndex = mObstacles.size() - scoreDifference;
                if (scoreDifference != 0 && mBird.getX() > (mObstacles.get(nextObstacleIndex).getX()+ mObstacles.get(nextObstacleIndex).getWidth())) {
                    score = mObstaclePool.getObstacleIndex();
                    mHudText.setText(String.valueOf(score));
                }

                    //don't let bird leave top of screen
                if (mBird.getY() < 0) {
                    final Body faceBody = (Body)mBird.getUserData();
                    Vector2 newVelocity = Vector2Pool.obtain(0,0);

                    faceBody.setLinearVelocity(newVelocity);
                    Vector2Pool.recycle(newVelocity);
                }

                if(mCraps.size() > 0) {
                    checkForCrapContact(ground);
                    flushCrap();

                }
                    //rotate bird with changing velocity
                if(mBird.getY() + mBird.getHeight() < ground.getY()) {
                    Body birdBody = (Body) mBird.getUserData();
                    mBird.setRotation(birdBody.getLinearVelocity().y * 2 - 10);
                }


                mPhysicsWorld.onUpdate(pSecondsElapsed);

            }
        });


        final float labelX = (SCREEN_WIDTH - mResourceManager.mResumedTextureRegion.getWidth()) / 2;
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
        final Sprite resumedSprite = new Sprite(labelX, labelY, mResourceManager.mResumedTextureRegion, mVertexBufferObjectManager);
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
        final float overX = (SCREEN_WIDTH - mResourceManager.mPausedTextureRegion.getWidth()) / 2;
        final float overY = labelY + mResourceManager.mStateTextureRegion.getHeight();

        final float playX = overX;
        final float playY = overY + mResourceManager.mPausedTextureRegion.getHeight();

        final float posX = SCREEN_WIDTH/2;
        final float posY = playY;

        final float medalX = overX + 46;
        final float medalY = overY + 46;

        final float scoreX = overX + 165;
        final float scoreY = overY + 40;

        final float mostX = scoreX;
        final float mostY = scoreY + 25;

        mGameOverScene = new CameraScene(mCamera);

        final TiledSprite labelSprite = new TiledSprite(readyX, readyY, mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
        labelSprite.setCurrentTileIndex(1);
        mGameOverScene.attachChild(labelSprite);

        final Sprite pauseSprite = new Sprite(overX, overY, mResourceManager.mPausedTextureRegion, mVertexBufferObjectManager);
        pauseSprite.setScale(0.75f);
        mGameOverScene.attachChild(pauseSprite);

        medalSprite = new TiledSprite(medalX, medalY, mResourceManager.mMedalTextureRegion, mVertexBufferObjectManager);
        medalSprite.setCurrentTileIndex(0);
        medalSprite.setScale(0.75f);
        mGameOverScene.attachChild(medalSprite);

        scoreText = new Text(scoreX, scoreY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        scoreText.setText("0");
        mGameOverScene.attachChild(scoreText);

        mostText = new Text(mostX, mostY, mResourceManager.mFont4, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mostText.setText(String.valueOf(most));
        mGameOverScene.attachChild(mostText);

        final TiledSprite playSprite = new TiledSprite(playX, playY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
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

        final TiledSprite posSprite = new TiledSprite(posX, posY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager);
        posSprite.setCurrentTileIndex(1);
        posSprite.setScale(0.75f);
        mGameOverScene.registerTouchArea(posSprite);
        mGameOverScene.attachChild(posSprite);

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
        mCrapMeter.setVisible(false);
        gameHUD.attachChild(mCrapMeter);
        mCamera.setHUD(gameHUD);
    }

    private void checkForObstacleLeavingScreen() {
        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if (!obstacle.hasParent()) {
                break;
            } else if (obstacle.getX() <  -obstacle.getWidth()) {
                detachChild(obstacle);
                mObstaclePool.recyclePoolItem(obstacle);
                mObstaclePool.shufflePoolItems();
            }
        }
    }

    /**
     * adds a new obstacle when the earliest added obstacle still on the screen reaches a certain x value
     * @param x the x value to check
     */
    private void addNewObstacle(float x) {
        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if(!obstacle.hasParent()) {
                break;
            } else {
                if (obstacle.getX() < x && !obstacle.getPassedAddXValue()) {
                    obstacle.passedAddXValue();
                    mObstacles.add(mObstaclePool.obtainPoolItem());
                    attachChild(mObstacles.get(mObstacles.size() - 1));
                    sortChildren();
                }
            }
        }
    }

    /**
     * used when bird hits ground
     */
    private void killObstacles() {

        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if(!obstacle.hasParent()) {
                break;
            } else {
                if(!obstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstaclePlane")) {
                    obstacle.die();
                } else { //plane keeps flying just at a lower velocity because scrolling stops
                    obstacle.setVelocity(-150f, 0);
                }
            }
        }
    }

    private void checkForObstacleBirdContact() {
        for(int i = mObstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = mObstacles.get(i);
            if(!mObstacles.get(i).hasParent()) {
                break;
            } else if (!mGameOver && obstacle.collidesWith(mBird)) {
                mGameOver = true;
                mResourceManager.mSound.play();
                mBird.stopAnimation(0);
                stopCrap();

                if(!obstacle.getClass().getName().equals("com.application.nick.crappybird.entity.ObstaclePlane")) {
                    obstacle.die();
                } else { //plane keeps flying just at a lower velocity because scrolling stops
                    obstacle.setVelocity(-150f, 0);
                }

                mAutoParallaxBackground.setParallaxChangePerSecond(0);
                return;
            }


        }
    }

    private void checkForCrapContact(IShape shape) {
        if(shape.getClass().getName().equals("org.andengine.entity.primitive.Rectangle")) { //if shape is ground
                for (int i = mCraps.size() - 1; i >= 0; i--) {
                    if(!mCraps.get(i).hasParent()){
                        break;
                    }
                    if (mCraps.get(i).getY() + mCraps.get(i).getHeight() > shape.getY()) {
                        mCraps.get(i).hitsGround(mGameOver);
                    }
                }
        }
    }

    /**
     * Shrinks crap meter on jump/crap
     * @param amountToShrink
     */
    private void shrinkCrapMeter(float amountToShrink) {
        float currentWidth = mCrapMeter.getWidth();
        if(currentWidth <= amountToShrink) {
            mGameOver = true;
        } else {
            mCrapMeter.setWidth(currentWidth - amountToShrink);
        }
    }

    /**
     * Check if crap has left screen. If so, recycle
     */
    private void flushCrap() {
                for (int i = mCraps.size() - 1; i >= 0; i--) {
                    if(!mCraps.get(i).hasParent()){
                        break;
                    } else if(mCraps.get(i).getX() < -mResourceManager.mCrapTextureRegion.getWidth()) {
                        detachChild(mCraps.get(i));
                        mCrapPool.recyclePoolItem(mCraps.get(i));
                    }
                }


    }



    /**
     * Stops crap when gameover
     */
    private void stopCrap() {
        if(mCraps.size() > 0) {
                for (int i = mCraps.size() - 1; i >= 0; i--) {
                    if(!mCraps.get(i).hasParent()){
                        break;
                    }
                    mCraps.get(i).setXVelocity(0);
                }

        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if(mPhysicsWorld != null) {
            if(!mGameOver && pSceneTouchEvent.isActionDown()) {
                jumpFace(mBird);
                return true;
            }
        }
        return false;
    }

    private void jumpFace(final AnimatedSprite face) {
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

            //drop crap

        mCraps.add(mCrapPool.obtainPoolItem());
        int crapIndex = mCrapPool.getCrapIndex();

        Crap crap = mCraps.get(crapIndex);

        crap.setPosition(currentXPosition, currentYPosition + (mBird.getHeight()));

        Log.i("currentYPosition", currentYPosition + "");
        Log.i("currentXPosition", currentXPosition + "");
        Log.i("mBird.getHeight()", mBird.getHeight() + "");

        if(!crap.hasParent()) {
            attachChild(crap);
        }

        shrinkCrapMeter((float) Math.sqrt((double) score * 4));
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
                    mAutoParallaxBackground.setParallaxChangePerSecond(0);

                    if (score > most) {
                        most = score;
                        mActivity.setMaxScore(most);
                    }
                    //hide score and crapMeter
                    mHudText.setVisible(false);
                    mCrapMeter.setVisible(false);

                    //display game over with score
                    scoreText.setText(String.valueOf(score));
                    mostText.setText(String.valueOf(most));
                    medalSprite.setCurrentTileIndex(score>100 ? 3 : (score>50 ? 2 : (score>10 ? 1 : 0)));
                    setChildScene(mGameOverScene, false, true, true);
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
        mCrapMeter.setVisible(false);
        mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU
        );
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
