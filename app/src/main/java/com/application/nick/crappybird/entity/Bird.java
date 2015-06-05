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
public class Bird extends AnimatedSprite {

    private static final float DEMO_VELOCITY = 150.0f;
    private static final float DEMO_POSITION = 1.1f* GameActivity.CAMERA_WIDTH;
    private static final float GRAVITY = 15f * 30;
    private static final float JUMP_VELOCITY = 5 * 30;



    private final PhysicsHandler mPhysicsHandler;

    public Bird(float x, float y, TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(x, y, pTiledTextureRegion, pVertexBufferObjectManager);

        mPhysicsHandler = new PhysicsHandler(this);
        registerUpdateHandler(mPhysicsHandler);

        setAcceleration(0, GRAVITY);
        setVelocity(0, 0);

    }

    public void animate(int selectedBird) {
        long fDur = 200;
        this.animate(new long[]{fDur, fDur, fDur}, selectedBird * 3, selectedBird * 3 + 2, true);

    }

    public void jump() {
        float currentYVelocity = mPhysicsHandler.getVelocityY();
        if(currentYVelocity > 0) {
            setVelocityY(-JUMP_VELOCITY);
        }
    }

    public void jump(boolean megaCrapActivated) {
        float currentYVelocity = getVelocityY();
        if(currentYVelocity < 0) {
            if(!megaCrapActivated) {
                setVelocityY(currentYVelocity - (JUMP_VELOCITY * 4 + currentYVelocity) * (1.0f / 4.0f));
            } else {
                setVelocityY(currentYVelocity - (JUMP_VELOCITY * 4 + currentYVelocity) * (1.0f / 2.0f));
            }
        } else {
            if(!megaCrapActivated) {
                setVelocityY(-JUMP_VELOCITY);
            } else {
                setVelocityY(-JUMP_VELOCITY * 2);
            }
        }
    }

    public float getVelocityY() {
        return mPhysicsHandler.getVelocityY();
    }

    public void setVelocityX(float x) {mPhysicsHandler.setVelocityX(x);}

    public void setVelocityY(float y) {mPhysicsHandler.setVelocityY(y);}

    public void setVelocity(float x, float y) {
        mPhysicsHandler.setVelocity(x, y);
    }

    public void setAcceleration(float x, float y) {
        mPhysicsHandler.setAcceleration(x, y);
    }

    public void setAccelerationY(float y) { mPhysicsHandler.setAccelerationY(y);}
    @Override
    public boolean collidesWith(IShape pOtherShape) {

        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float left = this.getX();
        float right = left + this.getWidth();
        float top = this.getY();
        float bottom = top + this.getHeight();

        if ((spriteRight > left && spriteLeft < right) && (spriteTop < bottom && spriteBottom > top)) {
            return true;
        }
        return false;
    }

    public void die() {
        unregisterUpdateHandler(mPhysicsHandler);
    }

    public void alive() {
        registerUpdateHandler(mPhysicsHandler);
    }



}
