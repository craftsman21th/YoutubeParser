package com.mars.united.international.webplayer.account.login

import android.content.Intent
import android.util.Log
import android.webkit.CookieManager
import androidx.appcompat.app.AppCompatActivity
import com.moder.compass.BaseApplication
import com.mars.united.international.webplayer.R
import com.mars.united.international.webplayer.account.login.page.YoutubeLoginActivity
import com.mars.united.international.webplayer.account.login.state.CALLBACK_ID
import com.mars.united.international.webplayer.account.login.state.LoginState
import com.mars.united.international.webplayer.account.login.state.PAGE_TARGET
import com.mars.united.international.webplayer.account.login.state.PageTarget
import com.mars.united.international.webplayer.account.repo.AccountRepo
import com.mars.united.international.webplayer.common.add
import com.mars.united.international.webplayer.common.commonHttpClient
import com.mars.united.international.webplayer.common.commonScope
import com.mars.united.international.webplayer.common.getCommonHeaders
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.utils.asString
import com.mars.united.international.webplayer.parser.utils.filter
import com.mars.united.international.webplayer.parser.utils.get
import com.mars.united.international.webplayer.parser.utils.set
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
 * @Date 2023/8/29-14:43
 * @Desc 登录相关操作
 */
class LoginWork {

    companion object {
        private const val TAG: String = "LoginWork"
    }

    private val commonRepo: CommonRepo by lazy {
        CommonRepo.get()
    }

    private val accountRepo: AccountRepo by lazy {
        AccountRepo.get()
    }

    /**
     * 登录
     * @param activity AppCompatActivity
     * @param onResult Function1<Boolean, Unit>
     */
    fun login(activity: AppCompatActivity, onResult: ResultCallback) {
        val callbackId = LoginConfiguration.addCallback(onResult)
        activity.startActivity(Intent(activity, YoutubeLoginActivity::class.java).apply {
            putExtra(PAGE_TARGET, PageTarget.ToLogin)
            putExtra(CALLBACK_ID, callbackId)
        })
    }

    /**
     * 登出
     * @param onResult Function1<Boolean, Unit>
     */
    fun logout(onResult: ResultCallback) {
        CookieManager.getInstance().removeAllCookies {
            Log.d(TAG, "removeAllCookies: ${it}")
            if (it) {
                AccountRepo.get().clear()
            }
            LoginWork().checkLogin { state ->
                onResult.invoke(!state.isLoggedId)
            }
        }
    }

    /**
     * 检查是否已经登录
     * @param lifecycleScope CoroutineScope
     * @param callback Function1<LoginState, Unit>?
     */
    fun checkLogin(
        lifecycleScope: CoroutineScope = commonScope,
        callback: (suspend (LoginState) -> Unit)? = null
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = getLoginState()
            callback?.apply {
                launch(Dispatchers.Main.immediate) {
                    invoke(result)
                }
            }
        }
    }

    /**
     * 检查是否已经登录
     * @return CheckLoginResult
     */
    suspend fun getLoginState(): LoginState = withContext(Dispatchers.IO) {
        if (!accountRepo.checkCookiesValid()) {
            AccountRepo.updateLoginState(LoginState.NotLoggedIn)
            return@withContext LoginState.NotLoggedIn
        }
        try {
            val response = getCheckLoginRequest().execute()
            val body = response.body()?.string() ?: ""
            val isLoggedIn = JsonCaller.create(body)
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
            if (isLoggedIn) {
                AccountRepo.updateLoginState(LoginState.LoggedIn)
                return@withContext LoginState.LoggedIn
            } else {
                AccountRepo.updateLoginState(LoginState.Expired)
                return@withContext LoginState.Expired
            }
        } catch (e: Throwable) {
            Log.e(TAG, "checkLoginErr: $e")
            AccountRepo.updateLoginState(LoginState.UNKNOWN)
            return@withContext LoginState.UNKNOWN
        }
    }

    /**
     * 获取登录请求的Call对象。
     *
     * @return 返回一个Call类型的对象。
     */
    private fun getCheckLoginRequest(): Call {
        val requestBody = RequestBody.create(
            MediaType.get("application/json; charset=utf-8"),
            JsonCaller.createObject().apply {
                this["context"] = accountRepo.getCommonParams()
                this["fetchLiveState"] = true
            }.toString()
        )
        val requestPostJson: Request = Request.Builder()
            .headers(getCommonHeaders().add(accountRepo.getLoginHeaders()))
            .url(
                BaseApplication.getContext().getString(
                    R.string.youtube_get_avatar_url,
                    commonRepo.apiKey
                )
            )
            .post(requestBody)
            .build()
        return commonHttpClient.newCall(requestPostJson)
    }

}