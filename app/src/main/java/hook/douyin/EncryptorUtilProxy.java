package hook.douyin;

import org.apolo.ArtEngine;
import org.apolo.HookName;

import hook.utils.Slog;

@HookName("com.bytedance.frameworks.encryptor.EncryptorUtil")
public class EncryptorUtilProxy {

    private static final String TAG = "EncryptorUtilProxy";

    @HookName("LIZ")
    public static byte[] LIZ(byte[] arg1, int arg2) {
        Slog.d(TAG, "LIZ called");
        return ArtEngine.callOrigin(null, arg1, arg2);
    }
}
