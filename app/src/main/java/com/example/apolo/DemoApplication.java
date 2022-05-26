package com.example.apolo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import org.apolo.ArtEngine;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import hook.Test;
import hook.android.app.ActivityThread;
import hook.android.app.ApplicationPackageManager;
import hook.android.app.ContextImpl;
import hook.android.os.HandlerProxy;
import hook.android.os.ProcessProxy;
import hook.android.provider.Settings;
import hook.java.io.FileProxy;
import hook.java.lang.StringBuilder;
import hook.java.lang.StringProxy;
import hook.javax.net.ssl.HttpsURLConnection;


public class DemoApplication extends Application {

    private static final String TAG = DemoApplication.class.getSimpleName();

    private static Application sApp;

    static {
        //init ArtHook
        ArtEngine.preLoad();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        initHook();
    }

    public static Application getMyApplication() {
        return sApp;
    }

    private void initHook() {
        ArtEngine.addHookers(getClassLoader(),
                StringProxy.class,
                StringBuilder.class,
                FileProxy.class,
                ProcessProxy.class,
                HandlerProxy.class,
                ActivityThread.class,
                ApplicationPackageManager.class,
                ContextImpl.class,
                Settings.Global.class,
                Test.class);

        ArtEngine.addHooker(HttpsURLConnection.class);

        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("XposedCompat", "beforeHookedMethod: " + param.method.getName());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.e("XposedCompat", "afterHookedMethod: " + param.method.getName());
            }
        });

        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("XposedCompat", "beforeHookedMethod: " + param.method.getName());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.e("XposedCompat", "afterHookedMethod: " + param.method.getName());
            }
        });
        //ArtEngine.setHookMode(ArtEngine.MODE_INTERPRET);
        ArtEngine.startHook();
    }
}
