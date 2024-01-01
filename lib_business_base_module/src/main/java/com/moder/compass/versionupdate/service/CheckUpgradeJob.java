package com.moder.compass.versionupdate.service;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.moder.compass.versionupdate.io.model.Version;
import com.dubox.drive.base.service.constant.BaseExtras;
import com.dubox.drive.base.service.constant.BaseStatus;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.util.FirebaseRemoteConfigKeysKt;

/**
 * 检查升级
 *
 * @author caowenbin
 *
 */
class CheckUpgradeJob extends BaseJob {
    private static final String TAG = "CheckUpgradeJob";

    private final ResultReceiver receiver;
    private final String bduss;
    private final String mUid;
    /** service context need weak，because its need lifecycle operation */
    private final WeakReference<Context> weakContext;

    public CheckUpgradeJob(Context context, final ResultReceiver receiver, String bduss, String uid) {
        super(TAG);
        this.weakContext = new WeakReference<>(context);
        this.receiver = receiver;
        this.bduss = bduss;
        mUid = uid;
    }

    @Override
    protected void performExecute() {
        Version version;
        try {
            version = FirebaseRemoteConfigKeysKt.getNewVersionDownloadDataConfig();
            if (receiver == null) {
                return;
            }
            if (version != null) {
                final Bundle bundle = new Bundle();
                bundle.putParcelable(BaseExtras.RESULT, version);
                receiver.send(BaseStatus.SUCCESS, bundle);
            } else {
                receiver.send(BaseStatus.FAILED, Bundle.EMPTY);
            }
        } catch (Exception e) {
            DuboxLog.w(TAG, "", e);
        }
    }
}
