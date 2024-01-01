package com.moder.compass.transfer.transmitter.p2p;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.Target30StorageKt;
import com.cocobox.library.P2P;

public class SDKTransmitterTask {

    /**
     * 下载文件目的地址
     */
    private final RFile localFile;
    /**
     * 文件在PCS上路径或者是dlink
     */
    private final String mServerPath;
    private long mSize; // 由于流畅下载，在开始前不知道大小，所以可以修改大小值，不用fianl

    private final String mServerMD5;
    private final String mDuboxPath;

    private final boolean mIsShare;

    /**
     * 下载的临时文件
     */
    private final RFile tempFile;

    /**
     * 整合下载SDK的传输任务
     * @param remoteUrl  文件在PCS上路径或者是dlink
     * @param size 文件大小
     * @param serverMD5 文件md5
     * @param duboxPath 网盘路径
     * @param isShare 是否为分享文件
     */
    public SDKTransmitterTask(RFile localFile, String remoteUrl, long size, String serverMD5,
                              String duboxPath, boolean isShare) {
        mIsShare = isShare;
        this.localFile = localFile;
        mServerPath = remoteUrl;
        mSize = size;
        mServerMD5 = serverMD5;
        mDuboxPath = duboxPath;

        if (Target30StorageKt.isPartitionStorage()) {
            tempFile = localFile;
        } else {
//            tempFile = PathKt.rFile(localFile.localUrl() + TransferFileNameConstant.DOWNLOAD_SUFFIX);
            // sdk下载使用原来后缀名
            tempFile = PathKt.rFile(localFile.localUrl() + P2P.getInstance().getTempFileAppendix());
        }

        DuboxLog.d("SDKTransmitterTask", "mDestinationPath:" + localFile + ", mServerPath:"
                + mServerPath + ", mPath:" + mDuboxPath);
    }

    public boolean isTs() {
        return false;
    }


    public RFile getTempFile() {
        return tempFile;
    }

    public RFile getLocalFile() {
        return localFile;
    }

    public String getServerPath() {
        return mServerPath;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public String getServerMD5() {
        return mServerMD5;
    }

    public String getDuboxPath() {
        return mDuboxPath;
    }

    public boolean isShare() {
        return mIsShare;
    }
}
