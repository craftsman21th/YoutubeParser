package com.moder.compass.unpeeklivedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 防倒灌LiveData 基类
 */
open class BaseAntiBackFlowLiveData<T> : LiveData<T> {
    constructor(initialValue: T) : super(initialValue)
    constructor() : super()

    companion object {
        private const val START_VERSION = -1
    }

    private val mCurrentVersion = AtomicInteger(START_VERSION)

    @JvmField
    protected var isAllowNullValue = false

    /**
     * 监听（非粘性）
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, createObserverWrapper(observer, mCurrentVersion.get()))
    }

    /**
     * 全局监听
     */
    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(createObserverWrapper(observer, mCurrentVersion.get()))
    }

    /**
     * 带生命周期 的 粘性监听
     */
    fun observeSticky(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, createObserverWrapper(observer, START_VERSION))
    }

    /**
     * 全局 的 粘性监听
     */
    fun observeStickyForever(observer: Observer<in T>) {
        super.observeForever(createObserverWrapper(observer, START_VERSION))
    }

    public override fun setValue(value: T) {
        mCurrentVersion.getAndIncrement()
        super.setValue(value)
    }

    /**
     * 观察者 包装器
     */
    internal inner class ObserverWrapper(observer: Observer<in T>, version: Int) : Observer<T?> {
        private val mObserver: Observer<in T>
        private var mVersion = START_VERSION

        init {
            mObserver = observer
            this.mVersion = version
        }

        override fun onChanged(t: T?) {
            if (mCurrentVersion.get() > mVersion && (t != null || isAllowNullValue)) {
                mObserver.onChanged(t)
            }
        }

        override fun equals(that: Any?): Boolean {
            return mObserver == (that as? BaseAntiBackFlowLiveData<*>.ObserverWrapper)?.mObserver
        }

        override fun hashCode(): Int {
            return Objects.hash(mObserver)
        }
    }

    /**
     * 移除观察者
     */
    override fun removeObserver(observer: Observer<in T>) {
        if (observer.javaClass.isAssignableFrom(ObserverWrapper::class.java)) {
            super.removeObserver(observer)
        } else {
            super.removeObserver(createObserverWrapper(observer, START_VERSION))
        }
    }

    private fun createObserverWrapper(observer: Observer<in T>, version: Int): ObserverWrapper {
        return ObserverWrapper(observer, version)
    }

    /**
     * 清空消息
     */
    fun clean() {
        super.setValue(null)
    }
}