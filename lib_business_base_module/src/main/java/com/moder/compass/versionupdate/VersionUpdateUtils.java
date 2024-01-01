package com.moder.compass.versionupdate;

import java.util.Date;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by liji01 on 15-1-23.
 */
public class VersionUpdateUtils {
    private static final String TAG = "VersionUpdateUtils";

    private static final int VERSION_NAME_LENGTH = 3;
    private static final String LASH_TIPS_VERSION = "last_tips_version";

    /**
     * 检查当天是否更新过,只要小于1天都true
     * 
     * @return
     */
    private static boolean hasCheckUpdatedToday(String key) {
        long updatetime = GlobalConfig.getInstance().getLong(key, -1);
        long currentTime = new Date().getTime();

        // if (isChecking) {
        // DuboxLog.v(TAG, "isChecking = " + isChecking);
        // return true;
        // }

        if (-1 != updatetime && 24 * 60 * 60 * 1000 >= currentTime - updatetime) {
            DuboxLog.d(TAG, "has updated today. updatetime:" + updatetime + ",currentTime - updatetime:"
                    + (currentTime - updatetime));
            return true;
        }

        // isChecking = true;
        return false;
    }

    /**
     * 使用过VersionName进行版本比较
     * 
     * @param versionServer
     * @return
     */
    public static boolean checkVersionUpdate(String versionServer, int versionCode) {
        DuboxLog.d(TAG, "version=" + AppCommon.VERSION_DEFINED);
        if (TextUtils.isEmpty(versionServer)) {
            return false;
        }
        boolean isNeedUpdate = checkUpgradeByVersionName(versionServer);
        if (!isNeedUpdate) {
            // 如果versionname判断不需要升级的情况下再判断versioncode
            if (versionCode > getVersionCode()) {
                isNeedUpdate = true;
            }
        }
        return isNeedUpdate;
    }

    /**
     * 通过versionName检查升级
     *
     * @param versionNameServer 目标版本号
     * @return true:升级；false:不升级
     */
    public static boolean checkUpgradeByVersionName(@NonNull String versionNameServer) {
        boolean isNeedUpdate = false;
        String[] server = versionNameServer.split("\\.");
        String[] local = (AppCommon.VERSION_DEFINED).split("\\.");
        DuboxLog.d(TAG, "" + versionNameServer);
        DuboxLog.d(TAG, "" + AppCommon.VERSION_DEFINED);
        if (server.length != local.length) {
            isNeedUpdate = false;
        } else {
            for (int i = 0; i < server.length; i++) {
                try {
                    final int serverVersion = Integer.parseInt(server[i]);
                    final int localVersion = Integer.parseInt(local[i]);
                    if (serverVersion > localVersion) {
                        isNeedUpdate = true;
                        break;
                    } else if (serverVersion < localVersion) {
                        break;
                    }
                } catch (NumberFormatException e) {
                    DuboxLog.w(TAG, "解析版本号出错", e);
                    break;
                }
            }
        }
        return isNeedUpdate;
    }

    /**
     * 获取版本信息
     *
     * @return
     */
    public static int getVersionCode() {
        Context context = BaseApplication.getInstance();
        PackageManager pm = context.getPackageManager();
        int versionCode = 0;
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            DuboxLog.e(TAG, "", e);
        }
        return versionCode;
    }

    /**
     *
     * @param v1
     * @param v2
     * @return 如果版本1 大于 版本2 GREATER 小于：LESSER 等于：EQUAL 版本信息错误：ERROR
     */
    public static VersionCompare compareVersions(String v1, String v2) {
        //判断是否为空数据
        if (TextUtils.isEmpty(v1) || TextUtils.isEmpty(v2)) {
            return VersionCompare.ERROR;
        }
        String[] str1 = v1.split("\\.");
        String[] str2 = v2.split("\\.");
        // 版本号长度小于当前版本长度时，证明是非法版本号
        if (str1.length < VERSION_NAME_LENGTH || str2.length < VERSION_NAME_LENGTH) {
            return VersionCompare.ERROR;
        }

        // 线下版本和线上版本比较时只比较前三位
        for (int i = 0; i < VERSION_NAME_LENGTH; i++) {
            try {
                if (Integer.parseInt(str1[i]) > Integer.parseInt(str2[i])) {
                    return VersionCompare.GREATER;
                }
                if (Integer.parseInt(str1[i]) < Integer.parseInt(str2[i])) {
                    return VersionCompare.LESSER;
                }
            } catch (NumberFormatException e) {
                DuboxLog.e(TAG, "Version name is error!");
                return VersionCompare.ERROR;
            }

        }
        return VersionCompare.EQUAL;
    }

    public enum VersionCompare {
        GREATER,
        LESSER,
        EQUAL,
        ERROR
    }

    public static boolean isNeedTipsUpdate(String newVersion) {
        String lastVersion = GlobalConfig.getInstance().getString(LASH_TIPS_VERSION);
        return TextUtils.isEmpty(lastVersion) || compareVersions(newVersion, lastVersion) == VersionCompare.GREATER;
    }

    public static void updateLastTipsVersion(String version) {
        GlobalConfig.getInstance().putString(LASH_TIPS_VERSION, version);
        GlobalConfig.getInstance().commit();
    }
}
