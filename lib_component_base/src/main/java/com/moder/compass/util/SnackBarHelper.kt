package com.moder.compass.util

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.moder.compass.component.base.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * @author sunmeng12
 * @since Terabox 2022/3/14
 *
 */

/**
 * Toast 在 Android 11 后不可设置居中属性，所以添加 snack bar 实现方案
 * @param view snackBar 显示都位置锚点
 * @param text 文案
 */
fun showSnackBarCenter(view: View, text: String, duration: Int = BaseTransientBottomBar.LENGTH_SHORT) {
    val snackBar = Snackbar.make(view, text, duration)
    val params = snackBar.view.layoutParams
    if (params is FrameLayout.LayoutParams) {
        params.gravity = Gravity.CENTER
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT
    }
    snackBar.view.background = view.resources.getDrawable(R.drawable.background_radius_12_white)
    snackBar.view.findViewById<TextView>(R.id.snackbar_text)?.let {
        it.setTextColor(view.resources.getColor(R.color.black))
        it.gravity = Gravity.CENTER
    }

    snackBar.show()
}