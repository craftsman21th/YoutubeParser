package com.moder.compass.util

import android.content.res.Resources

/**
 * UI工具类
 *
 * Created by zhouzhimin on 2022/6/30.
 */

private const val CARRIER: Float = 0.5f

/**
 * dp转px
 */
fun dp2px(dp: Float): Int = (dp * Resources.getSystem().displayMetrics.density + CARRIER).toInt()