package org.imarks.vliegertest.adapter;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import org.imarks.vliegertest.adapter.interfaces.ISoundPoolLoaded;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark on 10-4-2016.
 */
public class SoundPoolManager {

    private static SoundPoolManager instance;
    private SoundPool soundPool;
    private List<Integer> sounds;
    private HashMap<Integer, SoundSampleEntity> hashMap;
    private boolean isPlaySound = true;

    public synchronized static SoundPoolManager getInstance() {
        return instance;
    }

    public static void CreateInstance() {
        if (instance == null) {
            instance = new SoundPoolManager();
        }
    }

    public List<Integer> getSounds() {
        return sounds;
    }

    public void setSounds(List<Integer> sounds) {
        this.sounds = sounds;
    }

    public void InitializeSoundPool(Activity activity, final ISoundPoolLoaded callback) throws Exception {
        if (sounds == null || sounds.size() == 0) {
            throw new Exception("Sounds not set");
        }
        soundPool = new SoundPool(36, AudioManager.STREAM_MUSIC, 100);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                SoundSampleEntity entity = getEntity(sampleId);
                if (entity != null) {
                    entity.setLoaded(status == 0);
                }

                if (sampleId == maxSampleId()) {
                    callback.onSuccess();
                }
            }
        });
        int length = sounds.size();
        hashMap = new HashMap<Integer, SoundSampleEntity>();
        int index;
        for (index = 0; index < length; index++) {
            hashMap.put(sounds.get(index), new SoundSampleEntity(0, false));
        }
        index = 0;
        for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
            index++;
            entry.getValue().setSampleId(soundPool.load(activity, entry.getKey(), index));
        }
    }

    private int maxSampleId() {
        int sampleId = 0;
        for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
            SoundSampleEntity entity = entry.getValue();
            sampleId = entity.getSampleId() > sampleId ? entity.getSampleId() : sampleId;
        }
        return sampleId;
    }

    private SoundSampleEntity getEntity(int sampleId) {
        for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
            SoundSampleEntity entity = entry.getValue();
            if (entity.getSampleId() == sampleId) {
                return entity;
            }
        }
        return null;
    }

    public boolean isPlaySound() {
        return isPlaySound;
    }

    public void setPlaySound(boolean isPlaySound) {
        this.isPlaySound = isPlaySound;
    }

    public void playSound(int resourceId) {
        playSound(resourceId, .99f, .99f);
    }

    public void playSound(int resourceId, float left, float right) {
        if (isPlaySound()) {
            SoundSampleEntity entity = hashMap.get(resourceId);
            if (entity != null && entity.getSampleId() > 0 && entity.isLoaded()) {
                soundPool.play(entity.getSampleId(), left, right, 1, 0, 1f);
            } else {
                Log.e("Audio", "Resource " + resourceId + " could not be played");
                if (entity != null){
                    Log.e("Audio", "IsLoaded: " + (entity.isLoaded() ? "True" : "False") + " sampleId " + entity.getSampleId());
                }
            }
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
        }
    }

    public void stop() {
        if (soundPool != null) {
            for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
                SoundSampleEntity entity = entry.getValue();
                soundPool.stop(entity.getSampleId());
            }
        }
    }

    private class SoundSampleEntity {
        private int sampleId;
        private boolean isLoaded;

        public SoundSampleEntity(int sampleId, boolean isLoaded) {
            this.isLoaded = isLoaded;
            this.sampleId = sampleId;
        }

        public int getSampleId() {
            return sampleId;
        }

        public void setSampleId(int sampleId) {
            this.sampleId = sampleId;
        }

        public boolean isLoaded() {
            return isLoaded;
        }

        public void setLoaded(boolean isLoaded) {
            this.isLoaded = isLoaded;
        }
    }
}
