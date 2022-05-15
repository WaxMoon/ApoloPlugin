package core.apolo.xposed.util;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.lang.reflect.Method;

@SuppressWarnings("all")
public class ActivityThreadCompat {
    private static Class<?> classActivityThread;

    private static Context application;
    private static String processName;
    private static File cacheDir;

    public static Context currentApplication() {
        if (application != null) {
            return application;
        }
        synchronized (ActivityThreadCompat.class) {
            if (application == null) {
                try {
                    Method method = obtain().getDeclaredMethod("currentApplication");
                    application = (Application) method.invoke(null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return application;
    }

    public static String currentProcessName() {
        if (processName != null) {
            return processName;
        }
        synchronized (ActivityThreadCompat.class) {
            if (processName == null) {
                try {
                    Method method = obtain().getDeclaredMethod("currentApplication");
                    processName = (String) method.invoke(null);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return processName;
    }

    public static File getCacheDir() {
        if (cacheDir != null) {
            return cacheDir;
        }
        synchronized (ActivityThreadCompat.class) {
            if (cacheDir == null) {
                cacheDir = new File(new File(currentApplication().getCacheDir().getParentFile(), "apolo_xposed"), DexMakerUtil.MD5(ActivityThreadCompat.currentProcessName()));
            }
        }
        return cacheDir;
    }


    private static Class<?> obtain() {
        if (classActivityThread == null) {
            try {
                classActivityThread = Class.forName("android.app.ActivityThread");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void inject(Context app, String processName, File cacheDir) {
        ActivityThreadCompat.application = app;
        ActivityThreadCompat.processName = processName;
        ActivityThreadCompat.cacheDir = cacheDir;
    }

    public static ClassLoader currentClassLoader() {
        return currentApplication().getClassLoader();
    }
}
