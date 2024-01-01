package com.moder.compass.network.request

import android.text.TextUtils
import com.dubox.drive.kernel.Constants
import com.dubox.drive.base.network.StokenManager
import com.dubox.drive.kernel.architecture.net.RequestCommonParams
import com.dubox.drive.network.base.CommonParameters
import com.mars.kotlin.extension.Tag
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 特殊路径处理
 */
internal const val PASSPORT = "passport"
private const val HTTP_OK = 200
/**
 * okhttp拦截器
 */
@Tag("CommonParametersInterceptor")
class CommonParametersInterceptor constructor(
    private val params: CommonParameters) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val (bduss, uid) = params
        val stokenManager = StokenManager(bduss)
        val oldRequest = chain.request()
        // 添加新的参数
        val authorizedUrlBuilder = oldRequest.url().newBuilder().scheme(oldRequest.url().scheme())
            .host(oldRequest.url().host())
        // 如果是 passport 下的请求则使用 password 的公参
        val isPassport = oldRequest.url().pathSegments().contains(PASSPORT)
        if (isPassport) {
            authorizedUrlBuilder.addPassportParameters()
        } else {
            authorizedUrlBuilder.addCommonQueryParameters(bduss, uid)
        }
        var cookie = if (TextUtils.isEmpty(bduss)) "" else Constants.DUBOX_BDUSS_FIELD_NAME + "=" + bduss
        cookie = stokenManager.addPanPsc(cookie)
        cookie = stokenManager.addSToken(cookie)
        cookie = stokenManager.addSafeBoxPwdToken(cookie)
        cookie = stokenManager.addPanNdutFmt(cookie)
        // 新的请求
        val newUrl = authorizedUrlBuilder.build()
        val newRequest = oldRequest.newBuilder()
            .method(oldRequest.method(), oldRequest.body())
            .addHeader("Cookie", cookie)
            // todo @mali06 删除
            .apply {
//                if (DuboxLog.isDebug()) {
//                    addHeader("USER_UID", Account.uid)
//                    addHeader("Raw_appid", "20")
//                }
            }
            .addHeader("User-Agent", RequestCommonParams.getUserAgent())
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .apply {
                val referer = RequestCommonParams.getReferer(newUrl.toString())
                if (referer != null) {
                    addHeader("Referer", referer)
                }
            }
            .url(newUrl)
            .build()
        val response = chain.proceed(newRequest)
        return if (response.code() == HTTP_OK) {
            response
        } else {
            response.use {}
            chain.proceed(newRequest.newBuilder().url(authorizedUrlBuilder.scheme("http").build()).build())
        }
    }

}