package com.moder.compass

import java.io.File

interface DynamicSoLoadListener {
    fun onLoadSuccess()
    fun onLoadError(error: Throwable)
}


interface ResourceLoadListener {
    fun onLoadSuccess(file: File)
    fun onLoadError(error: Throwable)
}


interface CallBack<T>{
    fun invoke(t: T)
}