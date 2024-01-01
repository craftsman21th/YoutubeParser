package com.moder.compass.transfer.task.process.upload;

import com.moder.compass.transfer.base.UploadInfo;
import com.moder.compass.BaseApplication;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.UploadTask;

/**
 * 添加新的上传任务processer
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-14 下午06:47:39
 */
class NewUploadTaskProcessor extends Processor {
    private final boolean mIsNotify;
    private final String mBduss;
    private final String mUid;
    private final UploadInfo mUploadInfo;

    NewUploadTaskProcessor(UploadInfo info, boolean isNotify, String bduss, String uid) {
        super();
        this.mUploadInfo = info;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
    }

    @Override
    public void process() {
        TransferTask up =
                new UploadTask(BaseApplication.getInstance(), mUploadInfo.getLocalFile(), mUploadInfo.getRemotePath(),
                        mBduss, mUid, mUploadInfo.getConflictStrategy(), mUploadInfo.getQuality());
        new UploadProcessorHelper(mBduss).addTask(up, mIsNotify, mOnAddTaskListener);
    }
}
