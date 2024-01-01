package com.moder.compass.transfer.task;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_DOWNLOAD_UPDATE;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_PREVIEW_UPDATE;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;

import com.moder.compass.account.Account;
import com.dubox.drive.base.network.StokenManager;
import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.log.transfer.ITransferCalculable;
import com.moder.compass.transfer.P2PManager;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.PCSDownloadTransmitter;
import com.moder.compass.transfer.transmitter.PCSPreviewDownloadTransmitter;
import com.moder.compass.transfer.transmitter.Transmitter;
import com.moder.compass.transfer.transmitter.TransmitterOptions;
import com.moder.compass.transfer.transmitter.WebDownloadTransmitter;
import com.moder.compass.transfer.transmitter.p2p.OnP2PTaskListener;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;
import com.moder.compass.transfer.transmitter.p2p.SDKTransmitterTask;
import com.moder.compass.transfer.transmitter.p2p.UniversalDownloadTransmitter;
import com.moder.compass.transfer.transmitter.ratecaculator.impl.MultiThreadRateCalculator;
import com.moder.compass.transfer.transmitter.ratelimiter.IRateLimitable;
import com.moder.compass.transfer.transmitter.statuscallback.impl.DownloadTaskSCImpl;
import com.moder.compass.transfer.transmitter.statuscallback.impl.PreviewTaskSCImpl;
import com.moder.compass.transfer.transmitter.wifisetting.DisableWiFiDetection;
import com.moder.compass.transfer.transmitter.wifisetting.SwitchWiFiDetectionBySettings;
import com.cocobox.library.CallbackInterface;

public class DownloadTask extends AbstractDownloadTask {
    private static final String TAG = "DownloadTask";
    protected final String mBduss;
    protected final String mUid;
    protected boolean isPreviewMode = false;
    public final String serverMD5;
    protected final IRateLimitable mRateLimiter;
    protected final ITransferCalculable mTransferCalculable;
    private final P2PManager mP2PManager;


    public DownloadTask(RFile localFile, String remotePath, long size, String serverMD5, String bduss, String uid) {
        super(localFile, remotePath, size);
        mBduss = bduss;
        mUid = uid;
        this.serverMD5 = serverMD5;
        mRateLimiter = null;
        mTransferCalculable = null;
        mP2PManager = new P2PManager();
    }

    public DownloadTask(Cursor cursor, IRateLimitable rateLimiter, String bduss, String uid,
            ITransferCalculable transferCalculable) {
        super(cursor);
        final int columnIndex = cursor.getColumnIndex(CloudFileContract.Files.FILE_SERVER_MD5);
        if (columnIndex >= 0) {
            serverMD5 = cursor.getString(columnIndex);
        } else {
            serverMD5 = null;
        }

        mBduss = bduss;
        mUid = uid;
        mRateLimiter = rateLimiter;
        mTransferCalculable = transferCalculable;
        mP2PManager = new P2PManager();

    }

    @Override
    protected Transmitter getTransmitter(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
                                         OnP2PTaskListener onP2PTaskListener) {
        TransmitterOptions options;
        if (isPreviewMode) {
            options = new TransmitterOptions.Builder().setNetworkVerifier(false)
                    .setWiFiDetectionSwitcher(new DisableWiFiDetection())
                    .setStatusCallback(new PreviewTaskSCImpl(resolver, mBduss, mTaskId))
                    .setRateCalculator(new MultiThreadRateCalculator()).setTransferCalculable(mTransferCalculable)

                    .build();
        } else {
            options = new TransmitterOptions.Builder().setNetworkVerifier(true)
                    .setWiFiDetectionSwitcher(new SwitchWiFiDetectionBySettings())
                    .setStatusCallback(new DownloadTaskSCImpl(resolver, mBduss, mTaskId))
                    .setRateCalculator(new MultiThreadRateCalculator()).setRateLimiter(mRateLimiter)

                    .setTransferCalculable(mTransferCalculable).build();
        }

        // web页下载
        if (TransferContract.DownloadTasks.TRANSMITTER_TYPE_WEB.equals(mTransmitterType)) {
            DuboxLog.d(TAG, "DownloadTask WebDownloadTransmitter");
            transmitter = new WebDownloadTransmitter(mTaskId, mRemoteUrl, mLocalFileMeta, mSize, options, resolver,
                    TransferContract.DownloadTasks.buildProcessingUri(mBduss));
        // PCS文件下载
        } else if (TransferContract.DownloadTasks.TRANSMITTER_TYPE_PCS.equals(mTransmitterType)) {
            if (!isPreviewMode && canUseSdkDownloadForNormal()) {
                DuboxLog.d(TAG, "DownloadTask  UniversalDownloadTransmitter");
                SDKTransmitterTask sdkTransmitterTask = new SDKTransmitterTask(mLocalFileMeta, mRemoteUrl,
                        mSize, serverMD5, null, false);
                transmitter = new UniversalDownloadTransmitter(resolver, mBduss, mUid, mTaskId, options,
                        sdkTransmitterTask, onP2PTaskListener, null);

                if (aP2PSDKCallbackProxy != null) {
                    aP2PSDKCallbackProxy.add(String.valueOf(mTaskId), (CallbackInterface) transmitter);
                }
            } else {
                if (isPreviewMode) {
                    transmitter = new PCSPreviewDownloadTransmitter(mTaskId, mRemoteUrl, mLocalFileMeta, mSize,
                            options, serverMD5, mBduss, mUid);
                } else {
                    // 识别p2p下载任务
                    transmitter = new PCSDownloadTransmitter(mTaskId, mRemoteUrl, mLocalFileMeta, mSize, options,
                            serverMD5, mBduss, mUid);
                }
            }
        // 转码下载
        }  else {
            throw new IllegalArgumentException("unknown transmitter type:" + mTransmitterType);
        }

        return transmitter;
    }

    /**
     * 是否是预览产生的DownLoadTask
     *
     * @param isPreviewMode 是否为预览模式
     * @author 孙奇 V 1.0.0 Create at 2012-10-23 下午05:21:27
     */
    public void setIsPreview(boolean isPreviewMode) {
        this.isPreviewMode = isPreviewMode;
    }

    @Override
    protected void performRemove(boolean isDeleteFile) {
        super.performRemove(isDeleteFile);
        // 发送通知给第三方应用
        sendStateMessage(TransferContract.Tasks.STATE_DELETED);
    }

    @Override
    protected void performStart(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
            OnP2PTaskListener onP2PTaskListener) {
        super.performStart(resolver, aP2PSDKCallbackProxy, onP2PTaskListener);
        // 发送通知给第三方应用
        sendStateMessage(mState);
    }

    @Override
    protected void performPause() {
        super.performPause();
        // 发送通知给第三方应用
        sendStateMessage(mState);
    }

    /**
     * 发送通知给第三方应用
     *
     * @param state 任务状态
     * @since 7.9 2015-4-20 libin09
     */
    private void sendStateMessage(int state) {
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.REMOTE_URL, mRemoteUrl);
        data.putString(TransferContract.Tasks.LOCAL_URL, getLocalUrl());
        final int what = isPreviewMode ? MESSAGE_PREVIEW_UPDATE : MESSAGE_DOWNLOAD_UPDATE;
        EventCenterHandler.INSTANCE.sendMsg(what, mTaskId, state, data);
    }

    /**
     * 获取stoken
     *
     * @return
     */
    private boolean checkStoken() {
        if (!TextUtils.isEmpty(Account.stoken)) {
            return true;
        }

        return !TextUtils.isEmpty(new StokenManager(mBduss, false).addSToken(""));
    }

    /**
     * 是否可以用sdk进行下载
     */
    protected boolean canUseSdkDownloadForNormal() {
        // 检查云端开关是否可以使用SDK进行下载
        if (!mP2PManager.isNormalIntoSDKConfigEnable() || !mP2PManager.isP2PConfigEnable()) {
            DuboxLog.d(TAG, "can not UseSdkDownloadForNormal");
            return false;
        }

        if (mIsP2PFailed) {
            DuboxLog.d(TAG, "p2p is mIsP2PFailed");
            // 之前的p2p任务失败，改为普通下载
            return false;
        }

        if (!isInfoCompleteForSDk()) {
            return false;
        }

        // 新任务全部使用sdk进行下载；同时避免重新开始下载，老版本添加的非p2p任务，继续走旧的下载逻辑
        if (mOffset > 0 && !mIsDownloadSDKTask) {
            return false;
        }
        return true;
    }

    /**
     * SDK以及相关数据是否准备好
     */
    private boolean isInfoCompleteForSDk() {
        if (!mP2PManager.isInitOk()) {
            return false;
        }

        // 没有stoken时，无法使用p2p sdk.7.13.0 2016-4-22 libin09
        if (!checkStoken()) {
            DuboxLog.d(TAG, "stoken is null");
            return false;
        }
        return true;
    }
}