package com.moder.compass.ui.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.util.FLOAT_1
import com.dubox.drive.kernel.util.INT_0
import com.moder.compass.util.dp2px
import com.mars.united.widget.gone
import com.mars.united.widget.show
import kotlinx.android.synthetic.main.view_refresh_header.view.*

private const val HEADER_HEIGHT: Float = 70f
private const val TIP_ANIM_DURATION: Long = 200L

/**
 * 刷新头
 *
 * Created by zhouzhimin on 2022/11/30.
 */
class RefreshHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = INT_0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val root = LayoutInflater.from(context).inflate(R.layout.view_refresh_header, this)

    private val lottie: LottieAnimationView? by lazy { root.lottie }
    private val refreshTip: TextView? by lazy { root.refresh_tip }


    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(HEADER_HEIGHT))
        lottie?.setSafeMode(true)
    }

    /**
     * 设置lottie动画下的文字
     */
    fun setRefreshTips(tip: String? = null) {
        refreshTip?.text = tip
        refreshTip?.show(!TextUtils.isEmpty(tip))
    }

    /**
     * 播放loading动画
     */
    fun startRefresh() {
        reset()
        if (lottie?.isAnimating == false) {
            lottie?.playAnimation()
        }
    }

    /**
     * 停止loading，展示安全感文案
     */
    fun stopRefresh() {
        if (lottie?.isAnimating == true) {
            lottie?.cancelAnimation()
        }
        lottie?.gone()
        refreshTip?.gone()
    }

    /**
     * 重置初始状态
     */
    fun reset() {
        lottie?.show()
        lottie?.progress = FLOAT_1
    }
}