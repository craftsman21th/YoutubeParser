package com.moder.compass.ui.guide

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import com.moder.compass.firebase.DuboxRemoteConfig
import com.dubox.drive.kernel.architecture.config.PersonalConfig
import com.moder.compass.util.TELEGRAM_GUIDE_JOIN_LINK

private const val KEY_TELEGRAM_GROUP_GUIDE_SHOWED_TIME = "key_telegram_group_guide_showed_time"
private const val KEY_TELEGRAM_GROUP_GUIDE_FIRST_SHOW_TIME = "key_telegram_group_guide_first_show_time"
private const val KEY_HAS_ENTERED_ENTER_VIDEO_DOWNLOADER = "key_has_entered_enter_video_downloader"
private const val MILLS_OF_DAY: Long = 24 * 60 * 60 * 1000L
private const val TIME_STAMP_OF_ZERO: Long = 0L
private const val DAY_COUNT_ZERO: Int = 0
private const val DAY_COUNT_ONE: Int = 1
private const val DAY_COUNT_TWO: Int = 2

/**
 * Telegram群组引导
 *
 * created by zhouzhimin on 2022/6/16.
 */
class TelegramGroupJoinGuideHelper(val context: Context) {
    private val config: PersonalConfig = PersonalConfig.getInstance()
    private val showedOnce: Boolean
        get() = config.getInt(KEY_TELEGRAM_GROUP_GUIDE_SHOWED_TIME, DAY_COUNT_ZERO) >= DAY_COUNT_ONE
    private val showedTwice: Boolean
        get() = config.getInt(KEY_TELEGRAM_GROUP_GUIDE_SHOWED_TIME, DAY_COUNT_ZERO) >= DAY_COUNT_TWO
    private val groupLink: String get() = DuboxRemoteConfig.getString(TELEGRAM_GUIDE_JOIN_LINK)
    private val enabled: Boolean get() = !TextUtils.isEmpty(groupLink)
    private val over24hAfterFirstShow: Boolean
        get() = System.currentTimeMillis() -
                config.getLong(KEY_TELEGRAM_GROUP_GUIDE_FIRST_SHOW_TIME, TIME_STAMP_OF_ZERO) > MILLS_OF_DAY
    private val shouldShow: Boolean
        get() = enabled && !showedTwice && (!showedOnce || showedOnce && over24hAfterFirstShow)

    private fun recordEnterVideoDownloader() {
        config.putBoolean(KEY_HAS_ENTERED_ENTER_VIDEO_DOWNLOADER, true)
    }

    private fun isFirstEnterVideoDownloader(): Boolean {
        return !config.getBoolean(KEY_HAS_ENTERED_ENTER_VIDEO_DOWNLOADER, false)
    }

    private fun show(manager: FragmentManager) {
        if (!showedOnce) {
            config.putLong(KEY_TELEGRAM_GROUP_GUIDE_FIRST_SHOW_TIME, System.currentTimeMillis())
            config.putInt(KEY_TELEGRAM_GROUP_GUIDE_SHOWED_TIME, DAY_COUNT_ONE)
        } else {
            config.putInt(KEY_TELEGRAM_GROUP_GUIDE_SHOWED_TIME, DAY_COUNT_TWO)
        }
        TelegramGroupJoinGuideFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_TELEGRAM_GROUP_LINK, groupLink)
            }
            showNow(manager, "")
        }
    }

    /**
     * 判断是否满足视频下载器展示的条件，是则展示
     */
    fun tryShowFromVideoDownloader(manager: FragmentManager) {
        if (shouldShow) {
            if (!isFirstEnterVideoDownloader()) {
                show(manager)
            } else {
                recordEnterVideoDownloader()
            }
        }
    }

    /**
     * 判断是否满足资源圈展示的条件，是则展示
     */
    fun tryShowFromResource(manager: FragmentManager) {
        if (shouldShow) {
            show(manager)
        }
    }
}