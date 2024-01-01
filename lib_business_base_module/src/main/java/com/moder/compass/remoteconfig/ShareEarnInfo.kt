package com.moder.compass.remoteconfig

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/** 赚钱元素外漏到分享开关配置 */
@Keep
data class ShareEarnInfo(
    /** 启用渠道 */
    @SerializedName("enable_channel")
    val enableChannel: List<String> = emptyList(),

    /** 启用注册国 */
    @SerializedName("enable_country")
    val enableCountry: List<String> = emptyList()
)