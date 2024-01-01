package com.moder.compass.log.transfer;

import android.util.Pair;

import com.dubox.drive.base.network.NetworkUtil;
import com.moder.compass.base.utils.GlobalConfigKey;
import com.moder.compass.BaseApplication;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.moder.compass.log.ILogField;
import com.moder.compass.log.storage.db.LogContract;

/**
 * Created by liuliangping on 2016/3/20.
 */
public abstract class TransferLog implements ILogField {
    private static final String TAG = "TransferLog";
    /**
     * 文件大小类型
     */
    private static final int FILE_SIZE_TYPE_SMALL = 0;
    private static final int FILE_SIZE_TYPE_MIDDLE = 1;
    private static final int FILE_SIZE_TYPE_NORMAL = 2;
    private static final int FILE_SIZE_TYPE_LARGE = 3;
    private static final int FILE_SIZE_TYPE_HUGE = 4;
    /**
     * 文件大小阈值
     */
    private static final int FILE_SIZE_10M = 10485760;
    private static final int FILE_SIZE_100M = 104857600;
    private static final int FILE_SIZE_500M = 524288000;
    private static final long FILE_SIZE_2G = 2147483648L;

    protected long mStartTime = 0L;
    protected long mStartPosition = 0L;
    protected long mEndTime = 0L;
    protected long mTransferFileSize = 0L;
    protected int mPcsErrorCode = 0;
    protected int mHttpErrorCode = 0;
    protected int mOtherErrorCode = 0;
    protected String mOtherErrorMessage;
    protected String mRequestUrl;
    protected int mTaskFinishState = 0;
    protected String mLocalPath;
    protected String mRemoteUrl;
    protected long mFileSize = 0L;
    protected long mBlockSize = 0L;
    protected long mSpeedLimit = 0L;
    protected String mHttpRange;
    protected long mRate;
    protected ITransferCalculable mTransferCalculable;

    // block info
    protected String mXbsRequestId;
    protected String mXPcsRequestId;
    protected String mServerHost;
    protected String mServerIp;
    protected String mNetworkType;

    /**
     * 当前上传的类型是文件纬度还是块纬度
     */
    protected LogUploadType mCurrentUploadType = null;

    protected final String mUid;
    protected int mBlockSum;
    protected int mNeedBlockSum;
    protected String mLogTaskId;

    // 关于瞬时速度
    protected long mInstantEndTime;
    protected long mInstantEndSize;
    // 4M = 4 * 1024 * 1024
    private final long mNeedCalculateSize = 4194304L;
    // 只在大于4M后计算一次
    private boolean mCalulateOnce = false;
    private long mInstantSpeed;

    private int mIsSDKTransfer = 0;

    private int mUploadType = 0;

    private int mRapidType = 0;

    TransferFieldKey.FileTypeKey.DownloadType mDownloadType = TransferFieldKey
            .FileTypeKey.DownloadType.Normal;

    public TransferLog(String uid) {
        mUid = uid;
    }

    public void setUploadType(int uploadType) {
        this.mUploadType = uploadType;
    }

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public void setStartPosition(long startPosition) {
        this.mStartPosition = startPosition;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    public void setOffsetSize(long transferFileSize) {
        this.mTransferFileSize = transferFileSize;
    }

    public void setFileSize(long size) {
        this.mFileSize = size;
    }

    public void setBlockSize(long size) {
        this.mBlockSize = size;
    }

    public void setPcsErrorCode(int errorCode) {
        this.mPcsErrorCode = errorCode;
    }

    public void setHttpErrorCode(int errorCode) {
        this.mHttpErrorCode = errorCode;
    }

    public void setOtherErrorCode(int errorCode) {
        this.mOtherErrorCode = errorCode;
    }

    public void setOtherErrorMessage(String msg) {
        this.mOtherErrorMessage = msg;
    }

    public void setRequestUrl(String requestUrl) {
        this.mRequestUrl = requestUrl;
    }

    public void setTaskFinishStates(int taskFinishStates) {
        this.mTaskFinishState = taskFinishStates;
    }

    public void setLocalPath(String path) {
        this.mLocalPath = path;
    }

    public void setRapidType(int mRapidType) {
        this.mRapidType = mRapidType;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.mRemoteUrl = remoteUrl;
    }

    public void setCurrentUploadType(LogUploadType currentUploadType) {
        this.mCurrentUploadType = currentUploadType;
    }

    public void setSpeedLimit(long speedLimit) {
        this.mSpeedLimit = speedLimit;
    }

    public void setHttpRange(String httpRange) {
        this.mHttpRange = httpRange;
    }

    // block info
    public void setXbsRequestId(String xbsRequestId) {
        this.mXbsRequestId = xbsRequestId;
    }

    public void setPcsRequestId(String xPcsRequestId) {
        this.mXPcsRequestId = xPcsRequestId;
    }

    public void setServerHost(String serverHost) {
        this.mServerHost = serverHost;
    }

    public void setServerIp(String serverIp) {
        this.mServerIp = serverIp;
    }

    public String getServerIp() {
        return mServerIp;
    }

    public void setFileRate(long fileRate) {
        this.mRate = fileRate;
    }

    public void setTransferCalculable(ITransferCalculable transferCalculable) {
        this.mTransferCalculable = transferCalculable;
    }

    public void initNetWorkType() {
        this.mNetworkType = NetworkUtil.getNetworkInfo(BaseApplication.getInstance());
    }

    public void setBlockSum(int blockSum) {
        this.mBlockSum = blockSum;
    }

    public void setNeedBlockSum(int needBlockSum) {
        mNeedBlockSum = needBlockSum;
    }

    public void setLogTaskId(String logTaskId) {
        mLogTaskId = logTaskId;
    }

    public void setIsSDKTransfer(boolean isSDKTransfer) {
        if (isSDKTransfer) {
            mIsSDKTransfer = 1;
        }
    }

    public void setTransferType(TransferFieldKey.FileTypeKey.DownloadType type) {
        mDownloadType = type;
    }

    public int getUploadType() {
        return mUploadType;
    }

    public int getBlockSum() {
        return mBlockSum;
    }

    public int getNeedBlockSum() {
        return mNeedBlockSum;
    }

    @Override
    public long getStartTime() {
        return mStartTime;
    }

    @Override
    public long getEndTime() {
        return mEndTime;
    }

    @Override
    public long getTransferSize() {
        return mTransferFileSize - mStartPosition;
    }

    @Override
    public int getPcsErrorCode() {
        return mPcsErrorCode;
    }

    @Override
    public int getHttpErrorCode() {
        return mHttpErrorCode;
    }

    @Override
    public int getOtherErrorCode() {
        return mOtherErrorCode;
    }

    @Override
    public String getOtherErrorMessage() {
        return mOtherErrorMessage;
    }

    @Override
    public String getRequestUrl() {
        return mRequestUrl;
    }

    /**
     * 秒传是1；非秒传是0
     * @return
     */
    public int getRapidType() {
        return mRapidType;
    }

    /**
     * 成功是1，失败：2, 暂停：3, 删除: 4
     *
     * @return
     */
    @Override
    public int getFinishStates() {
        return mTaskFinishState;
    }

    public long getStartPosition() {
        return mStartPosition;
    }

    @Override
    public String getFieldSeparator() {
        return TransferFieldKey.FIELD_SEPARATOR;
    }

    public abstract String getFileFid();

    public abstract String getOpValue();

    public abstract String getTransferByteKey();

    public abstract String getTransferTimeKey();

    public abstract String getLogUploadType();

    public int getTransferType(){
        return mDownloadType.getValue();
    }

    public int getIsSDKTransfer() {
        return mIsSDKTransfer;
    }

    public enum LogUploadType {
        FILE, BLOCK_SUCCESS, BLOCK_FAIL;
    }

    public String getFileName() {
        return FileUtils.getFileName(mLocalPath);
    }

    public long getFileSize() {
        return mFileSize;
    }

    /**
     * 文件大小类型
     * @return 0=(0-10M) 1=(10-100M）2=(100-500M）3=(500-2G）4=2G以上
     */
    public int getFileSizeType() {
        if (mFileSize > FILE_SIZE_2G) {
            return FILE_SIZE_TYPE_HUGE;
        }
        if (mFileSize > FILE_SIZE_500M) {
            return FILE_SIZE_TYPE_LARGE;
        }
        if (mFileSize > FILE_SIZE_100M) {
            return FILE_SIZE_TYPE_NORMAL;
        }
        if (mFileSize > FILE_SIZE_10M) {
            return FILE_SIZE_TYPE_MIDDLE;
        }
        return FILE_SIZE_TYPE_SMALL;
    }

    public long getBlockSize() {
        return mBlockSize;
    }

    @Override
    public String getClientIp() {
        return GlobalConfig.getInstance().getString(GlobalConfigKey.CLIENT_IP);
    }

    /**
     * 获取uid
     *
     * @return
     */
    @Override
    public String getUid() {
        return mUid;
    }

    public long getSpeedLimit() {
        return mSpeedLimit;
    }

    public String getHttpRange() {
        return mHttpRange;
    }

    public String getXbsRequestId() {
        return mXbsRequestId;
    }

    public String getXPcsRequestId() {
        return mXPcsRequestId;
    }

    public String getServerHost() {
        return mServerHost;
    }

    /**
     * 单个文件的速度
     *
     * @return
     */
    public long getFileRate() {
        return mRate;
    }

    public Pair<Integer, Long> getTransferSchedulerInfo() {
        if (mTransferCalculable != null) {
            return mTransferCalculable.calculateTransferTask();
        }

        return null;
    }


    public String isVideo() {
        return FileType.isVideo(mLocalPath) ? LogContract.YES : LogContract.NO;
    }

    public String getNetWorkType() {
        return mNetworkType;
    }

    public String getLogTaskId() {
        return mLogTaskId;
    }

    /**
     * 约定每传输大于4M后，即可计算瞬时速度
     * @param endInstantSize
     * @return
     */
    public boolean isCanCalculateInstantSpeed(long endInstantSize) {
        if (mCalulateOnce) {
            return false;
        }
        boolean saveOnce = endInstantSize - getStartPosition() > mNeedCalculateSize;
        if (saveOnce) {
            mInstantEndSize = endInstantSize;
            mInstantEndTime = System.currentTimeMillis();
            mCalulateOnce = true;
        }
        return saveOnce;
    }

    /**
     * 获取当前任务传输4M左右的平均速度
     * @return
     */
    public long getInstantSpeed() {
        if (!mCalulateOnce) {
            return 0L;
        }
        if (mInstantSpeed > 0L) {
            return mInstantSpeed;
        }

        mInstantSpeed = (mInstantEndSize - getStartPosition()) / getIntervalTime(mInstantEndTime, getStartTime());
        return mInstantSpeed > 0L ? mInstantSpeed : 0L;
    }

    private long getIntervalTime(long end, long start) {
        long castTime = (long) Math.ceil((double) (end - start) / 1000);
        return castTime > 0L ? castTime : 1L;
    }
}
