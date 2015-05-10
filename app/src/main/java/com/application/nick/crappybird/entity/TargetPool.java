package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.ResourceManager;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

/**
 * Created by Nick on 4/5/2015.
 */
public class TargetPool extends GenericPool<Target> {

    private ResourceManager mResourceManager;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private float mGroundY;
    private int mTargetIndex;

    public TargetPool(ResourceManager pResourceManager, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY) {
        super();
        this.mResourceManager = pResourceManager;
        this.mVertexBufferObjectManager = pVertexBufferObjectManager;
        this.mGroundY = pGroundY;
    }

    @Override
    protected Target onAllocatePoolItem() {

        return new TargetPerson1(mResourceManager.mTargetPerson1TextureRegion, mVertexBufferObjectManager, mGroundY);


        /*int rand = (int) (Math.random() * 3);
        switch(rand) {
            case 0:
                return new ObstacleHouse(mResourceManager.mObstacleHouseTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleHouseTextureRegion.getHeight());
            case 1:
                return new ObstacleTree(mResourceManager.mObstacleTreesTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstacleTreesTextureRegion.getHeight());
            default:
                return new ObstaclePlane(mResourceManager.mObstaclePlanesTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mObstaclePlanesTextureRegion.getHeight());

        } */
    }


    @Override
    protected void onHandleObtainItem(Target pItem) {
        pItem.reset();
    }

    @Override
    public synchronized Target obtainPoolItem() {
        mTargetIndex++;
        return super.obtainPoolItem();
    }

    public int getTargetIndex() {
        return mTargetIndex;
    }

}