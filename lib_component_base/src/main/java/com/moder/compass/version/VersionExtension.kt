package com.moder.compass.version

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.architecture.AppCommon
import com.dubox.drive.kernel.architecture.debug.DuboxBetaController

/**
 * 获取版本号的高两位
 * 例：3.5.5 -> 3.5
 */
fun getBigVersion(version: String?): String? {
    if (version == null || TextUtils.isEmpty(version)) {
        return null
    }
    val vReal = version.replace("-.*".toRegex(), "")
    val splits = vReal.split("\\.".toRegex()).toTypedArray()
    return if (splits != null && splits.size > 2) {
        splits[0] + "." + splits[1]
    } else null
}

/**
 * 获取版本信息 eg:v3.5.5.5
 *
 * @return
 */
fun getVersionString(context: Context, version: String = AppCommon.VERSION_DEFINED): Spanned? {
    var version = context.getString(R.string.settings_version_num, version)
    if (DuboxBetaController.isBeta) {
        version = context.getString(R.string.beta_string) + version
    }
    return Html.fromHtml(version)
}