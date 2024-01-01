package com.moder.compass.statistics

import android.annotation.SuppressLint
import com.appsflyer.AppsFlyerLib
import com.moder.compass.BaseApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.mars.kotlin.extension.Tag

/**
 * 统计的参数Key
 */
private val STATISTIC_PARAMS = arrayOf("other0", "other1", "other2", "other3", "other4", "other5")

/**
 * 用户画像上报
 */
@Tag("UserFeatureReporter")
class UserFeatureReporter(private val key: String, private vararg val other: String) {
    /**
     * 上报af
     */
    fun reportAF() {
        val map = mutableMapOf<String,Any>().apply {
            other.take(STATISTIC_PARAMS.size).forEachIndexed { index, value ->
                // 核心业务上报，参数带source前缀
                this[STATISTIC_PARAMS[index]] = value
            }
        }
        AppsFlyerLib.getInstance().logEvent(BaseApplication.getInstance(), key, map)
//        "reportAF key=$key other=${other.joinToString()}".d()
    }

    /**
     * 上报firebase一级key
     * 有500个key的数量限制，没有组内review过的禁止调用该函数上报
     */
    @SuppressLint("MissingPermission")
    fun reportFirebaseLevel1() {
        FirebaseAnalytics.getInstance(BaseApplication.getInstance()).logEvent(key) {
            other.take(STATISTIC_PARAMS.size).forEachIndexed { index, value ->
                // 核心业务上报，参数带source前缀
                this.param(STATISTIC_PARAMS[index], value)
            }
        }
//        "reportFirebaseLevel1 key=$key other=${other.joinToString()}".d()
    }

    /**
     * warning ！！！ 有500个key的数量限制，没有组内review过的禁止调用该函数上报
     * 上报af 和 firebase一级key
     */
    fun reportAFAndFirebase() {
        reportAF()
        reportFirebaseLevel1()
    }
}