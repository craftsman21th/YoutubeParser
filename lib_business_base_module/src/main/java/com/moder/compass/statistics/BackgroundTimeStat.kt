package com.moder.compass.statistics

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import com.moder.compass.ActivityLifecycleManager
import com.moder.compass.AppVisibilityListener
import com.moder.compass.BaseApplication
import com.moder.compass.account.Account.isLogin
import com.moder.compass.extension.PendingIntentExt
import com.moder.compass.firebase.DuboxRemoteConfig
import com.mars.kotlin.extension.Tag
import com.mars.kotlin.extension.d
import com.mars.kotlin.extension.e

/**
 * @author : guoliang08
 * Create time : 2023/7/4 14:57
 * Description : 后台时长统计
 *
 * 采用Alarm Manager，用于统计后台运行时长,app退到后台之后每隔60s进行一次上报
 *
 * 不能使用USE_EXACT_ALARM权限,可以使用SCHEDULE_EXACT_ALARM . 官方文档:
 * https://support.google.com/googleplay/android-developer/answer/13161072
 *
 */
@Tag("BackgroundTimeStat")
object BackgroundTimeStat : AppVisibilityListener {

    private const val NEED_STAT_BACK_TIME = "need_stat_back_time"
    private var backgroundTime = 0L

    private val needStatBackgroundTime: Boolean
        get() = DuboxRemoteConfig.getBoolean(NEED_STAT_BACK_TIME)

    /**
     * 60 seconds
     */
    private const val STAT_DELAY_MILLIS: Long = 60000L
    private val alarmManager by lazy {
        BaseApplication.getContext().getSystemService(Service.ALARM_SERVICE) as? AlarmManager
    }
    private val timingPendingIntent by lazy {
        val intent = Intent(BaseApplication.getContext(), BackgroundStatAlarmReceiver::class.java)
        PendingIntentExt.getBroadcast(
            BaseApplication.getContext(),
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private var appIsVisible = true

    fun init() {
        "init needStatBackgroundTime=$needStatBackgroundTime ".d()
        if (!needStatBackgroundTime) {
            return
        }
        ActivityLifecycleManager.addOnAppVisibilityListener(this)
    }

    override fun onVisibilityChange(isVisible: Boolean) {
        appIsVisible = isVisible
        "onVisibilityChange isVisible=$isVisible needStatBackgroundTime=$needStatBackgroundTime ".d()
        if (!needStatBackgroundTime) {
            return
        }
        if (isVisible) {
            // 应用来到前台
            /*
            * 1. 开始报之前的时间到服务器
            * 2. 重置backgroundTime
            * 3. 停止计时
            * */
            reportBackgroundTime()
            backgroundTime = 0
            stopRecordTime()
        } else {
            // 应用退到后台
            /*
            * 1. 开始计时,每隔60s上报一次后台时长
            * */
            startRecordTime()
        }
    }

    private fun stopRecordTime() {
        kotlin.runCatching {
            alarmManager?.cancel(timingPendingIntent)
        }.onFailure {
            "stopRecordTime error $it".e()
        }
    }

    fun startRecordTime() {
        "startRecordTime  appIsVisible=$appIsVisible".d()
        if (appIsVisible) {
            return
        }

        "startRecordTime  开始计时".d()
        backgroundTime = System.currentTimeMillis()

        kotlin.runCatching {
            //先取消一次
            alarmManager?.cancel(timingPendingIntent)
            //再次启动,这里不延时，直接发送
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + STAT_DELAY_MILLIS, timingPendingIntent
                )
            } else {
                alarmManager?.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + STAT_DELAY_MILLIS,
                    timingPendingIntent
                )
            }
        }.onFailure {
            "startRecordTime error $it".e()
        }

        "startRecordTime backgroundTime=$backgroundTime".d()
    }

    fun reportBackgroundTime() {
        "reportBackgroundTime   backgroundTime=$backgroundTime".d()
        if (backgroundTime == 0L) {
            return
        }
        val isLogin = isLogin()
        val activeTime = (System.currentTimeMillis() - backgroundTime) / 1000
        "reportBackgroundTime isLogin=$isLogin  activeTime=$activeTime".d()
        if (isLogin && activeTime != 0L) {
            statisticActionEvent(APP_ACTION_TIME_BACKGROUND, activeTime.toString() + "")
            "上报后台存活时长 activeTime=$activeTime".d()
        }
        backgroundTime = System.currentTimeMillis()
    }

}