package hook.android.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.example.arthook.DemoApplication;

import java.util.Collections;
import java.util.List;


import core.apolo.ApoloHook;
import core.apolo.HookName;
import core.apolo.ThisObject;

@HookName("android.app.ApplicationPackageManager")
public class ApplicationPackageManager {

    private static final String TAG = ApplicationPackageManager.class.getSimpleName();

    @HookName("getInstalledPackages")
    public static List<PackageInfo> getInstalledPackages(@ThisObject PackageManager pm, int flags) {
        Log.d(TAG, "proxy_getInstalledPackages called+++ ", new Throwable());
        List<PackageInfo> pkgInfos = ApoloHook.callOrigin(pm, flags);
        Log.d(TAG, "proxy_getInstalledPackages origin list.size: " + pkgInfos.size());

        Toast.makeText(DemoApplication.getMyApplication(), "proxy_getInstalledPackages called", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "proxy_getInstalledPackages force return empty result");
        return Collections.emptyList();

    }
}
