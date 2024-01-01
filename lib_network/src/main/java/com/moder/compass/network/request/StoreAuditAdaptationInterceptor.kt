package com.moder.compass.network.request

import android.net.Uri
import com.moder.compass.business.kernel.HostURLManager.domainWithHttps
import com.moder.compass.util.StoreAuditAdaptationUtil
import com.mars.kotlin.extension.d
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 适配商店审核,增加审核中的标志给服服务器
 */
class StoreAuditAdaptationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(
            if (checkIsAddReviewHead(request)) {
                "[商店审核适配拦截器]当前渠道需要增加服务器请求标志...audit=1".d()
                request.newBuilder()
                    .addHeader("audit", "1")
                    .build()
            } else {
                request
            }
        )
    }

    // 检查是否需要添加审核中的标志头
    private fun checkIsAddReviewHead(request: Request): Boolean {
        //是否为自己的服务器
        try {
            if (request.url().host() != Uri.parse(domainWithHttps).host) {
                "[商店审核适配拦截器]不是去往指定服务器。放弃处理，原url=${request.url()},需处理的url地址：$domainWithHttps".d()
                return false //不是我们的服务器。放弃处理
            }
        } catch (e: Exception) {
            "[商店审核适配拦截器]检查是否为去往自有服务器出错了..e=$e".d()
        }
        //是否为审核中
        if (!StoreAuditAdaptationUtil.getIns().checkCurrentChannelNoIsUnderReview()) {
            "[商店审核适配拦截器]当前渠道未在审核中,放弃处理".d()
            return false //不在审核中。放弃处理
        }
        return true
    }
}