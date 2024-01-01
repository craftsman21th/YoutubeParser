package com.moder.compass.network.request

import android.net.Uri
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.dubox.drive.kernel.architecture.debug.sever.DebugServerHostPersistence
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 切换Server环境
 * 仅在测试环境生效！！！
 * @since Terabox 2022/8/4
 */
class ReplaceHostInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val oldHttpUrl: HttpUrl = request.url()
        val debugApiUrl = DebugServerHostPersistence.getDebugApiUrl(oldHttpUrl.encodedPath())
        val finalRequest = try {
            if (DuboxLog.isDebug() && debugApiUrl?.isNotEmpty() == true) {
                val newUri = Uri.parse(debugApiUrl)
                val builder = request.newBuilder()
                val newFullUrl: HttpUrl = oldHttpUrl
                        .newBuilder()
                        .scheme(newUri.scheme)
                        .host(newUri.host)
                        .port(newUri.port)
                        .build()
                builder.url(newFullUrl).build()
            } else {
                request
            }
        } catch (e: Exception) {
            request
        }
        return chain.proceed(finalRequest)
    }
}