package com.moder.compass.extension

import android.view.View
import android.view.ViewGroup.LayoutParams

/**
 * 更新 View 的 LayoutParams 的扩展
 */
inline fun <reified T : LayoutParams> View.updateLayoutParams(
    updater: T.() -> Unit
) {
    (layoutParams as? T)?.apply {
        updater()
        layoutParams = this
    }
}