package com.moder.compass.transfer.transmitter.wifisetting;

import com.moder.compass.base.utils.NetConfigUtil;

/**
 * 根据设置页里的仅在WIFI下上传下载的开关，动态控制传输器是否检测WIFI
 *
 * @author sunqi01
 */
public class SwitchWiFiDetectionBySettings implements IWiFiDetectionSwitcher {
    private static final String TAG = "SwitchWiFiDetectionBySettings";

    @Override
    public boolean isEnable() {
        return NetConfigUtil.isWiFiOnlyChecked();
    }
}
