package com.example.arthook;

import static com.example.arthook.MainActivity.startHookOnlyOnce;

import android.app.Application;

import core.apolo.ArtHook;

public class DemoApplication extends Application {
    private static Application sApp;

    static {
        ArtHook.preLoad();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        startHookOnlyOnce();
    }

    public static Application getMyApplication() {
        return sApp;
    }
}
