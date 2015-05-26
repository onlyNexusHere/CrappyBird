package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.GameActivity;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/5/2015.
 */
public class Crap extends AnimatedSprite {

    private static final float FALLING_X_VELOCITY = 32.0f;
    private static final float FALLING_Y_VELOCITY_INITIAL = 32.0f * 5;
    private static final float SCROLLING_X_VELOCITY = 150.0f;
    private static final float FALLING_Y_ACCELERATION = 9.8f * 32;
    private static final float POSITION_INITIAL = 1.1f* GameActivity.CAMERA_WIDTH;

    private float mBirdX;
    private float mCrapWidth;
    private float mCrapHeight;

    private final PhysicsHandler mPhysicsHandler;

    private boolean falling = true;

    public Crap(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(POSITION_INITIAL, -pTiledTextureRegion.getHeight(), pTiledTextureRegion, pVertexBufferObjectManager);

        mCrapWidth = pTiledTextureRegion.getWidth();
        mCrapHeight = pTiledTextureRegion.getHeight();

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);

        setVelocity(-FALLING_X_VELOCITY, FALLING_Y_VELOCITY_INITIAL);
        setAcceleration(0, FALLING_Y_ACCELERATION);
        setPosition(POSITION_INITIAL, 0);
    }

    public void setAngularVelocity(float w) { mPhysicsHandler.setAngularVelocity(w);}

    public void die() {
        unregisterUpdateHandler(mPhysicsHandler);
    }

    public void alive() {
        registerUpdateHandler(mPhysicsHandler);
    }


    @Override
    public void reset() {
        super.reset();
        falling = true;
        setScale(1);
        setCurrentTileIndex(0);
        setVelocity(-FALLING_X_VELOCITY, FALLING_Y_VELOCITY_INITIAL);
        setAcceleration(0, FALLING_Y_ACCELERATION);
        setPosition(POSITION_INITIAL, 0);
    }

    /**
     * stops crap from falling.
     * splats crap
     * if !gameOver: sets x velocity to match obstacles and ground for sidescrolling effect;
     * if gameOver: sets x velocity to 0 bec side scrolling has stopped
     */
    public void hitsGround(boolean gameOver, int selectedBird) {
        falling = false;
        setCurrentTileIndex(selectedBird * 2 + 1);
        setAcceleration(0, 0);
        setAngularVelocity(0);
        setRotation(0);
        if (!gameOver) {
            setVelocity(-SCROLLING_X_VELOCITY, 0);
        } else {
            setVelocity(0,0);
        }

    }

    public void moveForRespawn() {
        setVelocity(-SCROLLING_X_VELOCITY, 0);
    }

    /**
     * @return whether or not the crap is still falling
     */
    public boolean getFalling() {return falling;}

    /**
     * Set velocity of the crap (in px/s).
     * @param x x velocity
     * @param y y velocity
     */
    public void setVelocity(float x, float y) {

        mPhysicsHandler.setVelocity(x, y);
    }

    public float getVelocityX() {return mPhysicsHandler.getVelocityX();}

    public float getVelocityY() {return mPhysicsHandler.getVelocityY();}

    public float getAccelerationY() {return mPhysicsHandler.getAccelerationY();}

    public float getAccelerationX() {return mPhysicsHandler.getAccelerationX();}

    /**
     * Set Acceleration of the crap (in px/s).
     * @param x x accel
     * @param y y accel
     */
    public void setAcceleration(float x, float y) {

        mPhysicsHandler.setAcceleration(x, y);
    }

    /**
     * for setting a new x velocity (in px/s). Y stays the same
     * @param x
     */
    public void setXVelocity(float x) {

        mPhysicsHandler.setVelocityX(x);
    }



}
