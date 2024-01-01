package com.moder.compass.transfer.transmitter.locate;

import static com.moder.compass.business.kernel.HostURLManager.getDataDomain;

import android.net.Uri;
import android.text.TextUtils;

import com.moder.compass.account.Account;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.business.kernel.HostURLManagerKt;
import com.moder.compass.transfer.io.TransferApi;
import com.moder.compass.transfer.io.model.LocateDownloadResponse;
import com.moder.compass.transfer.transmitter.TransferLogUtil;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.dubox.drive.kernel.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * locateDownload CDN 帮助类
 *
 * @author 孙奇 <br/>
 *         create at 2013-7-19 下午05:30:52
 */
public class LocateDownload {
    private static final String TAG = "LocateDownloadHelper";

    /**
     * 是否为预览任务
     */
    private final boolean mIsPreview;
    private final String mOriginPath;
    /**
     * 是否过期
     *
     * @author 孙奇 V 1.0.0 Create at 2013-7-19 下午06:09:39
     */
    private boolean isExpireTime = true;
    /**
     * 服务器列表
     */
    private List<LocateDownloadUrls> mUrlList;
    /**
     * 服务器HOST
     */
    private String mHost;
    private final String mBduss;
    private final String mUid;
    private boolean mLocateDownloadHasError = false;
    private LocateDownloadResponse mResponse = null;

    private long mDownloadThreshold = -1L;// 默认值400kb/s

    public LocateDownload(String originPath, boolean isPreview, String bduss, String uid) {
        if (TextUtils.isEmpty(originPath)) {
            throw new IllegalArgumentException("originPath illegal");
        }
        mOriginPath = originPath;
        mIsPreview = isPreview;
        mBduss = bduss;
        mUid = uid;
    }

    public void initPcsServerList() {
        mUrlList = new ArrayList<>();
        mUrlList.add(new LocateDownloadUrls(formatDefaultUrl(getDataDomain(), mOriginPath),
                mIsPreview));
    }

    public void initDlinkServerList() {
        mUrlList = new ArrayList<>();
        mUrlList.add(new LocateDownloadUrls(mOriginPath, mIsPreview));
    }

    /**
     * 获取PCS下载接口的服务器列表
     */
    private void addPcsLocateDownloadAddress(String path) {
        // 检查当前服务器列表的有效期
        if (!isExpireTime) {
            return;
        }
        try {
            mResponse = new TransferApi(mBduss, mUid).getNormalLocateDownload(path, mIsPreview);
            processLocateDownloadResponse(mResponse);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    private void addPcsProbationaryDLAddress(String path, String token, String timestamp) {
        // 检查当前服务器列表的有效期
        if (!isExpireTime) {
            return;
        }
        try {
            mResponse = new TransferApi(mBduss, mUid)
                    .getSpeedLocateDownload(path, token, timestamp);
            processLocateDownloadResponse(mResponse);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 处理locatedownload的response
     *
     * @param response LocateDownloadResponse
     */
    private void processLocateDownloadResponse(LocateDownloadResponse response) {
        mDownloadThreshold = response.downloadThreshold;
        TransferLogUtil.saveClientIp(response.clientIP);

        List<LocateDownloadUrls> urls = response.urls;
        processUserType(response.type);
        mUrlList.clear();
        if (urls != null && !urls.isEmpty()) {
            mUrlList.addAll(urls);
        } else if (response.errorCode != 0) {
            mLocateDownloadHasError = true;
        }
        mUrlList.add(new LocateDownloadUrls(formatDefaultUrl(getDataDomain(), mOriginPath),
                mIsPreview));

        DuboxLog.d(TAG, "addPcsLocateDownloadAddress mUrlList:mUrlList.size()" + mUrlList.size());
        DuboxLog.d(TAG, "download urls : " + Arrays.toString(mUrlList.toArray()));
        // 对于新的服务器列表，需要将请求的映射清空
        isExpireTime = false;
    }

    /**
     * 获取Dlink下载接口的服务器列表
     * <p>
     * dlink 外链激发端使用Locatedownload调用，path为外链URL中/file/后面的所有querystring、参数。
     * <p>
     * dlinkFileString : dlink从"/file/"到"?"之间的字符
     * <p>
     * dlinkQueryString :dlink中从dlinkFile之后"?"之后的所有字符
     */
    private void addDlinkLocateDownloadAddress(String dlink) {
        String path = generateDlinkPath(dlink);
        if (TextUtils.isEmpty(path)) {
            return;
        }

        try {
            mResponse = new TransferApi(mBduss, mUid).getDlinkNormalLocateDownload(path);
            preocessDlinkLocateDownloadResponse(mResponse);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 获取Dlink加速下载接口的服务器列表
     */
    private void addDlinkProbationaryLocateDownloadAddress(String dlink, String token, String timestamp) {
        String path = generateDlinkPath(dlink);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            mResponse = new TransferApi(mBduss, mUid)
                    .getDlinkSpeedLocateDownload(path, token, timestamp);

            preocessDlinkLocateDownloadResponse(mResponse);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 处理Dlink locatedownload的response
     *
     * @param response LocateDownloadResponse
     */
    private void preocessDlinkLocateDownloadResponse(LocateDownloadResponse response) {
        if (response.errorCode == 0) {
            mDownloadThreshold = response.downloadThreshold;
        } else {
            mLocateDownloadHasError = true;
        }
        TransferLogUtil.saveClientIp(response.clientIP);
        processUserType(response.type);
        List<LocateDownloadUrls> urls = response.urls;
        mUrlList.clear();
        if (urls != null && urls.size() != 0) {
            mUrlList.addAll(urls);
        }

        mUrlList.add(new LocateDownloadUrls(mOriginPath, mIsPreview));
        DuboxLog.d(TAG, "parseLocateDownloadResponse mUrlList:mUrlList.size()" + mUrlList.size());

        isExpireTime = false;
    }

    private String generateDlinkPath(String dlink) {
        // 检查当前服务器列表的有效期
        String path = null;
        if (!isExpireTime) {
            return path;
        }

        if (TextUtils.isEmpty(dlink)) {
            return path;
        }

        String start = "/file/";
        int startIndex = dlink.indexOf(start);
        if (startIndex == -1) {
            return path;
        }

        int endIndex = dlink.indexOf("?", startIndex);
        if (endIndex == -1) {
            return path;
        }

        if (endIndex - start.length() <= startIndex) {
            return path;
        }

        String dlinkFileString = dlink.substring(startIndex + start.length(), endIndex);
        String dlinkQueryString = dlink.substring(endIndex + 1);
        DuboxLog.d(TAG, "dlink:" + dlink + " ,dlinkFileString:" + dlinkFileString + " ,dlinkQueryString:"
                + dlinkQueryString);

        path = TextUtils.isEmpty(dlinkQueryString) ? dlinkFileString : dlinkFileString + "&" + dlinkQueryString;
        return path;
    }

    /**
     * 获取URL list
     *
     * @return 下载地址列表
     */
    public List<LocateDownloadUrls> getPcsUrlList() {
        addPcsLocateDownloadAddress(mOriginPath);
        if (CollectionUtils.isEmpty(mUrlList)) {
            initPcsServerList();
        }
        return mUrlList;
    }

    /**
     * 获取URL list
     *
     * @return 下载地址列表
     */
    public List<LocateDownloadUrls> getPcsProbationaryUrlList(String token, String timeStamp) {
        addPcsProbationaryDLAddress(mOriginPath, token, timeStamp);
        if (CollectionUtils.isEmpty(mUrlList)) {
            initPcsServerList();
        }
        return mUrlList;
    }

    public List<LocateDownloadUrls> getDlinkUrlList() {
        addDlinkLocateDownloadAddress(mOriginPath);

        if (CollectionUtils.isEmpty(mUrlList)) {
            initDlinkServerList();
        }
        return mUrlList;
    }

    public List<LocateDownloadUrls> getDlinkSpeedUrlList(String token, String timeStamp) {
        addDlinkProbationaryLocateDownloadAddress(mOriginPath, token, timeStamp);

        if (CollectionUtils.isEmpty(mUrlList)) {
            initDlinkServerList();
        }
        return mUrlList;
    }

    private String formatDefaultUrl(String server, String path) {
        if (TextUtils.isEmpty(server) || TextUtils.isEmpty(path)) {
            return null;
        }
        ConfigSystemLimit configSystemLimit = ConfigSystemLimit.getInstance();
        String scheme =
                configSystemLimit.defaultLocateDownloadHttpsEnable
                        ? HostURLManagerKt.PRO_STR_HTTPS : HostURLManagerKt.PRO_STR_HTTP;
        return String.format(HostURLManager.INSTANCE.getDOWNLOAD_URL(), scheme, server, "download", Uri.encode(path));
    }

    public String getHost() {
        return mHost;
    }

    /**
     * 设置过期
     */
    public void setTimeExpire() {
        isExpireTime = true;
    }

    /**
     * 获取限速的阈值
     *
     * @return 限速阈值
     */
    public long getDownloadLimitThreshold() {
        return mDownloadThreshold;
    }

    /**
     * 处理locatedownload响应报文中的type字段，相应设置用户是否为破解用户
     *
     * @param type type值
     */
    private void processUserType(String type) {
        Account.INSTANCE.setCrackUser(!TextUtils.isEmpty(type) && type.equals(Account.CRACK_USER));
    }

    public boolean isLocateDownloadHasError() {
        return mLocateDownloadHasError;
    }

    public LocateDownloadResponse getResponse() {
        return mResponse;
    }
}
