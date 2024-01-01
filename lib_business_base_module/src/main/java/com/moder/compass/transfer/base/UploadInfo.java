package com.moder.compass.transfer.base;

import com.dubox.drive.kernel.util.RFile;

/**
 * Created by liuliangping on 2015/10/22.
 */
public class UploadInfo {
    private final int mQuality;
    private final int mConflictStrategy;
    private final RFile mLocalFile;
    private final String mRemoteUrl;

    public UploadInfo(RFile localFile, String remoteUrl, int compressQuality, int conflictStrategy) {
        mLocalFile = localFile;
        mRemoteUrl = remoteUrl;
        mQuality = compressQuality;
        mConflictStrategy = conflictStrategy;
    }

    public RFile getLocalFile() {
        return mLocalFile;
    }

    public String getRemotePath() {
        return mRemoteUrl;
    }

    public int getQuality() {
        return mQuality;
    }

    public int getConflictStrategy() {
        return mConflictStrategy;
    }
}
