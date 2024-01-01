package com.moder.compass.ui.floatview

import android.app.Activity

/**
 * @author sunmeng12
 * @since Terabox 2022/3/30
 * FloatView 显示过滤器，在这里配置 FloatView 不显示的页面 Activity 名称
 */
interface IAttachFilter {

    /**
     * 判断是否在当前 activity 显示
     */
    fun isShowInActivity(activity: Activity): Boolean
}