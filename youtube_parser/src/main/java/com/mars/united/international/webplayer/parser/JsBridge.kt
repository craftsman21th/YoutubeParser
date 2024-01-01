package com.mars.united.international.webplayer.parser

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import androidx.annotation.Keep

/**
 * @Author 陈剑锋
 * @Date 2023/7/12-15:26
 * @Desc
 */
@Keep
class JsBridge(
    private val onGetKeyInfo: (keyInfo: String) -> Unit
) {

    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onGetKeyInfo(keyInfo: String) = mainThreadHandler.post {
        onGetKeyInfo.invoke(keyInfo)
    }


}