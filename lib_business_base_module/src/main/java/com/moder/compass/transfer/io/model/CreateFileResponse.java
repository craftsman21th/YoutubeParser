package com.moder.compass.transfer.io.model;

import com.dubox.drive.network.base.Response;
import com.google.gson.annotations.SerializedName;

import android.annotation.SuppressLint;

/**
 * Created by linchangxin on 16/4/1.
 */
@SuppressLint("ParcelCreator")
public class CreateFileResponse extends Response {
    @SerializedName("fs_id")
    public long mFsId;

    @SerializedName("path")
    public String mPath;

    @SerializedName("size")
    public long mSize;

    @SerializedName("ctime")
    public long mCTime;

    @SerializedName("mtime")
    public long mMTime;

    @SerializedName("md5")
    public String mMd5;

    @SerializedName("status")
    public int mStatus;

    @SerializedName("isdir")
    public int mIsDir;

    @SerializedName("name")
    public String mName;

    public String mRawString;
}
