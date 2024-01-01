package com.moder.compass.response

import com.google.gson.annotations.SerializedName
import com.dubox.drive.network.base.BaseResponse

/**
 * Created by yeliangliang on 2021/2/1
 */
open class DataResponse<T>(
    @SerializedName("data")
    val data: @kotlinx.android.parcel.RawValue T?,
    @SerializedName("errno")
    private val errorNo: Int = 0,
    @SerializedName("request_id")
    private val requestId: String? = "",
    @SerializedName("errmsg")
    private val errorMsg: String? = ""
) : BaseResponse() {

    override fun getErrorNo() = errorNo

    override fun getRequestId() = requestId ?: ""

    override fun getErrorMsg() = errorMsg ?: ""

    override fun isSuccess() = errorNo == 0

    var yme: String = ""
    override fun setHeaderYme(headerYme: String) {
        yme = headerYme
    }

    override fun getHeaderYme() = yme
}