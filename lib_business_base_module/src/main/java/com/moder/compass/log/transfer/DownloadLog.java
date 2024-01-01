package com.moder.compass.log.transfer;

import com.moder.compass.log.storage.db.LogProviderHelper;

/**
 * Created by liuliangping on 2016/3/23.
 */
public class DownloadLog extends TransferLog {
    protected final LogProviderHelper mLogProviderHelper;

    public DownloadLog(String uid) {
        super(uid);
        mLogProviderHelper = new LogProviderHelper();
    }

    @Override
    public String getLogUploadType() {
        return TransferFieldKey.FileTypeKey.TYPE;
    }

    @Override
    public String getOpValue() {
        return TransferFieldKey.DOWNLOAD_OP_VALUE;
    }

    @Override
    public String getFileFid() {
        return mLogProviderHelper.getDownloadFid(mLocalPath, false);
    }

    @Override
    public String getTransferByteKey() {
        return TransferFieldKey.DOWNLOAD_RECV_BYTES;
    }

    @Override
    public String getTransferTimeKey() {
        return TransferFieldKey.DOWNLOAD_RECV_TIMES;
    }

    @Override
    public void clear() {
        mLogProviderHelper.deleteDownloadLog(mLocalPath, false);
    }
}
