package hook.android.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.apolo.ArtEngine;
import org.apolo.HookName;
import org.apolo.ThisObject;

import java.util.ArrayList;

@HookName("android.app.IActivityTaskManager$Stub$Proxy")
public class ActivityTaskManagerProxy {

    private static final String TAG = ActivityTaskManagerProxy.class.getSimpleName();

    //android11.0+
    @HookName("startActivity")
    public static int startActivity(@ThisObject Object thiz,
                              @HookName("android.app.IApplicationThread") Object appThread,
                              String callingPackage, String xx, Intent intent,
                              String resolvedType, IBinder resultTo, String resultWho,
                              int requestCode, int flags,
                              @HookName("android.app.ProfilerInfo") Object profilerInfo,
                              Bundle options) {

        Log.d(TAG, "startActivity called " + intent, new Exception());
        return ArtEngine.callOrigin(thiz, appThread, callingPackage, xx, intent,
                resolvedType, resultTo, resultWho, requestCode, flags, profilerInfo, options);
    }
}
