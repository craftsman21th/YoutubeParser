
package com.moder.compass.transfer.task.process.download.link;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.transfer.base.FileInfo;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.base.IFileInfoGenerator;
import com.moder.compass.transfer.base.ITaskGenerator;
import com.moder.compass.transfer.base.Processor;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.DownloadTask;
import com.moder.compass.transfer.task.IDownloadProcessorFactory;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.process.download.DownloadSetTaskStateToPendingProcessor;

import android.text.TextUtils;

/**
 * Created by liuliangping on 2015/6/30.
 */
public class LinkFileDownloadProcessorFactory implements IDownloadProcessorFactory {

    private static final String TAG = "LinkFileDownloadProcessorFactory";
    private final ITaskGenerator mTaskGenerator;
    private final Processor.OnAddTaskListener mOnAddTaskListener;
    private final IFileInfoGenerator mFileInfoGenerator;
    private final int mFrom;

    public static final int FROM_DEFAULT = 0;
    
    public LinkFileDownloadProcessorFactory(IFileInfoGenerator fileInfoGenerator, ITaskGenerator taskGenerator,
            Processor.OnAddTaskListener onAddTaskListener, int from) {
        mFileInfoGenerator = fileInfoGenerator;
        mTaskGenerator = taskGenerator;
        mOnAddTaskListener = onAddTaskListener;
        mFrom = from;
    }

    @Override
    public Processor createProcessor(IDownloadable downloadable, boolean isNotify, String bduss, String uid,
                                     int downloadPriority) {
        FileInfo fileInfo = mFileInfoGenerator.generate(downloadable);
        if (fileInfo == null) {
            return null;
        }

        TransferTask task = mTaskGenerator.generate(downloadable, bduss, uid);
        return createProcessor(fileInfo, task, isNotify, bduss, uid, downloadPriority);
    }

    private Processor createProcessor(FileInfo fileInfo, TransferTask task, boolean isNotify, String bduss, String uid,
            int downloadPriority) {
        Processor processor = null;
        int type = getType(fileInfo, (DownloadTask) task, bduss);

        switch (type) {
            case TYPE_NEW_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.DOWNLOAD_PROCESS_SHARE_TYPE_NEW_TASK);
                processor = new NewLinkDownloadFileProcessor(fileInfo, isNotify, bduss, uid, mFrom);
                break;
            case TYPE_NEW_TASK_AND_REMOVE_LAST_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys
                                .DOWNLOAD_PROCESS_SHARE_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK);
                processor =
                        new NewLinkDownloadTaskAndRemoveLastTaskProcessor(fileInfo, task, isNotify, bduss, uid, mFrom);
                break;
            case TYPE_NEW_FINISHED_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys
                                .DOWNLOAD_PROCESS_SHARE_TYPE_NEW_FINISHED_TASK);
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                processor = new NewLinkFinishedDownloadTaskProcessor(fileInfo, isNotify, bduss, uid, mFrom);
                break;
            case TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys
                                .DOWNLOAD_PROCESS_SHARE_TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH);
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                processor = new NewLinkFinishedDownloadTaskAndCopyToNewPathProcessor(fileInfo, task, isNotify, bduss,
                        uid, mFrom);
                break;
            case TYPE_SET_TASK_STATE_TO_PENDING:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys
                                .DOWNLOAD_PROCESS_SHARE_TYPE_SET_TASK_STATE_TO_PENDING);
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                processor = new DownloadSetTaskStateToPendingProcessor(bduss, uid, task, downloadPriority);
                break;
            case TYPE_START_SCHEDULER:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.DOWNLOAD_PROCESS_SHARE_TYPE_START_SCHEDULER);
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                processor = new StartLinkDownloadSchedulerProcessor(task);
                break;
            default:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.DOWNLOAD_PROCESS_SHARE_TYPE_START_SCHEDULER);
                processor = new StartLinkDownloadSchedulerProcessor(task);
                break;
        }
        processor.setOnAddTaskListener(mOnAddTaskListener);
        return processor;
    }

    private int getType(FileInfo fileInfo, DownloadTask task, String bduss) {
        String downloadUrl = fileInfo.serverPath;
        long downloadSize = fileInfo.size;

        if (TextUtils.isEmpty(downloadUrl) || downloadSize < 0L) {
            DuboxLog.e(TAG, "downloadUrl:" + downloadUrl + " ,downloadSize:" + downloadSize + " can not download");
            return TYPE_START_SCHEDULER;
        }

        if (task == null) {
            return TYPE_NEW_TASK; // 新下载
        } else {
            if (TextUtils.equals(task.mRemoteUrl, downloadUrl)) {
                int taskState = task.mState;
                if (TransferContract.Tasks.STATE_RUNNING == taskState
                        || TransferContract.Tasks.STATE_PENDING == taskState) {
                    return TYPE_START_SCHEDULER;
                } else if (TransferContract.Tasks.STATE_PAUSE == taskState) {
                    return TYPE_SET_TASK_STATE_TO_PENDING;
                } else if (TransferContract.Tasks.STATE_FINISHED == taskState) {
                    boolean localeChange;
                    if (task.mLocalFileMeta == null) {
                        localeChange = true;
                    } else {
                        localeChange = !task.mLocalFileMeta.exists() || task.mLocalFileMeta.length() != downloadSize;
                    }

                    return localeChange ? TYPE_NEW_TASK_AND_REMOVE_LAST_TASK : TYPE_START_SCHEDULER;
                } else if (TransferContract.Tasks.STATE_FAILED == taskState) {
                    return TYPE_NEW_TASK_AND_REMOVE_LAST_TASK;
                }
            }
        }
        return TYPE_START_SCHEDULER;
    }
}
