package com.mars.united.international.webplayer.parser.model

import androidx.annotation.Keep
import com.moder.compass.util.getYtbVideoQuality
import com.google.gson.annotations.SerializedName
import com.mars.united.international.webplayer.common.MIMETYPE_VIDEO_3GPP
import com.mars.united.international.webplayer.common.MIMETYPE_VIDEO_MP4
import com.mars.united.international.webplayer.common.MIMETYPE_VIDEO_WEBM
import kotlin.math.abs

private const val TBR_32KBPS = "32kbps"
private const val TBR_48KBPS = "48kbps"
private const val TBR_96KBPS = "96kbps"
private const val TBR_128KBPS = "128kbps"

private const val TBR_32: Int = 32
private const val TBR_48: Int = 48
private const val TBR_96: Int = 96
private const val TBR_128: Int = 128


/**
 * @Author 陈剑锋
 * @Date 2023/7/12-18:27
 * @Desc Youtube 视频信息
 */
@Keep
data class YoutubeVideoInfo(
    val detailInfo: DetailInfo? = DetailInfo(),
    val videoList: List<Video>? = listOf(),
    val silentVideoList: List<Video>? = listOf(),
    val audioList: List<Audio>? = listOf(),
    val ownerProfileUrl: String? = null
) {

    /**
     * 和指定宽度最匹配的视频（默认按照后台配置返回）
     * @param targetWidth Int
     * @return Video?
     */
    @SuppressWarnings("ComplexCondition")
    fun getMatchedQualityVideo(targetWidth: Int = getYtbVideoQuality().toInt()): Video? {
        var result: Video? = null
        var curMinWidthDiff = Int.MAX_VALUE
        videoList?.forEach { video ->
            val widthDiff = abs((video.width ?: Int.MAX_VALUE) - targetWidth)
            // 优先选择分辨率更匹配的视频
            if (widthDiff < curMinWidthDiff
                || (widthDiff == curMinWidthDiff
                        // 分辨率相同的情况下，优先选择压缩率更高的视频
                        && (result?.mimeType == MIMETYPE_VIDEO_WEBM && video.mimeType != MIMETYPE_VIDEO_WEBM
                        || result?.mimeType == MIMETYPE_VIDEO_3GPP && video.mimeType == MIMETYPE_VIDEO_MP4))
            ) {
                curMinWidthDiff = widthDiff
                result = video
            }
        }
        return result
    }

}

@Keep
data class Video(
    @SerializedName("approxDurationMs")
    var approxDurationMs: String? = "",
    @SerializedName("audioChannels")
    var audioChannels: Int? = 0,
    @SerializedName("audioQuality")
    var audioQuality: String? = "",
    @SerializedName("audioSampleRate")
    var audioSampleRate: String? = "",
    @SerializedName("averageBitrate")
    var averageBitrate: Int? = 0,
    @SerializedName("bitrate")
    var bitrate: Int? = 0,
    @SerializedName("contentLength")
    var contentLength: String? = "",
    @SerializedName("fps")
    var fps: Int? = 0,
    @SerializedName("height")
    var height: Int? = 0,
    @SerializedName("itag")
    var itag: Int? = 0,
    @SerializedName("lastModified")
    var lastModified: String? = "",
    @SerializedName("mimeType")
    var mimeType: String? = "",
    @SerializedName("projectionType")
    var projectionType: String? = "",
    @SerializedName("quality")
    var quality: String? = "",
    @SerializedName("qualityLabel")
    var qualityLabel: String? = "",
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("width")
    var width: Int? = 0
) {

    val duration: String
        get() {
            return kotlin.runCatching {
                val mills = approxDurationMs?.toLong()
                if (mills != null) {
                    val totalSeconds = mills / 1000
                    val seconds = totalSeconds % 60
                    val minutes = totalSeconds / 60 % 60
                    val hours = totalSeconds / 3600
                    if (hours <= 0) {
                        String.format("%02d:%02d", minutes, seconds)
                    } else {
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    }
                } else {
                    ""
                }
            }.getOrNull() ?: ""
        }

    val resolution: String
        get() {
            return if (this.width != null && this.height != null) {
                "${this.width}x${this.height}"
            } else {
                "--"
            }
        }

    val size: Long
        get() {
            return kotlin.runCatching {
                contentLength?.toLong() ?: 0L
            }.getOrNull() ?: 0L
        }

    val sizeText: String
        get() {
            return kotlin.runCatching {
                val bits = contentLength?.toLong()?.toFloat()
                if (bits != null) {
                    "${"%.2f".format(bits / 1024 / 1024)}MB"
                } else {
                    "--"
                }
            }.getOrNull() ?: "--"
        }

}

@Keep
data class DetailInfo(
    @SerializedName("allowRatings")
    var allowRatings: Boolean? = false,
    @SerializedName("author")
    var author: String? = "",
    @SerializedName("channelId")
    var channelId: String? = "",
    @SerializedName("isCrawlable")
    var isCrawlable: Boolean? = false,
    @SerializedName("isLiveContent")
    var isLiveContent: Boolean? = false,
    @SerializedName("isOwnerViewing")
    var isOwnerViewing: Boolean? = false,
    @SerializedName("isPrivate")
    var isPrivate: Boolean? = false,
    @SerializedName("isUnpluggedCorpus")
    var isUnpluggedCorpus: Boolean? = false,
    @SerializedName("lengthSeconds")
    var lengthSeconds: String? = "",
    @SerializedName("shortDescription")
    var shortDescription: String? = "",
    @SerializedName("thumbnail")
    var thumbnail: Thumbnail? = Thumbnail(),
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("videoId")
    var videoId: String? = "",
    @SerializedName("viewCount")
    var viewCount: String? = ""
)

@Keep
data class Thumbnail(
    @SerializedName("thumbnails")
    var thumbnails: List<ThumbnailX>? = listOf()
)

@Keep
data class ThumbnailX(
    @SerializedName("height")
    var height: Int? = 0,
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("width")
    var width: Int? = 0
)

@Keep
data class Audio(
    @SerializedName("approxDurationMs")
    var approxDurationMs: String? = "",
    @SerializedName("audioChannels")
    var audioChannels: Int? = 0,
    @SerializedName("audioQuality")
    var audioQuality: String? = "",
    @SerializedName("audioSampleRate")
    var audioSampleRate: String? = "",
    @SerializedName("averageBitrate")
    var averageBitrate: Int? = 0,
    @SerializedName("bitrate")
    var bitrate: Int? = 0,
    @SerializedName("contentLength")
    var contentLength: String? = "",
    @SerializedName("highReplication")
    var highReplication: Boolean? = false,
    @SerializedName("indexRange")
    var indexRange: IndexRange? = IndexRange(),
    @SerializedName("initRange")
    var initRange: InitRange? = InitRange(),
    @SerializedName("itag")
    var itag: Int? = 0,
    @SerializedName("lastModified")
    var lastModified: String? = "",
    @SerializedName("loudnessDb")
    var loudnessDb: Double? = 0.0,
    @SerializedName("mimeType")
    var mimeType: String? = "",
    @SerializedName("projectionType")
    var projectionType: String? = "",
    @SerializedName("quality")
    var quality: String? = "",
    @SerializedName("url")
    var url: String? = ""
) {

    val size: Long
        get() {
            return kotlin.runCatching {
                contentLength?.toLong() ?: 0L
            }.getOrNull() ?: 0L
        }

    /**
     * 音频码率转成字符串
     */
    val resolution: String
        get() {
            val tbr = (averageBitrate ?: 0) / 1000
            if (abs(tbr - TBR_32) <= 8) {
                return TBR_32KBPS
            }
            if (abs(tbr - TBR_48) <= 8) {
                return TBR_48KBPS
            }
            if (abs(tbr - TBR_96) <= 8) {
                return TBR_96KBPS
            }
            if (abs(tbr - TBR_128) <= 8) {
                return TBR_128KBPS
            }
            return "${tbr.toInt()}kbps"
        }

}

@Keep
data class IndexRange(
    @SerializedName("end")
    var end: String? = "",
    @SerializedName("start")
    var start: String? = ""
)

@Keep
data class InitRange(
    @SerializedName("end")
    var end: String? = "",
    @SerializedName("start")
    var start: String? = ""
)