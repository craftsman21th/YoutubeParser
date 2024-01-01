package com.moder.compass.statistics

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dubox.drive.kernel.architecture.debug.DuboxLog

/**
 * @author : guoliang08
 * Create time : 2023/7/5 10:42
 * Description : 后台存活时长统计 定时 Receiver
 */
class BackgroundStatAlarmReceiver :BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        DuboxLog.d("BackgroundTimeStat","BackgroundStatAlarmReceiver 计时时间到了")
        BackgroundTimeStat.reportBackgroundTime()
        BackgroundTimeStat.startRecordTime()
    }
}