package com.moder.compass.statistics

import androidx.annotation.Keep
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.google.gson.JsonObject

/**
 * @Author 陈剑锋
 * @Date 2023/4/6-15:48
 * @Desc 埋点相关
 */


/**
 * 埋点参数封装
 */
@Keep
class EventTraceParamsWrapper {

    val paramsMap: HashMap<String, String> = hashMapOf()

    /**
     * 添加参数
     * @receiver String
     * @param value Any
     */
    infix fun String.to(value: Any) {
        paramsMap[this] = value.toString()
    }

}

/**
 * 点击事件埋点
 * @param key String
 * @param page String
 * @param module String
 * @param source String
 * @param paramsScope [@kotlin.ExtensionFunctionType] Function1<EventTraceParamsWrapper, Unit>
 */
@JvmOverloads
fun clickEventTrace(
    key: String,
    page: String = NullStr,
    module: String = NullStr,
    source: String = NullStr,
    paramsScope: EventTraceParamsWrapper.() -> Unit
) {
    eventTrace(
        ACTION_EVENT_KEY, key, page, module, source,
        EventTraceParamsWrapper().apply(paramsScope).paramsMap
    )
}

/**
 * 曝光事件埋点
 * @param key String
 * @param page String
 * @param module String
 * @param source String
 * @param paramsScope [@kotlin.ExtensionFunctionType] Function1<EventTraceParamsWrapper, Unit>
 */
@JvmOverloads
fun viewEventTrace(
    key: String,
    page: String = NullStr,
    module: String = NullStr,
    source: String = NullStr,
    paramsScope: EventTraceParamsWrapper.() -> Unit
) {
    eventTrace(
        VIEW_EVENT_KEY, key, page, module, source,
        EventTraceParamsWrapper().apply(paramsScope).paramsMap
    )
}

/**
 * 曝光事件埋点
 * @param key String
 * @param page String
 * @param module String
 * @param source String
 * @param paramsJson String
 */
@JvmOverloads
fun viewEventTrace(
    key: String,
    page: String = NullStr,
    module: String = NullStr,
    source: String = NullStr,
    paramsJson: String = ""
) {
    runCatching {
        logEventInner(VIEW_EVENT_KEY, key, page, module, source, paramsJson)
    }.onFailure {
        if (DuboxLog.isDebug()) {
            throw it
        }
    }
}

/**
 * 埋点封装
 * @param eventType String
 * @param key String
 * @param page String
 * @param module String
 * @param source String
 * @param map Map<String, String>
 */
@JvmOverloads
fun eventTrace(
    eventType: String,
    key: String,
    page: String = NullStr,
    module: String = NullStr,
    source: String = NullStr,
    map: Map<String, String>
) {
    //将kv参数map 转换为 json字符串
    val paramsJsonStr = if (map.isEmpty()) {
        NullStr
    } else {
        JsonObject().apply {
            map.forEach { kv ->
                addProperty(kv.key, kv.value)
            }
        }.toString()
    }
    runCatching {
        logEventInner(eventType, key, page, module, source, paramsJsonStr)
    }.onFailure {
        if (DuboxLog.isDebug()) {
            throw it
        }
    }
}