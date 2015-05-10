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
public class TargetPerson1 extends Target {

    private static final float DEMO_VELOCITY = 150.0f;
    private static final float VELOCITY_RANGE = 100.0f;
    private static final float DEMO_POSITION = 1.5f* GameActivity.CAMERA_WIDTH;

    private boolean falling = false;
    private float mGroundY;

    public TargetPerson1(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY) {
        super(pTiledTextureRegion, pVertexBufferObjectManager, pGroundY);

        mGroundY = pGroundY;
        setY(pGroundY - getHeight());
        randomizeMovement();

    }


    public void randomizeMovement() {
        int rand = (int) (Math.random() * 4);
        if (rand == 0) {
            if(isAnimationRunning()) {
                stopAnimation(0);
            } else {
                setCurrentTileIndex(0); //make person stationary
            }
            setXVelocity(-150);
        } else {
            randomizeVelocity();
        }
    }

    @Override
    public void randomizeVelocity() {
        super.randomizeVelocity();

        float scrollSpeed = -150;
        float difference = Math.abs(getXVelocity() + 150);
        int fDur = (int)(5000 / difference); //frame duration
        if(fDur < 50) {
            fDur = 50;
        } else if (fDur > 400) {
            fDur = 400;
        }


        if(this.getXVelocity() < scrollSpeed) {
            this.animate(new long[]{fDur, fDur, fDur, fDur, fDur, fDur, fDur, fDur}, 16, 23, true); //walk facing left
        } else if(this.getXVelocity() > scrollSpeed) {
            this.animate(new long[]{fDur, fDur, fDur, fDur, fDur, fDur, fDur, fDur}, 24, 31, true); //walk facing right
        } else if(this.getXVelocity() == scrollSpeed) {
            this.setCurrentTileIndex(0);
        }
    }

    @Override
    public void reset() {
        super.reset();
        falling = false;
        setYVelocity(0);
        setRotation(0);
        setYAcceleration(0);
        setY(mGroundY - getHeight());

    }

    @Override
    public void hitByCrap() {
        super.hitByCrap();
        this.stopAnimation(7); //set to gravestone
    }

    @Override
    public void hitByCrap(boolean gameover) {
        super.hitByCrap(gameover);
        this.stopAnimation(7); //set to gravestone
    }

    public void setFalling(boolean bool) {
        if (bool) {
            passedAddXValue();
            falling = true;
            setYAcceleration(300);
            setRotation(180);
            int rand = (int) (Math.random() * 2);
            long fDur = 50;
            if (rand == 0) {
                this.animate(new long[]{fDur, fDur, fDur, fDur, fDur, fDur, fDur, fDur}, 16, 23, true); //walk facing left
            } else if (rand == 1) {
                this.animate(new long[]{fDur, fDur, fDur, fDur, fDur, fDur, fDur, fDur}, 24, 31, true); //walk facing right
            }

        } else {
            falling = false;
            setYAcceleration(0);
            setRotation(0);
            stopAnimation();

        }
    }

    /**
     * for making the person fall out of a hot air balloon
     * @param v velocity of the balloon
     * @param x x coordinate of the balloon's basket
     * @param y y coordinate of the balloon's basket
     */
    public void setFalling(float v, float x, float y) {
        setXVelocity(v);
        setY(y);
        setX(x);
        setFalling(true);
    }

    public void hitsGround(boolean gameOver) {
        setFalling(false);
        hitByCrap(gameOver); //changes to gravestone and sets velocity to scroll or 0 (if gameover = true)
    }

    public boolean getFalling() {
        return falling;
    }

}
