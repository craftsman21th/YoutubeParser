package com.mars.united.international.webplayer.account.jsbridge

import android.webkit.JavascriptInterface

/**
 * @Author 陈剑锋
 * @Date 2023/8/24-10:41
 * @Desc 登出用到的JS桥
 */
interface LogoutJsBridge {

    /**
     * 登出成功
     */
    @JavascriptInterface
    fun onLogoutSucceed()

    /**
     * 登出失败
     */
    @JavascriptInterface
    fun onLogoutFailed()

}