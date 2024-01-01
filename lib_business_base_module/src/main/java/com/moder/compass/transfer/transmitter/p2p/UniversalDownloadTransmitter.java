package com.moder.compass.transfer.transmitter.p2p;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.statistics.EventStatisticsKt;
import com.moder.compass.statistics.StatisticsKeysKt;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.util.TransferUtil;
import com.cocobox.library.ErrorCode;
import com.cocobox.library.Key;
import com.cocobox.library.Operation;
import com.cocobox.library.P2P;
import com.cocobox.library.TaskCreateInfo;
import com.cocobox.library.TaskRunningInfo;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.Constants;
import com.moder.compass.base.storage.config.ConfigAlertText;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.net.HttpParams;
import com.moder.compass.log.transfer.TransferFieldKey;
import com.moder.compass.transfer.storage.DownloadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.ErrorMessageHelper;
import com.moder.compass.transfer.transmitter.IDlinkExpireTimeProcessor;
import com.moder.compass.transfer.transmitter.PCSTransmitErrorCode;
import com.moder.compass.transfer.transmitter.SDKDownloadTransmitter;
import com.moder.compass.transfer.transmitter.TransferDownloadCheckHelper;
import com.moder.compass.transfer.transmitter.TransferLogUtil;
import com.moder.compass.transfer.transmitter.TransmitBlock;
import com.moder.compass.transfer.transmitter.TransmitterOptions;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.Map;

import rubik.generate.context.dubox_com_pavobox_drive.DriveContext;

/**
 * Created by liuliangping on 2017/9/5.
 */

public class UniversalDownloadTransmitter extends SDKDownloadTransmitter {
    private static final String TAG = "UniversalDownloadTransmitter";

    private static final String SDK_DEFAULT_FGID = "00000000000000000000000000000000";
    private static final String SDK_KEY_DLINK = "dlink";
    private static final String SDK_KEY_MD5 = "md5";
    private static final String SDK_KEY_DL_PARAM = "download_url_param";

    private final OnP2PTaskListener mOnP2PTaskListener;
    private final IDlinkExpireTimeProcessor mProcessor;

    private long mTaskHandle = 0L;
    private static final long UPDATE_THRESHOLD = Constants.CALCULAT_INTERVAL;
    private boolean mIsError = false;
    /**
     * 是否下完了
     */
    private volatile boolean mIsFinish;
    /**
     * 查询下载进度的线程
     */
    private QueryProgressThread mQueryProgressThread;
    /**
     * 删除任务操作枚举
     */
    private Operation mDeleteValue;
    /**
     * 任务在数据库中标识
     */
    private final Uri mTasksUri;
    /**
     * 操作下载数据库帮助类
     */
    private final DownloadTaskProviderHelper mProviderHelper;
    private long mP2PCreateTime = 0L;
    /**
     * 一次检查是否p2p任务
     */
    private boolean mOnceCheckP2PTask;

    private ConfigAlertText forbiddenAlertConfig;

    private SDKTransmitterTask mSDKTransmitterTask;

    /**
     * 停止
     */
    private boolean isStop = false;

    /**
     * 限速值，0表示不限速，单位KB/s
     */
    private long mThresholeInKB = -1;

    private boolean mFirstGetRunningInfo = true;


    /**
     * 整合下载
     * @param resolver ContentResolver
     * @param bduss 用户bduss
     * @param uid   用户uid
     * @param taskId 任务ID
     * @param options 传输器选项
     * @param sdkTransmitterTask 任务信息
     * @param onP2PTaskListener 任务状态的回调
     * @param processor 外链任务超时处理器
     */
    public UniversalDownloadTransmitter(ContentResolver resolver, String bduss, String uid,
                                        int taskId, TransmitterOptions options,
                                        SDKTransmitterTask sdkTransmitterTask, OnP2PTaskListener onP2PTaskListener,
                                        IDlinkExpireTimeProcessor processor) {
        super(taskId, options, bduss, uid, resolver);
        mSDKTransmitterTask = sdkTransmitterTask;
        mTasksUri = TransferContract.DownloadTasks.buildUri(mBduss);
        mProviderHelper = new DownloadTaskProviderHelper(mBduss);
        mProcessor = processor;
        mOnP2PTaskListener = onP2PTaskListener;
        addP2PSDKParameter();
        mInstantDownloadLog.setIsSDKTransfer(true);

        mTransferLog.setLocalPath(sdkTransmitterTask.getLocalFile().localUrl());
    }

    @Override
    protected void prepareTransmit() {
        // Target30 以后不需要手动创建文件夹， 30一下沿用老方案
        if (!Target30StorageKt.isPartitionStorage()) {
            FileUtils.createParentFileDir(mSDKTransmitterTask.getLocalFile().localUrl());
        }
        forbiddenAlertConfig = new ConfigAlertText("");
    }

    @Override
    protected void transmit(TransmitBlock transmitBean) {
        try {
            // 下载之前判断target30产生的uri是否为null ,如果为null，则下载失败
            if (TextUtils.isEmpty(mSDKTransmitterTask.getLocalFile().localUrl())) {
                callBackError(OtherErrorCode.ERROR_TARGET30_URI_NULL);
                EventStatisticsKt.statisticActionEvent(StatisticsKeysKt.MEDIASTORE_CREATE_LOCAI_URL_NULL);
                return;
            }
            final String p2pServerPath;
            // 非分区存储的时候，可以做文件copy操作
            if (!Target30StorageKt.isPartitionStorage() && checkDownloadAndPreviewTask()) {
                // 删除之前的临时文件，通知下载完成
                String sdkTempPath = mSDKTransmitterTask.getTempFile().localUrl();
                FileUtils.delete(sdkTempPath);
                callBackSuccess();
                return;
            }
            if (TextUtils.isEmpty(mSDKTransmitterTask.getDuboxPath()) && mProcessor == null) {
                p2pServerPath = mSDKTransmitterTask.getServerPath();
            } else {
                String dlinkPath = getDlinkPath();
                p2pServerPath = TextUtils.isEmpty(dlinkPath) ? mSDKTransmitterTask.getDuboxPath() : dlinkPath;
            }
            // 下载路径, 临时文件在P2PTarget30FileProcess中创建
            String path = mSDKTransmitterTask.getLocalFile().localUrl();
            DuboxLog.d(TAG, "localPath:" + path + " ,p2pServerPath:" + p2pServerPath);
            TaskCreateInfo info = createInfo(p2pServerPath, path);
            mP2PCreateTime = System.currentTimeMillis();
            P2P.getInstance().createUniversalTask(info);

            if (mQueryProgressThread != null) {
                mQueryProgressThread.interrupt();
                mQueryProgressThread = null;
            }
            mQueryProgressThread = new QueryProgressThread();
        } catch (StopRequestException e) {
            DuboxLog.d(TAG, "StopRequestException =" + e.getMessage(), e);
            if (isPause) {
                callBackPause();
                return;
            }
            P2P.getInstance().controlUniversalTask(mTaskHandle, Operation.TASK_STOP);
            DuboxLog.d(TAG, "e.mFinalStatus:" + e.mFinalStatus);
            callBackError(e.mFinalStatus);

        } catch (SecurityException e) {
            DuboxLog.d(TAG, "SecurityException =" + e.getMessage(), e);
            if (isPause) {
                callBackPause();
                return;
            }
            callBackError(ErrorCode.UNRESOLVED_ERROR);
        }
    }

    @Override
    public void remove(boolean isDeleteFile) {
        DuboxLog.d(TAG, "remove isDeleteFile:" + isDeleteFile);

        if (mTaskHandle > 0) {
            mDeleteValue = isDeleteFile ? Operation.TASK_DELETE_TASK_AND_FILES : Operation.TASK_DELETE;
        } else {
            mDeleteValue = isDeleteFile ? Operation.P2P_DELETE_TASK_AND_FILES : Operation.P2P_DELETE;
        }
        mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_REMOVE);
        pauseSDKTask();
    }

    @Override
    public void pause() {
        super.pause();
        pauseSDKTask();
    }

    @Override
    public void stop() {
        DuboxLog.i(TAG, "stop");
        isStop = true;
    }

    @Override
    protected void calculate(long size, long rate) {
        mOffsetSize = size;
        if (rate >= 0L) {
            mRate = rate;
        }

        if (mOptions != null && mOptions.getStatusCallback() != null) {
            final int result = mOptions.getStatusCallback().onUpdate(mOffsetSize, rate);

            // 如果更新数据库没效果，说明task已经被删除，提前停止传输，优化性能
            if (result <= 0) {
                DuboxLog.d(TAG, "pause since calculate failed");
                feedbackMonitorLog("pause since calculate failed");
                pause();
            }
        }
    }

    @Override
    public void onError(String s) {
        DuboxLog.d(TAG, "onError:" + s);
        // 错误处理
        callBackError(ErrorCode.UNRESOLVED_ERROR);
    }

    @Override
    public void onTaskCreate(String createId, long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "createId:" + createId + "; taskHandle:" + taskHandle + ";errorCode:" + errorCode);
        this.mTaskHandle = taskHandle;
        if (errorCode == ErrorCode.SUCCESS || ErrorCode.TASK_ERR_ALREADY_EXIST == errorCode) {
            StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.USE_P2P_DOWNLOAD_TIMES);
            updateSDKTaskState(true);
            P2P.getInstance().controlUniversalTask(mTaskHandle, Operation.TASK_START);
            return;
        }

        StatisticsLogForMutilFields.getInstance().updateCount(
                StatisticsLogForMutilFields.StatisticsKeys.NO_USE_P2P_DOWNLOAD_TIMES);
        DuboxLog.e(TAG, "P2P sdk inner error ");
        if (ErrorCode.TASK_ERR_DISK_SPACE == errorCode
                || ErrorCode.TASK_ERR_FILESYSTEM_INCAPABLE == errorCode) {
            // sd卡没空间
            // 文件系统不支持，比如在FAT32上创建大于4G的文件
            callBackError(errorCode);
            return;
        }
        updateSDKTaskState(false);
        mOnP2PTaskListener.onP2PCreateFailed(mTaskId);
    }

    @Override
    public void onTaskStart(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onStart:" + taskHandle + "," + mTaskHandle + "; " + errorCode);
        if (errorCode != ErrorCode.SUCCESS) {
            // 错误处理
            callBackError(errorCode);
            return;
        }

        try {
            checkConnectivity();
            if (mQueryProgressThread != null) {
                isPause = false;
                mQueryProgressThread.start();
            }
        } catch (StopRequestException e) {
            if (ConnectivityState.isConnected(BaseApplication.getInstance())) {
                DuboxLog.d(TAG, "e.mFinalStatus:" + e.mFinalStatus);
            } else {
                DuboxLog.d(TAG, "e.mFinalStatus:TransmitterConstant.NETWORK_NO_CONNECTION)");
            }
            P2P.getInstance().controlUniversalTask(mTaskHandle, Operation.TASK_STOP);
            notifyFailToOptions(e.mFinalStatus);
            callBackPause();
        }
    }

    @Override
    public void onTaskStop(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onStop:" + taskHandle + "," + errorCode);

        if (mDeleteValue != null && mDeleteValue.value > ErrorCode.SUCCESS.value) {
            P2P.getInstance().controlUniversalTask(mTaskHandle, mDeleteValue);
            mDeleteValue = null;
        }
        mOnP2PTaskListener.onP2PStop(String.valueOf(mTaskId));
    }

    @Override
    public void onTaskPause(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onPause:" + taskHandle + "," + errorCode);
        isPause = true;
    }

    @Override
    public void onTaskDeleteWithoutFile(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskDeleteWithoutFile:" + taskHandle + "," + errorCode);
    }

    @Override
    public void onTaskDeleteAndFile(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskDeleteAndFile:" + taskHandle + "," + errorCode);
    }

    @Override
    public void onTaskGetTaskInfo(long taskHandle, TaskRunningInfo info, ErrorCode errorCode) {
        saveTransferLog(info);
        if (errorCode != ErrorCode.SUCCESS) {
            DuboxLog.d(TAG, "onTaskGetTaskInfo is error:" + errorCode
                    + ", taskHandle:" + taskHandle);
            checkPcsError(info.pcsError, info.pcsErrorInfo);
            callBackError(errorCode);
            return;
        }

        boolean isCrackUser = false;
        if (!TextUtils.isEmpty(info.pcsType)) {
            isCrackUser = info.pcsType.equals(Account.CRACK_USER);
        }
        Account.INSTANCE.setCrackUser(isCrackUser);

        if (!mOnceCheckP2PTask && isConfirmP2PTask(info)) {
            TransferFieldKey.FileTypeKey.DownloadType type;
            mOnceCheckP2PTask = true;
            if (isP2PTask(info)) {
                updateP2PTaskState();
                type = TransferFieldKey.FileTypeKey.DownloadType.P2PDownload;
            } else {
                type = TransferFieldKey.FileTypeKey.DownloadType.Normal;
            }
            mInstantDownloadLog.setTransferType(type);
            mTransferLog.setTransferType(type);
        }

        DuboxLog.d(TAG,
                "p2pDownloadSpeed:" + info.p2pDownloadSpeed + ",httpDownloadSpeed:" + info.httpDownloadSpeed
                        + ",p2pDownloadedSize:" + info.p2pDownloadedSize + ",downloadedSize:" + info.downloadedSize
                        + ",downloadCompleted:" + info.downloadCompleted + " ,fileSize:" + info.fileSize
                        + " ,fgid:" + info.fgid + " ,m3U8ConvertedPercentage:" + info.m3U8ConvertedPercentage
                        + " ,sl: " + info.sl + " ,bsRequestId:" + info.bsRequestId
                        + " ,pcsRequestId:" + info.pcsRequestId + " ,url:" + info.requestUrl
                        + " ,clientIP:" + info.clientIp);

        long sumRate = info.httpDownloadSpeed + info.p2pDownloadSpeed;
        if (sumRate > 0L && mP2PCreateTime > 0L) {
            StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.P2P_START_COST_TIME,
                    String.valueOf(System.currentTimeMillis() - mP2PCreateTime));
            mInstantDownloadLog.setStartPosition(info.downloadedSize);
            mInstantDownloadLog.setStartTime(mP2PCreateTime);
            mP2PCreateTime = 0L;
        }

        if (mOptions.getRateLimiter() != null) {
            // 速度传入限速器，用于试用加速等逻辑
            mOptions.getRateLimiter().limit(sumRate, true);
            if (sumRate > 0 && mThresholeInKB == -1) {
                // p2p返回的是B/s
                mThresholeInKB = info.sl / 1024;
                mOptions.getRateLimiter().updateThreshold(mThresholeInKB);
            }
        }

        calculate(info.downloadedSize, sumRate);

        if (Float.valueOf(info.m3U8ConvertedPercentage).intValue() == 100) {
            // 原画文件以及流畅转码已经结束的情况下
            mTransferLog.setOffsetSize(mOffsetSize);
            mTransferLog.setFileRate(sumRate);
            if (mInstantLogGenerator != null && mInstantDownloadLog.isCanCalculateInstantSpeed(mOffsetSize)) {
                mInstantSpeed = mInstantDownloadLog.getInstantSpeed();
                if (mInstantSpeed > 0L) {
                    mLogTaskManager.addLogTask(mInstantLogGenerator, mInstantDownloadLog);
                    mInstantLogGenerator = null;
                }
            }
        }

        if (info.downloadCompleted) {
            DuboxLog.d(TAG, "transmit:: all done");
            Target30StorageKt.rename(mSDKTransmitterTask.getTempFile(),
                    mSDKTransmitterTask.getLocalFile(), false);
            mIsFinish = true;
            callBackSuccess();
        }
    }

    private void saveTransferLog(TaskRunningInfo info) {
        mTransferLog.setPcsErrorCode(info.pcsError);
        mTransferLog.setHttpErrorCode(info.httpCode);
        mTransferLog.setSpeedLimit(info.sl);
        if (!TextUtils.isEmpty(info.serverIp)) {
            mTransferLog.setServerIp(info.serverIp);
        }
        TransferLogUtil.saveClientIp(info.clientIp);

        if (!TextUtils.isEmpty(info.bsRequestId)) {
            mTransferLog.setXbsRequestId(info.bsRequestId);
        }
        if (!TextUtils.isEmpty(info.pcsRequestId)) {
            mTransferLog.setPcsRequestId(info.pcsRequestId);
        }
        if (!TextUtils.isEmpty(info.requestUrl)) {
            mTransferLog.setRequestUrl(info.requestUrl);
        }
        if (mFirstGetRunningInfo) {
            mTransferLog.initNetWorkType();
            mTransferLog.setStartTime(mP2PCreateTime);
            mTransferLog.setFileSize(info.fileSize);
            mTransferLog.setStartPosition(info.downloadedSize);
            mFirstGetRunningInfo = false;
        }
    }


    private void checkPcsError(int pcsError, String pcsErrorInfo) {
        if (pcsError == PCSTransmitErrorCode.SERVER_FORBIDDEN_USER
                && forbiddenAlertConfig.isShowForbiddenAlert) {
            errMsgForShow = !TextUtils.isEmpty(pcsErrorInfo) ? pcsErrorInfo :
                    forbiddenAlertConfig.forbiddenUserDownloadAlertText;
        }
        ErrorMessageHelper.checkPCSErrorNo(pcsError);
    }

    @Override
    public void onGetPlayM3u8Path(String createID, String path, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onGetPlayM3u8Path createID:" + createID + " ,path:" + path);
        callBackSuccess();
    }

    private void callBackPause() {
        if (mOptions.isRateCalculateEnable()) { // 计算传输速率
            resetRateCalculator();
        }
        if (!mIsError){
            updatePauseTransferLog();
        }
    }

    private void updatePauseTransferLog() {
        if (mTransferLog.getEndTime() == 0) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_PAUSE);
        }

        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    private void callBackSuccess() {
        DuboxLog.i(TAG, "callBackSuccess");

        if (mOptions.getStatusCallback() != null) {
            DuboxLog.i(TAG, "onSuccess");
            mOptions.getStatusCallback().onSuccess(null);
        }

        if (mOptions.isRateCalculateEnable()) { // 计算传输速率
            resetRateCalculator();
        }
        StatisticsLogForMutilFields.getInstance()
                .updateCount(StatisticsLogForMutilFields.StatisticsKeys.P2P_DOWNLOAD_SUCCESS);

        if (mTransferLog.getEndTime() == 0) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FINISH);
        }


        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    private void callBackError(ErrorCode errorCode) {
        feedbackMonitorError(errorCode.value, errorCode.name());
        DuboxLog.i(TAG, "callBackError:: errorCode = " + errorCode);
        mIsError = true;
        // 匹配p2psdk和网盘原有下载的错误
        final int errno = UniversalDownloadUtils.convertToClientErrCode(errorCode);
        updateFailTransferLog(errorCode.value);
        notifyFailToOptions(errno);
    }

    private void callBackError(int errorCode) {
        feedbackMonitorError(errorCode, "p2p");
        DuboxLog.i(TAG, "callBackError:: errorCode = " + errorCode);
        mIsError = true;
        // 匹配p2psdk和网盘原有下载的错误
        updateFailTransferLog(errorCode);
        notifyFailToOptions(errorCode);
    }

    private void notifyFailToOptions(int errno) {
        isPause = true;
        if (mOptions.getStatusCallback() != null) {
            mOptions.getStatusCallback().onFailed(errno, errMsgForShow);
        }

        if (mOptions.isRateCalculateEnable()) { // 计算传输速率
            resetRateCalculator();
        }
    }

    private void updateFailTransferLog(int errno) {
        StatisticsLogForMutilFields.getInstance()
                .updateCount(StatisticsLogForMutilFields.StatisticsKeys.P2P_DOWNLOAD_FAIL);

        if (mTransferLog.getEndTime() == 0) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FAIL);
        }
        mTransferLog.setOtherErrorCode(errno);
        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    private TaskCreateInfo createInfo(String p2pServerPath, String localPath)
            throws StopRequestException {
        TaskCreateInfo info = new TaskCreateInfo();
        info.createId = String.valueOf(mTaskId);
        info.serverPath = p2pServerPath;
        info.localPath = localPath;

        try {
            final JSONObject jo = new JSONObject();
            if (mProcessor != null) {
                Map<String, String> paramMap = mProcessor.getDlinkParameters();
                if (paramMap != null && paramMap.size() > 0) {
                    for (Map.Entry<String, String> e : paramMap.entrySet()) {
                        jo.put(e.getKey(), e.getValue());
                    }
                }
                jo.put(SDK_KEY_DLINK, mSDKTransmitterTask.getServerPath());
            }
            if (!TextUtils.isEmpty(mSDKTransmitterTask.getServerMD5())) {
                jo.put(SDK_KEY_MD5, String.valueOf(mSDKTransmitterTask.getServerMD5()));
            }
            jo.put(SDK_KEY_DL_PARAM, getSDKExtraDownloadParam());

            info.extraInfo = jo.toString();
        } catch (JSONException e) {
            DuboxLog.d(TAG, "createInfo", e);
        }
        info.fileSize = mSDKTransmitterTask.getSize();
        info.isTs = mSDKTransmitterTask.isTs();
        info.isShared = mSDKTransmitterTask.isShare();

        DuboxLog.d(TAG, "createInfo:" + info + " ,isShared:" + info.isShared + " ,isTs:" + info.isTs
                + " ,info.serverPath:" + info.serverPath + " filesize: " + info.fileSize
                + " ,info.localPath:" + info.localPath + " ,extraInfo: " + info.extraInfo);
        return info;
    }

    private String getSDKExtraDownloadParam() {
        HttpParams params = new HttpParams();
        return params.toString();
    }

    private void pauseSDKTask() {
        DuboxLog.i(TAG, "pauseSDKTask mTaskId:" + mTaskId);
        isPause = true;
        P2P.getInstance().controlUniversalTask(mTaskHandle, Operation.TASK_STOP);
    }

    /**
     * 更新task的状态
     */
    private void updateSDKTaskState(boolean isSDKTask) {
        final ContentValues values = new ContentValues(1);
        values.put(TransferContract.Tasks.IS_DOWNLOAD_SDK_TASK, isSDKTask ? TransferContract.Tasks.YES :
                TransferContract.Tasks.NO);
        mProviderHelper.updateTask(mContentResolver, mTasksUri, mTaskId, values);
        mOnP2PTaskListener.onSDKTaskTypeSet(mTaskId,isSDKTask);
        DuboxLog.d(TAG, "set database IS_DOWNLOAD_SDK_TASK " + isSDKTask
                + "mTasksUri: " + mTasksUri.toString() + " taskid: " + mTaskId);
    }

    /**
     * 是否已经确定P2P任务
     * @param info 运行中任务信息
     * @return true已经判断是否为P2P，false还未判断出是否为P2P
     */
    private boolean isConfirmP2PTask(TaskRunningInfo info){
        return !TextUtils.isEmpty(info.fgid);
    }

    private boolean isP2PTask(TaskRunningInfo info) {
        return !info.fgid.equals(SDK_DEFAULT_FGID);
    }

    /**
     * 更新task为p2p任务
     */
    private void updateP2PTaskState() {
        final ContentValues values = new ContentValues(1);
        values.put(TransferContract.Tasks.IS_P2P_TASK, TransferContract.Tasks.YES);
        mProviderHelper.updateTask(mContentResolver, mTasksUri, mTaskId, values);
        mOnP2PTaskListener.onP2PTaskTypeSet(mTaskId,true);
        DuboxLog.d(TAG, "updateP2PTaskState");
        DuboxLog.d(TAG, "set database IS_P2P_TASK "
                + "mTasksUri: " + mTasksUri.toString() + " taskid: " + mTaskId);
    }


    /**
     * dlink下载需要给p2p传获取locatedownload拼接后的path
     *
     * @return 从dlink中获取path值
     */
    private String getDlinkPath() {
        String start = "/file/";
        int startIndex = mSDKTransmitterTask.getServerPath().indexOf(start);
        if (startIndex == -1) {
            return null;
        }

        int endIndex = mSDKTransmitterTask.getServerPath().indexOf("?", startIndex);
        if (endIndex == -1) {
            return null;
        }

        if (endIndex - start.length() <= startIndex) {
            return null;
        }

        String dlinkFileString = mSDKTransmitterTask.getServerPath().substring(startIndex + start.length(), endIndex);
        String dlinkQueryString = mSDKTransmitterTask.getServerPath().substring(endIndex + 1);

        return TextUtils.isEmpty(dlinkQueryString) ? dlinkFileString : dlinkFileString + "&" + dlinkQueryString;
    }

    /**
     * 检查下载目录或者预览目录里面是否有相同文件<br/>
     * 视频只检查文件名是否相同；非视频需要检查云端文件版本
     *
     * @return true: 不需要下载，false：需要下载
     */
    private boolean checkDownloadAndPreviewTask() {
        if (!TextUtils.isEmpty(mSDKTransmitterTask.getDuboxPath())) {
            return false;
        }

        TransferDownloadCheckHelper helper = new TransferDownloadCheckHelper(mBduss);
        Pair<String, Long> fileInfo = helper.getDownloadedFileInfo(mSDKTransmitterTask.getServerPath());

        if (fileInfo == null) {
            return false;
        }

        boolean isVideo = FileType.isVideo(mSDKTransmitterTask.getServerPath());
        // 传输任务没有云端MD5时，直接下载新版本。可能是视频，也可能是老版本覆盖安装时遗留的任务
        if (TextUtils.isEmpty(mSDKTransmitterTask.getServerMD5())) {
            File localFile = new File(mSDKTransmitterTask.getLocalFile().localUrl());
            if (!localFile.exists()) {
                File oppFile = new File(fileInfo.first);
                return oppFile.exists() && isVideo
                        && isHandledVideoDownload(fileInfo.first);
            }

            // 覆盖安装
            return true;
        }

        File localFile = new File(fileInfo.first);
        if (!localFile.exists()) {
            return false;
        }

        if (isVideo) {
            return isHandledVideoDownload(fileInfo.first);
        }
        return isHandledNotVideoDownload(helper, fileInfo.first, fileInfo.second, localFile.lastModified());
    }

    /**
     * @param localPath 本地路径
     * @return true：已经处理过 liulp
     */
    private boolean isHandledVideoDownload(String localPath) {
        // 由于预览的根目录不随着选择用户sdcard的位置变化而改变，所以无论从预览到下载或者下载到预览，只要根目录不是内存卡的目录都使用copy后删除
        if (TransferUtil.isSameRootPath(localPath, mSDKTransmitterTask.getLocalFile().localUrl())) {
            // 同存储卡的是移动不需要检查空间
            boolean result = FileUtils.move(localPath, mSDKTransmitterTask.getLocalFile().localUrl());
            DuboxLog.d(TAG, "move video result:" + result);
            return result;
        } else {
            checkStorage(mSDKTransmitterTask.getSize());

            // 如果存在目标文件的老版本，需要先删除，才能保证成功
            File desFile = new File(mSDKTransmitterTask.getLocalFile().localUrl());
            if (desFile.exists()) {
                desFile.delete();
            }

            boolean result = FileUtils.moveFile(localPath, mSDKTransmitterTask.getLocalFile().localUrl());
            DuboxLog.d(TAG, "copy video file and delete result:" + result);
            return result;
        }
    }

    /**
     * cdiff and sdiff
     *
     * @return true：已经处理过 liulp
     */
    private boolean isHandledNotVideoDownload(TransferDownloadCheckHelper helper, String localPath, long lastTime,
                                              long localLastTime) {
        // cdiff
        if (lastTime != localLastTime) {
            return false;
        }

        // sdiff
        // 1.传输的mServerMD5 同文件库cachefilelist最新的是否一致
        String cloudMd5 = helper.getCloudFileMd5(mSDKTransmitterTask.getServerPath());
        if (!TextUtils.equals(mSDKTransmitterTask.getServerMD5(), cloudMd5)) {
            return false;
        }

        // 2.传输的mServerMD5，同对方已经下载完成文件的md5是否一致
        String downloadMd5 = helper.getDownloadedFileMd5(mSDKTransmitterTask.getServerPath());
        if (!TextUtils.equals(mSDKTransmitterTask.getServerMD5(), downloadMd5)) {
            return false;
        }

        checkStorage(mSDKTransmitterTask.getSize());

        // 如果存在目标文件的老版本，需要先删除，才能保证copy成功
        File desFile = new File(mSDKTransmitterTask.getLocalFile().localUrl());
        if (desFile.exists()) {
            desFile.delete();
        }

        boolean result = FileUtils.copyFile(localPath, mSDKTransmitterTask.getLocalFile().localUrl());
        DuboxLog.d(TAG, "copyFile result:" + result);

        return result;
    }

    /**
     * 检查本地存储是否足够
     *
     * @param size 需要的空间大小
     */
    private void checkStorage(long size) {
        if (size > 0L) {
            boolean result = Target30StorageKt.isDownloadSpaceEnough(size,
                    mSDKTransmitterTask.getLocalFile().localUrl(), false);
            if (!result) {
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_SPACE_FULL);
                DuboxLog.d(TAG, "isSDCardEnough false");
            }
            DuboxLog.d(TAG, "isSDCardEnough true");
        }
    }

    /**
     * 在onStart的回调中查询下载的进度，开线程查询不阻塞p2p回调的线程
     */
    private class QueryProgressThread extends Thread {
        @Override
        public void run() {
            try {
                while (!isPause && !mIsFinish && !isStop && !QueryProgressThread.interrupted()) {
                    P2P.getInstance().getUniversalTaskInfo(mTaskHandle);
                    checkConnectivity();

                    SystemClock.sleep(UPDATE_THRESHOLD);

                    if (isPause) {
                        callBackPause();
                    }
                }
            } catch (StopRequestException e) {
                DuboxLog.d(TAG, "StopRequestException =" + e.getMessage(), e);

                if (e.mFinalStatus == TransmitterConstant.NETWORK_NO_CONNECTION
                        || e.mFinalStatus == TransmitterConstant.NETWORK_NOT_AVAILABLE) {
                    P2P.getInstance().setParameter(Key.NETWORK_TYPE, TransferUtil.P2P_NET_TYPE_NONE);
                }
                if (ConnectivityState.isConnected(BaseApplication.getInstance())) {
                    DuboxLog.d(TAG, "e.mFinalStatus:" + e.mFinalStatus);
                } else {
                    DuboxLog.d(TAG, "e.mFinalStatus:TransmitterConstant.NETWORK_NO_CONNECTION)");
                }
                P2P.getInstance().controlUniversalTask(mTaskHandle, Operation.TASK_STOP);
                notifyFailToOptions(e.mFinalStatus);
                callBackPause();
            }
        }
    }

    /**
     * 业务日志回捞
     *
     * @param log
     */
    protected void feedbackMonitorLog(String log) {
        String s = log + ":UniversalDownloadTransmitter:";
        if (mSDKTransmitterTask != null && mSDKTransmitterTask.getServerPath() != null) {
            s = s + mSDKTransmitterTask.getServerPath() + " ";
        }
        if (mSDKTransmitterTask != null && mSDKTransmitterTask.getLocalFile() != null) {
            s = s + mSDKTransmitterTask.getLocalFile().localUrl();
        }
        DriveContext.reportFeedbackmonitorDownloadLog(s);
    }

    /**
     * 业务日志回捞
     */
    protected void feedbackMonitorError(int errNo, String errorMsg) {
        String s = errorMsg + ":UniversalDownloadTransmitter:";
        if (mSDKTransmitterTask != null && mSDKTransmitterTask.getServerPath() != null) {
            s = s + mSDKTransmitterTask.getServerPath() + " ";
        }
        if (mSDKTransmitterTask != null && mSDKTransmitterTask.getLocalFile() != null) {
            s = s + mSDKTransmitterTask.getLocalFile().localUrl();
        }
        DriveContext.reportFeedbackmonitorDownloadError(errNo, s);
    }
}
