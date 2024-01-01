package com.moder.compass.base.storage.config;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import android.text.TextUtils;

public class ConfigPreLoadVideo {
    private static final String TAG = "ConfigPreLoadVideo";

    /**
     * 是否允许预加载
     */
    @SerializedName("enable_preload_video")
    public boolean enablePreLoadVideo = true;

    /**
     * 是否允许边下边播
     */
    @SerializedName("enable_cache_video_when_play")
    public boolean enableCacheVideoWhenPlay = true;

    /**
     * 最大缓存大小
     */
    @SerializedName("max_cache_size")
    public long maxCacheSize = 536870912;

    /**
     * 每次加载的大小
     */
    @SerializedName("per_load_size")
    public  int perLoadSize = 30;

    public ConfigPreLoadVideo() {
    }

    public ConfigPreLoadVideo(String body) {
        if (!TextUtils.isEmpty(body)) {
            init(body);
        }
    }

    private void init(String body) {
        try {
            final ConfigPreLoadVideo config = new Gson().fromJson(body, this.getClass());
            if (config == null) {
                return;
            }
            enablePreLoadVideo = config.enablePreLoadVideo;
            enableCacheVideoWhenPlay = config.enableCacheVideoWhenPlay;
            if (config.maxCacheSize > 0) {
                maxCacheSize = config.maxCacheSize;
            }
            if (config.perLoadSize > 0) {
                perLoadSize = config.perLoadSize;
            }
        } catch (JsonSyntaxException e) {
            DuboxLog.d(TAG, "init.JsonSyntaxException.e:" + e.getMessage());
        } catch (JsonIOException e) {
            DuboxLog.d(TAG, "init.IOException.e:" + e.getMessage());
        } catch (NullPointerException e) {
            DuboxLog.d(TAG, "init.NullPointerException.e:" + e.getMessage());
        } catch (JsonParseException e) {
            DuboxLog.d(TAG, "init.JsonParseException.e:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            DuboxLog.d(TAG, "init.IllegalArgumentException.e:" + e.getMessage());
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "配置项初始化错误", ignore);
            if (DuboxLog.isDebug()) {
                throw ignore;
            }
        }
    }
}
