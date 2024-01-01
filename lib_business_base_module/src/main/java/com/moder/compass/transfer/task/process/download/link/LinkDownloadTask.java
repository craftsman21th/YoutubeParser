package com.moder.compass.transfer.task.process.download.link;

import android.content.ContentResolver;
import android.database.Cursor;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.log.transfer.ITransferCalculable;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.Transmitter;
import com.moder.compass.transfer.transmitter.TransmitterOptions;
import com.moder.compass.transfer.transmitter.WebDownloadTransmitter;
import com.moder.compass.transfer.transmitter.p2p.OnP2PTaskListener;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;
import com.moder.compass.transfer.transmitter.ratelimiter.IRateLimitable;
import com.moder.compass.transfer.transmitter.wifisetting.SwitchWiFiDetectionBySettings;

import com.moder.compass.transfer.task.DownloadTask;
import com.moder.compass.transfer.transmitter.ratecaculator.impl.MultiThreadRateCalculator;

/**
 * Created by liuliangping on 2015/11/3.
 */
public class LinkDownloadTask extends DownloadTask {
    LinkDownloadTask(RFile localFile, String remotePath, long size, String serverMD5, String bduss,
                     String uid) {
        super(localFile, remotePath, size, serverMD5, bduss, uid);
    }

    public LinkDownloadTask(Cursor cursor, IRateLimitable rateLimiter, String bduss, String uid,
                            ITransferCalculable transferCalculable) {
        super(cursor, rateLimiter, bduss, uid, transferCalculable);
    }

    @Override
    protected Transmitter getTransmitter(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
                                         OnP2PTaskListener onP2PTaskListener) {
        TransmitterOptions options = new TransmitterOptions.Builder().setNetworkVerifier(false)
                .setWiFiDetectionSwitcher(new SwitchWiFiDetectionBySettings())
                .setStatusCallback(new LinkTaskSCImpl(resolver, mBduss, mTaskId))
                .setRateCalculator(new MultiThreadRateCalculator()).setRateLimiter(mRateLimiter)
                .setTransferCalculable(mTransferCalculable).build();

        return new WebDownloadTransmitter(mTaskId, mRemoteUrl, mLocalFileMeta, mSize, options, resolver,
                TransferContract.DownloadTasks.buildProcessingUri(mBduss));
    }
}
