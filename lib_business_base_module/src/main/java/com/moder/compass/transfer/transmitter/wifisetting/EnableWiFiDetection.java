package com.moder.compass.transfer.transmitter.wifisetting;

/**
 * 开启传输过程中WIFI 检测
 *
 * @author sunqi01
 */
public class EnableWiFiDetection implements IWiFiDetectionSwitcher {
    private static final String TAG = "EnableWiFiDetection";

    @Override
    public boolean isEnable() {
        return true;
    }
}
