
package com.moder.compass.transfer.base.link;

import com.dubox.drive.cloudfile.base.IDownloadable;
import com.dubox.drive.cloudfile.io.model.CloudFile;

/**
 * Created by liuliangping on 2015/6/30.
 */
public class LinkDownload implements IDownloadable {
    private String mDownloadUrl;
    private String mFileName;
    private long mFileSize;

    public LinkDownload(String url, String fileName, long size) {
        mDownloadUrl = url;
        mFileName = fileName;
        mFileSize = size;
    }

    @Override
    public long getFileId() {
        return 0L;
    }

    @Override
    public long getSize() {
        return mFileSize;
    }

    @Override
    public long getDuration() {
        return 0L;
    }

    @Override
    public int getDownloadType() {
        return 0;
    }

    @Override
    public String getFileName() {
        return mFileName;
    }

    @Override
    public String getFileDlink() {
        return mDownloadUrl;
    }

    @Override
    public String getFilePath() {
        return null;
    }

    @Override
    public CloudFile getParent() {
        return null;
    }

    @Override
    public String getServerMD5() {
        return null;
    }

    @Override
    public boolean isSaved() {
        // 本产品线不需要
        return false;
    }
}
