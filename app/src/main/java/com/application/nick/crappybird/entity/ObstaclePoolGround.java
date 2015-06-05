package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.ResourceManager;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 6/4/2015.
 */
public class ObstaclePoolGround extends ObstaclePool {

    private ResourceManager mResourceManager;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private float mGroundY;

    public ObstaclePoolGround(ResourceManager pResourceManager, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY) {
        super();
        this.mResourceManager = pResourceManager;
        this.mVertexBufferObjectManager = pVertexBufferObjectManager;
        this.mGroundY = pGroundY;

    }

    @Override
    protected ObstacleGround onAllocatePoolItem() {
        int rand = (int) (Math.random() * 2);
        switch(rand) {
            case 0:
                return new ObstacleHouse(mResourceManager.mObstacleHouseTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleHouseTextureRegion.getHeight());
            default:
                return new ObstacleTree(mResourceManager.mObstacleTreesTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleTreesTextureRegion.getHeight());

        }
    }
}
