package com.mars.united.international.webplayer.parser.work

import com.dubox.drive.kernel.util.INT_0
import com.dubox.drive.kernel.util.INT_1
import com.dubox.drive.kernel.util.INT_2
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.mars.united.international.webplayer.account.repo.AccountRepo
import com.mars.united.international.webplayer.common.MIMETYPE_VIDEO_3GPP
import com.mars.united.international.webplayer.common.MIMETYPE_VIDEO_MP4
import com.mars.united.international.webplayer.common.MIMETYPE_VIDEO_WEBM
import com.mars.united.international.webplayer.common.commonScope
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.model.Audio
import com.mars.united.international.webplayer.parser.model.DetailInfo
import com.mars.united.international.webplayer.parser.model.Video
import com.mars.united.international.webplayer.parser.model.YoutubeVideoInfo
import com.mars.united.international.webplayer.parser.repo.ParserRepo
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.utils.asBoolean
import com.mars.united.international.webplayer.parser.utils.asJsonObject
import com.mars.united.international.webplayer.parser.utils.asString
import com.mars.united.international.webplayer.parser.utils.checkKeyParamsValid
import com.mars.united.international.webplayer.parser.utils.executeJs
import com.mars.united.international.webplayer.parser.utils.forEach
import com.mars.united.international.webplayer.parser.utils.get
import com.mars.united.international.webplayer.parser.utils.set
import com.mars.united.international.webplayer.parser.work.base.IYoutubeWork
import com.mars.united.international.webplayer.parser.work.callback.FetchVideoInfoWorkCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response

private typealias WorkCallback = (videoInfo: YoutubeVideoInfo?) -> Unit

/**
 * @Author 陈剑锋
 * @Date 2023/8/1-10:40
 * @Desc Youtube 获取视频信息 任务
 */
class FetchVideoInfoWork(
    val id: String,
    val lifecycleScope: CoroutineScope = commonScope,
    val onCallback: FetchVideoInfoWorkCallback
) : IYoutubeWork() {

    private val accountRepo: AccountRepo by lazy {
        AccountRepo.get()
    }

    private var videoInfo: YoutubeVideoInfo? = null

    override suspend fun getUrl(): String {
        return "https://www.youtube.com/youtubei/v1/player?key=${CommonRepo.get().apiKey}"
    }

    override suspend fun getRequestBody(): JsonCaller {
        return JsonCaller.createObject().apply {
            this["context"] = createParamContext()
            this["videoId"] = id
            this["playbackContext"] = createParamPlaybackContext()
            this["racyCheckOk"] = false
            this["contentCheckOk"] = false
        }
    }

    private fun createParamContext(): JsonCaller {
        val ytConfig = CommonRepo.get().ytConfig
        val innerTubeContext = ytConfig["INNERTUBE_CONTEXT"]
        return JsonCaller.createObject().apply {
            this["client"] = createParamContextParamClient()
            this["user"] = JsonCaller.createObject().apply {
                this["lockedSafetyMode"] = innerTubeContext["user"]["lockedSafetyMode"].asBoolean ?: false
            }
            this["request"] = JsonCaller.createObject().apply {
                this["useSsl"] = innerTubeContext["request"]["useSsl"].asBoolean ?: true
                this["internalExperimentFlags"] = JsonCaller.createArray()
                this["consistencyTokenJars"] = JsonCaller.createArray()
            }
        }
    }

    private fun createParamContextParamClient(): JsonCaller {
        return JsonCaller.createObject().apply {
            loadClient1()
            loadClient2()
        }
    }

    private fun JsonCaller.loadClient1() {
        val ytConfig = CommonRepo.get().ytConfig
        val innerTubeContext = ytConfig["INNERTUBE_CONTEXT"]
        this["hl"] = ytConfig["HL"].asString ?: "en"
        this["gl"] = ytConfig["GL"].asString ?: "US"
        this["deviceMake"] = innerTubeContext["client"]["deviceMake"] ?: "Apple"
        this["deviceModel"] = innerTubeContext["client"]["deviceModel"] ?: "iPhone"
        this["visitorData"] = ytConfig["VISITOR_DATA"].asString ?: ""
        this["userAgent"] = innerTubeContext["client"]["userAgent"] ?: ""
        this["clientName"] = ytConfig["INNERTUBE_CLIENT_NAME"].asString ?: "WEB"
        this["clientVersion"] = ytConfig["INNERTUBE_CLIENT_VERSION"].asString ?: "2.20230616.01.00"
        this["osName"] = innerTubeContext["client"]["osName"] ?: "iPhone"
        this["osVersion"] = innerTubeContext["client"]["osVersion"] ?: "13_2_3"
        this["screenPixelDensity"] = innerTubeContext["client"]["screenPixelDensity"] ?: 2
        this["screenDensityFloat"] = innerTubeContext["client"]["screenDensityFloat"] ?: 2
    }

    private fun JsonCaller.loadClient2() {
        val ytConfig = CommonRepo.get().ytConfig
        val innerTubeContext = ytConfig["INNERTUBE_CONTEXT"]
        this["timeZone"] = innerTubeContext["client"]["timeZone"] ?: ""
        this["platform"] = innerTubeContext["client"]["platform"] ?: ""
        this["browserName"] = innerTubeContext["client"]["browserName"] ?: ""
        this["browserVersion"] = innerTubeContext["client"]["browserVersion"] ?: ""
        this["acceptHeader"] = innerTubeContext["client"]["acceptHeader"] ?: ""
        this["deviceExperimentId"] = innerTubeContext["client"]["deviceExperimentId"] ?: ""
        this["clientFormFactor"] = innerTubeContext["client"]["clientFormFactor"] ?: "SMALL_FORM_FACTOR"
        this["configInfo"] = innerTubeContext["client"]["configInfo"] ?: JsonCaller.createObject()
        this["originalUrl"] = "https://m.youtube.com/watch?v=${id}"
        this["mainAppWebInfo"] = JsonCaller.createObject().apply {
            this["graftUrl"] = "/watch?v=${id}"
            this["webDisplayMode"] = "WEB_DISPLAY_MODE_BROWSER"
            this["isWebNativeShareAvailable"] = false
        }
    }

    private fun createParamPlaybackContext(): JsonCaller {
        return JsonCaller.createObject().apply {
            this["contentPlaybackContext"] = JsonCaller.createObject().apply {
                this["currentUrl"] = "/watch?v=${id}"
                this["vis"] = 0
                this["splay"] = false
                this["autoCaptionsDefaultOn"] = false
                this["autonavState"] = "STATE_ON"
                this["html5Preference"] = "HTML5_PREF_WANTS"
                this["lactMilliseconds"] = "-1"
                this["referer"] = "https://m.youtube.com/watch?v=${id}"
                if (ParserRepo.get().signatureTimestamp != INT_0) {
                    this["signatureTimestamp"] = ParserRepo.get().signatureTimestamp.toString()
                }
            }
        }
    }

    /**
     * 处理请求结果
     */
    override suspend fun onHandleResponse(response: Response): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = response.body()?.string() ?: ""
            val rawInfo = JsonParser().parse(body).asJsonObject
            if (rawInfo != null) {
                // 获取视频详情
                val videoDetails = rawInfo.getAsJsonObject("videoDetails") ?: kotlin.run {
                    return@withContext false
                }
                // 重新组装为更精简的json
                // 解析实体并回调
                val gson = Gson()
                videoInfo = kotlin.runCatching {
                    YoutubeVideoInfo(
                        detailInfo = gson.fromJson(videoDetails, DetailInfo::class.java),
                        videoList = gson.fromJson(
                            parseVideos(rawInfo),
                            object : TypeToken<List<Video?>?>() {}.type
                        ),
                        silentVideoList = gson.fromJson(
                            parseSilentVideos(rawInfo),
                            object : TypeToken<List<Video?>?>() {}.type
                        ),
                        audioList = gson.fromJson(
                            parseAudios(rawInfo),
                            object : TypeToken<List<Audio?>?>() {}.type
                        ),
                        ownerProfileUrl = JsonCaller.create(rawInfo)
                            .get("microformat")
                            .get("playerMicroformatRenderer")
                            .get("ownerProfileUrl")
                            .asString
                    )
                }.getOrNull()
                return@withContext true
            } else {
                return@withContext false
            }
        } catch (e: Throwable) {
            return@withContext false
        }
    }

    /**
     * 过滤可用的 普通视频 信息
     * @param rawInfo JsonObject
     * @return JsonArray
     */
    @SuppressWarnings("ComplexCondition")
    private fun parseVideos(rawInfo: JsonObject): JsonArray {
        val result = JsonArray()
        JsonCaller.create(rawInfo)["streamingData"]["formats"].forEach {
            val mimeType = this["mimeType"].asString
            if (mimeType != null
                && (mimeType.contains(MIMETYPE_VIDEO_MP4)
                        || mimeType.contains(MIMETYPE_VIDEO_3GPP)
                        || mimeType.contains(MIMETYPE_VIDEO_WEBM))
            ) {
                result.add(asJsonObject?.checkAndDecrypt() ?: return@forEach)
            }
        }
        return result
    }

    /**
     * 过滤可用的 无声视频 信息
     * @param rawInfo JsonObject
     * @return JsonArray
     */
    @SuppressWarnings("ComplexCondition")
    private fun parseSilentVideos(rawInfo: JsonObject): JsonArray {
        val result = JsonArray()
        JsonCaller.create(rawInfo)["streamingData"]["adaptiveFormats"].forEach {
            val mimeType = this["mimeType"].asString
            if (mimeType != null
                && (mimeType.contains(MIMETYPE_VIDEO_MP4)
                        || mimeType.contains(MIMETYPE_VIDEO_3GPP)
                        || mimeType.contains(MIMETYPE_VIDEO_WEBM))
            ) {
                result.add(asJsonObject?.checkAndDecrypt() ?: return@forEach)
            }
        }
        return result
    }

    /**
     * 过滤可用的音频信息
     * @param rawInfo JsonObject
     * @return JsonArray
     */
    private fun parseAudios(rawInfo: JsonObject): JsonArray {
        val result = JsonArray()
        val rawAudioJsonArr = (rawInfo
            .getAsJsonObject("streamingData")
            ?.getAsJsonArray("adaptiveFormats")
            ?: JsonArray())
        rawAudioJsonArr.iterator().forEach { rawAudioJsonElement ->
            val audioJsonObject = rawAudioJsonElement.asJsonObject
            val mimeType = audioJsonObject.get("mimeType")
            if (mimeType != null &&
                mimeType.isJsonPrimitive &&
                mimeType.asString.contains("audio")
            ) {
                result.add(audioJsonObject.checkAndDecrypt() ?: return@forEach)
            }
        }
        return result
    }

    /**
     * 在回调函数中将视频信息传递给主线程。
     */
    override fun onCallback(errCode: Int?) {
        lifecycleScope.launch(Dispatchers.Main.immediate) {
            onCallback(videoInfo)
        }
    }

    /**
     * 检查并解密url
     * @receiver JsonObject
     * @return JsonObject?
     */
    private fun JsonObject.checkAndDecrypt(): JsonObject? {
        val json = JsonCaller.create(this.deepCopy())
        val signatureCipher = json["signatureCipher"].asString
        if (json["url"].asString.checkKeyParamsValid()) {
            // 包含url字段，不需熬、要解密
            return json.asJsonObject
        } else if (signatureCipher.checkKeyParamsValid()) {
            // 包含signatureCipher字段，执行解密
            val cipherParams = signatureCipher?.split('&') ?: return null
            var cipherText = cipherParams.getOrNull(INT_0)?.split('=')?.getOrNull(INT_1) ?: return null
            val sign = cipherParams.getOrNull(INT_1)?.split('=')?.getOrNull(INT_1) ?: return null
            var url = cipherParams.getOrNull(INT_2)?.split('=')?.getOrNull(INT_1) ?: return null
            while (cipherText.contains('%')) {
                cipherText = executeJs("decodeURIComponent('${cipherText}')")
            }
            while (url.contains('%')) {
                url = executeJs("decodeURIComponent('${url}')")
            }
            // 执行js解密函数
            if (ParserRepo.get().decryptMethod.checkKeyParamsValid()) {
                val plainText = executeJs("(${ParserRepo.get().decryptMethod})('${cipherText}')")
                url = "${url}&${sign}=${plainText}"
            }
            json["url"] = url
            return json.asJsonObject
        } else {
            // 不包含url字段，也不包含signatureCipher字段，无法执行解密
            return null
        }
    }

}