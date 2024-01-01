package com.moder.compass.ui.lottie

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.util.INT_0
import com.moder.compass.ui.widget.tooltip.LottieUtil.fetchRemote

private const val TAG: String = "DynamicHostLottieView"
const val CDN_HOST_PREFIX: String = "http://data."

/**
 * @Author 陈剑锋
 * @Date 2023/8/9-14:56
 * @Desc
 */
@SuppressLint("CustomViewStyleable")
open class DynamicHostLottieView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LottieAnimationView(context, attrs) {

    private val remoteUrl: String
    private var autoPlay: Boolean
    private var loop: Boolean

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LottieAnimationView).apply {
            imageAssetsFolder = getString(R.styleable.LottieAnimationView_lottie_imageAssetsFolder) ?: ""
            remoteUrl = getString(R.styleable.LottieAnimationView_lottie_url) ?: ""
            autoPlay = getBoolean(R.styleable.LottieAnimationView_lottie_autoPlay, false)
            loop = getBoolean(R.styleable.LottieAnimationView_lottie_loop, false)
        }.recycle()
        initLottieParams()
    }

    /**
     * 处理Lottie参数
     */
    private fun initLottieParams() {
        setAnimationFromUrl(remoteUrl)
        if (loop) {
            repeatCount = LottieDrawable.INFINITE
        }
        if (autoPlay) {
            playAnimation()
        }
    }

    override fun setAnimationFromUrl(remoteUrl: String) {
        if (remoteUrl.isNotBlank()) {
            fetchRemote(
                remoteUrl
            ) { composition: LottieComposition ->
                setComposition(composition)
            }
        }
    }
}