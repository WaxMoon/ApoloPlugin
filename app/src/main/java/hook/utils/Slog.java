package hook.utils;

import android.util.Log;

import com.apolo.helper.FileLogUtils;

import org.apolo.ArtEngine;

public class Slog {
    private static final boolean LOG_TO_FILE = false;
    public static void d(String tag, String format, Object... args) {
        ArtEngine.hookTransition(true);
        if (LOG_TO_FILE) {
            FileLogUtils.getInstance().saveLog(tag + ": " + String.format(format, args));
        } else {
            Log.d(tag, String.format(format, args));
        }
        ArtEngine.hookTransition(false);
    }

    public static void d(String tag, String format, Throwable thr, Object... args) {
        ArtEngine.hookTransition(true);
        if (LOG_TO_FILE) {
            FileLogUtils.getInstance().saveLog(String.format(format, args));
        } else {
            Log.d(tag, String.format(format, args), thr);
        }
        ArtEngine.hookTransition(false);
    }
}
