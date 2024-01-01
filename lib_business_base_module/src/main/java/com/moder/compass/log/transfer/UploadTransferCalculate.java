package com.moder.compass.log.transfer;

import android.util.Pair;

import com.moder.compass.transfer.task.MultiTaskScheduler;

/**
 * Created by liuliangping on 2016/3/28.
 */
public class UploadTransferCalculate implements ITransferCalculable {
    private static final String TAG = "UploadTransferCalculate";

    /**
     * 文件上传任务调度器
     */
    private MultiTaskScheduler mUploadTaskScheduler;

    /**
     * 云端点对点分享文件上传任务调度器
     */
    private MultiTaskScheduler mMessageUploadTaskScheduler;

    public void setUploadScheduler(MultiTaskScheduler uploadScheduler) {
        mUploadTaskScheduler = uploadScheduler;
    }

    public void setMessageUploadScheduler(MultiTaskScheduler messageUploadScheduler) {
        mMessageUploadTaskScheduler = messageUploadScheduler;
    }

    @Override
    public Pair<Integer, Long> calculateTransferTask() {
        int count = 0;
        long rate = 0L;
        if (mUploadTaskScheduler != null) {
            count += mUploadTaskScheduler.getStatisticsTaskCount();
            rate += mUploadTaskScheduler.getStatisticsSumRate(TransferLogType.UPLOAD);
        }

        if (mMessageUploadTaskScheduler != null) {
            count += mMessageUploadTaskScheduler.getStatisticsTaskCount();
            rate += mMessageUploadTaskScheduler.getStatisticsSumRate(TransferLogType.UPLOAD);
        }
        return Pair.create(count, rate);
    }
}
