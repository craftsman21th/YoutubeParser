package com.moder.compass.util

import android.content.Context
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.moder.compass.base.utils.SizeUtils
import com.dubox.drive.db.cloudfile.model.OfflineStatus
import com.moder.compass.component.base.R
import com.mars.united.widget.gone
import com.mars.united.widget.show

private const val INDENT_WIDTH: Float = 18f

/**
 * 离线状态UI工具
 *
 * Created by zhouzhimin on 2022/6/7.
 */


/**
 * 根据状态控制icon的显隐
 */
fun handleStateByShowIcon(context: Context, status: Int, ivOfflineStatus: ImageView?) {
    ivOfflineStatus ?: return
    ivOfflineStatus.clearAnimation()
    when (status) {
        OfflineStatus.STATUS_OFFLINE.status -> {
            ivOfflineStatus.setImageResource(R.drawable.status_offline_icon)
            ivOfflineStatus.show()
        }
        OfflineStatus.STATUS_ONGOING.status -> {
            ivOfflineStatus.setImageResource(R.drawable.status_ongoing_icon)
            ivOfflineStatus.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.anim_backup_status).apply {
                    interpolator = LinearInterpolator()
                }
            )
            ivOfflineStatus.show()
        }
        else -> {
            ivOfflineStatus.gone()
        }
    }
}

/**
 * 根据状态控制icon的显隐，并缩进文案
 */
fun handleStateByIndent(
    context: Context,
    status: Int,
    ivOfflineStatus: ImageView?,
    tvFileName: TextView?,
    fileName: String?
) {
    ivOfflineStatus ?: return
    tvFileName ?: return
    ivOfflineStatus.clearAnimation()
    when (status) {
        OfflineStatus.STATUS_OFFLINE.status, OfflineStatus.STATUS_ONGOING.status -> {
            fileName?.let {
                tvFileName.text = SpannableString(fileName).apply {
                    setSpan(
                        LeadingMarginSpan.Standard(SizeUtils.dp2px(INDENT_WIDTH), 0),
                        0,
                        it.length,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } ?: run {
                tvFileName.text = null
            }

            when (status) {
                OfflineStatus.STATUS_OFFLINE.status -> {
                    ivOfflineStatus.setImageResource(R.drawable.status_offline_icon)
                }
                OfflineStatus.STATUS_ONGOING.status -> {
                    ivOfflineStatus.setImageResource(R.drawable.status_ongoing_icon)
                    ivOfflineStatus.startAnimation(
                        AnimationUtils.loadAnimation(context, R.anim.anim_backup_status).apply {
                            interpolator = LinearInterpolator()
                        }
                    )
                }
            }
            ivOfflineStatus.show()
        }
        else -> {
            tvFileName.text = fileName
            ivOfflineStatus.gone()
        }
    }
}