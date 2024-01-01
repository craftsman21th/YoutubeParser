package com.moder.compass.util

import androidx.lifecycle.LiveData

/**
 * @Author 陈剑锋
 * @Date 2023/12/14-10:31
 * @Desc
 */

val <T> LiveData<T>.version: Int
    @SuppressWarnings("VariableNaming")
    get() {
        kotlin.runCatching {
            val field_mVersion = LiveData::class.java.getDeclaredField("mVersion").apply {
                isAccessible = true
            }
            return field_mVersion.get(this) as Int
        }
        return Int.MIN_VALUE
    }