package com.moder.compass.ui.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import com.moder.compass.component.base.R
import java.util.*

private const val DURATION: Long = 3000L
/**
 * 屏幕中央的 toast
 */
class ToastDialog(context: Context) : Dialog(context) {

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.toast_dialog)
        setCancelable(false)
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                closeToast()
            }
        }
    }

    /**
     * 设置 toast 的文字
     */
    fun setText(text: String) {
        findViewById<TextView>(R.id.tv_toast).text = text
    }

    override fun show() {
        super.show()
        timer?.schedule(timerTask, DURATION)
    }

    /**
     * 关闭 toast
     */
    private fun closeToast() {
        dismiss()
        timer?.cancel()
        timerTask?.cancel()
    }
}