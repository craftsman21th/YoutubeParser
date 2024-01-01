/*
 * PCSDownloadTransmitter.java
 * classes : com.dubox.drive.task.transmit.PCSDownloadTransmitter
 * @author 文超
 * V 1.0.0
 * Create at 2013-6-6 下午4:16:06
 */
package com.moder.compass.transfer.transmitter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.common.util.CommonParam;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.base.network.NetworkUtil;
import com.dubox.drive.base.network.StokenManager;
import com.moder.compass.base.storage.config.ConfigAlertText;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.kernel.util.PhoneStatusKt;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpParams;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.log.transfer.DownloadLog;
import com.moder.compass.log.transfer.InstantDownloadLog;
import com.moder.compass.log.transfer.TransferFieldKey;
import com.moder.compass.log.transfer.TransferLog;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.points.BaseStats;
import com.moder.compass.statistics.points.TransformStats;
import com.moder.compass.transfer.io.model.LocateDownloadResponse;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.dubox.drive.db.preview.contract.PreviewContract;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;
import com.moder.compass.transfer.transmitter.locate.LocateDownload;
import com.moder.compass.transfer.transmitter.locate.LocateDownloadUrls;
import com.moder.compass.transfer.transmitter.throwable.Retry;
import com.moder.compass.transfer.transmitter.throwable.RetryLocateDownload;
import com.moder.compass.transfer.transmitter.throwable.StopRequestException;
import com.moder.compass.transfer.transmitter.util.SpeedUploadUtils;
import com.moder.compass.Target30StorageKt;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;

/**
 * @author 文超 <br/>
 *         create at 2013-6-6 下午4:16:06
 */
public class PCSDownloadTransmitter extends SingleThreadMultiPartDownloadTransmitter implements PCSTransmitErrorCode {
    private static final String TAG = "PCSDownloadTransmitter";

    /**
     * 返回header不包含这个字符串的就是类似moder mobile这样的没准入的网络
     *
     * @author 孙奇 V 1.0.0 Create at 2013-1-9 下午04:54:59
     */
    private static final String PCS_SERVER_TAG = "x-pcs-request-id";
    private static final String BS_SERVER_TAG = "x-bs-request-id";

    LocateDownload mLocateDownload;

    /**
     * 试用加速下载
     **/
    LocateDownload mProbationaryLocateDownload;

    /**
     * 当前连接的服务器IP （仅供数据采集用）
     **/
    private String mTargetServerIp;
    /**
     * 当前连接的URL（仅供数据采集用）
     **/
    private LocateDownloadUrls mTargetUrl;

    /**
     * 当前连接错误请求id()（仅供数据采集用）
     **/
    private String mRequestId = "";

    /**
     * PCS的serverPath
     */
    protected String mServerPath;

    protected final String mBduss;

    protected final String mUid;

    /**
     * 传输文件的md5
     */
    private String mServerMD5;

    /**
     * STOKEN管理器
     *
     * @author lcx 2016-6-3
     * @since 7.13.1
     */
    private final StokenManager mStokenManager;

    /**
     * 当下载文件的大小超过文件的size时候，添加一条日志，一次任务自添加一次
     */
    private boolean mFileDownloadOver = false;

    /**
     * 重试调用loacatedownload接口次数
     */
    private int mRetryLocateDownloadTimes = 0;

    public PCSDownloadTransmitter(int taskId, String serverPath, RFile localFile, long size,
                                  TransmitterOptions options, String serverMD5, String bduss, String uid) {
        super(taskId, localFile, size, options);
        mServerPath = serverPath;
        mServerMD5 = serverMD5;
        mBduss = bduss;
        mUid = uid;

        mTransferLog = new DownloadLog(uid);
        mTransferLog.setCurrentUploadType(TransferLog.LogUploadType.FILE);
        mTransferLog.setLogTaskId(Account.INSTANCE.getUid() + "_" + System.currentTimeMillis());
        if (options.getTransferCalculable() != null) {
            mTransferLog.setTransferCalculable(options.getTransferCalculable());
        }

        mInstantDownloadLog = new InstantDownloadLog(uid, TransferFieldKey.FileTypeKey.DownloadType.Normal);
        mInstantDownloadLog.setCurrentUploadType(TransferLog.LogUploadType.FILE);
        mInstantDownloadLog.setLogTaskId(mTransferLog.getLogTaskId());
        if (options.getTransferCalculable() != null) {
            mInstantDownloadLog.setTransferCalculable(options.getTransferCalculable());
        }

        mStokenManager = new StokenManager(mBduss);

        mFileInfo.fileName = FileUtils.getFileName(serverPath);
    }

    /**
     * dlink使用
     * @param taskId 任务ID
     * @param localFile 目标路径
     * @param size 文件大小
     * @param options 传输器的配置器
     * @param bduss 用户标识bduss
     * @param uid 用户标识uid
     */
    PCSDownloadTransmitter(int taskId, RFile localFile, long size, TransmitterOptions options,
                           String bduss, String uid) {
        this(taskId, null, localFile, size, options, null, bduss, uid);
    }

    /**
     * 构建连接
     *
     * @param block 传输块
     * @return http连接
     * @throws Retry 重试异常
     */
    private HttpURLConnection buildHttpConnection(TransmitBlock block, String url)
            throws Retry {
        HttpURLConnection connection = null;
        try {
            URL conURL = new URL(getAuthParams(url));
            connection = (HttpURLConnection) conURL.openConnection();
            connection.setConnectTimeout(Constants.READ_TIMEOUT);
            connection.setReadTimeout(Constants.READ_TIMEOUT);
            addHeaders(connection, block);
            if (mTargetUrl != null && !TextUtils.isEmpty(mTargetUrl.host)) {
                connection.addRequestProperty("Host", mTargetUrl.host);
            }
            getConnectionInfo(conURL);
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            StatisticsLog.countDownloadFailedByNetworkError();
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + e.getMessage());
        } catch (NumberFormatException e) {
            if (connection != null) {
                connection.disconnect();
            }
            // 7.12 修复MTJ报错，java.lang.NumberFormatException: Invalid int: "2720652591"
            // 部分情况下接收responseHeader时超出限制
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_NUMBER_FORMAT_EXCEPTION, "NumberFormatException " + e.getMessage());
        }
        return connection;
    }

    /**
     * 设置分散认证参数
     *
     * @param url 原始url
     * @return 加入认证参数的url
     */
    private String getAuthParams(String url) {
        if (!HostURLManager.checkDBDomain(url)) {
            return url;
        }
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

    private void getConnectionInfo(URL url) {
        /* 当前连接的服务器端口（仅供数据采集用） */
        try {
            int targetServerPort = url.getPort();
            DuboxLog.d(TAG, "ServerPort:" + targetServerPort);
            InetAddress address = InetAddress.getByName(url.getHost());
            mTargetServerIp = address.getHostAddress();
        } catch (Exception e) {
            mTargetServerIp = "";
        }
    }

    /**
     * 下载
     *
     * @throws StopRequestException 停止请求异常
     * @throws Retry 重试异常
     * @author 孙奇 V 1.0.0 Create at 2012-12-7 上午02:10:08
     */
    @Override
    protected void download(TransmitBlock block) throws StopRequestException, Retry,
            RetryLocateDownload, FileNotFoundException {
        DuboxLog.i(TAG, "download");
        BufferedInputStream downloadBuffer;
        HttpURLConnection connection = null;
        OutputStream os = null;
        RandomAccessFile file = null;
        long startTime = 0L;
        boolean status = true;
        long fileSize = 0L;
        long completeSize = 0L;
        long partSize = 0L;
        String url = "";
        try {
            if ((block.startPosition + block.completeSize - 1) == block.endPosition) {
                // 如果offset等于size建立连接range会报416错误
                DuboxLog.d(TAG, "already download success only need rename");
                return;
            }
            if (mLocateDownload != null && mLocateDownload.isLocateDownloadHasError()) {
                handleExceptionalLocateDLResponse(mLocateDownload);
            }
            checkConnectivity();
            file = setupDestinationFile(block);
            os = getOutputStream(block);
            mTargetUrl = getUrlString(block);

            // 添加网络参数，7.11 libin09 2015-8-24
            mTargetUrl.url = NetworkUtil.addNetworkType(BaseApplication.getInstance(), mTargetUrl.url);

            mTransferLog.setRequestUrl(mTargetUrl.url);
            mTransferLog.initNetWorkType();

            connection = buildHttpConnection(block, mTargetUrl.url);
            mTransferLog.setServerIp(NetworkUtil.getServerIP(connection.getURL()));

            checkBlockFile();// 下载开始检查一次

            handleExceptionalResponseCode(connection);
            handleExceptionalHeader(connection);
            downloadBuffer = openResponseEntity(connection);

            // 数据接收过程中 ， 有信号无网络此处抛出IOException YQH 20121116
            startTime = System.currentTimeMillis();


            transferData(file, os, downloadBuffer, block);

            fileSize = block.fileSize;
            completeSize = block.completeSize;
            partSize = block.endPosition - block.startPosition;
            url = mTargetUrl == null ? "" : mTargetUrl.url;
            if (!isPause && ((block.startPosition + block.completeSize - 1) < block.endPosition)) {
                DuboxLog.e(TAG, "download retry offSet != size");
                status = false;
                throw new Retry(OtherErrorCode.RETRY_COMPLETE_SIZE_LESS_FILE_SIZE, "download completeSize < fileSize");
            } else if (!isPause && ((block.startPosition + block.completeSize - 1) > block.endPosition)) {
                if (!mFileDownloadOver) {
                    mFileDownloadOver = true;
                }

                block.completeSize = 0L;
                status = false;
                throw new Retry(OtherErrorCode.RETRY_COMPLETE_SIZE_OVER_FILE_SIZE, "download completeSize > fileSize");
            }
            if (mTargetUrl != null) {
                DuboxLog.d(TAG, "download transferData done： " + mTargetUrl.toString());
            }
            DuboxLog.i(TAG, "download transferData done");
        } finally {
            DuboxLog.d(TAG, "url:" + url);
            DuboxLog.d(TAG, "mTargetServerIp:" + mTargetServerIp);
            SpeedUploadUtils.getInstance().addSpeedRecord(fileSize, completeSize, startTime, mTargetServerIp, url,
                    SpeedUploadUtils.OP_TYPE_DOWNLOAD, status, "0", 0, partSize, mRequestId);
            DuboxLog.i(TAG, "get finally.");
            try {
                if (file != null) {
                    file.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                DuboxLog.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * 处理locatedownload的错误响应,目前仅针对防盗链的错误进行了处理，没有改变原有逻辑
     * @param locateDownload {@link LocateDownload}
     */
    private void handleExceptionalLocateDLResponse(LocateDownload locateDownload) throws StopRequestException,
            RetryLocateDownload {
        LocateDownloadResponse response = locateDownload.getResponse();
        if (response != null) {
            if (response.errorCode == SERVER_FORBIDDEN_USER) {
                errMsgForShow = Uri.decode(response.errorInfo);
                if (TextUtils.isEmpty(errMsgForShow)) {
                    ConfigAlertText config = new ConfigAlertText("");
                    if (config.isShowForbiddenAlert) {
                        errMsgForShow = config.forbiddenUserDownloadAlertText;
                    }
                }
            }
            ErrorMessageHelper.checkPCSErrorNo(response.errorCode);
            if (response.redo == ErrorMessageHelper.NO_REDO_DEFAULT
                    || isNoRetryLocateDownloadError(response.httpCode)) {
                throw new StopRequestException(response.errorCode, "PCS ERRORCODE :: " + response.errorCode);
            } else {
                throw new RetryLocateDownload(response.errorCode,
                        "PCS ERRORCODE :: " + response.errorCode, response.redo);
            }
        }
    }

    @Override
    protected void doRetryLocateDownload(RetryLocateDownload t) throws StopRequestException {
        if (t.mCount != ErrorMessageHelper.NO_REDO_DEFAULT) {
            if (mRetryLocateDownloadTimes < t.mCount) {
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException e) {
                    DuboxLog.e(TAG, "retry InterruptedException ", e);
                    throw new StopRequestException(OtherErrorCode.THREAD_INTERRUPTED,
                            "Thread.sleep InterruptedException" + e.getMessage(), e);
                }
                mRetryLocateDownloadTimes++;
                mLocateDownload.setTimeExpire();
                mFileInfo.setUrls(initUrls());
            } else {
                throw new StopRequestException(t.mFinalStatus, "retry over max time fail task " + t.getMessage());
            }
        }
    }

    @Override
    protected void doRetry(Retry t) throws StopRequestException {
        if (t.mFinalStatus == TransmitterConstant.NETWORK_VERIFY_CHECKING) {
            DuboxLog.d(TAG, "doRetry NETWORK_VERIFY_CHECKING");
            if (mOptions.isNetworkVerifier()) {
                networkVerifierCheck();
                return;
            }
            throw new StopRequestException(OtherErrorCode.CHECK_NETWORK_RETRY_OVER_TIME,
                    "check network time over time " + t.getMessage());
        }

        if (t.mFinalStatus == TransmitterConstant.PCS_LINK_EXPIRE_TIME) {
            try {
                processPCSLinkExpireTime();
            } catch (StopRequestException e) {
                DuboxLog.e(TAG, "errorCode", e);
            }
            return;
        }

        // fileVerifierCheck(t);
        DuboxLog.d(TAG, "doRetry not NETWORK_VERIFY_CHECKING");
    }

    /**
     * @param conn http 连接
     */
    @Override
    protected void addHeaders(HttpURLConnection conn, TransmitBlock bean) {
        super.addHeaders(conn, bean);
        conn.setRequestProperty("User-Agent", RequestCommonParams.getUserAgent());
        if (mLocateDownload != null) {
            String host = mLocateDownload.getHost();
            if (!TextUtils.isEmpty(host)) {
                conn.setRequestProperty("Host", host);
            }
        }

        boolean isBdDomain = HostURLManager.checkDBDomain(conn.getURL().getAuthority());
        if (isBdDomain) {
            String cookie = Constants.DUBOX_BDUSS_FIELD_NAME + "=" + mBduss;
            // 添加stoken，libin09 2016-5-18 7.13.0
            cookie = mStokenManager.addPanPsc(cookie);
            cookie = mStokenManager.addSToken(cookie);
            cookie = mStokenManager.addPanNdutFmt(cookie);
            conn.setRequestProperty(Constants.DUBOX_COOKIE_TAG, cookie);

        } else {
            conn.setRequestProperty(Constants.DUBOX_COOKIE_TAG,
                    Constants.DUBOX_BDUSS_FIELD_NAME + "=" + mBduss);
        }
    }

    @Override
    protected void handleExceptionalResponseCode(HttpURLConnection response) throws StopRequestException, Retry {
        // 有信号无网络此处抛出UnknownHostException YQH 20121116
        int resp;
        try {
            resp = response.getResponseCode();
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + e.getMessage());
        } catch (NullPointerException e) {
            // 添加对getResponseCode的系统内部出现空指针异常的保护
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_NULL_EXCEPTION, "NullPointerException " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            // 添加对getResponseCode的步步高Vivo X510t系统内部出现异常的保护
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_OUT_OF_BOUNDS_EXCEPTION, "OutOfBoundsException " + e.getMessage());
        } catch (Exception e) {
            // 添加对getResponseCode的步步高Vivo X510t系统内部出现异常的保护
            DuboxLog.e(TAG, e.getMessage(), e);
            throw new Retry(OtherErrorCode.RETRY_OUT_OF_BOUNDS_EXCEPTION, "OutOfBoundsException " + e.getMessage());
        }

        // TESTONLY
        // resp = 401;

        DuboxLog.d(TAG, "handleExceptionalResponseCode：：resp = " + resp);

        if (resp != HttpURLConnection.HTTP_OK && resp != HttpURLConnection.HTTP_PARTIAL) {
            DuboxLog.d(TAG, "Error responseCode=" + resp);
            String errMsg;
            InputStream contentStream = null;
            try {
                try {
                    contentStream = response.getInputStream();
                } catch (IOException e) {
                    contentStream = response.getErrorStream();
                }
                if (contentStream == null) {
                    throw new Retry(resp, "reponse stream null");
                }
                errMsg = ErrorMessageHelper.readErrorMsg(contentStream);
            } catch (IOException e) {
                DuboxLog.d(TAG, e.getMessage(), e);
                throw new Retry(OtherErrorCode.RETRY_STREAM_EXCEPTION, "IOException " + e.getMessage());
            } finally {
                if (contentStream != null) {
                    try {
                        contentStream.close();
                    } catch (IOException e) {
                        DuboxLog.w(TAG, "Error contentStream", e);
                    }
                }
            }

            // TESTONLY
            // errMsg = "{ \"error_code\":31064, \"error_msg\":\"xcode expire time out error\"}";
            int errCode = ErrorMessageHelper.readErrorCode(errMsg);
            if (errCode == SERVER_FORBIDDEN_USER) {
                errMsgForShow = ErrorMessageHelper.readForbiddenErrMsgForShow(errMsg);
            }
            mTransferLog.setHttpErrorCode(resp);
            mTransferLog.setPcsErrorCode(errCode);

            DuboxLog.d(TAG, "handleExceptionalResponseCode::errMsg = " + errMsg);
            mRequestId = ErrorMessageHelper.readRequestId(errMsg);

            final JSONArray array = new JSONArray();
            try {
                final JSONObject result = new JSONObject();
                result.put(TransformStats.KEY_HTTP_ERROR_CODE, resp);
                result.put(TransformStats.KEY_PCS_ERROR_CODE, errCode);
                result.put(TransformStats.KEY_PCS_ERROR_MSG, errMsg);
                result.put(TransformStats.KEY_REMOTE_IP, mTargetServerIp);
                result.put(TransformStats.KEY_FILE_URL, mTargetUrl.url);
                String field = response.getHeaderField(TransformStats.KEY_PCS_SERVER_TAG);
                if (!TextUtils.isEmpty(field)) {
                    result.put(TransformStats.KEY_PCS_SERVER_TAG, field);
                    mTransferLog.setPcsRequestId(field);
                }
                field = response.getHeaderField(TransformStats.KEY_BS_SERVER_TAG);
                if (!TextUtils.isEmpty(field)) {
                    result.put(TransformStats.KEY_BS_SERVER_TAG, field);
                    mTransferLog.setXbsRequestId(field);
                }
                array.put(result);
            } catch (final JSONException e) {
                DuboxLog.e(TAG, "JSONException", e);
            }
            new TransformStats().uploadLog(BaseStats.KEY_BASE_STATS_POINTS, array.toString(),
                    TransformStats.KEY_FILE_DOWNLOAD);

            if (FILE_IS_NOT_AUTHORIZED == errCode && !TextUtils.isEmpty(errMsg)
                    && errMsg.contains("expire time")) {
                throw new Retry(TransmitterConstant.PCS_LINK_EXPIRE_TIME,
                        TransmitterConstant.getExceptionMsg(TransmitterConstant.PCS_LINK_EXPIRE_TIME));
            }

            // 根据服务端下发的重试redoCount判断是否需要重试
            if (isNoRetryServerError(resp, errCode, errMsg)) {
                if (errCode == FILE_DOES_NOT_EXISTS) {
                    StatisticsLog
                            .updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FAILED_FILE_NOT_EXISTS);
                }
                throw new StopRequestException(errCode, "PCS ERRORCODE :: " + errCode);
            }

            DuboxLog.i(TAG, "Error ErrorCode =" + errCode);
            ErrorMessageHelper.checkPCSErrorNo(errCode);
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DOWNLOAD_FAILED_SERVER_ERROR);
            throw new Retry(errCode, "download file error retry");
        }
    }

    /**
     * 是否是不需要重试LocateDownload的错误码，即406，防止雪崩问题
     * @param httpResponseCode http状态码
     * @return 是否不需要重试
     */
    private boolean isNoRetryLocateDownloadError(int httpResponseCode) {
        return httpResponseCode == HttpURLConnection.HTTP_NOT_ACCEPTABLE;
    }

    /**
     * 是否是不需要重试下载链接的serverError
     *
     * @param httpResponseCode http协议中的响应码
     * @param pcsErrorCode pcs返回错误码
     * @param pcsErrorMsg pcs返回的错误信息
     * @return 是否不需要重试serverError
     * @author 孙奇 V 1.0.0 Create at 2013-6-6 上午11:59:08
     */
    private boolean isNoRetryServerError(int httpResponseCode, int pcsErrorCode, String pcsErrorMsg) {
        if ((HttpURLConnection.HTTP_FORBIDDEN == httpResponseCode
                && SERVER_TEMP_INVALID != pcsErrorCode)
                || HttpURLConnection.HTTP_NOT_FOUND == httpResponseCode || 416 == httpResponseCode) {
            return true;
        }
        if (OBJECT_NOT_EXISTS == pcsErrorCode || FILE_DOES_NOT_EXISTS == pcsErrorCode || USER_NOT_EXISTS == pcsErrorCode
                || USER_IS_NOT_AUTHORIZED == pcsErrorCode || USER_IS_NOT_LOGIN == pcsErrorCode
                || BDUSS_IS_INVALID == pcsErrorCode || REMOTE_PCS_FILE_IS_IMPERFECT == pcsErrorCode
                || REMOTE_POMS_FILE_IS_IMPERFECT == pcsErrorCode || STOKEN_ERROR == pcsErrorCode) {
            return true;
        }
        if (DIGEST_NOT_MATCH == pcsErrorCode && pcsErrorMsg.contains("digest not match")) {
            return true;
        }
        return FILE_IS_NOT_AUTHORIZED == pcsErrorCode && pcsErrorMsg.contains("param wrong");
    }

    @Override
    protected void handleExceptionalHeader(HttpURLConnection response) throws StopRequestException {
        if (!TextUtils.isEmpty(response.getHeaderField(PCS_SERVER_TAG))
                || !TextUtils.isEmpty(response.getHeaderField(BS_SERVER_TAG))) {
            return;
        }
        // 类似百度Mobile的有信号无网处理
        DuboxLog.d(TAG, "isContainPCSServerTag false");
        if (mOptions.isNetworkVerifier()) {
            networkVerifierCheck();
        } else {
            throw new StopRequestException(OtherErrorCode.PCSID_OR_BCSID_IS_EMPTY, "Network not available");
        }
    }

    /**
     * 处理PCS链接过期
     *
     * @throws StopRequestException 停止请求异常
     */
    protected void processPCSLinkExpireTime() throws StopRequestException {
        if (mLocateDownload != null) {
            mLocateDownload.setTimeExpire();
        }
        mFileInfo.setUrls(initUrls());
    }

    @Override
    protected List<LocateDownloadUrls> initProbationaryUrls() {

        if (mProbationaryLocateDownload == null) {
            mProbationaryLocateDownload = new LocateDownload(mServerPath, false, mBduss, mUid);
            mProbationaryLocateDownload.initPcsServerList();
        }
        DuboxLog.d(TAG, "initProbationaryUrls");
        List<LocateDownloadUrls> list;

        String token = null;
        String timeStamp = null;
        if (mOptions.getRateLimiter() != null) {
            token = mOptions.getRateLimiter().getSpeedToken();
            timeStamp = mOptions.getRateLimiter().getSpeedTimeStamp();
        }
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(timeStamp)) {
            list = mProbationaryLocateDownload.getPcsProbationaryUrlList(token, timeStamp);
            DuboxLog.d(TAG, "token:" + token + ",timeStamp:" + timeStamp);
        } else {
            list = mProbationaryLocateDownload.getPcsUrlList();
        }

        // 需要在获取了locatedownload之后
        setSpeedThreshold(mProbationaryLocateDownload);
        return list;
    }

    @Override
    protected List<LocateDownloadUrls> initUrls() {

        DuboxLog.d(TAG, "initUrls");
        if (mLocateDownload == null) {
            mLocateDownload = new LocateDownload(mServerPath, false, mBduss, mUid);
            mLocateDownload.initPcsServerList();
        }

        List<LocateDownloadUrls> list = mLocateDownload.getPcsUrlList();

        // 需要在获取了locatedownload之后
        setSpeedThreshold(mLocateDownload);

        return list;
    }

    // 若vip身份变化则设置原下载链接超时，令mLocateDownload重新请求下载链接
    @Override
    protected void onVipLevelChange() {
        mLocateDownload.setTimeExpire();
    }

    void setSpeedThreshold(LocateDownload locateDownload) {
        long threshold = 0L;
        if (mOptions.getRateLimiter() != null) {
            threshold = locateDownload.getDownloadLimitThreshold();
            mTransferLog.setSpeedLimit(threshold);
            mOptions.getRateLimiter().updateThreshold(threshold);
        }
        saveMinos(threshold);
    }

    /**
     * 妈祖统计下载
     *
     * @param threshold 限速阈值
     */
    protected void saveMinos(long threshold) {

    }

    @Override
    public void start() {
        if (!isNeedDownload()) {
            super.start();
        } else {
            feedbackMonitorLog("start isNeedDownload = true");
        }
    }

    /**
     * 检查对方目录是否有相同文件
     *
     * @return true:文件相同直接复制即可，false:文件不同需要重新下载
     * @author 2015-06-01 liulp
     */
    private boolean isNeedDownload() {
        if (TextUtils.isEmpty(mServerPath)) {// 子类Dlink直接下载
            return false;
        }

        boolean isVideo = FileType.isVideo(mServerPath);
        if (TextUtils.isEmpty(mServerMD5)) {// 传输任务没有云端MD5时，直接下载新版本。可能是视频，也可能是老版本覆盖安装时遗留的任务
            if (!mFileInfo.destinationPath.exists()) {
                Pair<String, Long> fileInfo = getDownloadedFileInfo();
                if (fileInfo == null) {
                    return false;
                }
                File oppFile = new File(fileInfo.first);
                return oppFile.exists() && isVideo && isHandledVideoDownload(fileInfo.first);
            }

            if (mFileInfo.tempDestinationPath != null && mFileInfo.tempDestinationPath.exists()) {
                mFileInfo.tempDestinationPath.delete(BaseApplication.getInstance());
            }
            callBackSuccess();
            // 覆盖安装
            return true;
        }

        Pair<String, Long> fileInfo = getDownloadedFileInfo();
        if (fileInfo == null) {
            return false;
        }

        File localFile = new File(fileInfo.first);
        if (!localFile.exists()) {
            return false;
        }

        if (isVideo) {
            return isHandledVideoDownload(fileInfo.first);
        }
        return isHandledNotVideoDownload(fileInfo.first, fileInfo.second, localFile.lastModified());
    }

    /**
     * 下载检查预览目录
     *
     * @author 2015-06-01 liulp
     */
    protected Pair<String, Long> getDownloadedFileInfo() {
        if (TextUtils.isEmpty(mServerPath)) {
            return null;
        }

        Uri uri = PreviewContract.TaskFiles.buildUri(mBduss);
        String[] projection =
                new String[] { PreviewContract.TaskFiles.LOCAL_PATH, PreviewContract.TaskFiles.LOCAL_LAST_MODIFY_TIME };
        String select = PreviewContract.TaskFiles.SERVER_PATH;

        Cursor cursor = BaseApplication.getInstance().getContentResolver().query(uri, projection, select + "=?",
                new String[] { mServerPath }, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(0);
                long lastTime = cursor.getLong(1);
                return Pair.create(path, lastTime);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "getDownloadedFileInfo exception:", e);
            feedbackMonitorLog("getDownloadedFileInfo exception:" + e.getMessage());
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
    protected String getDownloadedFileMd5() {
        if (TextUtils.isEmpty(mServerPath)) {
            return null;
        }

        Uri uri = PreviewContract.Tasks.buildUri(mBduss);
        String[] projection = new String[] { CloudFileContract.Files.FILE_SERVER_MD5 };
        String select = PreviewContract.Tasks.REMOTE_URL + "=? AND " + PreviewContract.Tasks.STATE + "=?";

        Cursor cursor = BaseApplication.getInstance().getContentResolver().query(uri, projection, select,
                new String[] { mServerPath, String.valueOf(TransferContract.Tasks.STATE_FINISHED) }, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "getDownloadedFileMd5 exception:", e);
            feedbackMonitorLog("getDownloadedFileMd5 exception:" + e.getMessage());
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
    private String getCloudFileMd5() {
        if (TextUtils.isEmpty(mServerPath)) {
            return null;
        }

        final Cursor cursor = BaseApplication.getInstance().getContentResolver().query(
                CloudFileContract.Files.buildFileServerPathUri(mServerPath, mBduss),
                new String[] { CloudFileContract.Files.FILE_SERVER_MD5 }, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(CloudFileContract.Files.FILE_SERVER_MD5));
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
            feedbackMonitorLog("getCloudFileMd5 exception:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 检查下载的文件同文件库里面的文件对比是否变化
     *
     * @author 2015-06-01 liulp
     */
    private void checkBlockFile() throws StopRequestException {
        long cloudFileSize = getCloudFileLength();
        if (cloudFileSize > 0L && cloudFileSize != mFileInfo.fileSize) {

            Target30StorageKt.deleteTempFile(mFileInfo.tempDestinationPath,
                    mFileInfo.destinationPath, mFileInfo.isDownloadPrivateDir);

            throw new StopRequestException(TransmitterConstant.SERVER_FILE_IS_CHANGE, "server file has change");
        }
        DuboxLog.d(TAG, "checkBlockFile ok");
    }

    /**
     * 查询文件库，获取文件大小
     *
     * @return 文件的size
     */
    private long getCloudFileLength() {
        if (TextUtils.isEmpty(mServerPath)) {
            return 0L;
        }

        Cursor cursor = null;

        try {
            cursor = BaseApplication.getInstance().getContentResolver().query(
                    CloudFileContract.Files.buildFileServerPathUri(mServerPath, mBduss),
                    new String[]{CloudFileContract.Files.FILE_SIZE}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex(CloudFileContract.Files.FILE_SIZE));
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0L;
    }

    /**
     * 检查两个路径的根目录是否为默认的内存卡目录
     *
     * @param formPath  路径一
     * @param toPath 路径二
     * @return 返回两个路径根目录是否为默认内存卡目录
     */
    private boolean isSameRootPath(String formPath, String toPath) {
        if (TextUtils.isEmpty(formPath) || TextUtils.isEmpty(toPath)) {
            return false;
        }
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            return false;
        }
        String inRootDir = Environment.getExternalStorageDirectory().getPath();//获取跟SD卡目录
        return formPath.startsWith(inRootDir) && toPath.startsWith(inRootDir);
    }

    /**
     * @param localPath 本地路径
     * @return true：已经处理过 liulp
     */
    private boolean isHandledVideoDownload(String localPath) {
        // 由于预览的根目录不随着选择用户sdcard的位置变化而改变，所以无论从预览到下载或者下载到预览，只要根目录不是内存卡的目录都使用copy后删除
        if (isSameRootPath(localPath, mFileInfo.destinationPath.localUrl())) {
            // 同存储卡的是移动不需要检查空间
            boolean result = FileUtils.move(localPath, mFileInfo.destinationPath.localUrl());
            DuboxLog.d(TAG, "move video result:" + result);

            if (result) {
                if (mFileInfo.tempDestinationPath != null && mFileInfo.tempDestinationPath.exists()) {
                    mFileInfo.tempDestinationPath.delete(BaseApplication.getInstance());
                }
                DuboxLog.d(TAG, "move video ok callback success");
                callBackSuccess();
            }

            return result;
        } else {
            try {
                checkStorage(mFileInfo);
            } catch (StopRequestException e) {
                DuboxLog.i(TAG, "StopRequestException: " + mTaskId);
                DuboxLog.e(TAG, e.getMessage(), e);
                callBackError(e.mFinalStatus, "isHandledVideoDownload checkStorage " + e.getMessage());
                return true;
            }

            // 如果存在目标文件的老版本，需要先删除，才能保证成功
            if (mFileInfo.destinationPath.exists()) {
                mFileInfo.destinationPath.delete(BaseApplication.getInstance());
            }
            boolean result = FileUtils.moveFile(localPath, mFileInfo.destinationPath.localUrl());
            DuboxLog.d(TAG, "copy video file and delete result:" + result);

            if (result) {
                if (mFileInfo.tempDestinationPath != null && mFileInfo.tempDestinationPath.exists()) {
                    mFileInfo.tempDestinationPath.delete(BaseApplication.getInstance());
                }
                DuboxLog.d(TAG, "copy video file and delete callback success");
                callBackSuccess();
            }

            return result;
        }
    }

    /**
     * cdiff and sdiff
     *
     * @param localPath 本地路径
     * @return true：已经处理过 liulp
     */
    private boolean isHandledNotVideoDownload(String localPath, long lastTime, long localLastTime) {
        // cdiff
        if (lastTime != localLastTime) {
            return false;
        }

        // sdiff
        // 1.传输的mServerMD5 同文件库cachefilelist最新的是否一致
        String cloudMd5 = getCloudFileMd5();
        if (!TextUtils.equals(mServerMD5, cloudMd5)) {
            return false;
        }

        // 2.传输的mServerMD5，同对方已经下载完成文件的md5是否一致
        String downloadMd5 = getDownloadedFileMd5();
        if (!TextUtils.equals(mServerMD5, downloadMd5)) {
            return false;
        }

        try {
            checkStorage(mFileInfo);
        } catch (StopRequestException e) {
            DuboxLog.i(TAG, "StopRequestException: " + mTaskId);
            DuboxLog.e(TAG, e.getMessage(), e);
            feedbackMonitorLog("isHandledNotVideoDownload exception:" + e.getMessage());
            callBackError(e.mFinalStatus, "isHandledNotVideoDownload checkStorage " + e.getMessage());
            return true;
        }

        // 如果存在目标文件的老版本，需要先删除，才能保证copy成功
        if (mFileInfo.destinationPath.exists()) {
            mFileInfo.destinationPath.delete(BaseApplication.getInstance());
        }
        boolean result = FileUtils.copyFile(localPath, mFileInfo.destinationPath.localUrl());
        DuboxLog.d(TAG, "copyFile result:" + result);

        if (result) {// 为copy或者移动的成功
            if (mFileInfo.tempDestinationPath != null && mFileInfo.tempDestinationPath.exists()) {
                mFileInfo.tempDestinationPath.delete(BaseApplication.getInstance());
            }

            DuboxLog.d(TAG, "copyFile ok callback success");
            callBackSuccess();
        }
        return result;
    }

    @Override
    protected void prepareTransmit() {
        super.prepareTransmit();

        mTransferLog.setLocalPath(mFileInfo.destinationPath.localUrl());
        mTransferLog.setRemoteUrl(mServerPath);
    }
}
