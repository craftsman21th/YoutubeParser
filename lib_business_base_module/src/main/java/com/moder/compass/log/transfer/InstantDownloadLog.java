package com.moder.compass.log.transfer;

/**
 * Created by liuliangping on 2016/7/27.
 */
public class InstantDownloadLog extends DownloadLog {
    private static final String TAG = "InstantDownloadLog";

    public InstantDownloadLog(String uid) {
        this(uid, TransferFieldKey.FileTypeKey.DownloadType.Normal);
    }

    public InstantDownloadLog(String uid, TransferFieldKey.FileTypeKey.DownloadType type) {
        super(uid);
        mDownloadType = type;
    }

}
