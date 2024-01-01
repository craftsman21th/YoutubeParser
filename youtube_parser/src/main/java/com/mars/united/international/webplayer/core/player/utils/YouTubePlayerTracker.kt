package com.mars.united.international.webplayer.core.player.utils

import com.mars.united.international.webplayer.core.player.PlayerConstants
import com.mars.united.international.webplayer.core.player.YouTubePlayer
import com.mars.united.international.webplayer.core.player.listeners.AbstractYouTubePlayerListener

/**
 * Utility class responsible for tracking the state of YouTubePlayer.
 * This is a YouTubePlayerListener, therefore to work it has to be added as listener to a YouTubePlayer.
 */
open class YouTubePlayerTracker : AbstractYouTubePlayerListener() {
    /**
     * @return the player state. A value from [PlayerConstants.PlayerState]
     */
    var state: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
        private set
    var currentSecond: Float = 0f
        private set
    var videoDuration: Float = 0f
        private set
    var videoId: String? = null
        private set
    var isReady: Boolean = false
        private set

    override fun onReady(youTubePlayer: YouTubePlayer) {
        super.onReady(youTubePlayer)
        isReady = true
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        this.state = state
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        currentSecond = second
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        videoDuration = duration
    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        this.videoId = videoId
    }

    override fun onVideoQualityInfoReady(
        youTubePlayer: YouTubePlayer,
        videoId: String?,
        curQuality: PlayerConstants.PlaybackQuality?,
        qualityInfo: List<PlayerConstants.PlaybackQuality>?,
        curSpeed: PlayerConstants.PlaySpeedRate?,
        speedList: List<PlayerConstants.PlaySpeedRate>?
    ) {
        //接受当前分辨率信息
    }
}
