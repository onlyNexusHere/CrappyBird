package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.ResourceManager;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;
import org.andengine.util.math.MathUtils;

/**
 * Created by Nick on 4/5/2015.
 */
public abstract class ObstaclePool extends GenericPool<Obstacle> {

    private int mObstacleIndex;

    @Override
    protected abstract Obstacle onAllocatePoolItem();

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