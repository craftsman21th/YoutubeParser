package com.moder.compass.util.toast

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.moder.compass.ActivityLifecycleManager
import com.moder.compass.base.utils.SizeUtils
import com.moder.compass.component.base.R
import com.moder.compass.ui.floatview.CustomToastFloatView
import com.moder.compass.ui.floatview.FloatViewHelper
import com.mars.united.core.util.thread.mainHandler
import com.mars.united.widget.show
import kotlinx.android.synthetic.main.toast_open_file_dir_view.view.*
import java.lang.ref.WeakReference
import java.util.*

private const val MARGIN_RIGHT: Float = 40f
private const val MARGIN_BOTTOM: Float = 60f
private const val DURATION: Long = 3000L
private const val SHOW_DELAY: Long = 500L

/**
 * @author huping
 * @since Terabox 2022/10/13
 * 自定义样式的toast
 */
class CustomToast {
    private var view: WeakReference<CustomToastFloatView>? = null
    private var timer: Timer? = null
    private val marginRight by lazy { SizeUtils.dp2px(MARGIN_RIGHT) }
    private val marginBottom by lazy { SizeUtils.dp2px(MARGIN_BOTTOM) }

    private var timerTask: TimerTask? = null

    /**
     * show 默认在底部，可调整至顶部
     */
    fun show(
        layoutRes: Int,
        initViewBlock: (View) -> Unit = { _ -> },
        onClick: (() -> Unit)?,
        gravity: Int = Gravity.BOTTOM
    ) {
        if (view?.get() == null) {
            val topActivity: Activity = ActivityLifecycleManager.getTopActivity() ?: return
            view = WeakReference(CustomToastFloatView(topActivity))
        }
        val toast = view?.get() ?: return
        timerCancel()
        com.moder.compass.ui.floatview.FloatViewHelper.getInstance().add(toast, createLayoutParams(gravity), null)
        toast.flContent?.removeAllViews()
        val view = LayoutInflater.from(toast.flContent?.context).inflate(layoutRes, toast.flContent)
        initViewBlock.invoke(view)
        toast.setOnClickListener {
            onClick?.invoke()
        }
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                dismiss()
            }
        }
        timer?.schedule(timerTask, DURATION)
    }

    private fun createLayoutParams(gravity: Int): FrameLayout.LayoutParams {
        val fm = FrameLayout.LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        )
        fm.gravity = Gravity.CENTER_HORIZONTAL or gravity
        fm.rightMargin = marginRight
        fm.leftMargin = marginRight
        fm.bottomMargin = if (gravity == Gravity.BOTTOM) marginBottom else 0
        fm.topMargin = if (gravity == Gravity.TOP) marginBottom else 0
        return fm
    }

    private fun timerCancel() {
        timer?.cancel()
        timer = null
        timerTask?.cancel()
        timerTask = null
    }


    /**
     * dismiss
     */
    fun dismiss() {
        view?.get()?.let { it ->
            mainHandler.post {
                com.moder.compass.ui.floatview.FloatViewHelper.getInstance().remove(it)
                view?.get()?.flContent?.removeAllViews()
            }
        }
        timerCancel()
    }
}


private val customToast by lazy { CustomToast() }

/**
 * 展示在顶部的 Toast
 * 带一个查看按钮
 */
fun showOpenFileDirToast(tip: String, needShowButton: Boolean = true, onClick: (() -> Unit)? = null) {
    mainHandler.postDelayed({
        customToast.show(
            R.layout.toast_open_file_dir_view,
            initViewBlock = { view ->
                view.tv_path.text = tip
                view.tv_open.setOnClickListener {
                    onClick?.invoke()
                    customToast.dismiss()
                }
                view.tv_open.show(needShowButton)
            },
            null,
            gravity = Gravity.TOP
        )
    }, SHOW_DELAY)
}