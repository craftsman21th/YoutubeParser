package com.moder.compass.remoteconfig

import com.google.gson.annotations.SerializedName

/**
 * @author sunmeng12
 * @since moder 2022/10/13
 * 首页广告盒子配置信息
 */
data class AdBoxConfig(
    /**
     * 图标 Icon
     */
    @SerializedName("icon")
    val icon: String = "",
    /**
     * 跳转地址
     */
    @SerializedName("jump_link")
    val jumpLink: String = "",
)