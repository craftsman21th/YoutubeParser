package com.moder.compass.statistics

import com.mars.kotlin.extension.Tag
import com.mars.kotlin.extension.e

/**
 * Created by yeliangliang on 2021/5/7
 * @param other 额外的参数
 */
@Tag("PageDurationStatistics")
class PageDurationStatistics(
    private val pageTag: String,
    private val startKey: String,
    private val endKey: String,
    private val other: String = ""
) {

    private var startTime: Long = 0L

    /**
     * 页面开启
     */
    fun start() {
        startTime = System.currentTimeMillis()
        statisticActionEvent(startKey, pageTag, startTime.toString())
        "$this start".e()
    }

    /**
     * 页面销毁
     */
    fun end() {
        if (startTime == 0L) return
        statisticActionEvent(endKey, pageTag, startTime.toString(), (System.currentTimeMillis() - startTime)
            .toString(), other)
        "$this end: ".e()
        startTime = 0
    }
}