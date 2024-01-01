package com.moder.compass.remoteconfig

import com.google.gson.annotations.SerializedName

/**
 * 商店审核适配的配置参数实体
 */
data class StoreAuditAdaptationConfig(
    /**
     * 渠道号
     */
    @SerializedName("channelNo")
    val channelNo: String = "",

    /**
     * 是否正在审核中
     */
    @SerializedName("isUnderReview")
    val isUnderReview: Boolean = false,
)


