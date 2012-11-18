package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SegmentLoader {
    private JSONObject mEchonest;

    private List<Float> mFanCounts = new ArrayList<Float>();

    public SegmentLoader(String echonestJsonFileName) {
        loadEchonest(echonestJsonFileName);
        loadMusicMetric("assets/timeseries.csv");
    }

    private void loadMusicMetric(String string) {
        FileHandle fh = Gdx.files.internal(string);
        String fileString = fh.readString();
        for (String line : fileString.split("\n")) {
            mFanCounts.add(Float.parseFloat(line));
        }
    }

    private void loadEchonest(String echonestJsonFileName) {
        FileHandle fh = Gdx.files.internal(echonestJsonFileName);
        String fileString = fh.readString();
        try {
            mEchonest = new JSONObject(fileString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public float getTrackLength() {
        try {
            return (float) mEchonest.getJSONObject("track").getDouble(
                    "duration");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    public float getCurrentLoudness() {
        float currentTime = GangnamPlayer.getInstance().currentTimeSeconds();
        JSONArray jsa;
        try {
            jsa = mEchonest.getJSONArray("segments");

            for (int i = 0; i < jsa.length(); i++) {
                JSONObject jso = jsa.getJSONObject(i);
                float start = (float) jso.getDouble("start");
                float duration = (float) jso.getDouble("duration");
                if (currentTime > start && currentTime < start + duration) {
                    return 60 + (float) jso.getDouble("loudness_max");
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    public float getCurrentFans() {
        
        float currentTime = GangnamPlayer.getInstance().currentTimeSeconds();
        float percent = currentTime / getTrackLength();
        int bucket = (int) (percent * mFanCounts.size());
        if (bucket < mFanCounts.size()) {
            return mFanCounts.get(bucket);
        } else {
            return 0;
        }

    }

    public float getMaxFans() {
        float max = 0;
        for (float count : mFanCounts) {
            if (count > max) {
                max = count;
            }
        }

        return max;
    }
    
    public float getTimbre(int vector) {
        float currentTime = GangnamPlayer.getInstance().currentTimeSeconds();
        JSONArray jsa;
        try {
            jsa = mEchonest.getJSONArray("segments");

            for (int i = 0; i < jsa.length(); i++) {
                JSONObject jso = jsa.getJSONObject(i);
                float start = (float) jso.getDouble("start");
                float duration = (float) jso.getDouble("duration");
                if (currentTime > start && currentTime < start + duration) {
                    return (float) jso.getJSONArray("timbre").getDouble(vector);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }
    
    public float getNextBeatTime() {
        float currentTime = GangnamPlayer.getInstance().currentTimeSeconds();
        JSONArray jsa;
        try {
            jsa = mEchonest.getJSONArray("beats");
            
            for (int i = 0; i < jsa.length(); i++) {
                JSONObject jso = jsa.getJSONObject(i);

                float start = (float) jso.getDouble("start");
                if (start >= currentTime) {
                    return start;
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public int getNextBeatIndex() {
        float currentTime = GangnamPlayer.getInstance().currentTimeSeconds();
        JSONArray jsa;
        try {
            jsa = mEchonest.getJSONArray("beats");
            
            for (int i = 0; i < jsa.length(); i++) {
                JSONObject jso = jsa.getJSONObject(i);

                float start = (float) jso.getDouble("start");
                if (start >= currentTime) {
                    return i;
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public float timeToNextBeat() {
        float currentTime = GangnamPlayer.getInstance().currentTimeSeconds();
        return getNextBeatTime() - currentTime;
    }
    
    public float getPitch(int vector) {
        float currentTime = GangnamPlayer.getInstance().currentTimeSeconds();
        JSONArray jsa;
        try {
            jsa = mEchonest.getJSONArray("segments");

            for (int i = 0; i < jsa.length(); i++) {
                JSONObject jso = jsa.getJSONObject(i);
                float start = (float) jso.getDouble("start");
                float duration = (float) jso.getDouble("duration");
                if (currentTime > start && currentTime < start + duration) {
                    return (float) jso.getJSONArray("pitches").getDouble(vector);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }


}
