package com.application.nick.crappybird.entity;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public abstract class ObstacleAir extends Obstacle {

    public ObstacleAir(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pObstacleY, float pHeight) {

        super(pTiledTextureRegion, pVertexBufferObjectManager, pObstacleY, pHeight);

    }
}
