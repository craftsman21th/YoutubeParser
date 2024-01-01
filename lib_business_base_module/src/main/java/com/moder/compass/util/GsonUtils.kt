package com.moder.compass.util

import com.google.gson.Gson
import java.io.Reader

/**
 * 全局Gson实例，优化Gson内部类型缓存以及避免多次创Gson对象。
 */
object GsonUtils {
    /** Gson实例 */
    val gson: Gson by lazy { Gson() }
}

/**
 * 具体类型的json解析扩展。
 */
inline fun <reified T> Gson.fromJson(json: String): T = fromJson(json, T::class.java)

/**
 * 具体类型的json解析扩展，Reader参数版本。
 */
inline fun <reified T> Gson.fromJson(json: Reader): T = fromJson(json, T::class.java)
