package com.moder.compass.transfer.task.process.download;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.BaseApplication;
import com.moder.compass.log.storage.db.LogProviderHelper;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.DownloadTask;
import com.moder.compass.transfer.task.TransferTask;

/**
 * 重新下载并且覆盖原来文件处理类 com.dubox.drive.task.process.DownloadAndCoverProcesser
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午02:58:01
 */
public class NewDownloadTaskAndRemoveLastTaskProcessor extends Processor {
    private static final String TAG = "NewDownloadTaskAndRemoveLastTaskProcessor";
    private IDownloadable mFileWrapper;
    private TransferTask mTask;
    private final boolean mIsNotify;
    private final int mPriority;
    private final String mBduss;
    private final String mUid;

    NewDownloadTaskAndRemoveLastTaskProcessor(IDownloadable fileWrapper, TransferTask task, boolean isNotify,
            String bduss, String uid, int downloadPriority) {
        super();
        this.mFileWrapper = fileWrapper;
        this.mTask = task;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
        mPriority = downloadPriority;
    }

    @Override
    public void process() {
        // sunqi 重新下载并且删除原来的文件
        final DownloadProcessorHelper processorHelper = new DownloadProcessorHelper(mBduss, mUid);
        final LogProviderHelper logProviderHelper = new LogProviderHelper();

        if (mTask != null) {
            processorHelper.cancelTask(mTask.mTaskId); // 删除原来的任务
        }

        DownloadTask newTask = processorHelper.createDownloadTaskWithFileWrapper( // 创建新任务
                BaseApplication.getInstance(), mFileWrapper);

        if (newTask == null) {
            DuboxLog.d(TAG, "mTask == null");
            return;
        }

        newTask.setPriority(mPriority);
        // 由于使用事务批量添加任务，此处不挨个任务通知uri更新，而是在外面批量执行以后一并通知一次
        processorHelper.addDownloadFile(newTask, mIsNotify, mOnAddTaskListener);
        logProviderHelper.addDownloadFileLog(newTask.mRemoteUrl, newTask.getLocalUrl(),
                String.valueOf(mFileWrapper.getFileId()));

        if (mOnProcessListener != null) {
            mOnProcessListener.onItemTaskLoadProcess(newTask.mTaskId);
        }
    }
}
