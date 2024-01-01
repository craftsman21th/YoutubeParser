package com.moder.compass.transfer.transmitter;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.moder.compass.BaseApplication;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.dubox.drive.db.preview.contract.PreviewContract;

/**
 * 用于下载（预览）检查预览（下载）目录是否有相同的文件的帮助类
 *
 * Created by liuliangping on 2015/12/10.
 */
public class TransferDownloadCheckHelper {
    private static final String TAG = "TransferDownloadCheckHelper";
    private final String mBduss;

    public TransferDownloadCheckHelper(String bduss) {
        mBduss = bduss;
    }

    /**
     * 下载检查预览目录
     *
     * @author 2015-06-01 liulp
     */
    public Pair<String, Long> getPreviewDownloadedFileInfo(String serverPath) {
        if (TextUtils.isEmpty(serverPath)) {
            return null;
        }

        Uri uri = PreviewContract.TaskFiles.buildUri(mBduss);
        String[] projection =
                new String[] { PreviewContract.TaskFiles.LOCAL_PATH, PreviewContract.TaskFiles.LOCAL_LAST_MODIFY_TIME };
        String select = PreviewContract.TaskFiles.SERVER_PATH;

        Cursor cursor =
                BaseApplication.getInstance().getContentResolver()
                        .query(uri, projection, select + "=?", new String[] { serverPath }, null);
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
     * 查选下载完成的文件md5，下载检查预览目录
     *
     * @author 2015-06-01 liulp
     */
    public String getPreviewDownloadedFileMd5(String serverPath) {
        if (TextUtils.isEmpty(serverPath)) {
            return null;
        }

        Uri uri = PreviewContract.Tasks.buildUri(mBduss);
        String[] projection = new String[] { CloudFileContract.Files.FILE_SERVER_MD5 };
        String select = PreviewContract.Tasks.REMOTE_URL + "=? AND " + PreviewContract.Tasks.STATE + "=?";

        Cursor cursor =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(uri, projection, select,
                                new String[] { serverPath, String.valueOf(TransferContract.Tasks.STATE_FINISHED) },
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

    /**
     * 查询文件库cachefilelist里面文件的md5
     *
     * @author 2015-06-01 liulp
     */
    public String getCloudFileMd5(String serverPath) {
        if (TextUtils.isEmpty(serverPath)) {
            return null;
        }

        final Cursor cursor =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(CloudFileContract.Files.buildFileServerPathUri(serverPath, mBduss),
                                new String[] { CloudFileContract.Files.FILE_SERVER_MD5 }, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(CloudFileContract.Files.FILE_SERVER_MD5));
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 预览检查下载目录
     *
     * @author 2015-06-01 liulp
     */
    public Pair<String, Long> getDownloadedFileInfo(String serverPath) {
        if (TextUtils.isEmpty(serverPath)) {
            return null;
        }

        Uri uri = TransferContract.DownloadTaskFiles.buildUri(mBduss);
        String[] projection =
                new String[] { TransferContract.DownloadTaskFiles.LOCAL_PATH,
                    TransferContract.DownloadTaskFiles.LOCAL_LAST_MODIFY_TIME };
        String select = TransferContract.DownloadTaskFiles.SERVER_PATH;

        Cursor cursor =
                BaseApplication.getInstance().getContentResolver()
                        .query(uri, projection, select + "=?", new String[] { serverPath }, null);
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
    public String getDownloadedFileMd5(String serverPath) {
        if (TextUtils.isEmpty(serverPath)) {
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
                                new String[] { serverPath, String.valueOf(TransferContract.Tasks.STATE_FINISHED) },
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
