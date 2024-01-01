
package com.moder.compass.localfile;

import android.text.TextUtils;

/**
 * Created by 魏铮铮 on 15/2/11.
 */
public final class DownloadPath {

    private DownloadPath() {
    }

    private static String mDefaultDownloadDirName;

    public static String getDefaultDownloadDirName() {
        if (TextUtils.isEmpty(mDefaultDownloadDirName)) {
            throw new IllegalStateException("下载路径未初始化！必须在Application中初始化默认下载路径！");
        }
        return mDefaultDownloadDirName;
    }

    public static void initDefaultDownloadDirName(String path) {
        if (TextUtils.isEmpty(mDefaultDownloadDirName)) {
            mDefaultDownloadDirName = path;
        }
    }
}
