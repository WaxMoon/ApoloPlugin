package com.example.apolo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.apolo.ArtEngine;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import hook.Test;
import hook.android.app.ActivityManagerProxy;
import hook.android.app.ActivityTaskManagerProxy;
import hook.android.app.ActivityThread;
import hook.android.app.ApplicationPackageManager;
import hook.android.app.ContextImpl;
import hook.android.os.HandlerProxy;
import hook.android.os.ProcessProxy;
import hook.android.provider.Settings;
import hook.java.io.FileProxy;
import hook.javax.net.ssl.HttpsURLConnection;

public class HookInject {
    static {
        //init ArtHook
        ArtEngine.preLoad();
    }

    public static void main() {
        ArtEngine.addHookers(DemoApplication.class.getClassLoader(),
//                StringProxy.class,
//                StringBuilder.class,//尽量不hook StringBuilder
                FileProxy.class,
                ProcessProxy.class,
                HandlerProxy.class,
                ActivityThread.class,
                ApplicationPackageManager.class,
                ContextImpl.class,
                Settings.Global.class,
                Test.class);

        ArtEngine.addHooker(ActivityManagerProxy.class);
        ArtEngine.addHooker(ActivityTaskManagerProxy.class);


        //try inject douyin
//        try {
//            ArtEngine.addHookers(ProcessUtils.getMainApplication().getClassLoader(),
//                    EncryptorUtilProxy.class,
//                    TTEncryptUtilsProxy.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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
        //split ','
        //ArtEngine.setInterpretFilterRegex("android.app.*,android.os.*");
        //ArtEngine.enableInterpretLog();
        ArtEngine.setHookMode(ArtEngine.MODE_INTERPRET);//如果已声明debuggable=true，此处不需要再调用
        ArtEngine.startHook();
    }
}
