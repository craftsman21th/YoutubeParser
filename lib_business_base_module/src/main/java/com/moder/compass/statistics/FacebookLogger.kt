/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */
package com.moder.compass.statistics

import com.appsflyer.AppsFlyerLib
import com.moder.compass.BaseApplication
import com.moder.compass.base.utils.GlobalConfigKey
import com.moder.compass.base.utils.PersonalConfigKey
import com.dubox.drive.kernel.architecture.config.GlobalConfig
import com.dubox.drive.kernel.architecture.config.PersonalConfig
import com.dubox.drive.kernel.util.TIME_UNIT_1000

/**
 * @author: 曾勇
 * date: 2021-06-07 18:15
 * e-mail: zengyong01@moder.com
 * desc:
 */

private const val USE_APP_TIMES = 3
private const val TIME_MILLIS_OF_WEEK = 1000L * 60 * 60 * 24 * 7
private const val TEN_TIME_MILLIS = 1000L * 60 * 10
private const val VIEW_PHOTO_TOTAL_PICS = 30

/**
 * 打日志
 */
fun logEvent(key: String, map: Map<String, Any>?) {
    AppsFlyerLib.getInstance().logEvent(BaseApplication.getInstance(), key, map)
}

/**
 * 会员购买事件
 */
fun uploadPurchasedMembersEvent() {
    logEvent(APPS_FLYER_BUY_VIP_SUCCESS, null)
}

/**
 * 一周内使用moder app≥3次（前端活跃）
 * 埋点定义：
 * 1.上线时间点开始计算，每7天×24小时，前端活跃≥3次上报一次（超过3次，在该时间周期内不重复上报）
 * 2.每个自然周清空1次数据
 * 3.如果一个用户多次触发该条件，可重复上报
 */
fun uploadUseEvent() {
    val useTimes = GlobalConfig.getInstance().getInt(GlobalConfigKey.USE_APP_TIMES)
    if (useTimes < USE_APP_TIMES) {
        GlobalConfig.getInstance().putInt(GlobalConfigKey.USE_APP_TIMES, useTimes + 1)
        return
    }
    val lastUploadTime = GlobalConfig.getInstance().getLong(GlobalConfigKey.LAST_UPLOAD_USE_APP_TIME)
    if (System.currentTimeMillis() - lastUploadTime < TIME_MILLIS_OF_WEEK) {
        // 距离上次上报时间小于7 x 24
        return
    }
    GlobalConfig.getInstance().putInt(GlobalConfigKey.USE_APP_TIMES, 0)
    GlobalConfig.getInstance().putLong(GlobalConfigKey.LAST_UPLOAD_USE_APP_TIME, System.currentTimeMillis())
    logEvent(KEY_USE_APP_MORE_3_TIMES, null)
}

/**
 * 观看视频超过10分钟
 * @param stayTimeStamp 本次页面停留时间, 单位：s
 * 埋点定义：
 * 1.上线时间点开始计算，每7天×24小时，观看视频超过10分钟上报一次（超过10分钟，在该时间周期内不重复上报）
 * 2.每个自然周清空1次数据
 * 3.如果一个用户多次触发该条件，可重复上报
 */
fun uploadVideoPageStayTimeEvent(stayTimeStamp: Long) {
    val lastTotalTime = GlobalConfig.getInstance()
            .getLong(GlobalConfigKey.WATCH_VIDEO_TOTAL_TIME)
    val totalTime = lastTotalTime + stayTimeStamp * TIME_UNIT_1000
    if (totalTime < TEN_TIME_MILLIS) {
        GlobalConfig.getInstance().putLong(GlobalConfigKey.WATCH_VIDEO_TOTAL_TIME, totalTime)
        return
    }
    val lastUploadTime = GlobalConfig.getInstance()
            .getLong(GlobalConfigKey.LAST_UPLOAD_WATCH_VIDEO_TIME)
    if (System.currentTimeMillis() - lastUploadTime < TIME_MILLIS_OF_WEEK) {
        // 距离上次上报时间小于7 x 24
        return
    }
    GlobalConfig.getInstance().putLong(GlobalConfigKey.LAST_UPLOAD_WATCH_VIDEO_TIME, System.currentTimeMillis())
    GlobalConfig.getInstance().putLong(GlobalConfigKey.WATCH_VIDEO_TOTAL_TIME, 0)
    logEvent(KEY_WATCH_VIDEO_MORE_10_MINUTES, null)
}

/**
 * 查看照片超过30张
 * 埋点定义：
 * 1.上线时间点开始计算，每7天×24小时，查看照片超过30张上报一次（超过30张，在该时间周期内不重复上报）
 * 2.每个自然周清空1次数据
 * 3.如果一个用户多次触发该条件，可重复上报
 */
fun uploadViewPhotoEvent() {
    val lastViewTotalPics = GlobalConfig.getInstance()
            .getInt(GlobalConfigKey.VIEW_PHOTO_TOTAL_PICS)
    if (lastViewTotalPics < VIEW_PHOTO_TOTAL_PICS) {
        GlobalConfig.getInstance()
                .putInt(GlobalConfigKey.VIEW_PHOTO_TOTAL_PICS, lastViewTotalPics + 1)
        return
    }
    val lastUploadTime = GlobalConfig.getInstance()
            .getLong(GlobalConfigKey.LAST_UPLOAD_VIEW_PHOTO_TIME)
    if (System.currentTimeMillis() - lastUploadTime < TIME_MILLIS_OF_WEEK) {
        return
    }
    GlobalConfig.getInstance()
            .putInt(GlobalConfigKey.VIEW_PHOTO_TOTAL_PICS, 0)
    GlobalConfig.getInstance()
            .putLong(GlobalConfigKey.LAST_UPLOAD_VIEW_PHOTO_TIME, System.currentTimeMillis())
    logEvent(KEY_VIEW_MORE_30_PHOTOS, null)
}

/**
 * 空间存储Quota值超过10GB
 * 埋点定义：
 * 如果用户Quota值＞10GB，上报一次就行了，如果用户删除了，再存东西超过10GB，咱们再报一次就可以了
 */
fun uploadQuotaEvent() {
    val hasUpload = PersonalConfig.getInstance()
            .getBoolean(PersonalConfigKey.HAS_UPLOAD_QUOTA_MORE_10G)
    if (!hasUpload) {
        PersonalConfig.getInstance()
                .putBoolean(PersonalConfigKey.HAS_UPLOAD_QUOTA_MORE_10G, true)
        logEvent(KEY_SPACE_QUOTA_MORE_10G, null)
    }
}