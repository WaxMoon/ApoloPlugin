package hook.java.io;

import org.apolo.ArtEngine;
import org.apolo.HookClass;
import org.apolo.HookName;
import org.apolo.ThisObject;

import java.io.File;

import hook.utils.Slog;

@HookClass(File.class)
public class FileProxy {

    private static final String TAG = "FileProxy";

    @HookName("mkdir")
    public static boolean mkdir(@ThisObject File thiz) {
        Slog.d(TAG, "proxy_mkdir " + thiz.getPath(), new Exception());
        return ArtEngine.callOrigin(thiz);
    }
}
