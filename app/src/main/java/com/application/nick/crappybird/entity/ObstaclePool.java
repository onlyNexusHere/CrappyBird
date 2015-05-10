package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.ResourceManager;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.math.MathUtils;

/**
 * Created by Nick on 4/5/2015.
 */
public class ObstaclePool extends GenericPool<Obstacle> {

    private ResourceManager mResourceManager;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private float mGroundY;
    private int mObstacleIndex;

    public ObstaclePool(ResourceManager pResourceManager, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY) {
        super();
        this.mResourceManager = pResourceManager;
        this.mVertexBufferObjectManager = pVertexBufferObjectManager;
        this.mGroundY = pGroundY;
    }

    @Override
    protected Obstacle onAllocatePoolItem() {
        int rand = (int) (Math.random() * 4);
        switch(rand) {
            case 0:
                return new ObstacleHouse(mResourceManager.mObstacleHouseTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleHouseTextureRegion.getHeight());
            case 1:
                return new ObstacleTree(mResourceManager.mObstacleTreesTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleTreesTextureRegion.getHeight());
            case 2:
                return new ObstacleBalloon(mResourceManager.mObstacleBalloonTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleBalloonTextureRegion.getHeight());
            default:
                return new ObstaclePlane(mResourceManager.mObstaclePlanesTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstaclePlanesTextureRegion.getHeight());

        }
    }


    @Override
    protected void onHandleObtainItem(Obstacle pItem) {
        pItem.reset();
    }

    @Override
    public synchronized Obstacle obtainPoolItem() {
        mObstacleIndex++;
        return super.obtainPoolItem();
    }

    public int getObstacleIndex() {
        return mObstacleIndex;
    }

}