package com.mars.united.international.webplayer.account.subscribe

import com.moder.compass.BaseApplication
import com.dubox.drive.kernel.util.ToastHelper
import com.mars.united.international.webplayer.R
import com.mars.united.international.webplayer.account.login.LoginWork
import com.mars.united.international.webplayer.account.model.SubscribeInfoItem
import com.mars.united.international.webplayer.account.repo.AccountRepo
import com.mars.united.international.webplayer.account.subscribe.callbacks.SubscribeInfo
import com.mars.united.international.webplayer.account.subscribe.callbacks.SubscribeState
import com.mars.united.international.webplayer.common.CommonWork
import com.mars.united.international.webplayer.common.OWNER_PROFILE_URL_EXAMPLE
import com.mars.united.international.webplayer.common.YOUTUBE_HOME_PAGE_URL
import com.mars.united.international.webplayer.common.add
import com.mars.united.international.webplayer.common.commonHttpClient
import com.mars.united.international.webplayer.common.commonScope
import com.mars.united.international.webplayer.common.getCommonHeaders
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.utils.add
import com.mars.united.international.webplayer.parser.utils.asBoolean
import com.mars.united.international.webplayer.parser.utils.asString
import com.mars.united.international.webplayer.parser.utils.checkKeyParamsValid
import com.mars.united.international.webplayer.parser.utils.filter
import com.mars.united.international.webplayer.parser.utils.forEach
import com.mars.united.international.webplayer.parser.utils.get
import com.mars.united.international.webplayer.parser.utils.map
import com.mars.united.international.webplayer.parser.utils.set
import com.mars.united.international.webplayer.parser.work.FetchVideoInfoWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

/**
 * @Author 陈剑锋
 * @Date 2023/9/11-10:39
 * @Desc 订阅相关操作
 */
class SubscribeWork {

    companion object {
        private const val TAG: String = "SubscribeWork"
    }

    private val accountRepo: AccountRepo by lazy { AccountRepo.get() }
    private val commonRepo: CommonRepo by lazy { CommonRepo.get() }

    private val commonWork: CommonWork by lazy { CommonWork() }

    /**
     * 获取 订阅Key
     * @return String
     */
    private suspend fun getSubscribeKey(): String = withContext(Dispatchers.IO) {
        if (commonRepo.subscribeKey.checkKeyParamsValid()) {
            return@withContext commonRepo.subscribeKey
        }
        commonWork.getYtInitialData(OWNER_PROFILE_URL_EXAMPLE)
        return@withContext commonRepo.subscribeKey
    }

    /**
     * 获取 取消订阅Key
     * @return String
     */
    private suspend fun getUnsubscribeKey(): String = withContext(Dispatchers.IO) {
        if (commonRepo.unsubscribeKey.checkKeyParamsValid()) {
            return@withContext commonRepo.unsubscribeKey
        }
        commonWork.getYtInitialData(OWNER_PROFILE_URL_EXAMPLE)
        return@withContext commonRepo.unsubscribeKey
    }

    /**
     * 通过 owner主页链接 检查 订阅状态
     * @param ownerProfileUrl String
     * @param lifecycleScope CoroutineScope
     * @param checkLoginState Boolean
     * @param onResult Function1<[@kotlin.ParameterName] Boolean, Unit>
     */
    fun checkSubscribeByOwnerProfileUrl(
        ownerProfileUrl: String,
        lifecycleScope: CoroutineScope = commonScope,
        checkLoginState: Boolean = true,
        onResult: SubscribeState.() -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val ytInitialData = commonWork.getYtInitialData(ownerProfileUrl)
            val channelId = ytInitialData
                .get("header").get("c4TabbedHeaderRenderer").get("channelId").asString ?: ""
            val isSubscribed = ytInitialData
                .get("header").get("c4TabbedHeaderRenderer").get("subscribeButton")
                .get("subscribeButtonRenderer").get("subscribed").asBoolean ?: false
            withContext(Dispatchers.Main.immediate) {
                onResult.invoke(
                    SubscribeState.Success(
                        channelId = channelId,
                        isFollowing = isSubscribed
                    )
                )
            }
        }
    }

    /**
     * 通过 videoId 检查 订阅状态
     * @param videoId String
     * @param lifecycleScope CoroutineScope
     * @param onResult Function1<[@kotlin.ParameterName] Boolean, Unit>
     */
    fun checkSubscribeByVideoId(
        videoId: String,
        lifecycleScope: CoroutineScope = commonScope,
        onResult: SubscribeState.() -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            FetchVideoInfoWork(videoId) {
                val channelId = it?.detailInfo?.channelId ?: ""
                if (!LoginWork().getLoginState().isLoggedId) {
                    withContext(Dispatchers.Main.immediate) {
                        onResult.invoke(SubscribeState.Failed(channelId))
                    }
                    return@FetchVideoInfoWork
                }
                val ownerProfileUrl = it?.ownerProfileUrl ?: ""
                checkSubscribeByOwnerProfileUrl(ownerProfileUrl, lifecycleScope, false, onResult)
            }.start()
        }
    }

    /**
     * 订阅
     * @param channelId String
     * @param lifecycleScope CoroutineScope
     * @param result SuspendFunction1<[@kotlin.ParameterName] Boolean, Unit>
     */
    fun subscribe(
        channelId: String,
        lifecycleScope: CoroutineScope = commonScope,
        result: suspend SubscribeState.() -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val response = getSubscribeRequest(channelId).execute()
                val body = response.body()?.string() ?: ""
                val isSubscribed = JsonCaller.create(body)
                    .get("responseContext")
                    .get("serviceTrackingParams")
                    .filter {
                        this["service"].asString == "GFEEDBACK"
                    }[0]
                    .get("params")
                    .filter {
                        this["key"].asString == "logged_in"
                    }[0]
                    .get("value").asString == "1"
                withContext(Dispatchers.Main.immediate) {
                    if (isSubscribed) {
                        ToastHelper.showToast(R.string.follow_success)
                    } else if (AccountRepo.isLogin()) {
                        ToastHelper.showToast(R.string.follow_failed)
                    }
                    result.invoke(
                        SubscribeState.Success(
                            channelId = channelId,
                            isFollowing = isSubscribed
                        )
                    )
                }
            }.onFailure {
                withContext(Dispatchers.Main.immediate) {
                    if (AccountRepo.isLogin()) {
                        ToastHelper.showToast(R.string.follow_failed)
                    }
                    result.invoke(
                        SubscribeState.Failed(channelId)
                    )
                }
            }
        }
    }

    /**
     * 获取 订阅 请求
     * @param channelId String
     * @return Call
     */
    private suspend fun getSubscribeRequest(channelId: String): Call {
        val requestBody = RequestBody.create(
            MediaType.get("application/json; charset=utf-8"),
            JsonCaller.createObject().apply {
                this["context"] = accountRepo.getCommonParams()
                this["channelIds"] = JsonCaller.createArray().apply {
                    add(channelId)
                }
                this["params"] = getSubscribeKey()
            }.toString()
        )
        val requestPostJson: Request = Request.Builder()
            .headers(getCommonHeaders().add(accountRepo.getLoginHeaders()))
            .url(
                BaseApplication.getContext().getString(
                    R.string.youtube_subscribe_url,
                    commonRepo.apiKey
                )
            )
            .post(requestBody)
            .build()
        return commonHttpClient.newCall(requestPostJson)
    }


    /**
     * 取消订阅
     * @param channelId String
     * @param lifecycleScope CoroutineScope
     * @param result SuspendFunction1<[@kotlin.ParameterName] Boolean, Unit>
     */
    fun unsubscribe(
        channelId: String,
        lifecycleScope: CoroutineScope = commonScope,
        result: suspend SubscribeState.() -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val response = getUnsubscribeRequest(channelId).execute()
                val body = response.body()?.string() ?: ""
                val isUnsubscribed = JsonCaller.create(body)
                    .get("responseContext")
                    .get("serviceTrackingParams")
                    .filter {
                        this["service"].asString == "GFEEDBACK"
                    }[0]
                    .get("params")
                    .filter {
                        this["key"].asString == "logged_in"
                    }[0]
                    .get("value").asString == "1"
                withContext(Dispatchers.Main.immediate) {
                    if (isUnsubscribed) {
                        ToastHelper.showToast(R.string.unfollow_success)
                    } else if (AccountRepo.isLogin()) {
                        ToastHelper.showToast(R.string.unfollow_failed)
                    }
                    result.invoke(
                        SubscribeState.Success(
                            channelId = channelId,
                            isFollowing = !isUnsubscribed
                        )
                    )
                }
            }.onFailure {
                withContext(Dispatchers.Main.immediate) {
                    if (AccountRepo.isLogin()) {
                        ToastHelper.showToast(R.string.unfollow_failed)
                    }
                    result.invoke(
                        SubscribeState.Failed(channelId)
                    )
                }
            }
        }
    }

    /**
     * 获取 取消订阅 请求
     * @param channelId String
     * @return Call
     */
    private suspend fun getUnsubscribeRequest(channelId: String): Call {
        val requestBody = RequestBody.create(
            MediaType.get("application/json; charset=utf-8"),
            JsonCaller.createObject().apply {
                this["context"] = accountRepo.getCommonParams()
                this["channelIds"] = JsonCaller.createArray().apply {
                    add(channelId)
                }
                this["params"] = getUnsubscribeKey()
            }.toString()
        )
        val requestPostJson: Request = Request.Builder()
            .headers(getCommonHeaders().add(accountRepo.getLoginHeaders()))
            .url(
                BaseApplication.getContext().getString(
                    R.string.youtube_unsubscribe_url,
                    commonRepo.apiKey
                )
            )
            .post(requestBody)
            .build()
        return commonHttpClient.newCall(requestPostJson)
    }

    /**
     * 获取我的订阅列表
     * @param lifecycleScope CoroutineScope
     * @param onResult [@kotlin.ExtensionFunctionType] SuspendFunction1<SubscribeInfo, Unit>
     */
    fun getMySubscribedList(
        lifecycleScope: CoroutineScope = commonScope,
        onResult: suspend SubscribeInfo.() -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                val ytInitialData = commonWork.getYtInitialData(
                    BaseApplication.getContext().getString(R.string.youtube_my_subscribed_list)
                )
                val subscribeInfoItemList = mutableListOf<SubscribeInfoItem>().apply {
                    addAll(parseMySubscribeList(ytInitialData))
                }
                withContext(Dispatchers.Main.immediate) {
                    onResult.invoke(SubscribeInfo.Success(subscribedList = subscribeInfoItemList))
                }
                return@launch
            }
            withContext(Dispatchers.Main.immediate) { onResult.invoke(SubscribeInfo.Failed()) }
        }
    }

    /**
     * 解析我的订阅列表数据（在双列展示的情况下）
     * contents.twoColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer.contents[0].itemSectionRenderer.contents[0].shelfRenderer.content.expandedShelfContentsRenderer.items
     *
     * @param ytInitialData JsonCaller?
     * @return JsonCaller?
     */
    private fun parseMySubscribeList(ytInitialData: JsonCaller?): List<SubscribeInfoItem> {
        val result = mutableListOf<SubscribeInfoItem>()
        ytInitialData.get("contents").get("twoColumnBrowseResultsRenderer").get("tabs")[0]["tabRenderer"]
            .get("content")["sectionListRenderer"]["contents"][0]["itemSectionRenderer"]["contents"][0]
            .get("shelfRenderer")["content"]["expandedShelfContentsRenderer"]["items"]
            .map { this["channelRenderer"] }
            .forEach {
                val ownerProfileUrl = this["navigationEndpoint"]["browseEndpoint"]["canonicalBaseUrl"].asString?.let {
                    YOUTUBE_HOME_PAGE_URL + it
                } ?: ""
                val title = this["title"]["simpleText"].asString ?: ""
                val avatarUrl = this["thumbnail"]["thumbnails"][0]["url"].asString?.let { "https:${it}" } ?: ""
                val channelId = this["channelId"].asString ?: ""
                val followerNumStr = this["subscriberCountText"]["simpleText"].asString ?: ""
                result.add(
                    SubscribeInfoItem(
                        ownerProfileUrl = ownerProfileUrl,
                        title = title,
                        avatarUrl = avatarUrl,
                        channelId = channelId,
                        followerNumStr = followerNumStr,
                        isSubscribed = true
                    )
                )
            }
        return result
    }

    /**
     * 通过 channelId 改变订阅状态
     * @param channelsId String
     * @param targetSubscribeState Boolean                                      目标订阅状态
     * @param lifecycleScope CoroutineScope
     * @param result SuspendFunction1<[@kotlin.ParameterName] Boolean, Unit>
     */
    fun changeSubscribeStateByChannelId(
        channelsId: String,
        targetSubscribeState: Boolean,
        lifecycleScope: CoroutineScope = commonScope,
        result: suspend SubscribeState.() -> Unit
    ) {
        if (targetSubscribeState) {
            subscribe(channelsId, lifecycleScope, result)
        } else {
            unsubscribe(channelsId, lifecycleScope, result)
        }
    }

    /**
     * 通过 videoId 改变订阅状态
     * @param videoId String
     * @param targetSubscribeState Boolean
     * @param lifecycleScope CoroutineScope
     * @param onResult SuspendFunction1<[@kotlin.ParameterName] Boolean, Unit>
     */
    fun changeSubscribeStateByVideoId(
        videoId: String,
        targetSubscribeState: Boolean,
        lifecycleScope: CoroutineScope = commonScope,
        onResult: suspend SubscribeState.() -> Unit
    ) {
        FetchVideoInfoWork(videoId) {
            val channelId = it?.detailInfo?.channelId ?: ""
            if (!LoginWork().getLoginState().isLoggedId) {
                withContext(Dispatchers.Main.immediate) {
                    onResult.invoke(SubscribeState.Failed(channelId))
                }
                return@FetchVideoInfoWork
            }
            changeSubscribeStateByChannelId(channelId, targetSubscribeState, lifecycleScope, onResult)
        }.start()
    }

}