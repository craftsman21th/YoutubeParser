package com.moder.compass.firebasemodel

import com.google.gson.annotations.SerializedName

/**
 * @author: 曾勇
 * date: 2021-12-06 17:34
 * e-mail: zengyong01@moder.com
 * desc: 首页icon配置对象
 */
data class HomeIconConfig(
    @SerializedName("homeTab")
        val homeTab: HomeTabIconConfig?,
    @SerializedName("fileTab")
        val fileTab: HomeTabIconConfig?,
    @SerializedName("timeLineTab")
        val timeLineTab: HomeTabIconConfig?,
    @SerializedName("downloadTab")
        val downloadTab: HomeTabIconConfig?,
    @SerializedName("videoTab")
        val videoTab: HomeTabIconConfig?,
    @SerializedName("shareTab")
        val shareTab: HomeTabIconConfig?,
    @SerializedName("avatarDecoration")
        val avatarDecoration: String?
)

/**
 * 首页节日底部tab配置
 */
data class HomeTabIconConfig(
        @SerializedName("defaultIcon")
        val defaultIcon: String?,
        @SerializedName("selectIcon")
        val selectIcon: String?,
)