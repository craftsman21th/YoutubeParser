package com.mars.united.international.webplayer.account.subscribe

import com.dubox.drive.kernel.util.INT_0
import com.dubox.drive.kernel.util.INT_1
import com.mars.united.international.webplayer.common.CommonWork
import com.mars.united.international.webplayer.common.commonScope
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.utils.*
import com.mars.united.international.webplayer.parser.work.base.IYoutubeWork
import com.mars.united.international.webplayer.parser.work.callback.SubscribedShortsWorkCallback
import com.mars.united.international.webplayer.parser.work.manager.YoutubeWorkManager.DEFAULT_PAGE_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import kotlin.math.min

/**
 * @Author 陈剑锋
 * @Date 2023/10/25-11:02
 * @Desc 已关注的人发布的shorts列表 任务
 */
class SubscribedShortsWork(
    val lifecycleScope: CoroutineScope = commonScope
) : IYoutubeWork() {

    private class Result(
        val data: List<JsonCaller>,
        val continuationToken: String
    )

    private data class CallbackWrapper(
        val page: Int,
        val pageCount: Int,
        val callback: SubscribedShortsWorkCallback
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

    // 判断 当前请求 是不是 首次请求
    private val isFirstRequest: Boolean
        get() {
            return allResult.size <= INT_0
        }

    // 判断 当前请求 是不是 第二次请求
    private val isSecondRequest: Boolean
        get() {
            return allResult.size == INT_1
        }

    private var apiUrl: String = ""
    private var continuation: String = ""

    override suspend fun needLogin(): Boolean {
        return true
    }

    override suspend fun onWorkInit(): Boolean = withContext(Dispatchers.IO) {
        val ytInitialData = commonWork.getYtInitialData("https://m.youtube.com/feed/subscriptions/shorts")
        // 解析 请求地址
        if (apiUrl.isBlank()) {
            apiUrl = ytInitialData["contents"]["singleColumnBrowseResultsRenderer"]["tabs"]
                .get(INT_0)["tabRenderer"]["content"]["richGridRenderer"]["contents"]
                .map { this["continuationItemRenderer"] }
                .get(INT_0)["continuationEndpoint"]["commandMetadata"]["webCommandMetadata"]["apiUrl"]
                .asString ?: ""
        }
        // 解析 请求令牌
        if (continuation.isBlank()) {
            continuation = ytInitialData["contents"]["singleColumnBrowseResultsRenderer"]["tabs"]
                .get(INT_0)["tabRenderer"]["content"]["richGridRenderer"]["contents"]
                .map { this["continuationItemRenderer"] }
                .get(INT_0)["continuationEndpoint"]["continuationCommand"]["token"]
                .asString ?: ""
        }
        // 解析 第一页的数据
        ytInitialData["contents"]["singleColumnBrowseResultsRenderer"]["tabs"]
            .get(INT_0)["tabRenderer"]["content"]["richGridRenderer"]["contents"]
            .mapAs { this["richItemRenderer"]["content"]["reelItemRenderer"] }
            ?.let {
                allResult.add(
                    Result(
                        data = it,
                        continuationToken = continuation
                    )
                )
            }
        // 有下一页的请求条件 或 已经解析出了第一页的数据
        return@withContext continuation.isNotBlank() && apiUrl.isNotBlank()
                || !allResult.getOrNull(INT_0)?.data.isNullOrEmpty()
    }

    override suspend fun getUrl(): String = withContext(Dispatchers.IO) {
        return@withContext "https://m.youtube.com${apiUrl}?key=${CommonRepo.get().apiKey}&prettyPrint=false"
    }

    override suspend fun getRequestBody(): JsonCaller {
        val config = CommonRepo.get().ytConfig
        val contextParams = JsonCaller.createObject {
            this["client"] = JsonCaller.createObject {
                this["gl"] = config["GL"] ?: "US"
                this["hl"] = config["HL"] ?: "en"
                this["clientName"] = config["INNERTUBE_CLIENT_NAME"] ?: "MWEB"
                this["clientVersion"] = config["INNERTUBE_CLIENT_VERSION"] ?: "2.20230802.00.00"
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
        return this["onResponseReceivedActions"][INT_0]["appendContinuationItemsAction"]["continuationItems"]
            .map { this["continuationItemRenderer"] }
            .get(INT_0)["continuationEndpoint"]["continuationCommand"]["token"]
            .asString ?: ""
    }

    override fun onCallback(errCode: Int?) {
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
        this["onResponseReceivedActions"][INT_0]["appendContinuationItemsAction"]["continuationItems"].map {
            this["richItemRenderer"]["content"]["reelItemRenderer"]
        }.forEach {
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
            allResult.getOrNull(page - INT_1)?.data ?: emptyList()
        } else {
            // 按目标页码和页内数据条数组装分页，有可能需要请求几次才能返回1页
            val startItemIndex = (page - INT_1) * pageCount
            val endItemIndex = page * pageCount - INT_1
            // 将已有结果展开的总列表
            val flatAllResult = allResult.map { it.data }.flatten()
            if (startItemIndex >= flatAllResult.size) {
                return emptyList()
            }
            flatAllResult.subList(
                startItemIndex,
                min(flatAllResult.size, endItemIndex + INT_1)
            )
        }
    }

    /**
     * 尝试回调
     * @param page Int
     * @param pageCount Int
     * @param callback [@kotlin.ExtensionFunctionType] Function5<
     *      SubscribedShortsWork,
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
                    this@SubscribedShortsWork,
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
     *
     * @param page Int
     * @param pageCount Int
     * @param callback [@kotlin.ExtensionFunctionType] Function5<
     *      SubscribedShortsWork,
     *      [@kotlin.ParameterName] Int,
     *      [@kotlin.ParameterName] Int,
     *      [@kotlin.ParameterName] List<JsonCaller>,
     *      [@kotlin.ParameterName] Int?, Unit
     * >
     */
    fun fetch(page: Int, pageCount: Int = DEFAULT_PAGE_COUNT, callback: SubscribedShortsWorkCallback) {
        tryCallback(CallbackWrapper(page, pageCount, callback), null)
    }

}