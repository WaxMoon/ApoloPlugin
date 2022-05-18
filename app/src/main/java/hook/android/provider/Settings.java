package hook.android.provider;

import android.content.ContentResolver;

import org.apolo.ArtEngine;
import org.apolo.HookName;
import hook.utils.Slog;

public class Settings {
    @HookName("android.provider.Settings$Global")
    public static class Global {
        private static final String TAG = "Settings.Global";

        public static String getString(ContentResolver cr, String key) {
            String ret = ArtEngine.callOrigin(null, cr, key);
            Slog.d(TAG, "proxy_getString key: %s value: %s", key, ret);
            return ret;
        }
    }
}
