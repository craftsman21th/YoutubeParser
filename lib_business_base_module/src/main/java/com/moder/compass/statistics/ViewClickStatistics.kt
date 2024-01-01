package com.moder.compass.statistics

import com.mars.kotlin.extension.Tag

/**
 * Created by yeliangliang on 2021/12/7
 * @param maxRepeatedCount 去重上报判断的缓存数
 */
@Tag("ViewClickStatistics")
class ViewClickStatistics(private val op: String, private val maxCacheCount: Int = 10, private val maxRepeatedCount: Int = 1000) {

    private val ids = mutableListOf<String>()

    // 去重
    private val repeatedIds = LinkedHashMap<String, Int>(maxRepeatedCount)

    /**
     * 页面开启
     */
    fun view(id: String) {
        if (repeatedIds.contains(id)) return
        if (ids.size >= maxCacheCount) {
            end()
        }
        ids.add(id)
        repeatedIds[id] = 0
    }

    /**
     * 页面销毁
     */
    fun end() {
        if (ids.size >= 0) {
            StatisticsLogForMutilFields.getInstance()
                .updateCount(op, ids.joinToString("&"))
            ids.clear()
        }
    }
}