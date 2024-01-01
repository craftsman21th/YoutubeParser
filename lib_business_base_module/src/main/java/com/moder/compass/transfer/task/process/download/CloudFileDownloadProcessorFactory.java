package com.moder.compass.transfer.task.process.download;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.storage.config.Setting;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.db.cloudfile.model.OfflineStatus;
import com.dubox.drive.db.record.contract.RecordFilesContract;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.statistics.StatisticsLogForMutilFields.StatisticsKeys;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.base.ITaskGenerator;
import com.moder.compass.transfer.base.OnProcessListener;
import com.moder.compass.transfer.base.Processor;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.transfer.task.DownloadTask;
import com.moder.compass.transfer.task.IDownloadProcessorFactory;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.util.TransferUtil;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import rubik.generate.context.dubox_com_dubox_drive_cloud_image.CloudImageContext;
import rubik.generate.context.dubox_com_dubox_drive_files.FilesContext;

/**
 * 用于创建网盘文件下载的loadProcesser的工厂类 com.dubox.drive.task.process.DownloadFileProcesserFactory
 *
 * @author 孙奇 <br/>
 * create at 2012-11-12 上午01:58:49
 */
public class CloudFileDownloadProcessorFactory implements IDownloadProcessorFactory {
    private static final String TAG = "CloudFileDownloadProcessorFactory";

    /**
     * 表驱动的TYPE表:<br>
     * <table border="1px">
     * <tr>
     * <td>FileState\TaskState</td>
     * <td>No Task</td>
     * <td>Running&Pending State</td>
     * <td>Pause State</td>
     * <td>Finished State</td>
     * <td>Failed State</td>
     * </tr>
     * <tr>
     * <td>FileNotExist&PathNotChange</td>
     * <td>TYPE_NEW_TASK</td>
     * <td>TYPE_START_SCHEDULER</td>
     * <td>TYPE_SET_TASK_STATE_TO_PENDING</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
     * </tr>
     * <tr>
     * <td>FileNotExist&PathChange</td>
     * <td>TYPE_NEW_TASK</td>
     * <td>TYPE_NEW_TASK</td>
     * <td>TYPE_NEW_TASK</td>
     * <td>TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
     * </tr>
     * <tr>
     * <td>FileExist&Md5NotChange</td>
     * <td>TYPE_NEW_FINISHED_TASK</td>
     * <td>TYPE_START_SCHEDULER</td>
     * <td>TYPE_START_SCHEDULER</td>
     * <td>TYPE_START_SCHEDULER</td>
     * <td>TYPE_START_SCHEDULER</td>
     * </tr>
     * <tr>
     * <td>FileExist&Md5Change</td>
     * <td>TYPE_NEW_TASK_RENAME_BACKUP</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP</td>
     * </tr>
     * </table>
     *
     * @author 孙奇 V 1.0.0 Create at 2012-11-12 上午03:27:27
     */
    private final static int[][] TYPE_TABLE = {
            {TYPE_NEW_TASK, TYPE_START_SCHEDULER, TYPE_SET_TASK_STATE_TO_PENDING, TYPE_NEW_TASK_AND_REMOVE_LAST_TASK,
                    TYPE_NEW_TASK_AND_REMOVE_LAST_TASK},
            {TYPE_NEW_TASK, TYPE_NEW_TASK, TYPE_NEW_TASK, TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH,
                    TYPE_NEW_TASK_AND_REMOVE_LAST_TASK},
            {TYPE_NEW_FINISHED_TASK, TYPE_START_SCHEDULER, TYPE_START_SCHEDULER, TYPE_START_SCHEDULER,
                    TYPE_START_SCHEDULER},
            {TYPE_NEW_TASK_RENAME_BACKUP, TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP,
                    TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP,
                    TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP,
                    TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP}};

    private final ITaskGenerator mTaskGenerator;
    private final OnProcessListener mListener;
    private final Processor.OnAddTaskListener mOnAddTaskListener;

    /**
     * 此工厂只需task即可，故fileInfoGenerator是null
     *
     * @param taskGenerator
     * @param listener
     * @param onAddTaskListener
     */
    public CloudFileDownloadProcessorFactory(ITaskGenerator taskGenerator, OnProcessListener listener,
                                             Processor.OnAddTaskListener onAddTaskListener) {
        mTaskGenerator = taskGenerator;
        mListener = listener;
        mOnAddTaskListener = onAddTaskListener;
    }

    @Override
    public Processor createProcessor(IDownloadable downloadable, boolean isNotify, String bduss, String uid,
                                     int downloadPriority) {
        if (downloadable == null || mTaskGenerator == null) {
            DuboxLog.d("ShareFileOpPresenter",
                    "downloadable or mTaskGenerator is null"
                            + (downloadable == null) + "--" + (mTaskGenerator == null));
            return null;
        }

        // 此工厂只需task即可，故fileInfoGenerator是null, 先从download表中根据remote_url查询是否已经存在该task
        TransferTask task = mTaskGenerator.generate(downloadable, bduss, uid);
        return createProcessor(downloadable, task, isNotify, bduss, uid, downloadPriority);
    }

    private Processor createProcessor(IDownloadable fileWrapper, TransferTask task, boolean isNotify, String bduss,
                                      String uid, int downloadPriority) {
        Processor processor;
        int type = getType(fileWrapper, (DownloadTask) task, bduss);

        switch (type) {
            case TYPE_NEW_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK);
                processor = new NewDownloadTaskProcessor(fileWrapper, isNotify, bduss, uid,
                        downloadPriority);
                // 同步文件表和云图表下载状态
                FilesContext.setFileOfflineStatus(BaseApplication.getContext(),
                        String.valueOf(fileWrapper.getFileId()), OfflineStatus.STATUS_ONGOING.getStatus());
                if (FileType.isImageOrVideo(fileWrapper.getFileName())) {
                    CloudImageContext.updateOfflineStatusByFsid(BaseApplication.getContext(),
                            fileWrapper.getFileId(), OfflineStatus.STATUS_ONGOING.getStatus());

                    // 通知最近观看表刷新数据, 顺序必须是在云图表后面
                    BaseApplication.getContext().getContentResolver()
                            .notifyChange(RecordFilesContract.Companion.getUri(), null, false);
                }
                break;
            case TYPE_NEW_TASK_AND_REMOVE_LAST_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK);
                processor =
                        new NewDownloadTaskAndRemoveLastTaskProcessor(fileWrapper, task, isNotify, bduss, uid,
                                downloadPriority);
                break;
            case TYPE_NEW_FINISHED_TASK:
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_FINISHED_TASK);
                processor = new NewFinishedDownloadTaskProcessor(fileWrapper, isNotify, bduss, uid,
                        downloadPriority);
                break;
            case TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH:
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH);
                processor =
                        new NewFinishedDownloadTaskAndCopyToNewPathProcessor(fileWrapper, task, isNotify, bduss, uid,
                                downloadPriority);
                break;
            case TYPE_SET_TASK_STATE_TO_PENDING:
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_SET_TASK_STATE_TO_PENDING);

                processor = new DownloadSetTaskStateToPendingProcessor(bduss, uid, task, downloadPriority);
                break;
            case TYPE_START_SCHEDULER:
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_IGNORE);
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_START_SCHEDULER);
                processor = new StartDownloadSchedulerProcessor(bduss, task, downloadPriority);
                break;
            case TYPE_NEW_TASK_RENAME_BACKUP:
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_CREATE_BACKUP_FAILE);
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_RENAME_BACKUP);
                processor = new NewDownloadTaskAndRenameBackupProcessor(fileWrapper, isNotify, bduss,
                        uid, downloadPriority);
                break;
            case TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP:
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FILE_CREATE_BACKUP_FAILE);
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP);
                processor = new NewDownloadTaskAndRemoveLastTaskAndRenameBackupProcessor(fileWrapper,
                        task, isNotify, bduss, uid, downloadPriority);
                break;
            default:
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsKeys.DOWNLOAD_PROCESS_DUBOX_TYPE_START_SCHEDULER);
                processor = new StartDownloadSchedulerProcessor(bduss, task, downloadPriority);
                break;
        }
        processor.setOnProcessListener(mListener);
        processor.setOnAddTaskListener(mOnAddTaskListener);
        return processor;
    }


    private int getType(IDownloadable fileWrapper, DownloadTask task, String bduss) {
        if (Target30StorageKt.isPartitionStorage()) {
            return getTypeByUri(fileWrapper, task, bduss);
        }
        return getTypeBypath(fileWrapper, task, bduss);
    }

    private int getType(DownloadTask task, boolean isFileExist, boolean isFileChange,
                        boolean isDefaultDownloadPathChanged, boolean isServerChange) {
        int row = -1;
        int column = -1;
        if (isFileExist && isFileChange) { // 初始化行，文件存在且文件发生改变
            row = 3;
        } else if (isFileExist && !isFileChange) { // 文件存在且没有发生改变
            row = 2;
        } else if (!isFileExist && isDefaultDownloadPathChanged) { // 文件不存在，正在下载的task时，下载位置发生变化
            row = 1;
        } else if (!isFileExist && !isDefaultDownloadPathChanged) { // 文件不存在，正在下载的task与当前的下载位置一直
            row = 0;
        }
        if (task == null) { // 初始化列
            column = 0;
        } else {
            int taskState = task.mState;
            if (TransferContract.Tasks.STATE_RUNNING == taskState
                    || TransferContract.Tasks.STATE_PENDING == taskState) {
                column = 1;
            } else if (TransferContract.Tasks.STATE_PAUSE == taskState) {
                column = 2;
            } else if (TransferContract.Tasks.STATE_FINISHED == taskState) {
                if (row == 1 && isServerChange) { // 如果修改过默认下载路径，并且有新版本，老版本下载完成时，重新下这个任务
                    // (视频文件不比较云端版本)
                    column = 0;
                } else {
                    column = 3;
                }
            } else if (TransferContract.Tasks.STATE_FAILED == taskState) {
                column = 4;
            }
        }
        if (-1 == row || -1 == column) {
            DuboxLog.d(TAG, "getType error");
            return TYPE_START_SCHEDULER;
        }
        return TYPE_TABLE[row][column];
    }

    /**
     * 适配target30 getType
     *
     * @param fileWrapper
     * @param task
     * @param bduss
     * @return 表中没有查到数据，就表明文件不存在
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getTypeByUri(IDownloadable fileWrapper, DownloadTask task, String bduss) {
        boolean isFileExist = false;
        boolean isFileChange = false;
        // 获取download表中查询文件修改时间
        long lastModifyTime = TransferUtil.getLastModifyTime(fileWrapper.getFilePath(), bduss);
        if (task != null) {
            int taskState = task.mState;
            if (taskState == TransferContract.Tasks.STATE_FINISHED) {
                // target30, local_url存入的是uri，需要转换绝对路径来判断文件是否存在
                isFileExist = task.mLocalFileMeta.exists();
                if (isFileExist) {
                    isFileChange = task.mLocalFileMeta.lastModified() != lastModifyTime;
                }
            }
        }
        boolean isVideoFile = FileType.isVideo(fileWrapper.getFileName());
        String transferServerMD5 = task == null ? null : task.serverMD5;
        boolean isServerChange =
                !isVideoFile && isServerFileChange(fileWrapper.getServerMD5(), transferServerMD5);
        if (!isFileChange) {
            // 本地md5未变化时，再比较云端md5是否变化(视频文件不比较云端版本) libin09 2015-6-3
            isFileChange = isServerChange;
        }
        return getType(task, isFileExist, isFileChange, false, isServerChange);
    }


    private int getTypeBypath(IDownloadable fileWrapper, DownloadTask task, String bduss) {
        String localPath = fileWrapper.getFilePath();
        // 下载完成的文件，不包括下载中的临时文件
        boolean isFileExist = TransferUtil.isFileExist(localPath);
        final long lastModifyTime = TransferUtil.getLastModifyTime(fileWrapper.getFilePath(), bduss);
        // 本地文件最后修改时间变化
        boolean isFileChange = TransferUtil.isFileMd5Changed(localPath, lastModifyTime);
        final String transferServerMD5 = task == null ? null : task.serverMD5;

        // 视频任务 libin09 2015-6-3
        final boolean isVideoFile = FileType.isVideo(fileWrapper.getFileName());

        final boolean isServerChange =
                !isVideoFile && isServerFileChange(fileWrapper.getServerMD5(), transferServerMD5);
        if (!isFileChange) {// 本地md5未变化时，再比较云端md5是否变化(视频文件不比较云端版本) libin09 2015-6-3
            isFileChange = isServerChange;
        }

        String parentLocalPath = "";
        try {
            parentLocalPath = fileWrapper.getParent().getFilePath();
        } catch (Exception e) {
            DuboxLog.e(TAG, "error get parentLocalPath");
        }
        boolean isDefaultDownloadPathChanged = isDefaultDownloadPathChangedOnTask(task, parentLocalPath);

        return getType(task, isFileExist, isFileChange,
                isDefaultDownloadPathChanged, isServerChange);
    }


    /**
     * 比较云端文件是否有新版本
     *
     * @param serverMD5 云端md5
     * @param localMD5  本地md5
     * @return true:有新版本,false:没有
     * @author libin09
     * @since 7.9 2015-5-28
     */
    private boolean isServerFileChange(String serverMD5, String localMD5) {
        if (TextUtils.isEmpty(serverMD5) || TextUtils.isEmpty(localMD5)) {
            // 7.9版本覆盖安装时，保留以前的任务不处理新版本，视频文件不存md5，无需比较云端版本
            return false;
        }

        return !localMD5.equals(serverMD5);
    }


    /**
     * 当前默认下载路径和当前这个TASK的下载路径是否一致
     *
     * @param task
     * @param parentLocalPath
     * @return
     */
    private boolean isDefaultDownloadPathChangedOnTask(DownloadTask task, String parentLocalPath) {
        if (task == null) {
            return true;
        }
        String taskLocalPath = FileUtils.getFileDirectoryWithOutSlash(task.getLocalUrl());
        String defaultDownloadDir = Setting.getDefaultSaveDir(BaseApplication.getInstance());
        defaultDownloadDir += parentLocalPath;
        defaultDownloadDir = FileUtils.getFileDirectoryWithOutSlash(defaultDownloadDir);
        // DuboxLog.d(TAG, "isDefaultDownloadPathChangedOnTask taskLocalPath = " + taskLocalPath);
        // DuboxLog.d(TAG, "isDefaultDownloadPathChangedOnTask defaultDownloadDir = " + defaultDownloadDir);
        if (taskLocalPath.equals(defaultDownloadDir)) {
            return false;
        }
        return true;
    }
}
