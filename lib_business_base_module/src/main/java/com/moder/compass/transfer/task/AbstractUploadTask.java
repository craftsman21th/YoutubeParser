package com.moder.compass.transfer.task;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_UPDATE;

import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.statistics.StatisticsLog;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.p2p.OnP2PTaskListener;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;
import com.moder.compass.transfer.util.TransferUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

abstract class AbstractUploadTask extends TransferTask {
    protected Context mContext;

    AbstractUploadTask(Context context, Cursor cursor) {
        super(cursor);
        this.mType = TYPE_TASK_UPLOAD;
        mContext = context;
    }

    AbstractUploadTask(Context context, RFile localFile, String remotePath) {
        super(localFile, remotePath);
        this.mType = TYPE_TASK_UPLOAD;
        mContext = context;
        calculateFileSize();
    }

    @Override
    protected void performStart(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
                                OnP2PTaskListener onP2PTaskListener) {
        // 每个上传下载任务都调用统计，统计中会分辨是否要上报 YQH 20130228
        TransferUtil.sendUpTransferBroadcast();
        mState = TransferContract.Tasks.STATE_RUNNING;
        transmitter = getTransmitter(resolver, aP2PSDKCallbackProxy, onP2PTaskListener);
        transmitter.start();

        sendStateMessage(mState);
    }

    @Override
    protected void performPause() {
        mState = TransferContract.Tasks.STATE_PAUSE;
        if (transmitter != null) {
            transmitter.pause();
            transmitter = null;
        }

        sendStateMessage(mState);
    }

    private void calculateFileSize() {
        if (mLocalFileMeta.exists() && mLocalFileMeta.isFile()) { // 检查文件是否存在
            mSize = mLocalFileMeta.length();
        }
    }

    @Override
    protected void performRemove(boolean isDeleteFile) {
        if (TransferContract.Tasks.STATE_RUNNING == mState) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_USER_CANCEL);
        }

        if (transmitter != null) {
            transmitter.remove(isDeleteFile);
            transmitter = null;
        }

        sendStateMessage(TransferContract.Tasks.STATE_DELETED);
    }

    /**
     * 发送删除通知
     *
     * @param state 任务状态
     *
     * @since 7.9 2015-4-20 libin09
     */
    private void sendStateMessage(int state) {
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.LOCAL_URL, mLocalFileMeta.localUrl());
        data.putString(TransferContract.Tasks.REMOTE_URL, mRemoteUrl);
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_UPDATE, mTaskId, state, data);
    }
}
