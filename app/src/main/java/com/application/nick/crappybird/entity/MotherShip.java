package com.application.nick.crappybird.entity;

import com.application.nick.crappybird.GameActivity;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by Nick on 4/5/2015.
 */
public class MotherShip extends Obstacle {

    private static final float DEMO_VELOCITY = 1500.0f;
    private static final float GROUND_HEIGHT = 112;

    private final int SCREEN_WIDTH = GameActivity.CAMERA_WIDTH;
    private final int SCREEN_HEIGHT = GameActivity.CAMERA_HEIGHT;

    private boolean flyingPastScreen = false;

    public MotherShip(TiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(-pTiledTextureRegion.getWidth(), -pTiledTextureRegion.getHeight(), pTiledTextureRegion, pVertexBufferObjectManager);

        setScale(1);
        setVelocity(0, 0);


    }


    @Override
    public boolean collidesWith(IShape pOtherShape) {

        Sprite sprite = (Sprite) pOtherShape;

        float spriteLeft = sprite.getX();
        float spriteRight = spriteLeft + sprite.getWidth();
        float spriteTop = sprite.getY();
        float spriteBottom = spriteTop + sprite.getHeight();
        float left = this.getX();
        float top = this.getY();


        //create regions to handle the irregular shape of the obstacle
        int[] regionX = {0, 250, 520, 680, 780, 980, 1170};
        int[] regionYTop = {60, 50, 50, 30, 55, 0};
        int[] regionYBottom = {130, 150, 170, 170, 170, 190};

        for(int i = 0; i < regionYTop.length; i++) {
            if ((spriteRight > left + regionX[i] && spriteLeft < regionX[i+1]) && (spriteTop < top + regionYBottom[i] && spriteBottom > top + regionYTop[i])) {
                return true;
            }
        }
        return false;
    }


    public void flyingPastScreen(boolean bool) {
        if(bool) { //start flying
            flyingPastScreen = true;
            setX(SCREEN_WIDTH * 1.1f);
            setY(0);
            setVelocityX(-DEMO_VELOCITY);
        } else { //stop flying
            flyingPastScreen = false;
            setVelocityX(0);
        }
    }


}
