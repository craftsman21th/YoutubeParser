package com.moder.compass.log.storage.db;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * Created by liuliangping on 2016/3/18.
 */
public class LogProviderHelper {
    private static final String TAG = "LogProviderHelper";

    public LogProviderHelper() {

    }

    /**
     * 添加下载文件日志字段
     *
     * @param remoteUrl
     * @param localUrl
     * @param fid
     */
    public void addDownloadFileLog(@NonNull String remoteUrl, @NonNull String localUrl, @NonNull String fid) {
        final ContentValues contentValues = new ContentValues(4);
        contentValues.put(LogContract.DownloadFile.FILE_ID, fid);
        contentValues.put(LogContract.DownloadFile.REMOTE_URL, remoteUrl);
        contentValues.put(LogContract.DownloadFile.LOCAL_URL, localUrl);
        contentValues.put(LogContract.DownloadFile.IS_SMOOTH_VIDEO, LogContract.NO);
        BaseApplication.getInstance().getContentResolver().insert(LogContract.DownloadFile.BASE_CONTENT_URI,
                contentValues);
    }

    /**
     * 获取文件对应的fsId
     *
     * @param localPath
     * @return
     */
    public String getDownloadFid(@NonNull String localPath, boolean isSmoothVideo) {
        if (TextUtils.isEmpty(localPath)) {
            DuboxLog.d(TAG, "download file localPath is null");
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = BaseApplication.getInstance().getContentResolver().query(
                    LogContract.DownloadFile.BASE_CONTENT_URI, new String[] { LogContract.DownloadFile.FILE_ID },
                    LogContract.DownloadFile.LOCAL_URL + "=? COLLATE NOCASE AND "
                            + LogContract.DownloadFile.IS_SMOOTH_VIDEO + "=?",
                    new String[] { localPath, String.valueOf(isSmoothVideo ? LogContract.YES : LogContract.NO) }, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(LogContract.DownloadFile.FILE_ID));
            }
        } catch (Exception e) {
            DuboxLog.w(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * 删除文件对应的信息
     *
     * @param localPath
     * @param isSmoothVideo
     */
    public void deleteDownloadLog(@NonNull String localPath, boolean isSmoothVideo) {
        if (TextUtils.isEmpty(localPath)) {
            return;
        }

        int result =
                BaseApplication.getInstance().getContentResolver().delete(LogContract.DownloadFile.BASE_CONTENT_URI,
                        LogContract.DownloadFile.LOCAL_URL + "=? COLLATE NOCASE AND "
                                + LogContract.DownloadFile.IS_SMOOTH_VIDEO + "=?",
                        new String[] { localPath, String.valueOf(isSmoothVideo ? LogContract.YES : LogContract.NO) });
        DuboxLog.d(TAG, "deleteDownloadLog result:" + result);
    }

    public void updateLocalPath(@NonNull String localPath, @NonNull String oldPath, boolean isSmoothVideo) {
        if (TextUtils.isEmpty(localPath) || TextUtils.isEmpty(oldPath)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(LogContract.DownloadFile.LOCAL_URL, localPath);

        int result =
                BaseApplication.getInstance().getContentResolver()
                        .update(LogContract.DownloadFile.BASE_CONTENT_URI, values, LogContract.DownloadFile.LOCAL_URL
                                + "=? COLLATE NOCASE AND " + LogContract.DownloadFile.IS_SMOOTH_VIDEO + "=?",
                        new String[] { oldPath, String.valueOf(isSmoothVideo ? LogContract.YES : LogContract.NO) });
        DuboxLog.d(TAG, "updateLocalPath result:" + result);
    }

    public void updateTransmitterType(@NonNull String localPath, boolean isSmoothVideo) {
        if (TextUtils.isEmpty(localPath)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(LogContract.DownloadFile.IS_SMOOTH_VIDEO, isSmoothVideo ? LogContract.YES : LogContract.NO);

        int result =
                BaseApplication.getInstance().getContentResolver().update(LogContract.DownloadFile.BASE_CONTENT_URI,
                        values, LogContract.DownloadFile.LOCAL_URL + "=? COLLATE NOCASE ", new String[] { localPath });
        DuboxLog.d(TAG, "updateLocalPath result:" + result);
    }
}
