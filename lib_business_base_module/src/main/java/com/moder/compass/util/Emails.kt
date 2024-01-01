package com.moder.compass.util

import com.moder.compass.tool.REG_EMAIL
import java.util.regex.Matcher
import java.util.regex.Pattern

const val MAX_EMAIL_LENGTH: Int = 254
const val EMAIL_AT_SYMBOL = "@"

/**
 *
 * @author huping05
 * @since moder 2023/1/16
 */
fun isEmailRegexValid(it: String): Boolean {
    if (!it.contains(EMAIL_AT_SYMBOL)) {
        return false
    }
    // 国际标准为254字符
    if (it.length > MAX_EMAIL_LENGTH) {
        return false
    }
    val pattern = Pattern.compile(REG_EMAIL, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(it)
    return matcher.matches()
}