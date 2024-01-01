
package com.moder.compass.transfer.task.process.download;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.TransferTask;

/**
 * Created by liuliangping on 2015/1/29.
 */
public class DownloadSetTaskStateToPendingProcessor extends Processor {

    private static final String TAG = "NewOtherFinishedDownloadTaskProcessor";

    private TransferTask task;
    private final int mDownloadPriority;
    private final String mBduss;
    private final String mUid;

    public DownloadSetTaskStateToPendingProcessor(String bduss, String uid, TransferTask task, int priority) {
        super();
        mBduss = bduss;
        mUid = uid;
        this.task = task;
        mDownloadPriority = priority;
    }

    @Override
    public void process() {
        if (task == null) {
            return;
        }
        if (mOnProcessListener != null) {
            mOnProcessListener.onItemTaskLoadProcess(task.mTaskId);
        }
        new DownloadProcessorHelper(mBduss, mUid).resumeToPendingUpdateStates(task, mDownloadPriority);

        DuboxLog.d(TAG, "taskPath = " + task.getLocalUrl());

    }
}
