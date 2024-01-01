package com.moder.compass.router

import android.text.TextUtils
import com.dubox.drive.kernel.util.CONSTANT_0
import com.mars.kotlin.extension.e

/**
 * 解析 params 参数
 * @author huping05
 * @since moder 2022/9/26
 */
fun parseParamsToMap(params: String): Map<String, String> {
    if (params.isBlank()) {
        return emptyMap()
    }
    val map = mutableMapOf<String, String>()
    try {
        params.split("&").forEach {
            val index = it.indexOfFirst { c ->  TextUtils.equals(c.toString(), "=") }
            val key = it.subSequence(CONSTANT_0, index).toString()
            val value = it.subSequence(index + 1, it.length).toString()
            map[key] = value
        }
    } catch (e: Exception) {
        e.e()
    }

    return map
}