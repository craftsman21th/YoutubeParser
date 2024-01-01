package com.moder.compass.transfer.transmitter;

import static com.dubox.drive.kernel.util.ConStantKt.INT_2;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.CRC32;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.common.util.CommonParam;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.moder.compass.account.constant.AccountErrorCode;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.cloudfile.constant.DuboxErrorCode;
import com.dubox.drive.network.base.ServerResultHandler;
import com.dubox.drive.base.network.NetworkUtil;
import com.dubox.drive.base.network.StokenManager;
import com.dubox.drive.kernel.util.PhoneStatusKt;
import com.dubox.drive.cloudfile.io.CloudFileApi;
import com.dubox.drive.cloudfile.io.model.CloudFile;
import com.dubox.drive.cloudfile.io.model.Quota;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.cloudfile.storage.db.CloudFileProviderHelper;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.android.util.file.CloseUtils;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.monitor.battery.BatteryMonitor;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.android.util.network.NetWorkVerifier;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpParams;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.dubox.drive.kernel.util.DateUtil;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.kernel.util.encode.Base64Util;
import com.dubox.drive.kernel.util.encode.HexUtil;
import com.dubox.drive.kernel.util.encode.RC4Util;
import com.moder.compass.log.transfer.TransferFieldKey;
import com.moder.compass.log.transfer.TransferLog;
import com.moder.compass.log.transfer.UploadLog;
import com.moder.compass.log.transfer.UploadTransferLogGenerator;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.statistics.points.BaseStats;
import com.moder.compass.statistics.points.TransformStats;
import com.moder.compass.transfer.io.TransferApi;
import com.moder.compass.transfer.io.model.CreateFileResponse;
import com.moder.compass.transfer.io.model.PreCreateFileResponse;
import com.moder.compass.transfer.io.model.UploadUrlInfo;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.block.BlockUploadJob;
import com.moder.compass.transfer.transmitter.block.BlockUploadScheduler;
import com.moder.compass.transfer.transmitter.block.ConfigBlockUpload;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.constant.UploadExceptionCode;
import com.moder.compass.transfer.transmitter.locate.LocateUpload;
import com.moder.compass.transfer.transmitter.statuscallback.ITransferStatusCallback;
import com.moder.compass.transfer.transmitter.throwable.Retry;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;
import com.moder.compass.transfer.transmitter.util.SpeedUploadUtils;
import com.google.gson.Gson;
import com.media.vast.meta.IVastMetaListener;
import com.media.vast.meta.VastMeta;
import com.moder.compass.util.DynamicUploadBlockKt;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.exifinterface.media.ExifInterface;
import rubik.generate.context.dubox_com_pavobox_drive.DriveContext;

/**
 * 上传器
 *
 * @author 孙奇 <br/>
 * create at 2012-11-30 下午08:29:20
 */
public class UploadTransmitter extends Transmitter implements AccountErrorCode, DuboxErrorCode, PCSTransmitErrorCode {
    private static final String TAG = "UploadTransmitter";
    private static final String VIDEO_COMPRESS_TAG = "video_compress_tag";

    /**
     * 0 为不重命名（返回冲突）
     **/
    private static final int RTYPE_NORENAME = 0;

    /**
     * 2为path冲突且block_list不同才重命名， 默认为1
     **/
    private static final int RTYPE_FILE_RENAME = 2;

    /**
     * 3 为覆盖
     **/
    private static final int RTYPE_OVERRIDE = 3;

    /**
     * 文件上传分片大小(4M)
     */
    private static final long UPLOAD_BLOCK_SIZE = 1024 * 1024 * 4;
    private static final int UPLOAD_BYTES_SIZE = 1024 * 10;
    /**
     * 重试时间间隔
     */
    private static final int STEP_RETRY_DELAY = 1000;

    /**
     * 重试读取文件时间间隔
     */
    private static final int READ_RETRY_DELAY = 500;

    /**
     * 分片计算MD5时的buffer大小，经测试 16K大小性能表现比较好，再加大buffer大小性能提升不明显
     *
     * @author 孙奇 V 1.0.0 Create at 2013-2-20 下午11:57:10
     */
    private static final int CALL_MD5_SIZE = 1024 * 16;

    // 大于等于256K的文件前面256K算SliceMd5
    private static final long SLICE_SIZE = 256 * 1024;

    private static final String SQL_PLACEHOLDER = "=?";

    private long mLastModifiedTime;
    private long cTime = 0L;
    private long mTime = 0L;
    protected long fileSize = -1L;
    /**
     * 分别对不同文件大小，设置不同的分片规格：
     * 4G以下4M，8G以下8M，16G以下16M，32G以下32M
     */
    private long dynamicUploadBlockSize = UPLOAD_BLOCK_SIZE;
    protected RFile mLocalFile;

    protected List<String> mAllMd5List = new ArrayList<String>();
    private final List<Integer> mNeedSeqList = new ArrayList<Integer>();

    private volatile long lastUpdateClock = 0L;

    private boolean isEverUploaded = false;

    private boolean mWillBeOverride = false;

    public final int mSource;

    private Quota mQuota;
    /**
     * 目标服务器路径
     */
    private final String mRemotePath;

    /**
     * 用于聚合『最近使用』的参数，聚合策略：相同文件夹下的文件，图片、视频聚合为一类，其他类型为一类
     */
    private String mTargetPath;

    /**
     * 文件名字
     */
    protected String mFileName;

    private final ContentResolver mResolver;

    private final Uri mUri;

    protected final String mBduss;

    protected final String mUid;
    /**
     * percreate接口和rapidupload合并时需要的前256K文件内容md5
     */
    private String mSliceMd5;
    /**
     * percreate接口和rapidupload合并时需要的全文MD5 <br/>
     * 需要上传文件的全文md5
     */
    private String mContentMd5;
    /**
     * percreate接口和rapidupload合并时需要的crc32值
     */
    private long mContentCrc32;

    private String mSrcContentMd5;
    private String mSrcSliceMd5;
    private long mSrcContentCrc32;
    private long mSrcFileSize;

    /**
     * SuperFile2所用的upload id
     */
    private String mUploadId;

    /**
     * 共享给我的目录需要
     *
     * @since 8.0
     */
    private String mSign;

    /**
     * 压缩的质量，只用于图片和视频文件<br>
     * 对于图片,这个值相当于质量百分比，1-100;<br>
     * 对于视频，这个值赋值为如压缩成480P的视频，值为480<br>
     * 压缩后上传最后一步createFile时上报Server,用于Server通过质量去重（Server只保留最高品质的）。
     */
    private int mCurrentQuality;

    /**
     * 重试读取文件的次数
     */
    private int reReadNum = 0;

    public interface RapidSource {

        /**
         * 非秒传
         */
        int NORMAL_UPLOAD = 0;

        /**
         * 秒传
         */
        int Rapid_UPLOAD = 1;
    }

    /**
     * 上传来源
     *
     * @author libin 2015-11-10
     * @since 7.12.0
     */
    public interface Source {
        /**
         * 手动上传
         */
        int UPLOAD = 1;

        /**
         * 图片备份
         */
        int PHOTO_BACKUP = 4;

        /**
         * 视频备份
         */
        int VIDEO_BACKUP = 5;

        /**
         * 文件备份
         */
        int FILE_BACKUP = 3;
    }

    /**
     * 上传状态
     *
     */
    public interface State {
        /**
         * 备份/上传成功
         */
        int SUCCESS = 0;

        /**
         * 备份/上传失败
         */
        int FAIL = 1;
    }

    // 块纬度的日志
    private UploadLog mBlockTransferLog;

    private final StokenManager mStokenManager;

    private final Lock mCompressLock = new ReentrantLock();
    private final Condition mCompressCondition = mCompressLock.newCondition();

    /**
     * 分片并发上传-已上传数据大小
     */

    private final AtomicLong mBlockUploadLen = new AtomicLong(0);

    /**
     * 分片并发上传-瞬时上传文件大小
     */
    private final AtomicLong mDeltaBlockUploadLen = new AtomicLong(0);

    /**
     * 分片并发上传jobs
     */
    private final CopyOnWriteArrayList<BlockUploadJob> blockUploadJobs = new CopyOnWriteArrayList<>();

    /**
     * 分片并发上传-失败上传任务的<分片索引, 失败错误码>
     */
    private final ConcurrentHashMap<Integer, Throwable> mFailedErrorCodeBlockJobMap = new ConcurrentHashMap<>();

    public UploadTransmitter(int taskId, RFile localFile, String remotePath, String fileName,
                             TransmitterOptions options, ContentResolver resolver, Uri uri,
                             String bduss, String uid, int source, String uploadId) {
        super(taskId, options);
        this.mLocalFile = localFile;
        this.mRemotePath = remotePath;
        this.mFileName = fileName;
        mResolver = resolver;
        mUri = uri;
        mBduss = bduss;
        mUid = uid;
        mSource = source;
        mUploadId = uploadId;
        mLogGenerator = new UploadTransferLogGenerator();
        mTransferLog = new UploadLog(uid);
        mTransferLog.setLogTaskId(Account.INSTANCE.getUid() + "_" + System.currentTimeMillis());
        if (options.getTransferCalculable() != null) {
            mTransferLog.setTransferCalculable(options.getTransferCalculable());
        }

        mStokenManager = new StokenManager(mBduss);
    }

    public void setWillBeOverride(boolean willBeOverride) {
        mWillBeOverride = willBeOverride;
    }

    @Override
    protected void prepareTransmit() {
        DuboxLog.d(TAG, "prepareTransmit begin");
        isPause = false;
        isEverUploaded = false;
        initProgress();
        DuboxLog.d(TAG, "prepareTransmit done");

        mTransferLog.setStartPosition(0L);
        mTransferLog.setLocalPath(mLocalFile.localUrl());
        mTransferLog.setRemoteUrl(mRemotePath);
    }

    /**
     * 是否需要压缩
     *
     * @return
     */
    protected boolean needCompress() {
        return false;
    }

    protected void checkCondition() throws StopRequestException {
        checkPower();
        if (!ConnectivityState.isConnected(BaseApplication.getInstance())) {
            feedbackMonitorLog("checkCondition network_no_connection");
            throw new StopRequestException(TransmitterConstant.NETWORK_NO_CONNECTION,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.NETWORK_NO_CONNECTION));
        }
        checkWiFi();

    }

    @Override
    protected void transmit(TransmitBlock transmitBean) {
        DuboxLog.d(TAG, "mSource=" + mSource);
        DuboxLog.d(TAG, "transmit begin");
        try {
            while (retryTimes <= RETRY_MAX_TIMES) {
                try {
                    checkCondition();
                    DuboxLog.i(TAG, "transmit STEP0");
                    // handleCompress();
                    mCurrentQuality = 100;
                    DuboxLog.i(TAG, "transmit STEP1 prepareUploadInfo");
                    prepareUploadInfo();
                    getVideoExif(mLocalFile);

                    // 真正开始上传文件
                    DuboxLog.i(TAG, "transmit STEP2 preCreateFile");
                    mTransferLog.setStartTime(System.currentTimeMillis());
                    PreCreateFileResponse response = preCreateFile();

                    if (!CollectionUtils.isEmpty(mAllMd5List)) {
                        mTransferLog.setBlockSum(mAllMd5List.size());
                    }

                    if (!CollectionUtils.isEmpty(mNeedSeqList)) {
                        mTransferLog.setNeedBlockSum(mNeedSeqList.size());
                    }

                    if (response != null) {
                        // 命中秒传
                        feedbackMonitorLog("transmit 命中秒传");
                        DuboxLog.d(TAG, "命中秒传");
                        mTransferLog.setRapidType(RapidSource.Rapid_UPLOAD);
                        callBackSuccess(response.mRawString);
                        return;
                    }
                    DuboxLog.i(TAG, "transmit STEP3 pcsUploadFile");
                    pcsUploadFile();
                    DuboxLog.i(TAG, "transmit STEP4 createFile");
                    CreateFileResponse res = createFile();

                    if (mOptions.getStatusCallback() != null) {
                        callBackSuccess(res == null ? null : res.mRawString);
                        deleteCompressFile();
                    }

                    // 针对上传成功后二级文件列表不展示文件的问题，先手动插入以下
                    if (res != null) {
                        String bduss = Account.INSTANCE.getNduss();
                        CloudFile cloudFile = new CloudFile(FileUtils.getFileName(res.mName), res.mIsDir,
                                res.mSize, res.mPath, res.mMd5, String.valueOf(res.mFsId));
                        cloudFile.localCTime = cTime;
                        cloudFile.localMTime = mTime;

                        CloudFileProviderHelper helper = new CloudFileProviderHelper(bduss);
                        helper.insertFiles(BaseApplication.getContext(),
                                CloudFileContract.Files.buildFilesUri(bduss),
                                Collections.singletonList(cloudFile));
                    }
                    return;
                } catch (Retry e) {
                    // for retry
                    doRetry(e);
                }
            }
        } catch (StopRequestException e) {
            DuboxLog.d(TAG, "StopRequestException =" + e.getMessage(), e);
            deleteCompressFile();
            if (isPause) {
                callBackPause();
                return;
            }
            if (e.mFinalStatus == TransmitterConstant.NETWORK_VERIFY_CHECKING) {
                return;
            }
            //上传文件大小超限
            if (e.mFinalStatus == OtherErrorCode.ERROR_VIDEO_OVERSIZE && mResolver != null) {
                final ContentValues values = new ContentValues(INT_2);
                values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FAILED);
                values.put(TransferContract.Tasks.EXTRA_INFO_NUM,
                        TransferContract.UploadTasks.UPLOAD_VIDEO_OVER_SIZE);
                mResolver.update(TransferContract.UploadTasks.buildProcessingUri(mBduss), values,
                        TransferContract.Tasks._ID + SQL_PLACEHOLDER,
                        new String[] {String.valueOf(mTaskId)});

                return;
            }
            // 返回空间已满，需主动去拉取容量接口
            if (e.mFinalStatus == TransmitterConstant.NO_REMOTE_SPACE) {
                syncDuboxSpace();
            }

            if (!ConnectivityState.isConnected(BaseApplication.getInstance()) && mOptions.getStatusCallback() != null) {
                mOptions.getStatusCallback().onFailed(TransmitterConstant.NETWORK_NO_CONNECTION, null);
                // XXX 没有return？
            }

            callBackError(e.mFinalStatus, "transmit StopRequestException " + e.getMessage());
            DuboxLog.d(TAG, "transmit StopRequestException done");
            return;
        } catch (SecurityException e) {
            DuboxLog.e(TAG, "SecurityException", e);
            deleteCompressFile();
            if (isPause) {
                callBackPause();
                return;
            }
            callBackError(TransmitterConstant.NETWORK_REFUSE, "transmit SecurityException " + e.getMessage());
            DuboxLog.d(TAG, "transmit SecurityException done");
            return;
        } finally {
            DuboxLog.d(TAG, "transmit done");
        }
    }


    String mVideoInfo;

    /**
     * 设置videoInfo信息
     */
    private void getVideoExif(RFile localFile) {
        if (localFile.isVideo()) {
            ParcelFileDescriptor fd = localFile.fd(BaseApplication.getInstance(), "r");
            if (fd == null) {
                return;
            }
            DuboxLog.i(TAG, " videoPath = " + localFile.localUrl());
            final VastMeta vastMeta = new VastMeta();
            vastMeta.initVastMeta(fd.detachFd(), new IVastMetaListener() {
                @Override
                public void onProbeCompleted() {
                    try {
                        JSONObject videoJson = new JSONObject(vastMeta.getMetaInfo());
                        vastMeta.stopProbe();
                        vastMeta.destroyVastMeta();
                        videoJson.put("real_md5", mContentMd5);
                        // video数据库中自己拍摄的视频tags、des字段基本为空，所以android直接manual_type字段传空

                        videoJson.put("manual_type", 0);
                        final byte[] rc4Bytes = RC4Util.makeRc4WithCharset(videoJson.toString(),
                                "UTF-8");
                        mVideoInfo = Base64Util.encode(rc4Bytes);
                        DuboxLog.i(TAG, "mVideoInfo = " + mVideoInfo);
                    } catch (Exception e) {
                        feedbackMonitorLog("getVideoExif onProbeCompleted" + e.getMessage());
                        DuboxLog.e(TAG, "get video exif", e);
                    }finally {
                        try {
                            fd.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(int i) {
                    feedbackMonitorLog("getVideoExif onError");
                    try {
                        fd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            vastMeta.startProbe();
        }
    }

    /**
     * 检测wifi
     *
     * @throws StopRequestException
     */
    private void checkWiFi() throws StopRequestException {
        if (isWaitingWiFi()) {
            feedbackMonitorLog("checkWiFi waiting for wifi");
            throw new StopRequestException(TransmitterConstant.WAITING_FOR_WIFI, "checkWiFi waiting for wifi");
        }
    }

    /**
     * 检测电量
     *
     * @throws StopRequestException
     */
    private void checkPower() throws StopRequestException {
        if (mOptions.isPowerCheckEnable() && BatteryMonitor.isLowPower()) {
            feedbackMonitorLog("checkPower LOW POWER");
            throw new StopRequestException(TransmitterConstant.LOW_POWER, "checkPower LOW POWER");
        }
    }

    /**
     * 压缩完成，通知可进行下一步（即开始上传）
     * @param isPaused
     */
    protected void notifyCompressComplete(boolean isPaused) {
        mCompressLock.lock();
        try {
            mCompressCondition.signal();
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        } finally {
            mCompressLock.unlock();
        }

    }

    @Override
    protected void doRetry(Retry t) throws StopRequestException {
        if (retryTimes < RETRY_MAX_TIMES) {
            SystemClock.sleep(RETRY_DELAY);
            retryTimes++;
            return;
        }

        if (mOptions.isNetworkVerifier()) {
            networkVerifierCheck();
        } else {
            feedbackMonitorLog("doRetry over time:");
            throw new StopRequestException(OtherErrorCode.RETRY_OVER_TIME, "doRetry over time " + t.getMessage());
        }
    }

    @Override
    public void pause() {
        isPause = true;

        stopConcurrentBlockUpload();
        if (mBlockTransferLog != null) {
            mBlockTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_PAUSE);
        }
        mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_PAUSE);

        // 回调业务层
        if (mOptions.getStatusCallback() != null && (mOptions.getStatusCallback() instanceof ITransferStatusCallback)) {
            ((ITransferStatusCallback) mOptions.getStatusCallback()).onPause();
        }
    }

    @Override
    public void remove(boolean isDeleteFile) {
        isPause = true;
        stopConcurrentBlockUpload();
        if (mBlockTransferLog != null) {
            mBlockTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_REMOVE);
        }
        mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_REMOVE);
    }

    /**
     * 上传流程STEP1 准备上传文件的md5-list
     *
     * @return
     * @throws StopRequestException
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午09:37:00
     */
    private void prepareUploadInfo() throws StopRequestException {
        // YQH
        if (mOptions.isNetworkVerifier() && NetWorkVerifier.isNoNetwork()) {
            feedbackMonitorLog("prepareUploadInfo NETWORK_NOT_AVAILABLE");
            throw new StopRequestException(TransmitterConstant.NETWORK_NOT_AVAILABLE,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.NETWORK_NOT_AVAILABLE));
        }
        // 初始化时间和size供后面http请求携带
        mLastModifiedTime = mLocalFile.lastModified();
        cTime = mLastModifiedTime / 1000;
        mTime = cTime;
        if (fileSize == -1L) { // 初始化文件大小
            fileSize =  mLocalFile.length();
            dynamicUploadBlockSize = DynamicUploadBlockKt.getSize(fileSize);
            if (mResolver != null) {
                final ContentValues values = new ContentValues(1);
                values.put(TransferContract.Tasks.SIZE, fileSize);
                mResolver.update(ContentUris.withAppendedId(mUri, mTaskId), values, null, null);
            }
        }
        mTransferLog.setFileSize(fileSize);

        DuboxLog.d(TAG, "SuperFile2 mLastModifiedTime:" + mLastModifiedTime);
        DuboxLog.d(TAG, "SuperFile2 fileSize:" + fileSize);

        if (!calAllMd5s()) { // 计算MD5
            if (checkFileExist()) {
                feedbackMonitorLog("prepareUploadInfo calculate md5 list error");
                throw new StopRequestException(OtherErrorCode.MD5_LIST_EMPTY, "calculate md5 list error");
            } else {
                feedbackMonitorLog("calculate md5 list error, local file not exist");
                throw new StopRequestException(UploadExceptionCode.LOCAL_FILE_ERROR,
                        "calculate md5 list error, local file not exist");
            }

        }
        if (isPause) {
            feedbackMonitorLog("prepareUploadInfo task pause");
            throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
        }
    }

    /**
     * 计算要上传文件的全部MD5
     *
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-1 下午07:48:21
     */
    protected boolean calAllMd5s() {
        if (CollectionUtils.isNotEmpty(mAllMd5List)) {
            return true;
        }
        getFileMd5(fileSize);
        return !CollectionUtils.isEmpty(mAllMd5List);
    }

    /**
     * 获取分片的文件的md5，按4M分片和服务器保持一致
     *
     * @param fileSize
     * @return
     */
    protected void getFileMd5(long fileSize) {

        mAllMd5List = new ArrayList<String>();

        int block = (int) Math.ceil((double) fileSize / dynamicUploadBlockSize);
        DuboxLog.d(TAG, "SuperFile2 Block:" + (double) fileSize);
        DuboxLog.d(TAG, "SuperFile2 Block:" + dynamicUploadBlockSize);
        DuboxLog.d(TAG, "SuperFile2 Block:" + Math.ceil((double) fileSize / dynamicUploadBlockSize));
        DuboxLog.d(TAG, "SuperFile2 Block:" + block);
        byte[] buffer = new byte[CALL_MD5_SIZE];

        // 为了每4M算一次MD5,4M除以的到的值
        long blockDivCount = dynamicUploadBlockSize / CALL_MD5_SIZE;
        // 大于等于256K的文件前面256K算SliceMd5
        long sliceDivCount = SLICE_SIZE / CALL_MD5_SIZE;

        BufferedInputStream inputStream = null;
        MessageDigest digester = null; // 分片MD5计算器
        MessageDigest sliceDigester = null; // 前256K MD5计算器
        MessageDigest contentDigester = null; // 全文MD5计算器
        CRC32 contentCrc32 = null; // crc计算器
        try {
            digester = MessageDigest.getInstance("MD5");
            sliceDigester = MessageDigest.getInstance("MD5");
            contentDigester = MessageDigest.getInstance("MD5");
            contentCrc32 = new CRC32();

            inputStream = new BufferedInputStream(mLocalFile.inputStream(BaseApplication.getInstance()), CALL_MD5_SIZE);
            for (int i = 0; i < block; i++) {
                if (isPause) {
                    return;
                }
                // 4M算一次MD5
                for (int j = 0; j < blockDivCount; j++) {
                    if (isPause) {
                        return;
                    }
                    int blockLen = (int) ((blockDivCount * i + (j + 1)) * CALL_MD5_SIZE > fileSize
                            ? (fileSize - (blockDivCount * i + j) * CALL_MD5_SIZE) : CALL_MD5_SIZE);
                    inputStream.read(buffer, 0, blockLen);
                    digester.update(buffer, 0, blockLen);
                    contentDigester.update(buffer, 0, blockLen);

                    if (i == 0 && j < sliceDivCount) {
                        sliceDigester.update(buffer, 0, blockLen);
                    }

                    contentCrc32.update(buffer, 0, blockLen);

                    if ((blockDivCount * i + (j + 1)) * CALL_MD5_SIZE > fileSize) {
                        break;
                    }
                }
                mAllMd5List.add(HexUtil.toHexLowerCaseString(digester.digest()));
            }

            if (mAllMd5List.size() == 0) {
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.UPLOAD_FAIL_BY_MD5LIST_NULL, true,
                        String.valueOf(fileSize), String.valueOf(mLocalFile.length()),
                        String.valueOf(mSource));
            }

            mSliceMd5 = HexUtil.toHexLowerCaseString(sliceDigester.digest());
            mContentMd5 = HexUtil.toHexLowerCaseString(contentDigester.digest());
            mContentCrc32 = contentCrc32.getValue();
            if (DuboxLog.isDebug()) {
                DuboxLog.d(TAG, "SuperFile2 mAllMd5List:" + mAllMd5List.toString());
                DuboxLog.d(TAG,
                        "SuperFile2 mAllMd5List:" + new JSONArray(this.mAllMd5List).toString());
                DuboxLog.d(TAG, "SuperFile2 SliceMd5:" + mSliceMd5);
                DuboxLog.d(TAG, "SuperFile2 ContentMd5:" + mContentMd5);
                DuboxLog.d(TAG, "SuperFile2 ContentCrc32:" + mContentCrc32);
            }

        } catch (FileNotFoundException e) {
            feedbackMonitorLog("getFileMd5 FileNotFoundException" + e.getMessage());
            DuboxLog.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            feedbackMonitorLog("getFileMd5 IOException" + e.getMessage());
            DuboxLog.e(TAG, "IOException : mAllMd5List clear", e);
            mAllMd5List.clear();
            // 发生异常，重试两次，间隔一秒
            reReadNum++;
            while (reReadNum <= 2) {
                SystemClock.sleep(READ_RETRY_DELAY);
                getFileMd5(fileSize);
            }

            // 9.3 统计每天用户上传失败的次数、用户数，并上报错误信息、失败来源以及文件信息
            StatisticsLogForMutilFields.getInstance().updateCount(
                StatisticsLogForMutilFields.StatisticsKeys.UPLOAD_FAIL_BY_REASON, true,
                String.valueOf(mSource), mLocalFile.localUrl(), String.valueOf(mLocalFile.length()),
                Base64Util.encode(e.getMessage()));

            DuboxLog.e(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            feedbackMonitorLog("getFileMd5 NoSuchAlgorithmException" + e.getMessage());
            DuboxLog.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                DuboxLog.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }


    /**
     * 上传流程 STEP2 预创建文件
     *
     * @return
     * @throws StopRequestException
     * @throws Retry
     * @author 孙奇 V 1.0.0 Create at 2012-12-2 下午11:54:10
     */
    private PreCreateFileResponse preCreateFile() throws StopRequestException, Retry {
        int retry = 0;
        while (retry < RETRY_MAX_TIMES && !isPause) {
            if (retry != 0) {
                SystemClock.sleep(STEP_RETRY_DELAY);
            }
            retry++;

            DuboxLog.i(TAG, "preCreateFile try " + retry + " time");
            mNeedSeqList.clear();

            DuboxLog.d(TAG, "preCreateFile mRemoteUrl " + mRemotePath);

            if (isPause) {
                feedbackMonitorLog("preCreateFile task pause");
                throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
            }

            PreCreateFileResponse result = reqPreCreateFile();
            if (result != null) {
                mSign = result.mUploadSign;

                if (result.mReturnType == PreCreateFileResponse.RETURN_TYPE_RAPIDUPLOAD) {
                    return result;
                } else {
                    if (result.mBlockList == null) {
                        feedbackMonitorLog(
                            "preCreateFile mBlockList is null, network response result:" + result.mRawString);
                        throw new StopRequestException(OtherErrorCode.BLOCK_LIST_EMPTY,
                                "preCreateFile mBlockList is null, network response result:" + result.mRawString);
                    }

                    this.mNeedSeqList.addAll(result.mBlockList);
                    if (TextUtils.isEmpty(mUploadId)) {
                        mUploadId = result.mUploadId;

                        if (mUri != null) {
                            ContentValues values = new ContentValues();
                            values.put(TransferContract.UploadTasks.UPLOAD_ID, mUploadId);
                            mResolver.update(ContentUris.withAppendedId(mUri, mTaskId), values, null, null);
                        }
                    }

                    return null;
                }
            } else {
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_OTHER);
                feedbackMonitorLog("preCreateFile result==null");
                throw new Retry(OtherErrorCode.ERROR_OTHER_UNKNOWN, "preCreateFile network error");
            }
        }
        feedbackMonitorLog("pre create retry");
        throw new Retry(OtherErrorCode.RETRY_UPLOAD_PRE_CREATE_FILE, "pre create retry");
    }

    /**
     * upload precreate file request
     *
     * @return
     */
    private PreCreateFileResponse reqPreCreateFile() throws StopRequestException {
        try {
            return new TransferApi(mBduss, mUid).preCreateFile(getPreCreateParameter());
        } catch (Throwable e) {
            DuboxLog.e(TAG, "", e);
            if (e instanceof RemoteException && ((RemoteException) e).getErrorCode() == ERROR_CODE_VIDEO_OVERSIZE){
                throw new StopRequestException(OtherErrorCode.ERROR_VIDEO_OVERSIZE, "video upload size limit");
            }
            return null;
        }
    }

    private HttpParams getPreCreateParameter() {
        final HttpParams params = new HttpParams();
        if (!TextUtils.isEmpty(mUploadId)) {
            params.add("uploadid", mUploadId);
        }

        params.add("path", mRemotePath);
        params.add("size", String.valueOf(fileSize));
        params.add("isdir", "0");
        params.add("local_mtime", String.valueOf(mTime));
        params.add("local_ctime", String.valueOf(cTime));

        JSONArray json = new JSONArray(this.mAllMd5List);
        params.add("block_list", json.toString());

        params.add("autoinit", "1");

        if (!TextUtils.isEmpty(mContentMd5)) {
            params.add("content-md5", mContentMd5);
        }

        if (!TextUtils.isEmpty(mSliceMd5)) {
            params.add("slice-md5", mSliceMd5);
        }

        if (mContentCrc32 > 0) {
            params.add("contentCrc32", String.valueOf(mContentCrc32));
        }

        int rtype = mWillBeOverride ? RTYPE_OVERRIDE : RTYPE_FILE_RENAME;

        params.add("rtype", String.valueOf(rtype));
        // 8.6.0 支持server最近上传功能所需参数
        params.add("mode", String.valueOf(mSource));
        String targetPath = !TextUtils.isEmpty(mTargetPath) ? mTargetPath : FileUtils.getParentPath(mRemotePath);
        if (!TextUtils.isEmpty(targetPath)) {
            params.add("target_path", targetPath);
        }
        // 非压缩上传，走正常文件上传流程
        params.add("checkexist", "0");
        return params;
    }

    /**
     * 上传流程 STEP3 pcsUploadFile
     */
    private void pcsUploadFile() throws StopRequestException, Retry {
        // 暂停检查
        if (isPause) {
            feedbackMonitorLog("pcsUploadFile task pause");
            throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
        }

        // 全命中
        if (mNeedSeqList.isEmpty()) {
            DuboxLog.v(TAG, "mNeedSeqList size=" + mNeedSeqList.size());
            isEverUploaded = true;
            return;
        }

        final boolean isEnableBlockUpload = ConfigBlockUpload.INSTANCE.enable();
        int block = (int) Math.ceil((double) fileSize / dynamicUploadBlockSize);

        if (mTransferLog instanceof UploadLog) {
            ((UploadLog) mTransferLog).setConcurrentEnable(isEnableBlockUpload);
        }
        ConcurrentHashMap<Integer, Integer> mBlockInfoMap = new ConcurrentHashMap<>();
        PcsUploadBlockHelper pcsUploadBlockHelper = new PcsUploadBlockHelper();
        for (int i = 0; i < block && !isPause; i++) {
            // 检查文件是否存在
            if (!checkFileExist()) {
                feedbackMonitorLog("pcsUploadFile local file not exist or isDirectory");
                throw new StopRequestException(TransmitterConstant.LOCAL_FILE_ERROR,
                        "pcsUploadFile local file not exist or isDirectory");
            }

            // 7.9 libin09 每个分片传输开始前检查文件是否改变
            if (isModify()) {
                DuboxLog.i(TAG, "file is modified");
                feedbackMonitorLog("pcsUploadFile local file id changed");
                throw new StopRequestException(TransmitterConstant.LOCAL_FILE_CHANGE,
                        "pcsUploadFile local file id changed");
            }

            int blockLen = (int) ((i + 1) * dynamicUploadBlockSize > fileSize ? (fileSize - i
                    * dynamicUploadBlockSize) : dynamicUploadBlockSize);
            // 已经上传的片段不需要再上传
            if (i < mAllMd5List.size() && !mNeedSeqList.contains(i)) {
                long offset = 0L;
                if (i == 0) {
                    offset = blockLen;
                } else {
                    offset = (i - 1) * dynamicUploadBlockSize + blockLen;
                }
                mTransferLog.setStartPosition(offset);
//                notifyProgress(offset, -1L);
                continue;
            }
            if (isEnableBlockUpload) {
                mBlockInfoMap.put(i, blockLen);
            } else {
                boolean isRetry = false;
                while (!blockUpload(pcsUploadBlockHelper, false, i, blockLen, null, isRetry)) {
                    isRetry = true;
                }
            }
        }
        if (isPause) {
            if (isEnableBlockUpload) {
                stopConcurrentBlockUpload();
            } else {
                pcsUploadBlockHelper.stopUpload();
            }
            feedbackMonitorLog("pcsUploadFile task pause");
            throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
        } else {
            if (isEnableBlockUpload) {
                startConcurrentBlockUpload(mBlockInfoMap);
            }
        }
    }

    /**
     * 启动分片并发上传
     * @param blockInfoMap 待上传的分片任务
     */
    private void startConcurrentBlockUpload(final ConcurrentHashMap<Integer, Integer> blockInfoMap)
            throws Retry, StopRequestException {
        if (blockInfoMap == null || blockInfoMap.isEmpty()) {
            DuboxLog.d(TAG, ">>>>>>>>>>>> 上传文件=" + mFileName + "，分片并发上传 总片数 为空 <<<<<<<<<<<<");
            return;
        }
        DuboxLog.d(TAG, ">>>>>>>>>>>> 上传文件=" + mFileName + "，分片并发上传 总片数 "
                + blockInfoMap.size() + " <<<<<<<<<<<<");
        final BlockUploadListener listener = new BlockUploadListener() {
            @Override
            public void onProgress(long doneLen) {
                // 已经上传的总长度
                long uploadLen = mBlockUploadLen.addAndGet(doneLen);
                // 本次刷新进度上传的长度
                long uploadBlockLen = mDeltaBlockUploadLen.addAndGet(doneLen);

                long time = SystemClock.elapsedRealtime();
                long intervalTime = time - lastUpdateClock;
                long rate = 0;
                if (intervalTime >= PROGRESS_UPDATE_INTERVAL) {
                    if (mOptions.isRateCalculateEnable()) {
                        rate = mOptions.getRateCalculator().calculateSmoothRate(intervalTime,
                                mOptions.getRateCalculator().calculateRealRate(uploadBlockLen, intervalTime));
                    }
                    mDeltaBlockUploadLen.set(0L);
                    notifyProgress(uploadLen, rate);
                }
            }

            @Override
            public void onSuccess(int blockIndex) {
                DuboxLog.d(TAG, ">>>>>>>>>>>> 上传文件=" + mFileName + "，上传分片=" + blockIndex + " 成功");
            }

            @Override
            public void onError(int blockIndex, int errorCode) {
                DuboxLog.d(TAG, ">>>>>>>>>>>> 上传文件=" + mFileName + "，上传分片=" + blockIndex + " 失败");
            }
        };
        blockUploadJobs.clear();
        mFailedErrorCodeBlockJobMap.clear();
        ArrayList<BlockUploadJob> jobs = new ArrayList<>(blockInfoMap.size());
        for (Map.Entry<Integer, Integer> entry : blockInfoMap.entrySet()) {
            final int blockIndex = entry.getKey();
            final int blockLen = entry.getValue();
            BlockUploadJob blockUploadJob = new BlockUploadJob(blockUploadJobs) {
                @Override
                public void performExecute() {
                    try {
                        checkBlockCondition();
                        boolean isRetry = false;
                        while (!blockUpload(null, true, blockIndex , blockLen, listener,  isRetry)) {
                            isRetry = true;
                        }
                    } catch (Retry retry) {
                        DuboxLog.d(TAG, ">>>>>>>>>>>> 上传文件=" + mFileName + "，上传分片 " + blockIndex
                                + " 出现异常retry=" + retry.getMessage());
                        mFailedErrorCodeBlockJobMap.put(blockIndex, retry);
                        stopConcurrentBlockUpload();
                    } catch (StopRequestException e) {
                        DuboxLog.d(TAG, ">>>>>>>>>>>> 上传文件=" + mFileName + "，上传分片 " + blockIndex
                                + " 出现异常stopRequestException=" + e.getMessage());
                        mFailedErrorCodeBlockJobMap.put(blockIndex, e);
                        if (!isPause) {
                            // 引网络、等待wifi、请求错误等原因发生异常
                            stopConcurrentBlockUpload();
                        }
                    } finally {
                        DuboxLog.d(TAG, ">>>>>>>>>>>> 上传文件=" + mFileName
                                + "，上传分片=" + blockIndex + ",finally , 分片数目 " + blockUploadJobs.size());
                    }
                }
            };
            jobs.add(blockUploadJob);
        }
        blockUploadJobs.addAll(jobs);
        if (mTransferLog instanceof UploadLog) {
            ((UploadLog) mTransferLog).setConcurrentCount(
                    BlockUploadScheduler.Companion.getInstance().getCurrentPoolSize()
            );
        }
        checkBlockCondition();
        BlockUploadScheduler.Companion.getInstance().summitAllAndWait(blockUploadJobs);
        checkBlockCondition();
        checkFailedException();
    }
    /**
     * 检查是否有分片重试超出上限
     * @throws StopRequestException 错误异常
     */
    public void checkFailedException() throws StopRequestException, Retry {
        if (mFailedErrorCodeBlockJobMap.isEmpty()) {
            return;
        }
        for (Throwable value : mFailedErrorCodeBlockJobMap.values()) {
            if (value instanceof StopRequestException) {
                DuboxLog.d(TAG, "pcs upload StopRequestException error");
                throw (StopRequestException) value;
            }
            if (value instanceof Retry) {
                DuboxLog.d(TAG, "pcs upload retry error");
                throw (Retry) value;
            }
        }
    }

    /**
     * 停止分片并发上传任务
     */
    private void stopConcurrentBlockUpload() {
        DuboxLog.d(TAG, ">>>>>>>>>>>> stopConcurrentBlockUpload 停止分片并发上传任务 cancelBlockUpload");
        if (blockUploadJobs.isEmpty()) {
            return;
        }
        BlockUploadScheduler.Companion.getInstance().cancelAll(blockUploadJobs);
    }

    /**
     * 上传当前文件的第blockIndex个分块
     *
     * @param pcsUploadBlockHelper
     * @param blockIndex
     * @param blockLen
     * @return
     * @throws StopRequestException
     * @throws Retry
     */
    private boolean blockUpload(PcsUploadBlockHelper pcsUploadBlockHelper,
                                boolean isEnableBlockUpload,
                                int blockIndex, int blockLen,
                                BlockUploadListener listener,
                                boolean isRetry)
            throws StopRequestException, Retry {
        LocateUpload locateUpload = new LocateUpload();
        UploadUrlInfo urlInfo = locateUpload.getServer(mRemotePath, isRetry, mBduss, mUid, mSign);
        DuboxLog.d(TAG, "blockupload:urlInfo=" + urlInfo);

        // urlInfo 为空时退出分片的递归重试
        if (urlInfo == null || TextUtils.isEmpty(urlInfo.server)) {
            feedbackMonitorLog("blockUpload urlInfo null or urlInfo server null");
            throw new Retry(OtherErrorCode.RETRY_UPLOAD_URL_NULL, "urlInfo null or urlInfo server null");
        }
        // 初始化pcsUploadBlockHelper.blockUpload()
        if (isEnableBlockUpload) {
            final PcsUploadBlockHelper helper = new PcsUploadBlockHelper();
            helper.init(blockIndex, blockLen, urlInfo);
            if (helper.blockUpload(true, listener)) {
                if (listener != null) {
                    listener.onSuccess(blockIndex);
                }
                return true;
            }
        } else {
            pcsUploadBlockHelper.init(blockIndex, blockLen, urlInfo);
            if (pcsUploadBlockHelper.blockUpload(false, null)){
                return true;
            }
        }
        checkBlockCondition();
        return false;
    }

    protected void checkBlockCondition() throws StopRequestException {
        if (isPause) {
            feedbackMonitorLog("checkBlockCondition task pause");
            throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
        } else if (!ConnectivityState.isConnected(BaseApplication.getInstance())) {
            feedbackMonitorLog("checkBlockCondition NETWORK_NO_CONNECTION");
            throw new StopRequestException(TransmitterConstant.NETWORK_NO_CONNECTION,
                    TransmitterConstant.getExceptionMsg(TransmitterConstant.NETWORK_NO_CONNECTION));
        } else if (mOptions.isWiFiDetectionEnable() && isWaitingWiFi()) {
            feedbackMonitorLog("checkBlockCondition blockUpload waiting for wifi");
            throw new StopRequestException(TransmitterConstant.WAITING_FOR_WIFI, "blockUpload waiting for wifi");
        }
    }

    /**
     * 上传流程STEP4 提交文件
     *
     * @return
     * @throws StopRequestException
     * @throws Retry
     * @author 孙奇 V 1.0.0 Create at 2012-12-2 下午11:57:10
     */
    private CreateFileResponse createFile() throws StopRequestException, Retry {
        if (mAllMd5List.isEmpty()) {
            DuboxLog.i(TAG, "mAllMD5List size == 0");
            if (checkFileExist()) {
                feedbackMonitorLog("createFile md5 list empty");
                throw new StopRequestException(OtherErrorCode.MD5_LIST_EMPTY, "md5 list empty");
            } else {
                feedbackMonitorLog("createFile md5 list empty, local file not exist");
                throw new StopRequestException(TransmitterConstant.LOCAL_FILE_ERROR,
                        "md5 list empty, local file not exist");
            }
        }

        int retry = 0;
        while (!isPause && retry < RETRY_MAX_TIMES) {
            if (retry != 0) {
                SystemClock.sleep(STEP_RETRY_DELAY);
            }
            retry++;
            DuboxLog.d(TAG,
                    "isEverUploaded=" + isEverUploaded + " ,task=" + mFileName );

            CreateFileResponse result = null;
            try {
                result = reqCreateFile();
            } catch (RemoteException e) {
                int errno = e.getErrorCode();
                switch (errno) {
                    case FILE_ALREADY_EXIST:
                        return result;
                    case RESULT_BDUSS_INVALID:
                        ServerResultHandler.sendMsg(ServerResultHandler.MESSAGE_BDUSS_INVALID_SHOWLOGIN, errno, -1);
                        StatisticsLog
                                .updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_CREATE_ERROR);
                        feedbackMonitorLog("createFile bduss invalid");
                        throw new StopRequestException(TransmitterConstant.BDUSS_IS_INVALID,
                                "createFile bduss invalid");
                    case SPACE_FULL:
                        DuboxLog.d(TAG, " res.getResult() == result_space_full ");
                        // WZZ 统计，网盘空间已满
                        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_SPACE_FULL);
                        feedbackMonitorLog("createFile NO_REMOTE_SPACE");
                        throw new StopRequestException(TransmitterConstant.NO_REMOTE_SPACE,
                                "createFile NO_REMOTE_SPACE");
                    case FILE_OR_DIR_NAME_IVALID:
                        feedbackMonitorLog("createFile file name ivalid");
                        throw new StopRequestException(TransmitterConstant.FILE_NAME_ILLEGAL,
                                "preCreateFile file name ivalid");
                    case PARAM_ERROR:
                        feedbackMonitorLog("createFile param error");
                        throw new StopRequestException(TransmitterConstant.FILE_PARAMETER_ERROR,
                                "preCreateFile param error");
                    case FILE_MORE:
                        feedbackMonitorLog("createFile file num more");
                        throw new StopRequestException(TransmitterConstant.FILE_MORE_NUMBER,
                                "preCreateFile file num more");
                    case EXCEED_MAX_NUM:
                        feedbackMonitorLog("createFile file size limit");
                        throw new StopRequestException(TransmitterConstant.FILE_SIZE_LIMIT,
                                "preCreateFile file size limit");
                    case CREATE_SUPERFILE_FAILED:
                        feedbackMonitorLog("createFile CREATE_SUPERFILE_FAILED");
                        StatisticsLog
                                .updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_CREATE_ERROR);
                        continue;
                    case SAFE_BOX_SPACE_FULL:
                        DuboxLog.d(TAG, "-19 error");
                        feedbackMonitorLog("createFile safe box size limit");
                        throw new StopRequestException(TransmitterConstant.SAFE_BOX_SIZE_LIMIT,
                                "preCreateFile safe box size limit");
                    case ERROR_CODE_VIDEO_OVERSIZE:
                        DuboxLog.d(TAG, "146 error");
                        feedbackMonitorLog("video upload size limit");
                        throw new StopRequestException(OtherErrorCode.ERROR_VIDEO_OVERSIZE,
                                "video upload size limit");
                    default:
                        feedbackMonitorLog("createFile default " + errno);
                        StatisticsLog
                                .updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_CREATE_ERROR);
                        DuboxLog.d(TAG, "createFile::isNoRetryServerError");
                        if (isNoRetryDuboxError(errno) || isNoRetryAccountError(errno) || isNoRetryPCSError(errno)) {
                            throw new StopRequestException(errno, "createFile isNoRetryServerError");
                        }
                        continue;
                }
            } catch (IOException e) {
                feedbackMonitorLog("createFile IOException " + e.getMessage());
                DuboxLog.e(TAG, "doInBackground, parse http responce error ConnectTimeoutException::", e);
                StatisticsLog.countUploadFailedByNetworkError();
            }

            if (result != null) {
                return result;
            } else {
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_OTHER);
            }
        }
        feedbackMonitorLog("createFile result null");
        throw new Retry(OtherErrorCode.RETRY_UPLOAD_CREATE_FILE, "create file result null");
    }

    private CreateFileResponse reqCreateFile() throws RemoteException, IOException {
        try {
            return new TransferApi(mBduss, mUid).createFile(getCreateParameter());
        } catch (JSONException e) {
            feedbackMonitorLog("reqCreateFile JSONException " + e.getMessage());
            DuboxLog.e(TAG, "", e);
            return null;
        }
    }

    private HttpParams getCreateParameter() {
        final HttpParams params = new HttpParams();
        if (!TextUtils.isEmpty(mUploadId)) {
            params.add("uploadid", mUploadId);
        }

        // 共享给我的目录需要带
        if (!TextUtils.isEmpty(mSign)) {
            params.add("uploadsign", mSign);
        }

        params.add("path", mRemotePath);
        params.add("size", String.valueOf(fileSize));
        params.add("isdir", "0");
        params.add("local_mtime", String.valueOf(mTime));
        params.add("local_ctime", String.valueOf(cTime));

        JSONArray json = new JSONArray(this.mAllMd5List);
        params.add("block_list", json.toString());

        int rtype = RTYPE_FILE_RENAME;
        if (isEverUploaded) {
            rtype = RTYPE_NORENAME;
        } else if (mWillBeOverride) {
            rtype = RTYPE_OVERRIDE;
        }

        params.add("rtype", String.valueOf(rtype));

        params.add("mode", String.valueOf(mSource));
        String targetPath = !TextUtils.isEmpty(mTargetPath) ? mTargetPath : FileUtils.getParentPath(mRemotePath);
        if (!TextUtils.isEmpty(targetPath)) {
            params.add("target_path", targetPath);
        }

        // create接口中带上图片的exif信息
        if (mLocalFile.isImage()) {
            // create接口, 图片添加质量(原图质量100)和原图md5 7.12 2015-11-10
            params.add("zip_quality",
                    String.valueOf( mCurrentQuality));
            params.add("zip_sign", mContentMd5);
            InputStream fis = null;
            try {
                fis = mLocalFile.inputStream(BaseApplication.getInstance());
                ExifInterface exif = new ExifInterface(fis);
                String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                String model = exif.getAttribute(ExifInterface.TAG_MODEL);
                String make = exif.getAttribute(ExifInterface.TAG_MAKE);
                String sceneType = exif.getAttribute(ExifInterface.TAG_SCENE_TYPE);
                String flash = exif.getAttribute(ExifInterface.TAG_FLASH);
                String exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
                String isoSpeedRatings = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
                String fnumber = exif.getAttribute(ExifInterface.TAG_F_NUMBER);
                String shutterSpeedValue = exif.getAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE);
                double whiteBalance = exif.getAttributeDouble(ExifInterface.TAG_WHITE_BALANCE, 0);
                String focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                String gpsAltitude = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
                String gpsAltitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
                String gpsImgDirection = exif.getAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION);
                String gpsImgDirectionRef = exif.getAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION_REF);
                String gpsTimeStamp = exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
                String gpsDataStamp = exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
                String gpsProcessingMethod = exif.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
                int width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                int height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                String lastTime = DateUtil.formatDateTimeOriginal(mLocalFile.lastModified());

                ExifInterfaceBean exifBean = new ExifInterfaceBean();
                exifBean.mParentPath = mLocalFile.localUrl();
                exifBean.mDateTimeOriginal = dateTime;
                exifBean.mDateTimeDigitized = exifBean.mDateTimeOriginal;
                exifBean.mDateTime = lastTime;
                exifBean.mOrientation = orientation;
                exifBean.mLatitude = latitude;
                exifBean.mLongitude = longitude;
                exifBean.mLatitudeRef = latitudeRef;
                exifBean.mLongitudeRef = longitudeRef;
                exifBean.mModel = model;
                exifBean.mWidth = width;
                exifBean.mHeight = height;
                exifBean.mRecovery = (make != null && make.endsWith(" GPS")) ? 1 : 0;
                exifBean.mSceneType = sceneType;
                exifBean.mFlash = flash;
                exifBean.mExposureTime = exposureTime;
                exifBean.mIsoSpeedRatings = isoSpeedRatings;
                exifBean.mFNumber = fnumber;
                exifBean.mShutterSpeedValue = shutterSpeedValue;
                exifBean.mWhiteBalance = whiteBalance;
                exifBean.mFocalLength = focalLength;
                exifBean.mGpsAltitude = gpsAltitude;
                exifBean.mGPSAltitudeRef = gpsAltitudeRef;
                exifBean.mGPSImgDirection = gpsImgDirection;
                exifBean.mGPSImgDirectionRef = gpsImgDirectionRef;
                exifBean.mGPSTimeStamp = gpsTimeStamp;
                exifBean.mGPSDateStamp = gpsDataStamp;
                exifBean.mGPSProcessingMethod = gpsProcessingMethod;

                String exifInfoStr = new Gson().toJson(exifBean);
                final byte[] rc4Bytes = RC4Util.makeRc4WithCharset(exifInfoStr, "UTF-8");
                String imageInfo = Base64Util.encode(rc4Bytes);
                params.add("exif_info_encrypt", imageInfo);
                DuboxLog.i(TAG, "exif_info_encrypt:" + exifInfoStr);
            } catch (Exception e) {
                DuboxLog.e(TAG, "get com.dubox.drive.preview.image exif", e);
            } catch (StackOverflowError e) {
                DuboxLog.e(TAG, "get com.dubox.drive.preview.image exif", e);
            } finally {
                CloseUtils.closeIO(fis);
            }
        } else if (mLocalFile.isVideo()) {
            if (!TextUtils.isEmpty(mVideoInfo)) {
                params.add("exif_info_encrypt", mVideoInfo);
            }
        }
        DuboxLog.d(VIDEO_COMPRESS_TAG, "getCreateParameter 不用添加添加压缩视频上传参数");
        return params;
    }

    /**
     * 通知进度条变化
     *
     * @param doneLen
     * @author 孙奇 V 1.0.0 Create at 2012-12-2 下午11:56:03
     */
    private void notifyProgress(long doneLen, long rate) {
        if (doneLen > fileSize) {
            DuboxLog.e(TAG, "notifyProgress::doneLen > fileSize doneLen = " + doneLen);
            return;
        }
        boolean isUpdateIntervalEnable;
        isUpdateIntervalEnable = doneLen != fileSize;
        long time = SystemClock.elapsedRealtime();
        if (rate > 0L || (!isUpdateIntervalEnable || (time - lastUpdateClock > PROGRESS_UPDATE_INTERVAL))) {
            // if (progress != lastProgress) {
            lastUpdateClock = time;
            calculate(doneLen, rate);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-2 下午11:56:23
     */
    private boolean checkFileExist() {
        return  !(!mLocalFile.exists() || mLocalFile.isDirectory());
    }

    protected boolean pcsBlockCheckWifiCondition() {
        if (mOptions.isWiFiDetectionEnable() && isWaitingWiFi()) {
            DuboxLog.d(TAG, "PcsUploadBlockHelper.blockUpload isWaitingWifi return false");
            return false;
        }
        return true;
    }

    /**
     * 块上传帮助类，用于帮助生成每块的HTTP链接和实体部分的流
     *
     * @author 孙奇 <br/>
     * create at 2012-12-2 下午10:15:49
     */
    private class PcsUploadBlockHelper {
        private int blockIndex;
        private int blockLen;
        private int bodyLength;
        private String fileBlockName;
        private byte[] bodyHeader;
        private byte[] bodyEnding;
        private String boundary;
        HttpURLConnection conn;

        /**
         * 初始化
         *
         * @return
         * @throws StopRequestException
         * @author 孙奇 V 1.0.0 Create at 2013-2-20 下午07:17:54
         */
        private InputStream initUploadFile() throws StopRequestException {
            InputStream inputStream = mLocalFile.inputStream(BaseApplication.getInstance());
            if (inputStream == null) {
                DuboxLog.e(TAG, "FileNotFoundException initUploadFile localUrl = " + mLocalFile.localUrl());
                feedbackMonitorLog("initUploadFile inputStream == null ");
                throw new StopRequestException(TransmitterConstant.LOCAL_FILE_ERROR,
                    "initUploadFile FileNotFoundException ");
            }
            return inputStream;
        }

        void init(int blockIndex, int blockLength, UploadUrlInfo uploadUrlInfo) {
            DuboxLog.d(TAG, "PcsUploadBlockHelper.init, blockIndex = " + blockIndex +",blockLength = " + blockLength);
            this.blockIndex = blockIndex;
            // fileBody = buffer;

            blockLen = blockLength;
            boundary = String.valueOf(System.currentTimeMillis());
            initFileBlockName(blockIndex);
            initBodyHeader(fileBlockName);
            initBodyEnding();
            initBodyLength(bodyHeader, bodyEnding, blockLength);
            conn = getHttpURLConnection(uploadUrlInfo);
        }

        void stopUpload() {
            if (conn != null) {
                conn.disconnect();
            }
        }

        private boolean blockUpload(boolean isEnableBlockUpload,
                                    BlockUploadListener listener) throws StopRequestException, Retry {
            DuboxLog.d(TAG, "PcsUploadBlockHelper.blockUpload");
            if (conn == null) {
                feedbackMonitorLog("blockUpload conn ==null");
                DuboxLog.d(TAG, "PcsUploadBlockHelper.blockUpload conn ==null return false");
                return false;
            }
            OutputStream uploadBuffer = null;
            // 速度上报信息采集
            long startTime = System.currentTimeMillis();
            long size = -1L;
            String serverHost = "";
            boolean status = false;
            int stopReason = SpeedUploadUtils.REASON_SUCCESS;
            String requestId = "";
            String url = conn.getURL().toString();

            mTransferLog.setRequestUrl(url);
            mTransferLog.initNetWorkType();

            InputStream randomFile = null;
            try {
                uploadBuffer = conn.getOutputStream();
                serverHost = conn.getURL().getHost();
                if (uploadBuffer == null) {
                    feedbackMonitorLog("blockUpload uploadBuffer ==null");
                    DuboxLog.d(TAG, "PcsUploadBlockHelper.blockUpload uploadBuffer ==null return false");
                    status = false;
                    return false;
                }

                // 上传一块开始
                mBlockTransferLog = new UploadLog(mUid);
                mBlockTransferLog.setServerIp(NetworkUtil.getServerIP(conn.getURL()));
                mBlockTransferLog.setLogTaskId(mTransferLog.getLogTaskId());
                if (mOptions.getTransferCalculable() != null) {
                    mTransferLog.setTransferCalculable(mOptions.getTransferCalculable());
                }
                mBlockTransferLog.setUploadType(mSource);
                mBlockTransferLog.setLocalPath(mLocalFile.localUrl());
                mBlockTransferLog.setRemoteUrl(mRemotePath);
                mBlockTransferLog.setRequestUrl(url);
                mBlockTransferLog.initNetWorkType();
                mBlockTransferLog.setBlockSize(blockLen);
                mBlockTransferLog.setServerHost(serverHost);
                mBlockTransferLog.setStartTime(System.currentTimeMillis());
                mBlockTransferLog.setBlockIndex(this.blockIndex);
                mBlockTransferLog.setConcurrentEnable(isEnableBlockUpload);
                if (isEnableBlockUpload) {
                    mBlockTransferLog.setConcurrentCount(
                            BlockUploadScheduler.Companion.getInstance().getCurrentRunningThreads()
                    );
                }
                // 写头部
                uploadBuffer.write(bodyHeader);

                int bytesCount = (int) Math.ceil((double) blockLen / UPLOAD_BYTES_SIZE);

                DuboxLog.i(TAG, "bytes_count " + bytesCount + ", total block " + blockLen);
                byte[] buffer = null;

                if (bytesCount == 1) {
                    buffer = new byte[blockLen];
                } else {
                    buffer = new byte[UPLOAD_BYTES_SIZE];
                }
                long deltaSizeSum = 0L;
                randomFile = initUploadFile();
                long offset = blockIndex * dynamicUploadBlockSize;
                randomFile.skip(offset);
                for (long j = 0; j < bytesCount && !isPause; j++) {
                    int bytesSize;
                    if ((j + 1) * UPLOAD_BYTES_SIZE > blockLen) {
                        bytesSize = (int) (blockLen - j * UPLOAD_BYTES_SIZE);
                    } else {
                        bytesSize = UPLOAD_BYTES_SIZE;
                    }
                    randomFile.read(buffer, 0, bytesSize);
                    // 写文件实体
                    uploadBuffer.write(buffer, 0, bytesSize);
                    uploadBuffer.flush();

                    long doneLen = offset + bytesSize;
                    size = doneLen - blockIndex * dynamicUploadBlockSize;
                    if (isEnableBlockUpload) {
                        deltaSizeSum += bytesSize;
                    } else {
                        final long rate;
                        if (mOptions.isRateCalculateEnable()) {
                            rate = mOptions.getRateCalculator().calculate(bytesSize);
                        } else {
                            rate = 0L;
                        }
                        DuboxLog.e(TAG, "notifyProgress: " + doneLen + " : " + rate);
                        notifyProgress(doneLen, rate);
                    }
                    if (!ConnectivityState.isConnected(BaseApplication.getInstance())) {
                        status = false;
                        feedbackMonitorLog("blockUpload NETWORK_NO_CONNECTION ");
                        throw new StopRequestException(TransmitterConstant.NETWORK_NO_CONNECTION,
                                TransmitterConstant.getExceptionMsg(TransmitterConstant.NETWORK_NO_CONNECTION));
                    }

                    // 用于2G下上传过程中，勾选仅在WIFI下上传，触发RUNNING到PENDING的转换
                    if (!pcsBlockCheckWifiCondition()) {
                        status = false;
                        feedbackMonitorLog("blockUpload waiting for wifi");
                        throw new StopRequestException(TransmitterConstant.WAITING_FOR_WIFI,
                                "blockUpload waiting for wifi");
                    }
                }
                if (isPause) {
                    status = true;
                    feedbackMonitorLog("blockUpload task pause");
                    throw new StopRequestException(OtherErrorCode.TASK_PAUSE, "task pause");
                }
                uploadBuffer.write(bodyEnding); // 写尾部
                uploadBuffer.flush();

                int repCode = conn.getResponseCode();
                DuboxLog.i(TAG, "res code: " + repCode);
                String md5 = conn.getHeaderField("Content-MD5");
                DuboxLog.i(TAG, "Content-MD5 = " + md5);

                // 上传一块完成
                mBlockTransferLog.setOffsetSize(size);
                mBlockTransferLog.setEndTime(System.currentTimeMillis());

                if (repCode == HttpURLConnection.HTTP_OK) {
                    if (isEnableBlockUpload && listener != null) {
                        listener.onProgress(deltaSizeSum);
                    }
                    if (!(md5 != null && md5.equalsIgnoreCase(mAllMd5List.get(blockIndex)))) {
                        DuboxLog.d(TAG, "md5 not match ");
                        status = false;
                        feedbackMonitorLog("blockUpload md5 not match");
                        throw new Retry(TransmitterConstant.TRANSMITTER_STATE_TRANSMITTING, "md5 not match");
                        // return false;
                    }
                    DuboxLog.d(TAG, "md5 match OK");
                    status = true;

                    mBlockTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FINISH);
                    mBlockTransferLog.setCurrentUploadType(TransferLog.LogUploadType.BLOCK_SUCCESS);
                    mLogTaskManager.addLogTask(mLogGenerator, mBlockTransferLog);
                    return true;
                } else {
                    // WZZ 统计 上传失败：pcs服务器错误
                    StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_PCS_ERROR);
                    // Handle Cookie or passport expired case.
                    String errorMsg = ErrorMessageHelper.readErrorMsg(conn.getErrorStream());
                    int errCode = ErrorMessageHelper.readErrorCode(errorMsg);
                    stopReason = errCode;
                    requestId = ErrorMessageHelper.readRequestId(errorMsg);
                    ErrorMessageHelper.checkPCSErrorNo(errCode);
                    DuboxLog.d(TAG, "blockUpload::isNoRetryServerError");

                    mBlockTransferLog.setHttpErrorCode(repCode);
                    mBlockTransferLog.setPcsErrorCode(errCode);
                    mBlockTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FAIL);

                    mTransferLog.setHttpErrorCode(repCode);
                    mTransferLog.setPcsErrorCode(errCode);

                    final JSONArray array = new JSONArray();
                    try {
                        final JSONObject result = new JSONObject();
                        result.put(TransformStats.KEY_HTTP_ERROR_CODE, repCode);
                        result.put(TransformStats.KEY_PCS_ERROR_CODE, errCode);
                        result.put(TransformStats.KEY_PCS_ERROR_MSG, errorMsg);
                        result.put(TransformStats.KEY_FILE_URL, url);
                        result.put(TransformStats.KEY_REMOTE_IP, conn.getURL().getHost());
                        result.put(TransformStats.KEY_PCS_SERVER_TAG,
                                conn.getHeaderField(TransformStats.KEY_PCS_SERVER_TAG));
                        result.put(TransformStats.KEY_BS_SERVER_TAG,
                                conn.getHeaderField(TransformStats.KEY_BS_SERVER_TAG));

                        mBlockTransferLog.setXbsRequestId(conn.getHeaderField(TransformStats.KEY_BS_SERVER_TAG));
                        mBlockTransferLog.setPcsRequestId(conn.getHeaderField(TransformStats.KEY_PCS_SERVER_TAG));

                        mTransferLog.setXbsRequestId(conn.getHeaderField(TransformStats.KEY_BS_SERVER_TAG));
                        mTransferLog.setPcsRequestId(conn.getHeaderField(TransformStats.KEY_PCS_SERVER_TAG));
                        array.put(result);
                    } catch (final JSONException e) {
                        DuboxLog.e(TAG, "JSONException", e);
                    }
                    new TransformStats().uploadLog(BaseStats.KEY_BASE_STATS_POINTS, array.toString(),
                            TransformStats.KEY_FILE_UPLOAD);

                    // 调试代码
                    DuboxLog.d(TAG, "array.toString() = " + array.toString());
                    feedbackMonitorLog(
                        "blockUpload repCode != HttpURLConnection.HTTP_OK " + repCode + " errorMsg:" + errorMsg);
                    // 不重试的错误判断
                    if (repCode == HttpURLConnection.HTTP_NOT_ACCEPTABLE || isNoRetryPCSError(errCode)
                            || isNoRetryDuboxError(errCode) || isNoRetryAccountError(errCode)) {
                        status = false;
                        DuboxLog.d(TAG, "HTTP repCode not HTTP_OK  PCS errCode = " + errCode
                                + "; reqCode = " + repCode);
                        feedbackMonitorLog("blockUpload no retry" + repCode + " errorMsg:" + errorMsg);
                        throw new StopRequestException(errCode, "pcsUploadFile no retry");
                    }
                    return false;
                }
            } catch (SocketTimeoutException e) {
                SpeedUploadUtils.getInstance().addExceptionRecord(e, SpeedUploadUtils.OP_TYPE_UPLOAD);
                stopReason = SpeedUploadUtils.REASON_TIME_OUT;
                feedbackMonitorLog("blockUpload parse http responce error SocketTimeoutException " + e.getMessage());
                DuboxLog.e(TAG, "doInBackground, parse http responce error SocketTimeoutException::", e);
                StatisticsLog.countUploadFailedByNetworkError();
                status = false;
                return false;
                // 有信号无网络状态出现的异常 不可知的主机异常 YQH 20121115
            } catch (UnknownHostException e) {
                feedbackMonitorLog("blockUpload parse http responce error UnknownHostException " + e.getMessage());
                SpeedUploadUtils.getInstance().addExceptionRecord(e, SpeedUploadUtils.OP_TYPE_UPLOAD);
                DuboxLog.e(TAG, "doInBackground, parse http responce error UnknownHostException::", e);
                StatisticsLog.countUploadFailedByNetworkError();
                status = false;
                return false;
                // 有信号无网络状态出现的异常 TimeoutException YQH 20121115
            } catch (SocketException e) {
                feedbackMonitorLog("blockUpload parse http responce error SocketException " + e.getMessage());
                SpeedUploadUtils.getInstance().addExceptionRecord(e, SpeedUploadUtils.OP_TYPE_UPLOAD);
                DuboxLog.e(TAG, "UnknownHostException =" + e.getMessage(), e);
                status = false;
                return false;
            } catch (NullPointerException e) {
                // system bug on android sdk 19
                // java.net.NetworkInterface.getNetworkInterfacesList(NetworkInterface.java:304)
                feedbackMonitorLog("blockUpload parse http responce error NullPointerException " + e.getMessage());
                SpeedUploadUtils.getInstance().addExceptionRecord(e, SpeedUploadUtils.OP_TYPE_UPLOAD);
                DuboxLog.e(TAG, "NullPointerException", e);
                status = false;
                return false;
            } catch (IOException e) {
                feedbackMonitorLog("blockUpload parse http responce error IOException " + e.getMessage());
                SpeedUploadUtils.getInstance().addExceptionRecord(e, SpeedUploadUtils.OP_TYPE_UPLOAD);
                DuboxLog.e(TAG, "IOException", e);
                StatisticsLog.countUploadFailedByNetworkError();
                status = false;
                return false;
            } finally {
                // 速度上报
                if (!status && NetWorkVerifier.isNoNetwork()) {
                    stopReason = SpeedUploadUtils.REASON_NO_NET;
                }
                SpeedUploadUtils.getInstance().addSpeedRecord(fileSize, size, startTime, serverHost, url,
                        SpeedUploadUtils.OP_TYPE_UPLOAD, status, "-1", stopReason, blockLen, requestId);
                if (mOptions.isRateCalculateEnable()) {
                    resetRateCalculator();
                }
                if (uploadBuffer != null) {
                    try {
                        uploadBuffer.close();
                    } catch (IOException e) {
                        DuboxLog.e(TAG, "", e);
                    }
                }
                conn.disconnect();

                if (randomFile != null) {
                    try {
                        randomFile.close();
                    } catch (IOException e1) {
                        DuboxLog.e(TAG, "randomFile.close() IOException", e1);
                    }
                }

                DuboxLog.d(TAG, "block upload finally done");
            }
        }

        /**
         * 生成{@link HttpURLConnection}
         *
         * @return
         * @throws IOException
         * @author 孙奇 V 1.0.0 Create at 2012-12-2 下午10:16:44
         */
        private HttpURLConnection getHttpURLConnection(UploadUrlInfo uploadUrlInfo) {
            URL url = getURL(uploadUrlInfo);
            if (url == null) {
                return null;
            }

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                if (conn == null) {
                    feedbackMonitorLog("getHttpURLConnection conn == null");
                    return null;
                }

                DuboxLog.i(TAG, "my box url = " + url.toString());

                    // 添加stoken，lcx 2016-6-2 7.13.1
                    String cookie = Constants.DUBOX_BDUSS_FIELD_NAME + "=" + mBduss;
                    cookie = mStokenManager.addPanPsc(cookie);
                    cookie = mStokenManager.addSToken(cookie);
                    cookie = mStokenManager.addPanNdutFmt(cookie);
                    conn.setRequestProperty(Constants.DUBOX_COOKIE_TAG, cookie);

                conn.setConnectTimeout(20000);
                conn.setReadTimeout(20000);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setRequestProperty("User-Agent", RequestCommonParams.getUserAgent());
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
                conn.setAllowUserInteraction(false);
                conn.setRequestProperty("Content-Transfer-Encoding", "binary");
                conn.setRequestProperty("Content-Length", String.valueOf(bodyLength));

                if (!TextUtils.isEmpty(uploadUrlInfo.host)) {
                    conn.setRequestProperty("Host", uploadUrlInfo.host);
                    DuboxLog.d(TAG, "host:" + uploadUrlInfo.host);
                }

                if (bodyLength > 0) {
                    conn.setFixedLengthStreamingMode(bodyLength);// 可以避免产生OOM
                }
            } catch (IOException e) {
                feedbackMonitorLog("getHttpURLConnection IOException:" + e.getMessage());
                DuboxLog.e(TAG, "getHttpURLConnection IOException", e);
                if (conn != null) {
                    conn.disconnect();
                }
                StatisticsLog.countUploadFailedByNetworkError();
                return null;
            } catch (SecurityException e) {
                feedbackMonitorLog("getHttpURLConnection SecurityException:" + e.getMessage());
                DuboxLog.e(TAG, "getHttpURLConnection SecurityException", e);
                if (conn != null) {
                    conn.disconnect();
                }
                return null;
            }

            return conn;
        }

        private void initFileBlockName(int blockIndex) {
            StringBuffer sb = new StringBuffer();
            sb.append(mFileName);
            sb.append(blockIndex);
            sb.append(boundary);
            fileBlockName = sb.toString();
        }

        private void initBodyHeader(String fileBlockName) {
            StringBuffer boundaryStart = new StringBuffer("----").append(boundary).append("\r\n");
            boundaryStart.append("Content-Disposition: form-data; name=\"Filename\"").append("\r\n\r\n");
            boundaryStart.append(fileBlockName).append("\r\n");
            boundaryStart.append("----").append(boundary).append("\r\n");
            boundaryStart.append("Content-Disposition: form-data; name=\"FileNode\"; filename=\"").append(fileBlockName)
                    .append("\"\r\n");
            boundaryStart.append("Content-Type: application/octet-stream").append("\r\n\r\n");
            bodyHeader = boundaryStart.toString().getBytes();
        }

        private void initBodyEnding() {
            StringBuffer boundaryUpload = new StringBuffer("\r\n----").append(boundary).append("\r\n");
            boundaryUpload.append("Content-Disposition: form-data; name=\"Upload\"").append("\r\n\r\n");
            boundaryUpload.append("Submit Query").append("\r\n");
            boundaryUpload.append("----").append(boundary).append("--");
            bodyEnding = boundaryUpload.toString().getBytes();
        }

        private void initBodyLength(byte[] bodyHeader, byte[] bodyEnding, int blockLength) {
            if (bodyHeader == null || bodyEnding == null) {
                bodyLength = -1;
                return;
            }
            bodyLength = bodyHeader.length + bodyEnding.length + blockLength;
        }

        private URL getURL(UploadUrlInfo uploadUrlInfo) {
            String loadUrl = String.format(HostURLManager.INSTANCE.getUPLOAD_TMPFILE_URL(), uploadUrlInfo.server,
                    Uri.encode(mRemotePath), "" + (dynamicUploadBlockSize * blockIndex)) + "&uploadid="
                    + Uri.encode(mUploadId) + "&partseq=" + blockIndex;

            if (!TextUtils.isEmpty(mSign)) {
                loadUrl += "&uploadsign=" + Uri.encode(mSign);
            }

            // 加入安全参数 libin09 2015-7-27
            loadUrl = NetworkUtil.addRand(loadUrl, mBduss, mUid);

            loadUrl = getAuthParams(loadUrl);

            try {
                DuboxLog.d(TAG, "loadUrl:" + loadUrl);
                return new URL(loadUrl);
            } catch (MalformedURLException e) {
                feedbackMonitorLog("getURL MalformedURLException:" + e.getMessage());
                StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FAILED_OTHER);
                DuboxLog.e(TAG, "MalformedURLException", e);
                return null;
            }
        }

        /**
         * 设置分散认证参数
         *
         * @param url
         * @return
         */
        private String getAuthParams(String url) {
            HttpParams params = new HttpParams();

            params.add("devuid", AppCommon.DEVUID);
            params.add("clienttype", RequestCommonParams.getClientType());
            params.add("channel", RequestCommonParams.getChannel());
            params.add("version", AppCommon.VERSION_DEFINED);
            params.add("logid", RequestCommonParams.getLogId());
            if (AppCommon.FIRST_LAUNCH_TIME > 0) {
                params.add("firstlaunchtime", String.valueOf(AppCommon.FIRST_LAUNCH_TIME));
            }

            // 7.10 加入新的校验参数 libin09 2015-7-19
            if (!TextUtils.isEmpty(mUid)) {
                NetworkUtil.addRand(url, params, mBduss, mUid);
            }

            // 7.18.2
            params.add(Constants.APN, NetworkUtil.getCurrentNetworkAPN());

            Context context = BaseApplication.getInstance();
            if (context != null) {
                params.add("cuid", CommonParam.getCUID(context));
                // 加入网络情况
                NetworkUtil.addNetworkType(context, params);
            }
            params.add(PhoneStatusKt.ISO_KEY, PhoneStatusKt.getSimCarrierInfo(context));
            params.add(AppCommon.COMMON_PARAM_APP_ID, AppCommon.getSecondBoxPcsAppId());
            params.add("app_name", "moder");
            // 转换成url参数字符串
            if (!url.contains("?")) {
                url += "?";
            } else if (!url.endsWith("?")) {
                url += "&";
            }
            url += params.toString();
            return url;
        }
    }

    /**
     * 初始化进度条相关
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-4 上午03:59:17
     */
    private void initProgress() {
        lastUpdateClock = 0;
        final boolean isEnableBlockUpload = ConfigBlockUpload.INSTANCE.enable();
        if (isEnableBlockUpload) {
            Cursor cursor = null;
            try {
                cursor = BaseApplication.getInstance().getContentResolver()
                        .query(TransferContract.UploadTasks.buildProcessingUri(mBduss),
                                new String[]{TransferContract.TasksColumns.OFFSET_SIZE},
                                TransferContract.Tasks._ID + "=?",
                                new String[]{String.valueOf(mTaskId)}, null);

                if (cursor == null || cursor.getCount() == 0) {
                    return;
                }

                if (cursor.moveToFirst()) {
                    mBlockUploadLen.set(Math.max(cursor.getInt(0), 0));
                    DuboxLog.d(TAG, "initProgress() mBlockUploadLen=" + mBlockUploadLen);
                }
            } catch (Exception e) {
                DuboxLog.e(TAG, "", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private boolean isNoRetryDuboxError(int errorCode) {
        DuboxLog.d(TAG, "isNoRetryError :: errorCode " + errorCode);
        switch (errorCode) {
            case COOKIE_OUT_OF_DATE_NO_KEY:
            case COOKIE_OUT_OF_DATA_INVALID:
            case INVALID_COOKIE:
            case FILE_OR_DIR_NAME_IVALID:
            case FILE_ALREADY_EXIST:
            case FILE_OR_DIR_NOT_EXIST:
            case SPACE_FULL:
            case FREE_SPACE_FULL:
            case SERVICE_DOWNGRADE:
                return true;
            default:
                return false;
        }
    }

    private boolean isNoRetryPCSError(int errorCode) {
        DuboxLog.d(TAG, "isNoRetryPCSError :: errorCode " + errorCode);
        switch (errorCode) {
            case STOKEN_ERROR:
            case USER_NOT_EXISTS:
            case USER_IS_NOT_AUTHORIZED:
            case USER_IS_NOT_LOGIN:
            case BDUSS_IS_INVALID:
                return true;
            default:
                return false;
        }
    }

    private boolean isNoRetryAccountError(int errorCode) {
        DuboxLog.d(TAG, "isNoRetryAccountError :: errorCode " + errorCode);
        switch (errorCode) {
            case USERNAME_PASSWORD_VERIFY_FAILED:
            case UER_NOT_ACTIVATED:
            case RESULT_BDUSS_INVALID:
                return true;
            default:
                return false;
        }
    }

    /**
     * @param size
     * @param rate
     * @see Transmitter#calculate(long, long)
     */
    @Override
    protected void calculate(long size, long rate) {
        mOffsetSize = size;
        if (rate > 0L) {
            mRate = rate;
        }

        if (mTransferLog != null) {
            mTransferLog.setOffsetSize(mOffsetSize);
            mTransferLog.setFileRate(rate);
        }

        if (mBlockTransferLog != null) {
            mBlockTransferLog.setFileRate(rate);
        }

        if (mOptions != null && mOptions.getStatusCallback() != null) {
            mOptions.getStatusCallback().onUpdate(size, rate);
        }
    }

    protected boolean isModify() {
        return mLastModifiedTime != mLocalFile.lastModified() ;
    }

    /**
     * 删除压缩的临时文件
     */
    private void deleteCompressFile() {

    }

    /**
     * 同步网盘空间容量
     */
    private void syncDuboxSpace() {
        try {
            mQuota = getQuota();
        } catch (IOException e) {
            DuboxLog.w(TAG, "", e);
            return;
        } catch (RemoteException e) {
            DuboxLog.w(TAG, "", e);
            return;
        }
        // 空间剩余容量小于100kb，认为容量已满，不进行上传任务和备份任务调度，并且暂停任务
        Account.INSTANCE.setSpaceFull(mQuota != null && (mQuota.total - mQuota.used <= 102400));
    }

    /**
     * 请求server容量接口
     *
     * @return
     * @throws RemoteException
     * @throws IOException
     */
    private Quota getQuota() throws RemoteException, IOException {
        try {
            return new CloudFileApi(mBduss, mUid).getQuota(false);
        } catch (JSONException e) {
            DuboxLog.e(TAG, "", e);
            return null;
        }
    }

    /**
     * 出现错误时候回调
     *
     * @param errorCode
     */
    private void callBackError(int errorCode, String errorMessage) {
        DuboxLog.i(TAG, "callBackError");
        feedbackMonitorError(errorCode, "callBackError errorCode:" + errorCode + " errorMessage:" + errorMessage);
        if (mOptions.getStatusCallback() != null) {
            mOptions.getStatusCallback().onFailed(errorCode, null);
        }

        if (mBlockTransferLog != null) {
            mBlockTransferLog.setOtherErrorCode(errorCode);
            mBlockTransferLog.setOtherErrorMessage(errorMessage);
            mBlockTransferLog.setCurrentUploadType(TransferLog.LogUploadType.BLOCK_FAIL);
            mLogTaskManager.addLogTask(mLogGenerator, mBlockTransferLog);
        }

        if (mTransferLog.getEndTime() == 0L) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FAIL);
        }
        mTransferLog.setUploadType(mSource);
        mTransferLog.setOtherErrorCode(errorCode);
        mTransferLog.setOtherErrorMessage(errorMessage);
        mTransferLog.setCurrentUploadType(TransferLog.LogUploadType.FILE);
        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    /**
     * 回调成功
     */
    private void callBackSuccess(String rawContent) {
        feedbackMonitorLog("callBackSuccess rawContent:" + rawContent);
        DuboxLog.i(TAG, "callBackSuccess");
        if (mOptions.getStatusCallback() != null) {
            mOptions.getStatusCallback().onSuccess(rawContent);
        }

        if (mTransferLog.getEndTime() == 0L) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        mTransferLog.setUploadType(mSource);
        mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_FINISH);
        mTransferLog.setOffsetSize(fileSize);
        mTransferLog.setCurrentUploadType(TransferLog.LogUploadType.FILE);
        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    /**
     * 回调暂停
     */
    public void callBackPause() {
        feedbackMonitorLog("callBackPause");
        DuboxLog.i(TAG, "callBackPause");

        if (mOptions.getStatusCallback() != null && (mOptions.getStatusCallback() instanceof ITransferStatusCallback)) {
            ((ITransferStatusCallback) mOptions.getStatusCallback()).onPause();
        }

        if (mBlockTransferLog != null) {
            if (mBlockTransferLog.getEndTime() == 0L) {
                mBlockTransferLog.setEndTime(System.currentTimeMillis());
            }
            if (mBlockTransferLog.getFinishStates() == 0) {
                mBlockTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_PAUSE);
            }
            mBlockTransferLog.setCurrentUploadType(TransferLog.LogUploadType.BLOCK_FAIL);
            mLogTaskManager.addLogTask(mLogGenerator, mBlockTransferLog);
        }

        if (mTransferLog.getEndTime() == 0L) {
            mTransferLog.setEndTime(System.currentTimeMillis());
        }
        if (mTransferLog.getFinishStates() == 0) {
            mTransferLog.setTaskFinishStates(TransferFieldKey.TRANSFER_PAUSE);
        }

        mTransferLog.setCurrentUploadType(TransferLog.LogUploadType.FILE);
        mLogTaskManager.addLogTask(mLogGenerator, mTransferLog);
    }

    private void feedbackMonitorLog(String log) {
        String s = log + ":UploadTransmitter:";
        if (mLocalFile != null) {
            s = s + mLocalFile.localUrl();
        }
        DriveContext.reportFeedbackmonitorUploadLog(s);
    }

    private void feedbackMonitorError(int errNo, String errorMsg) {
        String s = errorMsg + ":UploadTransmitter:";
        if (mLocalFile != null) {
            s = s + mLocalFile.localUrl();
        }
        DriveContext.reportFeedbackmonitorUploadError(errNo, s);
    }

    /**
     * 分片上传回调
     */
    public interface BlockUploadListener {
        /**
         * 上传进度
         *
         * @param doneLen 已上传数据大小
         */
        void onProgress(long doneLen);

        /**
         * 上传成功
         *
         * @param blockIndex 分片索引
         */
        void onSuccess(int blockIndex);

        /**
         * 上传失败
         *
         * @param blockIndex 分片索引
         * @param errorCode 分片错误码
         */
        void onError(int blockIndex, int errorCode);
    }
}