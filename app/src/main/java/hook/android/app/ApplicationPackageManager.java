package hook.android.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.example.apolo.DemoApplication;

import java.util.Collections;
import java.util.List;


import org.apolo.ArtEngine;
import org.apolo.HookName;
import org.apolo.ThisObject;
import hook.utils.Slog;

@HookName("android.app.ApplicationPackageManager")
public class ApplicationPackageManager {

    private static final String TAG = ApplicationPackageManager.class.getSimpleName();

    @HookName("getInstalledPackages")
    public static List<PackageInfo> getInstalledPackages(@ThisObject PackageManager pm, int flags) {
        Slog.d(TAG, "proxy_getInstalledPackages called+++ ", new Throwable());
        List<PackageInfo> pkgInfos = ArtEngine.callOrigin(pm, flags);
        Slog.d(TAG, "proxy_getInstalledPackages origin list.size: " + pkgInfos.size());

        Toast.makeText(DemoApplication.getMyApplication(), "proxy_getInstalledPackages called", Toast.LENGTH_SHORT).show();

        Slog.d(TAG, "proxy_getInstalledPackages force return empty result");
        return Collections.emptyList();

    }
}
