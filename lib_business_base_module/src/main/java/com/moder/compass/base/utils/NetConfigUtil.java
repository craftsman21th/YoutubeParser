
package com.moder.compass.base.utils;

import com.dubox.drive.cloudfile.storage.config.TransferFileConfigKey;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 流量控制工具类
 * Created by liuliangping on 2015/2/9.
 */
public class NetConfigUtil {
    private static final String TAG = "NetConfigUtil";

    /**
     * 仅wifi条件下使用网络进行传输的快关，默认开。
     */
    private static boolean isWifiOnly = PersonalConfig.getInstance().getBoolean(TransferFileConfigKey.SETTING_USE_WIFI_ONLY, true);

    /**
     * 返回仅WiFi传输开关状态
     */
    public static boolean isWiFiOnlyChecked() {
        return isWifiOnly;
    }

    public static void resetWifiOnlyCheck() {
        isWifiOnly = true;
    }

    /**
     * 设置仅WiFi传输开关状态
     */
    public static void setWiFiOnlyCheckedConfig(boolean isChecked) {
        DuboxLog.d(TAG, "setWiFiOnlyCheckedConfig:" + isChecked);
        // 如果状态发生改变，发送广播通知插件状态发生改变
        if (isWiFiOnlyChecked() != isChecked) {
//            Intent intent = new Intent(PlatformBroadcast.ACTION_WIFI_DOWNLOAD_ONLY);
//            intent.putExtra(PlatformBroadcast.EXTRA_DOWNLOAD_WIFI_ONLY_STATE, isChecked);
//            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);
        }
        isWifiOnly = isChecked;

        // 更新设置页开关状态
        PersonalConfig.getInstance().putBoolean(TransferFileConfigKey.SETTING_USE_WIFI_ONLY, isChecked);
        PersonalConfig.getInstance().asyncCommit();
    }
}
