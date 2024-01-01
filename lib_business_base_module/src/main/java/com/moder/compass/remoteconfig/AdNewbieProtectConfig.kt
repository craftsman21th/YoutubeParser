package com.moder.compass.remoteconfig

import com.moder.compass.base.utils.GlobalConfigKey
import com.dubox.drive.kernel.architecture.config.GlobalConfig
import com.google.gson.annotations.SerializedName

/**
 * 新客保护配置
 * @author changlifei
 */
data class AdNewbieProtectConfig(
    /**
     * 冷启保护次数
     */
    @SerializedName("cold_ad_protect_times")
    val coldAdProtectTimes: Int = -1,

    /**
     * 热启保护次数
     */
    @SerializedName("hot_ad_protect_times")
    val hotAdProtectTimes: Int = -1,

    /**
     * home card Ad保护次数
     */
    @SerializedName("home_card_ad_protect_times")
    val homeCardAdProtectTimes: Int = -1,

    /**
     * about me ad 保护次数
     */
    @SerializedName("user_center_banner_ad_protect_times")
    val userCenterBannerAdProtectTimes: Int = -1,

    /***
     * 视频贴片广告保护次数
     */
    @SerializedName("video_bonding_ad_protect_times")
    val videoBondingAdProtectTimes: Int = -1,

    /***
     * 视频暂停广告保护次数
     */
    @SerializedName("video_pause_ad_protect_times")
    val videoPauseAdProtectTimes: Int = -1,

    /***
     * 挽留广告保护次数
     */
    @SerializedName("exit_app_dialog_ad_protect_times")
    val exitAppDialogAdProtectTimes: Int = -1,

    /***
     * 外链页广告保护次数
     */
    @SerializedName("share_link_native_ad_protect_times")
    val shareLinkNativeAdProtectTimes: Int = -1,
)



/**
 * 是否命中新客保护
 */
fun isHitAdNewbieProtect(protectTimes: Int): Boolean {
    val launchTime = GlobalConfig.getInstance().getInt(GlobalConfigKey.LAUNCH_APP_TIMES, 0)
    return launchTime <= protectTimes
}