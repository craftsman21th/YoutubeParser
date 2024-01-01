package com.moder.compass

import android.content.Context

class DynamicDocumentSoManager private constructor(
    private val context: Context
) {

    fun addLoadListener(listener: DynamicSoLoadListener) {
    }

    fun removeLoadListener(listener: DynamicSoLoadListener) {
    }

    fun tryLoad(pageTag: String= "", listener: DynamicSoLoadListener? = null) {
        listener?.onLoadSuccess()
    }

    fun hasLoaded(): Boolean = true

    fun destroy() {
    }

    companion object {
        private const val TAG = "dynamic_so_document"
        private var instance: DynamicDocumentSoManager? = null
        fun getInstance(ctx: Context): DynamicDocumentSoManager {
            return instance ?: synchronized(this) {
                instance ?: DynamicDocumentSoManager(ctx.applicationContext).also { instance = it }
            }
        }
    }

}