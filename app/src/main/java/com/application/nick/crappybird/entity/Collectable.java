package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.GameActivity;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/5/2015.
 */
public abstract class Collectable extends AnimatedSprite {

    private static final float DEMO_VELOCITY = 150.0f;
    private static final float VELOCITY_RANGE = 100.0f;
    private static final float DEMO_POSITION = 1.5f* GameActivity.CAMERA_WIDTH;

    private float mGroundY;
    private float mWidth;
    private float mHeight;

    boolean passedAddXValue = false;
    boolean collected = false;

    public enum collectableType {BURGER, HAM, MELON, MUFFIN, PIZZA, TACO}

    private final PhysicsHandler mPhysicsHandler;

    public Collectable(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY, float pHeight) {
        super(DEMO_POSITION, -pHeight, pTiledTextureRegion, pVertexBufferObjectManager);

        mGroundY = pGroundY;

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);

        setVelocity(-DEMO_VELOCITY, 0);

        setCurrentTileIndex(0);

        randomizeAltitude();
        randomizeVelocity();

    }

    public void randomizeVelocity() {
        float rand = (float) Math.random() - 1;
        float deltaV = rand * VELOCITY_RANGE;
        setVelocity(-(DEMO_VELOCITY + deltaV), 0);
    }

    public void randomizeAltitude() {
        float reference = mGroundY - 3 * mHeight;
        float deltaH = ((reference - mHeight) * (float)Math.random());
        setY(reference - deltaH);
    }

    public void setVelocity(float x, float y) {
        mPhysicsHandler.setVelocity(x, y);
    }

    public void setAcceleration(float x, float y) {
        mPhysicsHandler.setAcceleration(x, y);
    }

    @Override
    public boolean collidesWith(IShape pOtherShape) {
        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float collectableLeft = this.getX();
        float collectableRight = collectableLeft + this.getWidth();
        float collectableTop = this.getY();
        float collectableBottom = collectableTop + this.getHeight();

        if ((spriteRight > collectableLeft && spriteLeft < collectableRight) && (spriteTop < collectableBottom && spriteBottom > collectableTop)) {
            return true;
        }
        return false; //super.collidesWith(pOtherShape);
    }

    public void die() {
        unregisterUpdateHandler(mPhysicsHandler);
    }

    public void alive() {
        registerUpdateHandler(mPhysicsHandler);
    }

    @Override
    public void reset() {
        super.reset();
        setX(DEMO_POSITION);
        passedAddXValue = false;
        randomizeAltitude();
        randomizeVelocity();
        collected = false;
    }

    /**
     *
     */
    public void passedAddXValue() {
        passedAddXValue = true;
    }

    /**
     * set collected true
     */
    public void collect() {collected = true;}

    /**
     * @return bool whether or not this has been collected
     */
    public boolean getCollected() {return collected;}
    /**
     * gets value for passedAddXValue, the boolean that says whether this object has passed the x value that makes a new
     * obstacle get created (used so that too many obstacles are not created)
     * @return
     */
    public boolean getPassedAddXValue() {
        return passedAddXValue;
    }

    public abstract collectableType getCollectableType();

}
