package com.moder.compass.ui.widget.progressbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.util.CONSTANT_0
import com.dubox.drive.kernel.util.CONSTANT_2
import com.dubox.drive.kernel.util.INT_100
import com.dubox.drive.kernel.util.TIME_UNIT_1000

const val DEFAULT_STROKE_WIDTH: Int = 50
const val DEFAULT_FLOAT_ZERO: Float = 0F
const val DEFAULT_MAX_PROGRESS: Float = 100F
const val DEFAULT_START_ANGLE: Float = 135F
const val DEFAULT_ANGLE_SIZE: Float = 270F
const val CIRCLE_ANGLE: Float = 360F

/**
 * @author chenbin18
 * create at 2022/8/11
 *
 * 可以设置角度的圆弧进度条
 */
class ArcProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /**
     * 圆弧的宽度
     */
    var strokeWidth = DEFAULT_STROKE_WIDTH

    /**
     * 圆弧开始的角度  角度的计算是以正x轴为起点，顺时针旋转
     */
    var startAngle = DEFAULT_START_ANGLE

    /**
     * 起点角度和终点角度对应的夹角大小
     */
    var angleSize = DEFAULT_ANGLE_SIZE

    /**
     * 最大的进度，用于计算进度与夹角的比例
     */
    var maxProgress = DEFAULT_MAX_PROGRESS

    /**
     * 当前进度
     */
    var targetProgress = DEFAULT_FLOAT_ZERO
        set(progress) {
            if (progress <= CONSTANT_0) {
                return
            }
            if (progress > maxProgress) {
                field = maxProgress
            }
            field = progress
            startAnimator(target = (field / maxProgress * INT_100).toInt())
        }

    /**
     * 动画的执行时长
     */
    var animDuration: Long = TIME_UNIT_1000

    /**
     * 圆弧背景颜色
     */
    var arcBgColor = Color.BLUE

    /**
     * 进度圆弧渐变色的起始颜色,即左边的颜色
     */
    var progressStartColor = Color.WHITE

    /**
     * 进度圆弧渐变色的结束颜色
     */
    var progressEndColor = Color.BLUE

    var onProgressChangeListener: ((Int) -> Unit)? = null

    /**
     * 当前进度对应的起点角度到当前进度角度夹角的大小
     */
    private var currentAngleSize = DEFAULT_FLOAT_ZERO

    private var valueAnimator: ValueAnimator? = null

    private val bgPaint by lazy {
        Paint().also {
            it.style = Paint.Style.STROKE
            it.strokeWidth = strokeWidth.toFloat()
            //抗锯齿
            it.isAntiAlias = true
            it.color = arcBgColor
            it.strokeCap = Paint.Cap.ROUND
        }
    }

    private val progressPaint by lazy {
        Paint().also {
            it.style = Paint.Style.STROKE
            it.strokeWidth = strokeWidth.toFloat()
            it.isAntiAlias = true
            it.strokeCap = Paint.Cap.ROUND
        }
    }

    private val colorArray by lazy { intArrayOf(progressEndColor, progressStartColor, progressEndColor) }
    private val positionArray by lazy {
        val endAngle = targetProgress / maxProgress * DEFAULT_ANGLE_SIZE + startAngle
        // 大于360°的部分还是endColor
        floatArrayOf(DEFAULT_FLOAT_ZERO, startAngle / CIRCLE_ANGLE, endAngle / CIRCLE_ANGLE)
    }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar)
        maxProgress =
            array.getFloat(R.styleable.ArcProgressBar_arc_max_progress, DEFAULT_MAX_PROGRESS)
        targetProgress =
            array.getFloat(R.styleable.ArcProgressBar_arc_target_progress, DEFAULT_FLOAT_ZERO)
        arcBgColor = array.getColor(R.styleable.ArcProgressBar_arc_bg_color, Color.YELLOW)
        progressStartColor =
            array.getColor(R.styleable.ArcProgressBar_arc_progress_start_color, Color.WHITE)
        progressEndColor =
            array.getColor(R.styleable.ArcProgressBar_arc_progress_end_color, Color.BLUE)
        strokeWidth = array.getDimensionPixelOffset(
            R.styleable.ArcProgressBar_arc_stroke_width,
            DEFAULT_STROKE_WIDTH
        )
        startAngle = array.getFloat(R.styleable.ArcProgressBar_arc_start_angle, DEFAULT_START_ANGLE)
        angleSize = array.getFloat(R.styleable.ArcProgressBar_arc_angle_size, DEFAULT_ANGLE_SIZE)
        array.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rectF = RectF(
            strokeWidth.toFloat(), strokeWidth.toFloat(),
            (width - strokeWidth).toFloat(),
            (width - strokeWidth).toFloat()
        )
        drawArcBg(canvas, rectF)
        drawArcProgress(canvas, rectF)
        canvas.drawArc(rectF, startAngle, currentAngleSize, false, progressPaint)
    }

    /**
     * 画背景的圆弧
     */
    private fun drawArcBg(canvas: Canvas, rectF: RectF) {
        canvas.drawArc(rectF, startAngle, angleSize, false, bgPaint)
    }

    /**
     * 画进度的圆弧
     */
    private fun drawArcProgress(canvas: Canvas, rectF: RectF) {
        progressPaint.shader = SweepGradient(
            (rectF.left + rectF.right) / CONSTANT_2, (rectF.top + rectF.bottom) / CONSTANT_2,
            colorArray, positionArray
        )
        canvas.drawArc(rectF, startAngle, currentAngleSize, false, progressPaint)
    }

    /**
     * 开启动画
     *
     * @param start  开始位置占比
     * @param target 结束位置占比
     */
    private fun startAnimator(start: Int = CONSTANT_0, target: Int) {
        if (valueAnimator?.isRunning == true) {
            return
        }
        valueAnimator = ValueAnimator.ofInt(start, target)
        valueAnimator?.let {
            it.duration = animDuration
            it.addUpdateListener {
                currentAngleSize = angleSize / INT_100 * it.animatedValue.toString().toInt()
                onProgressChangeListener?.invoke(it.animatedValue as Int)
                invalidate()
            }
            it.start()
        }
    }
}