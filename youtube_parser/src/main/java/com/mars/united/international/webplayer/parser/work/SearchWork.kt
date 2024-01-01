package com.mars.united.international.webplayer.parser.work

import android.util.Log
import com.dubox.drive.kernel.util.INT_1
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.TAG
import com.mars.united.international.webplayer.parser.utils.*
import com.mars.united.international.webplayer.parser.work.base.IYoutubeWork
import com.mars.united.international.webplayer.parser.work.callback.SearchWorkCallback
import com.mars.united.international.webplayer.parser.work.manager.YoutubeWorkManager.DEFAULT_PAGE_COUNT
import com.mars.united.international.webplayer.parser.work.manager.YoutubeWorkManager.mainThreadHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response
import kotlin.math.min

/**
 * @Author 陈剑锋
 * @Date 2023/8/1-10:40
 * @Desc Youtube 搜索 任务
 */
class SearchWork(
    val searchText: String
) : IYoutubeWork() {

    private class Result(
        val data: List<JsonCaller>,
        val continuationToken: String
    )

    private data class CallbackWrapper(
        val page: Int,
        val pageCount: Int,
        val callback: SearchWorkCallback
    )

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

    override suspend fun getUrl(): String {
        return "https://www.youtube.com/youtubei/v1/search?key=${CommonRepo.get().apiKey}"
    }

    override suspend fun getRequestBody(): JsonCaller {
        val commonRepo = CommonRepo.get()
        val params = JsonCaller.createObject()
        params["context"] = JsonCaller.createObject().apply {
            this["client"] = JsonCaller.createObject().apply {
                this["hl"] = commonRepo.ytConfig["HL"].asString ?: ""
                this["clientName"] =
                    commonRepo.ytConfig["INNERTUBE_CLIENT_NAME"].asString ?: "MWEB"
                this["clientVersion"] =
                    commonRepo.ytConfig["INNERTUBE_CLIENT_VERSION"].asString ?: "2.20230616.01.00"
                this["playerType"] = "UNIPLAYER"
                this["platform"] = "MOBILE"
                this["clientFormFactor"] = "SMALL_FORM_FACTOR"
                this["mainAppWebInfo"] = JsonCaller.createObject().apply {
                    this["graftUrl"] = "/results?sp=mAEA&search_query=${
                        executeJs("decodeURIComponent('${searchText}')")
                    }"
                }
            }
            this["user"] = JsonCaller.createObject().apply {
                this["lockedSafetyMode"] = false
            }
            this["request"] = JsonCaller.createObject().apply {
                this["useSsl"] = true
                this["internalExperimentFlags"] = JsonCaller.createArray()
                this["consistencyTokenJars"] = JsonCaller.createArray()
            }
        }
        // 区分首次请求和加载更多，构造不同参数
        if (isFirstRequest) {
            params["query"] = searchText
            params["params"] = "mAEA"
        } else {
            params["continuation"] = lastContinuationToken
        }
        return params
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
                val continuationToken = rawInfo.extractContinuationToken(isFirstRequest)
                allResult.add(Result(rawInfo.extractResult(isFirstRequest), continuationToken))
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
    private fun JsonCaller?.extractContinuationToken(isFirstRequest: Boolean): String {
        return if (isFirstRequest) {
            this["contents"]["sectionListRenderer"]["contents"].filter {
                has("continuationItemRenderer")
            }[0]["continuationItemRenderer"]["continuationEndpoint"]["continuationCommand"]["token"]
                .asString ?: ""
        } else {
            this["onResponseReceivedCommands"].filter {
                has("appendContinuationItemsAction")
            }[0]["appendContinuationItemsAction"]["continuationItems"].filter {
                has("continuationItemRenderer")
            }[0]["continuationItemRenderer"]["continuationEndpoint"]["continuationCommand"]["token"]
                .asString ?: ""
        }
    }

    override fun onCallback(errCode: Int?) {
        Log.e(TAG, "search result - continuationToken:${lastContinuationToken}")
        if (errCode != null) {
            // 请求失败回调
            Log.e(TAG, "search result Error - errCode: ${errCode}")
        } else {
            Log.e(
                TAG, "search result Success - " +
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
    private fun JsonCaller?.extractResult(isFirstRequest: Boolean): List<JsonCaller> {
        this ?: return emptyList()
        val result = mutableListOf<JsonCaller>()
        (if (isFirstRequest) {
            this["contents"]["sectionListRenderer"]["contents"]
        } else {
            this["onResponseReceivedCommands"][0]["appendContinuationItemsAction"]["continuationItems"]
        }).map {
            this["itemSectionRenderer"]["contents"]
        }.forEach {
            this.map {
                this["videoWithContextRenderer"]
            }.forEach {
                result.add(this)
            }
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
            allResult.getOrNull(page - 1)?.data ?: emptyList()
        } else {
            // 按目标页码和页内数据条数组装分页，有可能需要请求几次才能返回1页
            val startItemIndex = (page - 1) * pageCount
            val endItemIndex = page * pageCount - 1
            // 将已有结果展开的总列表
            val flatAllResult = allResult.map { it.data }.flatten()
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
            mainThreadHandler.post {
                callbackWrapper.callback.invoke(
                    this@SearchWork,
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
    fun fetch(page: Int, pageCount: Int = DEFAULT_PAGE_COUNT, callback: SearchWorkCallback) {
        tryCallback(CallbackWrapper(page, pageCount, callback), null)
    }

}