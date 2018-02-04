package posener.poppit.handlers;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import posener.poppit.R;

/**
 * Created by Or on 01/07/2015.
 */
public class SoundPlayer {
    public static final int BALLOON_INFLATE = R.raw.balloon_pop;
    public static final int BALLOON_POP = R.raw.balloon_pop;

    private static SoundPool soundPool;
    private static HashMap soundPoolMap;

    public static void initSounds(Context context){
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap(1);

        soundPoolMap.put(BALLOON_POP, soundPool.load(context, BALLOON_POP, 1));
        soundPoolMap.put(BALLOON_INFLATE, soundPool.load(context, BALLOON_INFLATE, 1));

    }

    public static void playSound(Context context, int soundID) {
        if(soundPool == null || soundPoolMap == null){
            initSounds(context);
        }
        float volume = 1;
        soundPool.play((int) soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
    }
}
