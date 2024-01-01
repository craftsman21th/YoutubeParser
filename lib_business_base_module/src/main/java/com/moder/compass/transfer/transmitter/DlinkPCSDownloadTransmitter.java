package com.moder.compass.transfer.transmitter;

import android.text.TextUtils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.transfer.transmitter.locate.LocateDownload;
import com.moder.compass.transfer.transmitter.locate.LocateDownloadUrls;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;

import java.util.List;

/**
 * Dlink 的PCS 下载传输器
 *
 * @author 孙奇 <br/>
 * create at 2013-6-26 下午04:27:47
 */
public class DlinkPCSDownloadTransmitter extends PCSDownloadTransmitter {

    private static final String TAG = "DlinkPCSDownloadTransmitter";

    /**
     * 下载用的dlink
     */
    protected String mDlink;

    /**
     * 处理Dlink过期的处理类
     */
    private final IDlinkExpireTimeProcessor mDlinkExpireTimeProcessor;

    public DlinkPCSDownloadTransmitter(int taskId, String dlink, RFile localFile, long size,
                                       IDlinkExpireTimeProcessor processor, TransmitterOptions options,
                                       String bduss, String uid) {
        super(taskId, localFile, size, options, bduss, uid);
        mDlink = dlink;
        mDlinkExpireTimeProcessor = processor;
        mFileInfo.fileName = localFile.name();
    }

    @Override
    public void remove(boolean isDeleteFile) {
        mDlinkExpireTimeProcessor.delDlinkRecord();
        super.remove(isDeleteFile);

    }

    @Override
    protected void processPCSLinkExpireTime() throws StopRequestException {
        mDlink = mDlinkExpireTimeProcessor.getDlink();
        super.processPCSLinkExpireTime();
    }

    @Override
    protected List<LocateDownloadUrls> initUrls() {
        DuboxLog.d(TAG, "initUrls");
        if (mLocateDownload == null) {
            mLocateDownload = new LocateDownload(mDlink, false, mBduss, mUid);
            mLocateDownload.initDlinkServerList();
        }

        List<LocateDownloadUrls> list = mLocateDownload.getDlinkUrlList();
        setSpeedThreshold(mLocateDownload);
        return list;
    }

    @Override
    protected List<LocateDownloadUrls> initProbationaryUrls() {
        DuboxLog.d(TAG, "initProbationaryUrls");
        if (mProbationaryLocateDownload == null) {
            mProbationaryLocateDownload = new LocateDownload(mDlink, false, mBduss, mUid);
            mProbationaryLocateDownload.initDlinkServerList();
        }

        List<LocateDownloadUrls> list;
        String token = null;
        String timeStamp = null;
        if (mOptions.getRateLimiter() != null) {
            token = mOptions.getRateLimiter().getSpeedToken();
            timeStamp = mOptions.getRateLimiter().getSpeedTimeStamp();
        }
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(timeStamp)) {
            list = mProbationaryLocateDownload.getDlinkSpeedUrlList(token, timeStamp);
            DuboxLog.d(TAG, "token:" + token + ",timeStamp:" + timeStamp);
        } else {
            list = mProbationaryLocateDownload.getDlinkUrlList();
        }

        // 需要在获取了locatedownload之后
        setSpeedThreshold(mProbationaryLocateDownload);
        return list;
    }

    // 若vip身份变化则设置原下载链接超时，令mLocateDownload重新请求下载链接
    @Override
    protected void onVipLevelChange() {
        mLocateDownload.setTimeExpire();
    }

    @Override
    protected void saveMinos(long threshold) {
    }
}
