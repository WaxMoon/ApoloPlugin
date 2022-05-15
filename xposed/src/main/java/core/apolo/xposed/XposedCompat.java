package core.apolo.xposed;

import android.app.Application;
import android.content.Context;

import java.io.File;

import core.apolo.xposed.util.ActivityThreadCompat;
import core.apolo.xposed.util.FileUtils;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedCompat {
    public static boolean DEBUG = true;
    public static File cacheDir;
    public static volatile ClassLoader classLoader;
    public static boolean isFirstApplication;

    public static void inject(Application app, String processName, File cacheDir) {
        ActivityThreadCompat.inject(app, processName, cacheDir);
    }

    public static void inject(Application app, String processName) {
        ActivityThreadCompat.inject(app, processName, null);
    }


    public static void loadModule(String modulePath, String moduleOdexDir, String moduleSoPath, ClassLoader classLoader) {
        XposedInit.loadModule(modulePath, moduleOdexDir, moduleSoPath, classLoader);
    }


    public static void addXposedModuleCallback(IXposedHookLoadPackage module) {
        XposedBridge.hookLoadPackage(new IXposedHookLoadPackage.Wrapper(module));
    }

    public static void callXposedModuleInit() throws Throwable {
        //prepare LoadPackageParam
        XC_LoadPackage.LoadPackageParam packageParam = new XC_LoadPackage.LoadPackageParam(XposedBridge.sLoadedPackageCallbacks);
        Context application = ActivityThreadCompat.currentApplication();


        if (application != null) {
            if (packageParam.packageName == null) {
                packageParam.packageName = application.getPackageName();
            }

            if (packageParam.processName == null) {
                packageParam.processName = ActivityThreadCompat.currentProcessName();
            }
            if (packageParam.classLoader == null) {
                packageParam.classLoader = application.getClassLoader();
            }
            if (packageParam.appInfo == null) {
                packageParam.appInfo = application.getApplicationInfo();
            }

            if (cacheDir == null) {
                application.getCacheDir();
            }
        }
        XC_LoadPackage.callAll(packageParam);
    }


    public static boolean clearCache() {
        try {
            File file = ActivityThreadCompat.getCacheDir();
            FileUtils.delete(file);
            file.mkdirs();
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static void clearOatCache() {
        DynamicBridge.clearOatFile();
    }

}
