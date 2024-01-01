package com.moder.compass.base.storage.config;

import android.text.TextUtils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class ConfigDownloadSDK {
    private static final String TAG = "ConfigDownloadSDK";

    /**
     * 普通（原画）文件是否使用SDK下载
     */
    @SerializedName("normal_into_sdk_download_enabled")
    public boolean normalIntoSdkDownloadEnabled = true;

    /**
     * 流畅转码是否使用SDK下载
     */
    @SerializedName("smooth_into_sdk_download_enabled")
    public boolean smoothIntoSdkDownloadEnabled = false;

    /**
     * 下载最大并发数量
     */
    @SerializedName("download_multischeduler_limit")
    public int downloadMultiSchedulerLimit = 2;

    public ConfigDownloadSDK() {
    }

    public ConfigDownloadSDK(String body) {
        if (!TextUtils.isEmpty(body)) {
            init(body);
        }
    }

    private void init(String body) {
        try {
            final ConfigDownloadSDK config = new Gson().fromJson(body, this.getClass());
            if (config == null) {
                return;
            }
            normalIntoSdkDownloadEnabled = config.normalIntoSdkDownloadEnabled;
            smoothIntoSdkDownloadEnabled = config.smoothIntoSdkDownloadEnabled;
            if (config.downloadMultiSchedulerLimit > 0) {
                downloadMultiSchedulerLimit = config.downloadMultiSchedulerLimit;
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
