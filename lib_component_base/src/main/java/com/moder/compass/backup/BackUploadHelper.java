package com.moder.compass.backup;

import com.moder.compass.backup.transfer.SchedulerCompleteCode;
import com.moder.compass.base.imageloader.GlideHelper;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import java.util.ArrayList;

public class BackUploadHelper {

    private static volatile BackUploadHelper instance;
    public static int DEFAULT = 0;
    public static int PHOTO_TYPE_ERROR = 1;
    public static int VIDEO_TYPE_ERROR = 2;
    private int mPhotoErrorCode = -1;
    private int mVideoErrorCode = -1;
    private int mLastErrrorCode  = -1;
    /**
     * 压缩备份进度回调的list
     */
    private ArrayList<ICompressListener> mCompressListeners;

    public static BackUploadHelper getInstance() {
        if (instance == null) {
            synchronized (GlideHelper.class) {
                if (instance == null) {
                    instance = new BackUploadHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 构造
     */
    private BackUploadHelper() {
        mCompressListeners = new ArrayList<ICompressListener>();
    }

    public int getErrorCode() {
        return mLastErrrorCode;
    }
    public int getPhotoErrorCode() {
        return mPhotoErrorCode;
    }
    public int getVideoErrorCode() {
        return mVideoErrorCode;
    }

    public void setErrorCode(int mErrorCode, int type) {
        if (type == PHOTO_TYPE_ERROR) {
            this.mPhotoErrorCode = mErrorCode;
        } else if (type == VIDEO_TYPE_ERROR) {
            this.mVideoErrorCode = mErrorCode;
        }
        this.mLastErrrorCode = mErrorCode;
        DuboxLog.d("BackUploadHelper", "mErrorCode : ......." + mErrorCode);
    }

    public boolean isError() {
        if (mLastErrrorCode == SchedulerCompleteCode.SERVER_BAN
                || mLastErrrorCode == SchedulerCompleteCode.NO_REMOTE_SPACE
                || mLastErrrorCode == SchedulerCompleteCode.NO_SDCARD
                || mLastErrrorCode == SchedulerCompleteCode.NO_WIFI
                || mLastErrrorCode == SchedulerCompleteCode.NO_NETWORK
                || mLastErrrorCode == SchedulerCompleteCode.LOW_POWER) {
            return true;
        }
        return false;
    }

    public boolean isPhotoError() {
        if (mPhotoErrorCode == SchedulerCompleteCode.SERVER_BAN
                || mPhotoErrorCode == SchedulerCompleteCode.NO_REMOTE_SPACE
                || mPhotoErrorCode == SchedulerCompleteCode.NO_SDCARD
                || mPhotoErrorCode == SchedulerCompleteCode.NO_WIFI
                || mPhotoErrorCode == SchedulerCompleteCode.NO_NETWORK
                || mPhotoErrorCode == SchedulerCompleteCode.LOW_POWER
                || mPhotoErrorCode == SchedulerCompleteCode.NO_TASK
                || mPhotoErrorCode == SchedulerCompleteCode.SUCCESS ) {
            return true;
        }
        return false;
    }

    public boolean isVideoError() {
        if (mVideoErrorCode == SchedulerCompleteCode.SERVER_BAN
                || mVideoErrorCode == SchedulerCompleteCode.NO_REMOTE_SPACE
                || mVideoErrorCode == SchedulerCompleteCode.NO_SDCARD
                || mVideoErrorCode == SchedulerCompleteCode.NO_WIFI
                || mVideoErrorCode == SchedulerCompleteCode.NO_NETWORK
                || mVideoErrorCode == SchedulerCompleteCode.LOW_POWER
                || mVideoErrorCode == SchedulerCompleteCode.NO_TASK
                || mVideoErrorCode == SchedulerCompleteCode.SUCCESS ) {
            return true;
        }
        return false;
    }

    /**
     * 压缩备份进度回调
     *
     * @param percent 进度百分比
     */
    public void setVideoCompressPercent(int percent) {
        DuboxLog.d("BackUploadHelper", "BackUploadHelper setVideoCompressPercent : " + percent);
        notifyCompressPercentChange(percent);
    }

    /**
     * 添加压缩备份进度监听
     *
     * @param listener 监听器
     */
    public void addCompressListener(ICompressListener listener) {
        synchronized (this) {
            if (listener != null && !mCompressListeners.contains(listener)) {
                this.mCompressListeners.add(listener);
            }
        }
    }

    /**
     * 删除压缩备份进度监听
     *
     * @param listener 监听器
     */
    public void removeCompressListener(final ICompressListener listener) {
        synchronized (this) {
            if (listener != null) {
                this.mCompressListeners.remove(listener);
            }
        }
    }


    /**
     * 通知压缩备份进度变化
     *
     */
    public void notifyCompressPercentChange(int percent) {
        synchronized (this) {
            final int size = mCompressListeners.size();
            for (int i = 0; i < size; i++) {
                ICompressListener callback = mCompressListeners.get(i);
                if (callback != null) {
                    callback.onCompressPercentChange(percent);
                }
            }
        }
    }

}
