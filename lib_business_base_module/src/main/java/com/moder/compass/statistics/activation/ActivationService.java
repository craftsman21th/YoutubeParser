package com.moder.compass.statistics.activation;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.dubox.drive.base.service.BaseScheduledService;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;

/**
 * 设备激活相关接口调度Service
 * 
 * @author tianzengming<br/>
 *         Created by tianzengming on 2015/8/27.
 */
public class ActivationService extends BaseScheduledService {
    private static final String TAG = "ActiveService";

    /**
     * 发送APP激活 YQH 20130226
     */
    static final String ACTION_SEND_APP_ACTIVATE = "com.dubox.drive.ACTION_SEND_APP_ACTIVATE";

    /**
     * 发送日活跃 YQH 20130226
     */
    public static final String ACTION_SEND_ACTIVE = "com.dubox.drive.ACTION_SEND_ACTIVE";

    /**
     * >>>>>>> remotes/origin/dev 日活跃actionType YQH 20130226
     */
    public static final String EXTRA_ACTIVE_ACTION_TYPE = "com.dubox.drive.extra.ACTIVE_ACTION_TYPE";

    /**
     * 应用启动时，未登录状态时统计
     */
    static final String ACTION_REPORT_LOGOUT_ANALYTICS = "com.dubox.drive.ACTION_REPORT_LOGOUT_ANALYTICS";

    /**
     * 日活上报时间戳（只在失败重报时使用）
     */
    public static final String EXTRA_REPORT_TIMESTAMP = "com.dubox.drive.extra.EXTRA_REPORT_TIMESTAMP";

    public ActivationService(final TaskSchedulerImpl scheduler) {
        super(scheduler);
    }

    /**
     * 是否为支持匿名访问的接口，即无需提供bduss的接口
     *
     * @param action
     * @return
     */
    @Override
    protected boolean supportEmptyBdussAction(String action) {
        return ACTION_SEND_APP_ACTIVATE.equals(action)
                || ACTION_REPORT_LOGOUT_ANALYTICS.equals(action);
    }

    /**
     * @param intent
     * @param bduss
     * @param uid
     * @param action
     * @param receiver
     * @param context
     */
    @Override
    protected void handleAction(final Intent intent, final String bduss, final String uid, final String action,
            final ResultReceiver receiver, final Context context) {

        if (ACTION_SEND_APP_ACTIVATE.equals(action)) { // 发送APP激活
            mScheduler.addLowTask(new SendAppActivateJob(context, receiver, bduss, uid));
        } else if (ACTION_SEND_ACTIVE.equals(action)) { // 发送日活跃
            mScheduler.addLowTask(new SendActiveJob(context, intent, receiver, bduss, uid));
        } else if (ACTION_REPORT_LOGOUT_ANALYTICS.equals(action)) {
            mScheduler.addHighTask(new ReportAnalyticsLogoutJob(context, intent, receiver, bduss, uid));
        } else {
            // 抛出异常方便发现问题
            throw new IllegalArgumentException(action + " unhandled");
        }
    }
}
