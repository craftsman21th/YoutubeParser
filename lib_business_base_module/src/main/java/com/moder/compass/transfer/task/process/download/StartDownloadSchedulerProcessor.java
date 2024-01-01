package com.moder.compass.transfer.task.process.download;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_DOWNLOAD_UPDATE;

import android.os.Bundle;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.EventCenterHandler;
import com.moder.compass.transfer.base.Processor;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.TransferTask;

/**
 * doNothing Processer com.dubox.drive.task.process.DoNothingProcesser 没有操作，只是为了有信号无网，开启了一次任务调度
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午04:46:41
 */
class StartDownloadSchedulerProcessor extends Processor {
    private TransferTask mTask;

    StartDownloadSchedulerProcessor(String bduss, TransferTask task, int priority) {
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
        // new DownloadProcessorHelper(mBduss).updateStates(mTask, mPriority); 预览下载分开不需要重启调度器

        if (mOnProcessListener != null) {
            mOnProcessListener.onItemTaskLoadProcess(mTask.mTaskId);
        }


        // 发送广播，通知接收 libin09 2015-4-17
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.REMOTE_URL, mTask.mRemoteUrl);
        data.putString(TransferContract.Tasks.LOCAL_URL, mTask.getLocalUrl());
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_DOWNLOAD_UPDATE, mTask.mTaskId,
                TransferContract.Tasks.STATE_FINISHED, data);
    }
}
