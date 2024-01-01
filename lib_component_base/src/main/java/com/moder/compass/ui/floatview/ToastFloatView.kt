package com.moder.compass.ui.floatview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.moder.compass.component.base.R

/**
 * @author sunmeng12
 * @since Terabox 2022/4/15
 * Activity 切换时任然显示的 Toast
 */
class ToastFloatView(context: Context) : com.moder.compass.ui.floatview.FloatView(context) {

    val tvContent: TextView? by lazy { findViewById(R.id.tv_content) }
    val imgLoading: ImageView? by lazy { findViewById(R.id.img_loading) }
    val adParent: FrameLayout? by lazy { findViewById(R.id.ad_parent) }
    val contentParent: LinearLayout? by lazy { findViewById(R.id.content_parent) }
    override fun initView(context: Context?): View {
        setDrag(false)
        return LayoutInflater.from(context).inflate(R.layout.layout_uploaded_toast, null)
    }
}