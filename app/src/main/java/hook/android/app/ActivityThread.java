package hook.android.app;

import org.apolo.ArtEngine;
import org.apolo.HookName;
import hook.utils.Slog;

@HookName("android.app.ActivityThread")
public class ActivityThread {

    private static final String TAG = ActivityThread.class.getSimpleName();

    @HookName("currentActivityThread")
    public static Object currentActivityThread() {
        Slog.d(TAG, "proxy_currentActivityThread called", new Exception());
        return ArtEngine.callOrigin(null);
    }
}
