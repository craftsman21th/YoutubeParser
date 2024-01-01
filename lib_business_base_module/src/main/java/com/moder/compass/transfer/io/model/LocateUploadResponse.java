/*
 * LocateUploadResponse.java
 * @author 文超
 * V 1.0.0
 * Create at 2013-6-9 上午11:51:04
 */
package com.moder.compass.transfer.io.model;

import com.dubox.drive.kernel.util.NoProguard;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * LocateUploadResponse
 * 
 * @author 文超 <br/>
 *         create at 2013-6-9 上午11:51:04
 */
public class LocateUploadResponse implements NoProguard {
    private static final String TAG = "LocateUploadResponse";

    @SerializedName("client_ip")
    public String clientIP;

    @SerializedName("servers")
    public List<UploadUrlInfo> servers;

    @SerializedName("expire")
    public int expire;

    public LocateUploadResponse() {
    }

    @Override
    public String toString() {
        return "LocateUploadResponse{clientIP='" + clientIP + '\'' + ", server=" + servers
                + ", expire=" + expire + '}';
    }
}
