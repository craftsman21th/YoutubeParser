package com.mars.united.international.webplayer.common

import android.os.Build
import android.webkit.WebSettings
import com.moder.compass.BaseApplication
import com.moder.compass.network.request.simpleHttpClient
import com.mars.united.international.webplayer.BuildConfig
import com.mars.united.international.webplayer.R
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.Headers
import okhttp3.OkHttpClient
import java.util.Locale

/**
 * @Author 陈剑锋
 * @Date 2023/9/12-10:18
 * @Desc 公共配置
 */

const val WEB_PAGE_HEADER_ORIGIN: String = "origin"
const val WEB_PAGE_HEADER_AUTHORIZATION: String = "Authorization"
const val WEB_PAGE_HEADER_COOKIE: String = "cookie"
const val WEB_PAGE_HEADER_ACCEPT_LANGUAGE: String = "Accept-Language"
const val WEB_PAGE_HEADER_USER_AGENT: String = "User-Agent"

const val YOUTUBE_HOME_PAGE_URL: String = "https://m.youtube.com"

internal const val COOKIE_NAME_SAPISID: String = "SAPISID"

internal const val OWNER_PROFILE_URL_EXAMPLE: String = "https://m.youtube.com/@star-pw4vc"

internal val commonHttpClient: OkHttpClient = simpleHttpClient

internal val commonScope = CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { context, exception ->
    if (BuildConfig.DEBUG) {
        throw exception
    }
})

internal const val MIMETYPE_VIDEO_MP4: String = "video/mp4"
internal const val MIMETYPE_VIDEO_WEBM: String = "video/webm"
internal const val MIMETYPE_VIDEO_3GPP: String = "video/3gpp"

/**
 * 获取 请求Header里的 语言参数
 * @return String
 */
fun getLanguageHeaderValue(): String = Locale.getDefault().language + "-" + Locale.getDefault().country

/**
 * 获取 请求Header里的 User-Agent参数值
 * @return String
 */
fun getUserAgentValue(): String {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
        runCatching {
            System.getProperty("http.agent")
        }.getOrNull() ?: WebSettings.getDefaultUserAgent(BaseApplication.getContext())
    } else {
        WebSettings.getDefaultUserAgent(BaseApplication.getContext())
    }
}

/**
 * 将 channelId 组装为 用户首页地址
 * @param channelId String
 * @return String
 */
fun getOwnerProfileUrl(channelId: String): String {
    return BaseApplication.getContext().getString(R.string.owner_profile_url_format, channelId)
}

/**
 * 获取通用Header
 * @return Headers
 */
fun getCommonHeaders(): Headers {
    return Headers.Builder().apply {
        add(WEB_PAGE_HEADER_ORIGIN, YOUTUBE_HOME_PAGE_URL)
        add(WEB_PAGE_HEADER_ACCEPT_LANGUAGE, getLanguageHeaderValue())
        add(WEB_PAGE_HEADER_USER_AGENT, getUserAgentValue())
    }.build()
}

/**
 * 合并两个Header
 * @receiver Headers
 * @param that Headers
 * @return Headers
 */
fun Headers.add(that: Headers): Headers {
    return Headers.Builder().apply {
        this@add.names().map { Pair(it, this@add.get(it)) }.forEach {
            add(it.first, it.second)
        }
        that.names().map { Pair(it, that.get(it)) }.forEach {
            add(it.first, it.second)
        }
    }.build()
}