/*
 * DownloadTransmitter.java
 * @author libin09
 * V 1.0.0
 * Create at 2014-1-24 上午11:23:20
 */
package com.moder.compass.transfer.transmitter;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.log.ILogGenerator;
import com.moder.compass.log.transfer.DownloadTransferLogGenerator;
import com.moder.compass.log.transfer.InstantDownloadLog;
import com.moder.compass.log.transfer.InstantLogGenerator;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.statuscallback.ITransferStatusCallback;

/**
 *
 * @author libin09 <br/>
 *         create at 2014-1-24 上午11:23:20
 */
public abstract class DownloadTransmitter extends Transmitter {
    private static final String TAG = "DownloadTransmitter";
    protected InstantDownloadLog mInstantDownloadLog;
    protected ILogGenerator mInstantLogGenerator;

    /**
     * 随接口下发的展示文案
     */
    protected String errMsgForShow = null;

    /**
     * @param taskId
     * @param options
     */
    public DownloadTransmitter(int taskId, TransmitterOptions options) {
        super(taskId, options);
        mLogGenerator = new DownloadTransferLogGenerator();
        mInstantLogGenerator = new InstantLogGenerator();
    }

    /**
     * @param size
     * @param rate
     *
     * @see Transmitter#calculate(long, long)
     */
    @Override
    protected void calculate(long size, long rate) {
        mOffsetSize += size;
        if (rate >= 0L) {
            mRate = rate;
        }
        
        if (mTransferLog != null) {
            mTransferLog.setOffsetSize(mOffsetSize);
            mTransferLog.setFileRate(rate);

            // 记录当前任务的瞬时速度
            if (mInstantLogGenerator != null && mInstantDownloadLog.isCanCalculateInstantSpeed(mOffsetSize)) {
                mInstantSpeed = mInstantDownloadLog.getInstantSpeed();
                if (mInstantSpeed > 0L) {
                    mLogTaskManager.addLogTask(mInstantLogGenerator, mInstantDownloadLog);
                    mInstantLogGenerator = null;
                }
            }
        }

        if (mOptions != null && mOptions.getStatusCallback() != null) {
            final int result = mOptions.getStatusCallback().onUpdate(mOffsetSize, rate);

            if (result <= 0) {// 如果更新数据库没效果，说明task已经被删除，提前停止传输，优化性能
                DuboxLog.d(TAG, "pause since calculate failed");
                pause();
            }
        }
        DuboxLog.d(TAG, "calculate mOffsetSize:" + mOffsetSize);
    }

    protected boolean isNetError(int errorCode) {
        return errorCode == TransmitterConstant.NETWORK_NOT_AVAILABLE
                || errorCode == TransmitterConstant.NETWORK_NO_CONNECTION
                || errorCode == TransmitterConstant.WAITING_FOR_WIFI;
    }

    @Override
    public void pause() {

        // 回调业务层
        if (mOptions.getStatusCallback() != null && (mOptions.getStatusCallback() instanceof ITransferStatusCallback)) {
            ((ITransferStatusCallback) mOptions.getStatusCallback()).onPause();
        }
    }
}
