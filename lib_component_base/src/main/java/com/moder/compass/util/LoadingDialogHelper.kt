package com.moder.compass.util

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import com.moder.compass.component.base.R
import com.mars.united.core.os.dip2px
import kotlinx.android.synthetic.main.dialog_novel_add.view.*

private const val SPACE_14 = 14F
/**
 * Created by linwentao on 2019-09-20.
 *
 * Describe: 网盘通用的带lottie loading弹窗
 */
class LoadingDialogHelper(val mActivity: Activity) {

    /** loading  */
    private var mLoadingDialog: Dialog = Dialog(mActivity, R.style.ModerDialogTheme)

    /** 加载布局  */
    private var view: View = LayoutInflater.from(mActivity).inflate(R.layout.dialog_novel_add, null)

    init {
        mLoadingDialog.setContentView(view)
        mLoadingDialog.setOnShowListener {
            if (view.loading_lottie != null) {
                view.loading_lottie.setSafeMode(true)
                view.loading_lottie.playAnimation()
            }
        }
        mLoadingDialog.setOnDismissListener {
            if (view.loading_lottie != null) {
                view.loading_lottie.cancelAnimation()
            }
        }
        mLoadingDialog.setOnCancelListener {
            if (view.loading_lottie != null) {
                view.loading_lottie.cancelAnimation()
            }
        }
    }

    /**
     * 展示loading
     *
     * @param loadingText Int 提示文案
     */
    fun showLoading(loadingText: Int): Dialog? {
        if (mActivity.isFinishing || mActivity.isDestroyed) {
            return null
        }
        view.dialog_tips.setText(loadingText)
        mLoadingDialog.show()
        val radius = view.context.dip2px(SPACE_14).toFloat()
        setDayOrNightModeForView(view, radius, radius, radius, radius)
        return mLoadingDialog
    }

    /**
     * 隐藏loading
     */
    fun dismisssLoading() {
        if (mActivity.isFinishing || mActivity.isDestroyed) {
            return
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }
}