package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.ResourceManager;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

/**
 * Created by Nick on 4/5/2015.
 */
public class CollectablePool extends GenericPool<Collectable> {

    private ResourceManager mResourceManager;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private float mGroundY;
    private int mCollectableIndex;

    public CollectablePool(ResourceManager pResourceManager, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY) {
        super();
        this.mResourceManager = pResourceManager;
        this.mVertexBufferObjectManager = pVertexBufferObjectManager;
        this.mGroundY = pGroundY;
    }

    @Override
    protected Collectable onAllocatePoolItem() {

        return new CollectablePizza(mResourceManager.mCollectablePizzaTextureRegion, mVertexBufferObjectManager, mGroundY, mResourceManager.mCollectablePizzaTextureRegion.getHeight());

    }

    @Override
    public synchronized void batchAllocatePoolItems(final int pCount) {
        super.batchAllocatePoolItems(pCount);
    }


    @Override
    protected void onHandleObtainItem(Collectable pItem) {
        pItem.reset();
    }

    @Override
    public synchronized Collectable obtainPoolItem() {
        mCollectableIndex++;
        return super.obtainPoolItem();
    }

    public int getCollectableIndex() {
        return mCollectableIndex;
    }

}