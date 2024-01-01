package com.mars.united.international.webplayer.account.login.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.dubox.drive.kernel.util.INT_100
import com.moder.compass.ui.widget.dialog.Loading
import com.mars.united.international.webplayer.R
import com.mars.united.international.webplayer.account.jsbridge.LoginJsBridge
import com.mars.united.international.webplayer.account.jsbridge.LogoutJsBridge
import com.mars.united.international.webplayer.account.login.LoginConfiguration
import com.mars.united.international.webplayer.account.login.LoginWork
import com.mars.united.international.webplayer.account.login.state.CALLBACK_ID
import com.mars.united.international.webplayer.account.login.state.PAGE_TARGET
import com.mars.united.international.webplayer.account.login.state.PageTarget
import com.mars.united.international.webplayer.account.repo.AccountRepo
import com.mars.united.international.webplayer.databinding.FragmentYoutubeLoginBinding
import com.mars.united.international.webplayer.parser.utils.readTextByRawId

private const val JS_BRIDGE_NAME: String = "NativeBridge"
private val youtubeUrlRegex: Regex = """^http(s?)://(www|m)\.youtube\.com(/?|/\?.*)$""".toRegex()


/**
 * @Author 陈剑锋
 * @Date 2023/8/24-10:48
 * @Desc Youtube登录页
 */
class YoutubeLoginFragment : Fragment(), IBackPress {

    companion object {

        private const val TAG: String = "YoutubeLoginFragment"

    }

    private var binding: FragmentYoutubeLoginBinding? = null

    private val pageTarget: PageTarget by lazy {
        (arguments?.getSerializable(PAGE_TARGET) as? PageTarget) ?: PageTarget.ToLogin
    }

    private val callbackId: Long by lazy {
        arguments?.getLong(CALLBACK_ID, 0L) ?: 0L
    }

    private val targetUrl: String by lazy {
        when (pageTarget) {
            is PageTarget.ToLogin -> getString(R.string.youtube_login_url)
            is PageTarget.ToLogout -> getString(R.string.youtube_logout_url)
        }
    }

    private val injectJsRawResId: Int by lazy {
        when (pageTarget) {
            is PageTarget.ToLogin -> R.raw.after_youtube_login_js
            is PageTarget.ToLogout -> R.raw.after_youtube_logout_js
        }
    }

    private var isJsInjected: Boolean = false

    private val accountRepo: AccountRepo by lazy {
        AccountRepo.get()
    }

    private val jsBridge: Any by lazy {
        when (pageTarget) {
            is PageTarget.ToLogin -> object : LoginJsBridge {
                @JavascriptInterface
                override fun onLoginSucceed(ytConfig: String, url: String) {
                    val cookies = CookieManager.getInstance().getCookie(url)
                    accountRepo.update(ytConfig, cookies)
                    LoginWork().checkLogin()
                    Log.d(
                        TAG, "onLoginSucceed: \n" +
                                "ytConfig = ${ytConfig}\n" +
                                "cookies = ${cookies}"
                    )
                    callback(true)
                    releaseWebView()
                    activity?.finish()
                }

                @JavascriptInterface
                override fun onLoginFailed() {
                    Log.d(TAG, "onLoginFailed: ")
                    callback(false)
                    releaseWebView()
                    activity?.finish()
                }
            }
            is PageTarget.ToLogout -> object : LogoutJsBridge {
                @JavascriptInterface
                override fun onLogoutSucceed() {
                    Log.d(TAG, "onLogoutSucceed: ")
                    releaseWebView()
                    activity?.finish()
                }

                @JavascriptInterface
                override fun onLogoutFailed() {
                    Log.d(TAG, "onLogoutFailed: ")
                    releaseWebView()
                    activity?.finish()
                }
            }
        }
    }

    private val loading: Loading? by lazy {
        Loading(
            ctx = context ?: return@lazy null,
            showCancel = false
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentYoutubeLoginBinding.inflate(
            LayoutInflater.from(container?.context ?: return null),
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView() {
        binding?.webView?.apply {
            settings.apply {
                javaScriptEnabled = true
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_DEFAULT
                domStorageEnabled = true
            }

            addJavascriptInterface(jsBridge, JS_BRIDGE_NAME)

            webChromeClient = object : WebChromeClient() {
                private var lastProgress: Int = 0
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)

                    // 加载进度回调 去重
                    if (lastProgress == newProgress) return
                    lastProgress = newProgress
                    if (url?.matches(youtubeUrlRegex) == true) {
                        if (pageTarget is PageTarget.ToLogin) {
                            binding?.webView?.visibility = View.INVISIBLE
                            showLoading()
                        }
                        // 登录/登出成功，已经跳转到Youtube页面
                        if (newProgress == INT_100) {
                            // 完全加载完成
                            injectJs()
                        }
                    }

                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    // 防止WebView打开新页面的时候，跳转到浏览器去了
                    view?.loadUrl(url ?: return true)
                    return true
                }
            }

            loadUrl(targetUrl)
        }
    }

    /**
     * 注入js
     */
    private fun injectJs() {
        if (isJsInjected) return
        isJsInjected = true
        executeJsOnWebView(injectJsRawResId)
    }


    /**
     * 在WebView执行js
     * @param jsFileRawId Int                       js文件的raw资源id
     * @param onResult Function1<String, Unit>      回调
     */
    private fun executeJsOnWebView(jsFileRawId: Int, onResult: ((String) -> Unit)? = null) {
        executeJsOnWebView(
            // 从raw资源读取js命令
            readTextByRawId(activity ?: return, jsFileRawId),
            onResult
        )
    }

    /**
     * 在WebView执行js
     * @param command String                        命令
     * @param onResult Function1<String, Unit>      回调
     */
    private fun executeJsOnWebView(command: String, onResult: ((String) -> Unit)? = null) {
        binding?.webView?.evaluateJavascript(command) {
            onResult?.invoke(it)
        }
    }

    private fun showLoading() {
        if (loading?.isShowing != true) {
            loading?.show()
        }
    }

    private fun hideLoading() {
        if (loading?.isShowing != false) {
            loading?.hide()
        }
    }

    private fun callback(loggedIn: Boolean) {
        Handler(Looper.getMainLooper()).post {
            LoginConfiguration.getCallback(callbackId)?.invoke(loggedIn)
            LoginConfiguration.removeCallback(callbackId)
        }
    }

    private var isWebViewReleased: Boolean = false

    private fun releaseWebView() {
        Handler(Looper.getMainLooper()).post {
            if (isWebViewReleased) return@post
            isWebViewReleased = true
            binding?.webView?.destroy()
        }
    }

    override fun onBackPressed(): Boolean {
        if (binding?.webView?.canGoBack() == true) {
            binding?.webView?.goBack()
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        callback(false)
        releaseWebView()
    }
}