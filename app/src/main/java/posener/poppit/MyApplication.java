package posener.poppit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;

import posener.poppit.handlers.SoundPlayer;

/**
 * Created by Or on 02/07/2015.
 */
public class MyApplication extends Application {
    private static boolean activityVisible = true;
    @Override
    public void onCreate(){
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getResources().getString(R.string.parse_app), getResources().getString(R.string.parse_client));
        SoundPlayer.initSounds(this);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}