package com.mars.united.international.webplayer.account.login.page

/**
 * @Author 陈剑锋
 * @Date 2023/8/29-11:20
 * @Desc 返回按钮接口
 */
interface IBackPress {

    /**
     * 当 按下 返回按钮
     * @return Boolean
     */
    fun onBackPressed(): Boolean
}