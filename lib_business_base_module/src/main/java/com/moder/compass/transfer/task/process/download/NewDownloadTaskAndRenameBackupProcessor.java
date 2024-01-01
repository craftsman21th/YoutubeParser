package com.moder.compass.transfer.task.process.download;

import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.transfer.util.TransferUtil;
import com.moder.compass.BaseApplication;
import com.moder.compass.base.storage.config.Setting;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.log.storage.db.LogProviderHelper;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.DownloadTask;

/**
 * 新建下载任务并且重命名之前的下载文件
 * 
 * @author 孙奇 <br/>
 *         create at 2013-1-8 下午03:33:03
 */
public class NewDownloadTaskAndRenameBackupProcessor extends Processor {
    private static final String TAG = "NewDownloadTaskAndRenameBackupProcessor";

    private IDownloadable mFileWrapper;
    private final boolean mIsNotify;
    private final String mBduss;
    private final String mUid;
    private final int mDownloadPriority;

    NewDownloadTaskAndRenameBackupProcessor(IDownloadable fileWrapper, boolean isNotify, String bduss,
            String uid, int priority) {
        super();
        this.mFileWrapper = fileWrapper;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
        mDownloadPriority = priority;
    }

    @Override
    public void process() {
        if (!Target30StorageKt.isPartitionStorage()) {
            String defaultPath = Setting.getDefaultSaveDir(BaseApplication.getInstance());
            StringBuilder sb = new StringBuilder();
            sb.append(defaultPath);

            String localPath = mFileWrapper.getFilePath();
            sb.append(localPath);
            String localFilePath = sb.toString();
            if (!TransferUtil.changeOldDownloadFileName(localFilePath)) { // 重命名失败则不添加下载任务
                return;
            }
        }

        final DownloadProcessorHelper processorHelper = new DownloadProcessorHelper(mBduss, mUid);
        final LogProviderHelper logProviderHelper = new LogProviderHelper();

        DownloadTask task = processorHelper.createDownloadTaskWithFileWrapper(BaseApplication.getInstance(),
                mFileWrapper);
        if (task == null) {
            DuboxLog.d(TAG, "task == null");
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
