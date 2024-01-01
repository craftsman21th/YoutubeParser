package com.mars.united.international.webplayer.account.posts

import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.dubox.drive.kernel.util.INT_0
import com.dubox.drive.kernel.util.INT_1
import com.dubox.drive.kernel.util.INT_2
import com.mars.united.international.webplayer.common.CommonWork
import com.mars.united.international.webplayer.common.commonScope
import com.mars.united.international.webplayer.common.getOwnerProfileUrl
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.TAG
import com.mars.united.international.webplayer.parser.utils.*
import com.mars.united.international.webplayer.parser.work.base.IYoutubeWork
import com.mars.united.international.webplayer.parser.work.callback.VideoPostListWorkCallback
import com.mars.united.international.webplayer.parser.work.manager.YoutubeWorkManager.DEFAULT_PAGE_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import kotlin.math.min

/**
 * @Author 陈剑锋
 * @Date 2023/8/1-10:40
 * @Desc 视频发布列表 任务
 */
class VideoPostListWork(
    val lifecycleScope: CoroutineScope = commonScope,
    val channelId: String,
    val videoType: VideoType
) : IYoutubeWork() {

    private class Result(
        val data: List<JsonCaller>,
        val continuationToken: String
    )

    private data class CallbackWrapper(
        val page: Int,
        val pageCount: Int,
        val callback: VideoPostListWorkCallback
    )

    private val commonWork: CommonWork by lazy { CommonWork() }

    // 等待回调的集合
    private val waitingForCallbackSet: HashSet<CallbackWrapper> = hashSetOf()

    // 将 多次请求 的结果 放在列表里
    private var allResult: MutableList<Result> = mutableListOf()

    // 判断是否有更多数据了
    private val hasMore: Boolean
        get() {
            if (allResult.isNotEmpty() && allResult.last().data.isEmpty()) {
                // 最后一次请求结果的数据为空，则表示没有更多数据
                return false
            }
            return true
        }

    // 最后一次返回的 用于加载更多的token
    private val lastContinuationToken: String
        get() {
            return if (allResult.isNotEmpty()) {
                allResult.last().continuationToken
            } else {
                ""
            }
        }

    // 判断 当前请求 是不是 首次请求
    private val isFirstRequest: Boolean
        get() {
            return allResult.size <= 0
        }

    // 判断 当前请求 是不是 第二次请求
    private val isSecondRequest: Boolean
        get() {
            return allResult.size == 1
        }

    private var apiUrl: String = ""
    private var continuation: String = ""

    override suspend fun onWorkInit(): Boolean = withContext(Dispatchers.IO) {
        val ytInitialData = commonWork.getYtInitialData(getOwnerProfileUrl(channelId))
        // 解析 请求地址
        if (apiUrl.isBlank()) {
            apiUrl = ytInitialData["contents"]["singleColumnBrowseResultsRenderer"]["tabs"].filter {
                this["tabRenderer"]["content"]["sectionListRenderer"]["continuations"] != null
            }[if (videoType is VideoType.VIDEOS) INT_0 else INT_1]
                .get("tabRenderer")["endpoint"]["commandMetadata"]["webCommandMetadata"]["apiUrl"]
                .asString ?: ""
        }
        // 解析 请求令牌
        if (continuation.isBlank()) {
            continuation = ytInitialData["contents"]["singleColumnBrowseResultsRenderer"]["tabs"].map {
                this["tabRenderer"]["content"]["sectionListRenderer"]["continuations"]
            }[if (videoType is VideoType.VIDEOS) INT_0 else INT_1]
                .get(INT_0)["reloadContinuationData"]["continuation"]
                .asString ?: ""
        }
        return@withContext continuation.isNotBlank() && apiUrl.isNotBlank()
    }

    override suspend fun getUrl(): String = withContext(Dispatchers.IO) {
        return@withContext "https://m.youtube.com${apiUrl}?key=${CommonRepo.get().apiKey}&prettyPrint=false"
    }

    @SuppressWarnings("ComplexMethod")
    override suspend fun getRequestBody(): JsonCaller {
        val config = CommonRepo.get().ytConfig
        val contextParams = JsonCaller.createObject {
            this["client"] = JsonCaller.createObject {
                this["acceptHeader"] = config["INNERTUBE_CONTEXT"]["client"]["acceptHeader"] ?: ""
                this["browserName"] = config["INNERTUBE_CONTEXT"]["client"]["browserName"] ?: ""
                this["browserVersion"] = config["INNERTUBE_CONTEXT"]["client"]["browserVersion"] ?: ""
                this["clientFormFactor"] =
                    config["INNERTUBE_CONTEXT"]["client"]["clientFormFactor"] ?: "SMALL_FORM_FACTOR"
                this["clientName"] = config["INNERTUBE_CLIENT_NAME"] ?: "MWEB"
                this["clientVersion"] = config["INNERTUBE_CLIENT_VERSION"] ?: "2.20230802.00.00"
                this["configInfo"] = config["INNERTUBE_CONTEXT"]["client"]["configInfo"] ?: {}
                this["deviceExperimentId"] = config["INNERTUBE_CONTEXT"]["client"]["deviceExperimentId"] ?: ""
                this["deviceMake"] = config["INNERTUBE_CONTEXT"]["client"]["deviceMake"] ?: "Samsung"
                this["deviceModel"] = config["INNERTUBE_CONTEXT"]["client"]["deviceModel"] ?: "SM-G955U"
                this["gl"] = config["GL"] ?: "US"
                this["hl"] = config["HL"] ?: "en"
                this["mainAppWebInfo"] = JsonCaller.createObject {
                    this["graftUrl"] = if (videoType is VideoType.VIDEOS) "https://m.youtube.com/${channelId}/videos"
                    else "https://m.youtube.com/${channelId}/shorts"
                    this["webDisplayMode"] = "WEB_DISPLAY_MODE_BROWSER"
                    this["isWebNativeShareAvailable"] = false
                }
                this["originalUrl"] = "https://m.youtube.com/"
                this["osName"] = config["INNERTUBE_CONTEXT"]["client"]["osName"] ?: "Android"
                this["osVersion"] = config["INNERTUBE_CONTEXT"]["client"]["osVersion"] ?: "8.0.0"
                this["platform"] = config["INNERTUBE_CONTEXT"]["client"]["platform"] ?: ""
                this["playerType"] = config["INNERTUBE_CONTEXT"]["client"]["playerType"] ?: ""
                this["screenDensityFloat"] = config["INNERTUBE_CONTEXT"]["client"]["screenDensityFloat"] ?: INT_2
                this["screenPixelDensity"] = config["INNERTUBE_CONTEXT"]["client"]["screenPixelDensity"] ?: INT_2
                this["timeZone"] = config["INNERTUBE_CONTEXT"]["client"]["timeZone"] ?: ""
                this["userAgent"] = config["INNERTUBE_CONTEXT"]["client"]["userAgent"] ?: ""
                this["visitorData"] = config["VISITOR_DATA"] ?: ""
            }
            this["clientScreenNonce"] = "MC40MDI0NjgxNTY0Mjc5MDM4"
            this["user"] = JsonCaller.createObject {
                this["lockedSafetyMode"] = config["INNERTUBE_CONTEXT"]["user"]["lockedSafetyMode"] ?: false
            }
            this["request"] = JsonCaller.createObject {
                this["useSsl"] = config["INNERTUBE_CONTEXT"]["request"]["useSsl"] ?: true
                this["internalExperimentFlags"] = JsonCaller.createArray()
                this["consistencyTokenJars"] = JsonCaller.createArray()
            }
        }
        return JsonCaller.createObject {
            this["context"] = contextParams
            this["continuation"] = allResult.lastOrNull()?.continuationToken ?: continuation
        }
    }

    /**
     * 处理请求结果
     * @param response Response
     * @return Boolean
     */
    override suspend fun onHandleResponse(response: Response): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = response.body()?.string() ?: ""
            val rawInfo = JsonCaller.create(body)
            if (rawInfo != null) {
                val continuationToken = rawInfo.extractContinuationToken()
                allResult.add(Result(rawInfo.extractResult(), continuationToken))
                return@withContext true
            } else {
                return@withContext false
            }
        } catch (e: Throwable) {
            return@withContext false
        }
    }

    /**
     * 提取 继续请求 需要用到的token
     * @receiver JsonCaller?
     * @param isFirstRequest Boolean
     * @return String
     */
    private fun JsonCaller?.extractContinuationToken(): String {
        return if (isFirstRequest) {
            this["continuationContents"]["richGridContinuation"]["header"]["feedFilterChipBarRenderer"]["contents"]
                .get(INT_0)["chipCloudChipRenderer"]["navigationEndpoint"]["continuationCommand"]["token"]
                .asString ?: ""
        } else if (isSecondRequest) {
            this["onResponseReceivedActions"][INT_0]["reloadContinuationItemsCommand"]["continuationItems"].map {
                this["continuationItemRenderer"]
            }[INT_0]["continuationEndpoint"]["continuationCommand"]["token"]
                .asString ?: ""
        } else {
            this["onResponseReceivedActions"][INT_0]["appendContinuationItemsAction"]["continuationItems"].map {
                this["continuationItemRenderer"]
            }[INT_0]["continuationEndpoint"]["continuationCommand"]["token"]
                .asString ?: ""
        }
    }

    override fun onCallback(errCode: Int?) {
        DuboxLog.e(TAG, "request result - continuationToken:${lastContinuationToken}")
        if (errCode != null) {
            // 请求失败回调
            DuboxLog.e(TAG, "request result Error - errCode: ${errCode}")
        } else {
            DuboxLog.e(
                TAG, "request result Success - " +
                        "Request count: ${allResult.size}, All Result Size: ${allResult.map { it.data }.flatten().size}"
            )
        }
        val iterator = waitingForCallbackSet.iterator()
        while (iterator.hasNext()) {
            val callbackWrapper = iterator.next()
            if (tryCallback(callbackWrapper, errCode)) {
                iterator.remove()
            }
        }
    }

    /**
     * 提取请求结果
     * @receiver JsonCaller?
     * @param isFirstRequest Boolean
     * @return List<JsonCaller>
     */
    private fun JsonCaller?.extractResult(): List<JsonCaller> {
        this ?: return emptyList()
        val result = mutableListOf<JsonCaller>()
        (when (videoType) {
            is VideoType.VIDEOS -> {
                if (isFirstRequest) {
                    this["continuationContents"]["richGridContinuation"]["contents"].map {
                        this["richItemRenderer"]["content"]["compactVideoRenderer"]
                    }
                } else if (isSecondRequest) {
                    this["onResponseReceivedActions"][INT_0]["reloadContinuationItemsCommand"]
                        .get("continuationItems").map {
                            this["richItemRenderer"]["content"]["compactVideoRenderer"]
                        }
                } else {
                    this["onResponseReceivedActions"][INT_0]["appendContinuationItemsAction"]["continuationItems"].map {
                        this["richItemRenderer"]["content"]["compactVideoRenderer"]
                    }
                }
            }

            is VideoType.SHORTS -> {
                if (isFirstRequest) {
                    this["continuationContents"]["richGridContinuation"]["contents"].map {
                        this["richItemRenderer"]["content"]["reelItemRenderer"]
                    }
                } else if (isSecondRequest) {
                    this["onResponseReceivedActions"][INT_0]["reloadContinuationItemsCommand"]
                        .get("continuationItems").map {
                            this["richItemRenderer"]["content"]["reelItemRenderer"]
                        }
                } else {
                    this["onResponseReceivedActions"][INT_0]["appendContinuationItemsAction"]
                        .get("continuationItems").map {
                            this["richItemRenderer"]["content"]["reelItemRenderer"]
                        }
                }
            }
        }).forEach {
            result.add(this)
        }
        return result
    }

    /**
     * 取当前已有结果中的分页数据（可能取不满）
     * @param page Int
     * @param pageCount Int
     * @return List<JsonCaller>
     */
    private fun getSplitResult(page: Int, pageCount: Int): List<JsonCaller> {
        return if (pageCount == DEFAULT_PAGE_COUNT) {
            // 不自己组装分页，每次请求接口返回几条数据，就作为当前分页数据条数（每一页的数据条数可能不一样）

            // 由于第1次请求和第2次请求的数据一模一样，取值的时候跳过第2次请求（index=1）的结果
            val validIndex = if (page <= INT_1) INT_0 else page

            allResult.getOrNull(validIndex)?.data ?: emptyList()
        } else {
            // 按目标页码和页内数据条数组装分页，有可能需要请求几次才能返回1页
            val startItemIndex = (page - 1) * pageCount
            val endItemIndex = page * pageCount - 1
            // 将已有结果展开的总列表
            val flatAllResult = allResult.mapIndexed { index, result ->

                // 由于第1次请求和第2次请求的数据一模一样，取值的时候跳过第2次请求（index=1）的结果
                if (index == INT_1) emptyList() else result.data

            }.flatten()
            if (startItemIndex >= flatAllResult.size) {
                return emptyList()
            }
            flatAllResult.subList(
                startItemIndex,
                min(flatAllResult.size, endItemIndex + 1)
            )
        }
    }

    /**
     * 尝试回调
     * @param page Int
     * @param pageCount Int
     * @param callback [@kotlin.ExtensionFunctionType] Function5<
     *      SearchWork,
     *      [@kotlin.ParameterName] Int,
     *      [@kotlin.ParameterName] Int,
     *      [@kotlin.ParameterName] List<JsonCaller>,
     *      [@kotlin.ParameterName] Int?, Unit
     * >
     * @return Boolean  是否回调成功
     */
    private fun tryCallback(callbackWrapper: CallbackWrapper, errCode: Int?): Boolean {
        // 当前页 期待返回的数据量
        val wantedSplitSize = if (callbackWrapper.pageCount == DEFAULT_PAGE_COUNT) {
            // 如果 期待的页内数据条数，是按照接口请求结果是多少就返回多少，即表示只要有数据就行，即期待数为1
            INT_1
        } else {
            callbackWrapper.pageCount
        }
        // 当前页 已有的数据量
        val existResult = getSplitResult(callbackWrapper.page, callbackWrapper.pageCount)
        return if (errCode == null && existResult.size < wantedSplitSize && hasMore) {
            // 没发生错误 && 没有取满 & 有更多数据 → 无法回调，需要加载更多
            // 将回调加入等待列表
            waitingForCallbackSet.add(callbackWrapper)
            start()
            false
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                callbackWrapper.callback.invoke(
                    this@VideoPostListWork,
                    callbackWrapper.page,
                    callbackWrapper.pageCount,
                    existResult,
                    errCode
                )
            }
            true
        }
    }

    /**
     * 拉取分页信息
     * @param page Int
     * @param pageCount Int
     * @param callback [@kotlin.ExtensionFunctionType] Function5<
     *      SearchWork,
     *      [@kotlin.ParameterName] Int,
     *      [@kotlin.ParameterName] Int,
     *      [@kotlin.ParameterName] List<JsonCaller>,
     *      [@kotlin.ParameterName] Int?, Unit
     * >
     */
    fun fetch(page: Int, pageCount: Int = DEFAULT_PAGE_COUNT, callback: VideoPostListWorkCallback) {
        tryCallback(CallbackWrapper(page, pageCount, callback), null)
    }

}