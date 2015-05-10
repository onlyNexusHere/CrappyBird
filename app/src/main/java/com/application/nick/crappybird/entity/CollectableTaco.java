package com.application.nick.crappybird.entity;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/9/2015.
 */
public class CollectableTaco extends Collectable {

    public CollectableTaco(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, float pGroundY, float pHeight) {

        super(pTiledTextureRegion, pVertexBufferObjectManager, pGroundY - pHeight, pHeight);
    }
}
