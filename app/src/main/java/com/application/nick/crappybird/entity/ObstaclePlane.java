package com.application.nick.crappybird.entity;

import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public class ObstaclePlane extends ObstacleAir {

    private final float PLANE_VELOCITY = 200f;
    private final float MAX_PLANE_VERTICAL_VELOCITY = 50;
    float mGroundY, mHeight;


    public ObstaclePlane(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY, float pHeight) {
        super(pTiledTextureRegion, pVertexBufferObjectManager, -pHeight, pHeight);

        mGroundY = pGroundY;
        mHeight = pHeight;

        randomizePlaneAltitude();
        setVelocity(-PLANE_VELOCITY, 0);

        animate(100);

    }

    public void randomizePlaneAltitude() {
        float reference = mGroundY - 3 * mHeight;
        float deltaH = (reference * (float)Math.random());
        setY(reference - deltaH);
    }

    /**
     * this method randomizes the y velocity. makes it go at an angle. used for planes and other flying obstacles
     */
    public void randomizeYVelocity() {
        float middleOfScreen = mGroundY / 2 - this.getHeight() / 2;
        float rand = (float) Math.random() * MAX_PLANE_VERTICAL_VELOCITY;
        if(this.getY() > middleOfScreen) {
            setVelocityY(-rand);
        } else {
            setVelocityY(rand);
        }
    }

    @Override
    public void setSlowMotion(boolean bool) {
        super.setSlowMotion(bool);
        if(bool) {
            animate(200);
        } else {
            animate(100);
        }

    }

    @Override
    public void reset() {
        super.reset();
        randomizePlaneAltitude();
        setVelocityY(0);
    }

    @Override
    public boolean collidesWith(IShape pOtherShape) {
        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float left = this.getX();
        float top = this.getY();

        //create regions to handle the irregular shape of the obstacle
        int[] regionX = {5, 40, 70, 90};
        int[] regionYTop = {9, 17, 0};
        int[] regionYBottom = {37, 31, 26};

        for(int i = 0; i < regionYTop.length; i++) {
            if ((spriteRight > left + regionX[i] && spriteLeft < left + regionX[i+1]) && (spriteTop < top + regionYBottom[i] && spriteBottom > top + regionYTop[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Obstacle.obstacleType getObstacleType() {return obstacleType.PLANE;}
}
