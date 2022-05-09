package com.example.arthook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import core.apolo.ArtHook;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startHookOnlyOnce();
    }

    public void onButtonClick(View view) {
        Log.d(TAG, "getInstalledPackages called before+++++++");
        List xx = getPackageManager().getInstalledPackages(0);
        Log.d(TAG, "getInstalledPackages called end--------" + xx);
    }


    public static void startHookOnlyOnce() {
        try {
            //1. get origin-method -> ApplicationPackageManager.getInstalledPackages(int flags)
            Class<?> class_ApplicationPackageManager = Class.forName("android.app.ApplicationPackageManager");
            Method method_getInstalledPackages = class_ApplicationPackageManager
                    .getDeclaredMethod("getInstalledPackages", int.class);

            //2. get proxy-method -> MainActivity.getInstalledPackages
            Method proxyMethod_getInstalledPackages = MainActivity.class.getDeclaredMethod("proxy_getInstalledPackages",
                    PackageManager.class, int.class);

            //3. startHook
            HashMap<Method, Method> proxyMethods = new HashMap<>();
            proxyMethods.put(method_getInstalledPackages, proxyMethod_getInstalledPackages);
            ArtHook.startHook(proxyMethods);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<PackageInfo> proxy_getInstalledPackages(PackageManager pm, int flags) {
        Log.d(TAG, "proxy_getInstalledPackages called+++ ", new Throwable());

        try {
            //1.disable proxy method so that you can call origin-method
            ArtHook.hookTransition(true);

            //2.call origin method
            List<PackageInfo> pkgInfos = pm.getInstalledPackages(flags);
            Log.d(TAG, "proxy_getInstalledPackages origin list.size: " + pkgInfos.size());

            Toast.makeText(DemoApplication.getMyApplication(), "proxy_getInstalledPackages called", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "proxy_getInstalledPackages force return empty result");
            return Collections.emptyList();

        } finally {
            //3.enable proxy method
            ArtHook.hookTransition(false);
        }
    }
}