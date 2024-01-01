package com.mars.united.international.webplayer.core.player.utils

import android.webkit.WebResourceRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object HtmlUtil {
    private const val mDefaultEncoding = "UTF-8"

    //描述：请求指定的url并返回html页字符串
    fun request2Text(request: WebResourceRequest, cookies: String? = null): String {
        val total = StringBuilder()
        try {
            val url = URL(request.url.toString())
            val headers = request.requestHeaders
            val connection = url.openConnection() as HttpURLConnection
            //设置请求的header
            for ((key, value) in headers) {
                println("Key = $key, Value = $value")
                connection.setRequestProperty(key, value)
            }
            //扫码请求登录网页cookies一定要设置，不然后台判断登录状态会出错
            if (cookies != null && !cookies.isEmpty()) {
                connection.setRequestProperty("Cookie", cookies)
            }
            connection.requestMethod = request.method
            connection.connect()
            val `is` = connection.inputStream
            var encoding = connection.contentEncoding
            if (encoding == null) {
                encoding = mDefaultEncoding
            }
            val r = BufferedReader(InputStreamReader(`is`, encoding))
            var line: String?
            while (r.readLine().also { line = it } != null) {
                total.append(line)
            }
            `is`.close()
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return total.toString()
    }

}