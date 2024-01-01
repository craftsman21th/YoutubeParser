package com.mars.united.international.webplayer.account.login.state

/**
 * 检查登录结果
 */
sealed class LoginState {
    /**
     * 已登录
     */
    object LoggedIn : LoginState()

    /**
     * 未登录
     */
    object NotLoggedIn : LoginState()

    /**
     * 登录过期
     */
    object Expired : LoginState()

    /**
     * 未知（网络不好啥的）
     */
    object UNKNOWN : LoginState()

    val isLoggedId: Boolean get() = this is LoggedIn
}