package com.moder.compass.transfer.transmitter;

import android.content.ContentResolver;
import android.text.TextUtils;

import com.moder.compass.account.Account;
import com.dubox.drive.base.network.StokenManager;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;
import com.cocobox.library.CallbackInterface;
import com.cocobox.library.ErrorCode;
import com.cocobox.library.Key;
import com.cocobox.library.P2P;
import com.cocobox.library.P2PTaskRunningInfo;
import com.cocobox.library.TaskRunningInfo;
import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.log.transfer.DownloadLog;
import com.moder.compass.log.transfer.InstantDownloadLog;
import com.moder.compass.log.transfer.TransferLog;
import com.moder.compass.transfer.P2PManager;
import com.moder.compass.transfer.transmitter.throwable.Retry;

public class SDKDownloadTransmitter extends DownloadTransmitter implements CallbackInterface {

    private static final String TAG = "SDKDownloadTransmitter";

    protected final String mBduss;

    protected final String mUid;

    protected final ContentResolver mContentResolver;

    /**
     * @param taskId 任务ID标识
     * @param options 传输选项
     */
    public SDKDownloadTransmitter(int taskId, TransmitterOptions options, String bduss,
                                  String uid, ContentResolver resolver) {
        super(taskId, options);
        mBduss = bduss;
        mUid = uid;
        mContentResolver = resolver;
        mTransferLog = new DownloadLog(uid);
        mTransferLog.setCurrentUploadType(TransferLog.LogUploadType.FILE);
        mTransferLog.setLogTaskId(Account.INSTANCE.getUid() + "_" + System.currentTimeMillis());
        if (options.getTransferCalculable() != null) {
            mTransferLog.setTransferCalculable(options.getTransferCalculable());
        }
        mTransferLog.setIsSDKTransfer(true);
        mInstantDownloadLog = new InstantDownloadLog(uid);
        mInstantDownloadLog.setCurrentUploadType(TransferLog.LogUploadType.FILE);
        mInstantDownloadLog.setLogTaskId(mTransferLog.getLogTaskId());
        if (options.getTransferCalculable() != null) {
            mInstantDownloadLog.setTransferCalculable(options.getTransferCalculable());
        }
        P2PManager manager = new P2PManager();
        manager.initVipType();
        manager.initP2PNetType(BaseApplication.getInstance());
    }

    protected void addP2PSDKParameter() {
        String cookie = Constants.DUBOX_BDUSS_FIELD_NAME + "=" + mBduss;

        // 7.13.0 加入stoken和pan psc libin09 2016-4-28
        StokenManager stokenManager = new StokenManager(mBduss);
        cookie = stokenManager.addPanPsc(cookie);
        cookie = stokenManager.addPanNdutFmt(cookie);

        final String stoken = Account.stoken;
        if (!TextUtils.isEmpty(stoken)) {
            cookie += ";" + Constants.COOKIE_STOKEN + "=" + stoken;
        }

        // 增加NDID和GID标示
        P2P.getInstance().setParameter(Key.USER_ID, mUid);
        P2P.getInstance().setParameter(Key.USER_COOKIE, cookie);


    }

    @Override
    public void onP2PCreate(String s, ErrorCode errorCode) {

    }

    @Override
    public void onP2PStart(String s, ErrorCode errorCode) {

    }

    @Override
    public void onP2PStop(String s, ErrorCode errorCode) {

    }

    @Override
    public void onP2PPause(String s, ErrorCode errorCode) {

    }

    @Override
    public void onP2PDeleteTaskWithoutFiles(String s, ErrorCode errorCode) {

    }

    @Override
    public void onP2PDeleteTaskAndFiles(String s, ErrorCode errorCode) {

    }

    @Override
    public void onP2PGetTaskInfo(String s, P2PTaskRunningInfo p2PTaskRunningInfo, ErrorCode errorCode) {

    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onTaskCreate(String s, long l, ErrorCode errorCode) {

    }

    @Override
    public void onTaskStart(long l, ErrorCode errorCode) {

    }

    @Override
    public void onTaskStop(long l, ErrorCode errorCode) {

    }

    @Override
    public void onTaskPause(long l, ErrorCode errorCode) {

    }

    @Override
    public void onTaskDeleteWithoutFile(long l, ErrorCode errorCode) {

    }

    @Override
    public void onTaskDeleteAndFile(long l, ErrorCode errorCode) {

    }

    @Override
    public void onTaskGetTaskInfo(long l, TaskRunningInfo taskRunningInfo, ErrorCode errorCode) {

    }

    @Override
    public void onGetPlayM3u8Path(String s, String s1, ErrorCode errorCode) {

    }

    @Override
    public void onGetParameter(ErrorCode errorCode, Key key, String s) {

    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    protected void prepareTransmit() {

    }

    @Override
    protected void transmit(TransmitBlock transmitBean) {

    }

    @Override
    public void remove(boolean isDeleteFile) {

    }

    @Override
    protected void doRetry(Retry t) throws StopRequestException {
        DuboxLog.d(TAG, "doRetry");
    }
}
