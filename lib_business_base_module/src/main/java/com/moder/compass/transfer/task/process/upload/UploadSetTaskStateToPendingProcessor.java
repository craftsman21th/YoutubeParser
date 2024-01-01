
package com.moder.compass.transfer.task.process.upload;

import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.UploadTaskManager;

/**
 * Created by liuliangping on 2015/1/29.
 */
class UploadSetTaskStateToPendingProcessor extends Processor {
    private TransferTask mTask;
    private final String mBduss;
    private final String mUid;

    UploadSetTaskStateToPendingProcessor(TransferTask task, String bduss, String uid) {
        super();
        this.mTask = task;
        mBduss = bduss;
        mUid = uid;
    }

    @Override
    public void process() {
        // 上传任务
        new UploadTaskManager(mBduss, mUid).resumeToPending(mTask.mTaskId);
    }
}
