/*
 * GetApkParser.java
 * classes : GetApkParser
 * @author chenyuquan
 * V 1.0.0
 * Create at 2013年12月16日 下午4:59:44
 */
package com.moder.compass.versionupdate.io.parser;

import android.text.TextUtils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.dubox.drive.kernel.util.encode.MD5Util;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * GetApkParser
 *
 * @author chenyuquan <br/>
 * create at 2013年12月16日 下午4:59:44
 */
public class GetApkParser implements IApiResultParseable<String> {
    private static final String TAG = "GetApkParser";

    private final String mDownloadDir;

    private final String mFilename;

    /**
     * 下发的apk文件正确md5
     */
    private final String mApkMd5;
    private final DownloadListener listener;

    /**
     * 构造
     *
     * @param downloadDir 下载路径
     * @param filename    文件名
     * @param md5         正确的md5
     */
    public GetApkParser(final String downloadDir, final String filename, final String md5) {
        this.mDownloadDir = downloadDir;
        this.mFilename = filename;
        this.mApkMd5 = md5;
        this.listener = null;
    }

    /**
     * 构造
     *
     * @param downloadDir 下载路径
     * @param filename    文件名
     * @param md5         正确的md5
     */
    public GetApkParser(final String downloadDir, final String filename, final String md5, DownloadListener listener) {
        this.mDownloadDir = downloadDir;
        this.mFilename = filename;
        this.mApkMd5 = md5;
        this.listener = listener;
    }

    @Override
    public String parse(HttpResponse response) throws JSONException, IOException, RemoteException {
        final String path = download(response);
        DuboxLog.v(TAG, "path:" + path);
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        validateAPK(path);
        return path;
    }

    private String download(HttpResponse response) throws RemoteException {
        byte[] buffer = new byte[1024 * 16];
        BufferedInputStream bs = null;
        FileOutputStream fos = null;
        try {
            final long length = response.getContentLength();
            bs = new BufferedInputStream(response.getInputStream());
            File dirFile = new File(mDownloadDir);
            File apkFile = new File(mDownloadDir + mFilename);

            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            if (apkFile.exists()) {
                apkFile.delete();
            }
            if (!apkFile.exists()) {
                apkFile.createNewFile();
                DuboxLog.v(TAG, "create apk file");
            }
            fos = new FileOutputStream(apkFile);
            int num = bs.read(buffer);
            long count = num != -1 ? num : 0;
            while (num != -1) {
                // fos.write(buffer);
                fos.write(buffer, 0, num);
                num = bs.read(buffer);
                if (num != -1) {
                    count += num;
                }
                if (listener != null) {
                    listener.onProgress((float) count / length);
                }
            }

            // 检查是否下完整了，如果未下完整，返回异常
            if (length > 0L && length != count) {
                DuboxLog.d(TAG, "entity length:" + length + ",download length:" + count);
                throw new RemoteException(0, "未下载完整:" + count + "," + length);
            }

            return apkFile.getAbsolutePath();
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            return null;
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    DuboxLog.e(TAG, "", ex);
                }
            }

            if (null != bs) {
                try {
                    bs.close();
                } catch (IOException ex) {
                    DuboxLog.e(TAG, "", ex);
                }
            }
        }
    }

    /**
     * 校验APK合法性，比对apk签名与百度网盘签名是否一直
     *
     * @param absolutePath apk绝对路径
     */
    private void validateAPK(String absolutePath) {
        if (TextUtils.isEmpty(mApkMd5)) {
            // 下发的md5为空，则视为不要校验md5
            DuboxLog.d(TAG, "不需要校验md5");
            return;
        }
        File apkFile = new File(absolutePath);
        if (!checkMd5(apkFile)) {
            // md5校验不合法
            if (apkFile.isFile()) {
                apkFile.delete();
            }
            throw new IllegalArgumentException("md5校验不合法");
        }
        DuboxLog.d(TAG, "md5校验合法");

    }

    /**
     * 检查下载的apk文件的md5是否合法
     *
     * @param apkFile apk文件
     * @return true 合法；false 不合法
     */
    private boolean checkMd5(File apkFile) {
        String localApkMd5 = MD5Util.getMD5Digest(PathKt.rFile(apkFile.getAbsolutePath()));
        DuboxLog.d(TAG, "下发的md5为： " + mApkMd5);
        DuboxLog.d(TAG, "本地apk计算的md5为： " + localApkMd5);
        if (TextUtils.isEmpty(localApkMd5)) {
            return false;
        }
        return localApkMd5.equalsIgnoreCase(mApkMd5);
    }

    public interface DownloadListener {
        void onStart();

        void onProgress(float progress);
    }
}
