package com.application.nick.crappybird.scene;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.BasicBird;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.Tutorial;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 4/5/2015.
 */
public class MainMenuScene extends BaseScene {

    private BasicBird mBird;
    private CrapPool mCrapPool;
    private List<Crap> mCraps = new ArrayList<Crap>();
    private Tutorial mTutorial;

    private TiledSprite playButton, helpButton, backButton;
    private Sprite title;

    private boolean playTransition = false, helpTransitionOn = false, helpTransitionOff = false, showingTutorial = false;

    private final int MAX_CRAPS = 10;

    private AutoParallaxBackground autoParallaxBackground;
    private ParallaxBackground.ParallaxEntity parallaxLayerBack;
    private ParallaxBackground.ParallaxEntity parallaxLayerMiddle;
    private ParallaxBackground.ParallaxEntity parallaxLayerFront;


    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 10);
        parallaxLayerBack = new ParallaxBackground.ParallaxEntity(0, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerBack.getHeight(), mResourceManager.mParallaxLayerBack, mVertexBufferObjectManager));
        parallaxLayerMiddle = new ParallaxBackground.ParallaxEntity(0, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight() - mResourceManager.mParallaxLayerMiddle.getHeight(), mResourceManager.mParallaxLayerMiddle, mVertexBufferObjectManager));
        parallaxLayerFront = new ParallaxBackground.ParallaxEntity(0, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), mResourceManager.mParallaxLayerFront, mVertexBufferObjectManager));

        autoParallaxBackground.attachParallaxEntity(parallaxLayerBack);
        autoParallaxBackground.attachParallaxEntity(parallaxLayerFront);
        autoParallaxBackground.attachParallaxEntity(parallaxLayerMiddle);

        setBackground(autoParallaxBackground);

        title = new Sprite(0,0, mResourceManager.mTitleTextureRegion, mVertexBufferObjectManager);
        title.setPosition((SCREEN_WIDTH - title.getWidth())/2f, 75);
        title.setScale(1.5f);
        attachChild(title);

        final float birdX = (SCREEN_WIDTH - mResourceManager.mBirdTextureRegion.getWidth()) / 2;
        final float birdY = title.getY() + title.getHeight() + 25;
        mBird = new BasicBird(birdX, birdY, mResourceManager.mBirdTextureRegion, mVertexBufferObjectManager);
        mBird.setRotation(-15);
        attachChild(mBird);

        mCrapPool = new CrapPool(mResourceManager.mCrapTextureRegion, mVertexBufferObjectManager);
        mCrapPool.batchAllocatePoolItems(MAX_CRAPS);


        if (!mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.play();
            mResourceManager.mMusic.setLooping(true);
        }


        final float tutorialX = -mResourceManager.mTutorialTextureRegion.getWidth();
        final float tutorialY = 0;

        mTutorial = new Tutorial(tutorialX, tutorialY, mResourceManager.mTutorialTextureRegion, mVertexBufferObjectManager);
        attachChild(mTutorial);

        final float playX = (SCREEN_WIDTH - mResourceManager.mPlayButtonTextureRegion.getWidth()) / 2;
        final float playY = SCREEN_HEIGHT / 2;

        playButton = new TiledSprite(playX, playY, mResourceManager.mPlayButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (playButton.isVisible()) {
                    if (pSceneTouchEvent.isActionDown()) {
                        setCurrentTileIndex(1);
                    }
                    if (pSceneTouchEvent.isActionUp()) {
                        if (mResourceManager.mMusic.isPlaying()) {
                            mResourceManager.mMusic.stop();
                        }
                        setCurrentTileIndex(0);
                        playTransition = true;
                        mBird.setXVelocity(90);

                    }
                }
                return true;
            }
        };


        playButton.setCurrentTileIndex(0);
        playButton.setScale(0.75f);
        registerTouchArea(playButton);
        attachChild(playButton);

        final float helpX = playX;
        final float helpY = playY + playButton.getHeight() * 1.1f;

        helpButton = new TiledSprite(helpX, helpY, mResourceManager.mHelpButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if(helpButton.isVisible()) {
                    if (pSceneTouchEvent.isActionDown()) {
                        setCurrentTileIndex(1);
                    }

                    if (pSceneTouchEvent.isActionUp()) {
                        setCurrentTileIndex(0);

                        transitionHelpOn(true);

                    }
                } else if(backButton.isVisible()) { //because back button shows up on top of help button when tutorial is visible
                    if (pSceneTouchEvent.isActionDown()) {
                        backButton.setCurrentTileIndex(1);
                    }

                    if (pSceneTouchEvent.isActionUp()) {
                        backButton.setCurrentTileIndex(0);

                        transitionHelpOff(true);

                    }
                }
                return true;
            }
        };


        helpButton.setCurrentTileIndex(0);
        helpButton.setScale(0.75f);
        registerTouchArea(helpButton);
        attachChild(helpButton);

        backButton = new TiledSprite(helpX, helpY, mResourceManager.mBackButtonTextureRegion, mVertexBufferObjectManager);

        backButton.setCurrentTileIndex(0);
        backButton.setScale(0.75f);
        attachChild(backButton);
        backButton.setVisible(false);

        final Rectangle ground = new Rectangle(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), SCREEN_WIDTH, mResourceManager.mParallaxLayerFront.getHeight(), mVertexBufferObjectManager);
        ground.setColor(Color.TRANSPARENT);

        /* The actual collision-checking. */
        registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(float pSecondsElapsed) {

                checkBirdPosition(SCREEN_HEIGHT / 2 - mBird.getHeight() * 2);

                if (mCraps.size() > 0) {
                    checkForCrapGroundContact(ground);
                }

                //rotate bird with changing velocity
                if (mBird.getY() + mBird.getHeight() < ground.getY()) {
                    mBird.setRotation((mBird.getVelocityY() / 30) * 2 - 10);
                }

                if(mBird.getX() > SCREEN_WIDTH && playTransition) {
                    playTransition = false;
                    mSceneManager.setScene(SceneManager.SceneType.SCENE_GAME);
                }

                if(helpTransitionOn) {
                    if(mTutorial.getX() > 0) {
                        transitionHelpOn(false);
                    }
                }

                if(helpTransitionOff) {
                    if(mTutorial.getX() > SCREEN_WIDTH) {
                        transitionHelpOff(false);
                    }
                }


            }
        });

    }

    /**
     * checks if bird is below y. if so, jumps and drops crap
     * @param y y value
     */
    private void checkBirdPosition(float y) {
        if(mBird.getY() > y) {
            mBird.setY(y - 1);
            jumpBird();
        }
    }


    private void jumpBird() {

        mBird.jump();

        dropCrap(mBird.getX(), mBird.getY());

    }

    /**
     * for transitioning the tutorial onto the screen
     * @param starting true if the transition is starting. false if ending
     */
    private void transitionHelpOn(boolean starting) {
        if(starting) {
            helpTransitionOn = true;

            title.setVisible(false);
            helpButton.setVisible(false);
            playButton.setVisible(false);

            mBird.setXVelocity(150);
            mTutorial.setXVelocity(150);
        } else {
            helpTransitionOn = false;

            mBird.setXVelocity(0);
            mBird.setX(-SCREEN_WIDTH + (SCREEN_WIDTH - mResourceManager.mBirdTextureRegion.getWidth()) / 2);
            mTutorial.setXVelocity(0);

            backButton.setVisible(true);
            showingTutorial = true;
        }
    }

    /**
     * for transitioning the tutorial off the screen
     * @param starting true if the transition is starting. false if ending
     */
    private void transitionHelpOff(boolean starting) {
        if(starting) {
            helpTransitionOff = true;

            mBird.setXVelocity(150);
            mTutorial.setXVelocity(150);

            backButton.setVisible(false);
            showingTutorial = false;

        } else {
            helpTransitionOff = false;

            mBird.setXVelocity(0);
            mTutorial.setXVelocity(0);
            mTutorial.setX(-SCREEN_WIDTH);

            title.setVisible(true);
            helpButton.setVisible(true);
            playButton.setVisible(true);
        }
    }

    private void dropCrap(float currentXPosition, float currentYPosition) {
        if(mCraps.size() == MAX_CRAPS) {
            detachChild(mCraps.get(0));
            mCrapPool.recyclePoolItem(mCraps.get(0));
            mCraps.remove(0);
        }

        mCraps.add(mCrapPool.obtainPoolItem());

        Crap crap = mCraps.get(mCraps.size() - 1);

        crap.setPosition(currentXPosition, currentYPosition + (mBird.getHeight()));

        crap.setXVelocity(randomizeCrapVelocityX());

        attachChild(crap);

        crap.setZIndex(5);

    }

    private float randomizeCrapVelocityX() {
        int rand = (int)(Math.random()*10) - 5;
        return (float) rand;
    }

    private void checkForCrapGroundContact(IShape ground) {
        for (int i = mCraps.size() - 1; i >= 0; i--) {
            if (mCraps.get(i).getY() + mCraps.get(i).getHeight() > ground.getY()) {
                mCraps.get(i).hitsGround(true);
            }
        }
    }


    @Override
    public void onBackKeyPressed() {
        if(showingTutorial) {
            transitionHelpOff(true);
        } else {
            mResourceManager.unloadGameResources();
            mActivity.finish();
        }
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
