package com.moder.compass.extension

import com.google.gson.JsonObject

/**
 * @author chencaixing
 * @time 2020/8/4 上午11:08
 */
class JsonObjectScope(val json: JsonObject) {
    /**
     *
     */
    operator fun String.minus(value: Any?) {
        json.addValue(this, value)
    }
}

/**
 *
 */
fun JsonObject.addValue(key: String, data: Any?) {
    when (data) {
        null -> return
        is String -> addProperty(key, data)
        is Number -> addProperty(key, data)
        is Boolean -> addProperty(key, data)
        is Char -> addProperty(key, data)
    }
}

/**
 *
 */
operator fun JsonObject.invoke(init: JsonObjectScope.() -> Unit): JsonObject {
    init.invoke(JsonObjectScope(this))
    return this
}

/**
 *
 */
fun json(init: JsonObjectScope.() -> Unit): JsonObject {
    return JsonObject()(init)
}
