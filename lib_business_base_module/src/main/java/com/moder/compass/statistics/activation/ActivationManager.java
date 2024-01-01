package com.moder.compass.statistics.activation;

import static com.moder.compass.base.utils.PersonalConfigKey.PHOTO_AUTO_BACKUP;
import static com.moder.compass.statistics.StatisticsKeysKt.LAUNCH_WITH_OPEN_AUTO_BACKUP;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.moder.compass.ActivityLifecycleManager;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.moder.compass.base.utils.PersonalConfigKey;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.TimeUtil;
import com.moder.compass.base.BackgroundWeakHelperKt;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.google.gson.Gson;
import com.moder.compass.statistics.EventStatisticsKt;
import com.moder.compass.statistics.EventTraceKt;
import com.moder.compass.statistics.StatisticsKeysKt;

import java.util.HashMap;

/***
 * 日活和APP激活统计管理 com.dubox.drive.util.DailyActiveManager
 *
 * @author yangqinghai <br/>
 * @time create at 2013-3-1 上午10:56:47
 */
public class ActivationManager {
    private static final String TAG = "DailyActiveManager";

    private static final String NOTIFICATION_SWITCH_ON = "1";
    private static final String NOTIFICATION_SWITCH_OFF = "0";

    /**
     * 网盘在前台时报活的后缀
     */
    private static final String SUFFIX_FOREGROUND = "_QIAN";
    /**
     * 网盘在后台时报活的后缀
     */
    private static final String SUFFIX_BACKGROUND = "_HOU";

    private static String sSourceUrl;

    /**
     * 文件夹自动备份日活
     */
    public static void reportFileBackUp() {
        ActivationManager.todayReport(
                StatisticsLogForMutilFields.StatisticsKeys.REPORT_USER_BACKGROUND_FILE_BACK_UP, true);
    }

    /***
     * 相册备份日活跃上报处理
     *
     * @author yangqinghai <br/>
     *         create at 2013-2-28 下午04:41:24
     */
    public static void reportAlbum() {
        todayReport(StatisticsLog.StatisticsKeys.REPORT_USER_BACKGROUND_ALBUMBACKUP, true);
    }

    /***
     * 上传下载日活跃上报处理
     *
     * @author yangqinghai <br/>
     *         create at 2013-2-28 下午04:41:24
     */
    public static void reportUpOrDownLoad() {
        todayReport(StatisticsLog.StatisticsKeys.REPORT_USER_BACKGROUND_UPLOAD_AND_DOWNLOAD, true);
    }

    /**
     * 上传日活上报
     */
    public static void reportUpload() {
        todayReport(StatisticsLog.StatisticsKeys.REPORT_USER_BACKGROUND_UPLOAD, true);
    }

    /**
     * 下载日活上报
     */
    public static void reportDownload() {
        todayReport(StatisticsLog.StatisticsKeys.REPORT_USER_BACKGROUND_DOWNLOAD, true);
    }

    /***
     * 发送前台用户日活跃
     *
     * @author yangqinghai <br/>
     *         create at 2013-3-4 上午11:16:11
     */
    public static void sendActiveUser(ResultReceiver receiver) {
        if (!TextUtils.isEmpty(sSourceUrl) && Account.INSTANCE.isLogin()) {
            PersonalConfig.getInstance().putString(PersonalConfigKey.KEY_REPORT_USER_SOURCE_URL, sSourceUrl);
            PersonalConfig.getInstance().asyncCommit();
            setSourceUrl(null);
        }
        todayReport(StatisticsLog.StatisticsKeys.REPORT_USER, false, receiver);
    }

    /**
     * 启动时需要统计相关活跃用户数的上报
     */
    public static void reportOnApplicationStart() {
        boolean photoAutoBackup = PersonalConfig.getInstance().getBoolean(PHOTO_AUTO_BACKUP, false);
        if (photoAutoBackup) {
            EventStatisticsKt.statisticActionEvent(LAUNCH_WITH_OPEN_AUTO_BACKUP);
        }
        reportPushData();
    }

    private static void reportPushData() {
        // 上报通知栏开关
        NotificationManagerCompat manager = NotificationManagerCompat.from(BaseApplication.getInstance());
        boolean isOpened = manager.areNotificationsEnabled();
        String notificationSwitch = NOTIFICATION_SWITCH_OFF;
        if (isOpened) {
            notificationSwitch = NOTIFICATION_SWITCH_ON;
        }
        StatisticsLogForMutilFields.getInstance().updateCount(
                StatisticsLogForMutilFields.StatisticsKeys.PUSH_NOTIFICATION_SWITCH_STATE, notificationSwitch);
    }


    /**
     * 发送今天日活跃统计，先与本地保存时间比较，不一致才发送
     *
     * @param keyDayType        :与本地保存的哪个时间比较
     * @param isCheckForeground 是否区分前后台
     * @author yangqinghai <br/>
     */
    private static void todayReport(String keyDayType, boolean isCheckForeground) {
        todayReport(keyDayType, isCheckForeground, null);
    }


    /**
     * 发送今天日活跃统计，先与本地保存时间比较，不一致才发送
     *
     * @param keyDayType        :与本地保存的哪个时间比较
     * @param isCheckForeground 是否区分前后台
     * @param receiver          上报结果回调
     * @author yangqinghai <br/>
     */
    private static void todayReport(String keyDayType, boolean isCheckForeground, ResultReceiver receiver) {
        boolean hasNetwork = ConnectivityState.isConnected(BaseApplication.getInstance());
        if (!hasNetwork) {
            return;
        }
        if (isCheckForeground) {
            if (ActivityLifecycleManager.isDuboxForeground()) {
                keyDayType = keyDayType + SUFFIX_FOREGROUND;
            } else {
                keyDayType = keyDayType + SUFFIX_BACKGROUND;
            }
        }
        long currentTime = System.currentTimeMillis();
        String day = TimeUtil.getCurrentDayTime(currentTime);
        String lastReportsDay = PersonalConfig.getInstance().getString(keyDayType);
        DuboxLog.d(TAG, "day::" + day + ":" + keyDayType + ":" + lastReportsDay);
        if (day.equals(lastReportsDay)) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("status", String.valueOf(PersonalConfig.getInstance().getInt(PersonalConfigKey.KEY_BAN_ADULT_SELECT_INDEX)));
        EventTraceKt.viewEventTrace(StatisticsKeysKt.CONTENT_PRF_STATUS, "", "", "", new Gson().toJson(map));
        ActivationServiceHelper.sendActive(BaseApplication.getInstance().getApplicationContext(), receiver, keyDayType);
        // 应用在后台时触发上报
        if (isCheckForeground && !ActivityLifecycleManager.isDuboxForeground()) {
            Intent intent = new Intent("action_background_today_report");
            intent.putExtra(Intent.EXTRA_REFERRER_NAME, keyDayType);
            LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(intent);
        }
        // 前台归因时上报埋点
        if (!isCheckForeground) {
            BackgroundWeakHelperKt.checkTimeInterval();
        }
    }


    /**
     * 客户端激活
     *
     * @param context create at 2013-3-4 下午06:25:59
     * @author yangqinghai <br/>
     */
    public static void sendAppActivate(Context context) {
        ActivationServiceHelper.sendAppActivate(context, null);
    }


    /**
     * 应用启动时，未登录状态时统计
     */
    public static void sendReportAnalyticsInLogout(Context context) {
        if (Account.INSTANCE.isLogin()) {
            DuboxLog.d(TAG, "账户已经登录，无需上报未登录日活");
            return;
        }
        ActivationServiceHelper.reportAnalyticsLogout(context);
    }

    /**
     * 日活用户上报时，新注册、登录用户，上报携带活动链接
     *
     * @param sourceUrl
     */
    public static void setSourceUrl(String sourceUrl) {
        sSourceUrl = sourceUrl;
    }
}
