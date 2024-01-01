package com.moder.compass.util

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import com.moder.compass.base.utils.GlobalConfigKey
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.architecture.config.GlobalConfig

/**
 * @author: 曾勇
 * date: 2021-11-24 16:28
 * e-mail: zengyong01@baidu.com
 * desc: 夜间模式
 */

/**
 * 日间背景
 */
private const val DAY_MODE_COLOR = 0x00000000

/**
 * 夜间背景
 */
const val NIGHT_MODE_COLOR = 0x99000000.toInt()

/**
 * 为activity设置夜间模式
 * @param needChange 手动修改暗黑模式选项时需要更改背景，主动检测是不需要
 */
fun setDayOrNightMode(decorView: View?, needChange: Boolean) {
    if (decorView == null) return
    val isOpenNightMode = GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_OPEN_NIGHT_MODE)
    if (!isOpenNightMode && !needChange) {
        return
    }
    val colorDrawable = if (isOpenNightMode) {
        ColorDrawable(NIGHT_MODE_COLOR)
    } else {
        ColorDrawable(DAY_MODE_COLOR)
    }
    if (decorView is FrameLayout) {
        decorView.foreground = colorDrawable
    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        decorView.foreground = colorDrawable
    }
}

/**
 * 该方法用来为dialog添加夜间模式，
 * 该方法的调用必须在dialog.show方法后才起作用
 */
fun setDayOrNightModeForDialog(dialog: Dialog?,
                               leftTopR: Float = 0F, rightTopR: Float = 0F,
                               leftBottomR: Float = 0F, rightBottomR: Float = 0F) {
    val isOpenNightMode = GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_OPEN_NIGHT_MODE)
    val contentView = dialog?.findViewById<View>(R.id.dialogContentView) ?: return
    if (!isOpenNightMode) return

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        val drawable = GradientDrawable().also {
            it.shape = GradientDrawable.RECTANGLE
            it.gradientType = GradientDrawable.LINEAR_GRADIENT
            it.setColor(NIGHT_MODE_COLOR)
            it.cornerRadii = floatArrayOf(leftTopR, leftTopR, rightTopR, rightTopR,
                    rightBottomR, rightBottomR, leftBottomR, leftBottomR)
        }
        contentView.foreground = drawable
    }

}

/**
 * 该方法用来为指定View添加夜间模式，
 */
fun setDayOrNightModeForView(contentView: View?,
                               leftTopR: Float = 0F, rightTopR: Float = 0F,
                               leftBottomR: Float = 0F, rightBottomR: Float = 0F) {
    if (contentView == null) return
    val isOpenNightMode = GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_OPEN_NIGHT_MODE)
    if (!isOpenNightMode) return

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        val drawable = GradientDrawable().also {
            it.shape = GradientDrawable.RECTANGLE
            it.gradientType = GradientDrawable.LINEAR_GRADIENT
            it.setColor(NIGHT_MODE_COLOR)
            it.cornerRadii = floatArrayOf(leftTopR, leftTopR, rightTopR, rightTopR,
                    rightBottomR, rightBottomR, leftBottomR, leftBottomR)
        }
        contentView.foreground = drawable
    }

}