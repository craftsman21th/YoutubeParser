/*
 * UploadFactory.java
 * @author libin09
 * V 1.0.0
 * Create at 2014-1-24 下午2:10:37
 */
package com.moder.compass.transfer.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.log.transfer.ITransferCalculable;

import com.moder.compass.transfer.transmitter.ratelimiter.IRateLimitable;
import com.moder.compass.util.FirebaseRemoteConfigKeysKt;

import rubik.generate.context.dubox_com_dubox_drive_vip.VipContext;

/**
 * UploadSchedulerFactory
 *
 * @author libin09 <br/>
 *         create at 2014-1-24 下午2:10:37
 */
class UploadSchedulerFactory extends AbstractSchedulerFactory {

    /**
     * @param resolver
     * @param bduss
     * @param uid
     */
    UploadSchedulerFactory(ContentResolver resolver, String bduss, String uid) {
        super(resolver, bduss, uid);
    }

    /**
     * @return
     * @see AbstractSchedulerFactory#createProjection()
     */
    @Override
    public String[] createProjection() {
        return new String[] { TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL, TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE, TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE,
                TransferContract.Tasks.OFFSET_SIZE, TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.DATE, TransferContract.Tasks.PRIORITY, TransferContract.UploadTasks.NEED_OVERRIDE,
                TransferContract.Tasks.EXTRA_INFO_NUM, TransferContract.UploadTasks.QUALITY, TransferContract.UploadTasks.UPLOAD_ID };
    }

    /**
     * @return
     * @see AbstractSchedulerFactory#createUpdateUri()
     */
    @Override
    public Uri createUpdateUri() {
        return TransferContract.UploadTasks.buildSchedulerUri(mBduss);
    }

    /**
     * @return
     * @see AbstractSchedulerFactory#getNotificationType()
     */
    @Override
    public int getNotificationType() {
        return TransferTask.TYPE_TASK_UPLOAD;
    }

    /**
     * @param cursor
     * @return
     * @see AbstractSchedulerFactory
     */
    @Override
    public TransferTask createTask(Context context, Cursor cursor, IRateLimitable rateLimiter,
            ITransferCalculable transferCalculable) {
        return new UploadTask(context, cursor, mBduss, mUid, transferCalculable);
    }

    /**
     * @param task
     * @param cursor
     * @see AbstractSchedulerFactory#syncTaskInfo(TransferTask, android.database.Cursor)
     */
    @Override
    public void syncTaskInfo(TransferTask task, Cursor cursor) {
        final int state = cursor.getInt(cursor.getColumnIndex(TransferContract.Tasks.STATE));
        final long offsetSize = cursor.getLong(cursor.getColumnIndex(TransferContract.Tasks.OFFSET_SIZE));

        final int qualityIndex = cursor.getColumnIndex(TransferContract.UploadTasks.QUALITY);
        if (qualityIndex >= 0) {
            ((UploadTask) task).mQuality = cursor.getInt(qualityIndex);
        }

        final int uploadIdIndex = cursor.getColumnIndex(TransferContract.UploadTasks.UPLOAD_ID);
        if (uploadIdIndex >= 0) {
            ((UploadTask) task).mUploadId = cursor.getString(uploadIdIndex);
        }
        task.mState = state;
        task.mOffset = offsetSize;
    }

    /**
     * @return
     * @see AbstractSchedulerFactory#createClearTaskUri()
     */
    @Override
    public Uri createClearTaskUri() {
        return TransferContract.DeletedUploadTasks.buildUri(mBduss);
    }

    @Override
    public String createOrderBy() {
        return TransferContract.Tasks.PRIORITY + " DESC,CASE WHEN " + TransferContract.Tasks.PRIORITY + "=1 THEN " + TransferContract.Tasks.DATE + " ELSE 0 END DESC";
    }

    @Override
    public boolean isSupportNotification() {
        return true;
    }

    @Override
    public boolean isSupportWifiOnly() {
        return true;
    }

    @Override
    public boolean transferVideoEnable() {
        return !FirebaseRemoteConfigKeysKt.isUploadVideoPremiumSwitchOpen() || VipContext.Companion.isVip();
    }
}