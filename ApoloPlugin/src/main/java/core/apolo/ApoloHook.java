package core.apolo;

import android.util.Log;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ApoloHook {

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

    public static <T> T callOrigin(Object instance, Object... args) {
        return ArtHook.callOrigin(instance, args);
    }

    public static void startHook() {
        ArtHook.startHook(ArtHookInternal.methods);
    }


    public static boolean contains(Member hookMethod) {
        return ArtHookInternal.contains(hookMethod);
    }

}
