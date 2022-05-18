package hook.android.app;

import org.apolo.ArtEngine;
import org.apolo.HookName;
import org.apolo.ThisObject;
import hook.utils.Slog;

@HookName("android.app.ContextImpl")
public class ContextImpl {
    private static final String TAG = ContextImpl.class.getSimpleName();

    @HookName("getSystemService")
    public static Object getSystemService(@ThisObject Object thiz, String serviceName) {
        Slog.d(TAG, "proxy_getSystemService for %s", new Exception(), serviceName);
        return ArtEngine.callOrigin(thiz, serviceName);
    }
}
