package com.application.nick.crappybird.entity;

import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public class ObstacleTree extends ObstacleGround {

    float mGroundY, mHeight, mWidth;

    public ObstacleTree(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY, float pHeight) {

        super(pTiledTextureRegion, pVertexBufferObjectManager, -pHeight, pHeight);

        mGroundY = pGroundY;
        mHeight = pHeight;
        mWidth = this.getWidth();

        randomizeTreeHeight();

    }

    public void randomizeTreeHeight() {
        float rand = (float) Math.random();
        float treeHeight = (rand * mHeight * 2) + mHeight;
        float treeWidth = (rand * mWidth) + mWidth;
        setHeight(treeHeight);
        setWidth(treeWidth);
        setY(mGroundY - treeHeight);
    }

    @Override
    public void reset() {
        super.reset();
        randomizeTreeHeight();
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

        //take into account odd shape of tree
        float obstacleMiddleX = (obstacleLeft + obstacleRight) / 2;
        float obstacleRadiusX = (obstacleRight - obstacleLeft) / 2;
        obstacleLeft = obstacleMiddleX - obstacleRadiusX / 2;
        obstacleRight = obstacleMiddleX + obstacleRadiusX / 2;
        float obstacleMiddleY = (obstacleTop + obstacleBottom) / 2;
        float obstacleRadiusY = (obstacleBottom - obstacleTop) / 2;
        obstacleTop = obstacleMiddleY - obstacleRadiusY * (5f/6);

        if ((spriteRight > obstacleLeft && spriteLeft < obstacleRight) && (spriteTop < obstacleBottom && spriteBottom > obstacleTop)) {
            return true;
        }
        return false;
    }

    @Override
    public Obstacle.obstacleType getObstacleType() {return obstacleType.TREE;}


}
