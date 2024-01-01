package com.moder.compass.transfer.transmitter;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.List;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.log.transfer.TransferFieldKey;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.statistics.EventStatisticsKt;
import com.moder.compass.statistics.StatisticsKeysKt;
import com.moder.compass.transfer.TransferFileNameConstant;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.locate.LocateDownloadUrls;
import com.moder.compass.transfer.transmitter.ratelimiter.IRateLimitable;
import com.moder.compass.transfer.transmitter.throwable.Retry;
import com.moder.compass.transfer.transmitter.throwable.RetryLocateDownload;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;

import android.text.TextUtils;
import android.util.Pair;

import rubik.generate.context.dubox_com_pavobox_drive.DriveContext;
import rubik.generate.context.dubox_com_dubox_drive_vip.VipContext;

/**
 * 单线程下载传输器
 * 
 * @author 孙奇 <br/>
 *         create at 2012-12-7 上午02:14:09
 */
abstract class SingleThreadMultiPartDownloadTransmitter extends DownloadTransmitter {
    private static final String TAG = "SingleThreadMultiPartDownloadTransmitter";

    /**
     * 下载BUFFER
     */
    private static final int DOWNLOAD_BYTES_SIZE = 1024 * 16;

    /**
     * 传输器状态
     */
    protected int mState = TransmitterConstant.TRANSMITTER_STATE_INIT; // 下载器状态

    /**
     * 状态切换锁
     */
    private final byte[] mStateLock = new byte[1];

    /**
     * 每块长度50M
     */
    private static final long BLOCK_LENGTH = 1024 * 1024 * 50L;

    /**
     * 整个文件信息
     */
    TransmitBlock mFileInfo;


    private boolean mLastProbationaryStateIsRun = false;
    private boolean mCurrentProbationaryStateIsRun = false;

    SingleThreadMultiPartDownloadTransmitter(int taskId, RFile localFile, long size,
                                             TransmitterOptions options) {
        super(taskId, options);
        mFileInfo = new TransmitBlock();
        // Target29及以上，该值为uri.toString, 以下为文件绝对路径
        mFileInfo.destinationPath = localFile;
        mFileInfo.fileSize = size;
        mFileInfo.endPosition = mFileInfo.fileSize - 1;
        mFileInfo.isDownloadPrivateDir = isDownloadPrivateDir();
        // 低于target30, 预览下载到私有目录，不需要重新创建临时目录
        if (!Target30StorageKt.isPartitionStorage() || mFileInfo.isDownloadPrivateDir) {
            String tempPath = localFile.localUrl() + TransferFileNameConstant.DOWNLOAD_SUFFIX;
            mFileInfo.tempDestinationPath = PathKt.rFile(tempPath);
        }

        // PreviewDownload无RateLimiter
        if (mOptions.getRateLimiter() != null) {
            mLastProbationaryStateIsRun = mOptions.getRateLimiter().isRunningProbationary();
            mCurrentProbationaryStateIsRun = mOptions.getRateLimiter().isRunningProbationary();
        }
    }

    /**
     * 是否下载到私有目录，默认下载到Download公共目录，
     * 原图下载下载到私有目录
     * @return
     */
    protected boolean isDownloadPrivateDir() {
        return false;
    }


    /**
     * 初始化URL列表
     *
     * @return 下载列表
     */
    protected abstract List<LocateDownloadUrls> initUrls();

    /**
     * 初始化加速试用URL列表
     *
     * @return 试用加速的下载列表
     */
    protected abstract List<LocateDownloadUrls> initProbationaryUrls();

    /**
     * 用户vip等级发生变化
     */
    protected abstract void onVipLevelChange();

    @Override
    public void pause() {
        super.pause();
        isPause = true;
        DuboxLog.i(TAG, "set transmitter pause mTaskId:" + mTaskId);
    }

    @Override
    public void start() {
        if (checkState(TransmitterConstant.TRANSMITTER_STATE_TRANSMITTING)) {// 如果正在下载就return
            DuboxLog.i(TAG, "checkState is already TRANSMITTER_STATE_TRANSMITTING return:" + mTaskId);
            return;
        }
        DuboxLog.i(TAG, "setState TRANSMITTER_STATE_TRANSMITTING:" + mTaskId);
        setState(TransmitterConstant.TRANSMITTER_STATE_TRANSMITTING);
        callBackStart();
        prepareTransmit();
        try {
            checkStorage(mFileInfo);
        } catch (StopRequestException e) {
            DuboxLog.i(TAG, "StopRequestException: " + mTaskId);
            DuboxLog.e(TAG, e.getMessage(), e);
            callBackError(e.mFinalStatus, "start checkStorage " + e.getMessage());
            return;
        }
        TransmitBlock block = createNextBlock(false);
        feedbackMonitorLog("startThreads::start block " + block.toString());
        TransmitThread downloadThread = new TransmitThread(block);
        downloadThread.start();
    }

    /**
     * 创建下一个传输块
     * @param isFromProbation 是否从非限速态转变过来
     * @return 下个传输块
     */
    private TransmitBlock createNextBlock(boolean isFromProbation) {
        TransmitBlock block = createBlock();
        if (isFromProbation) {
            block.setUrls(initUrls());
        }
        return block;
    }

    /**
     * 创建下一个传输块
     * @param isFromNormal 是否从限速态转变过来
     * @return 下个传输块
     */
    private TransmitBlock createProbationBlock(boolean isFromNormal) {
        TransmitBlock block = createBlock();
        if (isFromNormal) {
            block.setUrls(initProbationaryUrls());
        }
        return block;
    }

    private TransmitBlock createBlock() {
        TransmitBlock block = mFileInfo.clone();
        block.completeSize = 0L;
        block.startPosition = mFileInfo.completeSize;
        if ((block.startPosition + BLOCK_LENGTH) >= mFileInfo.endPosition) {
            block.endPosition = mFileInfo.endPosition;
        } else {
            block.endPosition = block.startPosition + BLOCK_LENGTH;
        }
        return block;
    }

    @Override
    protected void prepareTransmit() {
        try {
            Target30StorageKt.createTempFile(mFileInfo.tempDestinationPath, mFileInfo.isDownloadPrivateDir);
        } catch (IOException e) {
            callBackError(TransmitterConstant.DESTINATION_FILE_ERROR,
                    "prepareTransmit createFile " + e.getMessage());
            return;
        }
        initFileInfo();
    }

    /**
     * 初始化整体文件信息
     */
    private void initFileInfo() {
        // PreviewDownload的mCurrentProbationaryStateIsRun为false
        if (mCurrentProbationaryStateIsRun) {
            mFileInfo.setUrls(initProbationaryUrls());
        } else {
            mFileInfo.setUrls(initUrls());
        }
        // 初始化断点
        mFileInfo.completeSize =
                Target30StorageKt.getCurrentDownloadSize(mFileInfo.tempDestinationPath,
                        mFileInfo.destinationPath, mFileInfo.isDownloadPrivateDir);
        mOffsetSize = mFileInfo.completeSize;
        mTransferLog.setStartPosition(mFileInfo.completeSize);
        mInstantDownloadLog.setStartPosition(mFileInfo.completeSize);
    }

    /** 因为在传输过程中就算是删除TASK，传输的线程也不会停止，这样会导致删除文件失败。 所以建立标志位来控制在传输停止时删除文件 **/
    private boolean isNeedToDeleteFile = false;

    @Override
    public void remove(boolean isDeleteFile) {
        isNeedToDeleteFile = isDeleteFile;
        isPause = true;

        Target30StorageKt.deleteTempFile(mFileInfo.tempDestinationPath, mFileInfo.destinationPath,
                mFileInfo.isDownloadPrivateDir);

        mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_REMOVE);
    }

    @Override
    protected void transmit(TransmitBlock block) {
        try {
            // 下载之前判断target30产生的uri是否为null ,如果为null，则下载失败
            if (TextUtils.isEmpty(block.destinationPath.localUrl())) {
                feedbackMonitorLog("transmit:local download uri is null " + block.toString());
                callBackError(OtherErrorCode.ERROR_TARGET30_URI_NULL,
                        "local download uri is null");
                EventStatisticsKt.statisticActionEvent(StatisticsKeysKt.MEDIASTORE_CREATE_LOCAI_URL_NULL);
                return;
            }
            // 是否需要切换链接
            boolean isChangeUrl = false;
            // 用户原会员身份
            Boolean isVip = VipContext.Companion.isVip();
            while (true) {
                if (isPause) {
                    DuboxLog.d(TAG, "transmit is pause taskId:" + mTaskId);
                    throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
                }

                try {
                    download(block);
                    break;
                } catch (Retry e) {
                    DuboxLog.d(TAG, "Retry");
                    if (e.mFinalStatus == TransmitterConstant.DOWNLOAD_URL_CHANGE) {
                        DuboxLog.d(TAG, "catch DOWNLOAD_URL_CHANGE retry");
                        isChangeUrl = true;
                        break;
                    }
                    doRetry(e);
                } catch (RetryLocateDownload e) {
                    doRetryLocateDownload(e);
                }
            }
            mFileInfo.completeSize += block.completeSize;
            if (mFileInfo.completeSize == mFileInfo.fileSize) { // 如果完成量等于文件长度，则表示全部完成
                DuboxLog.d(TAG, "transmit:: all done");
                rename();
                callBackSuccess();
            } else {// 继续下另一个块
                TransmitThread downloadThread;
                if (mCurrentProbationaryStateIsRun) {
                    downloadThread = new TransmitThread(createProbationBlock(isChangeUrl));
                    DuboxLog.d(TAG, "new thread pro: " + isChangeUrl);
                } else {
                    processVipLevel(isVip);
                    downloadThread = new TransmitThread(createNextBlock(isChangeUrl));
                    DuboxLog.d(TAG, "new thread normal: " + isChangeUrl);
                }
                downloadThread.start();
            }
        } catch (StopRequestException e) {
            DuboxLog.d(TAG, "StopRequestException =" + e.getMessage(), e);
            if (isPause) {
                callBackPause();
                return;
            }

            DuboxLog.d(TAG, "ConnectivityState.isConnected(BaseApplication.getInstance():"
                    + ConnectivityState.isConnected(BaseApplication.getInstance()));

            if (ConnectivityState.isConnected(BaseApplication.getInstance())) {
                DuboxLog.d(TAG, "e.mFinalStatus:" + e.mFinalStatus);
                callBackError(e.mFinalStatus, "transmit isConnected " + e.getMessage());
            } else {
                DuboxLog.d(TAG, "e.mFinalStatus:TransmitterConstant.NETWORK_NO_CONNECTION)");
                callBackError(TransmitterConstant.NETWORK_NO_CONNECTION,
                        "transmit not isConnected " + e.getMessage());
            }
        } catch (SecurityException e) {
            DuboxLog.d(TAG, "SecurityException =" + e.getMessage(), e);
            if (isPause) {
                callBackPause();
                return;
            }
            callBackError(TransmitterConstant.NETWORK_REFUSE, "transmit SecurityException e:" + e.getMessage());
        } catch (FileNotFoundException e) {
            DuboxLog.d(TAG, "FileNotFoundException =" + e.getMessage(), e);
            if (isPause) {
                callBackPause();
                return;
            }
            callBackError(TransmitterConstant.NETWORK_REFUSE,
                    "transmit FileNotFound e:" + e.getMessage());
        } finally {
            if (isNeedToDeleteFile) {
                mFileInfo.destinationPath.delete(BaseApplication.getInstance());
                if (mFileInfo.tempDestinationPath != null) {
                    mFileInfo.tempDestinationPath.delete(BaseApplication.getInstance());
                }
            }
        }
    }

    protected abstract void doRetryLocateDownload(RetryLocateDownload t) throws StopRequestException;

    /**
     * 检查当前vip等级和之前vip等级是否相同，若vip身份变化，则调用onVipLevelChange
     *
     */
    private void processVipLevel(Boolean isVip) {
        Boolean currentIsVip = VipContext.Companion.isVip();
        if (isVip == null || currentIsVip == null) {
            return;
        }
        if (isVip.booleanValue() != currentIsVip.booleanValue()) {
            DuboxLog.d(TAG, "vipLevelChange,last= "
                    + isVip + " current: " + currentIsVip);
            onVipLevelChange();
        }
    }


    /**
     * 回调开始
     */
    private void callBackStart() {
        DuboxLog.i(TAG, "callBackStart");
        mTransferLog.setFileSize(mFileInfo.fileSize);
        long startTime = System.currentTimeMillis();
        mTransferLog.setStartTime(startTime);
        mInstantDownloadLog.setStartTime(startTime);
    }

    /**
     * 出现错误时候回调
     * 
     * @param errorCode 错误码
     */
    void callBackError(int errorCode, String errMessage) {
        DuboxLog.i(TAG, "callBackError:: errorCode = " + errorCode);
        if (checkState(TransmitterConstant.TRANSMITTER_STATE_ERROR)) {
            DuboxLog.i(TAG, "callBackError:: checkState  already TRANSMITTER_STATE_ERROR return ");
            return;
        }
        feedbackMonitorError(errorCode, "callBackError errorCode:" + errorCode + " errorMessage:" + errMessage);

        setState(TransmitterConstant.TRANSMITTER_STATE_ERROR);

        if (mOptions.getStatusCallback() != null) {
            mOptions.getStatusCallback().onFailed(errorCode, errMsgForShow);
        }

        if (mOptions.isRateCalculateEnable()) {// 计算传输速率
            resetRateCalculator();
        }

        if (mTransferLog.getEndTime() == 0) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            if (isNetError(errorCode)) {
                mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_PAUSE);
            } else {
                mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FAIL);
            }
        }
        mTransferLog.setOtherErrorCode(errorCode);
        mTransferLog.setOtherErrorMessage(errMessage);
        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    /**
     * 回调成功
     */
    void callBackSuccess() {
        DuboxLog.i(TAG, "callBackSuccess");
        if (checkState(TransmitterConstant.TRANSMITTER_STATE_DONE)) {
            return;
        }

        setState(TransmitterConstant.TRANSMITTER_STATE_DONE);

        if (mOptions.getStatusCallback() != null) {
            DuboxLog.i(TAG, "onSuccess");
            mOptions.getStatusCallback().onSuccess(null);
        }

        if (mOptions.isRateCalculateEnable()) {// 计算传输速率
            resetRateCalculator();
        }

        if (mTransferLog.getEndTime() == 0) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FINISH);
        }
        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    /**
     * 回调暂停
     */
    private void callBackPause() {
        if (checkState(TransmitterConstant.TRANSMITTER_STATE_PAUSE)) {
            return;
        }
        setState(TransmitterConstant.TRANSMITTER_STATE_PAUSE);
        if (mOptions.isRateCalculateEnable()) {// 计算传输速率
            resetRateCalculator();
        }

        if (mTransferLog.getEndTime() == 0) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_PAUSE);
        }
        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    /**
     * 设置State
     * 
     * @param stateCode 传输器状态码
     */
    protected void setState(int stateCode) {
        synchronized (mStateLock) {
            if (mState != stateCode) {
                mState = stateCode;
            }
        }
    }

    /**
     * 检测当前传输器状态是否为stateCode
     * 
     * @param stateCode 传输器状态码
     * @return 当前传输器状态是否为stateCode
     */
    private boolean checkState(int stateCode) {
        synchronized (mStateLock) {
            return mState == stateCode;
        }
    }

    /**
     * 下载过程
     * 
     * @param block 传输块
     * 
     * @throws StopRequestException 停止请求异常
     * @throws Retry 重试异常
     */
    protected abstract void download(TransmitBlock block) throws StopRequestException, Retry,
            RetryLocateDownload, FileNotFoundException;

    /**
     * 检测Sdcard空间是否足够
     * 
     * @param bean 传输块
     * 
     * @throws StopRequestException 停止请求异常
     */
    void checkStorage(TransmitBlock bean) throws StopRequestException {
        long needSpace = bean.fileSize - bean.completeSize;
        if (needSpace > 0) {
            if (!Target30StorageKt.isDownloadSpaceEnough(needSpace,
                    bean.destinationPath.localUrl(), bean.isDownloadPrivateDir)) {
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_SPACE_FULL);
                DuboxLog.d(TAG, "isSDCardEnough false");
                feedbackMonitorLog("isSDCardEnough false:");
                throw new StopRequestException(TransmitterConstant.SDCARD_NO_SPACE_ERROR,
                        TransmitterConstant.getExceptionMsg(TransmitterConstant.SDCARD_NO_SPACE_ERROR));
            }
            DuboxLog.d(TAG, "isSDCardEnough true");
        }
    }


    LocateDownloadUrls getUrlString(TransmitBlock block) throws Retry {
        LocateDownloadUrls strUrl = block.getServer(true);
        DuboxLog.d(TAG, "getUrlString = " + strUrl);
        if (strUrl == null || TextUtils.isEmpty(strUrl.url)) {
            throw new Retry(TransmitterConstant.NETWORK_VERIFY_CHECKING, "url == null");
        }
        return strUrl;
    }

    /**
     * 获取HTTP输入流
     * 
     * @param conn http连接
     * @return 数据输入流
     */
    protected BufferedInputStream openResponseEntity(HttpURLConnection conn) throws Retry {
        try {
            if (conn == null) {
                throw new Retry(OtherErrorCode.RETRY_HTTP_URL_CONNECTION, "HttpURLConnection null");
            }
            return new BufferedInputStream(conn.getInputStream());
        } catch (IOException ex) {
            DuboxLog.e(TAG, ex.getMessage(), ex);
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + ex.getMessage());
        }
    }

    /**
     * 适配Target30下载，获取输出流，30以下使用RandomAccessFile
     * @param block
     * @return
     */
    OutputStream getOutputStream(TransmitBlock block) throws FileNotFoundException {
        if (Target30StorageKt.isPartitionStorage() && !block.isDownloadPrivateDir) {
            // target30非私有目录存储，不使用RandomAccessFile,
            return block.destinationPath.outputStream(BaseApplication.getContext(), "wa");
        }
        return null;
    }

    /**
     * 创建目标文件, Target30,使用uri获取输出流，该方法返回null
     * @param block 传输块
     * @return 待写入的文件
     * @throws Retry 重试异常
     */
    RandomAccessFile setupDestinationFile(TransmitBlock block) throws Retry {
        if (Target30StorageKt.isPartitionStorage() && !mFileInfo.isDownloadPrivateDir) {
            return null;
        }
        // 30以下走老逻辑
        long offset;
        if (!block.tempDestinationPath.exists()) { // 临时文件不存在则创建临时文件，如果目录不存在就先创建目录
            DuboxLog.i(TAG, "file do not exist!" + block.tempDestinationPath.localUrl());
            // 传输一部分文件然后删除下载目录，文件创建不成功能
            String tempPath = block.tempDestinationPath.localUrl();
            FileUtils.createParentFileDir(tempPath);
            offset = block.startPosition;
        } else {// 如果临时文件存在,信任临时文件的长度 offset赋值为临时文件长度
            offset = block.startPosition + block.completeSize;
        }
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(block.tempDestinationPath.localUrl(), "rw");
            file.seek(offset);
        } catch (FileNotFoundException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(TransmitterConstant.DESTINATION_FILE_ERROR,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.DESTINATION_FILE_ERROR)
                            + e.getMessage());
        } catch (IOException e) {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e1) {
                DuboxLog.w(TAG, "file.seek异常", e1);
            }
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(TransmitterConstant.DESTINATION_FILE_ERROR,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.DESTINATION_FILE_ERROR)
                            + e.getMessage());
        }
        return file;
    }

    /**
     * 改名字去掉!bn
     *
     * @author 孙奇 V 1.0.0 Create at 2013-7-19 上午11:35:33
     */
    private void rename() throws StopRequestException {
        if (!Target30StorageKt.isPartitionStorage() || mFileInfo.isDownloadPrivateDir) {
            mFileInfo.destinationPath.delete(BaseApplication.getInstance());
        }
        if (Target30StorageKt.rename(mFileInfo.tempDestinationPath, mFileInfo.destinationPath,
                mFileInfo.isDownloadPrivateDir)) {
            calculate(mFileInfo.fileSize - mOffsetSize, -1L);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new StopRequestException(OtherErrorCode.THREAD_INTERRUPTED,
                        "rename failed " + e.getMessage(), e);
            }
            if (Target30StorageKt.rename(mFileInfo.tempDestinationPath,
                    mFileInfo.destinationPath, mFileInfo.isDownloadPrivateDir)) {
                calculate(mFileInfo.fileSize - mOffsetSize, -1L);
            } else {
                throw new StopRequestException(OtherErrorCode.LOCAL_RENAME_FAIL, "rename failed ");
            }
        }
    }

    /**
     * 处理异常的头信息
     * 
     * @param response  http响应
     * @throws StopRequestException 返回的头信息有异常情况时抛出的异常，停止请求
     */
    protected void handleExceptionalHeader(HttpURLConnection response) throws StopRequestException {
    }

    /**
     * 处理异常的响应码
     * <p/>
     * 如果子类对异常响应码有特殊处理，那么不要调用super，因为基类中会抛出异常，导致子类不能正常处理
     * 
     * @param response http响应
     * @throws StopRequestException 响应码异常的情况下抛出的异常，停止请求
     * @throws Retry 重试异常
     */
    protected void handleExceptionalResponseCode(HttpURLConnection response) throws StopRequestException, Retry {
        int resp;
        try {
            resp = response.getResponseCode();
            if (resp != HttpURLConnection.HTTP_OK && resp != HttpURLConnection.HTTP_PARTIAL) {
                DuboxLog.i(TAG, "Error responseCode=" + resp);
            }
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new StopRequestException(OtherErrorCode.STREAM_EXCEPTION,
                    "handle response code ioException " + e.getMessage());
        }
    }

    /**
     * 为网络连接增加头信息
     * 
     * @param conn http连接
     * @param block 传输块
     */
    protected void addHeaders(HttpURLConnection conn, TransmitBlock block) {
        conn.setRequestProperty("Content-Transfer-Encoding", "binary");
        conn.setRequestProperty("RANGE",
                "bytes=" + (block.startPosition + block.completeSize) + "-" + block.endPosition);
        conn.setRequestProperty("Accept-Encoding", "identity");
        mTransferLog.setHttpRange("bytes=" + (block.startPosition + block.completeSize) + "-" + block.endPosition);
    }

    /**
     * 传输数据
     * 
     * @param file 待写入的文件
     * @param downloadInputStream 下载数据的输入流
     * @param block 传输块
     * @throws StopRequestException 停止请求异常
     * @throws Retry 重试异常
     */
    protected void transferData(RandomAccessFile file, OutputStream os ,
                                BufferedInputStream downloadInputStream, TransmitBlock block)
            throws StopRequestException, Retry {
        DuboxLog.d(TAG, "transferData begin:" + mTaskId);
        byte[] buf = new byte[DOWNLOAD_BYTES_SIZE];
        int deltaSize = 0;
        // 下面是用于降低进度和速度计算频率的延迟
        long deltaSizeSum = 0L;
        long lastSendCalculateTime = System.currentTimeMillis();

        // 限速中只读1字节.2015.6.18,7.10,libin09
        byte[] bufInLimited = new byte[1];
        // 是否被限速，如果被限速.2015.6.18,7.10,libin09
        IRateLimitable.State limitState = IRateLimitable.State.UNLIMITED;

        try {
            while ((!isPause) && (downloadInputStream != null)
                    && (IRateLimitable.State.LIMITED == limitState || (deltaSize = downloadInputStream
                            .read(IRateLimitable.State.LIMITED_READ == limitState ? bufInLimited : buf)) != -1)) {
                if (isWaitingWiFi()) {// 用于2G下上传过程中，勾选仅在WIFI下下载，触发RUNNING到PENDING的转换
                    throw new StopRequestException(TransmitterConstant.WAITING_FOR_WIFI,
                            TransmitterConstant.getExceptionMsg(TransmitterConstant.WAITING_FOR_WIFI));
                }

                if (IRateLimitable.State.LIMITED != limitState) {
                    // 限速1分钟内中，不读取任何数据
                    if (file != null) {
                        file.write(IRateLimitable.State.LIMITED_READ == limitState
                                ? bufInLimited : buf, 0, deltaSize);
                    } else if (os != null) {
                        os.write(IRateLimitable.State.LIMITED_READ == limitState
                                ? bufInLimited : buf, 0, deltaSize);
                    }
                    block.completeSize += deltaSize;
                    deltaSizeSum += deltaSize;
                }

                long time = System.currentTimeMillis();
                long intervalTime = time - lastSendCalculateTime;
                if (intervalTime >= Constants.CALCULAT_INTERVAL) {
                    final long rate;
                    if (mOptions.isRateCalculateEnable()) {// 计算传输速率
                        final long realRate =
                                mOptions.getRateCalculator().calculateRealRate(deltaSizeSum, intervalTime);

                        // DuboxLog.d(TAG, "transferData realRate:" + realRate);
                        long threshold = 0L;
                        if (mOptions.getRateLimiter() != null) {
                            // 根据真实速度确定是否限速
                            final Pair<IRateLimitable.State, Long> value = mOptions.getRateLimiter()
                                    .limit(realRate, false);
                            limitState = value.first;
                            //
                            threshold = value.second;
                            mCurrentProbationaryStateIsRun = mOptions.getRateLimiter().isRunningProbationary();
                        }

                        if (mLastProbationaryStateIsRun != mCurrentProbationaryStateIsRun) {
                            mLastProbationaryStateIsRun = mCurrentProbationaryStateIsRun;
                            DuboxLog.d(TAG, "throw retry:" + mCurrentProbationaryStateIsRun);
                            feedbackMonitorLog("throw retry:" + mCurrentProbationaryStateIsRun);
                            throw new Retry(TransmitterConstant.DOWNLOAD_URL_CHANGE,
                                    "DOWNLOAD_URL_CHANGE");
                        }

                        // 平滑后的速度
                        time = System.currentTimeMillis();
                        final long intervalTime1 = time - lastSendCalculateTime;
                        rate = mOptions.getRateCalculator().calculateSmoothRate(intervalTime1, threshold);
                        DuboxLog.d(TAG, "isInLimited:" + limitState + ",realRate:" + realRate + ",rate:" + rate
                                + ",time:" + (time - lastSendCalculateTime));
                        if (IRateLimitable.State.UNLIMITED == limitState) {
                            mOptions.getRateCalculator().calculateSmoothRateEnd();
                            DuboxLog.d(TAG, "限速完成或未限速");
                        }
                        // DuboxLog.d(TAG, "transferData smoothRate:" + rate);
                    } else {
                        rate = -1L;
                    }

                    DuboxLog.d(TAG, "deltaSizeSum:" + deltaSizeSum + ",block.completeSize:" + block.completeSize
                            + " ,fileSize:" + mFileInfo.fileSize);
                    calculate(deltaSizeSum, rate);
                    // 计算传输进度

                    deltaSizeSum = 0L;
                    if (IRateLimitable.State.UNLIMITED == limitState) {
                        // 分片限速过程中，不更新最后计算速度时间和重置平滑速度计算的分子，等待一次限速完成后
                        lastSendCalculateTime = time;
                    }
                }
            }

            if (isPause) {
                DuboxLog.d(TAG, "transferData isPause:" + mTaskId);
                throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
            }
            DuboxLog.d(TAG, "transferData done");
        } catch (IOException e) {
            DuboxLog.d(TAG, "transferData failed:" + mTaskId + " ,exception:" + e);
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + e.getMessage());
        } catch (IllegalArgumentException e){
            DuboxLog.d(TAG, "transferData failed:" + mTaskId + " ,exception:" + e);
            throw new Retry(OtherErrorCode.RETRY_ILLEGAL_ARGUMENT_EXCEPTION, "ArgumentException " + e.getMessage());
        }
    }


    /**
     * 传输线程
     * @author sunqi01
     */
    private class TransmitThread extends Thread {
        private final TransmitBlock mBlock;

        TransmitThread(TransmitBlock block) {
            this.mBlock = block;
        }

        @Override
        public void run() {
            transmit(mBlock);
        }
    }

    /**
     * 业务日志回捞
     *
     * @param log
     */
    protected void feedbackMonitorLog(String log) {
        String s = log + ":SingleThreadMultiPartDownloadTransmitter:";
        if (mFileInfo != null) {
            s = s + mFileInfo.getServer(false) + " ";
        }
        if (mFileInfo != null && mFileInfo.destinationPath != null) {
            s = s + mFileInfo.destinationPath.localUrl();
        }
        DriveContext.reportFeedbackmonitorDownloadLog(s);
    }

    /**
     * 业务日志回捞
     */
    protected void feedbackMonitorError(int errNo, String errorMsg) {
        String s = errorMsg + ":SingleThreadMultiPartDownloadTransmitter:";
        if (mFileInfo != null) {
            s = s + mFileInfo.getServer(false) + " ";
        }
        if (mFileInfo != null && mFileInfo.destinationPath != null) {
            s = s + mFileInfo.destinationPath.localUrl();
        }
        DriveContext.reportFeedbackmonitorDownloadError(errNo, s);
    }
}