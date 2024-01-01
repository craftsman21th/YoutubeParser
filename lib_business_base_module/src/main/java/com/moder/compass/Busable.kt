package com.moder.compass

/**
 * 貌似和组件通信有关
 */
interface Busable {
    /**
     *
     */
    fun <T> getBus(clazz: Class<T>): T?
}