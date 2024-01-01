package com.moder.compass.response

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import com.dubox.drive.network.base.Response
/**
 * Created by yeliangliang on 2020/10/30
 */
@SuppressLint("ParcelCreator")
class ListResponse<T>(
    @SerializedName("list")
    val list: List<T>?) : Response()