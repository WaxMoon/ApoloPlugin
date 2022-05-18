package hook.java.lang;

import org.apolo.ArtEngine;
import org.apolo.HookName;
import org.apolo.ThisObject;
import hook.utils.Slog;

@HookName("java.lang.StringBuilder")
public class StringBuilder {

    private static final String TAG = StringBuilder.class.getSimpleName();

    @HookName("toString")
    public static String toString(@ThisObject Object sb) {
        String ret = ArtEngine.callOrigin(sb);
        ArtEngine.hookTransition(true);
        Slog.d(TAG, "proxy_toString :" + ret);
        ArtEngine.hookTransition(false);
        return ret;
    }
}
