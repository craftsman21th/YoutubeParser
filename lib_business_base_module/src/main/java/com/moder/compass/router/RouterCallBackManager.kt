package com.moder.compass.router

import android.content.Intent
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap

typealias RouterCallbackFunction = (intent: Intent?) -> Unit

/**
 * 路由的回调执行
 *
 * @author huping05
 * @since moder 2022/8/26
 */
object RouterCallBackManager {
    private val routerCallbackMap = ConcurrentHashMap<String, SoftReference<RouterCallbackFunction>>()

    /**
     * 执行回调方法，默认执行一次后清除
     */
    fun doCallback(router: String, intent: Intent? = null, isRemove: Boolean = true) {
        routerCallbackMap.get(router)?.get()?.invoke(intent)
        if (isRemove) {
            remove(router)
        }
    }

    fun add(router: String, routerCallbackFunction: RouterCallbackFunction) {
        routerCallbackMap[router] = SoftReference(routerCallbackFunction)
    }

    fun remove(router: String) {
        routerCallbackMap.remove(router)
    }

    fun clear() {
        routerCallbackMap.clear()
    }

    /**
     * containsKey
     */
    fun containsKey(router: String) = routerCallbackMap.containsKey(router)

}