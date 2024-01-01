package com.moder.compass.transfer.transmitter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.dubox.drive.base.network.NetworkUtil;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.transfer.TransferFileNameConstant;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.throwable.Retry;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网页资源下载传输器
 * 
 * @author 孙奇 <br/>
 *         create at 2013-7-3 下午03:43:41
 */
public class WebDownloadTransmitter extends SingleThreadSinglePartDownloadTransmitter {

    private static final String TAG = "WebDownloadTransmitter";
    private static final Object renameLock = new Object();

    private String mUrl;

    private ContentResolver mResolver;
    private Uri mUri;

    public WebDownloadTransmitter(int taskId, String url, RFile localFile, long size,
                                  TransmitterOptions options, ContentResolver resolver, Uri uri) {
        super(taskId, localFile, size, options);
        mResolver = resolver;
        mUri = uri;
        mUrl = url;
    }

    /**
     * 下载
     * 
     * @author 孙奇 V 1.0.0 Create at 2012-12-7 上午02:10:08
     * @throws StopRequestException
     * @throws Retry
     */
    @Override
    protected void download() throws StopRequestException, Retry, FileNotFoundException {
        BufferedInputStream downloadBuffer = null;
        HttpURLConnection conn = null;
        RandomAccessFile file = null;
        OutputStream os = null;
        try {
            checkConnectivity();
            file = setupDestinationFile();
            os = getOutputStream(mDestinationPath);
            if (mCompleteSize == mSize && mSize > 0) { // 如果offset 等于 size 建立连接range会报416错误
                DuboxLog.d(TAG, "already download success only need rename");
                return;
            }
            checkStorage();
            conn = buildConnection();
            handleExceptionalResponseCode(conn);
            handleExceptionalHeader(conn);
            updateSizeByConnection(conn);// 更新SIZE
            downloadBuffer = openResponseEntity(conn);
            // 数据接收过程中 ， 有信号无网络此处抛出IOException YQH 20121116
            transferData(file, os, downloadBuffer);
            DuboxLog.i(TAG, "transferData done");
        } finally {
            DuboxLog.i(TAG, "download get finally.");
            try {
                if (file != null) {
                    file.close();
                    file = null;
                }
                if (downloadBuffer != null) {
                    downloadBuffer.close();
                    downloadBuffer = null;
                }

                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                DuboxLog.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    @Override
    protected void rename() throws StopRequestException {
        synchronized (renameLock) {
            RFile realDestFile;
            if (Target30StorageKt.isPartitionStorage()) {
                realDestFile = mDestinationPath;
            } else {
                String realDestinationPath = getRealDestinationPath();
                realDestFile = PathKt.rFile(realDestinationPath);
            }
            if (Target30StorageKt.rename(tempFilePath, realDestFile, false)) {
                updateTaskLocalPath(mTaskId, realDestFile.localUrl());
                calculate(mSize - mOffsetSize, -1L);
            } else {
                SystemClock.sleep(1000L);
                if (Target30StorageKt.rename(tempFilePath, realDestFile, false)) {
                    updateTaskLocalPath(mTaskId, realDestFile.localUrl());
                    calculate(mSize - mOffsetSize, -1L);
                } else {
                    throw new StopRequestException(OtherErrorCode.LOCAL_RENAME_FAIL, "rename failed ");
                }
            }

        }

    }

    /**
     * 获取最终的目标文件路径
     * 
     * @return
     */
    private String getRealDestinationPath() {
        if (Target30StorageKt.isPartitionStorage()) {
            return null;
        }
        if (!mDestinationPath.exists()) {
            return mDestinationPath.localUrl();
        }
        String path = mDestinationPath.localUrl();
        String directoryPath = FileUtils.getFileDirectoryWithOutSlash(path);
        String fileName = FileUtils.getFileName(path);

        File file = new File(directoryPath,
                String.format(TransferFileNameConstant.BACKUP_FILE_NAME, fileName));
        if (!file.exists()) {
            return file.getAbsolutePath();
        }

        for (int i = 0;; i++) {
            file = new File(directoryPath, String.format(TransferFileNameConstant.BACKUP_INDEX_FILE_NAME, i,
                    fileName));
            if (!file.exists()) {
                return file.getAbsolutePath();
            }
        }
    }

    /**
     * 根据content_length更新SIZE
     * 
     * @param conn
     * @throws StopRequestException
     * @author 孙奇 V 1.0.0 Create at 2013-7-4 下午04:44:16
     * @throws Retry
     * @throws IOException
     */
    private void updateSizeByConnection(HttpURLConnection conn) throws StopRequestException, Retry {
        long size = (long) conn.getContentLength();
        int resultCode = 0;
        try {
            resultCode = conn.getResponseCode();
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException "  + e.getMessage());
        }
        DuboxLog.d(TAG, "updateSizeByConnection ::getContentLength " + size);
        // 判断服务器是否配置contentLength
        if (size == -1L) {
            return;
        }
        long newSize = 0L;
        if (resultCode == HttpURLConnection.HTTP_PARTIAL) {
            newSize = size + mCompleteSize;
        } else if (resultCode == HttpURLConnection.HTTP_OK) {
            newSize = size;
        }

        DuboxLog.d(TAG, "updateTaskInfoByConnection:: mSize = " + mSize + " newSize = " + newSize);
        if (mSize != newSize) {
            mSize = newSize;

            mCompleteSize = 0L;
            updateTaskSize(mTaskId, newSize);
        }
        checkStorage();
    }

    @Override
    protected void doRetry(Retry t) throws StopRequestException {
        if (t.mFinalStatus == TransmitterConstant.UPDATE_BY_HTTP) {
            return;
        }
        if (retryTimes < RETRY_MAX_TIMES) {
            try {
                Thread.sleep(RETRY_DELAY);// 重试间隔
            } catch (InterruptedException e) {
                DuboxLog.e(TAG, "retry InterruptedException ", e);
                throw new StopRequestException(OtherErrorCode.THREAD_INTERRUPTED, "InterruptedException", e);
            }
            retryTimes++;
        } else {
            if (mOptions.isNetworkVerifier()) {
                networkVerifierCheck();
            } else {
                throw new StopRequestException(OtherErrorCode.RETRY_OVER_TIME, "doRetry over time");
            }
        }
    }

    @Override
    protected String getUrlString() throws Retry, StopRequestException {
        return mUrl;
    }

    /**
     * 更新任务localPath
     * 
     * @param taskId
     * @param localPath
     * @return
     */
    private int updateTaskLocalPath(int taskId, String localPath) {
        DuboxLog.d(TAG, "updateTaskLocalPath localPath:" + localPath);

        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.LOCAL_URL, localPath);

        return mResolver.update(mUri, values, TransferContract.Tasks._ID + "=?", new String[] { String.valueOf(taskId) });
    }

    /**
     * 更新任务的大小
     * 
     * @param taskId
     * @param newSize
     * @return
     */
    private int updateTaskSize(int taskId, long newSize) {
        final ContentValues values = new ContentValues(2);
        values.put(TransferContract.Tasks.SIZE, newSize);
        values.put(TransferContract.Tasks.OFFSET_SIZE, 0L);

        return mResolver.update(mUri, values, TransferContract.Tasks._ID + "=?", new String[] { String.valueOf(taskId) });
    }

    @Override
    protected void handleExceptionalHeader(HttpURLConnection conn) throws Retry, StopRequestException {
        // 获取Content-Disposition字段的所有信息
        final String contentDisposition = conn.getHeaderField("Content-Disposition");
        String attachmentName = NetworkUtil.getFileName(contentDisposition);
        DuboxLog.d(TAG, "contentDisposition:" + contentDisposition + " ,attachmentName:" + attachmentName);
        if (TextUtils.isEmpty(attachmentName)) {
            final URL url = conn.getURL();
            String fileName = FileUtils.getFileName(Uri.decode(url.getPath()));
            updateLocalPathByConnection(fileName);
        } else {
            updateLocalPathByConnection(attachmentName);
        }
    }

    private void updateLocalPathByConnection(String fileName) throws Retry {
        if (Target30StorageKt.isPartitionStorage()) {
            return;
        }
        String oldFileName = mDestinationPath.name();
        DuboxLog.d(TAG, "updateTaskInfoByConnection:: fileName = " + fileName);
        if (!TextUtils.isEmpty(fileName) && !TextUtils.equals(oldFileName, fileName)) {
            // 删除之前oldFile
            if (tempFilePath != null && tempFilePath.exists()) {
                tempFilePath.delete(BaseApplication.getInstance());
            }
            String newDestinationPath =
                    FileUtils.getFileDirectory(mDestinationPath.localUrl()) + fileName;
            mDestinationPath = PathKt.rFile(newDestinationPath);
            String newTempPath =
                    mDestinationPath.localUrl() + TransferFileNameConstant.DOWNLOAD_SUFFIX;
            tempFilePath = PathKt.rFile(newTempPath);
            String localPath = FileUtils.getFileDirectory(mDestinationPath.localUrl())
                            + FileUtils.getFileName(tempFilePath.localUrl());
            updateTaskLocalPath(mTaskId, localPath);
            DuboxLog.d(TAG, "updateTaskInfoByConnection:: mDestinationPath = " + mDestinationPath);
            throw new Retry(TransmitterConstant.UPDATE_BY_HTTP, "updateLocalPathByConnection");
        }
    }

}
