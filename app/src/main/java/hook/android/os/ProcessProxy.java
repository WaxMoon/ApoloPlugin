package hook.android.os;

import android.os.Process;

import org.apolo.ArtEngine;
import org.apolo.HookClass;
import org.apolo.HookName;

import hook.utils.Slog;

@HookClass(Process.class)
public class ProcessProxy {

    private static final String TAG = "ProcessProxy";
    @HookName("killProcess")
    public static final void killProcess(int pid) {
        Slog.d(TAG, "killProcess", new Exception());
        ArtEngine.callOrigin(null, pid);
    }
}
