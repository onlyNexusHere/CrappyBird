package com.application.nick.crappybird.entity;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public class ObstacleHouse extends ObstacleGround {

    private float positionY;

    public ObstacleHouse(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY, float pHeight) {

        super(pTiledTextureRegion, pVertexBufferObjectManager, pGroundY - pHeight, pHeight);
        positionY = pGroundY - pHeight;
    }

    @Override
    public void reset() {
        super.reset();
        setY(positionY);
    }

    @Override
    public Obstacle.obstacleType getObstacleType() {return obstacleType.HOUSE;}
}
