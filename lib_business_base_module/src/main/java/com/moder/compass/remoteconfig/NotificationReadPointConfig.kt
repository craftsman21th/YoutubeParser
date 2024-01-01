package com.moder.compass.remoteconfig

import com.google.gson.annotations.SerializedName

/**
 * 通知栏小红点得配置
 */
data class NotificationReadPointConfig(
    /**
     * 是否允许显示小红点
     */
    @SerializedName("switch")
    val switch: Boolean = false,
    /**
     * 距离上次进入前台的时间间隔，单位min，>=该时间后，常驻通知栏icon上
     */
    @SerializedName("time")
    val time: Long = 0L,
    /**
     * 红点显示次数，-1表示没有次数限制
     */
    @SerializedName("times")
    val times: Long = 0L,
)
