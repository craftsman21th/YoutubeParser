
package com.moder.compass.transfer.task.process.download.link;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.transfer.base.FileInfo;
import com.moder.compass.transfer.base.Processor;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.DownloadTask;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.process.download.DownloadProcessorHelper;

class NewLinkFinishedDownloadTaskAndCopyToNewPathProcessor extends Processor {
    private static final String TAG = "NewFinishedDownloadTaskAndCopyToNewPathProcesser";

    private FileInfo mFileInfo;
    private TransferTask oldTask;
    private final boolean mIsNotify;
    private String mBduss;
    private final String mUid;

    NewLinkFinishedDownloadTaskAndCopyToNewPathProcessor(FileInfo mFileInfo, TransferTask oldTask, boolean isNotify,
                                                         String bduss, String uid, int from) {
        super();
        this.mFileInfo = mFileInfo;
        this.oldTask = oldTask;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
    }

    @Override
    public void process() {
        final DownloadProcessorHelper processorHelper = new DownloadProcessorHelper(mBduss, mUid);
        RFile oldPath = oldTask.getFile();
        if (oldTask.mLocalFileMeta.exists()) { // 如果已经下载过的文件存在且内容没变化
            // 创建完成任务，并且搬运
            DownloadTask task = new LinkDownloadTask(mFileInfo.localFile, mFileInfo.serverPath,
                    mFileInfo.size, null, mBduss, mUid);
            task.setTransmitterType(TransferContract.DownloadTasks.TRANSMITTER_TYPE_LINK);

            processorHelper.addFinishedDownloadFile(task, mIsNotify);

            RFile desPath = task.getFile();
            oldPath.copy(BaseApplication.getInstance(), desPath);
            DuboxLog.d(TAG, "oldPath = " + oldPath + " desPath = " + desPath);
        } else {// 如果已经下载过的文件被手动删除了，无法搬运
            // sunqi 重新下载并且删除原来的文件
            processorHelper.cancelTask(oldTask.mTaskId);// 删除原来的任务

            LinkDownloadTask newTask = new LinkDownloadTask(mFileInfo.localFile, mFileInfo.serverPath,
                mFileInfo.size, null, mBduss, mUid);
            newTask.setTransmitterType(TransferContract.DownloadTasks.TRANSMITTER_TYPE_LINK);
            processorHelper.addDownloadFile(newTask, mIsNotify, mOnAddTaskListener);

        }
    }
}