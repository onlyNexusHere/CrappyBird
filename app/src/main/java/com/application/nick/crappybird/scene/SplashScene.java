package com.application.nick.crappybird.scene;

import com.application.nick.crappybird.SceneManager;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.util.GLState;
import org.andengine.util.HorizontalAlign;

/**
 * Created by Nick on 4/5/2015.
 */
public class SplashScene extends BaseScene {

    @Override
    public void createScene() {

        Sprite splash = new Sprite(0, 0, mResourceManager.mSplashTextureRegion, mVertexBufferObjectManager) {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera)
            {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };
        attachChild(splash);

        Text copyrightText = new Text(0, 0, mResourceManager.mFont1, "(c) 2015", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        copyrightText.setPosition(SCREEN_WIDTH - copyrightText.getWidth()-20, SCREEN_HEIGHT - copyrightText.getHeight()-5);
        attachChild(copyrightText);
    }

    @Override
    public void onBackKeyPressed() {
        mActivity.finish();
    }

    @Override
    public SceneManager.SceneType getSceneType() {
        return SceneManager.SceneType.SCENE_SPLASH;
    }

    @Override
    public void disposeScene() {
    }

}
