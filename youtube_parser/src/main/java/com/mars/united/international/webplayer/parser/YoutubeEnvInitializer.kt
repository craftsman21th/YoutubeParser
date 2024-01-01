package com.mars.united.international.webplayer.parser

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.http.SslError
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.webkit.*
import com.moder.compass.business.kernel.HostURLManager
import com.moder.compass.business.kernel.PRO_STR_HTTPS
import com.dubox.drive.kernel.util.INT_100
import com.dubox.drive.kernel.util.LONG_0
import com.mars.united.international.webplayer.R
import com.mars.united.international.webplayer.common.commonHttpClient
import com.mars.united.international.webplayer.parser.repo.ParserRepo
import com.mars.united.international.webplayer.parser.utils.checkKeyParamsValid
import com.mars.united.international.webplayer.parser.utils.readTextByRawId
import com.mars.united.international.webplayer.parser.work.manager.YoutubeWorkManager
import com.mars.united.international.webplayer.parser.work.manager.YoutubeWorkManager.mainThreadHandler
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

internal const val TAG: String = "YoutubeEnvInitializer"

/**
 * @Author 陈剑锋
 * @Date 2023/7/12-15:21
 * @Desc
 */
@SuppressLint("StaticFieldLeak")
object YoutubeEnvInitializer {

    private const val ENV_URL: String = "https://m.youtube.com"
    private val LOAD_PARSER_FUNCTION_REMOTE_FILE_URL: String =
        "$PRO_STR_HTTPS${HostURLManager.getDataDomain()}/issue/pavobox/CoCoBox/Youtube/Crack/InitialEnvInjectJs/v1.js"

    // 准备环境的 重试次数
    private const val MAX_ENV_RETRY_TIMES: Int = 3

    // 下一次 准备环境的重试 延迟
    private const val ENV_RETRY_DELAY: Long = 3000

    private lateinit var context: Application
    private var webView: WebView? = null

    // 准备环境 已经重试的次数
    private var envRetryTimes: Int = 0

    // 页面是否正在加载中
    private var isLoading: Boolean = false

    // 环境是否已经准备好了，即可以开始解析视屏信息
    private var isEnvPrepared: Boolean = false

    private var connectivityManagerCache: ConnectivityManager? = null
    private val connectivityManager: ConnectivityManager?
        get() {
            return connectivityManagerCache ?: (context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as? ConnectivityManager)?.also {
                connectivityManagerCache = it
            }
        }

    // 网络连接状态
    private var isNetworkConnected: Boolean? = null

    /**
     * 初始化
     * @param application Application
     */
    fun init(application: Application) {
        if (this::context.isInitialized) {
            Log.e(TAG, "Already initialized!")
            return
        }
        context = application
        startNetworkListener()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13及以上版本需要主动初始化
            isNetworkConnected = checkNetworkConnection()
            checkEnv()
        }
    }

    /**
     * 准备解析环境
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun prepareEnv(delay: Long = LONG_0) {
        if (isLoading) return
        if (hasEnvCache().also { isEnvPrepared = it }) {
            YoutubeWorkManager.onEnvInitSuccess()
            // if (BuildConfig.DEBUG) {
            //     DuboxLog.d(TAG, "检查缓存: 有缓存")
            //     DuboxLog.d(TAG, "检查缓存: keyInfo = ${keyInfoRepo.ytConfig}")
            // }
            return
        }
        isLoading = true
        Log.e(TAG, "检查缓存: 无缓存")
        if (webView != null) {
            webView?.reload()
            return
        }
        mainThreadHandler.postDelayed({
            webView = WebView(context)
            webView?.apply {
                settings.apply {
                    javaScriptEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    domStorageEnabled = true
                }

                addJavascriptInterface(
                    JsBridge(
                        onGetKeyInfo = block@{
                            if (isEnvPrepared) {
                                return@block
                            }
                            if (it.checkKeyParamsValid()) {
                                Log.e(TAG, "onGetKeyInfo: ${it}")
                                isEnvPrepared = true
                                releaseEnv()
                                ParserRepo.get().update(it)
                                YoutubeWorkManager.onEnvInitSuccess()
                            } else {
                                checkEnvRetry()
                            }
                        }
                    ),
                    "NativeBridge"
                )

                webChromeClient = object : WebChromeClient() {

                    private var lastProgress: Int = 0

                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        // 去重
                        if (lastProgress == newProgress) return
                        Log.e(TAG, "onProgressChanged: ${newProgress}")
                        if (newProgress == INT_100) {
                            loadParserFunction()
                        }
                        lastProgress = newProgress
                    }

                }

                webViewClient = object : WebViewClient() {

                    @SuppressLint("WebViewClientOnReceivedSslError")
                    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                        super.onReceivedSslError(view, handler, error)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isLoading = false
                    }

                }

                loadUrl(ENV_URL)
            }
        }, delay)
    }

    /**
     * 加载 解析函数 到WebView
     */
    private fun loadParserFunction() {
        if (isEnvPrepared) {
            return
        }
        val checkLoadResult: () -> Unit = {
            executeJsOnWebView("window.isLoadSuccess()") checkLoad@{
                if (isEnvPrepared) {
                    return@checkLoad
                }
                if (it == "true") {
                    // 环境初始化成功
                    executeJsOnWebView("window.getKeyInfo()")
                } else {
                    checkEnvRetry()
                }
            }
        }
        commonHttpClient.newCall(
            Request.Builder()
                .url(LOAD_PARSER_FUNCTION_REMOTE_FILE_URL)
                .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainThreadHandler.post {
                    executeJsOnWebView(R.raw.load_parser_function)
                    checkLoadResult.invoke()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string() ?: ""
                mainThreadHandler.post {
                    if (body.isNotBlank()) {
                        executeJsOnWebView(body)
                    } else {
                        executeJsOnWebView(R.raw.load_parser_function)
                    }
                    checkLoadResult.invoke()
                }
            }
        })
    }

    /**
     * 在WebView执行js
     * @param jsFileRawId Int                       js文件的raw资源id
     * @param onResult Function1<String, Unit>      回调
     */
    private fun executeJsOnWebView(jsFileRawId: Int, onResult: ((String) -> Unit)? = null) {
        executeJsOnWebView(
            // 从raw资源读取js命令
            readTextByRawId(context, jsFileRawId),
            onResult
        )
    }

    /**
     * 在WebView执行js
     * @param command String                        命令
     * @param onResult Function1<String, Unit>      回调
     */
    private fun executeJsOnWebView(command: String, onResult: ((String) -> Unit)? = null) {
        webView?.evaluateJavascript(command) {
            onResult?.invoke(it)
        }
    }

    /**
     * 停止准备环境
     */
    private fun stopPrepareEnv() {
        mainThreadHandler.postDelayed({
            if (checkEnv(false)) {
                // 如果环境准备好了，不做任何处理
                return@postDelayed
            }
            webView?.stopLoading()
        }, 0L)
    }

    /**
     * 清除环境
     */
    private fun releaseEnv() {
        webView?.stopLoading()
        webView?.removeAllViews()
        webView = null
    }

    /**
     * 开始网络监听
     */
    private fun startNetworkListener() {
        context.registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    val isNetworkConnectedNew = checkNetworkConnection()
                    // 防止重复回调
                    if (isNetworkConnectedNew == isNetworkConnected) return
                    isNetworkConnected = isNetworkConnectedNew
                    if (isNetworkConnectedNew) {
                        // 网络连接成功
                        checkEnv()
                    } else {
                        // 网络连接断开
                        stopPrepareEnv()
                    }
                }
            },
            IntentFilter().apply {
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            }
        )
    }

    /**
     * 检查一次网络连接
     * @return Boolean
     */
    private fun checkNetworkConnection(): Boolean {
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    /**
     * 检查环境是否准备好了（如果没有初始化会重新初始化一次）
     * @return Boolean
     */
    fun checkEnv(autoReload: Boolean = true, delay: Long = 0L): Boolean {
        return isEnvPrepared.also {
            if (!hasEnvCache() && autoReload && !isLoading && !isEnvPrepared && isNetworkConnected == true) {
                prepareEnv(delay)
            }
        }
    }

    /**
     * 检查是否还有 重试准备环境 的机会，如果有，则再次重试
     * @return Boolean
     */
    fun checkEnvRetry(): Boolean {
        return (envRetryTimes < MAX_ENV_RETRY_TIMES).also { haveRetryChance ->
            if (haveRetryChance) {
                clearEnvCache()
                checkEnv(delay = ENV_RETRY_DELAY)
                envRetryTimes++
            } else {
                YoutubeWorkManager.onEnvInitFailed()
                envRetryTimes = 0
            }
        }
    }

    /**
     * 判断 是否有 WebView缓存的关键请求参数
     *      如果有的话，先用缓存；记录标志位，如果通过缓存拉取Youtube视频信息失败，再重新初始化环境
     * @return Boolean
     */
    private fun hasEnvCache(): Boolean {
        return (ParserRepo.get().check()).also {
            if (it) {
                isEnvPrepared = true
            }
        }
    }

    /**
     * 清除 WebView缓存的关键请求参数
     */
    private fun clearEnvCache() {
        ParserRepo.get().clearDecryptMethod()
        isEnvPrepared = false
    }

}