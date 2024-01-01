package com.moder.compass.util

/**
 * @Author 陈剑锋
 * @Date 2023/7/31-11:39
 * @Desc Map封装语法糖
 */

fun <KEY, VALUE> mapOf(
    paramsScope: MapScope<KEY, VALUE>.() -> Unit
) = MapScope<KEY, VALUE>().apply(paramsScope).value

class MapScope<KEY, VALUE> {

    val value: HashMap<KEY, VALUE> = hashMapOf()

    operator fun KEY.minus(value: VALUE) {
        this@MapScope.value[this] = value
    }

}