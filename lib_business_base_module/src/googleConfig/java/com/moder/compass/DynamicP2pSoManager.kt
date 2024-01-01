package com.moder.compass

import android.content.Context

class DynamicP2pSoManager private constructor(
    private val context: Context
) {

    fun addLoadListener(listener: DynamicSoLoadListener) {
    }

    fun removeLoadListener(listener: DynamicSoLoadListener) {
    }

    fun tryLoad(pageTag: String = "", listener: DynamicSoLoadListener? = null) {
        listener?.onLoadSuccess()
    }

    fun hasLoaded(): Boolean {
        return true
    }

    fun destroy() {
    }

    companion object {
        private var instance: DynamicP2pSoManager? = null
        fun getInstance(ctx: Context): DynamicP2pSoManager {
            return instance ?: synchronized(this) {
                instance ?: DynamicP2pSoManager(ctx.applicationContext).also { instance = it }
            }
        }
    }


}




