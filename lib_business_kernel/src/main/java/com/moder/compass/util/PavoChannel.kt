package com.moder.compass.util

import com.dubox.drive.kernel.architecture.AppCommon

/**
 * @author : guoliang08
 * Create time : 2023/7/28 14:08
 * Description : 渠道相关
 */
const val GOOGLE_PLAY_CHANNEL = "google_play"
const val PAVO_WEBPAGE_CHANNEL = "pavo_webpage"

fun isGoogleChannel(): Boolean {
    return GOOGLE_PLAY_CHANNEL == AppCommon.CHANNEL_NUM
}