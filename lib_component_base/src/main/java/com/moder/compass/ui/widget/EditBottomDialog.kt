package com.moder.compass.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.util.SPACE_10
import com.moder.compass.sns.ui.magicindicator.buildins.UIUtil
import com.moder.compass.util.setDayOrNightModeForDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mars.united.core.os.dip2px

/**
 * @author sunmeng
 * create at 2021-07-20
 * Email: sunmeng12@baidu.com
 */

class EditBottomDialog : BottomSheetDialog {

    private var mContent: TextView? = null
    private var mLeftBtn: TextView? = null
    private var mRightBtn: TextView? = null
    private var mEditText: EditText? = null

    constructor(context: Context) : super(context, R.style.BottomSheetInputDialog) {
        initView()
    }

    private fun initView() {
        val parentView = LayoutInflater.from(context).inflate(R.layout.edit_bottom_dialog_layout, null)

        val layoutParams = ViewGroup.LayoutParams(
            UIUtil.getScreenWidth(context),
                ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(parentView, layoutParams)

        mContent = parentView.findViewById(R.id.tv_content)
        mLeftBtn = parentView.findViewById(R.id.tv_cancel)
        mRightBtn = parentView.findViewById(R.id.tv_confirm)
        mEditText = parentView.findViewById(R.id.input_edittext)
    }

    override fun onStart() {
        super.onStart()
        window?.let {
            it.findViewById<View>(R.id.design_bottom_sheet).background = ColorDrawable(Color.TRANSPARENT)
            it.setWindowAnimations(R.style.anim_dialog_slide_from_bottom)
        }
    }

    override fun show() {
        super.show()
        val radius = context.dip2px(SPACE_10)?.toFloat()
        setDayOrNightModeForDialog(this, radius, radius, radius, radius)
        mEditText?.requestFocus()
    }

    /**
     * 获取输入框内容
     */
    fun getInputContent(): String? {
        return mEditText?.text.toString().trim()
    }

    /**
     * 设置提示文案
     */
    fun setContent(title: String) {
        mContent?.text = title
    }

    /**
     * 修改提示文案颜色
     */
    fun setContentColor(color: Int) {
        mContent?.setTextColor(color)
    }

    /**
     * 设置确认按钮点击事件
     */
    fun setRightBtnOnClickListener(listener: View.OnClickListener) {
        mRightBtn?.setOnClickListener(listener)
    }

    /**
     * 设置取消按钮点击事件
     */
    fun setLeftBtnOnClickListener(listener: View.OnClickListener) {
        mLeftBtn?.setOnClickListener(listener)
    }

}