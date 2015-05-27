package com.application.nick.crappybird.scene;

import android.util.Log;

import com.application.nick.crappybird.SceneManager;
import com.parse.ParseUser;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;

/**
 * Created by Nick on 4/5/2015.
 */
public class MarketScene extends BaseScene {

    public static final String[] BIRD_NAMES = {
            "Classic Bird",
            "Red Bird",
            "Blue Bird",
            "Brown Bird",
            "Black Bird",
            "White Bird",
            "Ninja Bird",
            "Rainbow Bird",
            "Hungry Bird",
            "Blocky Bird",
            "Ghost Bird",
            "Golden Bird"
    };

    public static final int[] BIRD_PRICES = {
            0,    //classic bird
            50,   //red bird
            50,   //blue bird
            100,  //brown bird
            200,  //black bird
            200,  //white bird
            500,  //ninja bird
            500,  //rainbow bird
            1000, //hungry bird
            1000, //blocky bird
            2000, //ghost bird
            5000  //golden bird
    };

    public static final String[] PIZZA_PURCHASES = {
            "1,000 Pizza",
            "5,000 Pizza",
            "10,000 Pizza"
    };

    public static final String[] POWER_UPS = {
            "Thunder Taco",
            "Double Points Ham",
            "Slo-mo Muffin",
            "Mega Melon"
    };

    public static final int[][] POWER_UP_PRICES = {
            {0, 200, 500, 1000, 1500, 2000},   //thunder taco
            {0, 200, 500, 1000, 1500, 2000},   //double points ham
            {100, 200, 500, 1000, 1500, 2000}, //slo-mo muffin
            {100, 200, 500, 1000, 1500, 2000}  //mega melon
    };

    private static String[] mPizzaPurchasePrices = new String[3];

    private final int MYSTERY_INDEX = 12;

    private CameraScene mLoginScene, mBirdMarketScene, mPowerUpMarketScene, mPizzaMarketScene;

    private Text loadingText, pizzaPurchasePriceText, pizzaPurchaseText, birdMarketTotalPizzaText, pizzaMarketTotalPizzaText, powerUpMarketTotalPizzaText;

    private Sprite birdMarketTotalPizzaSprite, pizzaMarketTotalPizzaSprite, powerUpMarketTotalPizzaSprite;

    private int currentBird, numBirds, currentPizzaPurchase = 0, currentPowerUp = 0;

    private AutoParallaxBackground autoParallaxBackground;
    private ParallaxBackground.ParallaxEntity parallaxLayerBack;
    private ParallaxBackground.ParallaxEntity parallaxLayerMiddle;
    private ParallaxBackground.ParallaxEntity parallaxLayerFront;

    private ParseUser currentUser;

    private boolean birdMarketSceneOpen, powerUpMarketSceneOpen, pizzaMarketSceneOpen = false;


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

        currentBird = mActivity.getSelectedBird();
        numBirds = MarketScene.BIRD_NAMES.length;

        MarketScene.mPizzaPurchasePrices[0] = mActivity.getPrice1000Pizza();
        MarketScene.mPizzaPurchasePrices[1] = mActivity.getPrice5000Pizza();
        MarketScene.mPizzaPurchasePrices[2] = mActivity.getPrice10000Pizza();

        if (!mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.play();
            mResourceManager.mMusic.setVolume(0.6f);
            mResourceManager.mMusic.setLooping(true);
        }

        //if user is not logged in already, open login popup
        if(ParseUser.getCurrentUser() == null) {
            createLoginScene();
            setChildScene(mLoginScene, false, true, true);
        } else {
            currentUser = ParseUser.getCurrentUser();

            currentUser.put("hasBird0", true);
            if(currentUser.getInt("powerUp0") == 0) {
                currentUser.put("powerUp0", 1);
                currentUser.put("powerUp1", 1);
            }

            if(currentUser.getInt("pizzaCollected") > mActivity.getPizza()) {
                mActivity.setPizza(currentUser.getInt("pizzaCollected"));
            }

            createBirdMarketScene();
            createPowerUpMarketScene();
            createPizzaMarketScene();
            openPowerUpsMarketScene();

        }


        /*final float loadingTextY = 90;
        loadingText = (new Text(0, loadingTextY, mResourceManager.mFont3, "Loading...", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        loadingText.setX((SCREEN_WIDTH - loadingText.getWidth()) / 2);
        attachChild(loadingText);
        */
    }


    private void createPowerUpMarketScene(){

        mPowerUpMarketScene = new CameraScene(mCamera);
        mPowerUpMarketScene.setBackgroundEnabled(false);

        final float backButtonX = 0;
        final float backButtonY = -5;

        final float logoutButtonX = SCREEN_WIDTH - mResourceManager.mLogoutButtonTextureRegion.getWidth();
        final float logoutButtonY = -5;

        final float boardX = (SCREEN_WIDTH - mResourceManager.mTutorialBoardTextureRegion.getWidth()) / 2;
        final float boardY = backButtonY + mResourceManager.mBackButtonTextureRegion.getHeight();

        final float birdsButtonX = SCREEN_WIDTH/2 - mResourceManager.mBirdsMarketButtonTextureRegion.getWidth();
        final float birdsButtonY = boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight();;

        final float powerUpsButtonX = SCREEN_WIDTH/2;
        final float powerUpsButtonY = birdsButtonY;

        final float getMoreButtonX = (SCREEN_WIDTH - mResourceManager.mGetMorePizzaButtonTextureRegion.getWidth()) / 2;
        final float getMoreButtonY = boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight() - mResourceManager.mGetMorePizzaButtonTextureRegion.getHeight();

        final float totalPizzaY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2 - mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2;
        final float totalPizzaTextY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2 - mResourceManager.mFont3.getLineHeight() / 2;

        final float arrowLeftX = boardX;
        final float arrowButtonsY = (2 * boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight()) / 2 - mResourceManager.mArrowLeftButtonTextureRegion.getHeight() / 2;

        final float arrowRightX = boardX + mResourceManager.mTutorialBoardTextureRegion.getWidth() - mResourceManager.mArrowLeftButtonTextureRegion.getWidth();

        final float powerUpX = (SCREEN_WIDTH - mResourceManager.mMarketBirdsTextureRegion.getWidth()) / 2;
        final float powerUpY = (2 * boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight()) / 2 - mResourceManager.mMarketBirdsTextureRegion.getHeight() / 2;

        final float purchaseButtonX = (SCREEN_WIDTH - mResourceManager.mPurchaseButtonTextureRegion.getWidth()) / 2;
        final float purchaseButtonY = arrowButtonsY + mResourceManager.mArrowLeftButtonTextureRegion.getHeight();

        final float levelTextY = powerUpY + mResourceManager.mPowerUpPurchasesTextureRegion.getHeight();

        powerUpMarketTotalPizzaSprite = new Sprite(0, totalPizzaY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mPowerUpMarketScene.attachChild(powerUpMarketTotalPizzaSprite);
        powerUpMarketTotalPizzaSprite.setScale(.5f);

        powerUpMarketTotalPizzaText = (new Text(0, totalPizzaTextY, mResourceManager.mFont3, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        powerUpMarketTotalPizzaText.setText(Integer.toString(mActivity.getPizza()));
        mPowerUpMarketScene.attachChild(powerUpMarketTotalPizzaText);

        powerUpMarketTotalPizzaSprite.setX((SCREEN_WIDTH - powerUpMarketTotalPizzaSprite.getWidth() - powerUpMarketTotalPizzaText.getWidth()) / 2); //adjust margins
        powerUpMarketTotalPizzaText.setX(powerUpMarketTotalPizzaSprite.getX() + powerUpMarketTotalPizzaSprite.getWidth());



        final Sprite popUpBoard = new Sprite(boardX, boardY, mResourceManager.mTutorialBoardTextureRegion, mVertexBufferObjectManager);
        mPowerUpMarketScene.attachChild(popUpBoard);


        final float titleTextY = boardY + 20;

        final Text titleText = (new Text(0, titleTextY, mResourceManager.mFont5, "Power Up Market", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        titleText.setX((SCREEN_WIDTH - titleText.getWidth()) / 2);
        mPowerUpMarketScene.attachChild(titleText);


        final float powerUpNameY = (titleTextY + titleText.getHeight() + powerUpY) / 2 - mResourceManager.mFont2.getLineHeight() / 2;
        final float powerUpPricePizzaSpriteY = powerUpNameY + mResourceManager.mFont2.getLineHeight();
        final float powerUpPriceY = powerUpPricePizzaSpriteY + mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2 - mResourceManager.mFont3.getLineHeight() / 2;


        final String initText = "0123456789 qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

        final Text powerUpNameText = (new Text(0, powerUpNameY, mResourceManager.mFont2, initText, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        powerUpNameText.setText(POWER_UPS[currentPowerUp] + " (lvl " + (mActivity.getPowerUpLevel(currentPowerUp) + 1) + ")");
        mPowerUpMarketScene.attachChild(powerUpNameText);

        final Sprite powerUpPricePizzaSprite = new Sprite(0, powerUpPricePizzaSpriteY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mPowerUpMarketScene.attachChild(powerUpPricePizzaSprite);
        powerUpPricePizzaSprite.setScale(.5f);

        final Text powerUpPriceText = (new Text(0, powerUpPriceY, mResourceManager.mFont3, initText, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        if(mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
            powerUpPriceText.setText(Integer.toString(POWER_UP_PRICES[currentPowerUp][mActivity.getPowerUpLevel(currentPowerUp)]));
        } else {
            powerUpPriceText.setText("NA");
        }
        mPowerUpMarketScene.attachChild(powerUpPriceText);

        final Text levelText = (new Text(0, levelTextY, mResourceManager.mFont3, "Upgrade for +2 Seconds", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        if(mActivity.getPowerUpLevel(currentPowerUp) > 0 && mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
            levelText.setVisible(true);
        } else {
            levelText.setVisible(false);
        }
        mPowerUpMarketScene.attachChild(levelText);

        levelText.setX((SCREEN_WIDTH - levelText.getWidth()) / 2);
        powerUpNameText.setX((SCREEN_WIDTH - powerUpNameText.getWidth()) / 2);
        powerUpPricePizzaSprite.setX((SCREEN_WIDTH - powerUpPricePizzaSprite.getWidth() - powerUpPriceText.getWidth()) / 2); //adjust margins
        powerUpPriceText.setX(powerUpPricePizzaSprite.getX() + powerUpPricePizzaSprite.getWidth());


        final TiledSprite logoutButton = new TiledSprite(logoutButtonX, logoutButtonY, mResourceManager.mLogoutButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    mActivity.logout();
                }
                return true;
            }
        };
        logoutButton.setCurrentTileIndex(0);
        logoutButton.setScale(0.75f);
        mPowerUpMarketScene.registerTouchArea(logoutButton);
        mPowerUpMarketScene.attachChild(logoutButton);

        final TiledSprite backButton = new TiledSprite(backButtonX, backButtonY, mResourceManager.mBackButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                }
                return true;
            }
        };
        backButton.setCurrentTileIndex(0);
        backButton.setScale(0.75f);
        mPowerUpMarketScene.registerTouchArea(backButton);
        mPowerUpMarketScene.attachChild(backButton);

        final TiledSprite birdsButton = new TiledSprite(birdsButtonX, birdsButtonY, mResourceManager.mBirdsMarketButtonTextureRegion, mVertexBufferObjectManager) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    openBirdsMarketScene();
                }
                return true;
            }
        };

        birdsButton.setCurrentTileIndex(0);
        birdsButton.setScale(0.75f);
        mPowerUpMarketScene.registerTouchArea(birdsButton);
        mPowerUpMarketScene.attachChild(birdsButton);

        final TiledSprite powerUpsButton = new TiledSprite(powerUpsButtonX, powerUpsButtonY, mResourceManager.mPowerUpsMarketButtonTextureRegion, mVertexBufferObjectManager);
        powerUpsButton.setCurrentTileIndex(1);
        powerUpsButton.setScale(0.75f);
        mPowerUpMarketScene.attachChild(powerUpsButton);


        final TiledSprite getMoreButton = new TiledSprite(getMoreButtonX, getMoreButtonY, mResourceManager.mGetMorePizzaButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    openPizzaMarketScene();
                }
                return true;
            }
        };
        getMoreButton.setCurrentTileIndex(0);
        getMoreButton.setScale(0.75f);
        mPowerUpMarketScene.registerTouchArea(getMoreButton);
        mPowerUpMarketScene.attachChild(getMoreButton);


        final TiledSprite powerUpsSprite = new TiledSprite(powerUpX, powerUpY, mResourceManager.mPowerUpPurchasesTextureRegion, mVertexBufferObjectManager);
        powerUpsSprite.setCurrentTileIndex(currentPowerUp);
        mPowerUpMarketScene.attachChild(powerUpsSprite);


        final TiledSprite purchaseButton = new TiledSprite(purchaseButtonX, purchaseButtonY, mResourceManager.mPurchaseButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                //if you have enough pizza and the power-up is not maxed out
                if (mActivity.getPowerUpLevel(currentPowerUp) < (POWER_UP_PRICES[currentPowerUp].length) && mActivity.getPizza() >= POWER_UP_PRICES[currentPowerUp][mActivity.getPowerUpLevel(currentPowerUp)]) {
                    if (pSceneTouchEvent.isActionDown()) {
                        if (getCurrentTileIndex() == 0) {
                            setCurrentTileIndex(2);
                        } else if (getCurrentTileIndex() == 3) {
                            setCurrentTileIndex(5);
                        }
                    }

                    if (pSceneTouchEvent.isActionUp()) {
                        if (getCurrentTileIndex() == 2) {
                            setCurrentTileIndex(3);
                        } else if (getCurrentTileIndex() == 5 && mActivity.getPowerUpLevel(currentPowerUp) == (POWER_UP_PRICES[currentPowerUp].length - 1)) {
                            setCurrentTileIndex(4); //max out
                        }
                        mActivity.subtractPizza(POWER_UP_PRICES[currentPowerUp][mActivity.getPowerUpLevel(currentPowerUp)]);
                        mActivity.setPowerUpLevel(currentPowerUp, mActivity.getPowerUpLevel(currentPowerUp) + 1);

                            //update name and price in UI
                        if(mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
                            powerUpNameText.setText(POWER_UPS[currentPowerUp] + " (lvl " + (mActivity.getPowerUpLevel(currentPowerUp) + 1) + ")");
                            powerUpPriceText.setText(Integer.toString(POWER_UP_PRICES[currentPowerUp][mActivity.getPowerUpLevel(currentPowerUp)]));
                        } else {
                            powerUpNameText.setText(POWER_UPS[currentPowerUp]);
                            powerUpPriceText.setText("NA");
                        }
                            //adjust margins
                        powerUpNameText.setX((SCREEN_WIDTH - powerUpNameText.getWidth()) / 2);
                        powerUpPricePizzaSprite.setX((SCREEN_WIDTH - powerUpPricePizzaSprite.getWidth() - powerUpPriceText.getWidth()) / 2);
                        powerUpPriceText.setX(powerUpPricePizzaSprite.getX() + powerUpPricePizzaSprite.getWidth());

                        if(mActivity.getPowerUpLevel(currentPowerUp) > 0 && mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
                            levelText.setVisible(true);
                        } else {
                            levelText.setVisible(false);
                        }

                        updateTotalPizzaText();
                    }
                }

                return true;

            }
        };
        if(mActivity.getPowerUpLevel(currentPowerUp) == 0) {
            purchaseButton.setCurrentTileIndex(0); //"purchase"
        } else if (mActivity.getPowerUpLevel(currentPowerUp) == POWER_UP_PRICES[currentPowerUp].length) {
            purchaseButton.setCurrentTileIndex(4); //"maxed out"
        } else {
            purchaseButton.setCurrentTileIndex(3); //"upgrade"
        }
        purchaseButton.setScale(0.75f);
        mPowerUpMarketScene.registerTouchArea(purchaseButton);
        mPowerUpMarketScene.attachChild(purchaseButton);

        final TiledSprite arrowLeftButton = new TiledSprite(arrowLeftX, arrowButtonsY, mResourceManager.mArrowLeftButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    if(currentPowerUp != 0) {
                        currentPowerUp--;
                    } else {
                        currentPowerUp = POWER_UPS.length - 1;
                    }
                    powerUpsSprite.setCurrentTileIndex(currentPowerUp);

                    if(mActivity.getPowerUpLevel(currentPowerUp) == 0) {
                        purchaseButton.setCurrentTileIndex(0); //"purchase"
                    } else if (mActivity.getPowerUpLevel(currentPowerUp) == POWER_UP_PRICES[currentPowerUp].length) {
                        purchaseButton.setCurrentTileIndex(4); //"maxed out"
                    } else {
                        purchaseButton.setCurrentTileIndex(3); //"upgrade"
                    }

                    //update name and price in UI
                    if(mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
                        powerUpNameText.setText(POWER_UPS[currentPowerUp] + " (lvl " + (mActivity.getPowerUpLevel(currentPowerUp) + 1) + ")");
                        powerUpPriceText.setText(Integer.toString(POWER_UP_PRICES[currentPowerUp][mActivity.getPowerUpLevel(currentPowerUp)]));
                    } else {
                        powerUpNameText.setText(POWER_UPS[currentPowerUp]);
                        powerUpPriceText.setText("NA");
                    }

                    powerUpNameText.setX((SCREEN_WIDTH - powerUpNameText.getWidth()) / 2);
                    powerUpPricePizzaSprite.setX((SCREEN_WIDTH - powerUpPricePizzaSprite.getWidth() - powerUpPriceText.getWidth()) / 2); //adjust margins
                    powerUpPriceText.setX(powerUpPricePizzaSprite.getX() + powerUpPricePizzaSprite.getWidth());

                    if(mActivity.getPowerUpLevel(currentPowerUp) > 0 && mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
                        levelText.setVisible(true);
                    } else {
                        levelText.setVisible(false);
                    }

                }
                return true;
            }
        };
        arrowLeftButton.setCurrentTileIndex(0);
        arrowLeftButton.setScale(0.75f);
        mPowerUpMarketScene.registerTouchArea(arrowLeftButton);
        mPowerUpMarketScene.attachChild(arrowLeftButton);

        final TiledSprite arrowRightButton = new TiledSprite(arrowRightX, arrowButtonsY, mResourceManager.mArrowRightButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    if(currentPowerUp != POWER_UPS.length - 1) {
                        currentPowerUp++;
                    } else {
                        currentPowerUp = 0;
                    }
                    powerUpsSprite.setCurrentTileIndex(currentPowerUp);

                    if(mActivity.getPowerUpLevel(currentPowerUp) == 0) {
                        purchaseButton.setCurrentTileIndex(0); //"purchase"
                    } else if (mActivity.getPowerUpLevel(currentPowerUp) == POWER_UP_PRICES[currentPowerUp].length) {
                        purchaseButton.setCurrentTileIndex(4); //"maxed out"
                    } else {
                        purchaseButton.setCurrentTileIndex(3); //"upgrade"
                    }

                    //update name and price in UI
                    if(mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
                        powerUpNameText.setText(POWER_UPS[currentPowerUp] + " (lvl " + (mActivity.getPowerUpLevel(currentPowerUp) + 1) + ")");
                        powerUpPriceText.setText(Integer.toString(POWER_UP_PRICES[currentPowerUp][mActivity.getPowerUpLevel(currentPowerUp)]));
                    } else {
                        powerUpNameText.setText(POWER_UPS[currentPowerUp]);
                        powerUpPriceText.setText("NA");
                    }

                    powerUpNameText.setX((SCREEN_WIDTH - powerUpNameText.getWidth()) / 2);
                    powerUpPricePizzaSprite.setX((SCREEN_WIDTH - powerUpPricePizzaSprite.getWidth() - powerUpPriceText.getWidth()) / 2); //adjust margins
                    powerUpPriceText.setX(powerUpPricePizzaSprite.getX() + powerUpPricePizzaSprite.getWidth());

                    if(mActivity.getPowerUpLevel(currentPowerUp) > 0 && mActivity.getPowerUpLevel(currentPowerUp) < POWER_UP_PRICES[currentPowerUp].length) {
                        levelText.setVisible(true);
                    } else {
                        levelText.setVisible(false);
                    }

                }
                return true;
            }
        };
        arrowRightButton.setCurrentTileIndex(0);
        arrowRightButton.setScale(0.75f);
        mPowerUpMarketScene.registerTouchArea(arrowRightButton);
        mPowerUpMarketScene.attachChild(arrowRightButton);

    }


    private void createBirdMarketScene() {


        mBirdMarketScene = new CameraScene(mCamera);
        mBirdMarketScene.setBackgroundEnabled(false);

        final float backButtonX = 0;
        final float backButtonY = -5;

        final float logoutButtonX = SCREEN_WIDTH - mResourceManager.mLogoutButtonTextureRegion.getWidth();
        final float logoutButtonY = -5;

        final float boardX = (SCREEN_WIDTH - mResourceManager.mTutorialBoardTextureRegion.getWidth()) / 2;
        final float boardY = backButtonY + mResourceManager.mBackButtonTextureRegion.getHeight();

        final float birdsButtonX = SCREEN_WIDTH/2 - mResourceManager.mBirdsMarketButtonTextureRegion.getWidth();
        final float birdsButtonY = boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight();

        final float powerUpsButtonX = SCREEN_WIDTH/2;
        final float powerUpsButtonY = birdsButtonY;

        final float arrowLeftX = boardX;
        final float arrowButtonsY = (2 * boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight()) / 2 - mResourceManager.mArrowLeftButtonTextureRegion.getHeight() / 2;

        final float arrowRightX = boardX + mResourceManager.mTutorialBoardTextureRegion.getWidth() - mResourceManager.mArrowLeftButtonTextureRegion.getWidth();

        final float birdX = (SCREEN_WIDTH - mResourceManager.mMarketBirdsTextureRegion.getWidth()) / 2;
        final float birdY = (2 * boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight()) / 2 - mResourceManager.mMarketBirdsTextureRegion.getHeight() / 2;

        final float selectButtonX = SCREEN_WIDTH/2 - mResourceManager.mSelectButtonTextureRegion.getWidth();
        final float selectButtonY = arrowButtonsY + mResourceManager.mArrowLeftButtonTextureRegion.getHeight();

        final float purchaseButtonX = SCREEN_WIDTH/2;
        final float purchaseButtonY = selectButtonY;

        final float getMoreButtonX = (SCREEN_WIDTH - mResourceManager.mGetMorePizzaButtonTextureRegion.getWidth()) / 2;
        final float getMoreButtonY = boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight() - mResourceManager.mGetMorePizzaButtonTextureRegion.getHeight();

        //final float currentUserTextY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2;

        final float titleTextY = boardY + 20;

        final float totalPizzaY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2 - mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2;
        final float totalPizzaTextY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2 - mResourceManager.mFont3.getLineHeight() / 2;


        final Sprite popUpBoard = new Sprite(boardX, boardY, mResourceManager.mTutorialBoardTextureRegion, mVertexBufferObjectManager);
        mBirdMarketScene.attachChild(popUpBoard);


        final Text titleText = (new Text(0, titleTextY, mResourceManager.mFont5, "Bird Market", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        titleText.setX((SCREEN_WIDTH - titleText.getWidth()) / 2);
        mBirdMarketScene.attachChild(titleText);

        birdMarketTotalPizzaSprite = new Sprite(0, totalPizzaY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mBirdMarketScene.attachChild(birdMarketTotalPizzaSprite);
        birdMarketTotalPizzaSprite.setScale(.5f);

        birdMarketTotalPizzaText = (new Text(0, totalPizzaTextY, mResourceManager.mFont3, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        birdMarketTotalPizzaText.setText(Integer.toString(mActivity.getPizza()));
        mBirdMarketScene.attachChild(birdMarketTotalPizzaText);

        birdMarketTotalPizzaSprite.setX((SCREEN_WIDTH - birdMarketTotalPizzaSprite.getWidth() - birdMarketTotalPizzaText.getWidth()) / 2); //adjust margins
        birdMarketTotalPizzaText.setX(birdMarketTotalPizzaSprite.getX() + birdMarketTotalPizzaSprite.getWidth());


        final float birdNameY = (titleTextY + titleText.getHeight() + birdY) / 2 - mResourceManager.mFont2.getLineHeight() / 2;
        final float birdPricePizzaSpriteY = birdNameY + mResourceManager.mFont2.getLineHeight();
        final float birdPriceY = birdPricePizzaSpriteY + mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2 - mResourceManager.mFont3.getLineHeight() / 2;

        final String initText = "0123456789 qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

        final Text birdNameText = (new Text(0, birdNameY, mResourceManager.mFont2, initText, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        birdNameText.setText(BIRD_NAMES[currentBird]);
        mBirdMarketScene.attachChild(birdNameText);

        final Sprite birdPricePizzaSprite = new Sprite(0, birdPricePizzaSpriteY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mBirdMarketScene.attachChild(birdPricePizzaSprite);
        birdPricePizzaSprite.setScale(.5f);

        final Text birdPriceText = (new Text(0, birdPriceY, mResourceManager.mFont3, initText, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        birdPriceText.setText(Integer.toString(BIRD_PRICES[currentBird]));
        mBirdMarketScene.attachChild(birdPriceText);

        birdNameText.setX((SCREEN_WIDTH - birdNameText.getWidth()) / 2);
        birdPricePizzaSprite.setX((SCREEN_WIDTH - birdPricePizzaSprite.getWidth() - birdPriceText.getWidth()) / 2); //adjust margins
        birdPriceText.setX(birdPricePizzaSprite.getX() + birdPricePizzaSprite.getWidth());




        final TiledSprite logoutButton = new TiledSprite(logoutButtonX, logoutButtonY, mResourceManager.mLogoutButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    //log the user out and set max score = 0. They can get it back by logging in again
                    mActivity.logout();
                }
                return true;
            }
        };
        logoutButton.setCurrentTileIndex(0);
        logoutButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(logoutButton);
        mBirdMarketScene.attachChild(logoutButton);

        final TiledSprite backButton = new TiledSprite(backButtonX, backButtonY, mResourceManager.mBackButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                }
                return true;
            }
        };
        backButton.setCurrentTileIndex(0);
        backButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(backButton);
        mBirdMarketScene.attachChild(backButton);

        final TiledSprite birdsButton = new TiledSprite(birdsButtonX, birdsButtonY, mResourceManager.mBirdsMarketButtonTextureRegion, mVertexBufferObjectManager);
        birdsButton.setCurrentTileIndex(1);
        birdsButton.setScale(0.75f);
        mBirdMarketScene.attachChild(birdsButton);

        final TiledSprite powerUpsButton = new TiledSprite(powerUpsButtonX, powerUpsButtonY, mResourceManager.mPowerUpsMarketButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    openPowerUpsMarketScene();
                }
                return true;
            }
        };
        powerUpsButton.setCurrentTileIndex(0);
        powerUpsButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(powerUpsButton);
        mBirdMarketScene.attachChild(powerUpsButton);

        final TiledSprite birdsSprite = new TiledSprite(birdX, birdY, mResourceManager.mMarketBirdsTextureRegion, mVertexBufferObjectManager);
        birdsSprite.setCurrentTileIndex(currentBird);
        mBirdMarketScene.attachChild(birdsSprite);


        final TiledSprite selectButton = new TiledSprite(selectButtonX, selectButtonY, mResourceManager.mSelectButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if(isVisible() && getCurrentTileIndex() == 0) {
                    if (pSceneTouchEvent.isActionDown()) {
                        setCurrentTileIndex(1);
                        mActivity.setSelectedBird(currentBird);

                    }
                }
                return true;
            }
        };
        selectButton.setCurrentTileIndex(1);
        selectButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(selectButton);
        mBirdMarketScene.attachChild(selectButton);


        final TiledSprite purchaseButton = new TiledSprite(purchaseButtonX, purchaseButtonY, mResourceManager.mPurchaseButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if(getCurrentTileIndex() == 0 && mActivity.getPizza() >= BIRD_PRICES[currentBird]) {
                    if (pSceneTouchEvent.isActionDown()) {
                        setCurrentTileIndex(1);
                        Log.i("current bird", "" + currentBird);
                        selectButton.setCurrentTileIndex(1);
                        selectButton.setVisible(true);
                        mActivity.setSelectedBird(currentBird);
                        int pizza = mActivity.getPizza();
                        pizza -= BIRD_PRICES[currentBird];
                        mActivity.setPizza(pizza);
                        currentUser.put("pizzaCollected", pizza);
                        currentUser.put("hasBird" + currentBird, true);

                        mActivity.saveCurrentUser();

                        birdsSprite.setCurrentTileIndex(currentBird);

                        updateTotalPizzaText();
                    }
                }
                return true;

            }
        };
        purchaseButton.setCurrentTileIndex(1);
        purchaseButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(purchaseButton);
        mBirdMarketScene.attachChild(purchaseButton);

        final TiledSprite arrowLeftButton = new TiledSprite(arrowLeftX, arrowButtonsY, mResourceManager.mArrowLeftButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    if(currentBird != 0) {
                        currentBird--;
                    } else {
                        currentBird = numBirds - 1;
                    }
                    if(currentUser.getBoolean("hasBird" + currentBird)) {
                        birdsSprite.setCurrentTileIndex(currentBird);
                        selectButton.setVisible(true);
                        purchaseButton.setCurrentTileIndex(1);
                    } else {
                        birdsSprite.setCurrentTileIndex(MYSTERY_INDEX);
                        selectButton.setVisible(false);
                        purchaseButton.setCurrentTileIndex(0);
                    }
                    if(currentBird == mActivity.getSelectedBird()) {
                        selectButton.setCurrentTileIndex(1);
                    } else {
                        selectButton.setCurrentTileIndex(0);
                    }

                    birdNameText.setText(BIRD_NAMES[currentBird]);
                    birdPriceText.setText(String.valueOf(BIRD_PRICES[currentBird]));

                    birdNameText.setX((SCREEN_WIDTH - birdNameText.getWidth()) / 2);
                    birdPricePizzaSprite.setX((SCREEN_WIDTH - birdPricePizzaSprite.getWidth() - birdPriceText.getWidth()) / 2); //adjust margins
                    birdPriceText.setX(birdPricePizzaSprite.getX() + birdPricePizzaSprite.getWidth());
                }
                return true;
            }
        };
        arrowLeftButton.setCurrentTileIndex(0);
        arrowLeftButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(arrowLeftButton);
        mBirdMarketScene.attachChild(arrowLeftButton);

        final TiledSprite arrowRightButton = new TiledSprite(arrowRightX, arrowButtonsY, mResourceManager.mArrowRightButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    if(currentBird != numBirds - 1) {
                        currentBird++;
                    } else {
                        currentBird = 0;
                    }
                    if(currentUser.getBoolean("hasBird" + currentBird)) {
                        birdsSprite.setCurrentTileIndex(currentBird);
                        selectButton.setVisible(true);
                        purchaseButton.setCurrentTileIndex(1);
                    } else {
                        birdsSprite.setCurrentTileIndex(MYSTERY_INDEX);
                        selectButton.setVisible(false);
                        purchaseButton.setCurrentTileIndex(0);
                    }
                    if(currentBird == mActivity.getSelectedBird()) {
                        selectButton.setCurrentTileIndex(1);
                    } else {
                        selectButton.setCurrentTileIndex(0);
                    }

                    birdNameText.setText(BIRD_NAMES[currentBird]);
                    birdPriceText.setText(String.valueOf(BIRD_PRICES[currentBird]));

                    birdNameText.setX((SCREEN_WIDTH - birdNameText.getWidth()) / 2);
                    birdPricePizzaSprite.setX((SCREEN_WIDTH - birdPricePizzaSprite.getWidth() - birdPriceText.getWidth()) / 2); //adjust margins
                    birdPriceText.setX(birdPricePizzaSprite.getX() + birdPricePizzaSprite.getWidth());
                }
                return true;
            }
        };
        arrowRightButton.setCurrentTileIndex(0);
        arrowRightButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(arrowRightButton);
        mBirdMarketScene.attachChild(arrowRightButton);


        final TiledSprite getMoreButton = new TiledSprite(getMoreButtonX, getMoreButtonY, mResourceManager.mGetMorePizzaButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    openPizzaMarketScene();
                }
                return true;
            }
        };
        getMoreButton.setCurrentTileIndex(0);
        getMoreButton.setScale(0.75f);
        mBirdMarketScene.registerTouchArea(getMoreButton);
        mBirdMarketScene.attachChild(getMoreButton);

    }

    private void createPizzaMarketScene() {
        mPizzaMarketScene = new CameraScene(mCamera);
        mPizzaMarketScene.setBackgroundEnabled(false);

        final float backButtonX = 0;
        final float backButtonY = -5;

        final float logoutButtonX = SCREEN_WIDTH - mResourceManager.mLogoutButtonTextureRegion.getWidth();
        final float logoutButtonY = -5;

        final float boardX = (SCREEN_WIDTH - mResourceManager.mTutorialBoardTextureRegion.getWidth()) / 2;
        final float boardY = backButtonY + mResourceManager.mBackButtonTextureRegion.getHeight();

        final float birdsButtonX = SCREEN_WIDTH/2 - mResourceManager.mBirdsMarketButtonTextureRegion.getWidth();
        final float birdsButtonY = boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight();

        final float powerUpsButtonX = SCREEN_WIDTH/2;
        final float powerUpsButtonY = birdsButtonY;

        final float arrowLeftX = boardX;
        final float arrowButtonsY = (2 * boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight()) / 2 - mResourceManager.mArrowLeftButtonTextureRegion.getHeight() / 2;

        final float arrowRightX = boardX + mResourceManager.mTutorialBoardTextureRegion.getWidth() - mResourceManager.mArrowLeftButtonTextureRegion.getWidth();

        final float pizzaPurchasesX = (SCREEN_WIDTH - mResourceManager.mPizzaPurchasesTextureRegion.getWidth()) / 2;
        final float pizzaPurchasesY = (2 * boardY + mResourceManager.mTutorialBoardTextureRegion.getHeight()) / 2 - mResourceManager.mPizzaPurchasesTextureRegion.getHeight() / 2;

        final float purchaseButtonX = (SCREEN_WIDTH - mResourceManager.mPurchaseButtonTextureRegion.getWidth())/2;;
        final float purchaseButtonY = arrowButtonsY + mResourceManager.mArrowLeftButtonTextureRegion.getHeight();

        //final float currentUserTextY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2;

        final float titleTextY = boardY + 20;

        final float totalPizzaY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2 - mResourceManager.mCollectablePizzaTextureRegion.getHeight() / 2;
        final float totalPizzaTextY = (backButtonY * 2 + mResourceManager.mBackButtonTextureRegion.getHeight()) / 2 - mResourceManager.mFont3.getLineHeight() / 2;


        final Sprite popUpBoard = new Sprite(boardX, boardY, mResourceManager.mTutorialBoardTextureRegion, mVertexBufferObjectManager);
        mPizzaMarketScene.attachChild(popUpBoard);


        final Text titleText = (new Text(0, titleTextY, mResourceManager.mFont5, "Get More Pizza", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        titleText.setX((SCREEN_WIDTH - titleText.getWidth()) / 2);
        mPizzaMarketScene.attachChild(titleText);

        pizzaMarketTotalPizzaSprite = new Sprite(0, totalPizzaY, mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager);
        mPizzaMarketScene.attachChild(pizzaMarketTotalPizzaSprite);
        pizzaMarketTotalPizzaSprite.setScale(.5f);

        pizzaMarketTotalPizzaText = (new Text(0, totalPizzaTextY, mResourceManager.mFont3, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        pizzaMarketTotalPizzaText.setText(Integer.toString(mActivity.getPizza()));
        mPizzaMarketScene.attachChild(pizzaMarketTotalPizzaText);

        pizzaMarketTotalPizzaSprite.setX((SCREEN_WIDTH - pizzaMarketTotalPizzaSprite.getWidth() - pizzaMarketTotalPizzaText.getWidth()) / 2); //adjust margins
        pizzaMarketTotalPizzaText.setX(pizzaMarketTotalPizzaSprite.getX() + pizzaMarketTotalPizzaSprite.getWidth());


        final float pizzaPurchaseTextY = (titleTextY + titleText.getHeight() + pizzaPurchasesY) / 2 - mResourceManager.mFont2.getLineHeight() / 2;
        final float pizzaPurchasePriceTextY = (pizzaPurchaseTextY + mResourceManager.mFont2.getLineHeight() + pizzaPurchasesY) / 2 - mResourceManager.mFont3.getLineHeight() / 2;

        final String initText = "0123456789 qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM$";

        pizzaPurchaseText = (new Text(0, pizzaPurchaseTextY, mResourceManager.mFont2, initText, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        pizzaPurchaseText.setText(PIZZA_PURCHASES[currentPizzaPurchase]);
        mPizzaMarketScene.attachChild(pizzaPurchaseText);

        pizzaPurchasePriceText = (new Text(0, pizzaPurchasePriceTextY, mResourceManager.mFont3, initText, new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager));
        mPizzaMarketScene.attachChild(pizzaPurchasePriceText);

        pizzaPurchaseText.setX((SCREEN_WIDTH - pizzaPurchaseText.getWidth()) / 2);
        pizzaPurchasePriceText.setX((SCREEN_WIDTH - pizzaPurchasePriceText.getWidth()) / 2);

        final TiledSprite logoutButton = new TiledSprite(logoutButtonX, logoutButtonY, mResourceManager.mLogoutButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    //log the user out and set max score = 0. They can get it back by logging in again
                    mActivity.logout();
                }
                return true;
            }
        };
        logoutButton.setCurrentTileIndex(0);
        logoutButton.setScale(0.75f);
        mPizzaMarketScene.registerTouchArea(logoutButton);
        mPizzaMarketScene.attachChild(logoutButton);

        final TiledSprite backButton = new TiledSprite(backButtonX, backButtonY, mResourceManager.mBackButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
                }
                return true;
            }
        };
        backButton.setCurrentTileIndex(0);
        backButton.setScale(0.75f);
        mPizzaMarketScene.registerTouchArea(backButton);
        mPizzaMarketScene.attachChild(backButton);

        final TiledSprite birdsButton = new TiledSprite(birdsButtonX, birdsButtonY, mResourceManager.mBirdsMarketButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    openBirdsMarketScene();
                    pizzaMarketSceneOpen = false;
                }
                return true;
            }
        };
        birdsButton.setCurrentTileIndex(0);
        birdsButton.setScale(0.75f);
        mPizzaMarketScene.registerTouchArea(birdsButton);
        mPizzaMarketScene.attachChild(birdsButton);

        final TiledSprite powerUpsButton = new TiledSprite(powerUpsButtonX, powerUpsButtonY, mResourceManager.mPowerUpsMarketButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);

                    openPowerUpsMarketScene();
                    pizzaMarketSceneOpen = false;
                }
                return true;
            }
        };
        powerUpsButton.setCurrentTileIndex(0);
        powerUpsButton.setScale(0.75f);
        mPizzaMarketScene.registerTouchArea(powerUpsButton);
        mPizzaMarketScene.attachChild(powerUpsButton);

        final TiledSprite pizzaPurchasesSprite = new TiledSprite(pizzaPurchasesX, pizzaPurchasesY, mResourceManager.mPizzaPurchasesTextureRegion, mVertexBufferObjectManager);
        pizzaPurchasesSprite.setCurrentTileIndex(currentPizzaPurchase);
        mPizzaMarketScene.attachChild(pizzaPurchasesSprite);


        final TiledSprite purchaseButton = new TiledSprite(purchaseButtonX, purchaseButtonY, mResourceManager.mPurchaseButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    if (pSceneTouchEvent.isActionDown()) {
                        setCurrentTileIndex(2);
                    }
                    if(pSceneTouchEvent.isActionUp()) {
                        setCurrentTileIndex(0);
                        mActivity.purchasePizza(currentPizzaPurchase);
                    }

                return true;

            }
        };
        purchaseButton.setCurrentTileIndex(0);
        purchaseButton.setScale(0.75f);
        mPizzaMarketScene.registerTouchArea(purchaseButton);
        mPizzaMarketScene.attachChild(purchaseButton);

        final TiledSprite arrowLeftButton = new TiledSprite(arrowLeftX, arrowButtonsY, mResourceManager.mArrowLeftButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    if(currentPizzaPurchase != 0) {
                        currentPizzaPurchase--;
                    } else {
                        currentPizzaPurchase = PIZZA_PURCHASES.length - 1;
                    }
                    pizzaPurchasesSprite.setCurrentTileIndex(currentPizzaPurchase);

                    pizzaPurchaseText.setText(PIZZA_PURCHASES[currentPizzaPurchase]);
                    pizzaPurchasePriceText.setText(mPizzaPurchasePrices[currentPizzaPurchase]);

                    pizzaPurchaseText.setX((SCREEN_WIDTH - pizzaPurchaseText.getWidth()) / 2);
                    pizzaPurchasePriceText.setX((SCREEN_WIDTH - pizzaPurchasePriceText.getWidth()) / 2);
                }
                return true;
            }
        };
        arrowLeftButton.setCurrentTileIndex(0);
        arrowLeftButton.setScale(0.75f);
        mPizzaMarketScene.registerTouchArea(arrowLeftButton);
        mPizzaMarketScene.attachChild(arrowLeftButton);

        final TiledSprite arrowRightButton = new TiledSprite(arrowRightX, arrowButtonsY, mResourceManager.mArrowRightButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    if(currentPizzaPurchase != PIZZA_PURCHASES.length - 1) {
                        currentPizzaPurchase++;
                    } else {
                        currentPizzaPurchase = 0;
                    }
                    pizzaPurchasesSprite.setCurrentTileIndex(currentPizzaPurchase);

                    pizzaPurchaseText.setText(PIZZA_PURCHASES[currentPizzaPurchase]);
                    pizzaPurchasePriceText.setText(mPizzaPurchasePrices[currentPizzaPurchase]);

                    pizzaPurchaseText.setX((SCREEN_WIDTH - pizzaPurchaseText.getWidth()) / 2);
                    pizzaPurchasePriceText.setX((SCREEN_WIDTH - pizzaPurchasePriceText.getWidth()) / 2);
                }
                return true;
            }
        };
        arrowRightButton.setCurrentTileIndex(0);
        arrowRightButton.setScale(0.75f);
        mPizzaMarketScene.registerTouchArea(arrowRightButton);
        mPizzaMarketScene.attachChild(arrowRightButton);

    }

    private void openBirdsMarketScene() {
        clearChildScene();
        setChildScene(mBirdMarketScene, false, true, true);
        currentPizzaPurchase = 0;

        birdMarketSceneOpen = true;
        powerUpMarketSceneOpen = false;

    }

    private void openPowerUpsMarketScene() {
        clearChildScene();
        setChildScene(mPowerUpMarketScene, false, true, true);
        currentPizzaPurchase = 0;

        birdMarketSceneOpen = false;
        powerUpMarketSceneOpen = true;

    }

    private void openPizzaMarketScene() {
        clearChildScene();
        pizzaPurchaseText.setText(PIZZA_PURCHASES[currentPizzaPurchase]);
        pizzaPurchasePriceText.setText(mPizzaPurchasePrices[currentPizzaPurchase]);

        pizzaPurchaseText.setX((SCREEN_WIDTH - pizzaPurchaseText.getWidth()) / 2);
        pizzaPurchasePriceText.setX((SCREEN_WIDTH - pizzaPurchasePriceText.getWidth()) / 2);
        setChildScene(mPizzaMarketScene, false, true, true);

        pizzaMarketSceneOpen = true;

    }

    public void updateTotalPizzaText() {
        int pizza = mActivity.getPizza();

        birdMarketTotalPizzaText.setText(Integer.toString(pizza));
        birdMarketTotalPizzaSprite.setX((SCREEN_WIDTH - birdMarketTotalPizzaSprite.getWidth() - birdMarketTotalPizzaText.getWidth()) / 2); //adjust margins
        birdMarketTotalPizzaText.setX(birdMarketTotalPizzaSprite.getX() + birdMarketTotalPizzaSprite.getWidth());

        pizzaMarketTotalPizzaText.setText(Integer.toString(pizza));
        pizzaMarketTotalPizzaSprite.setX((SCREEN_WIDTH - birdMarketTotalPizzaSprite.getWidth() - birdMarketTotalPizzaText.getWidth()) / 2); //adjust margins
        pizzaMarketTotalPizzaText.setX(birdMarketTotalPizzaSprite.getX() + birdMarketTotalPizzaSprite.getWidth());

        powerUpMarketTotalPizzaText.setText(Integer.toString(pizza));
        powerUpMarketTotalPizzaSprite.setX((SCREEN_WIDTH - birdMarketTotalPizzaSprite.getWidth() - birdMarketTotalPizzaText.getWidth()) / 2); //adjust margins
        powerUpMarketTotalPizzaText.setX(birdMarketTotalPizzaSprite.getX() + birdMarketTotalPizzaSprite.getWidth());
    }

    private void createLoginScene() {
        mLoginScene = new CameraScene(mCamera);
        mLoginScene.setBackgroundEnabled(false);

        final float boardX = (SCREEN_WIDTH - mResourceManager.mTutorialBoardTextureRegion.getWidth()) / 2;
        final float boardY = boardX;

        final float loginTextX = (SCREEN_WIDTH - mResourceManager.mLeaderboardLoginTextTextureRegion.getWidth()) / 2;
        final float loginTextY = boardY + 10;

        final float signUpButtonX = (SCREEN_WIDTH - mResourceManager.mSignUpButtonTextureRegion.getWidth()) / 2;
        final float signUpButtonY = loginTextY + mResourceManager.mLeaderboardLoginTextTextureRegion.getHeight();

        final float loginButtonX = signUpButtonX;
        final float loginButtonY = signUpButtonY + mResourceManager.mLoginButtonTextureRegion.getHeight();

        final Sprite popUpBoard = new Sprite(boardX, boardY, mResourceManager.mTutorialBoardTextureRegion, mVertexBufferObjectManager);
        mLoginScene.attachChild(popUpBoard);

        final Sprite loginText = new Sprite(loginTextX, loginTextY, mResourceManager.mMarketLoginTextTextureRegion, mVertexBufferObjectManager);
        mLoginScene.attachChild(loginText);

        final TiledSprite signUpButton = new TiledSprite(signUpButtonX, signUpButtonY, mResourceManager.mSignUpButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    mActivity.openSignUpActivity();
                }
                return true;
            }
        };
        signUpButton.setCurrentTileIndex(0);
        mLoginScene.registerTouchArea(signUpButton);
        mLoginScene.attachChild(signUpButton);

        final TiledSprite loginButton = new TiledSprite(loginButtonX, loginButtonY, mResourceManager.mLoginButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    setCurrentTileIndex(1);

                }
                if (pSceneTouchEvent.isActionUp()) {
                    setCurrentTileIndex(0);
                    mActivity.openLoginActivity();
                }
                return true;
            }
        };
        loginButton.setCurrentTileIndex(0);
        mLoginScene.registerTouchArea(loginButton);
        mLoginScene.attachChild(loginButton);

    }



    @Override
    public void onBackKeyPressed() {
        if(pizzaMarketSceneOpen) {
            if(powerUpMarketSceneOpen) {
                pizzaMarketSceneOpen = false;
                openPowerUpsMarketScene();
            } else if (birdMarketSceneOpen) {
                pizzaMarketSceneOpen = false;
                openBirdsMarketScene();
            }
            currentPizzaPurchase = 0;
        } else {
            mSceneManager.setScene(SceneManager.SceneType.SCENE_MENU);
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
