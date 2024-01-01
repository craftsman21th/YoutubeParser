package com.moder.compass.remoteconfig

import com.google.gson.annotations.SerializedName

/**
 * radar 云控
 */
data class RadarConfig(
    /**
     * 开关
     */
    @SerializedName("radarEnable")
    val enable: Boolean = false,
    /**
     * 最大翻牌次数
     */
    @SerializedName("radarFlipMaxCount")
    val maxCount: Long = 0,
)
