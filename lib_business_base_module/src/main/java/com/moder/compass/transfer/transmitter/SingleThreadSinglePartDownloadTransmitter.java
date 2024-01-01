package com.moder.compass.transfer.transmitter;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.kernel.architecture.net.HttpRequest;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.statistics.EventStatisticsKt;
import com.moder.compass.statistics.StatisticsKeysKt;
import com.moder.compass.transfer.TransferFileNameConstant;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.throwable.Retry;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

/**
 * 下载传输器
 * 
 * @author 孙奇 <br/>
 *         create at 2012-12-7 上午02:14:09
 */
public abstract class SingleThreadSinglePartDownloadTransmitter extends DownloadTransmitter {
    private static final String TAG = "SingleThreadSinglePartDownloadTransmitter";

    private static final int DOWNLOAD_BYTES_SIZE = 1024 * 16;

    protected Context context;
    protected RFile mDestinationPath;
    protected long mSize;
    protected long mCompleteSize;
    protected RFile localFile;

    /**
     * 临时文件路径
     * 
     * @author 孙奇 V 1.0.0 Create at 2012-12-7 上午12:26:11
     */
    protected RFile tempFilePath;

    protected SingleThreadSinglePartDownloadTransmitter(int taskId, RFile localFile, long size,
                                                        TransmitterOptions options) {
        super(taskId, options);
        context = BaseApplication.getInstance();
        this.localFile = localFile;
        mDestinationPath = localFile;
        mSize = size;
        if (!Target30StorageKt.isPartitionStorage()) {
            String tempPath = localFile.localUrl() + TransferFileNameConstant.DOWNLOAD_SUFFIX;
            tempFilePath = PathKt.rFile(tempPath);
        }
    }

    @Override
    public void start() {
        TaskSchedulerImpl.INSTANCE.addHighTask(new BaseJob("SingleThreadSinglePartDownloadTransmitterRunnable") {
            @Override
            protected void performExecute()  {
                retryTimes = 0;
                signalNetworkProcessRetryTimes = 0;
                prepareTransmit();
                transmit(null);
            }
        });
    }

    @Override
    public void pause() {
        super.pause();
        isPause = true;
        DuboxLog.d(TAG, "pause()");
    }

    @Override
    public void prepareTransmit() {
        isPause = false;
    }

    /**
     * 因为在传输过程中就算是删除TASK，传输的线程也不会停止，这样会导致删除文件失败。 所以建立标志位来控制在传输停止时删除文件
     **/
    private boolean isNeedToDeleteFile = false;

    @Override
    public void remove(boolean isDeleteFile) {
        isNeedToDeleteFile = isDeleteFile;
        isPause = true;
        Target30StorageKt.deleteTempFile(tempFilePath, mDestinationPath, false);
        DuboxLog.d(TAG, "remove()");
    }

    @Override
    protected void transmit(TransmitBlock block) {
        try {
            // 下载之前判断target30产生的uri是否为null ,如果为null，则下载失败
            if (block != null && TextUtils.isEmpty(block.destinationPath.localUrl())) {
                if (mOptions.getStatusCallback() != null) {
                    mOptions.getStatusCallback().onFailed(OtherErrorCode.ERROR_TARGET30_URI_NULL,
                            "local download uri is null");
                    EventStatisticsKt.statisticActionEvent(StatisticsKeysKt.MEDIASTORE_CREATE_LOCAI_URL_NULL);
                }
                return;
            }
            while (true) {
                if (isPause) {
                    DuboxLog.d(TAG, "transmit is pause  taskId:" + mTaskId);
                    throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "transmit task pause");
                }

                try {
                    download();
                    break;
                } catch (Retry e) {
                    // for retry
                    DuboxLog.d(TAG, "Retry");
                    doRetry(e);
                }
            }

            DuboxLog.d(TAG, "transmit mCompleteSize:" + mCompleteSize + " ,mSize:" + mSize);
            rename();
            if (mOptions.getStatusCallback() != null) {
                DuboxLog.d(TAG, "onSuccess");
                mOptions.getStatusCallback().onSuccess(null);
            }
        } catch (StopRequestException e) {
            DuboxLog.d(TAG, "StopRequestException =" + e.getMessage(), e);
            if (isPause) {
                return;
            }
            if (e.mFinalStatus == TransmitterConstant.NETWORK_VERIFY_CHECKING) {
                return;
            }

            if (!ConnectivityState.isConnected(BaseApplication.getInstance())) {
                if (mOptions.getStatusCallback() != null) {
                    mOptions.getStatusCallback().onFailed(TransmitterConstant.NETWORK_NO_CONNECTION, null);
                }
            } else {
                if (mOptions.getStatusCallback() != null) {
                    mOptions.getStatusCallback().onFailed(e.mFinalStatus, null);
                }
            }
        } catch (SecurityException e) {
            DuboxLog.d(TAG, "SecurityException =" + e.getMessage(), e);
            if (isPause) {
                return;
            }
            if (mOptions.getStatusCallback() != null) {
                mOptions.getStatusCallback().onFailed(TransmitterConstant.NETWORK_REFUSE, null);
            }
        } catch (FileNotFoundException e) {
            DuboxLog.d(TAG, "FileNotFoundException =" + e.getMessage(), e);
            if (isPause) {
                return;
            }
            if (mOptions.getStatusCallback() != null) {
                mOptions.getStatusCallback().onFailed(TransmitterConstant.NETWORK_REFUSE, null);
            }
        } finally {
            if (isNeedToDeleteFile) {
                if (Target30StorageKt.isPartitionStorage()) {
                    mDestinationPath.delete(BaseApplication.getInstance());
                } else {
                    mDestinationPath.delete(BaseApplication.getInstance());
                    if (tempFilePath != null) {
                        tempFilePath.delete(BaseApplication.getInstance());
                    }
                }
            }
        }
    }

    /**
     * 下载过程
     * 
     * @throws StopRequestException
     * @throws Retry
     */
    protected abstract void download() throws StopRequestException, Retry, FileNotFoundException;

    /**
     * 检测Sdcard空间
     * 
     * @throws StopRequestException
     */
    protected void checkStorage() throws StopRequestException {
        if (mCompleteSize < 0L) {
            mCompleteSize = 0L;
        }
        long needSpace = mSize - mCompleteSize;
        if (needSpace > 0) {
            if (!Target30StorageKt.isDownloadSpaceEnough(needSpace, mDestinationPath.localUrl(), false)) {
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_SPACE_FULL);
                DuboxLog.d(TAG, "isSDCardEnough false");
                throw new StopRequestException(TransmitterConstant.SDCARD_NO_SPACE_ERROR,
                        TransmitterConstant.getExceptionMsg(TransmitterConstant.SDCARD_NO_SPACE_ERROR));
            }
            DuboxLog.d(TAG, "isSDCardEnough true");
        }
    }

    /**
     * 构建连接
     * 
     * @return
     * @throws Retry
     * @throws StopRequestException
     */
    protected HttpURLConnection buildConnection() throws Retry, StopRequestException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) (getUrl().openConnection());
            conn.setInstanceFollowRedirects(true);// 是否自动重定向302
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setRequestMethod(HttpRequest.HTTP_GET);
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            addHeaders(conn);
        } catch (ProtocolException e) {
            if (conn != null) {
                conn.disconnect();
            }
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_PROTOCOL_EXCEPTION, "ProtocolException");
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            StatisticsLog.countDownloadFailedByNetworkError();
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + e.getMessage());
        }
        return conn;
    }

    /**
     * 获取URL
     * 
     * @return
     * @throws Retry
     * @throws StopRequestException
     */
    protected URL getUrl() throws Retry, StopRequestException {
        try {
            return new URL(getUrlString());
        } catch (MalformedURLException e) {
            DuboxLog.e(TAG, "getUrl::MalformedURLException " + e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_URL_EXCEPTION, "MalformedURLException");
        }
    }

    /**
     * 获取URL
     * 
     * @return
     * @throws Retry
     * @throws StopRequestException
     */
    protected abstract String getUrlString() throws Retry, StopRequestException;

    /**
     * 获取HTTP输入流
     * 
     * @param conn
     * @return
     * @throws StopRequestException
     */
    protected BufferedInputStream openResponseEntity(HttpURLConnection conn) throws StopRequestException {
        try {
            return new BufferedInputStream(conn.getInputStream());
        } catch (IOException ex) {
            DuboxLog.e(TAG, ex.getMessage(), ex);
            throw new StopRequestException(OtherErrorCode.STREAM_EXCEPTION, ex.getMessage(), ex);
        }
    }

    /**
     * 适配Target30下载，获取输出流，30以下使用RandomAccessFile
     * @param destinationPath
     * @return
     */
    OutputStream getOutputStream(RFile destinationPath) throws FileNotFoundException {
        if (Target30StorageKt.isPartitionStorage()) {
            // target30非私有目录存储，不使用RandomAccessFile,
            return destinationPath.outputStream(BaseApplication.getContext(), "wa");
        }
        return null;
    }


    /**
     * 创建目标文件
     * 
     * @return
     * @throws Retry
     */
    protected RandomAccessFile setupDestinationFile() throws Retry {
        if (Target30StorageKt.isPartitionStorage() || tempFilePath == null) {
            return null;
        }
        String tempPath = tempFilePath.localUrl();
        if (tempFilePath.exists()) {
            mCompleteSize = tempFilePath.length();
        } else {
            FileUtils.createParentFileDir(tempPath);
            mCompleteSize = 0L;
        }
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(tempPath, "rw");
            file.seek(mCompleteSize);
        } catch (FileNotFoundException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(TransmitterConstant.DESTINATION_FILE_ERROR,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.DESTINATION_FILE_ERROR));
        } catch (IOException e) {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e1) {
                DuboxLog.w(TAG, "file.seek", e1);
            }
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(TransmitterConstant.DESTINATION_FILE_ERROR,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.DESTINATION_FILE_ERROR));
        }
        return file;
    }

    /**
     * 改名字去掉!bn
     * 
     * @return
     * @author 孙奇 V 1.0.0 Create at 2013-7-19 上午11:35:33
     */
    protected void rename() throws StopRequestException {
        if (!Target30StorageKt.isPartitionStorage()) {
            mDestinationPath.delete(BaseApplication.getInstance());  // 删除同名旧文件
        }
        // rename的是要下载目录下的文件
        if (Target30StorageKt.rename(tempFilePath, mDestinationPath, false)) {
            DuboxLog.i(TAG, "rename succeed.");
            calculate(mSize - mCompleteSize, -1L);
        } else {
            DuboxLog.i(TAG, "rename failed.");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                DuboxLog.e(TAG, e.getMessage(), e);
                throw new StopRequestException(OtherErrorCode.THREAD_INTERRUPTED, "rename failed "
                        + e.getMessage(), e);
            }
            if (Target30StorageKt.rename(tempFilePath, mDestinationPath, false)) {
                // rename的是要下载目录下的文件
                DuboxLog.i(TAG, "rename succeed.");
                calculate(mSize - mCompleteSize, -1L);
            } else {
                throw new StopRequestException(OtherErrorCode.LOCAL_RENAME_FAIL, "rename failed ");
            }
        }
    }

    /**
     * 处理异常的头信息
     * 
     * @param conn
     * @throws StopRequestException 返回的头信息有异常情况时抛出的异常，停止请求
     */
    protected void handleExceptionalHeader(HttpURLConnection conn) throws Retry, StopRequestException {
    }

    /**
     * 处理异常的响应码
     * <p/>
     * 如果子类对异常响应码有特殊处理，那么不要调用super，因为基类中会抛出异常，导致子类不能正常处理
     * 
     * @param conn
     * @throws IOException
     * @throws StopRequestException 响应码异常的情况下抛出的异常，停止请求
     * @throws Retry
     */
    protected void handleExceptionalResponseCode(HttpURLConnection conn) throws StopRequestException, Retry {
        // 有信号无网络此处抛出UnknownHostException YQH 20121116
        int resp = 0;
        try {
            resp = conn.getResponseCode();
        } catch (IOException e) {
            DuboxLog.d(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + e.getMessage());
        } catch (NullPointerException ignore) {
            // 处理2.3系统获取返回值崩溃 libin09 2016-3-21
            DuboxLog.w(TAG, "handleExceptionalResponseCode", ignore);
            throw new Retry(OtherErrorCode.RETRY_URL_EXCEPTION, "NullPointerException " + ignore.getMessage());
        }

        if (resp != HttpURLConnection.HTTP_OK && resp != HttpURLConnection.HTTP_PARTIAL) {
            DuboxLog.i(TAG, "Error responseCode=" + resp);
            throw new StopRequestException(OtherErrorCode.HTTP_NO_200_AND_206,
                    "http resp code not 200 and not 206");
        }
    }

    /**
     * 为网络连接增加头信息
     * 
     * @param conn
     */
    protected void addHeaders(HttpURLConnection conn) {
        conn.setRequestProperty("Content-Transfer-Encoding", "binary");
        if (mCompleteSize >= 0L) {
            conn.setRequestProperty("RANGE", "bytes=" + mCompleteSize + "-");
            conn.setRequestProperty("Accept-Encoding", "identity");
        }
    }

    /**
     * 传输数据
     * 
     * @param file
     * @param downloadInputStream
     * @throws StopRequestException
     * @throws Retry
     */
    protected void transferData(RandomAccessFile file,
                                OutputStream os, BufferedInputStream downloadInputStream)
            throws StopRequestException, Retry {
        long lastUpdateClock = SystemClock.elapsedRealtime();

        DuboxLog.d(TAG, "transferData begin");
        byte[] buf = new byte[DOWNLOAD_BYTES_SIZE];
        int size = 0;
        long deltaSizeSum = 0L;
        try {
            while ((!isPause) && (downloadInputStream != null) && ((size = downloadInputStream.read(buf)) != -1)) {
                if (isWaitingWiFi()) {// 用于2G下上传过程中，勾选仅在WIFI下下载，触发RUNNING到PENDING的转换
                    throw new StopRequestException(TransmitterConstant.WAITING_FOR_WIFI,
                            TransmitterConstant.getExceptionMsg(TransmitterConstant.WAITING_FOR_WIFI));
                }
                if (file != null) {
                    file.write(buf, 0, size);
                } else if (os != null) {
                    os.write(buf, 0, size);
                }

                deltaSizeSum += size;
                mCompleteSize += size;

                long time = SystemClock.elapsedRealtime();
                long intervalTime = time - lastUpdateClock;
                if (intervalTime > PROGRESS_UPDATE_INTERVAL) {
                    final long rate;
                    if (mOptions.isRateCalculateEnable()) {// 计算传输速率
                        rate = mOptions.getRateCalculator().calculateRealRate(deltaSizeSum, intervalTime);
                    } else {
                        rate = -1L;
                    }

                    calculate(deltaSizeSum, rate);

                    deltaSizeSum = 0L;
                    lastUpdateClock = time;
                }
            }
            if (isPause) {
                throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
            }
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + e.getMessage());
        } finally {
            if (mOptions.isRateCalculateEnable()) {// 计算传输速率
                resetRateCalculator();
            }
        }
    }
}