
package com.moder.compass.transfer.task.process.download.link;

import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.BaseApplication;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.TransferTask;

/**
 * doNothing Processer com.dubox.drive.task.process.DoNothingProcesser 没有操作，只是为了有信号无网，开启了一次任务调度
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午04:46:41
 */
class StartLinkDownloadSchedulerProcessor extends Processor {
    private TransferTask mTask;

    StartLinkDownloadSchedulerProcessor(TransferTask task) {
        super();
        this.mTask = task;
    }

    @Override
    public void process() {
        if (mTask == null) {
            return;
        }

        BaseApplication.getInstance().getContentResolver()
                .notifyChange(TransferContract.DownloadTasks.SCHEDULER_CONTENT_URI, null, false);
    }
}
