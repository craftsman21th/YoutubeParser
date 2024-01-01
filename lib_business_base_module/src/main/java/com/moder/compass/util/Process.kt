package com.moder.compass.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process

/**
 * 是否是主进程
 */
fun isMainProcess(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
    val currentPid = Process.myPid()
    var currentProcessName = ""
    val l: MutableList<ActivityManager.RunningAppProcessInfo> = manager.runningAppProcesses ?: return false
    for (p in l) {
        if (p.pid == currentPid) {
            currentProcessName = p.processName
            break
        }
    }
    return context.packageName == currentProcessName
}

/**
 * 获取当前进程名称
 */
fun currentProcessName(context: Context): String {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager?: return ""
    val currentPid = Process.myPid()
    var currentProcessName = ""
    val l: MutableList<ActivityManager.RunningAppProcessInfo> = manager.runningAppProcesses?: return ""
    for (p in l) {
        if (p.pid == currentPid) {
            currentProcessName = p.processName
            break
        }
    }
    return currentProcessName
}
