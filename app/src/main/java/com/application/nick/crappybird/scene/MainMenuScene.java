package com.application.nick.crappybird.scene;

import android.opengl.GLES20;

import com.application.nick.crappybird.SceneManager;

import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

/**
 * Created by Nick on 4/5/2015.
 */
public class MainMenuScene extends BaseScene implements MenuScene.IOnMenuItemClickListener {

    protected MenuScene mMenuScene;
    private MenuScene mSubMenuScene;

    protected static final int MENU_PLAY = 0;
    protected static final int MENU_RATE = 1;
    protected static final int MENU_EXTRAS = 2;
    protected static final int MENU_QUIT = 3;
    protected static final int MENU_READ = 4;
    protected static final int MENU_MORE = 5;

    @Override
    public void createScene() {
        AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 10);
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-5.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerBack.getHeight(), mResourceManager.mParallaxLayerBack, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), mResourceManager.mParallaxLayerFront, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxBackground.ParallaxEntity(-10.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight() - mResourceManager.mParallaxLayerMiddle.getHeight(), mResourceManager.mParallaxLayerMiddle, mVertexBufferObjectManager)));

        setBackground(autoParallaxBackground);

        Text nameText = new Text(0, 0, mResourceManager.mFont2, "Crappy Bird", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        nameText.setPosition((SCREEN_WIDTH - nameText.getWidth())/2f, 75);
        attachChild(nameText);

        final float birdX = (SCREEN_WIDTH - mResourceManager.mBirdTextureRegion.getWidth()) / 2;
        final float birdY = nameText.getY() + nameText.getHeight() + 25;
        TiledSprite bird = new TiledSprite(birdX, birdY, mResourceManager.mBirdTextureRegion, mVertexBufferObjectManager);
        bird.setRotation(-15);
        attachChild(bird);

        mMenuScene = createMenuScene();
        mSubMenuScene = createSubMenuScene();

        /* Attach the menu. */
        this.setChildScene(mMenuScene, false, true, true);

        if (!mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.play();
        }
    }

    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {

        switch(pMenuItem.getID()) {
            case MENU_PLAY:
                if (mResourceManager.mMusic.isPlaying()) {
                    mResourceManager.mMusic.pause();
                }
                mMenuScene.closeMenuScene();
                mSceneManager.setScene(SceneManager.SceneType.SCENE_GAME);
                return true;
/*
            case MENU_RATE:
                //TODO implement
                return true;

            case MENU_EXTRAS:
                pMenuScene.setChildSceneModal(mSubMenuScene);
                return true;

            case MENU_QUIT:
            // End Activity.
                mActivity.finish();
                return true;
*/
            case MENU_READ:
                mSubMenuScene.back();
                //TODO implement
                return true;

            case MENU_MORE:
                mSubMenuScene.back();
                //TODO implement
                return true;

            default:
                return false;
        }
    }


    protected MenuScene createMenuScene() {
        final MenuScene menuScene = new MenuScene(mCamera);

        final IMenuItem playMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_PLAY, mResourceManager.mFont3, "Play", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        playMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(playMenuItem);

        /*final IMenuItem rateMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_RATE, mResourceManager.mFont3, "Rate", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        rateMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(rateMenuItem);

        final IMenuItem extrasMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_EXTRAS, mResourceManager.mFont3, "Extras", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        extrasMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(extrasMenuItem);

        final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, mResourceManager.mFont3, "Quit", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem); */

        menuScene.buildAnimations();

        menuScene.setBackgroundEnabled(false);

        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    protected MenuScene createSubMenuScene() {
        final MenuScene subMenuScene = new MenuScene(mCamera);

        final IMenuItem moreMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_MORE, mResourceManager.mFont3, "More Apps", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        moreMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        subMenuScene.addMenuItem(moreMenuItem);

        final IMenuItem readMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_READ, mResourceManager.mFont3, "Read Tutorial", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        readMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        subMenuScene.addMenuItem(readMenuItem);

        subMenuScene.setMenuAnimator(new SlideMenuAnimator());
        subMenuScene.buildAnimations();

        subMenuScene.setBackgroundEnabled(false);

        subMenuScene.setOnMenuItemClickListener(this);
        return subMenuScene;
    }

    @Override
    public void onBackKeyPressed() {
        if (mMenuScene.hasChildScene())
            mSubMenuScene.back();
        else
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
