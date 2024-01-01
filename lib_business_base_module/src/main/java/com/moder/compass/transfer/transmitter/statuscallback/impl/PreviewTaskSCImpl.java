package com.moder.compass.transfer.transmitter.statuscallback.impl;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_PREVIEW_UPDATE;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.moder.compass.BaseApplication;
import com.dubox.drive.network.base.ServerResultHandler;
import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.encode.MD5Util;
import com.moder.compass.statistics.BroadcastStatisticKt;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.transfer.storage.DownloadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract.DownloadTasks;
import com.dubox.drive.db.transfer.contract.TransferContract.Tasks;
import com.dubox.drive.db.preview.contract.PreviewContract;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.statuscallback.IStatusCallback;

import java.io.File;

/**
 * DownloadTask的传输状态回调实现
 * 
 * @author sunqi01
 * 
 */
public class PreviewTaskSCImpl implements IStatusCallback {
    private static final String TAG = "PreviewTaskSCImpl";

    private final DownloadTaskProviderHelper mTaskProviderHelper;

    private final ContentResolver mResolver;

    private final int mTaskId;

    /**
     * 传输任务结束时的通知
     */
    private final Uri mUri;

    /**
     * 传输过程中更新进度的通知
     */

    private final String mBduss;

    private final ContentValues mUpdateValuesCache;

    public PreviewTaskSCImpl(ContentResolver resolver, String bduss, int taskId) {
        mResolver = resolver;
        mTaskId = taskId;
        mTaskProviderHelper = new DownloadTaskProviderHelper(bduss);
        mBduss = bduss;
        mUri = PreviewContract.Tasks.buildUri(bduss);

        mUpdateValuesCache = new ContentValues();
    }

    @Override
    public void onFailed(int reason, String extraInfo) {
        final int errno;
        final int state;
        switch (reason) {
            case TransmitterConstant.NETWORK_NO_CONNECTION:
                state = Tasks.STATE_FAILED;
                errno = TransmitterConstant.NETWORK_NO_CONNECTION;
                break;
            case TransmitterConstant.NETWORK_NOT_AVAILABLE:
                state = Tasks.STATE_FAILED;
                ServerResultHandler.sendMsg(ServerResultHandler.NETWORK_EXCEPTION_TOAST, 0, 0);
                errno = TransmitterConstant.NETWORK_NOT_AVAILABLE;
                break;
            case TransmitterConstant.WAITING_FOR_WIFI:
                state = Tasks.STATE_PENDING;
                errno = TransmitterConstant.WAITING_FOR_WIFI;
                break;
            case TransmitterConstant.SDCARD_NO_SPACE_ERROR:
                errno = DownloadTasks.EXTRA_INFO_NUM_NO_SDCARD_SPACE;
                state = Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.FILE_DOES_NOT_EXISTS:
                errno = Tasks.EXTRA_INFO_NUM_FILE_NOT_EXIST;
                state = Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SHARE_ID_NOT_EXIST:
                errno = DownloadTasks.EXTRA_INFO_NUM_CANCEL_SHARE;
                state = Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SERVER_FORBIDDEN_USER:
                errno = DownloadTasks.EXTRA_USER_IS_FORBIDDEN;
                state = Tasks.STATE_FAILED;
                break;
            default:
                errno = Tasks.EXTRA_INFO_NUM_DEFAULT;
                state = Tasks.STATE_FAILED;
                break;
        }

        // 使用单独task的uri更新，防止监听的uri通知多次
        mTaskProviderHelper.updateDownloadingTaskState(mResolver, mUri, mTaskId, state, errno, extraInfo);

        // 发送失败信息
        sendFailMessage(ContentUris.withAppendedId(mUri, mTaskId), state, errno);

        if (errno != DownloadTasks.EXTRA_INFO_NUM_NO_SDCARD_SPACE) { // 不包含 本地空间不足
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_ERROR);
            // DT平台当日下载文件的用户数为统计
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_DOWNLOADFILES);
        }

        // 预览下载
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_PREVIEW_DOWNLOAD);

        DuboxLog.d(TAG, "onFailed:" + reason);
    }

    /**
     * @see IStatusCallback#onSuccess(String content)
     */
    @Override
    public void onSuccess(String content) {
        DuboxLog.d(TAG, "onSuccess:" + content);
        // 设置任务完成态
        mTaskProviderHelper.updateDownloadingTaskState(mResolver, mUri, mTaskId, Tasks.STATE_FINISHED,
                Tasks.EXTRA_INFO_NUM_DEFAULT);

        // 更新文件列表对应的md5
        final Uri taskUri = ContentUris.withAppendedId(mUri, mTaskId);

        // 查询任务信息
        final Cursor cursor =
                mResolver.query(taskUri, new String[] { Tasks.LOCAL_URL, Tasks.REMOTE_URL, Tasks.TRANSMITTER_TYPE },
                        null, null, null);

        if (cursor == null) {
            return;
        }

        try {
            if (!cursor.moveToFirst()) {
                return;
            }

            final String localUrl = cursor.getString(0);
            final String remoteUrl = cursor.getString(1);
            final String transmitterType = cursor.getString(2);

            // 发送广播，通知接收
            sendFinishMessage(remoteUrl);

            // 网盘内下载后，更新去重表对应的md5
            updateFilesystemInfo(localUrl, remoteUrl);
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "onSuccess", ignore);
        } finally {
            cursor.close();
        }

        // 预览下载
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_PREVIEW_DOWNLOAD);
        /** 统计下载完成 **/
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_SUCCUSS);
        // DT平台当日下载文件的用户数为统计
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_DOWNLOADFILES);
        // 预览下载
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_PREVIEW_DOWNLOAD_SUCCUSS);
    }

    /**
     * @param size
     * @param rate
     * @return
     * @see IStatusCallback#onUpdate(long, long)
     */
    @Override
    public int onUpdate(long size, long rate) {
        mUpdateValuesCache.clear();
        mUpdateValuesCache.put(Tasks.OFFSET_SIZE, size);
        if (rate >= 0L) {
            mUpdateValuesCache.put(Tasks.RATE, rate);
        }

        return mTaskProviderHelper.updateTask(mResolver, mUri, mTaskId, mUpdateValuesCache);
    }

    /**
   * 
   */
    private void updateFilesystemInfo(String localUrl, String remoteUrl) {
        scanFile(BaseApplication.getInstance(), localUrl);// 加入媒体库
        String md5 = MD5Util.getMD5Digest(PathKt.rFile(localUrl));

        mTaskProviderHelper.insertFileLocalMd5AndPath(PreviewContract.TaskFiles.buildUri(mBduss), mResolver, remoteUrl,
                md5, localUrl, new File(localUrl).lastModified());
    }

    private void scanFile(Context context, String path) {
        Uri data = Uri.parse("file://" + path);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
        BroadcastStatisticKt.statisticSendBroadcast(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    }

    /**
     * 发送完成通知
     *
     * @param remoteUrl 云端路径
     * @since 7.9 2015-4-20 libin09
     */
    private void sendFinishMessage(String remoteUrl) {
        final Bundle data = new Bundle(1);
        data.putString(Tasks.REMOTE_URL, remoteUrl);
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_PREVIEW_UPDATE, mTaskId, Tasks.STATE_FINISHED, data);
        DuboxLog.d(TAG, "sendFinishMessage:" + data);
    }

    /**
     * 发送失败通知
     *
     * @param taskUri 任务Uri
     * @param state 任务状态
     * @param errno 错误码
     * @since 7.9 2015-4-20 libin09
     */
    private void sendFailMessage(Uri taskUri, int state, int errno) {
        Cursor cursor = null;
        try {
            cursor = mResolver.query(taskUri, new String[] { Tasks.REMOTE_URL }, null, null, null);
        } catch (IllegalStateException e) {
            DuboxLog.e(TAG, "ignore", e);
        }

        if (cursor == null) {
            return;
        }

        try {
            if (!cursor.moveToFirst()) {
                return;
            }

            final String remoteUrl = cursor.getString(0);

            final Bundle data = new Bundle(2);
            data.putString(Tasks.REMOTE_URL, remoteUrl);
            data.putInt(Tasks.EXTRA_INFO_NUM, errno);
            EventCenterHandler.INSTANCE.sendMsg(MESSAGE_PREVIEW_UPDATE, mTaskId, state, data);
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "sendFailMessage", ignore);
        } finally {
            cursor.close();
        }
    }
}
