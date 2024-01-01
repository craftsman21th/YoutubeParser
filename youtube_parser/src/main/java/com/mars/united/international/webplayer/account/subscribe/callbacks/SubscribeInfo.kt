package com.mars.united.international.webplayer.account.subscribe.callbacks

import com.mars.united.international.webplayer.account.model.SubscribeInfoItem

/**
 * @Author 陈剑锋
 * @Date 2023/9/15-17:03
 * @Desc 订阅状态
 */
sealed class SubscribeInfo(
    val subscribedList: List<SubscribeInfoItem> = emptyList(),
) {

    /**
     * 获取关注信息成功
     * @constructor
     */
    class Success(subscribedList: List<SubscribeInfoItem>) : SubscribeInfo(subscribedList)

    /**
     * 获取关注信息失败
     */
    class Failed : SubscribeInfo()

    val isSuccess: Boolean get() = this is Success

}