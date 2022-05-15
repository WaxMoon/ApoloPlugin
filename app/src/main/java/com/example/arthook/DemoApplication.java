package com.example.arthook;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import core.apolo.ApoloHook;
import core.apolo.xposed.XposedCompat;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import hook.Test;
import hook.android.app.ApplicationPackageManager;


public class DemoApplication extends Application {
    private static Application sApp;


    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            XposedCompat.inject(this, getProcessName(), null);
        }

        ApoloHook.addHookers(getClassLoader(), ApplicationPackageManager.class, Test.class);
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
        ApoloHook.startHook();
    }

    public static Application getMyApplication() {
        return sApp;
    }
}
