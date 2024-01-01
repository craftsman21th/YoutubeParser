package com.mars.united.international.webplayer.parser.work.base

import android.util.Log
import com.dubox.drive.kernel.util.INT_0
import com.dubox.drive.kernel.util.INT_10
import com.dubox.drive.kernel.util.INT_3
import com.dubox.drive.kernel.util.INT_30
import com.dubox.drive.kernel.util.INT_6
import com.dubox.drive.kernel.util.TIME_UNIT_1000
import com.mars.united.international.webplayer.account.repo.AccountRepo
import com.mars.united.international.webplayer.common.add
import com.mars.united.international.webplayer.common.commonHttpClient
import com.mars.united.international.webplayer.common.getCommonHeaders
import com.mars.united.international.webplayer.parser.TAG
import com.mars.united.international.webplayer.parser.YoutubeEnvInitializer
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.work.manager.YoutubeWorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import kotlin.math.pow

/**
 * @Author 陈剑锋
 * @Date 2023/8/1-10:35
 * @Desc Youtube请求基础类
 */
abstract class IYoutubeWork {

    companion object {

        const val ERR_TYPE_INIT_ENV_FAILED: Int = -1    // 环境初始化失败
        const val ERR_TYPE_NO_RETRY_CHANCE: Int = -2    // 没有重试次数了
        const val ERR_TYPE_TIMEOUT: Int = -3            // 处理超时

    }

    /**
     * 任务创建时间
     */
    val createTime: Long by lazy { System.currentTimeMillis() }

    /**
     * 控制 最多请求次数 相关参数
     */
    protected open val maxRequestTimes: Int = Int.MAX_VALUE // 最多可以请求几次
    private var requestedTimes: Int = INT_0
    protected open val retryDelay: Long = INT_3 * TIME_UNIT_1000  // 重试延迟

    /**
     * 控制 请求超时 相关参数
     */
    private var startRequestTime: Long = Long.MAX_VALUE
    protected open val requestTimeout: Long = INT_30 * TIME_UNIT_1000 // 超时时长(ms)

    var isHandling: Boolean = false
        private set

    private var job: Job? = null

    private var isWorkInitSuccess: Boolean = false

    /**
     * 任务初始化
     * @return Boolean  初始化是否成功
     */
    protected open suspend fun onWorkInit(): Boolean {
        return true
    }

    /**
     * 是否需要登录信息 才能请求
     * @return Boolean      为true的时候会把登录信息加入请求Header
     */
    protected open suspend fun needLogin(): Boolean {
        return false
    }

    /**
     * 获取请求Url
     * @return String
     */
    protected abstract suspend fun getUrl(): String

    /**
     * 获取请求的body参数（为提高封装程度，这里目前默认body里面传的都是json）
     * @return JsonCaller
     */
    protected abstract suspend fun getRequestBody(): JsonCaller

    /**
     * 检查是否还有 重试拉取信息 的机会，如果有，则再次重试
     * @return Boolean
     */
    protected open suspend fun checkRetry(): Boolean {
        return (requestedTimes < maxRequestTimes &&
                System.nanoTime() < startRequestTime + requestTimeout * INT_10.toDouble().pow(INT_6)
                ).also { haveRetryChance ->
                if (haveRetryChance) {
                    withContext(Dispatchers.IO) {
                        delay(retryDelay)
                        handleRequest()
                    }
                }
            }
    }

    /**
     * 处理请求
     */
    private suspend fun handleRequest() = withContext(Dispatchers.IO) {
        if (!isHandling) {
            return@withContext
        }
        if (!isWorkInitSuccess) {
            // 如果任务没有初始化成功，则每次重复请求前都会重复初始化任务
            isWorkInitSuccess = onWorkInit()
        }
        if (onRequest().also { requestedTimes++ }) {
            Log.d(TAG, "request success.")
            YoutubeWorkManager.removeWaitingWork(this@IYoutubeWork)
            isHandling = false
            onCallback() // 请求成功 回调
        } else if (checkRetry()) {
            Log.e(TAG, "request failed, retry ...")
        } else {
            // 请求失败，且没有重试机会
            Log.e(TAG, "request failed.")
            onError(ERR_TYPE_NO_RETRY_CHANCE)
        }
    }

    /**
     * 发送请求并处理结果
     * @return Boolean      返回true表示请求成功，并且处理请求结果满足预期
     */
    protected open suspend fun onRequest(): Boolean = withContext(Dispatchers.IO) {
        val requestBody = RequestBody.create(
            MediaType.get("application/json; charset=utf-8"),
            getRequestBody().toString()
        )
        val requestPostJson: Request = Request.Builder()
            .apply {
                if (needLogin()) {
                    headers(getCommonHeaders().add(AccountRepo.get().getLoginHeaders()))
                } else {
                    headers(getCommonHeaders())
                }
            }
            .url(getUrl())
            .post(requestBody)
            .build()
        try {
            return@withContext onHandleResponse(commonHttpClient.newCall(requestPostJson).execute())
        } catch (e: Throwable) {
            return@withContext false
        }
    }

    /**
     * 处理请求结果（因为请求失败后可以配置重复请求，这里也可能会重复调用）
     * @return Boolean      返回true表示请求结果满足预期
     */
    protected abstract suspend fun onHandleResponse(response: Response): Boolean

    protected open fun onError(errType: Int) {
        YoutubeWorkManager.removeWaitingWork(this@IYoutubeWork)
        isHandling = false
        onCallback(errType)
    }

    protected abstract fun onCallback(errCode: Int? = null)

    /**
     * 开始任务
     * @return IYoutubeWork
     */
    fun start(): IYoutubeWork {
        if (isHandling) {
            return this
        }
        isHandling = true
        YoutubeWorkManager.mainThreadHandler.postDelayed({
            // 超时时间后，判断是否回调成功
            if (isHandling) {
                stop()
                onError(ERR_TYPE_TIMEOUT)
            }
        }, requestTimeout)
        startRequestTime = System.nanoTime()
        YoutubeWorkManager.mainThreadHandler.post {
            if (YoutubeEnvInitializer.checkEnv()) {
                job = CoroutineScope(Dispatchers.IO).launch {
                    handleRequest()
                }
            } else {
                YoutubeWorkManager.addWaitingWork(this)
            }
        }
        return this
    }

    /**
     * 当环境准备好
     */
    fun onEnvReady() {
        job = CoroutineScope(Dispatchers.IO).launch {
            handleRequest()
        }
    }

    fun onEnvInitFailed() {
        onError(ERR_TYPE_INIT_ENV_FAILED)
    }

    /**
     * 停止任务
     */
    open fun stop() {
        kotlin.runCatching {
            job?.cancel()
        }
    }

}