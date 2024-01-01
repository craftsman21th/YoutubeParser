package com.moder.compass.base.storage.config;

import static com.dubox.drive.basemodule.BuildConfig.EXT_DEFAULT_DOWNLOAD_DIR;
import static com.dubox.drive.basemodule.BuildConfig.EXT_DOWNLOAD_DIR;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import com.dubox.drive.base.network.NetworkException;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageUtils;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.util.CloudFileHelper;

import java.io.File;

public class Setting {

    private static final String BRAND_OPPO = "OPPO";
    public static final String PREF_SETTINGS = "Setting";
    public static final String KEY_DEFAULT_DIR = "default_directory";
    private final static String TAG = "Setting";
    /**
     *  保险箱文件夹路径
     */
    public static final String SAFE_BOX_DIR = "_pcs_.safebox";

    /**
     * app外部私有存储目录
     */
    private static final String PREVIEW_DEFAULT_FOLDER =
        DeviceStorageUtils.getExternalCacheDir().getAbsolutePath() + File.separator + CloudFileHelper.PREVIEW_CACHE_PATH
            + EXT_DEFAULT_DOWNLOAD_DIR;
    /**
     * 外部下载目录,文件下载相对路径
     */
    public static final String DOWNLOAD_RELATIVE_DIR = EXT_DOWNLOAD_DIR;

    /**
     * target30升级后，目前OppO Reno2 android11 通过uri在Download创建的moder目录，被修改为moder
     */
    public static final String DOWNLOAD_RELATIVE_DIR_OPP = EXT_DOWNLOAD_DIR;

    /**
     * 每次注销的时候将网络异常的开关打开（一次登录提示一次）
     *
     * @param open
     */
    public static void setNetworkExceptionDialogSwitcher(boolean open) {
        GlobalConfig.getInstance().putBoolean(NetworkException.NETWORK_EXCEPTION_SWITCHER, open);
        GlobalConfig.getInstance().commit();
    }

    /**
     * 获取 /storage/emulated/0/DuboxDownloads目录
     * @return
     */
    public static String getDefaultFolder() {
        return DeviceStorageUtils.getSDPath() + EXT_DEFAULT_DOWNLOAD_DIR;
    }

    /**
     * 设配target30文件存储下载目录
     * @param context
     * @return
     */
    public static String getDownloadDir(Context context) {
        if (Target30StorageKt.isPartitionStorage()) {
//            if (BRAND_OPPO.equalsIgnoreCase(Build.BRAND)) {
//                return Environment.getExternalStoragePublicDirectory(Environment
//                        .DIRECTORY_DOWNLOADS).getAbsolutePath() + DOWNLOAD_RELATIVE_DIR_OPP;
//            }
            return Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_DOWNLOADS).getAbsolutePath() + DOWNLOAD_RELATIVE_DIR;
        }
        return getDefaultSaveDir(context);
    }

    /**
     * 判断下载目录是否可用，兼容老版本，
     * 如果不存在，将会创建，
     * @return
     */
    public static boolean isDownloadDirAvailable(Context context) {
        // target30分区存储后下载目录不需要判断是否存在
        if (Target30StorageKt.isPartitionStorage()) {
            return true;
        }
        String defaultDir = getDefaultSaveDir(context);
        if (TextUtils.isEmpty(defaultDir)) {
            return false;
        }
        File file = new File(defaultDir);
        if (!file.exists() || !file.isDirectory()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static String getDefaultSaveDir(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_SETTINGS, 0);
        // 替换为Utilconfig存储同时保持原有版本的兼容
        String downloadDir = getDefaultFolder();
        String settingDefaultDir = settings.getString(KEY_DEFAULT_DIR, downloadDir);
        String configDefaultDir = PersonalConfig.getInstance().getString(KEY_DEFAULT_DIR, downloadDir);
        // 兼容之前的旧版本，新版本都使用utilconfig
        if (settingDefaultDir != null && !settingDefaultDir.equals(downloadDir)) {
            downloadDir = settingDefaultDir;
            PersonalConfig.getInstance().putString(KEY_DEFAULT_DIR, downloadDir);
            PersonalConfig.getInstance().commit();
        } else if (!configDefaultDir.equals(downloadDir)) {
            downloadDir = configDefaultDir;
        }
        return downloadDir;
    }

    /**
     * 获取预览的默认目录
     * 为外部私有存储目录，可以直接使用File访问
     * @return
     */
    public static String getPreviewDefaultSaveDir() {
        File file = new File(PREVIEW_DEFAULT_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return PREVIEW_DEFAULT_FOLDER;
    }


}
