package hook.android.os;

import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;

import org.apolo.ArtEngine;
import org.apolo.HookClass;
import org.apolo.HookName;
import org.apolo.ThisObject;

import hook.utils.Slog;

@HookClass(Handler.class)
public class HandlerProxy {
    private static final String TAG = HandlerProxy.class.getSimpleName();

    @HookName("sendMessageAtTime")
    public static boolean sendMessageAtTime(@ThisObject Object obj, Message msg, long uptimeMillis) {
        Slog.d(TAG, "proxy_sendMessageAtTime called: " + msg);
        return ArtEngine.callOrigin(obj, msg, uptimeMillis);
    }

    @HookName("enqueueMessage")
    public static boolean enqueueMessage(@ThisObject Handler handler, MessageQueue msgQueue,
                                         Message msg, long uptimeMillis) {
        Slog.d(TAG, "proxy_enqueueMessage called");
        return ArtEngine.callOrigin(handler, msgQueue, msg, uptimeMillis);
    }
}
