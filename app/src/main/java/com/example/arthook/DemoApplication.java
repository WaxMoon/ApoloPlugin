package com.example.arthook;

import android.app.Application;

import core.apolo.ArtHook;

public class DemoApplication extends Application {
    private static Application sApp;
    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        ArtHook.preLoad();
    }

    public static Application getMyApplication() {
        return sApp;
    }
}
