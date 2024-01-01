
package com.moder.compass.util

import android.graphics.Rect
import android.view.View

private const val HEIGHTSCALE: Float = 0.15F

/**
 * 粘贴板
 */
class KeyboardDetector {
    private var isKeyboardShowing = false
    /**
     * 监听 键盘
     * */
    fun observeKeyboard(contentView: View, onKeyboardVisibilityChanged: (Boolean) -> Unit) {
        contentView.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            contentView.getWindowVisibleDisplayFrame(r)
            val screenHeight = contentView.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * HEIGHTSCALE) {
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                    onKeyboardVisibilityChanged(true)
                }
            } else {
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                    onKeyboardVisibilityChanged(false)
                }
            }
        }
    }
}