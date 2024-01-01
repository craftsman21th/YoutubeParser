
package com.moder.compass.transfer.transmitter;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.locate.LocateDownload;
import com.moder.compass.BaseApplication;
import com.moder.compass.transfer.transmitter.locate.LocateDownloadUrls;

import java.util.List;

/**
 * Created by liuliangping on 2015/6/14.
 * PCS预览传输器
 */
public class PCSPreviewDownloadTransmitter extends PCSDownloadTransmitter {
    private static final String TAG = "PCSPreviewDownloadTransmitter";

    public PCSPreviewDownloadTransmitter(int taskId, String serverPath, RFile localFile, long size,
                                         TransmitterOptions options, String serverMD5, String bduss, String uid) {
        super(taskId, serverPath, localFile, size, options, serverMD5, bduss, uid);
    }

    @Override
    protected boolean isDownloadPrivateDir() {
        return true;
    }

    @Override
    protected List<LocateDownloadUrls> initUrls() {
        if (mLocateDownload == null) {
            mLocateDownload = new LocateDownload(mServerPath, true, mBduss, mUid);
            mLocateDownload.initPcsServerList();
        }

        List<LocateDownloadUrls> list = mLocateDownload.getPcsUrlList();

        long threshold = 0L;
        // 需要在获取了locatedownload之后
        if (mOptions.getRateLimiter() != null) {
            threshold = mLocateDownload.getDownloadLimitThreshold();
            mTransferLog.setSpeedLimit(threshold);
            mOptions.getRateLimiter().updateThreshold(threshold);
        }

        return list;
    }

    /**
     * 预览检查下载目录
     *
     * @author 2015-06-01 liulp
     */
    @Override
    protected Pair<String, Long> getDownloadedFileInfo() {
        if (TextUtils.isEmpty(mServerPath)) {
            return null;
        }

        Uri uri = TransferContract.DownloadTaskFiles.buildUri(mBduss);
        String[] projection =
                new String[] { TransferContract.DownloadTaskFiles.LOCAL_PATH,
                        TransferContract.DownloadTaskFiles.LOCAL_LAST_MODIFY_TIME };
        String select = TransferContract.DownloadTaskFiles.SERVER_PATH;

        Cursor cursor =
                BaseApplication.getInstance().getContentResolver()
                        .query(uri, projection, select + "=?", new String[] { mServerPath }, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(0);
                long lastTime = cursor.getLong(1);
                return Pair.create(path, lastTime);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "getCheckFilePath exception:", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 查选下载完成的文件md5，预览检查下载目录
     *
     * @author 2015-06-01 liulp
     */
    @Override
    protected String getDownloadedFileMd5() {
        if (TextUtils.isEmpty(mServerPath)) {
            return null;
        }

        Uri uri = TransferContract.DownloadTasks.buildUri(mBduss);
        String[] projection = new String[] { CloudFileContract.Files.FILE_SERVER_MD5 };
        String select =
                TransferContract.DownloadTasks.REMOTE_URL + "=? AND " + TransferContract.DownloadTasks.STATE + "=?";

        Cursor cursor =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(uri, projection, select,
                                new String[] { mServerPath, String.valueOf(TransferContract.Tasks.STATE_FINISHED) },
                                null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "getCheckFilePath exception:", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
