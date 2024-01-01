package com.moder.compass.util

import com.dubox.drive.kernel.util.INT_1000
import java.text.DecimalFormat
import java.util.*


/**
 * Number工具类集合
 *
 * Created by zhouzhimin on 2022/8/17.
 */
private const val MAX_999: Int = 999

/**
 * 积分最大值
 */
const val MAX_999_9999: Int = 9_999_999

/**
 * 数字转String，>999的情况下显示999+
 */
fun max999(number: Number): String = if (number.toInt() > MAX_999) "999+" else number.toString()

/**
 * 格式化数字, 显示千分位，格式化最大值
 */
fun formatNumber(number: Number, max: Number): String {
    return if (number.toLong() > max.toLong()) {
        DecimalFormat.getNumberInstance().format(max) + "+"
    } else {
        DecimalFormat.getNumberInstance().format(number)
    }
}


private const val HUNDRED_THOUSAND = 100000
private const val ONE_THOUSAND = 1000
private const val TEN_THOUSAND = 10000

fun formatBigNumber(count: Long): String {
    return if (count < INT_1000) {
        count.toString()
    } else {
        val formatter = Formatter(Locale.ENGLISH)
        if (count < HUNDRED_THOUSAND) {
            formatter.format("%.1f", count.toDouble() / ONE_THOUSAND).toString() + "k"
        } else {
            formatter.format("%.1f", count.toDouble() / TEN_THOUSAND).toString() + "w"
        }
    }
}