package com.mars.united.international.webplayer.core.player

import android.util.Log
import com.dubox.drive.kernel.architecture.debug.DuboxLog

class PlayerConstants {
    /**
     * 用于枚举播放状态
     * */
    enum class PlayerState {
        UNKNOWN, UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, VIDEO_CUED
    }

    /**
     * 用于枚举播放分辨率
     * */
    enum class PlaybackQuality {
        UNKNOWN, SMALL, MEDIUM, LARGE, HD720, HD1080, HD1440, HD2160, HD2880, HIGH_RES, TINY, AUTO, DEFAULT
    }
    /**
     * 用于枚举播放错误
     * */
    enum class PlayerError {
        UNKNOWN, INVALID_PARAMETER_IN_REQUEST, HTML_5_PLAYER, VIDEO_NOT_FOUND, VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
    }
    /**
     * 用于枚举播放速度
     * */
    enum class PlaySpeedRate {
        UNKNOWN, RATE_0_25, RATE_0_5, RATE_0_75, RATE_1,RATE_1_25, RATE_1_5,RATE_1_75, RATE_2
    }

    companion object
}


private const val SPEED_RATE_0_25F = 0.25f
private const val SPEED_RATE_0_5F = 0.5f
private const val SPEED_RATE_0_75F = 0.75f
private const val SPEED_RATE_1F = 1f
private const val SPEED_RATE_1_25F = 1.25f
private const val SPEED_RATE_1_5F = 1.5f
private const val SPEED_RATE_1_75F = 1.75f
private const val SPEED_RATE_2F = 2f
/**
 * 用于转换播放速度从枚举转为具体浮点数值
 * */
fun PlayerConstants.PlaySpeedRate.toFloat(): Float {
    return when (this) {
        PlayerConstants.PlaySpeedRate.UNKNOWN -> SPEED_RATE_1F
        PlayerConstants.PlaySpeedRate.RATE_0_25 -> SPEED_RATE_0_25F
        PlayerConstants.PlaySpeedRate.RATE_0_5 -> SPEED_RATE_0_5F
        PlayerConstants.PlaySpeedRate.RATE_0_75 -> SPEED_RATE_0_75F
        PlayerConstants.PlaySpeedRate.RATE_1 -> SPEED_RATE_1F
        PlayerConstants.PlaySpeedRate.RATE_1_25 -> SPEED_RATE_1_25F
        PlayerConstants.PlaySpeedRate.RATE_1_5 -> SPEED_RATE_1_5F
        PlayerConstants.PlaySpeedRate.RATE_1_75 -> SPEED_RATE_1_75F
        PlayerConstants.PlaySpeedRate.RATE_2 -> SPEED_RATE_2F
    }
}
/**
 * 分辨率相关常量
 * */
private const val QUALITY_SMALL = "small"
private const val QUALITY_MEDIUM = "medium"
private const val QUALITY_LARGE = "large"
private const val QUALITY_HD720 = "hd720"
private const val QUALITY_HD1080 = "hd1080"
private const val QUALITY_HD1440 = "hd1440"
private const val QUALITY_HD2160 = "hd2160"
private const val QUALITY_HD2880 = "hd2880"
private const val QUALITY_HIGH_RES = "highres"
private const val QUALITY_TINY = "tiny"
private const val QUALITY_AUTO = "auto"
private const val QUALITY_DEFAULT = "default"
/**
 * 倍数相关常量
 * */
private const val RATE_0_25 = "0.25"
private const val RATE_0_5 = "0.5"
private const val RATE_0_75 = "0.75"
private const val RATE_1 = "1"
private const val RATE_1_0 = "1.0"
private const val RATE_1_25 = "1.25"
private const val RATE_1_5 = "1.5"
private const val RATE_1_75 = "1.75"
private const val RATE_2 = "2"
private const val RATE_2_0 = "2.0"
/**
 * 播放状态相关常量
 * */
private const val STATE_UNSTARTED = "UNSTARTED"
private const val STATE_ENDED = "ENDED"
private const val STATE_PLAYING = "PLAYING"
private const val STATE_PAUSED = "PAUSED"
private const val STATE_BUFFERING = "BUFFERING"
private const val STATE_CUED = "CUED"
/**
 * 错误码相关常量
 * */
private const val ERROR_INVALID_PARAMETER_IN_REQUEST = "2"
private const val ERROR_HTML_5_PLAYER = "5"
private const val ERROR_VIDEO_NOT_FOUND = "100"
private const val ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER1 = "101"
private const val ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER2 = "150"

/**
 * 分辨率字符串与枚举转换
 * */
fun PlayerConstants.Companion.parsePlaybackQuality(quality: String): PlayerConstants.PlaybackQuality {
    return when {
        quality.equals(
            QUALITY_SMALL,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.SMALL
        quality.equals(
            QUALITY_MEDIUM,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.MEDIUM
        quality.equals(
            QUALITY_LARGE,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.LARGE
        quality.equals(
            QUALITY_HD720,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.HD720
        quality.equals(
            QUALITY_HD1080,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.HD1080
        quality.equals(
            QUALITY_HD1440,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.HD1440
        quality.equals(
            QUALITY_HD2160,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.HD2160
        quality.equals(
            QUALITY_HD2880,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.HD2880
        quality.equals(
            QUALITY_HIGH_RES,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.HIGH_RES
        quality.equals(
            QUALITY_TINY,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.TINY
        quality.equals(
            QUALITY_AUTO,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.AUTO
        quality.equals(
            QUALITY_DEFAULT,
            ignoreCase = true
        ) -> PlayerConstants.PlaybackQuality.DEFAULT
        else -> PlayerConstants.PlaybackQuality.UNKNOWN
    }
}

/**
 * 倍数字符串与枚举转换
 * */
fun PlayerConstants.Companion.parsePlaySpeedRate(rate: String): PlayerConstants.PlaySpeedRate {
    return when {
        rate.equals(RATE_0_25, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_0_25
        rate.equals(RATE_0_5, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_0_5
        rate.equals(RATE_0_75, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_0_75
        rate.equals(RATE_1, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_1
        rate.equals(RATE_1_0, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_1
        rate.equals(RATE_1_25, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_1_25
        rate.equals(RATE_1_5, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_1_5
        rate.equals(RATE_1_75, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_1_75
        rate.equals(RATE_2, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_2
        rate.equals(RATE_2_0, ignoreCase = true) -> PlayerConstants.PlaySpeedRate.RATE_2
        else -> PlayerConstants.PlaySpeedRate.UNKNOWN
    }
}

/**
 * 播放状态转换
 * */
 fun PlayerConstants.Companion.parsePlayerState(state: String): PlayerConstants.PlayerState {
    return when {
        state.equals(
            STATE_UNSTARTED,
            ignoreCase = true
        ) -> PlayerConstants.PlayerState.UNSTARTED
        state.equals(STATE_ENDED, ignoreCase = true) -> PlayerConstants.PlayerState.ENDED
        state.equals(STATE_PLAYING, ignoreCase = true) -> PlayerConstants.PlayerState.PLAYING
        state.equals(STATE_PAUSED, ignoreCase = true) -> PlayerConstants.PlayerState.PAUSED
        state.equals(
            STATE_BUFFERING,
            ignoreCase = true
        ) -> PlayerConstants.PlayerState.BUFFERING
        state.equals(STATE_CUED, ignoreCase = true) -> PlayerConstants.PlayerState.VIDEO_CUED
        else -> PlayerConstants.PlayerState.UNKNOWN
    }
}

/**
 * 错误码转换
 * */
fun PlayerConstants.Companion.parsePlayerError(error: String): PlayerConstants.PlayerError {
    return when {
        error.equals(
            ERROR_INVALID_PARAMETER_IN_REQUEST,
            ignoreCase = true
        ) -> PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST
        error.equals(
            ERROR_HTML_5_PLAYER,
            ignoreCase = true
        ) -> PlayerConstants.PlayerError.HTML_5_PLAYER
        error.equals(
            ERROR_VIDEO_NOT_FOUND,
            ignoreCase = true
        ) -> PlayerConstants.PlayerError.VIDEO_NOT_FOUND
        error.equals(
            ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER1,
            ignoreCase = true
        ) -> PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
        error.equals(
            ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER2,
            ignoreCase = true
        ) -> PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
        else -> PlayerConstants.PlayerError.UNKNOWN
    }.also {
        Log.e("Youtube Play Error", "code: ${error}, type: ${it.name}")
    }
}