package com.application.nick.crappybird;

import android.graphics.Color;
import android.graphics.Typeface;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

import java.io.IOException;

/**
 * Created by Nick on 4/5/2015.
 */
public class ResourceManager {

    private static final ResourceManager INSTANCE = new ResourceManager();

    public GameActivity mActivity;

    private BitmapTextureAtlas mSplashTextureAtlas;
    public ITextureRegion mSplashTextureRegion;

    private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
    public ITextureRegion mParallaxLayerBack;
    public ITextureRegion mParallaxLayerMiddle;
    public ITextureRegion mParallaxLayerFront;

    private BitmapTextureAtlas mBitmapTextureAtlas;
    public TiledTextureRegion mBirdTextureRegion;
    public TiledTextureRegion mPipeTextureRegion;

    public TiledTextureRegion mCrapTextureRegion;
    public TiledTextureRegion mObstacleHouseTextureRegion;
    public TiledTextureRegion mObstacleTreesTextureRegion;
    public TiledTextureRegion mObstaclePlanesTextureRegion;

    public TiledTextureRegion mCollectablePizzaTextureRegion;

    public TiledTextureRegion mTargetPerson1TextureRegion;

    private BitmapTextureAtlas mSubBitmapTextureAtlas;
    public TiledTextureRegion mStateTextureRegion;
    public ITextureRegion mBoardTextureRegion;
    public ITextureRegion mMeterTextureRegion;
    public ITextureRegion mHelpTextureRegion;
    public TiledTextureRegion mButtonTextureRegion;
    public ITextureRegion mTitleTextureRegion;

    public Font mFont1;
    public Font mFont2;
    public Font mFont3;
    public Font mFont4;
    public Font mFont5;

    public Sound mSound;
    public Music mMusic;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    public void prepare(GameActivity activity) {
        INSTANCE.mActivity = activity;
    }

    public void loadSplashResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");
        mSplashTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 320, 533, TextureOptions.BILINEAR);
        mSplashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSplashTextureAtlas, mActivity, "logo2.png", 0, 0);
        mSplashTextureAtlas.load();

        mFont1 = FontFactory.create(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 10, Color.GRAY);
        mFont1.load();
    }

    public void unloadSplashResources() {
        mSplashTextureAtlas.unload();
        mSplashTextureRegion = null;

        mFont1.unload();
        mFont1 = null;
    }

    public void loadGameResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
            //load gfx
        mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(mActivity.getTextureManager(), 512, 1024);
        mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, mActivity, "ground.png", 0, 0);
        mParallaxLayerMiddle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, mActivity, "grass.png", 0, 150);
        mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, mActivity, "background.png", 0, 175);
        mAutoParallaxBackgroundTexture.load();

        mBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 400, 500, TextureOptions.BILINEAR);

        mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "birds.png", 0, 0, 3, 1);

        mTargetPerson1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "person1.png", 0, 30, 8, 4);

        mCrapTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "Flappy_Crappies2.png", 0, 230, 1, 3);
        mMeterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, mActivity, "Crap_Supply_Meter.png", 0, 270);

        mObstacleHouseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "house.png", 0, 300, 1, 1);
        mObstacleTreesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "two-trees.png", 100, 300, 2, 1);
        mObstaclePlanesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "planes.png", 0, 400, 4, 1);

        mCollectablePizzaTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "pizza.png", 0, 450, 1, 1);

        mBitmapTextureAtlas.load();

        mSubBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 512, 520, TextureOptions.BILINEAR);
        mStateTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity, "ready_over2.png", 0, 0, 2, 1);
        mBoardTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSubBitmapTextureAtlas, mActivity, "board2.png", 0, 60);
        mHelpTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSubBitmapTextureAtlas, mActivity, "help2.png", 0, 200);
        mButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity, "play_button.png", 0, 350, 2, 1);
        mTitleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSubBitmapTextureAtlas, mActivity, "title2.png", 0, 450);

        mSubBitmapTextureAtlas.load();
            //load fonts
        mFont4 = FontFactory.create(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 45, Color.BLACK);
        mFont4.load();

        FontFactory.setAssetBasePath("font/");
        //ITexture fontTexture2 = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        //mFont2 = FontFactory.createStrokeFromAsset(mActivity.getFontManager(), fontTexture2, mActivity.getAssets(), "GrutchShaded.ttf", 40, true, Color.YELLOW, 2, Color.DKGRAY);
        mFont2 = FontFactory.create(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 45, Color.BLACK);
        mFont2.load();

        //ITexture fontTexture3 = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        //mFont3 = FontFactory.createFromAsset(mActivity.getFontManager(), fontTexture3, mActivity.getAssets(), "Archistico_Bold.ttf", 24, true, Color.WHITE);
        mFont3 = FontFactory.create(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 25, Color.BLACK);
        mFont3.load();

        //ITexture fontTexture5 = new BitmapTextureAtlas(mActivity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        //mFont5 = FontFactory.createStrokeFromAsset(mActivity.getFontManager(), fontTexture5, mActivity.getAssets(), "GrutchShaded.ttf", 36, true, Color.WHITE, 2, Color.DKGRAY);
        mFont5 = FontFactory.create(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 30, Color.WHITE);
        mFont5.load();
            //load sound/music
        SoundFactory.setAssetBasePath("mfx/");
        try {
            mSound = SoundFactory.createSoundFromAsset(mActivity.getEngine().getSoundManager(), mActivity, "metal_hit.ogg");
        } catch (final IOException e) {
            Debug.e(e);
        }

        MusicFactory.setAssetBasePath("mfx/");
        try {
            mMusic = MusicFactory.createMusicFromAsset(mActivity.getEngine().getMusicManager(), mActivity, "bird_sound.ogg");
            mMusic.setLooping(true);
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    public void unloadGameResources() {
            //unload gfx
        mAutoParallaxBackgroundTexture.unload();
        mBitmapTextureAtlas.unload();
        mSubBitmapTextureAtlas.unload();

        mAutoParallaxBackgroundTexture = null;
        mParallaxLayerFront = null;
        mParallaxLayerMiddle = null;
        mParallaxLayerBack = null;

        mBitmapTextureAtlas = null;
        mBirdTextureRegion = null;
        mCrapTextureRegion = null;
        mPipeTextureRegion = null;

        mObstacleHouseTextureRegion = null;
        mObstaclePlanesTextureRegion = null;
        mObstacleTreesTextureRegion = null;

        mTargetPerson1TextureRegion = null;

        mStateTextureRegion = null;
        mMeterTextureRegion = null;
        mBoardTextureRegion = null;
        mHelpTextureRegion = null;
        mButtonTextureRegion = null;
        mTitleTextureRegion = null;
            //unload fonts
        mFont4.unload();
        mFont4 = null;

        mFont2.unload();
        mFont2 = null;

        mFont3.unload();
        mFont3 = null;

        mFont5.unload();
        mFont5 = null;
            //unload sound/music
        mSound.release();
        mSound = null;

        mMusic.stop();
        mMusic.release();
        mMusic = null;


    }
}
