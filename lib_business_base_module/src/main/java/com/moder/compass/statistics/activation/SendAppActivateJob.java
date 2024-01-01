package com.moder.compass.statistics.activation;

import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;

import android.content.Context;
import android.os.ResultReceiver;

import org.json.JSONException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * 发送APP激活
 * 
 * @author caowenbin
 * 
 */
class SendAppActivateJob extends BaseJob {
    private static final String TAG = "SendAppActivateJob";

    private final ResultReceiver receiver;
    private final String bduss;
    private final String mUid;
    private final Context context;

    public SendAppActivateJob(Context context, final ResultReceiver receiver, String bduss, String uid) {
        super(TAG);
        this.context = context;
        this.receiver = receiver;
        this.bduss = bduss;
        mUid = uid;
    }

    @Override
    protected void performExecute() {
        if (GlobalConfig.getInstance().getBoolean(ActivationConfigKey.KEY_IS_ACTIVITED, false)) {
            DuboxLog.d(TAG, "isActivited:: 已激活过，取消发送");
            return;
        }

        // 记录设备激活时间
        try {
            if (sendAppActivate(bduss, mUid)) {
                setActivited(true);
            }

            DuboxLog.d(TAG, "isActivited::" + "sendAppActivate::sendAppActivate()");
        } catch (RemoteException e) {
            DuboxLog.w(TAG, "", e);
            BaseServiceHelper.handleRemoteException(e, receiver);
        } catch (IOException e) {
            DuboxLog.w(TAG, "", e);
            BaseServiceHelper.handleIOException(e, receiver);
        }
    }

    /**
     * 设置设备上是否激活过客户端
     *
     * @author libin09 2013-2-26
     * @param isActivited
     *
     */
    private void setActivited(boolean isActivited) {
        GlobalConfig.getInstance().putBoolean(ActivationConfigKey.KEY_IS_ACTIVITED, isActivited);
        if (isActivited) {
            // 激活成功则记录下激活时间
            GlobalConfig.getInstance()
                    .putLong(ActivationConfigKey.KEY_IS_ACTIVITED_TIME, System.currentTimeMillis() / 1000);
        }
        GlobalConfig.getInstance().commit();
    }

    /**
     * 发送APP激活
     *
     * @param bduss 账号标识
     * @param uid
     * @return
     * @throws RemoteException 处理业务上的失败，通常服务器会返回具体错误代码
     * @throws IOException
     */
    boolean sendAppActivate(String bduss, String uid) throws RemoteException, IOException {
        try {
            return new ActivationApi(bduss, uid).sendAppActivate();
        } catch (KeyManagementException e) {
            DuboxLog.e(TAG, "", e);
        } catch (UnrecoverableKeyException e) {
            DuboxLog.e(TAG, "", e);
        } catch (NoSuchAlgorithmException e) {
            DuboxLog.e(TAG, "", e);
        } catch (KeyStoreException e) {
            DuboxLog.e(TAG, "", e);
        } catch (JSONException e) {
            DuboxLog.e(TAG, "", e);
        }
        return false;
    }

}
