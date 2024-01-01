package com.moder.compass.business.kernel

import com.dubox.drive.kernel.architecture.debug.DuboxLog

/** todo @yeliangliang 日志总开关 上线前关闭 */
private const val IS_DEBUG: Boolean = true

/**
 * 设置 debug 开关
 * 初始化时设置一次即可
 */
fun setIsDebug() {
    DuboxLog.setIsDebug(IS_DEBUG)
}