package com.moder.compass.transfer.transmitter;

import android.content.ContentResolver;
import android.net.Uri;

import com.moder.compass.account.constant.AccountErrorCode;
import com.dubox.drive.cloudfile.constant.DuboxErrorCode;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;
import com.moder.compass.transfer.transmitter.statuscallback.ITransferStatusCallback;

/**
 * 多线程上传传输器
 *
 * @author sunqi01
 */
public class MultiUploadTransmitter extends UploadTransmitter
        implements AccountErrorCode, DuboxErrorCode, PCSTransmitErrorCode {
    private static final String TAG = "MultiUploadTransmitter";

    public MultiUploadTransmitter(int taskId, RFile localFile, String remotePath, String fileName,
                                  TransmitterOptions options, ContentResolver resolver,
                                  Uri uri, String bduss, String uid, int mode,
                                  String uploadId) {
        super(taskId, localFile, remotePath, fileName, options, resolver, uri, bduss, uid, mode, uploadId);
    }

    @Override
    public void start() {
        if (mOptions.getStatusCallback() != null
                && mOptions.getStatusCallback() instanceof ITransferStatusCallback) {
            ((ITransferStatusCallback) mOptions.getStatusCallback()).onStart();
        }
        TaskSchedulerImpl.INSTANCE.addHighTask(new BaseJob("MultiUploadTransmitterRunnable") {
            @Override
            protected void performExecute()  {
                retryTimes = 0;
                signalNetworkProcessRetryTimes = 0;
                prepareTransmit();
                DuboxLog.d(TAG, "开始传输。。。");

                transmit(null);
            }
        });
    }

    @Override
    protected boolean needCompress() {
        return false;
    }

}
