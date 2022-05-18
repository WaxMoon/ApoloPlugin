package hook.java.lang;

import org.apolo.ArtEngine;
import org.apolo.HookClass;
import org.apolo.HookName;
import org.apolo.ThisObject;
import hook.utils.Slog;

@HookClass(String.class)
public class StringProxy {

    private static final String TAG = StringProxy.class.getSimpleName();
    @HookName("equals")
    public static boolean equals(@ThisObject String str1, Object str2) {
        Slog.d(TAG, "proxy_equals called %s vs %s", str1, str2);
        return ArtEngine.callOrigin(str1, str2);
    }
}
