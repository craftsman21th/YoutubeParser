package com.moder.compass.extension

import androidx.annotation.MainThread
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.moder.compass.unpeeklivedata.AntiBackFlowMediatorLiveData

/**
 * LiveData转换拓展
 * @param isAntiBackFlow Boolean    是否防倒灌
 */
@MainThread
fun <X, Y> LiveData<X>.switchMap(
    isAntiBackFlow: Boolean = false,
    switchMapFunction: Function<X?, LiveData<Y>>
): MediatorLiveData<Y?> {
    var mSource: LiveData<Y>? = null
    val result = if (isAntiBackFlow) {
        AntiBackFlowMediatorLiveData<Y?>()
    } else {
        MediatorLiveData<Y?>()
    }
    result.addSource(this) { x: X? ->
        val newLiveData = switchMapFunction.apply(x)
        if (mSource === newLiveData) {
            return@addSource
        }
        if (mSource != null) {
            result.removeSource(mSource!!)
        }
        mSource = newLiveData
        if (mSource != null) {
            result.addSource(mSource!!) { y ->
                result.setValue(y)
            }
        }
    }
    return result
}

/**
 * LiveData转换拓展
 * @param isAntiBackFlow Boolean    是否防倒灌
 */
@MainThread
fun <X, Y> LiveData<X>.switchMapNotNull(
    isAntiBackFlow: Boolean = false,
    switchMapFunction: Function<X, LiveData<Y>>
): MediatorLiveData<Y?> {
    var mSource: LiveData<Y>? = null
    val result = if (isAntiBackFlow) {
        AntiBackFlowMediatorLiveData<Y?>()
    } else {
        MediatorLiveData<Y?>()
    }
    result.addSource(this) { x: X ->
        val newLiveData = switchMapFunction.apply(x)
        if (mSource === newLiveData) {
            return@addSource
        }
        if (mSource != null) {
            result.removeSource(mSource!!)
        }
        mSource = newLiveData
        if (mSource != null) {
            result.addSource(mSource!!) { y ->
                result.setValue(y)
            }
        }
    }
    return result
}


/**
 * LiveData转换拓展
 * @param isAntiBackFlow Boolean    是否防倒灌
 */
@MainThread
fun <X, Y> LiveData<X>.map(
    isAntiBackFlow: Boolean = false,
    mapFunction: Function<X?, Y>
): MediatorLiveData<Y> {
    val result = if (isAntiBackFlow) {
        AntiBackFlowMediatorLiveData<Y>()
    } else {
        MediatorLiveData<Y>()
    }
    result.addSource(this) { x: X? ->
        result.value = mapFunction.apply(x)
    }
    return result
}


/**
 * LiveData转换拓展
 * @param isAntiBackFlow Boolean 是否防倒灌
 */
@MainThread
fun <X, Y> LiveData<X>.mapNotNull(
    isAntiBackFlow: Boolean = false,
    mapFunction: Function<X, Y>
): MediatorLiveData<Y> {
    val result = if (isAntiBackFlow) {
        AntiBackFlowMediatorLiveData<Y>()
    } else {
        MediatorLiveData<Y>()
    }
    result.addSource(this) { x: X ->
        result.setValue(mapFunction.apply(x))
    }
    return result
}