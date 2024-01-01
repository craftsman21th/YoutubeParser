package com.moder.compass.ui.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import com.moder.compass.component.base.R
import com.mars.united.widget.gone
import com.mars.united.widget.show
import kotlinx.android.synthetic.main.dialog_loading.*

/**
 * 一个Loading样式
 *
 * Created by zhouzhimin on 2022/7/10.
 */
class Loading constructor(private val ctx: Context, private val showCancel: Boolean) : Dialog(ctx) {

    val animation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.clockwise_rotate_animation).apply {
            interpolator = LinearInterpolator()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        val view: View = (ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.dialog_loading, null)
        this.setContentView(view)
        setCancelable(false)
        if (showCancel) {
            cancel.show()
        } else {
            cancel.gone()
        }
        cancel.setOnClickListener {
            dismiss()
        }
    }
    /**
     * 设置 loading 的文字
     */
    fun setText(text: String) {
        tv_loading.text = text
    }
    override fun show() {
        super.show()
        icon?.startAnimation(animation)
    }

    override fun dismiss() {
        icon?.clearAnimation()
        super.dismiss()
    }
}