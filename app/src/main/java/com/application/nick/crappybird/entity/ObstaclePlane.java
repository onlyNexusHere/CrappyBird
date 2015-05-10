package com.application.nick.crappybird.entity;

import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public class ObstaclePlane extends Obstacle {

    private final float PLANE_VELOCITY = 200f;
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
        float rand = (float) Math.random() * 90;
        if(this.getY() > middleOfScreen) {
            setVelocityY(-rand);
        } else {
            setVelocityY(rand);
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
        float obstacleLeft = this.getX();
        float obstacleRight = obstacleLeft + this.getWidth();
        float obstacleTop = this.getY();
        float obstacleBottom = obstacleTop + this.getHeight();

        //take into account odd shape of plane
        float obstacleMiddleX = (obstacleLeft + obstacleRight) / 2;
        float obstacleRadiusX = (obstacleRight - obstacleLeft) / 2;
        obstacleLeft = obstacleMiddleX - obstacleRadiusX * (5f/6);
        obstacleRight = obstacleMiddleX + obstacleRadiusX * (5f/6);
        float obstacleMiddleY = (obstacleTop + obstacleBottom) / 2;
        float obstacleRadiusY = (obstacleBottom - obstacleTop) / 2;
        obstacleTop = obstacleMiddleY - obstacleRadiusY * (5f/6);
        obstacleBottom = obstacleMiddleY + obstacleRadiusY * (5f/6);

        if ((spriteRight > obstacleLeft && spriteLeft < obstacleRight) && (spriteTop < obstacleBottom && spriteBottom > obstacleTop)) {
            return true;
        }
        return false;
    }
}
