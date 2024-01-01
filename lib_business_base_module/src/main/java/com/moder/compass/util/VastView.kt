package com.moder.compass.util

import android.net.Uri
import com.dubox.drive.kernel.BaseShellApplication
import com.moder.compass.statistics.OPEN_FD_SECURITY_EXCEPTION
import com.moder.compass.statistics.statisticViewEvent
import com.mars.kotlin.extension.e
import com.media.vast.VastView
import java.io.File

/**
 * Created by yeliangliang on 2021/8/9
 */
fun VastView.setMediaUrl(str: String?) {
    str ?: return
    // 本地路径或者网络资源
    if (str.startsWith(File.separator) || str.startsWith("http", true) || str.startsWith("https", true)) {
        setFilePath(str)
        return
    }
    // uri
    try {
        val fd = BaseShellApplication.getContext().contentResolver.openFileDescriptor(Uri.parse(str), "r") ?: return
        setFileFd(fd.detachFd())
    } catch (e: Exception) {
        e.e()
        statisticViewEvent(OPEN_FD_SECURITY_EXCEPTION, str, "${e.message}")
        try {
            if (File(str).exists()) {
                setFilePath(str)
            }
        } catch (e: Exception) {
            e.e()
        }
    }
}