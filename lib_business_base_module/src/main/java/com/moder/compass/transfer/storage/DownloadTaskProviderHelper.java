/*
 * DownloadTaskProviderHelper.java
 * @author libin09
 * V 1.0.0
 * Create at 2013-12-19 下午5:24:10
 */
package com.moder.compass.transfer.storage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import com.moder.compass.BaseApplication;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.db.cloudfile.model.OfflineStatus;
import com.dubox.drive.cloudfile.io.model.CloudFile;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.db.record.contract.RecordFilesContract;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.model.DataTransferModel;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.dubox.drive.db.preview.contract.PreviewContract;
import com.moder.compass.transfer.task.DownloadTask;
import com.mars.united.core.os.database.CursorExtKt;

import java.util.ArrayList;
import java.util.List;

import rubik.generate.context.dubox_com_dubox_drive_cloud_image.CloudImageContext;
import rubik.generate.context.dubox_com_dubox_drive_files.FilesContext;

/**
 * 
 * @author libin09 <br/>
 *         传输文件的provider帮助类<br/>
 *         create at 2013-12-19 下午5:24:10
 */
public class DownloadTaskProviderHelper {

    /**
     * 用于区别账号，每个操作只影响自己所在账号
     */
    private String mBduss;

    /**
     * 构造方法
     * 
     * @param bduss 用于标识账号
     */
    public DownloadTaskProviderHelper(String bduss) {
        mBduss = bduss;
    }

    /**
     * 添加下载任务
     * 
     * @param contentResolver
     * @param task
     * @return
     */
    public Uri addDownloadingTask(ContentResolver contentResolver, DownloadTask task, boolean isNotify,
            boolean isPreview) {
        final ContentValues cv = new ContentValues();
        cv.put(TransferContract.Tasks.TYPE, TransferContract.DownloadTasks.TYPE_DOWNLOAD_FILE);
        cv.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
        cv.put(TransferContract.Tasks.LOCAL_URL, task.mLocalFileMeta.localUrl());
        cv.put(TransferContract.Tasks.REMOTE_URL, task.mRemoteUrl);
        cv.put(TransferContract.Tasks.SIZE, task.mSize);
        cv.put(TransferContract.Tasks.TRANSMITTER_TYPE, task.mTransmitterType);
        cv.put(TransferContract.Tasks.DATE, System.currentTimeMillis());
        cv.put(CloudFileContract.Files.FILE_SERVER_MD5, task.serverMD5);

        if (task.mPriority > 0) {// 优先级默认为0
            cv.put(TransferContract.Tasks.PRIORITY, task.mPriority);
        }

        final Uri uri;
        if (isPreview) {
            uri = contentResolver.insert(PreviewContract.Tasks.buildUri(mBduss), cv);
        } else {
            cv.put(TransferContract.Tasks.FILE_NAME, task.getFileName());
            uri = contentResolver.insert(TransferContract.DownloadTasks.buildProcessingUri(mBduss, isNotify), cv);
        }

        return uri;
    }

    /**
     * 添加下载完成任务记录
     * 
     * @param contentResolver
     * @param task
     * @param isNotify
     */
    public Uri updateDownloadFinishTask(ContentResolver contentResolver, DownloadTask task, boolean isNotify) {
        final ContentValues cv = new ContentValues();
        cv.put(TransferContract.Tasks.TYPE, TransferContract.DownloadTasks.TYPE_DOWNLOAD_FILE);
        cv.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FINISHED);
        cv.put(TransferContract.Tasks.LOCAL_URL, task.mLocalFileMeta.localUrl());
        cv.put(TransferContract.Tasks.REMOTE_URL, task.mRemoteUrl);
        cv.put(TransferContract.Tasks.OFFSET_SIZE, task.mSize);
        cv.put(TransferContract.Tasks.SIZE, task.mSize);
        cv.put(TransferContract.Tasks.TRANSMITTER_TYPE, task.mTransmitterType);
        cv.put(CloudFileContract.Files.FILE_SERVER_MD5, task.serverMD5);
        cv.put(TransferContract.Tasks.FILE_NAME, task.getFileName());

        if (task.mPriority > 0) {// 优先级默认为0
            cv.put(TransferContract.Tasks.PRIORITY, task.mPriority);
        }

        final Uri uri = contentResolver.insert(TransferContract.DownloadTasks.buildFinishedUri(mBduss, isNotify), cv);
        return uri;
    }

    /**
     * 添加下载完成任务记录
     * 
     * @param contentResolver
     * @param task
     */
    public void updateDownloadFinishTask(ContentResolver contentResolver, DownloadTask task) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.STATE, TransferContract.DownloadTasks.STATE_FINISHED);
        values.put(TransferContract.Tasks.OFFSET_SIZE, task.mSize);

        if (task.mPriority > 0) {
            values.put(TransferContract.Tasks.PRIORITY, task.mPriority);
        }

        updateTask(contentResolver, task.mTaskId, values);
    }

    /**
     * 删除正在进行的下载任务
     * 
     * @param contentResolver
     * @param isDeleteFile
     * @param taskIds
     */
    public void deleteTask(ContentResolver contentResolver, boolean isDeleteFile, List<Integer> taskIds) {
        deleteTask(contentResolver, isDeleteFile, taskIds, false);
    }

    public void deleteTask(ContentResolver contentResolver, boolean isDeleteFile,
                           List<Integer> taskIds, boolean isPreview) {

        final long begin = System.currentTimeMillis();
        Uri uri;
        if (isPreview) {
            uri = PreviewContract.Tasks.buildDeleteUri(mBduss, isDeleteFile);
        } else {
            uri = TransferContract.DownloadTasks.buildDeleteUri(mBduss, isDeleteFile);
        }
        // 触发更新文件表offline_status！
        if (!isPreview) {
            restoreFileOfflineStatus(contentResolver, uri, taskIds, isDeleteFile);
        }
        deleteTask(contentResolver, uri, taskIds);

        final long end = System.currentTimeMillis();
        DuboxLog.d(DownloadTaskProviderHelper.class.getSimpleName(),
                "deleteTask 耗时:" + (end - begin));
    }

    private void deleteTask(ContentResolver contentResolver, Uri uri, List<Integer> taskIds) {
        contentResolver.delete(uri, TransferContract.Tasks._ID + " IN (" + TextUtils.join(",", taskIds) + ")", null);
    }

    private void restoreFileOfflineStatus(ContentResolver contentResolver,
                                          Uri uri, List<Integer> taskIds, boolean isDeleteFile) {
        Context context = BaseApplication.getContext();
        String[] projection = new String[]{TransferContract.Tasks._ID,
                TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.STATE};
        if (context != null) {
            for (int id : taskIds) {
                Cursor cursor = contentResolver.query(uri,
                        projection, TransferContract.Tasks._ID + "=?",
                        new String[]{String.valueOf(id)}, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        final String remoteUrl =
                                cursor.getString(cursor.getColumnIndex(TransferContract.Tasks.REMOTE_URL));
                        // 删除文件一定会更新文件表offline_status
                        // 不删除文件的话，除了已下载完成的状态，其他状态下本地本来也没有文件，因此也设置offline_status
                        if (TextUtils.isEmpty(remoteUrl)) {
                            continue;
                        }
                        FilesContext.setFileOfflineStatusByServerPath(context,
                                remoteUrl, OfflineStatus.STATUS_DEFAULT.getStatus());
                        // 更新云图表下载状态
                        if (FileType.isImageOrVideo(FileUtils.getFileName(remoteUrl))) {
                            CloudImageContext.updateOfflineStatusByServerPath(context, remoteUrl,
                                    OfflineStatus.STATUS_DEFAULT.getStatus());
                            context.getContentResolver().notifyChange(RecordFilesContract.Companion.getUri(),
                                    null, false);
                        }
                    }
                } catch (Exception e) {
                    DuboxLog.e(getClass().getSimpleName(),
                            "error restoreFileOfflineStatus!", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }

    /**
     * 更新下载地址
     * 
     * @param contentResolver
     * @param taskId
     * @param remoteUrl
     * @return
     */
    public int updateDownloadingTaskRemoteUrl(ContentResolver contentResolver, long taskId, String remoteUrl) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.REMOTE_URL, remoteUrl);
        return updateTask(contentResolver, taskId, values);
    }

    /**
     * 更新任务状态
     * 
     * @param contentResolver
     * @param taskId
     * @param state
     * @param extraInfoNum
     * @return
     */
    public int updateDownloadingTaskState(ContentResolver contentResolver, long taskId, int state, int extraInfoNum) {
        return updateDownloadingTaskState(contentResolver, null, taskId, state, extraInfoNum);
    }

    public int updateDownloadingTaskState(ContentResolver contentResolver, Uri uri, long taskId, int state,
                                          int extraInfoNum) {
        return updateDownloadingTaskState(contentResolver, uri, taskId, state, extraInfoNum, null);
    }
    /**
     * 更新任务状态
     * 
     * @param contentResolver
     * @param taskId
     * @param state
     * @param extraInfoNum
     * @return
     */
    public int updateDownloadingTaskState(ContentResolver contentResolver, Uri uri, long taskId, int state,
            int extraInfoNum, String extraInfoStr) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.STATE, state);
        if (TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT != extraInfoNum) {
            values.put(TransferContract.Tasks.EXTRA_INFO_NUM, extraInfoNum);
            values.put(TransferContract.Tasks.EXTRA_INFO, extraInfoStr);
        }

        switch (state) {
            case TransferContract.Tasks.STATE_PENDING:
                values.put(TransferContract.Tasks.RATE, 0);
                break;
            case TransferContract.Tasks.STATE_FAILED:
            case TransferContract.Tasks.STATE_FINISHED:
            case TransferContract.Tasks.STATE_PAUSE:
                values.put(TransferContract.Tasks.RATE, 0);
                values.put(TransferContract.Tasks.DATE, System.currentTimeMillis());
                break;
        }

        return updateTask(contentResolver, uri, taskId, values);
    }

    /**
     * 更新正在进行的下载任务
     * 
     * @param contentResolver
     * @param taskId
     * @param values
     * @return
     */
    public int updateTask(ContentResolver contentResolver, long taskId, ContentValues values) {
        return updateTask(contentResolver, TransferContract.DownloadTasks.buildProcessingUri(mBduss), taskId, values);
    }

    /**
     * 更新正在进行的下载任务
     * 
     * @param contentResolver
     * @param taskId
     * @param values
     * @return
     */
    public int updateTask(ContentResolver contentResolver, Uri uri, long taskId, ContentValues values) {
        if (uri == null) {
            uri = TransferContract.DownloadTasks.buildProcessingUri(mBduss);
        }

        return contentResolver.update(uri, values, TransferContract.Tasks._ID + "=?", new String[] { String.valueOf(taskId) });
    }

    /**
     * @param contentResolver
     */
    public int pauseAllTasks(ContentResolver contentResolver) {
        final ContentValues values = new ContentValues(2);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PAUSE);
        values.put(TransferContract.Tasks.RATE, 0);

        return contentResolver.update(
                TransferContract.DownloadTasks.buildProcessingUri(mBduss),
                values,
                TransferContract.Tasks.STATE + "=" + String.valueOf(TransferContract.Tasks.STATE_RUNNING) + " OR " + TransferContract.Tasks.STATE + "="
                        + String.valueOf(TransferContract.Tasks.STATE_PENDING), null);
    }

    /**
     * 只是暂停全部非预览的任务
     * 
     * liulp 2015-05-11
     * 
     * @param contentResolver
     */
    public int pauseAllTasksForPreview(ContentResolver contentResolver) {
        final ContentValues values = new ContentValues(2);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PAUSE);
        values.put(TransferContract.Tasks.RATE, 0);

        return contentResolver
                .update(TransferContract.DownloadTasks.buildSchedulerUri(mBduss), values, "(" + TransferContract.Tasks.STATE + "="
                        + String.valueOf(TransferContract.Tasks.STATE_RUNNING) + " OR " + TransferContract.Tasks.STATE
                        + "=" + String.valueOf(TransferContract.Tasks.STATE_PENDING) + ")", null);
    }

    /**
     * @param contentResolver
     */
    public void startAllTasks(ContentResolver contentResolver) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
        contentResolver.update(TransferContract.DownloadTasks.buildProcessingUri(mBduss), values, TransferContract.Tasks.STATE + "=?",
                new String[] { String.valueOf(TransferContract.Tasks.STATE_PAUSE), });
    }

    /**
     * 恢复所有running态的下载任务
     * @param contentResolver resolver
     */
    public void restartAllTasks(ContentResolver contentResolver) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
        contentResolver.update(TransferContract.DownloadTasks.buildProcessingUri(mBduss), values, TransferContract.Tasks.STATE + "=?",
                new String[] { String.valueOf(TransferContract.Tasks.STATE_RUNNING), });
    }

    /**
     * 更新文件的本地md5值和本地存在路径
     * 
     * @param contentResolver 操作数据库的类
     * @param serverPath 文件服务器路径
     * @param localMd5 本地md5
     * @param localPath 本地存放路径
     * @param localLastModifyTime 本地最后修改时间
     * @return 新增数据的Uri
     * @throws RemoteException
     * @throws OperationApplicationException
     */
    public Uri insertFileLocalMd5AndPath(Uri uri, ContentResolver contentResolver, String serverPath, String localMd5,
            String localPath, long localLastModifyTime) {

        final ContentValues values = new ContentValues();
        values.put(TransferContract.DownloadTaskFiles.SERVER_PATH, serverPath);
        values.put(TransferContract.DownloadTaskFiles.LOCAL_MD5, localMd5);
        values.put(TransferContract.DownloadTaskFiles.LOCAL_PATH, localPath);
        values.put(TransferContract.DownloadTaskFiles.LOCAL_LAST_MODIFY_TIME, localLastModifyTime);

        return contentResolver.insert(uri, values);
    }

    /**
     * 查询文件系统文件的下载信息
     * 
     * @param contentResolver
     * @param serverPath
     * @return
     */
    public Cursor getDownloadFile(ContentResolver contentResolver, String serverPath) {
        return contentResolver.query(TransferContract.DownloadTaskFiles.buildUri(mBduss), TransferContract.DownloadTaskFiles.Query.PROJECTION,
                TransferContract.DownloadTaskFiles.SERVER_PATH + "=? COLLATE NOCASE", new String[] { serverPath }, null);
    }

    /**
     * 根据本地文件路径，查询任务是否已经在下载中状态
     *
     * @param contentResolver
     * @param localPath
     * @return
     */
    public boolean isSameDownloadingTaskByLocalUrl(ContentResolver contentResolver, String localPath) {

        final Cursor cursor = contentResolver.query(TransferContract.DownloadTasks.buildProcessingUri(mBduss),
                TransferContract.Tasks.CommonQuery.PROJECTION,
                TransferContract.Tasks.LOCAL_URL + "=? COLLATE NOCASE", new String[]{localPath}, null);

        if (cursor == null) {
            return false;
        }

        try {
            return cursor.getCount() > 0;
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "", e);
        } finally {
            cursor.close();
        }
        return false;
    }

    /**
     * 根据本地文件路径，查询任务是否已经在完成状态
     *
     * @param contentResolver
     * @param localPath
     * @return
     */
    public boolean isSameDownloadFinishTaskByLocalUrl(ContentResolver contentResolver, String localPath) {

        final Cursor cursor = contentResolver.query(TransferContract.DownloadTasks.buildFinishedUri(mBduss, true),
                TransferContract.Tasks.CommonQuery.PROJECTION, TransferContract.Tasks.LOCAL_URL + "=? COLLATE NOCASE",
                new String[]{localPath}, null);

        if (cursor == null) {
            return false;
        }

        try {
            return cursor.getCount() > 0;
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "", e);
        } finally {
            cursor.close();
        }
        return false;
    }

    public Cursor getDownloadFiles(ContentResolver contentResolver, ArrayList<CloudFile> wrrappers) {
        StringBuilder sql = new StringBuilder(" IN(");
        int len = wrrappers.size();
        for (int i = 0; i < len; i++) {
            String path = wrrappers.get(i).getFilePath();
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            path = path.replaceAll("'", "''");
            sql.append("'" + path + "'");
            if (i == len - 1) {
                sql.append(")");
            } else {
                sql.append(",");
            }
        }
        return contentResolver.query(TransferContract.DownloadTaskFiles.buildUri(mBduss), TransferContract.DownloadTaskFiles.Query.PROJECTION,
                TransferContract.DownloadTaskFiles.SERVER_PATH + sql.toString() + " COLLATE NOCASE", null, null);
    }

    /**
     * 获取当前正在下载的任务数量，包含下载中、等待下载
     *
     * @return num
     */
    public Cursor getDownloadingTaskNum() {

        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        Uri uri = TransferContract.DownloadTasks.buildProcessingUri(mBduss);
        String selection = TransferContract.DownloadTasks.STATE + " = " + TransferContract.Tasks.STATE_RUNNING
                + " OR " + TransferContract.DownloadTasks.STATE + " = " + TransferContract.Tasks.STATE_PENDING;

        return contentResolver.query(uri, null, selection, null, null);
    }


    /**
     * 获取下载表中的所有下载成功的cloudFile
     * @return
     */
    public List<CloudFile> getDownloadSuccessCloudFiles() {
        ArrayList<CloudFile> cloudFiles = new ArrayList<>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
            Uri uri = TransferContract.DownloadTasks.buildUri(mBduss);
            cursor = contentResolver.query(uri, new String[] { TransferContract.Tasks.LOCAL_URL,
                            CloudFileContract.Files.FILE_SERVER_MD5,
                            TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.SIZE },
                    TransferContract.DownloadTasks.STATE + "=?",
                    new String[] { String.valueOf(TransferContract.DownloadTasks.STATE_FINISHED) }, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    CloudFile cloudFile = new CloudFile();
                    cloudFile.localUrl = CursorExtKt.getStringOrDefault(cursor,
                            TransferContract.Tasks.LOCAL_URL, "");
                    cloudFile.md5 = CursorExtKt.getStringOrDefault(cursor,
                            CloudFileContract.Files.FILE_SERVER_MD5,"");
                    cloudFile.size = CursorExtKt.getLongOrDefault(cursor,
                            TransferContract.Tasks.SIZE, 0);
                    cloudFile.path = CursorExtKt.getStringOrDefault(cursor, TransferContract.Tasks.REMOTE_URL, "");
                    cloudFiles.add(cloudFile);
                }
            }
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cloudFiles;
    }

    /**
     * 获取下载表中的所有正在下载中的cloudFile
     * @return
     */
    public List<CloudFile> getDownloadingCloudFiles() {
        ArrayList<CloudFile> cloudFiles = new ArrayList<>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
            Uri uri = TransferContract.DownloadTasks.buildUri(mBduss);
            String selection = "state=? or state=? or state=?";
            String[] selectArgs = new String[]{
                    String.valueOf(TransferContract.Tasks.STATE_PENDING),
                    String.valueOf(TransferContract.Tasks.STATE_RUNNING),
                    String.valueOf(TransferContract.Tasks.STATE_PAUSE)};
            cursor = contentResolver.query(
                    uri,
                    new String[]{TransferContract.Tasks.LOCAL_URL,
                            CloudFileContract.Files.FILE_SERVER_MD5,
                            TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.SIZE},
                    selection,
                    selectArgs,
                    null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    CloudFile cloudFile = new CloudFile();
                    cloudFile.localUrl = CursorExtKt.getStringOrDefault(cursor,
                            TransferContract.Tasks.LOCAL_URL, "");
                    cloudFile.md5 = CursorExtKt.getStringOrDefault(cursor,
                            CloudFileContract.Files.FILE_SERVER_MD5, "");
                    cloudFile.size = CursorExtKt.getLongOrDefault(cursor,
                            TransferContract.Tasks.SIZE, 0);
                    cloudFile.path = CursorExtKt.getStringOrDefault(cursor, TransferContract.Tasks.REMOTE_URL, "");
                    cloudFiles.add(cloudFile);
                }
            }
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cloudFiles;
    }

    /**
     * 删除下载成功表中记录
     */
    public void deleteDownloadRecord(List<String> localUrls) {
        try {
            if (localUrls == null || localUrls.isEmpty()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String localUrl : localUrls) {
                sb.append("'").append(localUrl).append("'").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
            Uri uri = TransferContract.DownloadTasks.buildFinishedUri(mBduss, false);
            int count = contentResolver.delete(uri,
                    TransferContract.Tasks.LOCAL_URL + " in (" + sb.toString() + ")",
                    null);
            DuboxLog.e("DownloadTaskProviderHelper", "delete record " + count);
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "delete record failed", e);
        }
    }


    /**
     * 数据迁移完成后，更新数据库表
     */
    public int updateTransferFinishTask(CloudFile task) {
        if (task == null) {
            return 0;
        }
        ContentValues cv = new ContentValues();
        cv.put(TransferContract.Tasks.FILE_NAME, task.getFileName());
        cv.put(TransferContract.Tasks.LOCAL_URL, task.localUri);
        cv.put(TransferContract.Tasks.DATE, task.mDateTaken);

        String selection = "remote_url=?";
        String[] selectArgs = new String[]{task.getFilePath()};
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        Uri uri = TransferContract.DownloadTasks.buildFinishedUri(mBduss, false);
        return contentResolver.update(uri, cv, selection, selectArgs);
    }

    /**
     * 获取未完成的下载任务，用户数据迁移
     * @return
     */
    public List<DataTransferModel> getNotFinishDownloadTask() {
        List<DataTransferModel> tasks = new ArrayList<>();
        String[] projection = new String[] {
                TransferContract.DownloadTasks._ID,
                TransferContract.Tasks.LOCAL_URL, TransferContract.Tasks.REMOTE_URL,
                TransferContract.Tasks.SIZE, TransferContract.Tasks.TRANSMITTER_TYPE,
                CloudFileContract.Files.FILE_SERVER_MD5, TransferContract.DownloadTasks.PRIORITY,
                TransferContract.Tasks.P2P_FGID};
        String selection = "state=? or state=? or state=? or state=?";
        String[] selectArgs = new String[] {
                String.valueOf(TransferContract.Tasks.STATE_PENDING),
                String.valueOf(TransferContract.Tasks.STATE_RUNNING),
                String.valueOf(TransferContract.Tasks.STATE_PAUSE),
                String.valueOf(TransferContract.Tasks.STATE_FAILED)};
        Uri uri = TransferContract.DownloadTasks.buildUri(mBduss);
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        try {
            cursor = contentResolver.query(uri, projection, selection, selectArgs, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int taskId = CursorExtKt.getIntOrDefault(cursor,
                            TransferContract.DownloadTasks._ID, 0);
                    String localUrl = CursorExtKt.getStringOrDefault(cursor,
                            TransferContract.Tasks.LOCAL_URL, "");
                    String remoteUrl = CursorExtKt.getStringOrDefault(cursor,
                            TransferContract.Tasks.REMOTE_URL, "");
                    long fileSize = CursorExtKt.getLongOrDefault(cursor,
                            TransferContract.Tasks.SIZE, 0);
                    String transmitterType = CursorExtKt.getStringOrDefault(cursor,
                            TransferContract.Tasks.TRANSMITTER_TYPE, "");
                    String fileMd5 = CursorExtKt.getStringOrDefault(cursor,
                            CloudFileContract.Files.FILE_SERVER_MD5, "");
                    int priority = CursorExtKt.getIntOrDefault(cursor,
                            TransferContract.DownloadTasks.PRIORITY, 0);
                    String p2pGid = CursorExtKt.getStringOrDefault(cursor,
                            TransferContract.Tasks.P2P_FGID, "");
                    String fileName = FileUtils.getFileName(localUrl);
                    if (fileName == null) {
                        fileName = "";
                    }
                    DataTransferModel model = new DataTransferModel(taskId, localUrl, remoteUrl,
                            fileSize, fileMd5, transmitterType, priority, fileName, p2pGid);
                    tasks.add(model);
                }
            }
            return tasks;
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "", e);
            return tasks;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    /**
     * 添加下载任务, 专门用来做下载任务的数据迁移
     * @return
     */
    public void addDataTransferTask(DataTransferModel task) {
        if (task == null) {
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(TransferContract.Tasks.TYPE, TransferContract.DownloadTasks.TYPE_DOWNLOAD_FILE);
        cv.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
        cv.put(TransferContract.Tasks.LOCAL_URL, task.getLocalUrl());
        cv.put(TransferContract.Tasks.REMOTE_URL, task.getRemoteUrl());
        cv.put(TransferContract.Tasks.SIZE, task.getFileSize());
        cv.put(TransferContract.Tasks.TRANSMITTER_TYPE, task.getTransmitterType());
        cv.put(TransferContract.Tasks.DATE, System.currentTimeMillis());
        cv.put(CloudFileContract.Files.FILE_SERVER_MD5, task.getFileMd5());
        cv.put(TransferContract.Tasks.PRIORITY, task.getPriority());
        cv.put(TransferContract.Tasks.FILE_NAME, task.getFileName());
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        contentResolver.insert(TransferContract.DownloadTasks.buildProcessingUri(mBduss, false), cv);
    }

    public void deleteDownloadTaskByLocalUrl(String localUrl) {
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        String where = TransferContract.Tasks.LOCAL_URL + "=?";
        String[] args = new String[] {localUrl};
        contentResolver.delete(TransferContract.DownloadTasks.buildProcessingUri(mBduss, false),
                where, args);
    }

}