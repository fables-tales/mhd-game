package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MermaidEntity implements Updatable, Drawable {
    private int mFrameIndex = 0;
    private List<Sprite> mSprites = new ArrayList<Sprite>();
    
    public MermaidEntity(int offset, float xPosition) {
        mFrameIndex = offset;
        mSprites.add(ResourceManager.spriteFromFilename("assets/mermaid.png"));
        mSprites.add(ResourceManager.spriteFromFilename("assets/mermaid-open.png"));        
        mSprites.add(ResourceManager.spriteFromFilename("assets/mermaid-flipped.png"));
        mSprites.add(ResourceManager.spriteFromFilename("assets/mermaid-flipped-open.png"));
        
        for (Sprite s : mSprites) {
            s.setPosition(xPosition, 0);
            s.setScale(0.7f);
        }
    }
    
    
    public void draw(SpriteBatch sb) {
        mSprites.get(mFrameIndex).draw(sb);
    }
    
    public void update() {
        mFrameIndex += 1;
        if (mFrameIndex >= mSprites.size()) {
            mFrameIndex = 0;
        }
    }
}
