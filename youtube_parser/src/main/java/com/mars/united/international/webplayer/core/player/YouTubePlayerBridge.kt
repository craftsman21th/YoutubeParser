package com.mars.united.international.webplayer.core.player

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.webkit.JavascriptInterface
import androidx.annotation.RestrictTo
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mars.united.international.webplayer.core.player.listeners.YouTubePlayerListener


/**
 * Bridge used for Javascript-Java communication.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class YouTubePlayerBridge(private val youTubePlayerOwner: YouTubePlayerBridgeCallbacks) {

    companion object {
        // these constants correspond to the values in the Javascript player

    }

    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    interface YouTubePlayerBridgeCallbacks {
        val listeners: Collection<YouTubePlayerListener>
        fun getInstance(): YouTubePlayer
        fun onYouTubeIFrameAPIReady()
    }

    /**
     * 获取屏幕宽度
     * @return Int
     */
    @JavascriptInterface
    fun getScreenWidth(): Int = DeviceDisplayUtils.getScreenWidth()

    /**
     * 获取屏幕高度
     * @return Int
     */
    @JavascriptInterface
    fun getScreenHeight(): Int = DeviceDisplayUtils.getScreenHeight()

    @JavascriptInterface
    fun sendYouTubeIFrameAPIReady() =
        mainThreadHandler.post { youTubePlayerOwner.onYouTubeIFrameAPIReady() }

    @JavascriptInterface
    fun sendReady() = mainThreadHandler.post {
        youTubePlayerOwner.listeners.forEach { it.onReady(youTubePlayerOwner.getInstance()) }
    }

    @JavascriptInterface
    fun sendStateChange(state: String) {
        val playerState = PlayerConstants.parsePlayerState(state)

        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onStateChange(
                    youTubePlayerOwner.getInstance(),
                    playerState
                )
            }
        }
    }

    @JavascriptInterface
    fun sendPlaybackQualityChange(quality: String) {
        DuboxLog.d("qqqq", "cur play quality:${quality}")
        val playbackQuality = PlayerConstants.parsePlaybackQuality(quality)
        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onPlaybackQualityChange(
                    youTubePlayerOwner.getInstance(),
                    playbackQuality
                )
            }
        }
    }

    @JavascriptInterface
    fun sendPlaybackRateChange(rate: String) {
        DuboxLog.d("qqqq", "cur play speed rate${rate}")
        val playbackRate = PlayerConstants.parsePlaySpeedRate(rate)
        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onPlaybackRateChange(
                    youTubePlayerOwner.getInstance(),
                    playbackRate
                )
            }
        }
    }

    @JavascriptInterface
    fun sendError(error: String) {
        val playerError = PlayerConstants.parsePlayerError(error)

        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onError(
                    youTubePlayerOwner.getInstance(),
                    playerError
                )
            }
        }
    }

    @JavascriptInterface
    fun sendApiChange() = mainThreadHandler.post {
        youTubePlayerOwner.listeners.forEach { it.onApiChange(youTubePlayerOwner.getInstance()) }
    }

    @JavascriptInterface
    fun sendVideoCurrentTime(seconds: String) {
        val currentTimeSeconds = try {
            seconds.toFloat()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return
        }

        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onCurrentSecond(
                    youTubePlayerOwner.getInstance(),
                    currentTimeSeconds
                )
            }
        }
    }

    @JavascriptInterface
    fun sendVideoDuration(seconds: String) {
        val videoDuration = try {
            val finalSeconds = if (TextUtils.isEmpty(seconds)) "0" else seconds
            finalSeconds.toFloat()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return
        }

        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onVideoDuration(
                    youTubePlayerOwner.getInstance(),
                    videoDuration
                )
            }
        }
    }

    @JavascriptInterface
    fun sendVideoLoadedFraction(fraction: String) {
        val loadedFraction = try {
            fraction.toFloat()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return
        }

        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onVideoLoadedFraction(
                    youTubePlayerOwner.getInstance(),
                    loadedFraction
                )
            }
        }
    }

    /**
     * 用于Js调用，透传当前视频id
     * @param videoId 视频Id
     *
     * */
    @JavascriptInterface
    fun sendVideoId(videoId: String) = mainThreadHandler.post {
        youTubePlayerOwner.listeners.forEach {
            it.onVideoId(
                youTubePlayerOwner.getInstance(),
                videoId
            )
        }
    }


    /**
     * 用于Js调用，透传分辨率信息
     * @param videoId 视频Id
     * @param curQualityStr 当前视频分辨率
     * @param qualityJson 支持分辨率列表
     *
     * */
    @JavascriptInterface
    fun sendVideoInfo(
        videoId: String,
        curQualityStr: String,
        qualityJson: String,
        curPlaySpeed: Int,
        speedListJson: String
    ) {
        val curQuality = PlayerConstants.parsePlaybackQuality(curQualityStr)
        val qualityList = mutableListOf<PlayerConstants.PlaybackQuality>()
        val curSpeed = PlayerConstants.parsePlaySpeedRate(curPlaySpeed.toString())
        val speedList = mutableListOf<PlayerConstants.PlaySpeedRate>()
        (kotlin.runCatching {
            Gson().fromJson<List<String>>(qualityJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull() ?: mutableListOf<String>()).forEach {
            qualityList.add(PlayerConstants.parsePlaybackQuality(it))
        }
        (kotlin.runCatching {
            Gson().fromJson<List<String>>(speedListJson, object : TypeToken<List<String>>() {}.type)
        }.getOrNull() ?: mutableListOf<String>()).forEach {
            speedList.add(PlayerConstants.parsePlaySpeedRate(it))
        }
        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onVideoQualityInfoReady(
                    youTubePlayerOwner.getInstance(),
                    videoId,
                    curQuality,
                    qualityList,
                    curSpeed,
                    speedList
                )
            }
        }
    }


    /**
     * 用于Js调用，透传分辨率信息
     * @param videoId 视频Id
     * @param curQualityStr 当前视频分辨
     * */
    @JavascriptInterface
    fun sendVideoCurQuality(
        videoId: String,
        curQualityStr: String
    ) {
        val curQuality = PlayerConstants.parsePlaybackQuality(curQualityStr)
        mainThreadHandler.post {
            youTubePlayerOwner.listeners.forEach {
                it.onVideoQualityInfoReady(
                    youTubePlayerOwner.getInstance(),
                    videoId,
                    curQuality,
                    null,
                    null,
                    null
                )
            }
        }
    }
}
