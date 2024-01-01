package com.moder.compass.transfer.task.process.upload;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_UPDATE;

import android.os.Bundle;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.EventCenterHandler;
import com.moder.compass.transfer.base.Processor;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.dubox.drive.db.transfer.contract.TransferContract.UploadTasks;
import com.moder.compass.transfer.task.TransferTask;

/**
 * doNothing Processer com.dubox.drive.task.process.DoNothingProcesser 没有操作，只是为了有信号无网，开启了一次任务调度
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午04:46:41
 */
class StartUploadSchedulerProcessor extends Processor {
    private TransferTask mTask;

    StartUploadSchedulerProcessor(String bduss, TransferTask task) {
        super();
        this.mTask = task;
    }

    @Override
    public void process() {
        if (mTask == null) {
            return;
        }

        BaseApplication.getInstance().getContentResolver().notifyChange(UploadTasks.SCHEDULER_CONTENT_URI, null, false);

        if (mOnProcessListener != null) {
            mOnProcessListener.onItemTaskLoadProcess(mTask.mTaskId);
        }


        // 发送广播，通知接收 libin09 2015-4-17
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.LOCAL_URL, mTask.mLocalFileMeta.localUrl());
        data.putString(TransferContract.Tasks.REMOTE_URL, mTask.mRemoteUrl);
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_UPDATE, mTask.mTaskId,
                TransferContract.Tasks.STATE_FINISHED, data);
    }
}
