package com.application.nick.crappybird.entity;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

/**
 * Created by Nick on 4/5/2015.
 */
public class CrapPool extends GenericPool<Crap> {

    private TiledTextureRegion mCrapTextureRegion;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private float mBirdX;
    private int mCrapIndex;

    public CrapPool(TiledTextureRegion pCrapTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super();
        this.mCrapTextureRegion = pCrapTextureRegion;
        this.mVertexBufferObjectManager = pVertexBufferObjectManager;
        mCrapIndex = -1;
    }

    @Override
    protected Crap onAllocatePoolItem() {
        return new Crap(mCrapTextureRegion, mVertexBufferObjectManager);
    }



    @Override
    protected void onHandleObtainItem(Crap pItem) {
        pItem.reset();
    }

    @Override
    public synchronized Crap obtainPoolItem() {
        mCrapIndex++;
        return super.obtainPoolItem();
    }

    public int getCrapIndex() {return mCrapIndex;}


}