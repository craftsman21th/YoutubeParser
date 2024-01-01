package com.moder.compass.transfer.task.process.download;

import com.moder.compass.BaseApplication;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.log.storage.db.LogProviderHelper;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.DownloadTask;

/**
 * 创建新的下载任务添加到下载列表
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午11:47:58
 */
class NewDownloadTaskProcessor extends Processor {
    private static final String TAG = "NewDownloadTaskProcesser";

    private IDownloadable mFileWrapper;
    private final boolean mIsNotify;
    private final int mDownloadPriority;
    private final String mBduss;
    private final String mUid;

    NewDownloadTaskProcessor(IDownloadable fileWrapper, boolean isNotify, String bduss, String uid,
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
        final LogProviderHelper logProviderHelper = new LogProviderHelper();

        DownloadTask task = processorHelper.createDownloadTaskWithFileWrapper(BaseApplication
                .getInstance(), mFileWrapper);

        if (task == null) {
            return;
        }

        task.setPriority(mDownloadPriority);
        // 由于使用事务批量添加任务，此处不挨个任务通知uri更新，而是在外面批量执行以后一并通知一次
        processorHelper.addDownloadFile(task, mIsNotify, mOnAddTaskListener);
        logProviderHelper.addDownloadFileLog(task.mRemoteUrl, task.getLocalUrl(),
                String.valueOf(mFileWrapper.getFileId()));

        if (mOnProcessListener != null) {
            mOnProcessListener.onItemTaskLoadProcess(task.mTaskId);
        }
    }
}
