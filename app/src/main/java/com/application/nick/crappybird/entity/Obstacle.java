package com.application.nick.crappybird.entity;

import android.util.Log;

import com.application.nick.crappybird.GameActivity;
import com.application.nick.crappybird.scene.GameScene;
import com.google.android.gms.games.Game;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.batch.DynamicSpriteBatch;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/5/2015.
 */
public abstract class Obstacle extends AnimatedSprite {

    private static final float DEMO_VELOCITY = 150.0f;
    private static final float DEMO_POSITION = 1.1f* GameActivity.CAMERA_WIDTH;

    public enum obstacleType {HOUSE, TREE, PLANE, BALLOON, MOTHERSHIP}

    private float mObstacleY;
    private float mWidth;
    private float mHeight;

    boolean scoreAdded = false, collidedWith = false, hitByMegaCrap = false, slowMotionActivated, hyperSpeedActivated = false;


    private final PhysicsHandler mPhysicsHandler;

    public Obstacle(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pObstacleY, float pHeight) {
        super(DEMO_POSITION, pObstacleY, pTiledTextureRegion, pVertexBufferObjectManager);

        mObstacleY = pObstacleY;

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);

        setVelocity(-DEMO_VELOCITY, 0);

        setCurrentTileIndex(0);

        setHeight(pHeight);

    }

    public Obstacle(float x, float y, TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(x, y, pTiledTextureRegion, pVertexBufferObjectManager);

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);
    }

    public void setVelocity(float x, float y) {
        mPhysicsHandler.setVelocity(x, y);
    }

    public void setVelocityX(float x) {mPhysicsHandler.setVelocityX(x);}

    public void setVelocityY(float y) {mPhysicsHandler.setVelocityY(y);}

    public void setAcceleration(float x, float y) {
        mPhysicsHandler.setAcceleration(x, y);
    }

    public void setAngularVelocity(float w) { mPhysicsHandler.setAngularVelocity(w);}



    @Override
    public boolean collidesWith(IShape pOtherShape) {

        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float obstacleLeft = this.getX();
        float obstacleRight = obstacleLeft + this.getWidth();
        float obstacleTop = this.getY();
        float obstacleBottom = obstacleTop + this.getHeight();

        if ((spriteRight > obstacleLeft && spriteLeft < obstacleRight) && (spriteTop < obstacleBottom && spriteBottom > obstacleTop)) {
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


    public void setSlowMotion(boolean bool) {
        if(bool) {
            slowMotionActivated = true;
            setVelocityX(getVelocityX() / 2);
        } else {
            slowMotionActivated = false;
            setVelocityX(getVelocityX() * 2);
        }
    }

    public boolean getSlowMotion() {return slowMotionActivated;}


    public void setHyperSpeed(boolean bool) {
            if (bool) {
                if(!hyperSpeedActivated) {
                    hyperSpeedActivated = true;
                    setVelocityX(getVelocityX() - GameScene.HYPER_SPEED_VELOCITY_SHIFT);
                }
            } else {
                hyperSpeedActivated = false;
                setVelocityX(getVelocityX() + GameScene.HYPER_SPEED_VELOCITY_SHIFT);
            }

    }

    public boolean getHyperSpeed() {
        return hyperSpeedActivated;
    }

    @Override
    public void reset() {
        super.reset();
        setX(DEMO_POSITION);
        setVelocity(-DEMO_VELOCITY, 0);
        setAngularVelocity(0);
        setRotation(0);
        setAcceleration(0,0);
        scoreAdded = false;
        collidedWith = false;
        hitByMegaCrap = false;
        hyperSpeedActivated = false;
    }

    /**
     * set scoreAdded to true
     */
    public void setScoreAdded() {
        scoreAdded = true;
    }

    /**
     * gets whether or not points have been awarded for passing this obstacle
     * @return
     */
    public boolean getScoreAdded() {
        return scoreAdded;
    }

    public void setCollidedWith() {collidedWith = true;}

    public boolean getCollidedWith() {return collidedWith;}

    /**
     * used when user has power up and blasts obstacles off the screen
     * @param velocityY the y velocity to set to the obstacle (in px/sec)
     */
    public void blastOff(float velocityY) {
        if(velocityY == 0) {
            setVelocityY(100);
        } else {
            setVelocityY(velocityY);
        }
        setAngularVelocity(100);
    }

    public void hitWithMegaCrap(MegaCrap crap) {
        hitByMegaCrap = true;
        setVelocity(crap.getVelocityX(), crap.getVelocityY());
        setAcceleration(crap.getAccelerationX(), crap.getAccelerationY());
        setAngularVelocity(100);
    }

    public boolean getHitByMegaCrap() {return hitByMegaCrap;}

    public float getVelocityY() {
        return mPhysicsHandler.getVelocityY();
    }

    public float getVelocityX() {
        return mPhysicsHandler.getVelocityX();
    }

    public abstract obstacleType getObstacleType();


}
