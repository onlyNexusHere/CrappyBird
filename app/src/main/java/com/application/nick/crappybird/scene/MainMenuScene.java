package com.application.nick.crappybird.scene;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.BasicBird;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.application.nick.crappybird.entity.Tutorial;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
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

    private final int MAX_TUTORIAL_TILE_INDEX = 3;

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

    private CameraScene mTutorialScene;


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
            mResourceManager.mMusic.setVolume(0.6f);
            mResourceManager.mMusic.setLooping(true);
        }


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
                        //if (mResourceManager.mMusic.isPlaying()) {
                        //    mResourceManager.mMusic.stop();
                        //}
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
                    if (pSceneTouchEvent.isActionDown()) {
                        setCurrentTileIndex(1);
                    }

                    if (pSceneTouchEvent.isActionUp()) {
                        setCurrentTileIndex(0);
                        showingTutorial = true;
                        setChildScene(mTutorialScene, false, true, true); //open tutorial scene

                    }

                return true;
            }
        };


        helpButton.setCurrentTileIndex(0);
        helpButton.setScale(0.75f);
        registerTouchArea(helpButton);
        attachChild(helpButton);
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


            }
        });


        mTutorialScene = new CameraScene(mCamera);
        mTutorialScene.setBackgroundEnabled(false);

        final float boardX = (SCREEN_WIDTH - mResourceManager.mTutorialBoardTextureRegion.getWidth()) / 2;
        final float boardY = boardX;

        final float tutorialX = boardX;
        final float tutorialY = boardY;

        final float nextX = boardX + mResourceManager.mTutorialBoardTextureRegion.getWidth() - mResourceManager.mNextButtonTextureRegion.getWidth();
        final float nextY = boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight() - mResourceManager.mNextButtonTextureRegion.getHeight();

        final float closeX = boardX;
        final float closeY = nextY;

        final Sprite tutorialBoard = new Sprite(boardX, boardY, mResourceManager.mTutorialBoardTextureRegion, mVertexBufferObjectManager);
        mTutorialScene.attachChild(tutorialBoard);

        final TiledSprite tutorial = new TiledSprite(tutorialX, tutorialY, mResourceManager.mTutorialTextureRegion, mVertexBufferObjectManager);
        mTutorialScene.attachChild(tutorial);

        final TiledSprite nextButton = new TiledSprite(nextX, nextY, mResourceManager.mNextButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    int tutorialTileIndex = tutorial.getCurrentTileIndex();
                    if(tutorialTileIndex == MAX_TUTORIAL_TILE_INDEX) {
                        tutorial.setCurrentTileIndex(0);
                    } else {
                        tutorial.setCurrentTileIndex(tutorialTileIndex + 1);
                    }
                }
                return true;
            }
        };
        nextButton.setCurrentTileIndex(0);
        nextButton.setScale(0.75f);
        mTutorialScene.registerTouchArea(nextButton);
        mTutorialScene.attachChild(nextButton);


        final TiledSprite closeButton = new TiledSprite(closeX, closeY, mResourceManager.mCloseButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);
                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    showingTutorial = false;
                    tutorial.setCurrentTileIndex(0);
                    clearChildScene();
                }
                return true;
            }
        };
        closeButton.setCurrentTileIndex(0);
        closeButton.setScale(0.75f);
        mTutorialScene.registerTouchArea(closeButton);
        mTutorialScene.attachChild(closeButton);

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
            clearChildScene();
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
