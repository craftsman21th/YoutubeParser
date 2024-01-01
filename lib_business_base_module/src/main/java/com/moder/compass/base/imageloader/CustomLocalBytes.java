package com.moder.compass.base.imageloader;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 本地图片加载，支持自定义缓存Key
 *
 * Created by panchao02 on 2019/03/28.
 */

public class CustomLocalBytes {

    private static String TAG = "CustomLocalUri";

    private String mCacheKey;

    private byte[] mByte;

    public CustomLocalBytes(byte[] bytes, String cacheKey) {
        this.mByte = bytes;
        this.mCacheKey = cacheKey;
    }

    public String getCacheKey() {
        DuboxLog.v(TAG, " getCacheKey():" + mCacheKey);
        return mCacheKey;
    }

    public byte[] getBytes() {
        DuboxLog.v(TAG, " getBytes():" + mByte);
        return mByte;
    }

    @Override
    public String toString() {
        return "CustomLocalBytes{"
                + mCacheKey +
                '}';
    }

}
