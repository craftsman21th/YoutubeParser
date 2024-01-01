/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package com.moder.compass.util

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import com.mars.kotlin.extension.Tag
import com.mars.kotlin.extension.d

/**
 * Created by mali06 on 2021/9/14.
 */
private const val API19: Int = 19

/**
 * 底部导航栏帮助类
 */
@Tag("BottomNavigationBarTool")
class BottomNavigationBarHider {

    companion object {
        /**
         * 沉浸式隐藏底部导航栏
         */
        fun immersiveHide(activity: Activity) {
            if (checkHideNavigationType() == HideNavigationBarType.IMMERSIVE_HIDE) {
                hideNavigationBar(activity)
            }
        }

        /**
         * 设置底部导航栏
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        fun hideNavigationBar(activity: Activity, systemUIHide: (Boolean) -> Unit = {}) {
            "DBG  Build.VERSION.SDK_INT  : ${Build.VERSION.SDK_INT}".d()
            val decorView = activity.window?.decorView
            when (checkHideNavigationType()) {
                HideNavigationBarType.NORMAL_HIDE -> {
                    decorView ?: return
                    // 设置一个导航条可见改变监听器
                    decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                        if (visibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION != 0) {
                            systemUIHide(true)
                        } else {
                            systemUIHide(false)
                        }
                    }
                }
                HideNavigationBarType.IMMERSIVE_HIDE -> {
                    decorView ?: return
                    var option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View
                            .SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // 隐藏顶部状态栏
                        option = option or View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                    decorView.systemUiVisibility = option
                }
                else -> {
                }
            }
        }

        /**
         * 检测是否需要隐藏NavigationBar，以及隐藏方式
         *
         * @return
         */
        private fun checkHideNavigationType(): HideNavigationBarType? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < API19) {
                HideNavigationBarType.NORMAL_HIDE
            } else if (Build.VERSION.SDK_INT >= API19) {
                HideNavigationBarType.IMMERSIVE_HIDE
            } else {
                HideNavigationBarType.UN_HIDE
            }
        }
    }


    /**
     * navigation bar隐藏方式
     */
    enum class HideNavigationBarType {
        UN_HIDE,  // 不隐藏，4.0以下版本没有navigation bar所以无需隐藏
        NORMAL_HIDE,  // 正常隐藏，4.0-4.3不支持沉浸模式
        IMMERSIVE_HIDE // 沉浸模式，4.4以上通过沉浸模式隐藏
    }
}

/**
 * 资源圈视频播放器专用全屏模式
 */
fun Window.immerseTransparentStatusBarAndNavBar(lightColor: Boolean = false, whenAllow: (() -> Unit)? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        statusBarColor = Color.TRANSPARENT
        var option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (lightColor) {
            option = option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        decorView.systemUiVisibility = option
        whenAllow?.invoke()
    }
}