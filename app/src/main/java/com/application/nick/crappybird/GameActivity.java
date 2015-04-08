package com.application.nick.crappybird;

import android.content.Context;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;


public class GameActivity extends SimpleBaseGameActivity {

    public static final int CAMERA_WIDTH = 320;
    public static final int CAMERA_HEIGHT = 480;

    private Camera mCamera;
    private ResourceManager mResourceManager;
    private SceneManager mSceneManager;

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        engineOptions.getAudioOptions().setNeedsSound(true).setNeedsMusic(true);
        return engineOptions;
    }

    @Override
    protected void onCreateResources() {
        mResourceManager = ResourceManager.getInstance();
        mResourceManager.prepare(this);
        mResourceManager.loadSplashResources();

        mSceneManager = SceneManager.getInstance();
    }

    @Override
    protected Scene onCreateScene() {

        mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                mResourceManager.loadGameResources();
                mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                mResourceManager.unloadSplashResources();
            }
        }));

        return mSceneManager.createSplashScene();
    }

    public int getMaxScore() {
        return getPreferences(Context.MODE_PRIVATE).getInt("maxScore", 0);
    }

    public void setMaxScore(int maxScore) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt("maxScore", maxScore).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        if (mSceneManager.getCurrentScene() != null) {
            mSceneManager.getCurrentScene().onBackKeyPressed();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (mResourceManager.mMusic!=null && mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.pause();
        }
        super.onPause();
    }
}
