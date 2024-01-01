package com.moder.compass.firebasemodel

import com.google.gson.annotations.SerializedName

/**
 * video downloader website 配置
 */
data class VDConfigWebsite(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("icon_url")
    val iconUrl: String = "",
)