package com.mars.united.international.webplayer.account.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Author 陈剑锋
 * @Date 2023/9/19-11:47
 * @Desc 订阅信息项
 */
@Parcelize
data class SubscribeInfoItem(
    val ownerProfileUrl: String,
    val title: String,
    val avatarUrl: String,
    val channelId: String,
    val followerNumStr: String,
    val isSubscribed: Boolean
) : Parcelable