package com.moder.compass.unpeeklivedata

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * 防倒灌LiveData
 */
class AntiBackFlowLiveData<T> : BaseAntiBackFlowLiveData<T>, DefaultLifecycleObserver {

    constructor(initialValue: T) : super(initialValue)
    constructor() : super()

    private val mOwners: MutableList<LifecycleOwner> = mutableListOf()
    private val ownerMap = mutableMapOf<Int, Observer<in T>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observeForever(owner, observer)
    }

    fun observeForever(lifecycleOwner: LifecycleOwner, observer: Observer<in T>) {
        super.observeForever(observer)
        lifecycleOwner.lifecycle.addObserver(this)
        mOwners.add(lifecycleOwner)
        ownerMap[lifecycleOwner.hashCode()] = observer
    }

    override fun onDestroy(owner: LifecycleOwner) {
        ownerMap.filter { item ->
            item.key == owner.hashCode()
        }.forEach { item ->
            this.removeObserver(item.value)
            ownerMap.remove(item.key)
        }
        mOwners.filter { item ->
            item == owner
        }.forEach { item ->
            item.lifecycle.removeObserver(this)
            mOwners.remove(item)
        }
        super.onDestroy(owner)
    }
}