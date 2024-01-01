
package com.moder.compass.network.request

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


/**
 * 重试请求添加参数
 */
class RetryTaskInterceptor(private val retryNumber: Int) : Interceptor {
    companion object {
        private const val RETRY_KEY = "wp_retry_num"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(processRequest(chain.request()))
    }

    private fun processRequest(request: Request): Request {
        return request.newBuilder().apply {
            val rawUrl = request.url()
            if (rawUrl.queryParameter(RETRY_KEY).isNullOrEmpty() && retryNumber > 0) {
                url(rawUrl.newBuilder().addQueryParameter(RETRY_KEY, retryNumber.toString()).build())
            } else {
                request
            }
        }.build()
    }
}