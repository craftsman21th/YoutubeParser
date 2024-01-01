package com.moder.compass.transfer.task.process.upload;

import com.moder.compass.BaseApplication;
import com.moder.compass.transfer.base.UploadInfo;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.UploadTask;

/**
 * 新建上传任务并且除去旧的任务记录
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-14 下午06:50:58
 */
class NewUploadTaskAndRemoveLastTaskProcessor extends Processor {
    private TransferTask oldTask;
    private final boolean mIsNotify;
    private final String mBduss;
    private final String mUid;
    private final UploadInfo mUploadInfo;

    NewUploadTaskAndRemoveLastTaskProcessor(UploadInfo uploadInfo, TransferTask oldTask, boolean isNotify,
            String bduss, String uid) {
        super();
        this.mUploadInfo = uploadInfo;
        this.oldTask = oldTask;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
    }

    @Override
    public void process() {
        final UploadProcessorHelper helper = new UploadProcessorHelper(mBduss);
        helper.cancelTask(oldTask.mTaskId);// 删除原来的任务
        TransferTask up =
                new UploadTask(BaseApplication.getInstance(), mUploadInfo.getLocalFile(), mUploadInfo.getRemotePath(),
                        mBduss, mUid, mUploadInfo.getConflictStrategy(), mUploadInfo.getQuality());
        helper.addTask(up, mIsNotify, mOnAddTaskListener);
    }
}
