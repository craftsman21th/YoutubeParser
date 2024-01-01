package com.moder.compass.base

import com.moder.compass.ActivityLifecycleManager.isDuboxForeground
import com.moder.compass.BaseApplication
import com.moder.compass.base.utils.PersonalConfigKey.KEY_KEEP_ACTIVE_NOTIFICATION_REPORT_TIME
import com.dubox.drive.kernel.architecture.config.PersonalConfig
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.dubox.drive.kernel.util.TIME_UNIT_1000
import com.dubox.drive.kernel.util.TimeUtil
import com.moder.compass.statistics.KEEP_ACTIVE_NOTIFICATION_APPEAR
import com.moder.compass.statistics.KEEP_ACTIVE_NOTIFICATION_BACKGROUND_REPORT
import com.moder.compass.statistics.KEY_REPORT_USER_START_SOURCE
import com.moder.compass.statistics.RECORD_FOREGROUND_LAUNCH_SOURCE_AND_TIME
import com.moder.compass.statistics.statisticActionEvent
import com.moder.compass.util.isMainProcess

/**
 * @author sunmeng12
 * @since moder 2022/5/25
 * 当前进程启动的原因
 */
private var startLaunchSource: String = ""

/**
 * 上次记录时间
 */
private var startLaunchTime: Long = 0L

/**
 * 针对后台启动进程切换
 * @param source 进程启动方式
 *
 * 1、前台启动且没有退出到后台的情况下不允许更新启动来源
 *
 * 2、后台启动统计规则如下：
 *   1）、AccountChangeHandler 中会注册账号保活库，同时回调启动的结果；如果是其他方式导致的拉活比如 Push/ Widget 会导致启动原因的失真
 *   2）、处理上述情况的方式是记录一个时间，30秒内允许更新启动方式，否则认为进程已经启动不在更新启动方式
 *
 * 3、补充规则：经过多次测试发现，存在先启动目标 service 再回调job_schedule的情况，所以暂时对 job_schedule 做一次过滤
 */
fun changeStartSource(source: String) {
    DuboxLog.d(TAG, "change start source $source")

    // 非主进程,则肯定是 多进程守护
    if (!isMainProcess(BaseApplication.getContext())) {
        DuboxLog.d(TAG, "前面   changeStartSource startLaunchSource = source:$source   更新startLaunchSource成功")
        startLaunchSource = BACKGROUND_START_SOURCE_DUAL_PROCESS
        PersonalConfig.getInstance().putString(KEY_REPORT_USER_START_SOURCE, startLaunchSource)
        return
    }
    if (startLaunchSource == source) {
        return
    }
    // 前台启动且未退到后台的情况下，不更新
    if (startLaunchSource == FOREGROUND_START_SOURCE_FRONTDESK && isDuboxForeground()) {
        return
    }
    // 检查时间间隔，前台优先级更高，可以直接更新
    val isForeground: Boolean = source == FOREGROUND_START_SOURCE_FRONTDESK
    val isJobSchedule: Boolean = source == BACKGROUND_START_SOURCE_JOB_SCHEDULE
    val currTime = System.currentTimeMillis()
    if (startLaunchTime == 0L) {
        startLaunchTime = currTime
    }

    // 进程已启动则超过 30秒 后不允许更新启动
    if (currTime - startLaunchTime > TIME_INTERVAL && !isForeground) {
        return
    }

    // JobSchedule 回调覆盖问题
    if (startLaunchSource.isNotEmpty() && isJobSchedule) {
        return
    }

    DuboxLog.d(TAG, "changeStartSource startLaunchSource = source:$source   更新startLaunchSource成功")
    startLaunchSource = source
    PersonalConfig.getInstance().putString(KEY_REPORT_USER_START_SOURCE, startLaunchSource)
}

/**
 * 存在新登陆用户，startSource 没有保存到 PersonalConfig 中的情况
 */
fun checkSourceAndUpdate(): String {
    if (startLaunchSource.isNotEmpty()) {
        return startLaunchSource
    }

    if (isDuboxForeground()) {
        startLaunchSource = FOREGROUND_START_SOURCE_FRONTDESK
        return startLaunchSource
    }
    return BACKGROUND_START_SOURCE_ACCOUNT_SYNC
}

private const val TAG: String = "BackgroundWeak"

/**
 * 更新启动方式的时间间隔
 */
private const val TIME_INTERVAL: Long = 30 * TIME_UNIT_1000

/**
 * 多进程更新启动方式的时间间隔
 */
private const val DUAL_PROCESS_TIME_INTERVAL: Long = 5 * TIME_UNIT_1000

/**
 * 启动来源：前台
 */
const val FOREGROUND_START_SOURCE_FRONTDESK: String = "frontdesk"

/**
 * 启动来源：账号同步
 */
const val BACKGROUND_START_SOURCE_ACCOUNT_SYNC: String = "account_sync"

/**
 * 启动来源： jobSchedule
 */
const val BACKGROUND_START_SOURCE_JOB_SCHEDULE: String = "job_schedule"

/**
 * 启动来源： Push
 */
const val BACKGROUND_START_SOURCE_PUSH_WEAK: String = "push_weak"

/**
 * 启动来源： Widget
 */
const val BACKGROUND_START_SOURCE_WIDGET_UPDATE: String = "widget_update"

/**
 * 启动来源： 系统唤醒
 */
const val BACKGROUND_START_SOURCE_SYSTEM_UPDATE: String = "system_update"

/**
 * 启动来源： 内容提供
 */
const val BACKGROUND_START_SOURCE_CONTENT_PROVIDER: String = "content_provider"

/**
 * 启动来源:  多进程守护
 */
const val BACKGROUND_START_SOURCE_DUAL_PROCESS: String = "dual_process"


/*******************************常驻通知栏*************************************/

/**
 * 上报常驻通知栏展现
 */
fun reportKeepActiveNotification() {
    val lastTime = PersonalConfig.getInstance().getLong(KEY_KEEP_ACTIVE_NOTIFICATION_REPORT_TIME)
    val currentTime = System.currentTimeMillis()
    DuboxLog.d(
        TAG, "reportKeepActiveNotification " +
            "lastTime ${TimeUtil.getCurrentDayTime(lastTime)} " +
            "currentTime ${TimeUtil.getCurrentDayTime(currentTime)}")
    // 一天只上报一次
    if (TimeUtil.isTheSameDay(lastTime, currentTime)) {
        return
    }

    PersonalConfig.getInstance().putLong(KEY_KEEP_ACTIVE_NOTIFICATION_REPORT_TIME, currentTime)
    statisticActionEvent(KEEP_ACTIVE_NOTIFICATION_APPEAR)
}

/**
 * 后台报活时判断当前上报时间是否为进程启动60秒后，如果是则认为是常驻通知栏保活导致的后台保活
 */
fun todayReportWithKeepActive(action: String) {
    val currentTime = System.currentTimeMillis()
    DuboxLog.d(
        TAG, "todayReportWithKeepActive " +
            "startLaunchTime $startLaunchTime " +
            "currentTime $currentTime")
    if (currentTime - startLaunchTime > TimeUtil.ONEMINUTE) {
        statisticActionEvent(KEEP_ACTIVE_NOTIFICATION_BACKGROUND_REPORT, action)
    }
}


/*******************************@since moder 3.0.0 前台报活埋点*************************************/

/**
 * 记录前台归因方式
 */
private var foregroundSource: String = "Undefine"

/**
 * 记录前台归因埋点时间，可能会比实际上报的时间要早，目的在于分析时间的间隔范围
 */
private var foregroundTime: Long = 0L

/**
 * 记录前台归因上报埋点
 */
fun recordForegroundLaunch(source: String) {
    foregroundSource = source
    foregroundTime = System.currentTimeMillis()
}

/**
 * 记录前台归因上报时和埋点的时间间隔
 * 1、统计埋点数和前台归因接口上报数的差值，找到埋点可能导致的数据丢失占比
 * 2、找到时间超过 60s 的占比
 */
fun checkTimeInterval() {
    val intervalTime = System.currentTimeMillis() - foregroundTime
    val isOver60Second = intervalTime > TimeUtil.ONEMINUTE
    statisticActionEvent(RECORD_FOREGROUND_LAUNCH_SOURCE_AND_TIME, foregroundSource, isOver60Second.toString())
}
