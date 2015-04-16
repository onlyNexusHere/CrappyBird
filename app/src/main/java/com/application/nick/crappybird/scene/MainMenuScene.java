package com.application.nick.crappybird.scene;

import android.opengl.GLES20;

import com.application.nick.crappybird.SceneManager;
import com.application.nick.crappybird.entity.BasicBird;
import com.application.nick.crappybird.entity.Crap;
import com.application.nick.crappybird.entity.CrapPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
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

    private final int MAX_CRAPS = 10;


    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 10);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerBack.getHeight(), mResourceManager.mParallaxLayerBack, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), mResourceManager.mParallaxLayerFront, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(0, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight() - mResourceManager.mParallaxLayerMiddle.getHeight(), mResourceManager.mParallaxLayerMiddle, mVertexBufferObjectManager)));

        setBackground(autoParallaxBackground);

        Sprite title = new Sprite(0,0, mResourceManager.mTitleTextureRegion, mVertexBufferObjectManager);
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

        final float playX = (SCREEN_WIDTH - mResourceManager.mButtonTextureRegion.getWidth()) / 2;
        final float playY = SCREEN_HEIGHT / 2;

        TiledSprite playSprite = new TiledSprite(playX, playY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);
                }
                if (pSceneTouchEvent.isActionUp()) {
                    /*if (mResourceManager.mMusic.isPlaying()) {
                        mResourceManager.mMusic.stop();
                    }*/

                    mSceneManager.setScene(SceneManager.SceneType.SCENE_GAME);

                }
                return true;
            }
        };
        playSprite.setCurrentTileIndex(0);
        playSprite.setScale(0.75f);
        registerTouchArea(playSprite);
        attachChild(playSprite);

        playSprite.setZIndex(20);

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
            mActivity.finish();
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
