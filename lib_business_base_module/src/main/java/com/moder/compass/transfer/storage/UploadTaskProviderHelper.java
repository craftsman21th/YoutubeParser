/*
 * UploadTaskProviderHelper.java
 * @author libin09
 * V 1.0.0
 * Create at 2013-12-19 下午5:24:10
 */
package com.moder.compass.transfer.storage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.transfer.model.UploadTaskTransferModel;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.UploadTask;
import com.mars.united.core.os.database.CursorExtKt;

import java.util.ArrayList;
import java.util.List;

/**
 * @author libin09 <br/>
 * 传输文件的provider帮助类<br/>
 * create at 2013-12-19 下午5:24:10
 */
public class UploadTaskProviderHelper {
    private static final String TAG = "UploadTaskProviderHelper";

    /**
     * 用于区别账号，每个操作只影响自己所在账号
     */
    private String mBduss;

    /**
     * 构造方法
     *
     * @param bduss 用于标识账号
     */
    public UploadTaskProviderHelper(String bduss) {
        mBduss = bduss;
    }

    /**
     * 添加上传任务
     *
     * @param contentResolver
     * @param task
     *
     * @return
     */
    public Uri addUploadingTask(ContentResolver contentResolver, TransferTask task, boolean isNotify) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.TYPE, task.mType);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
        values.put(TransferContract.Tasks.LOCAL_URL, task.mLocalFileMeta.localUrl());
        values.put(TransferContract.Tasks.REMOTE_URL, task.mRemoteUrl);
        values.put(TransferContract.Tasks.SIZE, task.mSize);

        if (task instanceof UploadTask) {
            UploadTask uploadTask = (UploadTask) task;
            values.put(TransferContract.UploadTasks.NEED_OVERRIDE, uploadTask.mConflictStrategy);
            values.put(TransferContract.UploadTasks.QUALITY, uploadTask.mQuality);
        }

        if (!TextUtils.isEmpty(task.mTransmitterType)) {
            values.put(TransferContract.Tasks.TRANSMITTER_TYPE, task.mTransmitterType);
        }
        values.put(TransferContract.Tasks.DATE, System.currentTimeMillis());

        return contentResolver.insert(TransferContract.UploadTasks.buildProcessingUri(mBduss, isNotify), values);
    }

    /**
     * 添加上传完成任务记录
     *
     * @param contentResolver
     * @param task
     */
    public Uri addUploadFinishTask(ContentResolver contentResolver, TransferTask task, boolean isNotify) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.TYPE, task.mType);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FINISHED);
        values.put(TransferContract.Tasks.LOCAL_URL, task.mLocalFileMeta.localUrl());
        values.put(TransferContract.Tasks.REMOTE_URL, task.mRemoteUrl);
        values.put(TransferContract.Tasks.SIZE, task.mSize);
        values.put(TransferContract.Tasks.OFFSET_SIZE, task.mSize);
        values.put(TransferContract.Tasks.FILE_NAME, task.mSize);
        if (!TextUtils.isEmpty(task.mTransmitterType)) {
            values.put(TransferContract.Tasks.TRANSMITTER_TYPE, task.mTransmitterType);
        }

        return contentResolver.insert(TransferContract.UploadTasks.buildFinishedUri(mBduss, isNotify), values);
    }

    /**
     * 删除上传任务
     *
     * @param contentResolver
     * @param isDeleteFile
     * @param taskIds
     *
     * @return
     */
    public int deleteTask(ContentResolver contentResolver, boolean isDeleteFile, List<Integer> taskIds) {
        return contentResolver.delete(TransferContract.UploadTasks.buildDeleteUri(mBduss, isDeleteFile),
            TransferContract.Tasks._ID + " IN (" + TextUtils.join(",", taskIds) + ")", null);
    }

    /**
     * 更新任务状态
     *
     * @param contentResolver
     * @param taskId
     * @param state
     * @param extraInfoNum
     *
     * @return
     */
    public int updateUploadingTaskState(ContentResolver contentResolver, long taskId, int state, int extraInfoNum) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.STATE, state);
        if (TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT != extraInfoNum) {
            values.put(TransferContract.Tasks.EXTRA_INFO_NUM, extraInfoNum);
        }

        switch (state) {
            case TransferContract.Tasks.STATE_FAILED:
            case TransferContract.Tasks.STATE_FINISHED:
            case TransferContract.Tasks.STATE_PAUSE:
            case TransferContract.Tasks.STATE_PENDING:
                values.put(TransferContract.Tasks.RATE, 0);
                break;
        }

        try {
            return updateUploadingTask(contentResolver, TransferContract.UploadTasks.buildProcessingUri(mBduss), taskId,
                values);
        } catch (IllegalStateException e) {
            DuboxLog.e(TAG, "ignore", e);
            return -1;
        }
    }

    /**
     * 更新正在上传的任务
     *
     * @param contentResolver
     * @param taskId
     * @param values
     *
     * @return
     */
    public int updateUploadingTask(ContentResolver contentResolver, Uri uri, long taskId, ContentValues values) {
        return contentResolver
            .update(uri, values, TransferContract.Tasks._ID + "=?", new String[] {String.valueOf(taskId)});
    }

    /**
     * @param contentResolver
     */
    public void startAllTasks(ContentResolver contentResolver) {
        final ContentValues values = new ContentValues(1);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
        contentResolver.update(TransferContract.UploadTasks.buildProcessingUri(mBduss), values,
            TransferContract.Tasks.STATE + "=?",
            new String[] {String.valueOf(TransferContract.Tasks.STATE_PAUSE)});
    }

    /**
     * @param contentResolver
     */
    public int pauseAllTasks(ContentResolver contentResolver) {
        final ContentValues values = new ContentValues(2);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PAUSE);
        values.put(TransferContract.Tasks.RATE, 0);

        return contentResolver.update(TransferContract.UploadTasks.buildProcessingUri(mBduss), values,
            "(" + TransferContract.Tasks.STATE + "=? OR "
                + TransferContract.Tasks.STATE + "=?)",
            new String[] {String.valueOf(TransferContract.Tasks.STATE_RUNNING),
                String.valueOf(TransferContract.Tasks.STATE_PENDING)});
    }

    /**
     * 插入相册备份数据
     *
     * @param contentResolver
     * @param item
     *
     * @return
     */
    public Uri addAlbum(ContentResolver contentResolver, RFile item, String remoteUrl) {
        final ContentValues values = new ContentValues(4);
        values.put(TransferContract.Tasks.LOCAL_URL, item.localUrl());
        values.put(TransferContract.Tasks.REMOTE_URL, remoteUrl);
        values.put(TransferContract.Tasks.SIZE, item.length());
        values.put(TransferContract.Tasks.DATE, item.lastModified());

        return contentResolver.insert(TransferContract.AlbumBackupTasks.buildUri(mBduss), values);
    }

    /**
     * 获取正在上传的任务数量
     *
     * @return num
     */
    public Cursor getUploadingTaskNum() {

        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        Uri uri = TransferContract.UploadTasks.buildProcessingUri(mBduss);
        String selection = TransferContract.UploadTasks.STATE + " = " + TransferContract.Tasks.STATE_RUNNING
            + " OR " + TransferContract.UploadTasks.STATE + " = " + TransferContract.Tasks.STATE_PENDING;

        return contentResolver.query(uri, null, selection, null, null);
    }

    /**
     * 获取未完成的上传任务，用户数据迁移
     *
     * @return
     */
    @Nullable
    public List<UploadTaskTransferModel> getNotFinishUploadTask() {
        List<UploadTaskTransferModel> tasks = new ArrayList<>();
        String[] projection = new String[] {
            TransferContract.UploadTasks._ID,
            TransferContract.Tasks.LOCAL_URL};
        String selection = "state != ?";
        String[] selectArgs = new String[] {
            String.valueOf(TransferContract.Tasks.STATE_FINISHED)};
        Uri uri = TransferContract.UploadTasks.buildUri(mBduss);
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        try {
            cursor = contentResolver.query(uri, projection, selection, selectArgs, null, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                int taskId = CursorExtKt.getIntOrDefault(cursor,
                    TransferContract.DownloadTasks._ID, 0);
                String localUrl = CursorExtKt.getStringOrDefault(cursor,
                    TransferContract.Tasks.LOCAL_URL, "");
                tasks.add(new UploadTaskTransferModel(taskId,localUrl));
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
     * 查询未完成的任务
     */
    @Nullable
    public List<String> queryNotFinishedTask(List<String> localUrls) {
        if (localUrls == null || localUrls.isEmpty()) {
            return null;
        }
        String columnLocalUrl = TransferContract.Tasks.LOCAL_URL;
        List<String> tasks = new ArrayList<>();
        String[] projection = new String[]{columnLocalUrl};
        String[] selectArgs = new String[]{
                String.valueOf(TransferContract.Tasks.STATE_FINISHED)};
        Uri uri = TransferContract.UploadTasks.buildUri(mBduss);
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        try {
            cursor = contentResolver.query(uri, projection, "state != ? AND " + columnLocalUrl + " IN " +
                    "('" + TextUtils.join("','", localUrls) + "')", selectArgs, null, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                String localUrl = CursorExtKt.getStringOrDefault(cursor,
                        TransferContract.Tasks.LOCAL_URL, "");
                tasks.add(localUrl);
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
}