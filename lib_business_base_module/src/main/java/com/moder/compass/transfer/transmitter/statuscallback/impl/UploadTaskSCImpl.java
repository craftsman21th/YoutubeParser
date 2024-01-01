/*
 * UploadTaskSCImpl.java
 * @author libin09
 * V 1.0.0
 * Create at 2014-3-8 下午4:28:57
 */
package com.moder.compass.transfer.transmitter.statuscallback.impl;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_COMPRESS_IMAGE;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_NOVEL_SUCCESS;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_SUCCESS;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_UPDATE;
import static com.moder.compass.statistics.UserFeatureKeysKt.KEY_USER_FEATURE_UPLOAD_SUCCESS;

import java.io.File;

import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.moder.compass.base.imageloader.GlideHelper;
import com.moder.compass.base.imageloader.GlideImageSize;
import com.moder.compass.base.imageloader.IImagePreLoadTask;
import com.moder.compass.base.imageloader.MediaStoreHelper;
import com.moder.compass.base.imageloader.ThumbnailHelper;
import com.moder.compass.base.imageloader.ThumbnailSizeType;
import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.UserFeatureReporter;
import com.moder.compass.transfer.storage.UploadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.UploadTaskManager;
import com.moder.compass.transfer.transmitter.TransferNumManager;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.statuscallback.IStatusCallback;
import com.moder.compass.transfer.transmitter.statuscallback.ITransferStatusCallback;
import com.dubox.glide.Priority;
import com.dubox.glide.load.engine.DiskCacheStrategy;
import com.dubox.glide.request.RequestOptions;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 上传传输器的回调
 *
 * @author libin09 <br/>
 * create at 2014-3-8 下午4:28:57
 */
public class UploadTaskSCImpl implements ITransferStatusCallback {
    private static final String TAG = "UploadTaskSCImpl";

    private final UploadTaskProviderHelper mTaskProviderHelper;

    private final ContentResolver mResolver;

    private final int mTaskId;

    private final String mBduss;

    private final ContentValues mUpdateValuesCache;

    private Uri mProcessingUri;

    public static final String ACTION_UPLOAD_TASK_COMPLETE_NOTIFY = "action_task_complete_notify";

    public UploadTaskSCImpl(ContentResolver resolver, String bduss, int taskId) {
        mResolver = resolver;
        mTaskId = taskId;
        mTaskProviderHelper = new UploadTaskProviderHelper(mBduss = bduss);
        mProcessingUri = TransferContract.UploadTasks.buildProcessingUri(mBduss);
        mUpdateValuesCache = new ContentValues();
    }

    /**
     * @param reason
     *
     * @see IStatusCallback#onFailed(int, String)
     */
    @Override
    public void onFailed(int reason, String extraInfo) {
        final Cursor cursor = queryCurrentTask();
        if (cursor == null) {
            return;
        }

        try {
            if (!cursor.moveToFirst()) {
                return;
            }

            final int type = cursor.getInt(0);
            final String localUrl = cursor.getString(1);
            final String remoteUrl = cursor.getString(2);

            final int errno;
            final int state;
            switch (reason) {
                case TransmitterConstant.SERVER_BAN:
                    errno = TransferContract.UploadTasks.EXTRA_INFO_SERVER_BAN;
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.LOCAL_FILE_ERROR:
                    if (type == TransferTask.TYPE_TASK_PHOTO || type == TransferTask.TYPE_TASK_VIDEO) {
                        StatisticsLog
                            .updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_FILE_NOT_EXIST_DCIM);
                    }
                    StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_FILE_NOT_EXIST);
                    errno = TransferContract.Tasks.EXTRA_INFO_NUM_FILE_NOT_EXIST;
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.NETWORK_NO_CONNECTION:
                    errno = TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT;
                    state = TransferContract.Tasks.STATE_PENDING;
                    break;
                case TransmitterConstant.NETWORK_NOT_AVAILABLE:
                    errno = TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT;
                    state = TransferContract.Tasks.STATE_PENDING;
                    break;
                case TransmitterConstant.WAITING_FOR_WIFI:
                    errno = TransmitterConstant.WAITING_FOR_WIFI;
                    state = TransferContract.Tasks.STATE_PENDING;
                    break;
                case TransmitterConstant.UPLOAD_BY_OTHER_APP:
                    errno = TransferContract.UploadTasks.EXTRA_INFO_NUM_UPLOAD_BY_OTHER_APP;
                    state = TransferContract.Tasks.STATE_FINISHED;
                    break;
                case TransmitterConstant.LOW_POWER:
                    errno = TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT;
                    state = TransferContract.Tasks.STATE_PENDING;
                    break;
                case TransmitterConstant.NO_REMOTE_SPACE:
                    errno = TransferContract.UploadTasks.EXTRA_INFO_NUM_NO_REMOTE_SPACE; // 空间不足
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.LOCAL_FILE_IS_IMPERFECT:
                    errno = TransferContract.UploadTasks.EXTRA_FILE_IS_IMPERFECT;
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.FILE_NAME_ILLEGAL:
                    errno = TransferContract.UploadTasks.EXTRA_FILE_NAME_ILLEGAL;
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.FILE_PARAMETER_ERROR:
                    errno = TransferContract.UploadTasks.EXTRA_FILE_PARAMETER_ERROR;
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.FILE_MORE_NUMBER:
                    errno = TransferContract.UploadTasks.EXTRA_FILE_MORE_NUMBER; // 文件数过多
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.FILE_SIZE_LIMIT:
                    errno = TransferContract.UploadTasks.EXTRA_FILE_SIZE_LIMIT; // 文件大小超过限制
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                case TransmitterConstant.SAFE_BOX_SIZE_LIMIT:
                    errno = TransferContract.UploadTasks.SAFE_BOX_SIZE_LIMIT; // 隐藏空间超限
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
                default:
                    errno = reason;
                    state = TransferContract.Tasks.STATE_FAILED;
                    break;
            }

            mTaskProviderHelper.updateUploadingTaskState(mResolver, mTaskId, state, errno);

            // 上传遇到空间已满错误时，暂停正在等待中的任务
            if (errno == TransferContract.UploadTasks.EXTRA_INFO_NUM_NO_REMOTE_SPACE) {
                final UploadTaskManager manager = new UploadTaskManager(Account.INSTANCE.getNduss(),
                    Account.INSTANCE.getUid());
                if (Account.INSTANCE.isSpaceFull()) {
                    manager.pauseAllTasks();
                    DuboxLog.d(TAG, "暂停等待中的任务。。。");
                }
            }

            // 发送失败信息
            sendUpdateMessage(localUrl, remoteUrl, state, errno);

            if (errno != TransferContract.UploadTasks.EXTRA_INFO_NUM_NO_REMOTE_SPACE
                || errno != TransferContract.Tasks.EXTRA_INFO_NUM_FILE_NOT_EXIST) {
                // 不包含 云端空间不足 或 文件不存在
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_UPLOAD_FAILED);
                // DT平台上传总数的统计
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_UPLOADFILES);
            }
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "onSuccess", ignore);
        } finally {
            cursor.close();
        }
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(false);
    }

    /**
     * @see IStatusCallback#onSuccess(String content)
     */
    @Override
    public void onSuccess(String content) {
        mTaskProviderHelper.updateUploadingTaskState(mResolver, mTaskId, TransferContract.Tasks.STATE_FINISHED,
            TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);

        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_UPLOAD_SUCCUSS);
        // DT平台上传总数的统计
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_UPLOADFILES);
        new UserFeatureReporter(KEY_USER_FEATURE_UPLOAD_SUCCESS).reportAFAndFirebase();
        final Cursor cursor = queryCurrentTask();
        if (cursor == null) {
            return;
        }

        try {
            if (!cursor.moveToFirst()) {
                return;
            }

            final String localPath = cursor.getString(1);
            // 文件上传成功时，判断文件的类型，并根据类型进行统计
            StatisticsLog.countUploadFileType(localPath);

            final String remotePath = cursor.getString(2);

            sendFinishMessage(localPath, remotePath, content);

            // 成功上传后，同时写入备份去重表
            mTaskProviderHelper.addAlbum(mResolver, PathKt.rFile(localPath), remotePath);

            // 将图片缓存到Glide中
            cacheThumbnails(localPath, remotePath);
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "onSuccess", ignore);
        } finally {
            cursor.close();
        }
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(false);
    }

    /**
     * @param size
     * @param rate
     *
     * @return
     *
     * @see IStatusCallback#onUpdate(long, long)
     */
    @Override
    public int onUpdate(long size, long rate) {
        mUpdateValuesCache.clear();
        mUpdateValuesCache.put(TransferContract.Tasks.OFFSET_SIZE, size);
        if (rate > 0L) {
            mUpdateValuesCache.put(TransferContract.Tasks.RATE, rate);
        }

        return mTaskProviderHelper.updateUploadingTask(mResolver, mProcessingUri, mTaskId, mUpdateValuesCache);
    }

    /**
     * 发送更新通知
     *
     * @param localUrl  本地路径
     * @param remoteUrl 云端路径
     * @param state     任务状态
     * @param errno     错误码
     *
     * @since 7.9 2015-4-20 libin09
     */
    private void sendUpdateMessage(String localUrl, String remoteUrl, int state, int errno) {
        final Bundle data = new Bundle(3);
        data.putString(TransferContract.Tasks.LOCAL_URL, localUrl);
        data.putString(TransferContract.Tasks.REMOTE_URL, remoteUrl);
        data.putInt(TransferContract.Tasks.EXTRA_INFO_NUM, errno);
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_UPDATE, mTaskId, state, data);
    }

    /**
     * 发送完成通知
     *
     * @param localUrl  本地路径
     * @param remoteUrl 云端路径
     *
     * @since 7.9 2015-4-20 libin09
     */
    private void sendFinishMessage(String localUrl, String remoteUrl, String content) {
        // 发送广播，通知接收
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.LOCAL_URL, localUrl);
        data.putString(TransferContract.Tasks.REMOTE_URL, remoteUrl);
        data.putString(UPLOAD_RESP_DATA, content);
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_UPDATE, mTaskId,
                TransferContract.Tasks.STATE_FINISHED, data);
        if (FileType.isImage(localUrl)) {
            EventCenterHandler.INSTANCE.sendMsg(MESSAGE_COMPRESS_IMAGE, mTaskId,
                    TransferContract.Tasks.STATE_FINISHED, data);
        }
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_SUCCESS, UPLOAD, TransferContract.Tasks
                .STATE_FINISHED, data);
        try {
            Intent intent=new Intent(ACTION_UPLOAD_TASK_COMPLETE_NOTIFY);
            intent.putExtras(data);
            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 小说上传成功后通知
        if (FileType.isNovel(localUrl)) {
            EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_NOVEL_SUCCESS, UPLOAD,
                    TransferContract.Tasks.STATE_FINISHED, data);
        }
    }

    /**
     * 缓存本地缩略图
     *
     * @param localPath  本地路径
     * @param remotePath 云端路径
     */
    private void cacheThumbnails(String localPath, String remotePath) {
        if (TextUtils.isEmpty(localPath) || TextUtils.isEmpty(remotePath)) {
            return;
        }

        String thumbnailPath = null;
        String completeRemotePath = null;

        if (FileType.isImage(localPath)) { // 上传的是图片
            // 1.从系统缩略图库查找缩略图文件
            String originImageId = MediaStoreHelper.getInstance().getOriginImageIdByPath(localPath);
            thumbnailPath = MediaStoreHelper.getInstance().getThumbnailPathById(originImageId);

            // 2.系统缩略图库没有，则由原图生成
            if (TextUtils.isEmpty(thumbnailPath)) {
                thumbnailPath = localPath;
            }
        } else if (FileType.isVideo(localPath)) { // 上传的是视频
            // 1.从系统缩略图库查找缩略图文件
            String originVideoId = MediaStoreHelper.getInstance().getOriginVideoIdByPath(localPath);
            thumbnailPath = MediaStoreHelper.getInstance().getThumbnailPathByVideoId(originVideoId);

            // 2.系统缩略图库没有，则判断是否属于微信文件，并查找文件
            if (TextUtils.isEmpty(thumbnailPath)) {
                String imagePath = "";
                int from = localPath.toLowerCase().lastIndexOf(".mp4");

                if (from != -1) {
                    imagePath = localPath.substring(0, from) + ".jpg";
                }

                if (!TextUtils.isEmpty(imagePath) && (new File(imagePath).exists())) {
                    thumbnailPath = imagePath;
                }
            }
        }

        completeRemotePath = new ThumbnailHelper(BaseApplication.getInstance()).makeRemoteUrlByPath(remotePath);
        DuboxLog.d(TAG, "preloadLocal >>> completeRemotePath:"
            + completeRemotePath + " thumbnailPath:" + thumbnailPath);

        if (!TextUtils.isEmpty(thumbnailPath)) {
            // 3.根据手机分辨率获取144*144或300*300的尺寸
            ThumbnailHelper thumbnailHelper = new ThumbnailHelper(BaseApplication.getInstance());
            GlideImageSize gridSize = thumbnailHelper.getImageSizeByType(
                ThumbnailSizeType.THUMBNAIL_SIZE_96);

            // 4.预加载指定策略的图片
            RequestOptions mOptions = new RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(gridSize.mWidth, gridSize.mHeight)
                .apply(new RequestOptions().priority(Priority.LOW));

            IImagePreLoadTask.PreLoadResultListener listener = new IImagePreLoadTask.PreLoadResultListener() {
                @Override
                public void onLoadFailed(String url) {
                    DuboxLog.d(TAG, "preloadLocal >>> onLoadFailed url " + url);
                }

                @Override
                public void onResourceReady(String url) {
                    DuboxLog.d(TAG, "preloadLocal >>> onResourceReady url " + url);
                }
            };

            GlideHelper.getInstance().addPreLoadTaskByUrl(null, "file://" + thumbnailPath,
                completeRemotePath, mOptions, gridSize, listener);
        }
    }

    @Override
    public void onStart() {
        final Cursor cursor = queryCurrentTask();
        if (cursor == null) {
            return;
        }

        // 发送上传开始通知
        try {
            if (!cursor.moveToFirst()) {
                return;
            }
            final String localPath = cursor.getString(1);
            final String remotePath = cursor.getString(2);
            sendUpdateMessage(localPath, remotePath, TransferContract.Tasks.STATE_RUNNING, -1);
        } catch (Exception e) {
            DuboxLog.w(TAG, "onStart " + e.getMessage());
        } finally {
            cursor.close();
        }
    }

    @Override
    public void onPause() {
        final Cursor cursor = queryCurrentTask();
        if (cursor == null) {
            return;
        }

        // 发送暂停通知
        try {
            if (!cursor.moveToFirst()) {
                return;
            }
            final String localPath = cursor.getString(1);
            final String remotePath = cursor.getString(2);
            sendUpdateMessage(localPath, remotePath, TransferContract.Tasks.STATE_PAUSE, -1);
        } catch (Exception e) {
            DuboxLog.w(TAG, "onPause " + e.getMessage());
        } finally {
            cursor.close();
        }

        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(false);
    }

    /**
     * 查询当前任务
     *
     * @return
     */
    private Cursor queryCurrentTask() {
        final Uri taskUri = ContentUris.withAppendedId(TransferContract.UploadTasks.buildUri(mBduss), mTaskId);
        final Cursor cursor =
            mResolver.query(taskUri, new String[] {TransferContract.Tasks.TYPE, TransferContract.Tasks.LOCAL_URL,
                    TransferContract.Tasks.REMOTE_URL}, null, null,
                null);
        return cursor;
    }
}
