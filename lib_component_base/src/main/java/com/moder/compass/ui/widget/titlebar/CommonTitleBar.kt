package com.moder.compass.ui.widget.titlebar

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.moder.compass.component.base.R
import com.mars.united.widget.show
import kotlinx.android.synthetic.main.layout_common_title_bar.view.*

/**
 * 中间title默认大小
 */
private const val DEFAULT_TITLE_SIZE = 15F
/**
 * @author: 曾勇
 * date: 2022-01-13 17:13
 * e-mail: zengyong01@baidu.com
 * desc: 通用TitleBar
 */
class CommonTitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val ivLeft by lazy { iv_left }
    private val tvTitle by lazy { tv_title }
    private val ivRight by lazy { iv_right }
    private val tvRight by lazy { tv_right }
    private val vShadow by lazy { v_shadow }

    private var leftClickListener: OnClickListener? = null
    private var rightClickListener: OnClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_common_title_bar, this, true)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar)
        // 左边图标
        val leftDrawable = ta.getDrawable(R.styleable.CommonTitleBar_left_img_res)
        leftDrawable?.let { ivLeft.setImageDrawable(leftDrawable) }
        val ivLeftShow = ta.getBoolean(R.styleable.CommonTitleBar_left_img_show, true)
        ivLeft.show(ivLeftShow)

        // 中间title
        tvTitle.text = ta.getString(R.styleable.CommonTitleBar_title_txt)
        // 字体大小
        val titleSize = ta.getDimension(R.styleable.CommonTitleBar_title_txt_size,
                DEFAULT_TITLE_SIZE)
        tvTitle.textSize = titleSize
        // 字体颜色
        val titleTxtColor = ta.getColor(R.styleable.CommonTitleBar_title_txt_color,
                resources.getColor(R.color.black))
        tvTitle.setTextColor(titleTxtColor)

        // 右边图标
        val rightDrawable = ta.getDrawable(R.styleable.CommonTitleBar_right_img_res)
        ivRight.setImageDrawable(rightDrawable)
        val ivRightShow = ta.getBoolean(R.styleable.CommonTitleBar_right_img_show, false)
        ivRight.show(ivRightShow)

        // 右边文案
        tvRight.text = ta.getString(R.styleable.CommonTitleBar_right_txt)
        val tvRightShow = ta.getBoolean(R.styleable.CommonTitleBar_right_txt_show, false)
        tvRight.show(tvRightShow)

        // 底部分割线
        val vShadowShow = ta.getBoolean(R.styleable.CommonTitleBar_show_divider, false)
        vShadow.show(vShadowShow)
        ta.recycle()

        // 设置点击事件
        ivLeft.setOnClickListener { view ->
            leftClickListener?.onClick(view)
            if (leftClickListener != null) return@setOnClickListener
            (context as? Activity)?.finish()
        }
        ivRight.setOnClickListener { view ->
            rightClickListener?.onClick(view)
        }
        tvRight.setOnClickListener { view ->
            rightClickListener?.onClick(view)
        }
    }

    /**
     * 设置左侧view点击事件
     */
    fun setLeftClickListener(clickListener: OnClickListener?): CommonTitleBar {
        leftClickListener = clickListener
        return this
    }

    /**
     * 设置右侧View点击事件
     */
    fun setRightClickListener(clickListener: OnClickListener?): CommonTitleBar {
        rightClickListener = clickListener
        return this
    }

    /**
     * 设置左边图标显示或隐藏
     */
    fun setLeftIvShow(condition: Boolean): CommonTitleBar {
        ivLeft.show(condition)
        return this
    }

    /**
     * 设置右边图标显示或隐藏
     */
    fun setRightIvShow(condition: Boolean): CommonTitleBar {
        ivRight.show(condition)
        return this
    }

    /**
     * 设置右边文案
     */
    fun setRightTxt(txtResId: Int): CommonTitleBar {
        tvRight.setText(txtResId)
        return this
    }

    /**
     * 设置右边文案
     */
    fun setRightTxt(text: String): CommonTitleBar {
        tvRight.text = text
        return this
    }

    /**
     * 设置右边文案颜色
     */
    fun setRightTxtColor(colorResId: Int): CommonTitleBar {
        tvRight.setTextColor(resources.getColor(colorResId))
        return this
    }

    /**
     * 设置标题
     */
    fun setTitleTxt(title: String): CommonTitleBar {
        tvTitle.text = title
        return this
    }

    /**
     * 设置标题
     */
    fun setTitleTxtRes(titleResId: Int): CommonTitleBar {
        tvTitle.setText(titleResId)
        return this
    }

}