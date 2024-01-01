
package com.moder.compass.transfer.base.link;

import android.database.Cursor;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.base.ITaskGenerator;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.TransferTask;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.task.DownloadTask;

/**
 * Created by liuliangping on 2015/6/30.
 */
public class LinkTaskGenerator implements ITaskGenerator {
    private static final String TAG = "LinkTaskGenerator";
    private final String mUrl;
    private final long mFileSize;

    public LinkTaskGenerator(String url, long fileSize) {
        mUrl = url;
        mFileSize = fileSize;
    }

    @Override
    public TransferTask generate(IDownloadable downloadable, String bduss, String uid) {
        if (downloadable == null) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getCursor(bduss);
            if (cursor != null && cursor.moveToFirst()) {
                return new DownloadTask(cursor, null, bduss, uid, null);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "generator exception:" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public Cursor getCursor(String bduss) {
        if (TextUtils.isEmpty(mUrl) || mFileSize <= 0L) {
            return null;
        }

        String[] remoteUrls = new String[]{mUrl};
        return BaseApplication
                .getInstance()
                .getContentResolver()
                .query(TransferContract.DownloadTasks.buildUri(bduss),
                        TransferContract.DownloadTasks.Query.PROJECTION,
                        TransferContract.Tasks.REMOTE_URL + " IN ('" + TextUtils.join("','", remoteUrls) + "')"
                                + " AND " + TransferContract.Tasks.SIZE + "=" + mFileSize, null, null);
    }
}
