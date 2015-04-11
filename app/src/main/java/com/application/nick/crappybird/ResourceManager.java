package com.application.nick.crappybird;

import android.graphics.Color;
import android.graphics.Typeface;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
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
    public ITextureRegion mParallaxLayerFront;

    private BitmapTextureAtlas mBitmapTextureAtlas;
    public TiledTextureRegion mBirdTextureRegion;
    public TiledTextureRegion mPipeTextureRegion;

    private BitmapTextureAtlas mBitmapTextureAtlas2;
    public TiledTextureRegion mCrapTextureRegion;

    private BitmapTextureAtlas mBitmapTextureAtlas3;
    private BitmapTextureAtlas mBitmapTextureAtlas4;
    private BitmapTextureAtlas mBitmapTextureAtlas5;
    private BitmapTextureAtlas mBitmapTextureAtlas6;
    private BitmapTextureAtlas mBitmapTextureAtlas7;

    public TiledTextureRegion mObstacleHouseTextureRegion;
    public TiledTextureRegion mObstacleTreesTextureRegion;
    public TiledTextureRegion mObstaclePlanesTextureRegion;

    private BitmapTextureAtlas mSubBitmapTextureAtlas;
    public TiledTextureRegion mStateTextureRegion;
    public ITextureRegion mPausedTextureRegion;
    public ITextureRegion mMeterTextureRegion;
    public ITextureRegion mResumedTextureRegion;
    public TiledTextureRegion mButtonTextureRegion;
    public TiledTextureRegion mMedalTextureRegion;

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
        mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, mActivity, "Flappy_Ground.png", 0, 0);
        mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAutoParallaxBackgroundTexture, mActivity, "Flappy_Background2.png", 0, 150);
        mAutoParallaxBackgroundTexture.load();

        mBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 120, 24, TextureOptions.BILINEAR);
        mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "birds.png", 0, 0, 3, 1);
        //mPipeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, mActivity, "Flappy_Pipe.png", 0, 125, 2, 1);
        mBitmapTextureAtlas.load();

        mBitmapTextureAtlas2 = new BitmapTextureAtlas(mActivity.getTextureManager(), 128, 512, TextureOptions.BILINEAR);
        mCrapTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas2, mActivity, "Flappy_Crappies2.png", 0, 0, 1, 3);
        mBitmapTextureAtlas2.load();

        mBitmapTextureAtlas3 = new BitmapTextureAtlas(mActivity.getTextureManager(), 320, 20, TextureOptions.BILINEAR);
        mMeterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas3, mActivity, "Crap_Supply_Meter.png", 0, 0);
        mBitmapTextureAtlas3.load();

        mBitmapTextureAtlas4 = new BitmapTextureAtlas(mActivity.getTextureManager(), 97, 97, TextureOptions.BILINEAR);
        mObstacleHouseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas4, mActivity, "house.png", 0, 0, 1, 1);
        mBitmapTextureAtlas4.load();

        mBitmapTextureAtlas5 = new BitmapTextureAtlas(mActivity.getTextureManager(), 97, 97, TextureOptions.BILINEAR);
        mObstacleTreesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas5, mActivity, "two-trees.png", 0, 0, 2, 1);
        mBitmapTextureAtlas5.load();

        mBitmapTextureAtlas6 = new BitmapTextureAtlas(mActivity.getTextureManager(), 383, 49, TextureOptions.BILINEAR);
        mObstaclePlanesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas6, mActivity, "planes.png", 0, 0, 4, 1);
        mBitmapTextureAtlas6.load();

        mSubBitmapTextureAtlas = new BitmapTextureAtlas(mActivity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
        mStateTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity, "ready_over.png", 0, 0, 2, 1);
        mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSubBitmapTextureAtlas, mActivity, "board.png", 0, 60);
        mResumedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSubBitmapTextureAtlas, mActivity, "help.png", 0, 200);
        mButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity, "play_pos.png", 0, 350, 2, 1);
        mMedalTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity, "medal.png", 0, 450, 4, 1);
        mSubBitmapTextureAtlas.load();
            //load fonts
        mFont4 = FontFactory.create(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 16, Color.BLACK);
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
        mBitmapTextureAtlas2.unload();
        mBitmapTextureAtlas3.unload();
        mBitmapTextureAtlas4.unload();
        mBitmapTextureAtlas5.unload();
        mBitmapTextureAtlas6.unload();
        mBitmapTextureAtlas7.unload();
        mSubBitmapTextureAtlas.unload();

        mAutoParallaxBackgroundTexture = null;
        mParallaxLayerFront = null;
        mParallaxLayerBack = null;

        mBitmapTextureAtlas = null;
        mBirdTextureRegion = null;
        mCrapTextureRegion = null;
        mPipeTextureRegion = null;

        mObstacleHouseTextureRegion = null;
        mObstaclePlanesTextureRegion = null;
        mObstacleTreesTextureRegion = null;

        mSubBitmapTextureAtlas = null;
        mStateTextureRegion = null;
        mMeterTextureRegion = null;
        mPausedTextureRegion = null;
        mResumedTextureRegion = null;
        mButtonTextureRegion = null;
        mMedalTextureRegion = null;
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
