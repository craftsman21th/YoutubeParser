package com.mars.united.international.webplayer.parser.utils

import android.content.ContextWrapper
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.dubox.drive.kernel.util.INT_0
import com.dubox.drive.kernel.util.INT_2
import com.mars.united.international.webplayer.parser.TAG
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest
import java.util.*

/**
 * @Author 陈剑锋
 * @Date 2023/7/28-10:22
 * @Desc
 */

/**
 * 判断时间是不是当日
 * @param timeStamp Long
 * @return Boolean
 */
fun isToday(timeStamp: Long): Boolean {
    val todayCalendar = Calendar.getInstance()
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return if (calendar[1] == todayCalendar[1]) todayCalendar[6] == calendar[6] else false
}

/**
 * 判断 关键参数 是否合法
 * @receiver String
 * @return Boolean
 */
internal fun String?.checkKeyParamsValid(): Boolean {
    return (!this.isNullOrBlank()
            && this.trim() != "null"
            && this.trim() != "undefined")
}

/**
 * 执行js
 * @param command String
 */
fun executeJs(command: String): String {
    return kotlin.runCatching {
        val jsContext = Context.enter().apply {
            optimizationLevel = -1
        }
        val jsScope: Scriptable = jsContext.initStandardObjects()
        val result = jsContext.evaluateString(
            jsScope,
            command,//or "(function(){return (${command})})();"
            "<cmd>",
            1,
            null
        ).toString()
        Log.e(TAG, "executeJs: OK\t${result}")
        result
    }.getOrElse {
        Log.e(TAG, "executeJs: ERROR\t${it.message}")
        ""
    }
}


/*
字符串转时间秒
 */
fun String.time2Seconds(): Long {
    return this.split(":").let {
        kotlin.runCatching {
            when (it.size) {
                1 -> it[0].toLong()
                2 -> it[0].toLong() * 60 + it[1].toLong()
                3 -> it[0].toLong() * 60 * 60 + it[1].toLong() * 60 + it[2].toLong()
                else -> 0L
            }
        }.getOrElse { 0L }
    }
}

/**
 * 根据raw资源id读取文件内容
 * @param context ContextWrapper
 * @param rawId Int
 * @return String
 */
@VisibleForTesting
fun readTextByRawId(context: ContextWrapper, rawId: Int): String {
    val inputStream = context.resources.openRawResource(rawId)
    inputStream.use {
        try {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            return bufferedReader.readLines().joinToString("\n")
        } catch (e: Exception) {
            return ""
        }
    }
}


/**
 * DigestAlgorithm 可以是下述摘要算法的任意一种
 * MD2 MD5 SHA-1(SHA) SHA-256 SHA-384 SHA-512
 */
const val KEY_SHA = "SHA-1"
const val XFF = 0xFF

/**
 * 计算String的sha1值
 * @receiver String
 * @return String
 */
fun String.sha1(): String {
    kotlin.runCatching {
        val digest = MessageDigest.getInstance(KEY_SHA).apply {
            update(this@sha1.toByteArray())
        }.digest()
        val hexstr = StringBuffer()
        var shaHex = ""
        for (i in digest.indices) {
            shaHex = Integer.toHexString(digest[i].toInt() and XFF)
            if (shaHex.length < INT_2) {
                hexstr.append(INT_0)
            }
            hexstr.append(shaHex)
        }
        return hexstr.toString()
    }
    return ""
}