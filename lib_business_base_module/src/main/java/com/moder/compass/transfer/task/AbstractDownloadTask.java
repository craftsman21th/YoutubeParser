package com.moder.compass.transfer.task;

import android.content.ContentResolver;
import android.database.Cursor;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.statistics.StatisticsLog;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.p2p.OnP2PTaskListener;
import com.moder.compass.transfer.util.TransferUtil;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;

abstract class AbstractDownloadTask extends TransferTask {

    AbstractDownloadTask(RFile localFile, String remotePath, long size) {
        super(localFile, remotePath);
        mType = TYPE_TASK_DOWNLOAD;
        mState = TransferContract.Tasks.STATE_PENDING;
        mTransmitterType = TransferContract.DownloadTasks.TRANSMITTER_TYPE_PCS;
        mSize = size;
    }

    AbstractDownloadTask(Cursor cursor) {
        super(cursor);
    }

    @Override
    protected void performStart(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
                                OnP2PTaskListener onP2PTaskListener) {
        TransferUtil.sendDownloadTransferBroadcast();
        mState = TransferContract.Tasks.STATE_RUNNING;
        transmitter = getTransmitter(resolver, aP2PSDKCallbackProxy, onP2PTaskListener);
        transmitter.start();
    }

    @Override
    protected void performPause() {
        mState = TransferContract.Tasks.STATE_PAUSE;
        if (transmitter != null) {
            transmitter.pause();
            transmitter = null;
        }
    }

    @Override
    protected void performRemove(boolean isDeleteFile) {
        if (TransferContract.Tasks.STATE_RUNNING == mState) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_USER_CANCEL);
        }

        if (transmitter != null) {
            transmitter.remove(isDeleteFile);
            transmitter = null;
        }
    }
}