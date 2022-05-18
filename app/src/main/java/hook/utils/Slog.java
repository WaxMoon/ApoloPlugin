package hook.utils;

import android.util.Log;

import org.apolo.ArtEngine;

public class Slog {
    public static void d(String tag, String format, Object... args) {
        ArtEngine.hookTransition(true);
        Log.d(tag, String.format(format, args));
        ArtEngine.hookTransition(false);
    }

    public static void d(String tag, String format, Throwable thr, Object... args) {
        ArtEngine.hookTransition(true);
        Log.d(tag, String.format(format, args), thr);
        ArtEngine.hookTransition(false);
    }
}
