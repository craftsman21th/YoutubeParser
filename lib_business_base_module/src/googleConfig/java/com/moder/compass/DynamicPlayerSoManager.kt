package com.moder.compass

import android.content.Context

class DynamicPlayerSoManager private constructor(
    private val context: Context
) {

    fun addLoadListener(listener: DynamicSoLoadListener) {
    }

    fun removeLoadListener(listener: DynamicSoLoadListener) {
    }

    fun tryLoad(pageTag : String = "",listener: DynamicSoLoadListener? = null) {
        listener?.onLoadSuccess()
    }

    fun hasLoaded(): Boolean {
        return true
    }

    fun destroy() {
    }

    companion object {
        private var instance: DynamicPlayerSoManager? = null
        fun getInstance(ctx: Context): DynamicPlayerSoManager {
            return instance ?: synchronized(this) {
                instance ?: DynamicPlayerSoManager(ctx.applicationContext).also { instance = it }
            }
        }
    }

}