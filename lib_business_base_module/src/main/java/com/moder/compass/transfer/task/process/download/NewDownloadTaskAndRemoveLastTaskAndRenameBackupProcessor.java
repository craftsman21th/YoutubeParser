package com.moder.compass.transfer.task.process.download;

import java.io.File;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.log.storage.db.LogProviderHelper;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.transfer.TransferFileNameConstant;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.util.TransferUtil;
import com.moder.compass.base.storage.config.Setting;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.task.DownloadTask;

/**
 * 新建下载任务并且移出上次的下载任务并且修改下载
 *
 * @author 孙奇 <br/>
 *         create at 2013-1-8 下午03:34:59
 *
 *         当下载记录中存在该记录，但通过app外部对下载的文件进行修改，会走该逻辑
 */
public class NewDownloadTaskAndRemoveLastTaskAndRenameBackupProcessor extends Processor {
    private static final String TAG = "NewDownloadTaskAndRemoveLastTaskAndRenameBackupProcesser";
    private IDownloadable mFileWrapper;
    private TransferTask task;
    private final boolean mIsNotify;
    private final String mBduss;
    private final String mUid;
    private final int mDownloadPriority;

    NewDownloadTaskAndRemoveLastTaskAndRenameBackupProcessor(IDownloadable fileWrapper, TransferTask task,
            boolean isNotify, String bduss, String uid, int priority) {
        super();
        this.mFileWrapper = fileWrapper;
        this.task = task;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
        mDownloadPriority = priority;
    }

    @Override
    public void process() {
        if (Target30StorageKt.isPartitionStorage()) {
            String fileName = File.separator + TransferFileNameConstant.BACKUP_OLD_FILE_NAME +
                            task.mLocalFileMeta.name();
            RFile backUpRFile = PathKt.rFile(fileName);
            task.mLocalFileMeta.rename(backUpRFile.name());

            DuboxLog.d(TAG, "oldPath = " + task.mLocalFileMeta.localUrl());
        } else {
            String defaultPath = Setting.getDefaultSaveDir(BaseApplication.getInstance());
            StringBuilder sb = new StringBuilder();
            sb.append(defaultPath);
            final String localPath = mFileWrapper.getFilePath();
            // 共享目录换本地路径
            sb.append(localPath);
            String localFilePath = sb.toString();
            if (!TransferUtil.changeOldDownloadFileName(localFilePath)) { // 重命名失败则不添加下载任务
                return;
            }
        }

        final DownloadProcessorHelper processorHelper = new DownloadProcessorHelper(mBduss, mUid);
        final LogProviderHelper logProviderHelper = new LogProviderHelper();
        // sunqi 重新下载并且删除原来的文件
        processorHelper.cancelTask(task.mTaskId);

        // 创建新任务
        DownloadTask newTask =
                processorHelper.createDownloadTaskWithFileWrapper(BaseApplication.getInstance(),
                        mFileWrapper);
        if (newTask == null) {
            DuboxLog.d(TAG, "task == null");
            return;
        }

        newTask.setPriority(mDownloadPriority);
        // 由于使用事务批量添加任务，此处不挨个任务通知uri更新，而是在外面批量执行以后一并通知一次
        processorHelper.addDownloadFile(newTask, mIsNotify, mOnAddTaskListener);
        logProviderHelper.addDownloadFileLog(newTask.mRemoteUrl, newTask.getLocalUrl(),
                String.valueOf(mFileWrapper.getFileId()));

        if (mOnProcessListener != null) {
            mOnProcessListener.onItemTaskLoadProcess(newTask.mTaskId);
        }
    }
}
