package com.mars.united.international.webplayer.account.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dubox.drive.kernel.util.TIME_UNIT_1000
import com.moder.compass.network.request.StorageValue
import com.mars.united.international.webplayer.account.login.state.LoginState
import com.mars.united.international.webplayer.account.model.Cookie
import com.mars.united.international.webplayer.common.COOKIE_NAME_SAPISID
import com.mars.united.international.webplayer.common.WEB_PAGE_HEADER_AUTHORIZATION
import com.mars.united.international.webplayer.common.WEB_PAGE_HEADER_COOKIE
import com.mars.united.international.webplayer.common.YOUTUBE_HOME_PAGE_URL
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.utils.asBoolean
import com.mars.united.international.webplayer.parser.utils.asInt
import com.mars.united.international.webplayer.parser.utils.asJsonObject
import com.mars.united.international.webplayer.parser.utils.asString
import com.mars.united.international.webplayer.parser.utils.get
import com.mars.united.international.webplayer.parser.utils.set
import com.mars.united.international.webplayer.parser.utils.sha1
import okhttp3.Headers
import java.lang.ref.WeakReference

/**
 * 存放 Youtube账户信息 的 仓库
 */
class AccountRepo {

    companion object {

        private const val TAG: String = "AccountRepo"

        private var instanceRef: WeakReference<AccountRepo> = WeakReference(null)

        /**
         * 获取实例
         * @return AccountRepo
         */
        fun get(): AccountRepo {
            return instanceRef.get() ?: AccountRepo().also {
                instanceRef = WeakReference(it)
            }
        }

        private val _loginState: MutableLiveData<LoginState> = MutableLiveData()
        val loginState: LiveData<LoginState> get() = _loginState

        /**
         * 更新绑定到Youtube账户的状态
         * @param status Boolean
         */
        fun updateLoginState(status: LoginState) {
            if (_loginState.value == status) return
            _loginState.postValue(status)
        }

        /**
         * 检查是否已经登录
         * @return Boolean
         */
        fun isLogin(): Boolean {
            val instance = get()
            return loginState.value?.isLoggedId == true && !instance.getCookie(COOKIE_NAME_SAPISID).isNullOrBlank()
        }

    }

    /**
     * 获取实例
     */
    private val commonRepo by lazy {
        CommonRepo.get()
    }

    private var cookies: String by StorageValue(
        key = "${TAG}_cookies",
        defaultValue = "",
        logSetValueTime = true,
        isSameDayValue = false
    )

    /**
     * 获取原始 cookie
     * @return String
     */
    fun getRawCookies(): String {
        return cookies
    }

    /**
     * 获取cookie列表
     * @return List<Cookie>
     */
    fun getCookies(): List<Cookie> {
        return kotlin.runCatching {
            getRawCookies().split(";")
                .map {
                    if (it.contains("=")) {
                        val equalIndex = it.indexOf("=")
                        Cookie(
                            name = it.substring(0, equalIndex).trim(),
                            value = if (it.length > equalIndex) {
                                it.substring(equalIndex + 1).trim()
                            } else {
                                ""
                            }
                        )
                    } else {
                        null
                    }
                }
                .filterNotNull()
        }.getOrNull() ?: emptyList()
    }

    /**
     * 获取指定名称的cookie
     * @param name String
     * @return String?
     */
    fun getCookie(name: String): String? {
        return getCookies().filter {
            it.name == name
        }.getOrNull(0)?.value
    }

    /**
     * 检查Cookie是否有效
     * @return Boolean
     */
    fun checkCookiesValid(): Boolean {
        return getCookie(COOKIE_NAME_SAPISID) != null
    }


    /**
     * 更新数据
     * @param ytConfig String
     * @param rawCookies String
     */
    fun update(ytConfig: String, rawCookies: String) {
        // 更新 ytConfig
        commonRepo.updateYtConfig(ytConfig)

        // 更新Cookie
        cookies = rawCookies
    }

    /**
     * 获取 账户鉴权
     * @return String
     */
    fun getAuthorization(): String {
        val origin = YOUTUBE_HOME_PAGE_URL
        val time = System.currentTimeMillis() / TIME_UNIT_1000
        val sApiSid = getCookie(COOKIE_NAME_SAPISID) ?: ""
        val sha1 = "$time $sApiSid $origin".sha1()
        return "SAPISIDHASH ${time}_${sha1}"
    }

    /**
     * 获取 账户相关接口 公共参数
     * @return JsonCaller?
     */
    fun getCommonParams(videoId: String = "undefined"): JsonCaller {
        val ytbContext = commonRepo.ytConfig["INNERTUBE_CONTEXT"]
        val params = JsonCaller.createObject()
        params["client"] = JsonCaller.createObject().apply {
            loadClient1(videoId)
            loadClient2()
        }
        params["user"] = JsonCaller.createObject().apply {
            this["lockedSafetyMode"] = ytbContext["user"]["lockedSafetyMode"].asBoolean ?: false
        }
        params["request"] = JsonCaller.createObject().apply {
            this["useSsl"] = ytbContext["request"]["useSsl"].asBoolean ?: true
            this["internalExperimentFlags"] = JsonCaller.createArray()
            this["consistencyTokenJars"] = JsonCaller.createArray()
        }
        return params
    }

    private fun JsonCaller.loadClient1(videoId: String) {
        val ytConfig = commonRepo.ytConfig
        val client = ytConfig["INNERTUBE_CONTEXT"]["client"]
        this["acceptHeader"] = client["acceptHeader"].asString ?: ""
        this["browserName"] = client["browserName"].asString ?: ""
        this["browserVersion"] = client["browserVersion"].asString ?: ""
        this["clientFormFactor"] = client["clientFormFactor"].asString ?: "SMALL_FORM_FACTOR"
        this["clientName"] = ytConfig["INNERTUBE_CLIENT_NAME"].asString ?: "MWEB"
        this["clientVersion"] = ytConfig["INNERTUBE_CLIENT_VERSION"].asString ?: "2.20230802.00.00"
        this["configInfo"] = client["configInfo"].asJsonObject ?: JsonCaller.createObject()
        this["deviceExperimentId"] = client["deviceExperimentId"].asString ?: ""
        this["deviceMake"] = client["deviceMake"].asString ?: "Samsung"
        this["deviceModel"] = client["deviceModel"].asString ?: "SM-G955U"
        this["gl"] = ytConfig["GL"].asString ?: "US"
        this["hl"] = ytConfig["HL"].asString ?: "en"
        this["mainAppWebInfo"] = JsonCaller.createObject().apply {
            this["graftUrl"] = "https://m.youtube.com/watch?v=${videoId}"
            this["webDisplayMode"] = "WEB_DISPLAY_MODE_BROWSER"
            this["isWebNativeShareAvailable"] = false
        }
    }

    private fun JsonCaller.loadClient2() {
        val ytConfig = commonRepo.ytConfig
        val client = ytConfig["INNERTUBE_CONTEXT"]["client"]
        this["originalUrl"] = "https://m.youtube.com/"
        this["osName"] = client["osName"].asString ?: "Android"
        this["osVersion"] = client["osVersion"].asString ?: "8.0.0"
        this["platform"] = client["platform"].asString ?: ""
        this["playerType"] = client["playerType"].asString ?: ""
        this["screenDensityFloat"] = client["screenDensityFloat"].asInt ?: 2
        this["screenPixelDensity"] = client["screenPixelDensity"].asInt ?: 2
        this["timeZone"] = client["timeZone"].asString ?: ""
        this["userAgent"] = client["userAgent"].asString ?: ""
        this["visitorData"] = ytConfig["VISITOR_DATA"].asString ?: ""
    }

    /**
     * 获取登录Header
     * @return Headers
     */
    fun getLoginHeaders(): Headers {
        return Headers.Builder().apply {
            add(WEB_PAGE_HEADER_AUTHORIZATION, getAuthorization())
            add(WEB_PAGE_HEADER_COOKIE, getRawCookies())
        }.build()
    }

    /**
     * 清除订阅参数
     */
    fun clearSubscribeParams() {
        commonRepo.subscribeKey = ""
        commonRepo.unsubscribeKey = ""
    }

    /**
     * 清除数据
     */
    fun clear() {
        clearSubscribeParams()
        cookies = ""
    }


}