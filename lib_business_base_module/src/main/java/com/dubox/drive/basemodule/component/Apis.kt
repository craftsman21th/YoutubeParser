package com.dubox.drive.basemodule.component

import com.moder.compass.statistics.StatisticsLogForMutilFields
import com.rubik.annotations.route.RRoute

/**
 * Created by yeliangliang on 2020/12/9
 */
@RRoute(path = "stats/multi/fields/update/count")
fun statsMultiFieldsUpdateCount(op: String, other: Array<String>?) {
    other?.let {
        StatisticsLogForMutilFields.getInstance().updateCount(op, *it)
        return
    }
    StatisticsLogForMutilFields.getInstance().updateCount(op)
}