package com.moder.compass.statistics.activation;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.json.JSONException;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.base.service.constant.BaseStatus;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.util.DateUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

/**
 * Created by liji01 on 15-4-22.
 */
public class ReportAnalyticsLogoutJob extends BaseJob {
    private static final String TAG = "ReportAnalyticsLogout";

    private final ResultReceiver receiver;
    private final String bduss;
    private final String mUid;
    private final Context context;
    private final Intent intent;

    public ReportAnalyticsLogoutJob(Context context, Intent intent, final ResultReceiver receiver, String bduss,
            String uid) {
        super(TAG);
        this.context = context;
        this.intent = intent;
        this.receiver = receiver;
        this.bduss = bduss;
        mUid = uid;
    }

    @Override
    protected void performExecute() {
        try {
            String dateLast = GlobalConfig.getInstance().getString(ActivationConfigKey.KEY_REPORT_LOGOUT_ANALYTICS);
            String now = DateUtil.formatTimeEndWithDay(System.currentTimeMillis());
            if (!TextUtils.isEmpty(now) && (now.compareTo(dateLast) > 0)) {
                DuboxLog.d(TAG, "开始上报未登录日活");
                reportAnalyticsLogout(bduss, mUid);
                GlobalConfig.getInstance().putString(ActivationConfigKey.KEY_REPORT_LOGOUT_ANALYTICS, now);
                GlobalConfig.getInstance().asyncCommit();
                DuboxLog.d(TAG, "上报成功记录上报时间： " + now);
            } else {
                DuboxLog.d(TAG, "在一个自然天之内，无需上报未登录日活");
            }
            if (receiver == null) {
                return;
            }
            receiver.send(BaseStatus.SUCCESS, Bundle.EMPTY);
        } catch (RemoteException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 应用启动时，未登录状态时统计
     *
     * @param bduss
     * @param uid
     * @return
     * @throws RemoteException
     * @throws IOException
     */
    public Void reportAnalyticsLogout(String bduss, String uid) throws RemoteException, IOException {
        try {
            return new ActivationApi(bduss, uid).reportAnalyticsLogout();
        } catch (KeyManagementException e) {
            DuboxLog.e(TAG, "reportAnalyticsLogout api lead to KeyManagementException", e);
            return null;
        } catch (UnrecoverableKeyException e) {
            DuboxLog.e(TAG, "reportAnalyticsLogout api lead to UnrecoverableKeyException", e);
            return null;
        } catch (NoSuchAlgorithmException e) {
            DuboxLog.e(TAG, "reportAnalyticsLogout api lead to NoSuchAlgorithmException", e);
            return null;
        } catch (KeyStoreException e) {
            DuboxLog.e(TAG, "reportAnalyticsLogout api lead to KeyStoreException", e);
            return null;
        } catch (JSONException e) {
            DuboxLog.e(TAG, "reportAnalyticsLogout api lead to JSONException", e);
            return null;
        }
    }
}
