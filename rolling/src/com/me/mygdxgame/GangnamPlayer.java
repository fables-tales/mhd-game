package com.me.mygdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

public class GangnamPlayer {

    private AudioDevice mAudioDevice;
    private String[] mData;
    private short[] mFrames;
    private static final float AUDIO_FRAME_RATE = 44100;
    private static final float GAME_FRAME_RATE = 60;
    private static final int nSamples = (int) (AUDIO_FRAME_RATE/GAME_FRAME_RATE);
    private int mPointerPos = 0;
    public float lastPeak = 1;
    short[] mSamplesBuffer = new short[nSamples * 2];

    
    private static GangnamPlayer sInstance;
    
    public static GangnamPlayer getInstance() {
        if (sInstance == null) {
            sInstance = new GangnamPlayer();
        }
        
        return sInstance;
    }
    
    private GangnamPlayer() {
        mAudioDevice = Gdx.audio.newAudioDevice(44100, true);

        mData = Gdx.files.internal("assets/gangnam.raw").readString().split("\n");
        mFrames = new short[mData.length];
        for (int i = 0; i < mData.length; i++) {
            mFrames[i] = Short.parseShort(mData[i]);

        }
        mAudioDevice.setVolume(1);
    }
    
    public float getCurrentSeconds() {
        return mPointerPos/AUDIO_FRAME_RATE;
    }

    public void dispatchFrame() {
        
        int peak = 0;
        for (int i = 0; i < nSamples; i++) {
            mFrames[i] = mFrames[i+mPointerPos];
            if (Math.abs(mFrames[i]) > peak) {
                peak = Math.abs(mFrames[i]);
            }
        }
        
        lastPeak = peak*1.0f/(65536/2);
        
        mAudioDevice.writeSamples(mFrames, 0, nSamples);
        mPointerPos += nSamples;
        
        
    }

    public float currentTimeSeconds() {
        return mPointerPos/AUDIO_FRAME_RATE;
    }
}
