package com.moder.compass.extension

/**
 * 线程不安全的懒加载，Android中很多场景都在主线程中进行，无需使用线程安全模式，以提升性能。
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> unsafeLazy(noinline initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, initializer)
