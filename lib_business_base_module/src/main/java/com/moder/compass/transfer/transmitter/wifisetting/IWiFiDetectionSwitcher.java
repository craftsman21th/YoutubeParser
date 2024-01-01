package com.moder.compass.transfer.transmitter.wifisetting;

/**
 * 用于设置是否开启传输过程中WIFI状态检测
 *
 * @author sunqi01
 */
public interface IWiFiDetectionSwitcher {
    /**
     * wifi检测是否开启
     *
     * @return
     */
    boolean isEnable();
}
