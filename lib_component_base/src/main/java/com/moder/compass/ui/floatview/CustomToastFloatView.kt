package com.moder.compass.ui.floatview

import android.content.Context
import android.view.View
import android.widget.FrameLayout

/**
 * @author huping
 * @since Terabox 2022/10/13
 * Activity 切换时仍然显示的 Toast
 */
class CustomToastFloatView(context: Context) : com.moder.compass.ui.floatview.FloatView(context) {

    var flContent: FrameLayout? = null

    override fun initView(context: Context?): View {
        setDrag(false)
        flContent = FrameLayout(this.context)
        return flContent!!
    }
}