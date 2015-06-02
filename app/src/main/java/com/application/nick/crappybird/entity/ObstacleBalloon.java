package com.application.nick.crappybird.entity;

import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public class ObstacleBalloon extends Obstacle {

    private final float PLANE_VELOCITY = 200f;
    float mGroundY, mHeight;

    boolean basketHit = false;


    public ObstacleBalloon(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY, float pHeight) {
        super(pTiledTextureRegion, pVertexBufferObjectManager, -pHeight, pHeight);

        mGroundY = pGroundY;
        mHeight = pHeight;

        setCurrentTileIndex(0);

        randomizeAltitude();

    }

    public void randomizeAltitude() {
        float reference = (mGroundY - getHeight()) * 2 / 3;
        float deltaH = (reference * (float)Math.random());
        setY(reference - deltaH);
    }

    /**
     * this method randomizes the y velocity. makes it go at an angle. used for planes and other flying obstacles
     */
    public void randomizeYVelocity() {
        float middleOfScreen = mGroundY / 2 - this.getHeight() / 2;
        float rand = (float) Math.random() * 30;
        setVelocityY(-rand);
    }

    @Override
    public void reset() {
        super.reset();
        randomizeAltitude();
        setVelocityY(0);
        setCurrentTileIndex(0);
        basketHit = false;
    }

    @Override
    public boolean collidesWith(IShape pOtherShape) { //collides with balloon... kills bird
        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float left = this.getX();
        float top = this.getY();

        //just balloon
        //create regions to handle the irregular shape of the obstacle
        int[] regionX = {1, 7, 15, 23, 55, 63, 71, 77};
        int[] regionYTop = {26, 21, 10, 2, 10, 21, 26};
        int[] regionYBottom = {50, 59, 73, 83, 73, 59, 50};

        for(int i = 0; i < regionYTop.length; i++) {
            if ((spriteRight > left + regionX[i] && spriteLeft < left + regionX[i+1]) && (spriteTop < top + regionYBottom[i] && spriteBottom > top + regionYTop[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean collidesWithBasket(IShape pOtherShape) { //collides with basket... kills balloon passenger
        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float obstacleLeft = this.getX();
        float obstacleRight = obstacleLeft + this.getWidth();
        float obstacleTop = this.getY();
        float obstacleBottom = obstacleTop + this.getHeight();

        //just basket
        float obstacleMiddleX = (obstacleLeft + obstacleRight) / 2;
        obstacleLeft = obstacleMiddleX - 10;
        obstacleRight = obstacleMiddleX + 10;
        obstacleTop = obstacleBottom - 40;


        if ((spriteRight > obstacleLeft && spriteLeft < obstacleRight) && (spriteTop < obstacleBottom && spriteBottom > obstacleTop)) {
            return true;
        }
        return false;
    }

    public void birdHitsBasket() {
        basketHit = true;
        setCurrentTileIndex(1);
        setVelocityY(getVelocityY() - 30);
    }

    //returns whether or not the basket has been basketHit by the bird and the person knocked out
    public boolean getBasketHit() {return basketHit;}

}
