package com.apolo.helper;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;


public class ProcessUtils {
    private static final String TAG = ProcessUtils.class.getSimpleName();
    private static String sCurProcessName;
    private static Application sMainApplication;
    public static String getCurProcessName(Context context) {
        String procName = sCurProcessName;
        if (!TextUtils.isEmpty(procName)) {
            return procName;
        }
        try {
            int selfPid = Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo info : mActivityManager.getRunningAppProcesses()) {
                if (info.pid == selfPid) {
                    sCurProcessName = info.processName;
                    return sCurProcessName;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getCurProcessName failed", e);
        }
        return sCurProcessName;
    }

    public static Application getMainApplication() {
        if (sMainApplication != null) {
            return sMainApplication;
        }
        RefUtils.MethodRef methodRef_currentActivityThread = new RefUtils.MethodRef(
                "android.app.ActivityThread", true,
                "currentActivityThread", new Class[0]
        );
        RefUtils.FieldRef<List<Application>> fieldRef_mAllApplications = new RefUtils.FieldRef<List<Application>>(
                "android.app.ActivityThread", false, "mAllApplications"
        );
        Object activityThread = methodRef_currentActivityThread.invoke(null, new Object[0]);
        List<Application> allApplications = fieldRef_mAllApplications.get(activityThread);

        if (allApplications != null && allApplications.size() > 0) {
            for (int i = 0; i < allApplications.size(); i++) {
                Application tmp = allApplications.get(i);
                if (TextUtils.equals(tmp.getApplicationInfo().processName,
                        ProcessUtils.getCurProcessName(tmp))) {
                    sMainApplication = tmp;
                    Log.d(TAG, "getMainApplication success");
                    break;
                }
            }
        } else {
            Log.e(TAG, "getMainApplication fail");
        }

        return sMainApplication;
    }
}
