package com.sferrini.simone;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by simone on 27/03/14.
 */
public class SoundManager {
    private static SoundPool soundPool;
    private static int shutter_sound;
    public static void init(Context context){
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        shutter_sound = soundPool.load(context,R.raw.soundshutter, 0);
    }
    public static void play() {
        soundPool.play(shutter_sound, 1f, 1f, 1, 0, 1f);
    }
}
