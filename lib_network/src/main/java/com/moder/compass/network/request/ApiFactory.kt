package com.moder.compass.network.request

import com.moder.compass.business.kernel.HostURLManager
import com.dubox.drive.network.base.BaseResponse
import com.dubox.drive.network.base.CommonParameters
import com.dubox.drive.network.base.IApiFactory
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 默认重试请求间隔
 */
const val DEFAULT_API_RETRY_INTERVAL:Long = 500L

/**
 * 默认重试次数
 */
const val DEFAULT_API_RETRY_TIMES:Int = 3
/**默认的超时时间 1分钟**/
const val COMMON_TIME_OUT: Long = 60L

/**
 * 最基础的httpClient，后续所有的httpClient都是使用该类扩展出
 */
val simpleHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(COMMON_TIME_OUT, TimeUnit.SECONDS)
    .readTimeout(COMMON_TIME_OUT, TimeUnit.SECONDS)
    .writeTimeout(COMMON_TIME_OUT, TimeUnit.SECONDS)
    .build()

/**
 * API 工厂
 */
object ApiFactory : IApiFactory {
    val gson = Gson()
    private val converterFactory = GsonConverterFactory.create(gson)

    override fun <K> create(commonParameters: CommonParameters, interceptors: List<Interceptor>,
                   path: String, clazz: Class<K>, retryNumber: Int): K {
        val absolutePath = if (path.startsWith("http")) path else HostURLManager.domainWithHttps + path
        val interceptorsList = mutableListOf<Interceptor>()
        interceptorsList.addAll(interceptors)
        interceptorsList.add(RetryTaskInterceptor(retryNumber))
        interceptorsList.add(ReplaceHostInterceptor())
        //增加对接口的商店审核适配的拦截器
        interceptorsList.add(StoreAuditAdaptationInterceptor())
        return Retrofit.Builder()
            .baseUrl(absolutePath)
            .client(simpleHttpClient.newBuilder().apply {
                addInterceptor(CommonParametersInterceptor(commonParameters))
                interceptorsList.forEach {
                    addInterceptor(it)
                }
            }.build())
            .addConverterFactory(converterFactory)
            .build().create(clazz)
    }

    /**
     * 不带公参的请求
     */
    override fun <K> create(path: String, clazz: Class<K>): K {
        val absolutePath = if (path.startsWith("http")) path else HostURLManager.domainWithHttps + path
        return Retrofit.Builder()
            .baseUrl(absolutePath)
            .client(simpleHttpClient)
            .addConverterFactory(converterFactory)
            .build().create(clazz)
    }

}
/**
 * 重试请求
 * */
fun <T : BaseResponse, K> requestWithRetry(
    commonParameters: CommonParameters, path: String, clazz: Class<K>,
    isSuccess: (T) -> Boolean = { it.isSuccess() },
    retryNumber: Int = DEFAULT_API_RETRY_TIMES, awaitTimeMills: Long = DEFAULT_API_RETRY_INTERVAL, requestInternal: (K) -> T?
): T? {
    return requestWithRetry(commonParameters, path, clazz, isSuccess, retryNumber, { awaitTimeMills }, requestInternal)
}

/**
 * 重试请求
 * */
inline fun <T : BaseResponse, K> requestWithRetry(
    commonParameters: CommonParameters, path: String, clazz: Class<K>,
    isSuccess: (T) -> Boolean = { it.isSuccess() },
    retryNumber: Int = DEFAULT_API_RETRY_TIMES, awaitTimeMills: () -> Long = { DEFAULT_API_RETRY_INTERVAL },
    requestInternal: (K) -> T?
): T? {
    if (retryNumber <= 1) {
        return requestInternal(ApiFactory.create(commonParameters, path, clazz, 0))
    }
    var lastResponse: T? = null
    var lastThrowable: Throwable? = null
    for (number in 0 until retryNumber) {
        lastResponse = try {
            requestInternal(ApiFactory.create(commonParameters, path, clazz, number))
        } catch (e: Throwable) {
            lastThrowable = e
            null
        }
        if (lastResponse != null && isSuccess(lastResponse)) {
            return lastResponse
        }
        if (awaitTimeMills() > 0) {
            Thread.sleep(awaitTimeMills())
        }
    }
    if (lastResponse == null) {
        lastThrowable?.let { throw it }
    }
    return lastResponse
}