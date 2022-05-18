package core.apolo.xposed;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apolo.ArtEngine;
import core.apolo.xposed.util.ActivityThreadCompat;
import core.apolo.xposed.util.FileUtils;
import de.robv.android.xposed.XposedBridge;

public final class DynamicBridge {

    public static synchronized void hookMethod(Member hookMethod, XposedBridge.AdditionalHookInfo additionalHookInfo) {
        if (!checkMember(hookMethod)) {
            return;
        }
        if (ArtEngine.contains(hookMethod)) {
            XposedLog.w("already hook :" + hookMethod.toString());
            return;
        }

        try {
            long timeStart = System.currentTimeMillis();
            new HookerDexMaker().start(hookMethod, additionalHookInfo, new ProxyClassLoader(DynamicBridge.class.getClassLoader(), hookMethod.getDeclaringClass().getClassLoader()), ActivityThreadCompat.getCacheDir());
            XposedLog.d("hook method <" + hookMethod.toString() + "> consume " + (System.currentTimeMillis() - timeStart) + " ms, by dex maker");
        } catch (Throwable e) {
            XposedLog.e("occur error when hook method <" + hookMethod.toString() + ">", e);
        }
    }

    public static void clearOatFile() {
        File fixedAppDataDir = ActivityThreadCompat.getCacheDir();
        File dexOatDir = new File(fixedAppDataDir, "oat");
        if (!dexOatDir.exists())
            return;
        try {
            FileUtils.delete(dexOatDir);
            dexOatDir.mkdirs();
        } catch (Throwable throwable) {
        }
    }

    private static boolean checkMember(Member member) {

        if (member instanceof Method) {
            return true;
        } else if (member instanceof Constructor<?>) {
            return true;
        } else if (member.getDeclaringClass().isInterface()) {
            XposedLog.e("not support hook interfaces: " + member.toString());
            return false;
        } else if (Modifier.isAbstract(member.getModifiers())) {
            XposedLog.e("not support hook abstract methods: " + member.toString());
            return false;
        } else {
            XposedLog.e("only methods and constructors can be hooked: " + member.toString());
            return false;
        }
    }
}


