package hook.android.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.apolo.ArtEngine;
import org.apolo.HookName;
import org.apolo.ThisObject;

import hook.utils.Slog;

@HookName("android.app.ActivityManager")
public class ActivityManagerProxy {
    private static final String TAG = ActivityManagerProxy.class.getSimpleName();

    @HookName("startActivity")
    public void startActivity(@ThisObject Object thiz, Context context, Intent intent, Bundle options) {

        Slog.d(TAG, "startActivity " + intent, new Exception());
        ArtEngine.callOrigin(thiz, context, intent, options);
    }
}
