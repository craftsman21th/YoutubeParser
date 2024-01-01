package com.moder.compass.log.transfer;

import android.util.Pair;

import com.moder.compass.transfer.task.MultiTaskScheduler;

/**
 * Created by liuliangping on 2016/3/28.
 */
public class DownloadTransferCalculate implements ITransferCalculable {
    private static final String TAG = "DownloadTransferCalculate";

    /**
     * 文件下载任务调度器
     */
    private MultiTaskScheduler mDownloadTaskScheduler;

    /**
     * 预览任务调度器
     */
    private MultiTaskScheduler mPreviewDownloadTaskScheduler;

    public void setDownloadScheduler(MultiTaskScheduler downloadScheduler) {
        mDownloadTaskScheduler = downloadScheduler;
    }

    public void setPreviewDownloadScheduler(MultiTaskScheduler previewDownloadScheduler) {
        mPreviewDownloadTaskScheduler = previewDownloadScheduler;
    }

    @Override
    public Pair<Integer, Long> calculateTransferTask() {
        int count = 0;
        long rate = 0L;
        if (mDownloadTaskScheduler != null) {
            count += mDownloadTaskScheduler.getStatisticsTaskCount();
            rate += mDownloadTaskScheduler.getStatisticsSumRate(TransferLogType.DOWNLOAD);
        }

        if (mPreviewDownloadTaskScheduler != null) {
            count += mPreviewDownloadTaskScheduler.getStatisticsTaskCount();
            rate += mPreviewDownloadTaskScheduler.getStatisticsSumRate(TransferLogType.DOWNLOAD);
        }
        return Pair.create(count, rate);
    }
}
