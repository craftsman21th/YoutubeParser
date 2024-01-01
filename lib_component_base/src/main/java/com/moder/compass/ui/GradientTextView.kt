package com.moder.compass.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.moder.compass.component.base.R

/**
 * 支持渐变的textview
 * */
class GradientTextView constructor(context: Context, attrs: AttributeSet?) :
    AppCompatTextView(context, attrs) {
    private var selectedStartColor: Int = 0
    private var selectedEndColor: Int = 0
    private var unselectedStartColor: Int = 0
    private var unselectedEndColor: Int = 0
    private var selectState: Boolean = false

    init {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.GradientTextView, 0, 0)
        selectedStartColor = typedArray.getColor(
            R.styleable.GradientTextView_selected_start_color,
            ContextCompat.getColor(context, android.R.color.black) // Default start color
        )
        selectedEndColor = typedArray.getColor(
            R.styleable.GradientTextView_selected_end_color,
            ContextCompat.getColor(context, android.R.color.black) // Default end color
        )

        unselectedStartColor = typedArray.getColor(
            R.styleable.GradientTextView_unselected_start_color,
            ContextCompat.getColor(context, android.R.color.black) // Default start color
        )
        unselectedEndColor = typedArray.getColor(
            R.styleable.GradientTextView_unselected_end_color,
            ContextCompat.getColor(context, android.R.color.black) // Default end color
        )

        selectState = typedArray.getBoolean(
            R.styleable.GradientTextView_select_state,
            false // Default end color
        )
        typedArray.recycle()

    }

    /**
     * 切换select状态
     * */
    fun setSelectState(selectState: Boolean) {
        this.selectState = selectState
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        (paint as TextPaint).shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            0f,
            if (selectState) {
                intArrayOf(selectedStartColor, selectedEndColor)
            } else {
                intArrayOf(unselectedStartColor, unselectedEndColor)
            },
            null,
            Shader.TileMode.CLAMP
        )
        super.onDraw(canvas)
    }
}