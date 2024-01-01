
package com.moder.compass.transfer.transmitter;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.transfer.transmitter.locate.LocateDownload;
import com.moder.compass.transfer.transmitter.locate.LocateDownloadUrls;

import java.util.List;

/**
 * Created by liuliangping on 2015/6/14.
 * 外链预览下载传输器
 */
public class DlinkPCSPreviewDownloadTransmitter extends DlinkPCSDownloadTransmitter {

    public DlinkPCSPreviewDownloadTransmitter(int taskId, String dlink, RFile localFile, long size,
                                              IDlinkExpireTimeProcessor processor, TransmitterOptions options,
                                              String bduss, String uid) {
        super(taskId, dlink, localFile, size, processor, options, bduss, uid);
        mFileInfo.fileName = localFile.name();
    }

    @Override
    protected boolean isDownloadPrivateDir() {
        return true;
    }

    @Override
    protected List<LocateDownloadUrls> initUrls() {
        if (mLocateDownload == null) {
            mLocateDownload = new LocateDownload(mDlink, true, mBduss, mUid);
            mLocateDownload.initDlinkServerList();
        }

        List<LocateDownloadUrls> list = mLocateDownload.getDlinkUrlList();
        if (mOptions.getRateLimiter() != null) {
            mTransferLog.setSpeedLimit(mLocateDownload.getDownloadLimitThreshold());
            mOptions.getRateLimiter().updateThreshold(mLocateDownload.getDownloadLimitThreshold());
        }

        return list;
    }
}
