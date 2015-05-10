package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.GameActivity;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/5/2015.
 */
public class Tutorial extends TiledSprite {

    private static final float DEMO_VELOCITY = 150.0f;
    private static final float DEMO_POSITION = 1.1f* GameActivity.CAMERA_WIDTH;
    private static final float GRAVITY = 9.8f * 30;
    private static final float JUMP_VELOCITY = 5 * 30;

    private final PhysicsHandler mPhysicsHandler;

    public Tutorial(float x, float y, TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(x, y, pTiledTextureRegion, pVertexBufferObjectManager);

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);

        setVelocity(0, 0);

    }

    public float getVelocityY() {
        return mPhysicsHandler.getVelocityY();
    }

    public void setXVelocity(float x) {mPhysicsHandler.setVelocityX(x);}

    public void setYVelocity(float y) {mPhysicsHandler.setVelocityY(y);}

    public void setVelocity(float x, float y) {
        mPhysicsHandler.setVelocity(x, y);
    }

    public void setAcceleration(float x, float y) {
        mPhysicsHandler.setAcceleration(x, y);
    }

    public void die() {
        unregisterUpdateHandler(mPhysicsHandler);
    }

    public void alive() {
        registerUpdateHandler(mPhysicsHandler);
    }



}
