package com.moder.compass.transfer.task.process.download;

import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.BaseApplication;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.DownloadTask;

/**
 * 创建下载完成的任务
 *
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午11:47:31
 */
class NewFinishedDownloadTaskProcessor extends Processor {
    private IDownloadable mFileWrapper;
    private final boolean mIsNotify;
    private final int mDownloadPriority;
    private final String mBduss;
    private final String mUid;

    NewFinishedDownloadTaskProcessor(IDownloadable fileWrapper, boolean isNotify, String bduss, String uid,
            int downloadPriority) {
        super();
        this.mFileWrapper = fileWrapper;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
        mDownloadPriority = downloadPriority;
    }

    @Override
    public void process() {
        final DownloadProcessorHelper processorHelper = new DownloadProcessorHelper(mBduss, mUid);

        DownloadTask task = processorHelper.createDownloadTaskWithFileWrapper(BaseApplication.getInstance(),
                mFileWrapper);
        if (task != null) {

            task.setPriority(mDownloadPriority);
            // 由于使用事务批量添加任务，此处不挨个任务通知uri更新，而是在外面批量执行以后一并通知一次
            processorHelper.addFinishedDownloadFile(task, mIsNotify);

            if (mOnProcessListener != null) {
                mOnProcessListener.onItemTaskLoadProcess(task.mTaskId);
            }
        }

    }
}
