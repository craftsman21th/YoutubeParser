package com.moder.compass.remoteconfig

import androidx.annotation.LongDef

/** 弱引导提示保存 */
const val NOTIFY_SAVE = 1L

/** 自动保存 */
const val AUTO_SAVE = 2L

/** 试看后保存 */
const val PREVIEW_SAVE = 3L

/**
 * 外链页视频播放实验
 * Created by zhouzhimin on 2023/2/7.
 */
@Retention(AnnotationRetention.SOURCE)
@LongDef(NOTIFY_SAVE, AUTO_SAVE, PREVIEW_SAVE)
annotation class ShareLinkVideoPlayStrategy
