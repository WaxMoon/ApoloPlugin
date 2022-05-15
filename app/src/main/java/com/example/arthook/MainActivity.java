package com.example.arthook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
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
    }

    public void onButtonClick(View view) {
        Log.d(TAG, "getInstalledPackages called before+++++++");
        List xx = getPackageManager().getInstalledPackages(0);
        Log.d(TAG, "getInstalledPackages called end--------" + xx);
    }


    public static void startHookOnlyOnce() {
        try {
            //-------.new HashMap to cache originMethod-proxyMethod
            HashMap<Member, Member> proxyMethods = new HashMap<>();

            /******************1.ApplicationPackageManager.getInstalledPackages BEGIN*******/
            //1.1 get origin-method
            Class<?> class_ApplicationPackageManager = Class.forName("android.app.ApplicationPackageManager");
            Method method_getInstalledPackages = class_ApplicationPackageManager
                    .getDeclaredMethod("getInstalledPackages", int.class);
            //1.2 get proxy-method
            Method proxyMethod_getInstalledPackages = MainActivity.class.getDeclaredMethod("proxy_getInstalledPackages",
                    PackageManager.class, int.class);
            //1.3 save to HashMap
            proxyMethods.put(method_getInstalledPackages, proxyMethod_getInstalledPackages);
            /******************1.ApplicationPackageManager.getInstalledPackages END*******/


            /******************2.TextView.<init>(Context.class) BEGIN*******/
            //1.1 get origin-method
            Constructor method_TextView_ctor = TextView.class.getConstructor(Context.class);
            //1.2 get proxy-method
            Method proxyMethod_TextView_ctor = MainActivity.class.getDeclaredMethod(
                    "proxy_TextView_ctor", TextView.class, Object.class);
            //1.3 save to HashMap
            proxyMethods.put(method_TextView_ctor, proxyMethod_TextView_ctor);
            /******************2.TextView.<init>(Context.class) BEGIN*******/

            Method method_Activity_onCreate = Activity.class.getDeclaredMethod("onCreate", Bundle.class);
            Method proxyMethod_Activity_onCreate = MainActivity.class.getDeclaredMethod("proxy_Activity_onCreate", Activity.class, Bundle.class);
            proxyMethods.put(method_Activity_onCreate, proxyMethod_Activity_onCreate);


            /*****************3.startHook BEGIN******************/
            ArtHook.startHook(proxyMethods);
            /*****************3.startHook END******************/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<PackageInfo> proxy_getInstalledPackages(PackageManager pm, int flags) {
        Log.d(TAG, "proxy_getInstalledPackages called+++ ", new Throwable());

        /********call origin method*************/
        List<PackageInfo> pkgInfos = ArtHook.callOrigin(pm, flags);

        Log.d(TAG, "proxy_getInstalledPackages origin list.size: " + pkgInfos.size());
        Toast.makeText(DemoApplication.getMyApplication(), "proxy_getInstalledPackages called", Toast.LENGTH_SHORT).show();

        /*********Support Call other proxy method BEGIN*******************/
        boolean useOrigin_TextViewCtor = false;//Test Feature, you can change is to be true
        if (useOrigin_TextViewCtor) {
            try {
                ArtHook.hookTransition(true);
                Log.d(TAG, "TextView_ctor<init> called before+++++++");
                TextView tv = new TextView(DemoApplication.getMyApplication());
                Log.d(TAG, "TextView_ctor<init> called end--------" + tv);
            } finally {
                ArtHook.hookTransition(false);
            }
        } else {
            Log.d(TAG, "TextView_ctor<init> called before+++++++");
            TextView tv = new TextView(DemoApplication.getMyApplication());
            Log.d(TAG, "TextView_ctor<init> called end--------" + tv);
        }
        /*********Support Call other proxy method END*******************/

        Log.d(TAG, "proxy_getInstalledPackages force return empty result");
        return Collections.emptyList();
    }

    public static void proxy_TextView_ctor(TextView tv, Object context) {
        Log.d(TAG, "proxy_TextView_ctor called+++ " + tv, new Throwable());
        Toast.makeText(DemoApplication.getMyApplication(), "proxy_TextView_ctor called", Toast.LENGTH_SHORT).show();
        ArtHook.callOrigin(tv, context);
        Log.d(TAG, "proxy_TextView_ctor called--- ");
    }

    public static void proxy_Activity_onCreate(Activity activity, Bundle bundle) {
        Log.d(TAG, "proxy_Activity_onCreate called " + activity);
        ArtHook.callOrigin(activity, bundle);
    }
}