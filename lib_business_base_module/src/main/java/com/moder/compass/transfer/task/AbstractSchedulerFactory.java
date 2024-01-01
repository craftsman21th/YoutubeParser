/*
 * AbstractTransferFactory.java
 * classes : com.dubox.drive.filetransfer.AbstractTransferFactory
 * @author libin09
 * V 1.0.0
 * Create at 2014-1-24 下午1:46:26
 */
package com.moder.compass.transfer.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.moder.compass.log.transfer.ITransferCalculable;
import com.moder.compass.transfer.transmitter.ratelimiter.IRateLimitable;

/**
 * com.dubox.drive.filetransfer.AbstractSchedulerFactory
 * 
 * @author libin09 <br/>
 *         create at 2014-1-24 下午1:46:26
 */
public abstract class AbstractSchedulerFactory {

    protected String mBduss;
    protected String mUid;
    protected ContentResolver mResolver;

    /**
     * @param bduss
     * @param resolver
     */
    public AbstractSchedulerFactory(ContentResolver resolver, String bduss, String uid) {
        mResolver = resolver;
        mBduss = bduss;
        mUid = uid;
    }

    /**
     * @return
     */
    public abstract Uri createUpdateUri();

    /**
     * @return
     */
    public abstract Uri createClearTaskUri();

    /**
     * @return
     */
    public abstract String[] createProjection();

    /**
     * @param context
     * @param cursor
     * @return
     */
    public abstract TransferTask createTask(Context context, Cursor cursor, IRateLimitable rateLimiter,
            ITransferCalculable transferCalculable);

    /**
     * 同步信息
     * 
     * @param task
     * @param cursor
     */
    public abstract void syncTaskInfo(TransferTask task, Cursor cursor);

    /**
     * 获取通知类型，包括上传和下载两种类型
     * 
     * @return
     */
    public abstract int getNotificationType();

    /**
     * @return
     */
    public abstract String createOrderBy();

    /**
     * 是支持通知栏显示
     * 
     * @since 7.9 libin09 2015-5-27
     * @return
     */
    public abstract boolean isSupportNotification();

    /**
     * 是否支持流量保护
     * 
     * @since 7.9 libin09 2015-5-27
     * @return
     */
    public abstract boolean isSupportWifiOnly();

    /**
     * 是否有视频传输能力
     *
     * @since 7.12 liulp 2015-11-4
     * @return
     */
    public abstract boolean transferVideoEnable();

}
