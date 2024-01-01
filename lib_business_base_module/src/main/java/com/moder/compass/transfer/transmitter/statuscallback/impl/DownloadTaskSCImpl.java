package com.moder.compass.transfer.transmitter.statuscallback.impl;

import static com.moder.compass.base.BusinessConstantKt.DOWNLOAD_FINISH_ACTION;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_DOWNLOAD_UPDATE;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_EXTRA_DATA;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;

import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.db.cloudfile.model.OfflineStatus;
import com.dubox.drive.db.record.contract.RecordFilesContract;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.statistics.BroadcastStatisticKt;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.statistics.EventStatisticsKt;
import com.moder.compass.statistics.StatisticsKeysKt;
import com.moder.compass.transfer.transmitter.TransferNumManager;
import com.moder.compass.transfer.transmitter.statuscallback.IStatusCallback;
import com.moder.compass.transfer.transmitter.statuscallback.ITransferStatusCallback;
import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.util.encode.MD5Util;
import com.moder.compass.transfer.storage.DownloadTaskProviderHelper;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;

import rubik.generate.context.dubox_com_dubox_drive_cloud_image.CloudImageContext;
import rubik.generate.context.dubox_com_dubox_drive_files.FilesContext;

/**
 * DownloadTask的传输状态回调实现
 * 
 * @author sunqi01
 * 
 */
public class DownloadTaskSCImpl implements ITransferStatusCallback {
    private static final String TAG = "DownloadTaskSCImpl";
    private final DownloadTaskProviderHelper mTaskProviderHelper;

    protected final ContentResolver mResolver;

    protected final int mTaskId;

    /**
     * 传输任务结束时的通知
     */
    protected final Uri mFinishUri;

    private final ContentValues mUpdateValuesCache;

    protected final String mBduss;

    private final Uri mProcessingUri;

    public DownloadTaskSCImpl(ContentResolver resolver, String bduss, int taskId) {
        mResolver = resolver;
        mTaskId = taskId;
        mTaskProviderHelper = new DownloadTaskProviderHelper(bduss);
        mBduss = bduss;
        mFinishUri = TransferContract.DownloadTasks.buildUri(bduss);
        mProcessingUri = TransferContract.DownloadTasks.buildProcessingUri(mBduss);

        mUpdateValuesCache = new ContentValues();
    }

    /**
     * @param reason 失败原因
     */
    @Override
    public void onFailed(int reason, String extraInfo) {
        DuboxLog.d(TAG, "onFailed:" + reason);

        Pair<Integer, Integer> failTaskErrnoState = getFailTaskInfo(reason);

        mTaskProviderHelper.updateDownloadingTaskState(mResolver, mProcessingUri, mTaskId,
                failTaskErrnoState.second, failTaskErrnoState.first, extraInfo);

        // 发送广播，通知接收
        final Uri taskUri = ContentUris.withAppendedId(TransferContract.DownloadTasks.buildUri(mBduss), mTaskId);

        // 发送失败信息
        sendFailMessage(taskUri, failTaskErrnoState.second, failTaskErrnoState.first);

        if (failTaskErrnoState.first != TransferContract.DownloadTasks.EXTRA_INFO_NUM_NO_SDCARD_SPACE) { // 不包含 本地空间不足
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_ERROR);
            // DT平台当日下载文件的用户数为统计
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_DOWNLOADFILES);
        }

        // 普通下载
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_QUEUE_DOWNLOAD);
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(true);
    }

    public Pair<Integer, Integer> getFailTaskInfo(int reason) {
        final int errno;
        final int state;
        switch (reason) {
            case TransmitterConstant.NETWORK_NO_CONNECTION:
                state = TransferContract.Tasks.STATE_PENDING;
                errno = TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT;
                break;
            case TransmitterConstant.NETWORK_NOT_AVAILABLE:
                state = TransferContract.Tasks.STATE_PENDING;
                errno = TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT;
                break;
            case TransmitterConstant.WAITING_FOR_WIFI:
                state = TransferContract.Tasks.STATE_PENDING;
                errno = TransmitterConstant.WAITING_FOR_WIFI; // 7.9为了在开放平台识别等待wifi，修改错误码 libin09
                // 2015-4-20
                break;
            case TransmitterConstant.SDCARD_NO_SPACE_ERROR:
                errno = TransferContract.DownloadTasks.EXTRA_INFO_NUM_NO_SDCARD_SPACE;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.FILE_DOES_NOT_EXISTS:
                errno = TransferContract.Tasks.EXTRA_INFO_NUM_FILE_NOT_EXIST;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SHARE_ID_NOT_EXIST:
                errno = TransferContract.DownloadTasks.EXTRA_INFO_NUM_CANCEL_SHARE;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.FILE_IS_ILLEGAL:
                errno = TransferContract.DownloadTasks.EXTRA_FILE_IS_ILLEGAL;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SERVER_FILE_IS_CHANGE:
                errno = TransferContract.DownloadTasks.EXTRA_FILE_HAS_CHANGE;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.REMOTE_PCS_FILE_IS_IMPERFECT:
            case TransmitterConstant.REMOTE_POMS_FILE_IS_IMPERFECT:
                errno = TransferContract.DownloadTasks.EXTRA_FILE_IS_IMPERFECT;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SERVER_FORBIDDEN_USER:
                errno = TransferContract.DownloadTasks.EXTRA_USER_IS_FORBIDDEN;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SDK_M3U8_TRANSFER_OVERTIME:
                errno = TransferContract.DownloadTasks.EXTRA_M3U8_TRANSFER_FAIL;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case OtherErrorCode.STREAM_EXCEPTION:
                errno = TransferContract.DownloadTasks.EXTRA_STREAM_EXCEPTION;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SERVER_FORBIDDEN_INVALID_KEY:
                errno = TransferContract.DownloadTasks.EXTRA_CHEAT_USER;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SDK_DLINK_REFRESH_EXCEPTION:
                errno = TransferContract.DownloadTasks.EXTRA_DLINK_REFRESH_FAIL;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SDK_TASK_NOT_EXIST:
                errno = TransferContract.DownloadTasks.EXTRA_TASK_NOT_EXIST_FAIL;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            // -10026 本地重命名失败
            case OtherErrorCode.LOCAL_RENAME_FAIL:
                errno = TransferContract.DownloadTasks.EXTRA_LOCAL_RENAME_FAIL;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            // SDK  41 本地重命名失败
            case TransmitterConstant.SDK_LOCAL_FILE_SYSTEM_ERROR:
                errno = TransferContract.DownloadTasks.EXTRA_LOCAL_FILE_SYSTEM_ERROR;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.SDK_P2P_CHECKSUM_ERROR:
                errno = TransferContract.DownloadTasks.EXTRA_P2P_CHECKSUM_ERROR;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case TransmitterConstant.USER_NOT_EXISTS:
                errno = TransferContract.DownloadTasks.EXTRA_USER_AUTH_ERROR;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
            case OtherErrorCode.ERROR_TARGET30_URI_NULL:
                errno = TransferContract.DownloadTasks.EXTRA_LOCAL_DOWNLOAD_URI_NULL;
                state = TransferContract.Tasks.STATE_FAILED;
                EventStatisticsKt.statisticActionEvent(StatisticsKeysKt.MEDIASTORE_CREATE_LOCAI_URL_NULL);
                break;
            default:
                errno = TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT;
                state = TransferContract.Tasks.STATE_FAILED;
                break;
        }
        return new Pair<>(errno, state);
    }

    /**
     * @see IStatusCallback#onSuccess(String content)
     */
    @Override
    public void onSuccess(String content) {
        // 设置任务完成态
        mTaskProviderHelper.updateDownloadingTaskState(mResolver, mProcessingUri, mTaskId, TransferContract.Tasks.STATE_FINISHED,
                TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);

        final Uri taskUri = ContentUris.withAppendedId(mFinishUri, mTaskId);

        final Cursor cursor =
                mResolver.query(taskUri, new String[] { TransferContract.Tasks.LOCAL_URL, TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.TRANSMITTER_TYPE,
                        TransferContract.Tasks.TYPE }, null, null, null);

        if (cursor == null) {
            return;
        }

        try {
            if (!cursor.moveToFirst()) {
                return;
            }

            final String localUrl = cursor.getString(0);
            final String remoteUrl = cursor.getString(1);
            RFile localRFile = PathKt.rFile(localUrl);

            StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.DOWNLOAD_TAB_FILE_DOWNLOAD_FINISH,
                    FileUtils.getExtension(localRFile.name()));

            // 发送广播，通知接收
            sendFinishMessage(remoteUrl, localUrl);

            // update 2.7.5:  下载完成后需要将记录更新到download_task_files表中，但之前需要先查询cachefilelist表，然后再更新下载表，
            // 由于保险箱数据没有插入到cachefilelist表中， 所以会导致保险箱重复下载，
            // 更新文件列表对应的md5
                Cursor fileListCursor = null;
                try {
                    // 查询文件列表里是否有这个文件，如果有，加入md5的数据
//                    final Uri uri;
//                    final String selection;
//                    final String[] args;

//                    uri = CloudFileContract.Files.buildFileServerPathUri(remoteUrl, mBduss);
//                    selection = null;
//                    args = null;
//
//                    fileListCursor = mResolver.query(uri,
//                            new String[] { CloudFileContract.Files._ID }, selection, args, null);
//
//                    if (fileListCursor == null) {
//                        return;
//                    }
//
//                    if (!fileListCursor.moveToFirst()) {
//                        return;
//                    }
                    // 本身在异步线程，不再另起Job
                    FilesContext.setFileOfflineStatusByServerPathSync(BaseApplication.getContext(),
                            remoteUrl, OfflineStatus.STATUS_OFFLINE.getStatus());
                    // (图片或者视频)更新云图表文件下载状态
                    if (FileType.isImageOrVideo(FileUtils.getFileName(remoteUrl))) {
                        CloudImageContext.updateOfflineStatusByServerPath(
                                BaseApplication.getContext(), remoteUrl,  OfflineStatus.STATUS_OFFLINE.getStatus());

                        // 通知最近观看表刷新数据， 顺序必须在云图表插入的后面，最近观看需要从云图表查询离线状态
                        BaseApplication.getContext().getContentResolver().notifyChange(
                                RecordFilesContract.Companion.getUri(), null, false);
                    }
                    updateFilesystemInfo(localRFile, remoteUrl);
                } catch (IllegalStateException ignore) {
                    DuboxLog.w(TAG, "loadInBackground", ignore);
                } catch (SQLiteException ignore) {
                    DuboxLog.w(TAG, "loadInBackground", ignore);
                } finally {
                    if (fileListCursor != null) {
                        fileListCursor.close();
                    }
                }
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "loadInBackground", ignore);
        } finally {
            TransferNumManager.getInstance().setTransferNumChanged(true);
            cursor.close();
        }

        // 预览下载
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_QUEUE_DOWNLOAD);
        /** 统计下载完成 **/
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_SUCCUSS);
        // DT平台当日下载文件的用户数为统计
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_DOWNLOADFILES);

        // 普通下载
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_QUEUE_DOWNLOAD_SUCCUSS);

        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(true);
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
        mUpdateValuesCache.put(TransferContract.Tasks.OFFSET_SIZE, size);
        if (rate >= 0L) {
            mUpdateValuesCache.put(TransferContract.Tasks.RATE, rate);
        }

        // 只有正在running的任务才更新进度，如果被暂停的任务，此处不会更新
        return mResolver.update(mProcessingUri, mUpdateValuesCache, TransferContract.Tasks._ID + "=? AND " + TransferContract.Tasks.STATE + "=?",
                new String[] { String.valueOf(mTaskId), String.valueOf(TransferContract.Tasks.STATE_RUNNING) });
    }

    /**
     * 更新文件列表对应的md5
     */
    private void updateFilesystemInfo(RFile localUrl, String remoteUrl) {
        scanFile(BaseApplication.getInstance(), localUrl.path()); // 加入媒体库
        String md5 = MD5Util.getMD5Digest(localUrl);

        final Uri uri = TransferContract.DownloadTaskFiles.buildUri(mBduss);
        final Uri result = mTaskProviderHelper.insertFileLocalMd5AndPath(uri, mResolver,
                remoteUrl, md5, localUrl.localUrl(), localUrl.lastModified());
        DuboxLog.d(TAG, "result:" + result);
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
     * @param localUrl 本地路径
     * @since 7.9 2015-4-20 libin09
     */
    private void sendFinishMessage(String remoteUrl, String localUrl) {
        DuboxLog.d(TAG, "sendFinishMessage " + remoteUrl + " localUrl " + localUrl);
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.REMOTE_URL, remoteUrl);
        data.putString(TransferContract.Tasks.LOCAL_URL, localUrl);
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_DOWNLOAD_UPDATE, mTaskId,
                TransferContract.Tasks.STATE_FINISHED, data);

        Intent intent = new Intent(DOWNLOAD_FINISH_ACTION);
        intent.putExtra(MESSAGE_EXTRA_DATA, data);
        BaseApplication.getContext().sendBroadcast(intent);
        BroadcastStatisticKt.statisticSendBroadcast(DOWNLOAD_FINISH_ACTION);
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
            cursor =
                    mResolver.query(taskUri, new String[] { TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.LOCAL_URL }, null, null, null);
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
            final String localUrl = cursor.getString(1);

            DuboxLog.d(TAG, "remoteUrl:" + remoteUrl + ",localUrl:" + localUrl);

            final Bundle data = new Bundle(3);
            data.putString(TransferContract.Tasks.REMOTE_URL, remoteUrl);
            data.putString(TransferContract.Tasks.LOCAL_URL, localUrl);
            data.putInt(TransferContract.Tasks.EXTRA_INFO_NUM, errno);
            EventCenterHandler.INSTANCE.sendMsg(MESSAGE_DOWNLOAD_UPDATE, mTaskId, state, data);
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "sendFailMessage", ignore);
        } finally {
            cursor.close();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(true);
    }
}
