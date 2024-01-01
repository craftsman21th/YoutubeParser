package com.moder.compass.ui.widget.titlebar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.moder.compass.component.base.R
import com.moder.compass.ui.widget.CircleImageView
import com.mars.united.widget.gone
import com.mars.united.widget.show
import kotlinx.android.synthetic.main.vip_avatar_icon_layout.view.*

/**
 * 根据VIP状态变动，且支持小红点通知
 */
class VipAvatarIconView : FrameLayout {

    private val noticeView: View by lazy { v_notice }

    private val vipPremium: ImageView by lazy { iv_premium }

    val avatarView: com.moder.compass.ui.widget.CircleImageView by lazy { iv_avatar }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.vip_avatar_icon_layout, this)
    }

    /**
     * @param isVip true: 显示VIP标志， false: 显示非VIP标志
     * @since 3.0 头像下边的vip角标带阴影效果
     */
    fun changeVipState(isVip: Boolean) {
//        vipPremium.setImageResource(if (isVip) R.drawable.icon_premium_advatar else R.drawable.icon_premium_n)
    }

    /**
     * @param isShow true: 显示VIP标识, false: 隐藏
     */
    fun showVipState(isShow: Boolean) {
//        if (isShow) vipPremium.show() else vipPremium.gone()
    }

    /**
     * @param isShow true: 显示小红点, false: 隐藏小红点
     */
    fun showNotice(isShow: Boolean) {
        if (isShow) noticeView.show() else noticeView.gone()
    }

}