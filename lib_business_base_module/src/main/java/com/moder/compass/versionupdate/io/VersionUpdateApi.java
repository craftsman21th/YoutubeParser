package com.moder.compass.versionupdate.io;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.json.JSONException;

import com.dubox.drive.base.network.BaseApi;
import com.moder.compass.base.networklegacy.NetworkTask;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.moder.compass.versionupdate.io.model.Version;
import com.moder.compass.versionupdate.io.parser.GetApkParser;
import com.moder.compass.versionupdate.io.parser.VersionParser;

/**
 * Created by liji01 on 15-1-22.
 */
public class VersionUpdateApi extends BaseApi {
    private static final String TAG = "VersionUpdateApi";

    /**
     * 传入token的构造方法
     *
     * @param token String 准入token
     * @param uid
     */
    public VersionUpdateApi(final String token, String uid) {
        super(token, uid);
    }

    /**
     * 下载apk包，直接返回流
     *
     * @param url
     * @param filePath 本地存放位置
     * @param filename 下载文件的名称
     * @param md5      下发的apk的md5
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.io.IOException
     * @throws org.json.JSONException
     * @throws com.dubox.drive.kernel.architecture.net.exception.RemoteException
     * @throws java.security.KeyManagementException
     * @throws java.security.UnrecoverableKeyException
     * @throws UnsupportedOperationException
     */
    public String getAppApk(String url, String filePath, String filename, String md5)
            throws NoSuchAlgorithmException, KeyStoreException, IOException, JSONException, RemoteException,
            KeyManagementException, UnrecoverableKeyException, UnsupportedOperationException {

        return new NetworkTask<String>().send(buildGetFileRequest(url), new GetApkParser(filePath, filename, md5));
    }

    /**
     * 下载apk包，直接返回流
     *
     * @param url
     * @param filePath 本地存放位置
     * @param filename 下载文件的名称
     * @param md5      下发的apk的md5
     * @param listener 下载进度监听
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.io.IOException
     * @throws org.json.JSONException
     * @throws com.dubox.drive.kernel.architecture.net.exception.RemoteException
     * @throws java.security.KeyManagementException
     * @throws java.security.UnrecoverableKeyException
     * @throws UnsupportedOperationException
     */
    public String getAppApk(String url, String filePath, String filename,
                            String md5, GetApkParser.DownloadListener listener)
            throws NoSuchAlgorithmException, KeyStoreException, IOException, JSONException, RemoteException,
            KeyManagementException, UnrecoverableKeyException, UnsupportedOperationException {
        if (listener != null) {
            listener.onStart();
        }
        return new NetworkTask<String>().send(buildGetFileRequest(url),
                new GetApkParser(filePath, filename, md5, listener));
    }

    /**
     * 查詢服務器是否需要升级
     *
     * @return
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public Version checkUpgrade() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyStoreException, IOException, JSONException, RemoteException {
        String url = HostURLManager.getApiDefaultHostName() + "version/getlatestversion";
        DuboxLog.d(TAG, "checkUpgrade:" + url);
        return new NetworkTask<Version>().send(buildGetRequest(url), new VersionParser());
    }
}
