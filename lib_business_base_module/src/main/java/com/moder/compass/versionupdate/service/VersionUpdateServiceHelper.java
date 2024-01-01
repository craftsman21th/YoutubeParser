package com.moder.compass.versionupdate.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.versionupdate.io.VersionUpdateApi;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.moder.compass.versionupdate.io.parser.GetApkParser;

import android.text.TextUtils;

/**
 * Created by liji01 on 15-1-22.
 */
public class VersionUpdateServiceHelper {
    private static final String TAG = "VersionUpdateServiceHelper";

    /**
     * 获取程序apk包的流
     *
     * @param url
     * @param filePath 本地文件存放位置
     * @param filename
     * @param md5      下发的md5
     * @param bduss    账号标识
     * @param uid
     * @return
     * @throws java.io.IOException
     * @throws RemoteException
     */
    public String getAppApk(String url,
                            String filePath,
                            final String filename,
                            String md5,
                            String bduss,
                            String uid,
                            GetApkParser.DownloadListener listener)
        throws IOException, RemoteException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String path = null;
        try {
            path = new VersionUpdateApi(bduss, uid).getAppApk(url, filePath, filename, md5, listener);
        } catch (KeyManagementException
            | UnrecoverableKeyException
            | NoSuchAlgorithmException
            | KeyStoreException
            | UnsupportedOperationException
            | JSONException e) {
            DuboxLog.e(TAG, "", e);
            return null;
        }
        return path;
    }
}
