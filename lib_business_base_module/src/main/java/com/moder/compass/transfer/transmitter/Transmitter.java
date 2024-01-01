package com.moder.compass.transfer.transmitter;

import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;
import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.android.util.network.NetWorkVerifier;
import com.moder.compass.log.ILogGenerator;
import com.moder.compass.log.LogTaskManager;
import com.moder.compass.log.transfer.TransferLog;

import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.throwable.Retry;

/**
 * 传输控制器基类，用于实现上传下载等传输逻辑
 *
 * @author 孙奇 <br/>
 *         create at 2012-11-30 下午05:05:54
 */
public abstract class Transmitter {
    private static final String TAG = "Transmitter";

    /**
     * 进度更新间隔时间
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-2 下午06:17:19
     */
    public static final int PROGRESS_UPDATE_INTERVAL = 500; // 1s
    /**
     * 重试间隔5秒
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午08:10:24
     */
    protected static final int RETRY_DELAY = 5000;

    /**
     * 重试最大次数
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午08:43:59
     */
    protected static final int RETRY_MAX_TIMES = 2;
    /**
     * 重试次数，在start的时候重新初始化
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午08:10:14
     */
    protected int retryTimes = 0;

    /**
     * 有信号无网计数器
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-28 下午02:09:34
     */
    protected int signalNetworkProcessRetryTimes = 0;

    protected volatile boolean isPause = false;

    protected int mTaskId;

    protected long mRate;

    public long mOffsetSize;

    protected long mInstantSpeed = 0L;

    /**
     * 传输器配置项
     */
    protected TransmitterOptions mOptions;


    /**
     * 日志统计相关 liuliangping
     */
    protected TransferLog mTransferLog;
    protected LogTaskManager mLogTaskManager;
    protected ILogGenerator mLogGenerator;

    public Transmitter(int taskId, TransmitterOptions options) {
        mTaskId = taskId;
        mOptions = options;

        mLogTaskManager = new LogTaskManager();
    }

    /**
     * 开始传输
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午08:13:53
     */
    public void start() {
        retryTimes = 0;
        signalNetworkProcessRetryTimes = 0;
        prepareTransmit();
        transmit(null);
    }

    /**
     * 暂停传输
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午08:14:04
     */
    public abstract void pause();

    /**
     * 停止
     */
    public void stop() {

    }

    protected abstract void prepareTransmit();

    protected abstract void calculate(long size, long rate);

    /**
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午08:47:18
     */
    protected abstract void transmit(TransmitBlock transmitBean);

    public abstract void remove(boolean isDeleteFile);

    /**
     * 在传输数据过程中检测WIFI
     *
     * @return
     */
    protected boolean isWaitingWiFi() {
        return mOptions.isWiFiDetectionEnable() && !ConnectivityState.isWifi(BaseApplication.getInstance());
    }

    /**
     * 重试
     *
     * @param t
     *
     * @throws StopRequestException
     */
    protected abstract void doRetry(Retry t) throws StopRequestException;

    /**
     * 有信号无网检测
     *
     * @throws StopRequestException
     */
    protected boolean networkVerifierCheck() throws StopRequestException {
        if (signalNetworkProcessRetryTimes >= RETRY_MAX_TIMES) {
            DuboxLog.d(TAG, "networkVerifierCheck::signalNetworkProcessRetryTimes >= RETRY_MAX_TIMES");
            throw new StopRequestException(OtherErrorCode.CHECK_SIGNAL_NETWORK_RETRY_OVER_TIME,
                    "signal network retry time over");
        } else {
            signalNetworkProcessRetryTimes++;
            DuboxLog.d(TAG, "networkVerifierCheck::signalNetworkProcessRetryTimes = "
                    + signalNetworkProcessRetryTimes);
            if (NetWorkVerifier.syncCheck(HostURLManager.defaultVerifierHostName())) {
                DuboxLog.d(TAG, "networkVerifierCheck::NetWorkVerifier.syncCheck() true");
                throw new StopRequestException(TransmitterConstant.NETWORK_NOT_AVAILABLE, "NetWorkVerifier.syncCheck "
                        + TransmitterConstant.getExceptionMsg(TransmitterConstant.NETWORK_NOT_AVAILABLE));
            }
            return false;
        }
    }


    public long getRate() {
        return mRate;
    }

    public long getInstantSpeed() {
        return mInstantSpeed;
    }

    /**
     * @return the mOffsetSize
     */
    public long getOffsetSize() {
        return mOffsetSize;
    }

    public void resetRateCalculator() {
        if (mOptions.getRateCalculator() != null) {
            mOptions.getRateCalculator().reset();
        }
    }

    /**
     * 检测网络连接
     *
     * @throws StopRequestException
     */
    public void checkConnectivity() throws StopRequestException {
        if (!ConnectivityState.isConnected(BaseApplication.getInstance())) {
            throw new StopRequestException(TransmitterConstant.NETWORK_NO_CONNECTION,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.NETWORK_NO_CONNECTION));
        }
        if (mOptions.isNetworkVerifier() && NetWorkVerifier.isNoNetwork()) {
            throw new StopRequestException(TransmitterConstant.NETWORK_NOT_AVAILABLE,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.NETWORK_NOT_AVAILABLE));
        }
        if (isWaitingWiFi()) {
            throw new StopRequestException(TransmitterConstant.WAITING_FOR_WIFI,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.WAITING_FOR_WIFI));
        }
    }
}
