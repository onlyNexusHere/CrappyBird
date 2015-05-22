package com.application.nick.crappybird;

import com.application.nick.crappybird.scene.BaseScene;
import com.application.nick.crappybird.scene.GameScene;
import com.application.nick.crappybird.scene.LeaderboardScene;
import com.application.nick.crappybird.scene.MainMenuScene;
import com.application.nick.crappybird.scene.MarketScene;
import com.application.nick.crappybird.scene.SplashScene;

/**
 * Created by Nick on 4/5/2015.
 */
public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    public enum SceneType {SCENE_SPLASH, SCENE_MENU, SCENE_GAME, SCENE_LEADERBOARD, SCENE_MARKET }

    private BaseScene mSplashScene;
    private BaseScene mMenuScene;
    private BaseScene mGameScene;
    private BaseScene mLeaderboardScene;
    private BaseScene mMarketScene;

    private SceneType mCurrentSceneType;
    private BaseScene mCurrentScene;

    private SceneManager() {}

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    public void setScene(SceneType sceneType) {
        switch (sceneType) {
            case SCENE_MENU:
                setScene(createMenuScene());
                break;
            case SCENE_GAME:
                setScene(createGameScene());
                break;
            case SCENE_SPLASH:
                setScene(createSplashScene());
                break;
            case SCENE_LEADERBOARD:
                setScene(createLeaderboardScene());
                break;
            case SCENE_MARKET:
                setScene(createMarketScene());
                break;
        }
    }

    private void setScene(BaseScene scene) {
        ResourceManager.getInstance().mActivity.getEngine().setScene(scene);
        mCurrentScene = scene;
        mCurrentSceneType = scene.getSceneType();
    }

    public SceneType getCurrentSceneType() {
        return mCurrentSceneType;
    }

    public BaseScene getCurrentScene() {
        return mCurrentScene;
    }

    public BaseScene createSplashScene() {
        mSplashScene = new SplashScene();
        return mSplashScene;
    }

    private BaseScene createMenuScene() {
        mMenuScene = new MainMenuScene();
        return mMenuScene;
    }

    private BaseScene createGameScene() {
        mGameScene = new GameScene();
        return mGameScene;
    }

    private BaseScene createLeaderboardScene() {
        mLeaderboardScene = new LeaderboardScene();
        return mLeaderboardScene;
    }

    private BaseScene createMarketScene() {
        mMarketScene = new MarketScene();
        return mMarketScene;
    }

}
