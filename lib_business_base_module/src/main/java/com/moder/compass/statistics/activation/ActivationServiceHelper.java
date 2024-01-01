package com.moder.compass.statistics.activation;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.moder.compass.account.Account;
import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.base.service.constant.BaseExtras;
import com.dubox.drive.base.service.constant.ServiceTypes;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 设备激活
 *
 * @author tianzengming<br />
 * Created by tianzengming on 2015/8/27.
 */
public class ActivationServiceHelper extends BaseServiceHelper {
    private static final String TAG = "ActiveServiceHelper";

    public static Intent buildIntent(Context context, String bduss, String uid, ResultReceiver resultReceiver) {
        Intent intent = BaseServiceHelper.buildIntent(context, bduss, uid, resultReceiver);
        if (intent != null) {
            intent.putExtra(BaseExtras.EXTRA_SERVICE_TYPE, ServiceTypes.SEND_ACTIVE);
        }
        return intent;
    }

    /**
     * 发送日活跃
     *
     * @param context        上下文
     * @param resultReceiver 用于返回结果 YQH 20130227
     */
    static void sendActive(Context context, ResultReceiver resultReceiver, String action) {
        DuboxLog.d(TAG, "sendActive::" + "start");
        if (!Account.INSTANCE.isLogin()) {
            return;
        }

        final String bduss = Account.INSTANCE.getNduss();

        if (TextUtils.isEmpty(bduss)) {
            return;
        }
        final String uid = Account.INSTANCE.getUid();

        if (TextUtils.isEmpty(uid)) {
            return;
        }
        DuboxLog.d(TAG, "sendActive::" + "startACTION_SEND_ACTIVE");

        try {
            Intent intent = buildIntent(context, bduss, uid, resultReceiver);
            if (intent != null) {
                intent.setAction(ActivationService.ACTION_SEND_ACTIVE);
                intent.putExtra(ActivationService.EXTRA_ACTIVE_ACTION_TYPE, action);
                BaseServiceHelper.startTargetVersionService(
                        context, intent);
            }
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "sendActive", ignore);
        }
    }

    /**
     * 发送APP激活
     *
     * @param context        上下文
     * @param resultReceiver 用于返回结果
     */
    public static void sendAppActivate(Context context, ResultReceiver resultReceiver) {
        boolean isActivited = GlobalConfig.getInstance().getBoolean(ActivationConfigKey.KEY_IS_ACTIVITED, false);
        DuboxLog.d(TAG, "sendAppActivate()isActivited::" + isActivited);
        if (isActivited) {
            return;
        }
        if (!isNetWorkAvailable(context, resultReceiver)) {
            return;
        }
        Intent intent = buildIntent(context, null, null, resultReceiver);
        if (intent != null) {
            intent.setAction(ActivationService.ACTION_SEND_APP_ACTIVATE);
            BaseServiceHelper.startTargetVersionService(context, intent);
        }
        DuboxLog.d(TAG, "isActivited::" + "sendAppActivate::startService");
    }

    /**
     * 应用启动时，未登录状态时统计
     */
    public static void reportAnalyticsLogout(Context context) {
        if (!isNetWorkAvailable(context, null)) {
            return;
        }
        Intent intent = buildIntent(context, null, null, null);
        if (intent != null) {
            intent.setAction(ActivationService.ACTION_REPORT_LOGOUT_ANALYTICS);
            BaseServiceHelper.startTargetVersionService(context, intent);
        }
    }

}
