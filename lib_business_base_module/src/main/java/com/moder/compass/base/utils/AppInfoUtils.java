/*
 * AppInfoUtils.java
 * classes : com.dubox.drive.util.AppInfoUtils
 * @author 魏铮铮
 * V 1.0.0
 * Create at 2013-5-23 下午2:50:07
 */
package com.moder.compass.base.utils;

import java.util.Collections;
import java.util.List;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.android.util.ContentUriUtils;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

/**
 * @author 魏铮铮 <br/>
 *         create at 2013-5-23 下午2:50:07
 */
public class AppInfoUtils {
    private static final String TAG = "AppInfo";
    /**
     * 图片类型 *
     */
    public static final String IMAGE_TYPE = "com.dubox.drive.preview.image/*";
    /** 版本号 */
    public static final String SP_APK_VERSION = "apk_version";

    /** 本版本安装时间 */
    public static final String SP_APK_INSTALL_TIME_MILLIS = "sp_apk_install_time_millis";
    /** 用于引导页面区分首次和覆盖安装 */
    public static final String IS_FIRST_INSTALL = "is_first_install";
    /**
     * 获取APK版本号
     *
     * @param ctx
     * @return
     */
    public static String getApkVersionName(Context ctx) {
        return getApkVersionName(ctx, ctx.getPackageName());
    }

    /**
     * 获取APK版本号
     *
     * @param ctx
     * @param strPackageName
     * @return
     */
    private static String getApkVersionName(Context ctx, String strPackageName) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(strPackageName, 0);

            int nSep = pi.versionName.indexOf('-');
            int nSepZ = pi.versionName.indexOf(0);

            if (nSep != -1) {
                if (nSepZ != -1) {
                    if (nSep < nSepZ)
                        return pi.versionName.substring(0, nSep);
                    else
                        return pi.versionName.substring(0, nSepZ);
                } else {
                    return pi.versionName.substring(0, nSep);
                }
            }

            return pi.versionName;
        } catch (NameNotFoundException e) {
            return "";
        } catch (RuntimeException e) {
            return "";
        }
    }

    public static boolean isActivityOnTop() {
        ActivityManager activityManager =
                (ActivityManager) BaseApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo != null && tasksInfo.size() > 0) {
            ComponentName activity = tasksInfo.get(0).topActivity;
            if (activity == null) {
                return false;
            }
            DuboxLog.d(TAG, " AIU DBG isActivityOnTop activity:" + activity.getClassName() + " "
                    + "getPackageName:" + activity.getPackageName());
            if (AppCommon.PACKAGE_NAME.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取能打开给定类型的app列表信息
     *
     * @param type 要打开的类型"text/plain"
     * @param packageName 所需要的包名
     * @return
     */
    public static String getAppListInfo(Context context, String type, String packageName) {

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType(type);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(sendIntent, 0);
        Collections.sort(activities, new ResolveInfo.DisplayNameComparator(packageManager));

        int numActivities = activities.size();
        for (int i = 0; i != numActivities; ++i) {
            final ResolveInfo info = activities.get(i);
            String className = info.activityInfo.name;
            if (className != null && className.contains(packageName)) {
                return className;
            }
        }
        return null;
    }

    /**
     * 通过其他应用编辑
     *
     * @param context
     * @param packageName
     * @param path
     */
    public static final void editWithTargetApp(Context context, String type, String packageName, String path) {
        String className = getAppListInfo(context, type, packageName);
        if (className != null && packageName != null) {
            ComponentName component = new ComponentName(packageName, className);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setComponent(component);
            Uri uri = new ContentUriUtils().getTargetUri(context, path);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType(type);
            context.startActivity(intent);
        }
    }

    /**
     * 安装Apk文件
     *
     * @param context
     * @param fileuri URI 文件URI
     */
    public static final void installApkFile(Context context, Uri fileuri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(fileuri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * 是否是覆盖安装
     */
    public static boolean isCoverInstall() {
        return !TextUtils.isEmpty(GlobalConfig.getInstance().getString(SP_APK_VERSION))
            && !GlobalConfig.getInstance().getString(SP_APK_VERSION)
            .equals(AppInfoUtils.getApkVersionName(BaseApplication.getInstance()));
    }

    /**
     * 是首次安装
     */
    public static boolean isFirstInstall() {
        return !GlobalConfig.getInstance().has(SP_APK_VERSION);
    }


}