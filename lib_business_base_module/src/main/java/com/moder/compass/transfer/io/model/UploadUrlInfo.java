package com.moder.compass.transfer.io.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by libin on 16/5/28.
 * 上传服务器地址信息
 */
public class UploadUrlInfo {
    @SerializedName("server")
    public String server;

    @SerializedName("host")
    public String host;

    public UploadUrlInfo() {
    }

    public UploadUrlInfo(String server) {
        this(server, null);
    }

    public UploadUrlInfo(String server, @Nullable String host) {
        this.server = server;
        this.host = host;
    }

    @Override
    public String toString() {
        return "UploadUrlInfo{" + "server='" + server + '\'' + ", host='" + host + '\'' + '}';
    }
}
