package com.example.arthook;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;

import hook.Test;


public class MainActivity extends ButtonActivity {

    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addButton("getPackageManager", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getInstalledPackages called before+++++++");
                List xx = getPackageManager().getInstalledPackages(0);
                Log.d(TAG, "getInstalledPackages called end--------" + xx);
            }
        });

        addButton("test io exception", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new Test().test();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}