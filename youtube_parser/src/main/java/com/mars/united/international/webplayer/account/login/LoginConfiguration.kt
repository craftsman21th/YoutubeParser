package com.mars.united.international.webplayer.account.login

/**
 * @Author 陈剑锋
 * @Date 2023/8/25-19:07
 * @Desc 登录杂项
 */

typealias ResultCallback = (Boolean) -> Unit

/**
 * 登录相关配置
 */
object LoginConfiguration {

    private val handleLoginCallbacks: HashMap<Long, ResultCallback> = hashMapOf()

    /**
     * 新增回调
     * @param callback Function1<Boolean, Unit>
     * @return Long
     */
    fun addCallback(callback: ResultCallback): Long {
        val timeNow = System.currentTimeMillis()
        synchronized(handleLoginCallbacks) {
            handleLoginCallbacks[timeNow] = callback
        }
        return timeNow
    }

    /**
     * 获取回调
     * @param callbackId Long
     * @return ResultCallback?
     */
    fun getCallback(callbackId: Long): ResultCallback? {
        synchronized(handleLoginCallbacks) {
            return handleLoginCallbacks[callbackId]
        }
    }

    /**
     * 移除回调
     * @param callbackId Long
     */
    fun removeCallback(callbackId: Long) {
        synchronized(handleLoginCallbacks) {
            handleLoginCallbacks.remove(callbackId)
        }
    }

}