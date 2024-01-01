package com.moder.compass.transfer.transmitter.wifisetting;

/**
 * 禁用WIFI检测
 *
 * @author sunqi01
 */
public class DisableWiFiDetection implements IWiFiDetectionSwitcher {
    private static final String TAG = "DisableWiFiDetection";

    @Override
    public boolean isEnable() {
        return false;
    }
}
