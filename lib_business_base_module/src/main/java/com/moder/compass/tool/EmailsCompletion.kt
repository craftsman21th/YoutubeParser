
package com.moder.compass.tool

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.MultiAutoCompleteTextView


/**
 * 邮箱匹配规则
 */
const val REG_EMAIL =
        ("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")

const val RETURN_CHAR: Char = '\n'
/**
 * 换行符Tokenizer
 */
class ReturnTokenizer : MultiAutoCompleteTextView.Tokenizer {
    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        var i = cursor
        while (i > 0 && text[i - 1] != RETURN_CHAR) {
            i--
        }
        return i
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        var i = cursor
        val len = text.length
        while (i < len) {
            if (text[i] == RETURN_CHAR) {
                return i
            } else {
                i++
            }
        }
        return len
    }

    override fun terminateToken(text: CharSequence): CharSequence {
        return "$text\n"
    }
}

/**
 * 特殊字符过滤
 */
class SpecialCharacterFilter : InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart:
    Int, dend: Int): CharSequence? {
        source?.let {
            var keepOriginal = true
            val sb = StringBuilder(end - start)
            for (i in start until end) {
                val c: Char = it[i]
                if (c != ' ')
                    sb.append(c) else keepOriginal = false
            }
            return@filter if (keepOriginal) null else {
                if (it is Spanned) {
                    val sp = SpannableString(sb)
                    TextUtils.copySpansFrom(it as Spanned?, start, sp.length, null, sp, 0)
                    sp
                } else {
                    sb
                }
            }
        }
        return null
    }
}

/**
 * 特殊字符过滤
 */
class CommaFilter : InputFilter {
    private val spChars = arrayOf(';', ',', '，', '；')
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart:
    Int, dend: Int): CharSequence? {
        source?.let {
            var keepOriginal = true
            val sb = StringBuilder(end - start)
            for (i in start until end) {
                var c: Char = it[i]
                if (c in spChars) {
                    c = RETURN_CHAR
                    sb.append(c)
                    keepOriginal = false
                } else {
                    sb.append(c)
                }
            }
            return@filter if (keepOriginal) null else {
                if (it is Spanned) {
                    val sp = SpannableString(sb)
                    TextUtils.copySpansFrom(it as Spanned?, start, end, null, sp, 0)
                    sp
                } else {
                    sb
                }
            }
        }
        return null
    }
}