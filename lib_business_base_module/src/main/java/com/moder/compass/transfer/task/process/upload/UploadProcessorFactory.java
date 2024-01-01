package com.moder.compass.transfer.task.process.upload;

import com.moder.compass.BaseApplication;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.base.UploadInfo;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.DBBean;
import com.moder.compass.transfer.task.IUploadProcessorFactory;
import com.moder.compass.transfer.task.UploadTask;
import com.moder.compass.transfer.task.UploadTaskManager;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * 用于创建上传文件loadProcesser的工厂类
 *
 * @author 孙奇 <br/>
 * create at 2012-11-12 上午02:00:19
 */
public class UploadProcessorFactory implements IUploadProcessorFactory {
    private static final String TAG = "UploadFileProcessorFactory";

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
     * <td>FileNotExist</td>
     * <td>TYPE_NEW_TASK</td>
     * <td>TYPE_START_SCHEDULER</td>
     * <td>TYPE_SET_TASK_STATE_TO_PENDING</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
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
     * <td>FileExist&Md5Change</td>
     * <td>TYPE_NEW_TASK</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
     * <td>TYPE_NEW_TASK_AND_REMOVE_LAST_TASK</td>
     * </tr>
     * </table>
     *
     * @author 孙奇 V 1.0.0 Create at 2012-11-12 上午03:27:27
     */
    private final static int[][] TYPE_TABLE = {
        {TYPE_NEW_TASK, TYPE_START_SCHEDULER, TYPE_SET_TASK_STATE_TO_PENDING, TYPE_NEW_TASK_AND_REMOVE_LAST_TASK,
            TYPE_NEW_TASK_AND_REMOVE_LAST_TASK},
        {TYPE_NEW_FINISHED_TASK, TYPE_START_SCHEDULER, TYPE_START_SCHEDULER, TYPE_START_SCHEDULER,
            TYPE_START_SCHEDULER},
        {TYPE_NEW_TASK, TYPE_NEW_TASK_AND_REMOVE_LAST_TASK, TYPE_NEW_TASK_AND_REMOVE_LAST_TASK,
            TYPE_NEW_TASK_AND_REMOVE_LAST_TASK, TYPE_NEW_TASK_AND_REMOVE_LAST_TASK}};

    private final String mBduss;
    private final String mUid;
    private final Processor.OnAddTaskListener mOnAddTaskListener;

    /**
     * @param bduss
     * @param onAddTaskListener
     */
    public UploadProcessorFactory(String bduss, String uid, Processor.OnAddTaskListener onAddTaskListener) {
        mBduss = bduss;
        mUid = uid;
        mOnAddTaskListener = onAddTaskListener;
    }

    @Override
    public Processor createProcessor(UploadInfo info, DBBean bean, boolean isNotify) {
        final UploadTask oldTask = getUploadTaskByRemoteUrl(info.getRemotePath(), info.getQuality());

        Processor processor;
        int type = getType(oldTask, info, bean);
        switch (type) {
            case TYPE_NEW_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.UPLOAD_PROCESS_DUBOX_TYPE_NEW_TASK);
                processor = new NewUploadTaskProcessor(info, isNotify, mBduss, mUid);
                break;
            case TYPE_NEW_TASK_AND_REMOVE_LAST_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.
                        UPLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK);
                processor = new NewUploadTaskAndRemoveLastTaskProcessor(info, oldTask, isNotify, mBduss, mUid);
                break;
            case TYPE_NEW_FINISHED_TASK:
                StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.UPLOAD_PROCESS_DUBOX_TYPE_NEW_FINISHED_TASK);
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_IGNORE);

                processor = new NewFinishedUploadTaskProcessor(info, isNotify, mBduss, mUid);
                break;
            case TYPE_SET_TASK_STATE_TO_PENDING:
                StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.
                        UPLOAD_PROCESS_DUBOX_TYPE_SET_TASK_STATE_TO_PENDING);
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_IGNORE);

                processor = new UploadSetTaskStateToPendingProcessor(oldTask, mBduss, mUid);
                break;
            case TYPE_START_SCHEDULER:
                StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.UPLOAD_PROCESS_DUBOX_TYPE_START_SCHEDULER);
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_IGNORE);
                processor = new StartUploadSchedulerProcessor(mBduss, oldTask);
                break;
            default:
                StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.UPLOAD_PROCESS_DUBOX_TYPE_START_SCHEDULER);
                processor = new StartUploadSchedulerProcessor(mBduss, oldTask);
                break;
        }

        DuboxLog.d(TAG, "type = " + type);
        processor.setOnAddTaskListener(mOnAddTaskListener);
        return processor;
    }

    private int getType(UploadTask oldTask, UploadInfo info, DBBean bean) {
        long time1 = System.currentTimeMillis();
        int row = -1;
        int column = -1;

        String remoteMd5 = getRemoteMd5(bean);
        if (TextUtils.isEmpty(remoteMd5)) {
            boolean hasRecord =
                hasUploadFileRecord(info.getLocalFile().toString(), info.getRemotePath(), info.getQuality());
            if (hasRecord) {
                row = 2;
            } else {
                row = 0;
            }
        } else {
            // 文件内容没有变化, 品质更高也需要添加新任务上传
            if (isFileNotChanged(info.getLocalFile(), info.getRemotePath(), bean)) {
                // 只根据remoteUrl相同来查询历史的任务
                UploadTask task = new UploadTaskManager(mBduss, mUid).getUploadTaskByRemoteUrl(info.getRemotePath());
                if (task != null
                    && (task.mQuality == 0 || task.mQuality >= info.getQuality())) {
                    row = 1;
                } else {
                    row = 2;
                }
            } else {
                row = 2;
            }
        }
        if (oldTask == null) {// 初始化列
            column = 0;
        } else {
            int taskState = oldTask.mState;
            if (TransferContract.Tasks.STATE_RUNNING == taskState
                || TransferContract.Tasks.STATE_PENDING == taskState) {
                column = 1;
            } else if (TransferContract.Tasks.STATE_PAUSE == taskState) {
                column = 2;
            } else if (TransferContract.Tasks.STATE_FINISHED == taskState) {
                column = 3;
            } else if (TransferContract.Tasks.STATE_FAILED == taskState) {
                column = 4;
            }
        }
        DuboxLog.d(TAG, "getType row = " + row + " column = " + column);
        if (-1 == row || -1 == column) {
            DuboxLog.d(TAG, "getType error");
            return TYPE_START_SCHEDULER;
        }
        DuboxLog.d(TAG, "asyncProcessUploadFile getType cost = " + (System.currentTimeMillis() - time1)
            + "row column = " + row + " " + column + "localPath = " + info.getLocalFile());
        return TYPE_TABLE[row][column];
    }

    /**
     * 文件内容是否没变化
     *
     * @return
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-7 下午04:05:54
     */
    private boolean isFileNotChanged(RFile localFile, String remotePath, DBBean bean) {
        long time1 = System.currentTimeMillis();
        if (localFile == null) {
            return false;
        }
        long localMTime = localFile.lastModified() / 1000;

        long remoteClientMTime = getRemoteFileCMTime(bean);
        long localSize = localFile.length();
        long remoteFileSize = getRemoteFileSize(bean);
        DuboxLog.d(TAG, "localMTime = " + localMTime + " remoteClientMTime = " + remoteClientMTime + " localSize = "
            + localSize + " remoteFileSize = " + remoteFileSize);
        if ((localMTime == remoteClientMTime) && (localSize == remoteFileSize)) {
            DuboxLog.d(TAG, "asyncProcessUploadFile isFileNotChanged cost = " + (System.currentTimeMillis() - time1));
            return true;
        }
        String remoteCopyPath = hasCopyHit(BaseApplication.getInstance(), remotePath, localMTime, localSize);
        if (remoteCopyPath != null) {
            DuboxLog.d(TAG, "remoteCopyPath = " + remoteCopyPath);
            return true;
        }
        DuboxLog.d(TAG, "asyncProcessUploadFile isFileNotChanged cost = " + (System.currentTimeMillis() - time1));
        return false;
    }

    /**
     * 获取文件服务器md5
     *
     * @return
     */
    private String getRemoteMd5(DBBean bean) {
        if (bean != null) {
            return bean.remoteMd5;
        }
        return null;
    }

    /**
     * 获取文件数据库中的文件修改时间
     *
     * @return
     */
    private long getRemoteFileCMTime(DBBean bean) {
        if (bean != null) {
            return bean.remoteFileCMTime;
        }
        return -1L;
    }

    /**
     * 获取远端文件大小
     *
     * @return
     */
    private long getRemoteFileSize(DBBean bean) {
        if (bean != null) {
            return bean.remoteFileSize;
        }
        return -1L;
    }

    private String hasCopyHit(Context context, String path, long fileCMTime, long fileSize) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(CloudFileContract.Files.buildFilesUri(mBduss),
                new String[] {CloudFileContract.Files.FILE_SERVER_PATH}, buildSelection(path),
                new String[] {String.valueOf(fileCMTime), String.valueOf(fileSize)}, null);
        } catch (IllegalStateException e) {
            DuboxLog.e(TAG, "ignore", e);
        }

        if (cursor == null) {
            DuboxLog.d(TAG, "hasCopyHit cursor is null");
            return null;
        }

        final String serverPath;

        try {
            if (cursor.moveToFirst()) {
                serverPath = cursor.getString(0);
            } else {
                serverPath = null;
            }
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
                DuboxLog.d(TAG, "hasCopyHit cursor close e:" + e);
            }
        }

        return serverPath;
    }

    private String buildSelection(String path) {
        String remotePath = path.replaceAll("'", "''");
        final StringBuilder sb = new StringBuilder();
        sb.append(CloudFileContract.Files.FILE_SERVER_PATH).append(" LIKE '");
        sb.append(FileUtils.getFileDirectoryWithOutSuffix(remotePath));
        sb.append("(%)");
        sb.append(FileUtils.getFileNameSuffix(remotePath));
        sb.append("'");
        sb.append(" AND ");
        sb.append(CloudFileContract.Files.FILE_CLIENT_MTIME);
        sb.append(" =?");
        sb.append(" AND ");
        sb.append(CloudFileContract.Files.FILE_SIZE);
        sb.append(" =?");
        return sb.toString();
    }

    /**
     * 查询上传文件是否上传相同的文件
     *
     * @param uri
     * @param remotePath
     *
     * @return
     */
    private boolean hasUploadFileRecord(String uri, String remotePath, int newQuality) {
        Cursor cursor =
            BaseApplication
                .getInstance()
                .getContentResolver()
                .query(TransferContract.UploadTasks.buildUri(mBduss),
                    new String[] {TransferContract.UploadTasks._ID,
                        TransferContract.UploadTasks.QUALITY},
                    TransferContract.UploadTasks.LOCAL_URL + " =? AND "
                        + TransferContract.UploadTasks.REMOTE_URL + " =?",
                    new String[] {uri, remotePath}, null);

        // 数据库里面没有该文件
        if (cursor == null) {
            return false;
        }

        try {
            if (cursor.moveToFirst()) {
                /**
                 * 1.覆盖安装的compress品质都没有，默认的为0，记录中所有文件都是之前没压缩的文件,相当于品质为100<br/>
                 * 2.如果新上传的品质比原库中的底，则不需要上传了，pm需求服务器只保存高品质的文件
                 */
                int oldQuality = cursor.getInt(1);
                if ((oldQuality >= newQuality) || oldQuality == 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            DuboxLog.d(TAG, "record cursor close e:" + e);
        } finally {
            cursor.close();
        }
        return false;
    }

    /**
     * 通过remoteUrl获取对应的TransferTask
     *
     * @param remoteUrl
     * @param newQuality
     *
     * @return
     */
    private UploadTask getUploadTaskByRemoteUrl(String remoteUrl, int newQuality) {
        if (TextUtils.isEmpty(remoteUrl)) {
            return null;
        }

        final Cursor cursor =
            BaseApplication
                .getInstance()
                .getContentResolver()
                .query(TransferContract.UploadTasks.buildUri(mBduss),
                    new String[] {TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL,
                        TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE,
                        TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE, TransferContract.Tasks.OFFSET_SIZE,
                        TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.DATE,
                        TransferContract.UploadTasks.NEED_OVERRIDE,
                        TransferContract.UploadTasks.QUALITY}, TransferContract.Tasks.REMOTE_URL + "=?",
                    new String[] {remoteUrl}, null);

        if (cursor == null) {
            return null;
        }

        try {
            while (cursor.moveToNext()) {
                int oldCompress = cursor.getInt(cursor.getColumnIndex(
                    TransferContract.UploadTasks.QUALITY));
                /**
                 * 1.覆盖安装的compress品质都没有，默认的为0，记录中所有文件都是之前没压缩的文件,相当于品质为100<br/>
                 * 2.如果新上传的品质比原库中的底，则不需要上传了，pm需求服务器只保存高品质的文件
                 */
                if ((oldCompress >= newQuality) || oldCompress == 0) {
                    return new UploadTask(BaseApplication.getInstance(), cursor, mBduss, mUid, null);
                }
            }
        } catch (Exception ignore) {
            DuboxLog.d(TAG, ignore.getMessage(), ignore);
        } finally {
            cursor.close();
        }

        return null;
    }
}
