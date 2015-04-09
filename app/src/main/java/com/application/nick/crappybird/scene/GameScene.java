package com.application.nick.crappybird.scene;

import android.hardware.SensorManager;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.Pipe;
import com.application.nick.crappybird.entity.PipePool;
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

    private Pipe mPipe;
    private PipePool mPipePool;

    private CrapPool mCrapPool;
    private List<Crap> mCraps = new ArrayList<Crap>();

    private boolean mGameOver;
    private float mPipeWidth;

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

        mPipeWidth = mResourceManager.mPipeTextureRegion.getWidth();



        //create entities
        final Rectangle ground = new Rectangle(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), SCREEN_WIDTH, mResourceManager.mParallaxLayerFront.getHeight(), mVertexBufferObjectManager);
        ground.setColor(Color.TRANSPARENT);
        final Rectangle roof = new Rectangle(0, 0, SCREEN_WIDTH, 1, mVertexBufferObjectManager);
        roof.setColor(Color.TRANSPARENT);

        mPipePool = new PipePool(mResourceManager.mPipeTextureRegion, mVertexBufferObjectManager, ground.getY());
        mPipePool.batchAllocatePoolItems(10);
        mPipe = mPipePool.obtainPoolItem();

        mCrapPool = new CrapPool(mResourceManager.mCrapTextureRegion, mVertexBufferObjectManager, birdX);
        mCrapPool.batchAllocatePoolItems(15);

        attachChild(ground);
        attachChild(roof);
        attachChild(mPipe);

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

                if (!mGameOver && mPipe.collidesWith(mBird)) {
                    mGameOver = true;
                    mResourceManager.mSound.play();
                    mBird.stopAnimation(0);
                    stopCrap();
                    mPipe.die();
                    mAutoParallaxBackground.setParallaxChangePerSecond(0);
                    return;
                }

                if (mPipe.getX() < -mPipeWidth) {
                    detachChild(mPipe);
                    mPipePool.recyclePoolItem(mPipe);
                    mPipePool.shufflePoolItems();

                    mPipe = mPipePool.obtainPoolItem();
                    attachChild(mPipe);
                    sortChildren();
                }

                if (score != mPipePool.getPipeIndex() && mBird.getX() > (mPipe.getX()+mPipeWidth)) {
                    score = mPipePool.getPipeIndex();
                    mHudText.setText(String.valueOf(score));
                }

                if (mBird.getY() < 0) {
                    final Body faceBody = (Body)mBird.getUserData();
                    Vector2 newVelocity = Vector2Pool.obtain(0,0);

                    faceBody.setLinearVelocity(newVelocity);
                    Vector2Pool.recycle(newVelocity);
                }

                if(mCraps.size() > 0) {
                    flushCrap();
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
        mCamera.setHUD(gameHUD);
    }

    /**
     * Check if crap has left screen. If so, recycle
     */
    private void flushCrap() {
            if(mCraps.size() < 15) {
                for(int i = 0; i < mCraps.size(); i++) {
                    if(mCraps.get(i).getX() < -mResourceManager.mCrapTextureRegion.getWidth()) {
                        detachChild(mCraps.get(i));
                        mCrapPool.recyclePoolItem(mCraps.get(i));
                    }
                }
            } else {
                for(int i = mCraps.size() - 15; i < mCraps.size(); i++) {
                    if(mCraps.get(i).getX() < -mResourceManager.mCrapTextureRegion.getWidth()) {
                        detachChild(mCraps.get(i));
                        mCrapPool.recyclePoolItem(mCraps.get(i));
                    }
                }
            }


    }

    /**
     * Stops crap when gameover
     */
    private void stopCrap() {
        if(mCraps.size() > 0) {
            if(mCraps.size() < 15) {
                for(int i = 0; i < mCraps.size(); i++) {
                    mCraps.get(i).setXVelocity(0);
                }
            } else {
                for(int i = mCraps.size() - 15; i < mCraps.size(); i++) {
                    mCraps.get(i).setXVelocity(0);
                }
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

        Vector2 currentPosition = faceBody.getPosition();
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

        /*final Sprite crap = new Sprite(currentXPosition, currentYPosition, mResourceManager.mBirdTextureRegion.getWidth(), mResourceManager.mBirdTextureRegion.getHeight(), mResourceManager.mCrapTextureRegion)
        {


            @Override
            protected void onManagedUpdate(final float pSecondsElapsed)
            {
                if (player.collidesWith(this))
                {
                    setVisible(false);
                    //Play pick up sound here
                    addToScore(10);
                    setIgnoreUpdate(true);
                }
                super.onManagedUpdate(pSecondsElapsed);
            }
        }; */


        mCraps.add(mCrapPool.obtainPoolItem());
        int crapIndex = mCrapPool.getCrapIndex();

        Crap crap = mCraps.get(crapIndex);

        crap.setY(currentYPosition + (mBird.getHeight()));

        TiledSprite crapSprite = crap.getCrapSprite();

        crapSprite.setCullingEnabled(true);
        attachChild(crapSprite);

        final FixtureDef crapFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0);

        final Body crapBody = PhysicsFactory.createCircleBody(mPhysicsWorld, crapSprite, BodyDef.BodyType.DynamicBody, crapFixtureDef);
        crapBody.setUserData("crap" + crapIndex);
        crapSprite.setUserData(crapBody);

        crapBody.setLinearVelocity(-1, 5); //initial gas propulsion and drag


        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(crapSprite, crapBody, true, false));

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
                    mPipe.die();
                    mAutoParallaxBackground.setParallaxChangePerSecond(0);

                    if (score > most) {
                        most = score;
                        mActivity.setMaxScore(most);
                    }
                    //hide bird and score
                    //mBird.setVisible(false);
                    mHudText.setVisible(false);

                    //display game over with score
                    scoreText.setText(String.valueOf(score));
                    mostText.setText(String.valueOf(most));
                    medalSprite.setCurrentTileIndex(score>100 ? 3 : (score>50 ? 2 : (score>10 ? 1 : 0)));
                    setChildScene(mGameOverScene, false, true, true);
                } else if (("crap".equals(userDataA.substring(0,4)) && "ground".equals(userDataB)) || ("ground".equals(userDataA) && "crap".equals(userDataB.substring(0,4)))) {
                    Crap crap;
                    if(userDataA.substring(0,4).equals("crap")) {
                        //get crapIndex from userData
                        crap = mCraps.get(Integer.parseInt(userDataA.substring(4)));
                    } else {
                        crap = mCraps.get(Integer.parseInt(userDataB.substring(4)));
                    }

                    crap.hitsGround();

                    if(!mGameOver) {
                        crap.setLinearVelocity(-5,0);
                    }

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
