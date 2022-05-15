package core.apolo;

import android.util.Log;

import java.lang.reflect.Member;
import java.util.HashMap;

class ArtHook {
    private static volatile boolean sAlreadHooked = false;

    private static final String TAG = "ArtHook";


    static {
        try {
            System.loadLibrary("apolo");
            Log.d(TAG, "preLoad success!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * @param instance If origin method is static, you can pass null
     * @param args     params must match it's signature
     * @return
     */
    public static <T> T callOrigin(Object instance/*Nullable*/, Object... args) {
        return (T) nativeCallOrigin(instance, args);
    }

    public static boolean startHook(HashMap<Member, Member> proxyMethods) {
        if (sAlreadHooked) {
            Log.e(TAG, "already hooked! only can be called once!");
            return false;
        }
        sAlreadHooked = true;
        return nativeStartHook(proxyMethods);
    }

    private static native Object nativeCallOrigin(Object instance/*Nullable*/, Object[] args);

    /**
     * @param proxyMethods <origin-Method, proxy-Method>
     * @return If hook success, will return true
     */
    private static native boolean nativeStartHook(HashMap<Member, Member> proxyMethods);

    /**
     * Note: Only the current thread is affected!
     * <p>
     * If you want to disable hook-status in the current thread, pass true.
     * HookTransition(true);
     * <p>
     * enable hook-status in the current thread, Pass false
     * HookTransition(false);
     *
     * @param origin Represents whether the original function is executed when method enter
     *               true: call originMethod
     *               false: call proxyMethod
     */
    public static native void hookTransition(boolean origin);

    private static native void reserve0();

    private static native void reserve1();
}
