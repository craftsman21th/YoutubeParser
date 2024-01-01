package com.moder.compass.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.moder.compass.component.base.BuildConfig
import com.moder.compass.component.base.R
import com.dubox.drive.kernel.util.ToastHelper
import com.mars.kotlin.extension.e

/**
 * Created by yeliangliang on 2021/7/9
 */
class ReportAbuser {

    /**
     * 上报
     * */
    fun report(activity: Activity, id: String, link: String) {
        val mailList = arrayOf(BuildConfig.FEEDBACK_EMAIL)
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, mailList)
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.copyright_report))
        intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.copyright_report_content, id, link).trimIndent())
        // 正文
        try {
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.e()
            ToastHelper.showToast(R.string.report_failed_not_install_email_app)
        }
    }
}