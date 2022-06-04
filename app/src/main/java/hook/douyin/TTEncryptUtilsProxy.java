package hook.douyin;

import org.apolo.ArtEngine;
import org.apolo.HookName;

import hook.utils.Slog;

@HookName("com.bytedance.frameworks.core.encrypt.TTEncryptUtils")
public class TTEncryptUtilsProxy {
    private static final String TAG = TTEncryptUtilsProxy.class.getSimpleName();

    @HookName("encrypt")
    public static byte[] encrypt(byte[] bytes, int arg) {
        Slog.d(TAG, "encrypt called");
        return ArtEngine.callOrigin(bytes, arg);
    }
}
