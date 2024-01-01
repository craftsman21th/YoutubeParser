package com.moder.compass.util.toast

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import com.moder.compass.ActivityLifecycleManager
import com.moder.compass.base.utils.SizeUtils
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.BaseShellApplication
import com.dubox.drive.kernel.android.util.TextTools
import com.dubox.drive.kernel.android.util.TextTools.getStringWithDrawble
import com.dubox.drive.kernel.util.CONSTANT_0
import com.dubox.drive.kernel.util.INT_0
import com.dubox.drive.kernel.util.INT_100
import com.moder.compass.statistics.UPLOAD_ADD_TASK_TOAST_PV
import com.moder.compass.statistics.UPLOAD_COMPLETE_TOAST_PV
import com.moder.compass.statistics.statisticViewEvent
import com.moder.compass.ui.floatview.FloatViewHelper
import com.moder.compass.ui.floatview.ToastFloatView
import com.mars.united.core.os.dip2px
import com.mars.united.core.util.thread.mainHandler
import com.mars.united.widget.gone
import com.mars.united.widget.show
import java.lang.ref.WeakReference
import java.util.*

private const val MARGIN_RIGHT: Float = 40f
private const val MARGIN_BOTTOM: Float = 60f
private const val DURATION: Long = 5000L
private const val SHOW_DELAY: Long = 500L
private const val TOAST_RADIUS: Float = 12f
private const val SAFE_CHECK_DURATION: Long = 3000L
private const val PROGRESS_DURATION: Long = 1000L

/**
 * @since Terabox 2.16.0 多次测试发现，设置延迟 广告才可点击，
 * 不知道原因是啥
 */
private const val AD_DELAY: Long = 300L

/**
 * 上传提示Toast
 */
internal class UploadToast {
    private var view: WeakReference<ToastFloatView>? = null
    private var timer: Timer? = null
    private val marginRight by lazy { SizeUtils.dp2px(MARGIN_RIGHT) }
    private val marginBottom by lazy { SizeUtils.dp2px(MARGIN_BOTTOM) }

    /**
     * 安全检测 toast 是否正在展示
     */
    var safeToastIsShowing: Boolean = false
    private var timerTask: TimerTask? = null

    /**
     * show
     */
    fun show(content: CharSequence, iconRes: Int, onClick: (() -> Unit)?,
             onAdViewVisible: ((FrameLayout) -> Unit)? = null, onPlayAnim: ((View) -> Unit)? = null, duration: Long = DURATION
    ) {
        safeToastIsShowing = false
        if (view?.get() == null) {
            val topActivity: Activity = ActivityLifecycleManager.getTopActivity() ?: return
            view = WeakReference(ToastFloatView(topActivity))
        }
        val toast = view?.get() ?: return
        timerCancel()
        toast.contentParent?.background = AppCompatResources.getDrawable(BaseShellApplication.getContext(),
            R.drawable.background_upload_toast)
        com.moder.compass.ui.floatview.FloatViewHelper.getInstance().add(toast, createLayoutParams(), null)
        toast.tvContent?.text = content
        // 添加没有图标时的处理方案
        if (iconRes > CONSTANT_0) {
            toast.imgLoading?.setImageResource(iconRes)
        }
        toast.imgLoading?.show(iconRes > CONSTANT_0)
        toast.setOnClickListener {
            onClick?.invoke()
        }
        toast.adParent?.let {
            it.removeAllViews()
            it.gone()
            it.postDelayed({onAdViewVisible?.invoke(it)}, AD_DELAY)
        }
        toast.contentParent?.let {
            onPlayAnim?.invoke(it)
        }
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                dismiss()
            }
        }
        timer?.schedule(timerTask, duration)
    }

    private fun createLayoutParams(): FrameLayout.LayoutParams {
        val fm = FrameLayout.LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        )
        fm.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        fm.rightMargin = marginRight
        fm.leftMargin = marginRight
        fm.bottomMargin = marginBottom
        return fm
    }

    private fun timerCancel() {
        timer?.cancel()
        timerTask?.cancel()
        timer = null
        timerTask = null
    }

    /**
     * 设置 toast 文案
     */
    fun setContentText(text: String) {
        val toast = view?.get() ?: return
        toast.tvContent?.text = text
    }

    /**
     * dismiss
     */
    fun dismiss() {
        view?.get()?.let { it ->
            mainHandler.post {
                it.adParent?.let { ad ->
                    ad.removeAllViews()
                    ad.gone()
                }
                com.moder.compass.ui.floatview.FloatViewHelper.getInstance().remove(it)
            }
        }
        timerCancel()
    }
}


private val toast by lazy { UploadToast() }

/**
 * 展示上传完成的toast
 */
fun showUploadCompleteToast(onClick: (() -> Unit)? = null,
                            onAdViewVisible: ((FrameLayout) -> Unit)? = null) {
    mainHandler.postDelayed({
        val content = BaseShellApplication.getContext().getString(R.string.file_upload_complete)
        toast.show(
            content,
            R.drawable.ic_upload_toast_complete,
            onClick,
            onAdViewVisible
        )
    }, SHOW_DELAY)
    statisticViewEvent(UPLOAD_COMPLETE_TOAST_PV)
}

/**
 * 展示上传任务添加的toast
 */
fun showAddUploadTaskToast(contentStr: String? = null, onClick: () -> Unit,
                           onAdViewVisible: ((FrameLayout) -> Unit)? = null) {
    mainHandler.postDelayed({
        toast.show(
            getAddUploadCharSequence(contentStr),
            R.drawable.ic_upload_toast_loading,
            onClick,
            onAdViewVisible
        )
    }, SHOW_DELAY)
    statisticViewEvent(UPLOAD_ADD_TASK_TOAST_PV)
}

/**
 * 展示添加下载任务的 toast
 */
fun showAddDownloadToast(contentStr: String, onClick: (() -> Unit)? = null,
                         onAdViewVisible: ((FrameLayout) -> Unit)? = null) {
    mainHandler.postDelayed({
        toast.show(
            contentStr,
            CONSTANT_0,
            onClick,
            onAdViewVisible
        )
    }, SHOW_DELAY)
}

private fun getAddUploadCharSequence(contentStr: String? = null): CharSequence {
    val context = BaseShellApplication.getContext()
    val icArrowRight = context.getDrawable(R.drawable.ic_arrow_right_white)
    return if (contentStr.isNullOrBlank()) {
        val viewNow = context.getString(R.string.chain_recognize_immediately_check)
        getStringWithDrawble(
            context.getString(
                R.string.added_task_to_upload_toast,
                viewNow
            ), " ", icArrowRight, 0
        )
    } else {
        contentStr
    }
}

/**
 * 展示资源圈资源保存成功的 toast
 */
fun showShareResourceSaveSuccessToast(dirName: String, onClick: () -> Unit,
                                      onAdViewVisible: ((FrameLayout) -> Unit)? = null) {
    mainHandler.postDelayed({
        toast.show(
            getShareResourceSaveSuccessText(dirName),
            R.drawable.ic_upload_toast_complete,
            onClick, onAdViewVisible)
    }, SHOW_DELAY)
}

private fun getShareResourceSaveSuccessText(dirName: String): CharSequence {
    val context = BaseShellApplication.getContext()
    val icArrowRight = context.getDrawable(R.drawable.ic_arrow_right_white)
    val viewNow = context.getString(R.string.chain_recognize_immediately_check)
    val toastStr = context.getString(R.string.share_resource_save_success_toast, dirName)
    val blueColor = context.resources.getColor(R.color.color_0bafe5)
    val lineColor = context.resources.getColor(R.color.color_33FFFFFF)
    val builder = TextTools.highlightText(toastStr, blueColor, true, viewNow)
    val resultBuilder = TextTools.highlightText(builder, lineColor, true, false, "|")
    return getStringWithDrawble(resultBuilder, " ", icArrowRight, 0)
}

/**
 * 展示安全检测的 toast
 */
fun showSafeCheckToast(onAnimEnd: () -> Unit) {
    if (toast.safeToastIsShowing) return
    val context = BaseShellApplication.getContext()
    if (!com.dubox.drive.base.network.NetworkUtil.isConnectedToAnyNetwork(context)) {
        onAnimEnd.invoke()
        return
    }
    val encryptUploadText = context.getString(R.string.encrypt_upload_preparation)
    val radius = context.dip2px(TOAST_RADIUS)
    mainHandler.postDelayed({
        toast.show(encryptUploadText,
            R.drawable.ic_safe_transparent,
            null,
            duration = SAFE_CHECK_DURATION,
            onPlayAnim = {
                val valueAnimator =
                    ValueAnimator.ofInt(INT_0, INT_100).setDuration(PROGRESS_DURATION)
                val progressDrawable = ProgressDrawable(
                    INT_100, context.resources.getColor(R.color.color_e5030B1A),
                    context.resources.getColor(R.color.color_00ddc8), radius, radius
                )
                it.background = progressDrawable
                valueAnimator.addUpdateListener {
                    val animValue = it.animatedValue as Int
                    progressDrawable.progress = animValue
                }
                valueAnimator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        // onAnimationStart
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        toast.setContentText(context.getString(R.string.safety_ability_title))
                        onAnimEnd.invoke()
                        toast.safeToastIsShowing = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        // onAnimationCancel
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                        // onAnimationRepeat
                    }
                })
                valueAnimator.start()
            })
    }, SHOW_DELAY)
    toast.safeToastIsShowing = true
}