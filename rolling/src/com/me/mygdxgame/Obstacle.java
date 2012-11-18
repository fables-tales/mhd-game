package com.me.mygdxgame;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Obstacle implements Drawable, Updatable {

    private Sprite mSprite;
    private Sprite mOtherSprite;
    private GameWrapper mGameWrapper;
    private static Random sRng = new Random();

    public Obstacle(GameWrapper gw) {
        loadSprites();
        mGameWrapper = gw;
        mSprite.setPosition(800,
                sRng.nextFloat() * 600);
    }

    private void loadSprites() {
        if (sRng.nextBoolean()) {
            mSprite = ResourceManager
                    .spriteFromFilename("assets/obstacles.png");
            mOtherSprite = ResourceManager.spriteFromFilename("assets/obstacles_open.png");
        } else {
            mSprite = ResourceManager.spriteFromFilename("assets/yellow.png");
            mOtherSprite = ResourceManager.spriteFromFilename("assets/yellow.png");
        }
    }

    public void update() {
        float dx = mGameWrapper.mPsyEntity.getXPosition() - (mSprite.getX()+mSprite.getWidth()/2);
        float dy = mGameWrapper.mPsyEntity.getYPosition() -  (mSprite.getY()+mSprite.getHeight()/2);
        System.out.println(Math.sqrt(dx * dx + dy * dy) - mGameWrapper.mPsyEntity.getSize());
        if (Math.sqrt(dx * dx + dy * dy) < mGameWrapper.mPsyEntity.getSize()) {
            //mGameWrapper.gameOver();
        }

        mSprite.setPosition(mSprite.getX() - 10
                * mGameWrapper.mGangnamPlayer.lastPeak, mSprite.getY());
        
        mOtherSprite.setPosition(mSprite.getX(), mSprite.getY());
    }
    
    int mDrawcount = 0;

    public void draw(SpriteBatch sb) {
        mDrawcount++;
        if ((mDrawcount/10) % 2 == 0) {
            mSprite.draw(sb);
        } else {
            mOtherSprite.draw(sb);
        }
    }

    public float getPositionX() {
        return mSprite.getX();
    }

}
