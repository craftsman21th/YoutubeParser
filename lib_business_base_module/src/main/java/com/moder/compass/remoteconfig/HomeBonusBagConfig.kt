package com.moder.compass.remoteconfig

import com.google.gson.annotations.SerializedName

/**
 * 首页福袋配置
 *
 * Created by zhouzhimin on 2022/7/7.
 */
data class HomeBonusBagConfig(
    /**
     * 是否开启
     */
    @SerializedName("rewardEnable")
    val rewardEnable: Boolean = false,
    /**
     * 可领取次数
     */
    @SerializedName("rewardCount")
    val rewardCount: Long = 0L,
    /**
     * 有效期
     */
    @SerializedName("rewardDays")
    val rewardDays: Long = 0L,
    /**
     * 奖励空间大小
     */
    @SerializedName("rewardSpace")
    val rewardSpace: String = "",
)
