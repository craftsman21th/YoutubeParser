package com.moder.compass.transfer.io.model;

import java.util.List;

import com.dubox.drive.network.base.Response;
import com.google.gson.annotations.SerializedName;

import android.annotation.SuppressLint;

/**
 * Created by linchangxin on 16/4/1.
 */
@SuppressLint("ParcelCreator")
public class PreCreateFileResponse extends Response {

    public static final int RETURN_TYPE_RAPIDUPLOAD = 2;

    @SerializedName("return_type")
    public int mReturnType;

    @SerializedName("uploadid")
    public String mUploadId;

    @SerializedName("uploadsign")
    public String mUploadSign;

    @SerializedName("block_list")
    public List<Integer> mBlockList;

    public String mRawString;

}
