package com.application.nick.crappybird.entity;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public class CollectableMuffin extends Collectable {

    public CollectableMuffin(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY, float pHeight) {

        super(pTiledTextureRegion, pVertexBufferObjectManager, pGroundY - pHeight, pHeight);
    }
}
