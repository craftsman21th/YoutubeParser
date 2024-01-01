package com.moder.compass.transfer.task;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.log.transfer.ITransferCalculable;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.MultiUploadTransmitter;
import com.moder.compass.transfer.transmitter.Transmitter;
import com.moder.compass.transfer.transmitter.TransmitterOptions;
import com.moder.compass.transfer.transmitter.block.ConfigBlockUpload;
import com.moder.compass.transfer.transmitter.p2p.OnP2PTaskListener;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;
import com.moder.compass.transfer.transmitter.ratecaculator.impl.MultiThreadRateCalculator;
import com.moder.compass.transfer.transmitter.ratecaculator.impl.SyncRateCalculator;
import com.moder.compass.transfer.transmitter.statuscallback.impl.UploadTaskSCImpl;
import com.moder.compass.transfer.transmitter.wifisetting.SwitchWiFiDetectionBySettings;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

/**
 * 上传任务
 *
 * @author sunqi01
 */
public class UploadTask extends AbstractUploadTask {
    protected final String mBduss;
    protected final String mUid;
    public int mConflictStrategy = TransferContract.UploadTasks.UPLOAD_RENAME;
    public int mQuality;
    public String mUploadId;
    protected ITransferCalculable mTransferCalculable;

    public UploadTask(Context context, RFile localFile, String remotePath, String bduss, String uid,
                      int conflictStrategy, int quality) {
        super(context, localFile, remotePath);
        mBduss = bduss;
        mUid = uid;
        mConflictStrategy = conflictStrategy;
        mQuality = quality;
        mTransferCalculable = null;

    }

    public UploadTask(Context context, Cursor cursor, String bduss, String uid,
                      ITransferCalculable transferCalculable) {
        super(context, cursor);
        mBduss = bduss;
        mUid = uid;
        final int columnIndex = cursor.getColumnIndex(TransferContract.UploadTasks.NEED_OVERRIDE);
        if (columnIndex >= 0) {
            mConflictStrategy = cursor.getInt(columnIndex);
        }

        final int errorCodeIndex = cursor.getColumnIndex(TransferContract.Tasks.EXTRA_INFO_NUM);
        if (errorCodeIndex >= 0) {
            extraInfoNum = cursor.getInt(errorCodeIndex);
        }

        final int compressIndex = cursor.getColumnIndex(TransferContract.UploadTasks.QUALITY);
        if (compressIndex >= 0) {
            mQuality = cursor.getInt(compressIndex);
        }

        final int uploadIdIndex = cursor.getColumnIndex(TransferContract.UploadTasks.UPLOAD_ID);
        if (uploadIdIndex >= 0) {
            mUploadId = cursor.getString(uploadIdIndex);
        }

        mTransferCalculable = transferCalculable;

    }

    /**
     * @param resolver
     *
     * @return
     */
    @Override
    protected Transmitter getTransmitter(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
                                         OnP2PTaskListener onP2PTaskListener) {
        // 是否开启分片并发上传
        boolean isEnableBlockUpload = ConfigBlockUpload.INSTANCE.enable();

        final TransmitterOptions options = new TransmitterOptions.Builder().setNetworkVerifier(true)
            .setWiFiDetectionSwitcher(new SwitchWiFiDetectionBySettings())
            .setRateCalculator(isEnableBlockUpload ? new MultiThreadRateCalculator() : new SyncRateCalculator())
            .setStatusCallback(new UploadTaskSCImpl(resolver, mBduss, mTaskId))

            .setTransferCalculable(mTransferCalculable).build();

        MultiUploadTransmitter multiUploadTransmitter =
            new MultiUploadTransmitter(mTaskId, mLocalFileMeta, mRemoteUrl, mFileName, options, resolver,
                TransferContract.UploadTasks.buildUri(mBduss), mBduss, mUid, MultiUploadTransmitter.Source.UPLOAD,
                mUploadId);
        multiUploadTransmitter.setWillBeOverride(mConflictStrategy == TransferContract.UploadTasks.UPLOAD_OVERRIDE);
        transmitter = multiUploadTransmitter;

        return transmitter;
    }
}