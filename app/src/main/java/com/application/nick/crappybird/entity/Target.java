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
public class Target extends AnimatedSprite {

    public static final float SCROLL_VELOCITY = 150.0f;
    private static final float VELOCITY_RANGE = 100.0f;
    private static final float DEMO_POSITION = 1.5f* GameActivity.CAMERA_WIDTH;

    private float mGroundY;
    private float mWidth;
    private float mHeight;

    private boolean passedAddXValue = false, hit = false, slowMotion = false;


    private final PhysicsHandler mPhysicsHandler;

    public Target(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY) {
        super(DEMO_POSITION, pGroundY - pTiledTextureRegion.getHeight(), pTiledTextureRegion, pVertexBufferObjectManager);

        mGroundY = pGroundY;

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);

        setVelocity(-SCROLL_VELOCITY, 0);

        setZIndex(10); //put the targets in front of the obstacles and other sprites

        setCurrentTileIndex(0);

    }

    public void randomizeMovement() {
        int rand = (int) (Math.random() * 4);
        if (rand == 0) {
            setVelocity(-SCROLL_VELOCITY, 0);
            setCurrentTileIndex(0); //make target stationary
        } else {
            randomizeVelocity();
        }
    }

    public void randomizeVelocity() {
        float rand = (float) Math.random() - 0.5f;
        float deltaV = rand * VELOCITY_RANGE;
        setVelocity(-(SCROLL_VELOCITY + deltaV), 0);
    }

    public void setSlowMotion(boolean bool) {
        if(bool) {
            slowMotion = true;
            setXVelocity(getXVelocity() / 2);
        } else {
            slowMotion = false;
            setXVelocity(getXVelocity() * 2);
        }
    }

    public void setVelocity(float x, float y) {
        mPhysicsHandler.setVelocity(x, y);
    }

    public void setXVelocity(float x) {mPhysicsHandler.setVelocityX(x);}

    public void setYVelocity(float y) {mPhysicsHandler.setVelocityY(y);}

    public float getXVelocity() {return mPhysicsHandler.getVelocityX();}

    public void setAcceleration(float x, float y) {
        mPhysicsHandler.setAcceleration(x, y);
    }

    public void setYAcceleration(float y) {mPhysicsHandler.setAccelerationY(y);}

    @Override
    public boolean collidesWith(IShape pOtherShape) {
        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float targetLeft = this.getX();
        float targetRight = targetLeft + this.getWidth();
        float targetTop = this.getY();
        float targetBottom = targetTop + this.getHeight();

        if ((spriteRight > targetLeft && spriteLeft < targetRight) && (spriteTop < targetBottom && spriteBottom > targetTop)) {
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
        slowMotion = false;
        setX(DEMO_POSITION);
        passedAddXValue = false;
        randomizeMovement();
        hit = false;
    }

    /**
     * sets passedAddXValue boolean to true
     */
    public void passedAddXValue() {
        passedAddXValue = true;
    }

    /**
     * gets value for passedAddXValue, the boolean that says whether this object has passed the x value that makes a new
     * obstacle get created (used so that too many obstacles are not created)
     * @return
     */
    public boolean getPassedAddXValue() {
        return passedAddXValue;
    }

    public boolean getHitValue() {return hit;}

    public void hitByCrap() {
        hit = true;
        if(slowMotion) {
            setVelocity(-SCROLL_VELOCITY / 2, 0);
        } else {
            setVelocity(-SCROLL_VELOCITY, 0);
        }
    }

    public void hitByCrap(boolean gameOver) {
        hit = true;
        if(gameOver) {
            setVelocity(0,0);
        } else {
            if(slowMotion) {
                setVelocity(-SCROLL_VELOCITY / 2, 0);
            } else {
                setVelocity(-SCROLL_VELOCITY, 0);
            }
        }
    }

    public boolean getSlowMotion() {return slowMotion;}



}
