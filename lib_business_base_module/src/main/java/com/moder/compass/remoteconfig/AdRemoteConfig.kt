package com.moder.compass.remoteconfig

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * @author sunmeng12
 * @since  2023/3/6
 * @since  3.6.0 线下包广告配置
 */
@Parcelize
class AdRemoteConfig(
    /**
     * 开关
     */
    @SerializedName("switch")
    val switch: Boolean = false,
    /**
     * 新客保护次数
     */
    @SerializedName("protect_times")
    val protectTimes: Int = 0,
    /**
     * 从第几张卡片开始显示广告
     */
    @SerializedName("ad_position")
    val start: Int = 0,
    /**
     * 间隔几张卡片显示广告
     */
    @SerializedName("ad_interval")
    val interval: Int = 0,
    /**
     * 每日广告触发上限
     */
    @SerializedName("time_limited")
    val timeLimited: Int = -1
) : Parcelable