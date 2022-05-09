/**
 * Created by WaxMoon on 2022/5/8.
 */

package core.apolo;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ArtHook {
    private static volatile boolean sAlreadHooked = false;

    private static final String TAG = "ArtHook";

    public static void preLoad() {
        System.loadLibrary("apolo");
        Log.d(TAG, "preLoad success!");
    }

    public static boolean startHook(HashMap<Method, Method> proxyMethods) {
        if (sAlreadHooked) {
            Log.e(TAG, "already hooked! only can be called once!");
            return false;
        }
        sAlreadHooked = true;
        return nativeStartHook(proxyMethods);
    }

    /**
     *
     * @param proxyMethods <origin-Method, proxy-Method>
     * @return If hook success, will return true
     */
    public static native boolean nativeStartHook(HashMap<Method, Method> proxyMethods);

    /**
     *
     * @param origin if you want to call origin-method, you need to call like below
     *
     *               try {
     *                   //1.disable proxy method so that you can call origin-method
     *                   hookTransition(true);
     *                   ...
     *                   //2.call originMethod here
     *                   ...
     *               } finally {
     *                   //3.enable proxy method
     *                   hookTransition(false);
     *               }
     */
    public static native void hookTransition(boolean origin);

    private static native void reserve0();
    private static native void reserve1();
}
