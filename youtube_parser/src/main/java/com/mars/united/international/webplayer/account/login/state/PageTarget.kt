package com.mars.united.international.webplayer.account.login.state

import java.io.Serializable

const val PAGE_TARGET: String = "page_target"
const val CALLBACK_ID: String = "callback_id"

/**
 * @Author 陈剑锋
 * @Date 2023/8/25-15:01
 * @Desc 页面目标
 */
sealed class PageTarget : Serializable {
    /**
     * 为了 登录
     */
    object ToLogin : PageTarget(), Serializable

    /**
     * 为了 登出
     */
    object ToLogout : PageTarget(), Serializable
}