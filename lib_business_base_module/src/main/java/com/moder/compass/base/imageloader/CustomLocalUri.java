package com.moder.compass.base.imageloader;

import android.net.Uri;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 本地图片加载，支持自定义缓存Key
 *
 * Created by lijunnian on 2018/9/14.
 */

public class CustomLocalUri {

    private static String TAG = "CustomLocalUri";

    private String mCacheKey;

    private Uri mUri;

    public CustomLocalUri(Uri uri, String cacheKey) {
        this.mUri = uri;
        this.mCacheKey = cacheKey;
    }

    public String getCacheKey() {
        DuboxLog.v(TAG, " getCacheKey():" + mCacheKey);
        return mCacheKey;
    }

    public Uri getUri() {
        DuboxLog.v(TAG, " getUri():" + mUri);
        return mUri;
    }

}
