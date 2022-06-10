package com.example.apolo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.apolo.helper.ProcessUtils;

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
import hook.douyin.EncryptorUtilProxy;
import hook.douyin.TTEncryptUtilsProxy;
import hook.java.io.FileProxy;
import hook.java.lang.StringProxy;
import hook.javax.net.ssl.HttpsURLConnection;


public class DemoApplication extends Application {

    private static final String TAG = DemoApplication.class.getSimpleName();

    private static Application sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        HookInject.main();
    }

    public static Application getMyApplication() {
        return sApp;
    }
}
