package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ElevatorGuyBackground {
    
    private int mFrameIndex = 0;
    
    private List<Sprite> mSprites = new ArrayList<Sprite>();
    
    public boolean mOver = false;
    
    public ElevatorGuyBackground() {
        for (int i = 0; i < 119; i++) {
            Sprite s = ResourceManager.spriteFromFilename("assets/elevator-" + i + ".png");
            s.setBounds(0, 0, 800, 600);
            mSprites.add(s);
        }
    }
    
    public void reset() {
        mOver = false;
        mFrameIndex = 0;
    }
    
    public void update() {
        mFrameIndex += 1;
        if (mFrameIndex == mSprites.size()) {
            mOver = true;
        }
    }
    
    public void draw(SpriteBatch sb) {
        mSprites.get(mFrameIndex).draw(sb);
    }
}
