
package com.moder.compass.transfer.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.json.JSONException;

import com.moder.compass.account.Account;
import com.dubox.drive.base.network.BaseApi;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.base.network.HttpRequestHelper;
import com.moder.compass.base.networklegacy.NetworkTask;
import com.dubox.drive.base.network.NetworkUtil;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpParams;
import com.dubox.drive.kernel.architecture.net.HttpRequest;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.util.encode.SHA1Util;
import com.moder.compass.business.kernel.HostURLManagerKt;
import com.moder.compass.transfer.io.model.CreateFileResponse;
import com.moder.compass.transfer.io.model.LocateDownloadResponse;
import com.moder.compass.transfer.io.model.LocateUploadResponse;
import com.moder.compass.transfer.io.model.PreCreateFileResponse;
import com.moder.compass.transfer.io.parser.CreateFileParser;
import com.moder.compass.transfer.io.parser.LocateDownloadParser;
import com.moder.compass.transfer.io.parser.LocateUploadParser;
import com.moder.compass.transfer.io.parser.PreCreateFileParser;
import com.moder.compass.transfer.io.parser.QueryFgidParser;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * Created by liuliangping on 2015/2/6.
 */
public class TransferApi extends BaseApi {
    private static final String TAG = "TransferApi";
    private static final String VIDEO_COMPRESS_TAG = "video_compress_tag";

    /**
     * 传入token的构造方法
     *
     * @param token String 准入token
     * @param uid
     */
    public TransferApi(String token, String uid) {
        super(token, uid);
    }

    /**
     * 获取普通下载接口所需要的服务器列表
     *
     * @param path
     * @param isPreview 是否是预览任务
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public LocateDownloadResponse getNormalLocateDownload(String path, boolean isPreview)
        throws IOException, JSONException, RemoteException {
        return getLocateDownload(path, isPreview, null, null);
    }


    /**
     * 获取使用提速下载接口所需要的服务器列表
     *
     * @param path
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public LocateDownloadResponse getSpeedLocateDownload(String path, String token,
                                                         String timeStamp) throws  IOException, JSONException,
            RemoteException {
        return getLocateDownload(path, false, token, timeStamp);
    }

    /**
     * 获取下载接口所需要的服务器列表
     *
     * @param path
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public LocateDownloadResponse getLocateDownload(String path, boolean isPreview, String speedToken,
                                                    String speedTimeStamp) throws  IOException, JSONException,
            RemoteException {
        final String host;

        if (isPreview) {
            host = HostURLManager.getSimplePCSHostName() + "?method=locatedownload&";
        } else {
            host = HostURLManager.getPCSHostName() + "?method=locatedownload&" +
                    HostURLManagerKt.FILE_PREVIEW_PARAM + "&";
        }
        String url = host + String.format("path=%s&ver=2.0&dtype=0&esl=1&ehps=%s&app_id="
                + AppCommon.getSecondBoxPcsAppId() + "&check_blue=1", Uri.encode(path), HostURLManager.getEhps());
        if (!TextUtils.isEmpty(speedToken) && !TextUtils.isEmpty(speedTimeStamp)) {
            url = url + String.format("&token=%s&timestamp=%s", speedToken, speedTimeStamp);
        }
        DuboxLog.d(TAG, "locateDownload:" + url);
        return new NetworkTask<LocateDownloadResponse>(10000).send(buildGetRequest(url, null),
                new LocateDownloadParser());
    }

    /**
     * 获取dlink的加速试用下载地址
     *
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public LocateDownloadResponse getDlinkSpeedLocateDownload(String path,
                                                              String token,
                                                              String timeStamp) throws  IOException, JSONException,
            RemoteException {
        return getDlinkLocateDownload(path, token, timeStamp);
    }

    /**
     * 获取dlink的普通下载地址
     *
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public LocateDownloadResponse getDlinkNormalLocateDownload(String path) throws  IOException, JSONException,
            RemoteException {
        return getDlinkLocateDownload(path, null, null);
    }

    /**
     * 获取dlink的下载地址
     *
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public LocateDownloadResponse getDlinkLocateDownload(String path,
                                                         String token,
                                                         String timeStamp) throws  IOException, JSONException,
            RemoteException {

        String url = HostURLManager.getSimplePCSHostName() + String
                .format("?method=locatedownload&path=%s&ver=2.0&dtype=0&esl=1&ehps=%s&app_id="
                        + AppCommon.getSecondBoxPcsAppId()
                        + "&check_blue=1", path, HostURLManager.getEhps());
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(timeStamp)) {
            url = url + String.format("&token=%s&timestamp=%s", token, timeStamp);
        }
        DuboxLog.d(TAG, "getDlinkLocateDownload:" + url);
        return new NetworkTask<LocateDownloadResponse>(10000).send(buildGetRequest(url, null),
                new LocateDownloadParser());
    }


    /**
     * 获取p2p下载的文件标识
     *
     * @param duboxPath   云端文件路径
     * @param theSDKVersion
     * @param uk            如果是外链分享需要UK
     * @return
     * @throws JSONException
     * @throws RemoteException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws KeyStoreException
     * @throws IOException
     */
    public String getFgid(String duboxPath, String fsid, String theSDKVersion, long uk) throws
            JSONException, RemoteException,  IOException {
        String url = null;
        if (!TextUtils.isEmpty(fsid)) {
            url = HostURLManager.getP2PHostName() + "/fgid?method=query&fsid=" + fsid
                    + "&sdk_version=" + theSDKVersion;
        } else {
            url = HostURLManager.getP2PHostName() + "/fgid?method=query&path=" + duboxPath
                    + "&sdk_version=" + theSDKVersion;
        }



        if (uk > 0L) {
            url += "&uk=" + uk;
        }

        return new NetworkTask<String>().send(buildGetRequest(url), new QueryFgidParser());
    }

    /**
     * 获取上传接口所需的服务器列表
     */
    public LocateUploadResponse getLocateUpload(@Nullable String sign) throws IOException,
            JSONException, RemoteException {
        String url = HostURLManager.getPCSHostName() + "?method=locateupload";

        if (!TextUtils.isEmpty(sign)) {
            url += "&uploadsign=" + sign;
        }

        url += "&upload_version=2.0&app_id=" + AppCommon.getSecondBoxPcsAppId();

        return new NetworkTask<LocateUploadResponse>(10000).send(buildGetRequest(url, null),
                new LocateUploadParser());
    }

    public PreCreateFileResponse preCreateFile(HttpParams params) throws  IOException,
            JSONException, RemoteException {

        final String url = HostURLManager.getApiDefaultHostName() + "precreate";

        DuboxLog.d(TAG, "upload precreate:" + url);
        HttpRequest[] requests = handleSpecialParams(url, params);

        return new NetworkTask<PreCreateFileResponse>().send(requests, new PreCreateFileParser());
    }

    public CreateFileResponse createFile(HttpParams params) throws  IOException, JSONException, RemoteException {

        final String url = HostURLManager.getApiDefaultHostName() + "create";

        DuboxLog.d(TAG, "upload create:" + url);
        HttpRequest[] requests = handleSpecialParams(url, params);

        return new NetworkTask<CreateFileResponse>().send(requests, new CreateFileParser());
    }

    /**
     * 处理一些特殊参数，如视频压缩上传，需要生成传入videozipsign参数
     *
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    private HttpRequest[] handleSpecialParams(String url, HttpParams params)
            throws UnsupportedEncodingException, JSONException {
        HttpRequest[] requests = buildPostRequest(url, params);
        // 含有参数videozip时，说明是视频压缩上传，需要添加参数videozipsign
        if (requests != null && params.containsName("videozip")) {
            HttpRequestHelper helper = new HttpRequestHelper();
            String sk = NetworkUtil.parseSK(Account.INSTANCE.getUid());
            String md5 = getUrlValue(params.toString(), "content-md5");
            for (HttpRequest request : requests) {
                // 为request添加基础参数，然后就能通过request获取到rand和time等基础参数，
                // 由于原逻辑是要到NetworkTask执行时才添加基础参数，导致这里我们不能获取到rand和time
                // ，所以这里使用先添加基础参数再读取的方式
                HttpRequestHelper.appendParams(request);
                String sign = getVideoZipSign(sk, md5, request);
                request.getParams().add("videozipsign", sign);
                request.setAppendParams(false);
            }
        }
        return requests;
    }

    /**
     * url的参数中根据key获取value
     *
     * @param params
     * @param key
     * @return
     */
    public String getUrlValue(String params, String key) {
        if (TextUtils.isEmpty(key) || !params.contains(key + "=")) {
            return null;
        }
        String[] splits = params.split("&");
        for (String split : splits) {
            String[] values = split.split("=");
            if (values.length < 2) {
                return null;
            }
            if (key.equals(values[0])) {
                return values[1];
            }
        }
        return null;
    }


    /**
     * 生成视频压缩上传需要的参数videozipsign
     *
     * @param sk
     * @param md5
     * @param request
     * @return
     * @throws JSONException
     */
    private String getVideoZipSign(String sk, String md5, HttpRequest request) {
        String requestUrl = request.getUrl();
        DuboxLog.d(VIDEO_COMPRESS_TAG, "requestUrl:" + requestUrl);
        Uri uri = Uri.parse(requestUrl);
        String rand = uri.getQueryParameter(Constants.DUBOX_RAND);
        String time = uri.getQueryParameter(Constants.DUBOX_TIME);
        DuboxLog.d(VIDEO_COMPRESS_TAG, "rand:" + rand + " time:" + time + " sk:" + sk + " md5:" + md5);
        return SHA1Util.sha1(rand + time + sk + md5);
    }
}
