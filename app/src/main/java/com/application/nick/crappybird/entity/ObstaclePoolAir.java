package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.ResourceManager;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 6/4/2015.
 */
public class ObstaclePoolAir extends ObstaclePool {

    private ResourceManager mResourceManager;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private float mGroundY;

    public ObstaclePoolAir(ResourceManager pResourceManager, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY) {
        super();
        this.mResourceManager = pResourceManager;
        this.mVertexBufferObjectManager = pVertexBufferObjectManager;
        this.mGroundY = pGroundY;

    }

    @Override
    protected ObstacleAir onAllocatePoolItem() {
        int rand = (int) (Math.random() * 2);
        switch(rand) {
            case 0:
                return new ObstacleBalloon(mResourceManager.mObstacleBalloonTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleBalloonTextureRegion.getHeight());
            default:
                return new ObstaclePlane(mResourceManager.mObstaclePlanesTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstaclePlanesTextureRegion.getHeight());

        }
    }
}
