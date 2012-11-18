package com.me.mygdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ResourceManager {
    public static Sprite spriteFromFilename(String filename) {
        FileHandle fh = Gdx.files.internal(filename);
        Texture t = new Texture(fh);
        return new Sprite(t);
    }
}
