package com.application.nick.crappybird.entity;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 5/25/2015.
 */
public class MegaCrapPool extends CrapPool {

    private TiledTextureRegion mCrapTextureRegion;
    private VertexBufferObjectManager mVertexBufferObjectManager;

    public MegaCrapPool(TiledTextureRegion pCrapTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pCrapTextureRegion, pVertexBufferObjectManager);
        mCrapTextureRegion = pCrapTextureRegion;
        mVertexBufferObjectManager = pVertexBufferObjectManager;
    }

    @Override
    protected Crap onAllocatePoolItem() {
        return new MegaCrap(mCrapTextureRegion, mVertexBufferObjectManager);
    }
}
