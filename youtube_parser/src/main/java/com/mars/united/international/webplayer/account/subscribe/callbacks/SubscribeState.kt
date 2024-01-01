package com.mars.united.international.webplayer.account.subscribe.callbacks

/**
 * @Author 陈剑锋
 * @Date 2023/9/15-17:03
 * @Desc 订阅状态
 */
sealed class SubscribeState(
    val channelId: String,
    val isFollowing: Boolean
) {

    /**
     * 关注成功
     * @constructor
     */
    class Success(
        channelId: String,
        isFollowing: Boolean
    ) : SubscribeState(channelId, isFollowing)

    /**
     * 关注失败
     */
    class Failed(channelId: String) : SubscribeState(channelId, false)

    val isSuccess: Boolean get() = this is Success

}