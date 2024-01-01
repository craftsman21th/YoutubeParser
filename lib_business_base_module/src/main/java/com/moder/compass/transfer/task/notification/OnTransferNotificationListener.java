
package com.moder.compass.transfer.task.notification;

import java.util.List;

/**
 * Created by liuliangping on 2015/2/6.
 */
public interface OnTransferNotificationListener {
    /**
     * 更新传输任务通知栏。传输进行中和结束时均调用该方法。
     *
     */
    boolean onTransferNotification(long rate, double progress, int runningCount, int pendingCount, int finishedCount,
            int failedCount, int type, boolean isShowIncrease, List<String> finishedFilesSuffixList);
}
