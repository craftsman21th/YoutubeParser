package com.moder.compass.versionupdate.service;

import com.moder.compass.account.Account;
import com.dubox.drive.base.service.constant.ServiceTypes;
import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.base.service.constant.BaseExtras;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.text.TextUtils;

/**
 * Created by liji01 on 15-1-22.
 */
public class VersionUpdateServiceDelegate extends BaseServiceHelper {
    private static final String TAG = "VersionUpdateDelegate";

    /**
     * 上下文引用
     */
    private Context mContext;

    public VersionUpdateServiceDelegate(Context context) {
        mContext = context;
    }

    public static Intent buildIntent(Context context, String bduss, String uid, ResultReceiver resultReceiver) {
        Intent it = BaseServiceHelper.buildIntent(context, bduss, uid, resultReceiver);
        if (it != null) {
            it.putExtra(BaseExtras.EXTRA_SERVICE_TYPE, ServiceTypes.VERSIONUPDATE);
            return it;
        } else {
            return null;
        }
    }

    /**
     * 检查是否有升级
     *
     * @param context        上下文
     * @param resultReceiver 用于返回结果
     */
    public static void checkUpgrade(Context context, ResultReceiver resultReceiver) {
        if (!isNetWorkAvailable(context, resultReceiver)) {
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
        Intent it = buildIntent(context, bduss, uid, resultReceiver);
        if (it != null) {
            it.setAction(VersionUpdateService.ACTION_CHECK_UPGRADE);
            BaseServiceHelper.startTargetVersionService(context, it);
        }

    }
}
