package com.mars.united.international.webplayer.parser.utils

import com.google.gson.*

/**
 * @Author 陈剑锋
 * @Date 2023/7/28-15:14
 * @Desc yt.config_ 解析工具
 *      可以将js中 yt.config_.param1.param2.param3 的调用方式
 *      转换为kotlin ytConfig["param1"]["param2"]["param3"] 的调用方式
 */
abstract class JsonCaller {

    lateinit var jsonEl: JsonElement

    companion object {
        fun create(rawInfo: String): JsonCaller? {
            return kotlin.runCatching {
                JsonParser().parse(rawInfo)?.let {
                    object : JsonCaller() {}.apply {
                        jsonEl = it
                    }
                }
            }.getOrNull()
        }

        fun create(json: JsonElement): JsonCaller {
            return object : JsonCaller() {}.apply {
                jsonEl = json
            }
        }

        /**
         * 创建 JsonObject 的调用封装
         * @param init [@kotlin.ExtensionFunctionType] Function1<JsonCaller, Unit>
         * @return JsonCaller
         */
        inline fun createObject(init: JsonCaller.() -> Unit = {}): JsonCaller {
            return object : JsonCaller() {}.apply {
                jsonEl = JsonObject()
                init.invoke(this)
            }
        }

        /**
         * 创建 JsonArray 的调用封装
         * @param init [@kotlin.ExtensionFunctionType] Function1<JsonCaller, Unit>
         * @return JsonCaller
         */
        inline fun createArray(init: JsonCaller.() -> Unit = {}): JsonCaller {
            return object : JsonCaller() {}.apply {
                jsonEl = JsonArray()
                init.invoke(this)
            }
        }
    }

    override fun toString(): String {
        return jsonEl.toString()
    }

}

operator fun JsonCaller?.get(key: Any): JsonCaller? {
    return runCatching {
        val value = when (key) {
            is String -> {
                this?.jsonEl?.asJsonObject?.get(key)
            }
            is Int -> {
                this?.jsonEl?.asJsonArray?.get(key)
            }
            else -> {
                throw IllegalArgumentException("arg 'key' only support String or Int")
            }
        }
        value?.let {
            object : JsonCaller() {}.apply { jsonEl = it }
        }
    }.getOrNull()
}

operator fun JsonCaller?.set(key: String, value: Any): JsonCaller? {
    this ?: return this
    runCatching {
        this.jsonEl.asJsonObject?.apply jsonObj@{
            if (this@jsonObj.has(key)) {
                remove(key)
            }
            when (value) {
                is JsonElement -> this@jsonObj.add(key, value)
                is Char -> this@jsonObj.addProperty(key, value)
                is Boolean -> this@jsonObj.addProperty(key, value)
                is Number -> this@jsonObj.addProperty(key, value)
                is String -> this@jsonObj.addProperty(key, value)
                is JsonCaller -> {
                    this@jsonObj.add(key, value.jsonEl)
                }
            }
        }
    }
    return this
}

fun JsonCaller?.add(value: Any): JsonCaller? {
    this ?: return this
    runCatching {
        asJsonArray?.apply jsonArr@{
            when (value) {
                is JsonElement -> this@jsonArr.add(value)
                is Char -> this@jsonArr.add(value)
                is Boolean -> this@jsonArr.add(value)
                is Number -> this@jsonArr.add(value)
                is String -> this@jsonArr.add(value)
                is JsonArray -> this@jsonArr.addAll(value)
                is JsonCaller -> {
                    this@jsonArr.add(value.jsonEl)
                }
            }
        }
    }
    return this
}

fun JsonCaller?.filter(onFilter: JsonCaller.() -> Boolean): JsonCaller? {
    this ?: return null
    runCatching {
        val filteredResultList = asJsonArray?.filter { item -> onFilter.invoke(JsonCaller.create(item)) }
        if (filteredResultList != null) {
            return object : JsonCaller() {}.apply {
                this.jsonEl = JsonArray().apply {
                    filteredResultList.forEach { filteredResult ->
                        add(filteredResult)
                    }
                }
            }
        }
    }
    return null
}

/**
 * 判断是否有包含某个字段的功能，支持多层查询
 * @receiver JsonCaller?
 * @param keys Any             需要查询的键，仅支持 Int 和 String （其它的字段也会转为String）
 * @return Boolean
 */
fun JsonCaller?.has(vararg keys: Any): Boolean {
    this ?: return false
    kotlin.runCatching {
        when {
            keys.isEmpty() -> {
                return false
            }
            keys.size == 1 -> {
                return this[keys[0]] != null
            }
            else -> {
                return this[keys[0]].has(keys.toList().subList(1, keys.size))
            }
        }
    }
    return false
}

/**
 * 遍历 Json 列表
 * @receiver JsonCaller?
 * @param onEach [@kotlin.ExtensionFunctionType] Function1<JsonCaller, Unit>
 */
inline fun JsonCaller?.forEach(onEach: JsonCaller.() -> Unit) {
    this ?: return
    runCatching {
        asJsonArray?.forEach { item ->
            onEach.invoke(JsonCaller.create(item))
        }
    }
}

fun JsonCaller?.map(onEach: JsonCaller.() -> JsonCaller?): JsonCaller? {
    this ?: return null
    runCatching {
        val mappedItems = asJsonArray?.mapNotNull { item -> JsonCaller.create(item).onEach()?.jsonEl }
        if (!mappedItems.isNullOrEmpty()) {
            return object : JsonCaller() {}.apply {
                this.jsonEl = JsonArray().apply {
                    mappedItems.forEach { mappedItem ->
                        add(mappedItem)
                    }
                }
            }
        }
    }
    return null
}

fun <T> JsonCaller?.mapAs(onEach: JsonCaller.() -> T?): List<T>? {
    this ?: return null
    runCatching {
        return asJsonArray?.mapNotNull { item -> JsonCaller.create(item).onEach() }
    }
    return null
}

val JsonCaller?.asLong: Long?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asLong
        }.getOrNull()
    }

val JsonCaller?.asString: String?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asString
        }.getOrNull()
    }

val JsonCaller?.asDouble: Double?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asDouble
        }.getOrNull()
    }

val JsonCaller?.asFloat: Float?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asFloat
        }.getOrNull()
    }

val JsonCaller?.asInt: Int?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asInt
        }.getOrNull()
    }

val JsonCaller?.asBoolean: Boolean?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asBoolean
        }.getOrNull()
    }

val JsonCaller?.asJsonArray: JsonArray?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asJsonArray
        }.getOrNull()
    }

val JsonCaller?.asJsonObject: JsonObject?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asJsonObject
        }.getOrNull()
    }

val JsonCaller?.asJsonPrimitive: JsonPrimitive?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asJsonPrimitive
        }.getOrNull()
    }

val JsonCaller?.asJsonNull: JsonNull?
    get() {
        return kotlin.runCatching {
            this?.jsonEl?.asJsonNull
        }.getOrNull()
    }