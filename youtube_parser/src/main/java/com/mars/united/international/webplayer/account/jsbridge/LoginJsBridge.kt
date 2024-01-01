package com.mars.united.international.webplayer.account.jsbridge

import android.webkit.JavascriptInterface

/**
 * @Author 陈剑锋
 * @Date 2023/8/24-10:41
 * @Desc 登录用到的JS桥
 */
interface LoginJsBridge {

    /**
     * 登录成功
     */
    @JavascriptInterface
    fun onLoginSucceed(ytConfig: String, url: String)

    /**
     * 登录失败
     */
    @JavascriptInterface
    fun onLoginFailed()

}