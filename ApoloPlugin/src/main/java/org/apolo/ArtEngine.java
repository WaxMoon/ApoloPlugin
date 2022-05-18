package org.apolo;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ArtEngine {
    private static final String TAG = "ApoloEngine";
    private static volatile boolean sAlreadyHooked = false;

    public static void preLoad() {
        System.loadLibrary("apolo");
        Log.d(TAG, "preLoad success!");
    }

    public static void addHooker(Class<?> cls) {
        try {
            ArtHookInternal.addHooker(cls.getClassLoader(), cls, false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void addHook(Member member, Method proxyMethod) {
        ArtHookInternal.addHook(member, proxyMethod);
    }

    public static void addHookers(ClassLoader loader, Class<?>... classes) {
        try {
            for (Class<?> cls : classes) {
                ArtHookInternal.addHooker(loader, cls, false);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static boolean contains(Member hookMethod) {
        return ArtHookInternal.contains(hookMethod);
    }

    public static boolean startHook() {
        if (sAlreadyHooked) {
            Log.e(TAG, "already hooked! only can be called once!");
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            Log.e(TAG, "hooked failed! platform not support!");
            return false;
        }
        sAlreadyHooked = true;
        return nativeStartHook(ArtHookInternal.methods);
    }

    /**
     * @param instance If origin method is static, you can pass null
     * @param args     params must match it's signature
     * @param <T> Type of return
     * @return Cast return to T
     */
    public static <T> T callOrigin(Object instance/*Nullable*/, Object... args) {
        return (T) nativeCallOrigin(instance, args);
    }

    /**
     * Note: Only the current thread is affected!
     * <p>
     * If you want to disable hook-status in the current thread, pass true.
     * HookTransition(true);
     * <p>
     * enable hook-status in the current thread, Pass false
     * HookTransition(false);
     *
     * @param originMode Represents whether the original function is executed when method enter
     *               true: call originMethod
     *               false: call proxyMethod
     */
    public static native void hookTransition(boolean originMode);

    private static native Object nativeCallOrigin(Object instance/*Nullable*/, Object[] args);

    /**
     * @param proxyMethods <origin-Method, proxy-Method>
     * @return If hook success, will return true
     */
    private static native boolean nativeStartHook(HashMap<Member, Member> proxyMethods);

    private static native void reserve0();

    private static native void reserve1();
}
