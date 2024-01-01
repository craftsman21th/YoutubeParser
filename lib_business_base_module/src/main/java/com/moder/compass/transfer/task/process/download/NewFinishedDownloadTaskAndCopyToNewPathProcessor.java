package com.moder.compass.transfer.task.process.download;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.kernel.util.encode.MD5Util;
import com.moder.compass.log.storage.db.LogProviderHelper;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.statistics.BroadcastStatisticKt;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.storage.DownloadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.DownloadTask;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.util.TransferUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 创建新的已完成的任务并且搬运 com.dubox.drive.task.process. NewFinishedDownloadTaskAndCopyToNewPathProcesser
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-12 下午12:04:48
 *
 * @update: target30以后是不允许指定下载目录的，所以该Processor只适用在低于target30上，target30不会产生该processor
 *
 */
public class NewFinishedDownloadTaskAndCopyToNewPathProcessor extends Processor {
    private static final String TAG = "NewFinishedDownloadTaskAndCopyToNewPathProcesser";

    private IDownloadable mFileWrapper;
    private TransferTask oldTask;
    private final boolean mIsNotify;
    private final String mUid;
    private final String mBduss;
    private final int mDownloadPriority;
    private final DownloadProcessorHelper processorHelper;

    NewFinishedDownloadTaskAndCopyToNewPathProcessor(IDownloadable fileWrapper, TransferTask oldTask,
            boolean isNotify, String bduss, String uid, int priority) {
        super();
        this.mFileWrapper = fileWrapper;
        this.oldTask = oldTask;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
        mDownloadPriority = priority;
        processorHelper = new DownloadProcessorHelper(mBduss, mUid);
    }

    @Override
    public void process() {
        if (mFileWrapper == null) {
            return;
        }
        RFile oldRFile = oldTask.mLocalFileMeta;
        final String serverPath = mFileWrapper.getFilePath();
        final long lastModifyTime = TransferUtil.getLastModifyTime(serverPath, mBduss);
        if (!FileUtils.isFileChanged(oldRFile.localUrl(), lastModifyTime)) { // 如果已经下载过的文件存在且内容没变化
            // 创建完成任务，并且搬运
            DownloadTask task = processorHelper.createDownloadTaskWithFileWrapper(
                    BaseApplication.getInstance(), mFileWrapper);
            if (mOnProcessListener != null) {
                mOnProcessListener.onItemTaskLoadProcess(task.mTaskId);
            }
            // 文件copy成功
            if (oldRFile.copy(BaseApplication.getContext(), task.mLocalFileMeta)) {
                task.setPriority(mDownloadPriority);
                processorHelper.addFinishedDownloadFile(task, false);
                // 重新下载并且删除原来的文件记录 libin09 2015-5-28 7.9
                processorHelper.cancelTask(oldTask.mTaskId); // 删除原来的任务
                // 添加去重表记录 libin09 2015-5-28
                addFilesystemInfo(task.mLocalFileMeta, serverPath);
                DuboxLog.d(TAG,
                        "oldPath = " + oldRFile.localUrl() + " desPath = " + task.mLocalFileMeta.localUrl());
            } else {
                reDownload();
            }
        } else {
            // 如果已经下载过的文件被手动删除了，无法搬运, 重新下载
            reDownload();
        }

    }

    private void reDownload() {
        // sunqi 重新下载并且删除原来的文件记录
        processorHelper.cancelTask(oldTask.mTaskId); // 删除原来的任务
        // 由于使用事务批量添加任务，此处不挨个任务通知uri更新，而是在外面批量执行以后一并通知一次
        DownloadTask newTask = processorHelper.createDownloadTaskWithFileWrapper( // 创建新任务
                BaseApplication.getInstance(), mFileWrapper);
        if (newTask == null) {
            DuboxLog.d(TAG, "task == null");
            return;
        }
        newTask.setPriority(mDownloadPriority);
        // 由于使用事务批量添加任务，此处不挨个任务通知uri更新，而是在外面批量执行以后一并通知一次
        processorHelper.addDownloadFile(newTask, mIsNotify, mOnAddTaskListener);
        LogProviderHelper logProviderHelper = new LogProviderHelper();
        logProviderHelper.addDownloadFileLog(newTask.mRemoteUrl, newTask.getLocalUrl(),
                String.valueOf(mFileWrapper.getFileId()));

        if (mOnProcessListener != null) {
            mOnProcessListener.onItemTaskLoadProcess(newTask.mTaskId);
        }
    }

    /**
     * 更新文件列表对应的md5
     */
    private void addFilesystemInfo(RFile localUrl, String remoteUrl) {
        scanFile(BaseApplication.getInstance(), localUrl.localUrl()); // 加入媒体库
        String md5 = MD5Util.getMD5Digest(localUrl);

        new DownloadTaskProviderHelper(mBduss).insertFileLocalMd5AndPath(
                TransferContract.DownloadTaskFiles.buildUri(mBduss),
                BaseApplication.getInstance().getContentResolver(), remoteUrl, md5, localUrl.localUrl(),
                localUrl.lastModified());
    }

    private void scanFile(Context context, String path) {
        Uri data = Uri.parse("file://" + path);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
        BroadcastStatisticKt.statisticSendBroadcast(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    }
}
