package com.moder.compass.firebase

import com.dubox.drive.kernel.architecture.AppCommon
import com.dubox.drive.kernel.architecture.config.DebugConfig
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.dubox.drive.kernel.util.NEGATIVE_1_L
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.JsonParser
import java.util.regex.Pattern

/**
 * FirebaseRemoteConfig的代理类
 * @author huping05
 * @since moder 2022/8/10
 */
object DuboxRemoteConfig {
    private val TRUE_REGEX = Pattern.compile("^(1|true|t|yes|y|on)$", Pattern.CASE_INSENSITIVE)
    private val FALSE_REGEX = Pattern.compile("^(0|false|f|no|n|off|)$", Pattern.CASE_INSENSITIVE)

    private const val DIFF_CHANNEL_CONFIG: String = "diff_channel_config"

    fun getFirebaseRemoteConfig(): FirebaseRemoteConfig? {
        return try {
            FirebaseRemoteConfig.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    fun getString(key: String): String {
        return getValueWithChannelCheck(
            key,
            FirebaseRemoteConfig.DEFAULT_VALUE_FOR_STRING
        )
    }

    fun getLong(key: String): Long {
        var configValue = getValueWithChannelCheck(key, "")
        if (configValue.isBlank()) {
            configValue = "${FirebaseRemoteConfig.DEFAULT_VALUE_FOR_LONG}"
        }
        return kotlin.runCatching { configValue.toLong() }.getOrDefault(NEGATIVE_1_L)
    }

    fun getDouble(key: String): Double {
        var configValue = getValueWithChannelCheck(key, "")
        if (configValue.isBlank()) {
            configValue = "${FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE}"
        }
        return configValue.toDouble()
    }

    fun getBoolean(key: String): Boolean {
        val configValue = getValueWithChannelCheck(
            key,
            "${FirebaseRemoteConfig.DEFAULT_VALUE_FOR_BOOLEAN}"
        )
        return TRUE_REGEX.matcher(configValue).matches()
    }

    /**
     * 获取配置值 并检查是否是分渠道配置
     * @param key String
     * @param defaultValue String
     * @return String
     */
    private fun getValueWithChannelCheck(key: String, defaultValue: String): String {
        val rawValue = if (shouldUsedLocalConfig(key)) {
            DebugConfig.getString(key, defaultValue)
        } else {
            getFirebaseRemoteConfig()?.getString(key) ?: defaultValue
        }
        return try {
            // 尝试解析分渠道配置
            val rootJson = JsonParser().parse(rawValue).asJsonObject
            if (rootJson.has(DIFF_CHANNEL_CONFIG)) {
                val diffConfigJson = rootJson.get(DIFF_CHANNEL_CONFIG).asJsonObject
                diffConfigJson.get(AppCommon.CHANNEL_NUM).toString()
            } else {
                rawValue
            }
        } catch (e: Throwable) {
            // 分渠道解析失败，则返回原值
            rawValue
        }
    }

    private fun shouldUsedLocalConfig(key: String): Boolean {
        return DuboxLog.isDebug() && DebugConfig.has(key)
    }
}