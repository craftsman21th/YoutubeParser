package com.moder.compass.transfer.transmitter;

import com.moder.compass.transfer.transmitter.ratecaculator.IRateCalculator;
import com.moder.compass.transfer.transmitter.statuscallback.IStatusCallback;
import com.moder.compass.transfer.transmitter.wifisetting.IWiFiDetectionSwitcher;
import com.moder.compass.log.transfer.ITransferCalculable;

import com.moder.compass.transfer.transmitter.ratelimiter.IRateLimitable;
import com.moder.compass.transfer.transmitter.wifisetting.DisableWiFiDetection;

/**
 * 传输器配置
 *
 * @author sunqi01
 */
public final class TransmitterOptions {
    private static final String TAG = "TransmitterOptions";

    /**
     * 传输过程中是否检测有信号无网
     */
    private boolean mIsNetworkVerifier;

    private IStatusCallback mStatusCallback;

    private IRateCalculator mTransmitRateCalculator;

    private IWiFiDetectionSwitcher mWiFiDetectionSwitcher;

    private boolean mIsPowerCheckEnable;

    private IRateLimitable mRateLimiter;

    private ITransferCalculable mTransferCalculable;

    private InternetBackupSwitchDetection mInternetBackupSwitchDetection;

    private TransmitterOptions(Builder builder) {
        mIsNetworkVerifier = builder.isNetworkVerifier;
        mStatusCallback = builder.statusCallback;
        mTransmitRateCalculator = builder.transmitRateCalculator;
        mWiFiDetectionSwitcher = builder.wifiDetectionSwitcher;
        mIsPowerCheckEnable = builder.isPowerCheckEnable;
        mRateLimiter = builder.rateLimiter;
        mTransferCalculable = builder.transferCalculable;
        mInternetBackupSwitchDetection = builder.internetBackupSwitchDetection;
    }

    /**
     * WIFI检测是否开启
     *
     * @return
     */
    boolean isWiFiDetectionEnable() {
        try {
            return this.mWiFiDetectionSwitcher.isEnable();
        } catch (SecurityException e) {
            return false;
        }
    }

    /**
     * 是否检测图片流量备份开关
     *
     * @return
     */
    public boolean isCheckPhotoInternetBackup() {
        return (mInternetBackupSwitchDetection != null && mInternetBackupSwitchDetection.needCheckPhotoType());
    }

    /**
     * 是否检测视频流量备份开关
     *
     * @return
     */
    public boolean isCheckVideoInternetBackup() {
        return (mInternetBackupSwitchDetection != null && mInternetBackupSwitchDetection.needCheckVideoType());
    }

    /**
     * 是否检查文件夹自动备份开关
     * @return
     */
    public boolean isCheckDirectoryInternetBackup() {
        return (mInternetBackupSwitchDetection != null && mInternetBackupSwitchDetection.needCheckDirectoryType());
    }

    /**
     * 开启仅在WIFI下传输
     *
     * @return
     */
    public boolean isOnlyWiFiEnable() {
        return false;
    }

    /**
     * 有信号无网的检测是否开启
     *
     * @return
     */
    public boolean isNetworkVerifier() {
        return this.mIsNetworkVerifier;
    }

    /**
     * 获取传输状态回调
     *
     * @return
     */
    public IStatusCallback getStatusCallback() {
        return this.mStatusCallback;
    }

    /**
     * 速率计算是否使能
     *
     * @return
     */
    public boolean isRateCalculateEnable() {
        return mTransmitRateCalculator != null;
    }

    /**
     * 是否检测电量
     *
     * @return
     */
    boolean isPowerCheckEnable() {
        return mIsPowerCheckEnable;
    }

    /**
     * 获取TransmitRateCalculator
     *
     * @return
     */
    public IRateCalculator getRateCalculator() {
        return mTransmitRateCalculator;
    }

    /**
     * 获取限速器
     * 
     * @return
     */
    public IRateLimitable getRateLimiter() {
        return mRateLimiter;
    }

    /**
     * 获取传输计算器
     *
     * @return
     */
    public ITransferCalculable getTransferCalculable() {
        return mTransferCalculable;
    }

    public static final class Builder {
        boolean isPowerCheckEnable = false;
        private boolean isNetworkVerifier = true;
        private IStatusCallback statusCallback = null;
        private IRateCalculator transmitRateCalculator = null;
        private IWiFiDetectionSwitcher wifiDetectionSwitcher = new DisableWiFiDetection();
        private IRateLimitable rateLimiter;
        private ITransferCalculable transferCalculable;
        private InternetBackupSwitchDetection internetBackupSwitchDetection;


        /**
         * 设置传输过程中WIFI检测是否开启的控制器
         *
         * @param switcher
         *
         * @return
         */
        public Builder setWiFiDetectionSwitcher(IWiFiDetectionSwitcher switcher) {
            wifiDetectionSwitcher = switcher;
            return this;
        }

        /**
         * 设置传输过程中是否检查图片、视频流量备份开关
         *
         * @param detection
         *
         * @return
         */
        public Builder setInternetBackupSwitchDetection(InternetBackupSwitchDetection detection) {
            internetBackupSwitchDetection = detection;
            return this;
        }

        /**
         * 设置有信号无网的检测
         *
         * @param enable
         *
         * @return
         */
        public Builder setNetworkVerifier(boolean enable) {
            isNetworkVerifier = enable;
            return this;
        }

        /**
         * 设置状态回调
         *
         * @param callback
         *
         * @return
         */
        public Builder setStatusCallback(IStatusCallback callback) {
            statusCallback = callback;
            return this;
        }

        /**
         * 设置传输速率计算器
         *
         * @param calculator
         *
         * @return
         */
        public Builder setRateCalculator(IRateCalculator calculator) {
            transmitRateCalculator = calculator;
            return this;
        }

        /**
         * 是否检测电量
         *
         * @param enable
         *
         * @return
         */
        public Builder setPowerCheckEnable(boolean enable) {
            isPowerCheckEnable = enable;
            return this;
        }

        public Builder setRateLimiter(IRateLimitable limiter) {
            rateLimiter = limiter;
            return this;
        }

        public Builder setTransferCalculable(ITransferCalculable calculable) {
            transferCalculable = calculable;
            return this;
        }

        public TransmitterOptions build() {
            return new TransmitterOptions(this);
        }

    }
}
