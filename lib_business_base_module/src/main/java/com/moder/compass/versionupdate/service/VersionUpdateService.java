package com.moder.compass.versionupdate.service;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.moder.compass.account.Account;
import com.dubox.drive.base.service.ISchedulerService;
import com.dubox.drive.base.service.constant.BaseExtras;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;

/**
 * Created by liji01 on 15-1-22.
 */
public class VersionUpdateService implements ISchedulerService {
    private static final String TAG = "VersionUpdateService";

    /**
     * 检查升级
     */
    static final String ACTION_CHECK_UPGRADE = "com.dubox.drive.ACTION_CHECK_UPGRADE";

    /**
     * 优先级队列调度器
     */
    private TaskSchedulerImpl scheduler;

    public VersionUpdateService(TaskSchedulerImpl scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void onHandleIntent(Intent intent, Context context) {
        String bduss = intent.getStringExtra(BaseExtras.BDUSS);
        final String action = intent.getAction();
        // 发起请求的账号是否失效(切换账号)
        final boolean isAccountInvalid = bduss != null && !bduss.equals(Account.INSTANCE.getNduss());
        // 用于返回服务处理的结果
        final ResultReceiver receiver = intent.getParcelableExtra(BaseExtras.RESULT_RECEIVER);
        if (isAccountInvalid || !Account.INSTANCE.isLogin()) {
            // 如果用户切换了账号，或者退出了账号，不再继续执行
            DuboxLog.d(TAG, action + " cancel");
            return;
        }

        DuboxLog.d(TAG, "trace onHandleIntent:" + action);

        String uid = intent.getStringExtra(BaseExtras.UID);
        if (ACTION_CHECK_UPGRADE.equals(action)) {// 检查升级
            scheduler.addLowTask(new CheckUpgradeJob(context, receiver, bduss, uid));
        }
    }
}
