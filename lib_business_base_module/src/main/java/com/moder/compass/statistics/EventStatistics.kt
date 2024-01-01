package com.moder.compass.statistics

import android.annotation.SuppressLint
import android.os.Bundle
import com.dubox.drive.basemodule.BuildConfig
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.mars.kotlin.extension.d

private const val TAG = "EventStatistics"

/**
 * 点击事件类型统计
 */
internal const val ACTION_EVENT_KEY = "dubox_event_name_action"

/**
 * 展示事件类型统计
 */
internal const val VIEW_EVENT_KEY = "dubox_event_name_view"

/**
 * 业务真实统计Key
 */
private const val EVENT_STATISTIC_KEY = "dubox_source"

/**
 * 统计的参数Key
 */
private val STATISTIC_PARAMS = arrayOf(
    "dubox_other0", "dubox_other1", "dubox_other2",
    "dubox_other3", "dubox_other4", "dubox_other5"
)

/**
 * 上报点击事件
 * [key] 业务Key [other] 参数，最多6个
 */
@JvmOverloads
fun statisticActionEvent(key: String, vararg other: String = emptyArray()) {
    runCatching {
        logEventInner(ACTION_EVENT_KEY, key, *other)
    }.onFailure {
        if (BuildConfig.DEBUG) {
            throw it
        }
    }
}

/**
 * 上报展示事件
 * [key] 业务Key [other] 参数，最多6个
 */
@JvmOverloads
fun statisticViewEvent(key: String, vararg other: String = emptyArray()) {
    runCatching {
        logEventInner(VIEW_EVENT_KEY, key, *other)
    }.onFailure {
        if (BuildConfig.DEBUG) {
            throw it
        }
    }
}

/**
 * 上报点击事件 - 立即上报，注意频繁调用此方法会影响性能
 * [key] 业务Key [other] 参数，最多6个
 */
@JvmOverloads
fun statisticActionEventNow(key: String, vararg other: String = emptyArray()) {
    runCatching {
        logEventInnerNow(ACTION_EVENT_KEY, key, *other)
    }.onFailure {
        if (BuildConfig.DEBUG) {
            throw it
        }
    }
}

/**
 * 兼容老的上报，新增打点不允许使用
 * @see statisticActionEvent
 * @see statisticViewEvent
 */
@Deprecated("don't use for new statistic event!")
@JvmOverloads
fun statisticDeprecatedEvent(key: String, params: Bundle? = null, vararg other: String = emptyArray()) {
    runCatching {
        logEventInnerDeprecated(key, params, *other)
    }.onFailure {
        if (BuildConfig.DEBUG) {
            throw it
        }
    }
}

@SuppressLint("MissingPermission")
internal fun logEventInner(eventKey: String, source: String, vararg other: String) {
//    FirebaseAnalytics.getInstance(BaseApplication.getInstance())
//            .logEvent(eventKey) {
//                param(EVENT_STATISTIC_KEY, source)
//                other.take(STATISTIC_PARAMS.size).forEachIndexed { index, value ->
//                    // 核心业务上报，参数带source前缀
//                    this.param(STATISTIC_PARAMS[index], "${source}_${value}")
//                }
//            }
    StatisticsLogForMutilFields.getInstance().updateCount(source, *other)
    ("logEventInner eventKey:$eventKey source:$source other:${other.joinToString()}").d(TAG)
}

@SuppressLint("MissingPermission")
private fun logEventInnerNow(eventKey: String, source: String, vararg other: String) {
//    FirebaseAnalytics.getInstance(BaseApplication.getInstance())
//        .logEvent(eventKey) {
//            param(EVENT_STATISTIC_KEY, source)
//            other.take(STATISTIC_PARAMS.size).forEachIndexed { index, value ->
//                // 核心业务上报，参数带source前缀
//                this.param(STATISTIC_PARAMS[index], "${source}_${value}")
//            }
//        }
    StatisticsLogForMutilFields.getInstance()
        .updateCountNow(source, *other)
    if (DuboxLog.isDebug()) {
        kotlin.runCatching {
            "logEventInner eventKey:$eventKey source:$source other:${other.joinToString()}".d(TAG)
        }.onFailure {
            it.printStackTrace()
        }
    }
}

@SuppressLint("MissingPermission")
private fun logEventInnerDeprecated(key: String, params: Bundle?, vararg other: String) {
//    FirebaseAnalytics.getInstance(BaseApplication.getInstance())
//            .logEvent(key, params)
    StatisticsLogForMutilFields.getInstance().updateCount(key, *other)
    if (DuboxLog.isDebug()) {
        "logEventInnerDeprecated eventKey:$key source:$params other:${other.joinToString()}".d(TAG)
    }
}
