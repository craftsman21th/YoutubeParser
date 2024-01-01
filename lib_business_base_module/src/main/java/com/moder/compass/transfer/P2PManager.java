package com.moder.compass.transfer;

import static com.moder.compass.base.utils.AMisServerKeysKt.DOWNLOAD_SDK_CONFIG;

import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONException;
import org.json.JSONObject;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.base.network.StokenManager;
import com.moder.compass.base.storage.config.ConfigDownloadSDK;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.moder.compass.base.storage.config.Setting;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.android.util.RealTimeUtil;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.config.ServerConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.dubox.drive.security.URLHandler;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;
import com.moder.compass.transfer.util.TransferUtil;
import com.cocobox.library.Key;
import com.cocobox.library.Operation;
import com.cocobox.library.P2P;
import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import rubik.generate.context.dubox_com_dubox_drive_vip.VipContext;

/**
 * Created by liuliangping on 2016/7/3.
 */
public class P2PManager {
    private static final String TAG = "P2PManager";

    /**
     * 配置是否可以使用p2p模块
     */
    private static final AtomicBoolean sConfigP2PEnable = new AtomicBoolean(false);

    /**
     * p2p-SDK参数是否初始化
     */
    private static final AtomicBoolean sSDKParamsInit = new AtomicBoolean(false);

    /**
     * p2p-SDK是否初始化
     */
    private static final AtomicBoolean sSDKInit = new AtomicBoolean(false);

    private static P2PSDKCallbackProxy sP2PSDKCallbackProxy;

    private static final String SK_KEY = "encode_sk";
    private static final String SERVER_TIME_KEY = "server_time";
    private static final String VERSION_APP = "version_app";

    /**
     * 获取p2p的配置是否打开
     *j
     * @return
     */
    public boolean isP2PConfigEnable() {
        if (!sConfigP2PEnable.get()) {
            ConfigSystemLimit configSystemLimit = ConfigSystemLimit.getInstance();
            if (configSystemLimit.p2pEnabled) {
                return sConfigP2PEnable.compareAndSet(false, true);
            }
            return false;
        }
        return true;
    }

    public void initVipType() {
        Boolean isVip = VipContext.Companion.isVip();
        int p2pVipLevel = 1;
        if (isVip != null && isVip) {
            p2pVipLevel = 3;
        }
        P2P.getInstance().setParameter(Key.MEMBERSHIP_TYPE, String.valueOf(p2pVipLevel));
    }

    /**
     * 获取普通文件进入整合下载SDK的配置是否打开
     */
    public boolean isNormalIntoSDKConfigEnable() {
        ConfigDownloadSDK configDownloadSDK =
                new ConfigDownloadSDK(ServerConfig.INSTANCE.getString(DOWNLOAD_SDK_CONFIG));
        if (configDownloadSDK.normalIntoSdkDownloadEnabled) {
            DuboxLog.d(TAG, "isNormalIntoSDKConfigEnable: true");
            return true;
        } else {
            DuboxLog.d(TAG, "isNormalIntoSDKConfigEnable: false");
            return false;
        }
    }


    /**
     * 获取本地web服务器接口
     *
     * @return
     */
    public String getHttpServerPort() {
        if (!isInitOk()) {
            return "";
        }
        return P2P.getInstance().getHttpServerPort();
    }

    /**
     * p2p模块是否初始化成功
     *
     * @return
     */
    public boolean isInitOk() {
        return sSDKParamsInit.get();
    }

    /**
     * p2p sdk 初始化
     */
    public void init(Context context) {
        if (!Account.INSTANCE.isLogin()) {
            DuboxLog.d(TAG, "do not init,  not login");
            return;
        }

        if (!isP2PConfigEnable()) {
            DuboxLog.d(TAG, "config do not use p2p");
            return;
        }

        if (!sSDKParamsInit.compareAndSet(false, true)) {
            DuboxLog.d(TAG, "P2P SDK params already init");
            return;
        }

        DuboxLog.d(TAG, "initP2P Params");
        P2P p = P2P.getInstance();
        p.setUseAppDownloadedSo(ConfigSystemLimit.getInstance().isP2pSDKUseDynamicSO);
        if (DuboxLog.isDebug()) {
            p.logOn();
            p.setParameter(Key.DEBUG_LOG_PATH, Setting.getDefaultFolder());
        } else {
            p.logOff();
        }
        p.setParameter(Key.BDUSS, Account.INSTANCE.getNduss());
        p.setParameter(Key.USER_ID, Account.INSTANCE.getUid());
        p.setParameter(Key.CONTROL_DOMAIN, HostURLManager.getDomainNoWWW());
        p.setParameter(Key.PCS_CONTROL_DOMAIN, HostURLManager.getDataSimpleDomain());
        p.setParameter(Key.DEVICE_ID, AppCommon.DEVUID);
        p.setParameter(Key.USER_AGENT, RequestCommonParams.getUserAgent());
        p.setParameter(Key.APP_DOWNLOAD_PATH, Setting.getDefaultSaveDir(BaseApplication.getInstance()));
        initP2PNetType(context);

        // 8.5 传入sk和时间参数
        String encodeSK = "";
        try {
            encodeSK = URLHandler.getSK();
        } catch (Throwable ex) {
            encodeSK = "";
        }
        final String timestamp = String.valueOf(RealTimeUtil.getTime() / 1000);
        final String appVersion = AppCommon.VERSION_DEFINED;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SK_KEY, encodeSK);
            jsonObject.put(SERVER_TIME_KEY, timestamp);
            jsonObject.put(VERSION_APP, appVersion);
            DuboxLog.d(TAG, "set P2P EXTEND_PARAM: " + jsonObject.toString());
            P2P.getInstance().setParameter(Key.EXTRA_URL_PARAMS, jsonObject.toString());
        } catch (JSONException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
        p.setAndroidContext(context);
        p.setP2SPCallbackImpl(getCallback());
        addCookie();
        initVipType();
        if (sSDKInit.compareAndSet(false, true)) {
            DuboxLog.d(TAG, "initP2P SDK");
            p.yunP2PInit();
        }
        p.getParameter(Key.HTTP_SERVER_PORT);
        StatisticsLogForMutilFields.getInstance()
            .updateCount(StatisticsLogForMutilFields.StatisticsKeys.P2P_SDK_INIT_TIMES);
    }

    public void initP2PNetType(Context context) {
        String netType = TransferUtil.P2P_NET_TYPE_NONE;
        if (ConnectivityState.isConnected(context)) {
            String type = ConnectivityState.getNetWorkType(context);
            if (!TextUtils.isEmpty(type)) {
                if (type.trim().equals("wifi")) {
                    netType = TransferUtil.P2P_NET_TYPE_WIFI;
                } else {
                    netType = TransferUtil.P2P_NET_TYPE_MOBILE;
                }
            }
        }
        P2P.getInstance().setParameter(Key.NETWORK_TYPE, netType);
    }

    private void addCookie() {
        String cookie = Constants.DUBOX_BDUSS_FIELD_NAME + "=" + Account.INSTANCE.getNduss();

        // 7.13.0 加入stoken和pan psc libin09 2016-4-28
        StokenManager stokenManager = new StokenManager(Account.INSTANCE.getNduss());
        cookie = stokenManager.addPanPsc(cookie);
        cookie = stokenManager.addPanNdutFmt(cookie);
        final String stoken = Account.stoken;
        if (!TextUtils.isEmpty(stoken)) {
            cookie += ";" + Constants.COOKIE_STOKEN + "=" + stoken;
        }
        P2P.getInstance().setParameter(Key.USER_COOKIE, cookie);

    }

    public static void setSDKEncodeSK(String encodeSK) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SK_KEY, encodeSK);
            DuboxLog.d(TAG, "set P2P EXTEND_PARAM: " + jsonObject.toString());
            P2P.getInstance().setParameter(Key.EXTRA_URL_PARAMS, jsonObject.toString());
        } catch (JSONException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 销毁P2PSDK
     */
    public void destroy() {
        if (!sSDKParamsInit.compareAndSet(true, false)) {
            return;
        }
        P2P p = P2P.getInstance();
        p.setParameter(Key.BDUSS, "");
        p.setParameter(Key.USER_ID, "");
        p.setParameter(Key.APP_DOWNLOAD_PATH, "");
        p.setParameter(Key.USER_COOKIE, "");
        StatisticsLogForMutilFields.getInstance()
            .updateCount(StatisticsLogForMutilFields.StatisticsKeys.P2P_SDK_EXIT_TIMES);
    }

    /**
     * p2p下载情况下，由文件名生成获取临时文件名
     *
     * @return
     *
     * @author liuliangping Create at 2016-7-11
     */
    @Nullable
    public String getP2PTemporaryFileName(String filePath) {
        if (!isInitOk()) {
            return null;
        }

        StringBuffer temporaryFileName = new StringBuffer(filePath);
        if (filePath.length() > 0) {
            temporaryFileName = temporaryFileName.append(P2P.getInstance().getTempFileAppendix());
        }
        DuboxLog.d(TAG, "getP2PTemporaryFileName temporaryFileName::" + temporaryFileName.toString());
        return temporaryFileName.toString();
    }

    /**
     * 获取p2p相关的代理
     *
     * @return
     */
    public P2PSDKCallbackProxy getCallback() {
        if (!isInitOk()) {
            return null;
        }

        synchronized (P2PManager.class) {
            if (sP2PSDKCallbackProxy == null) {
                sP2PSDKCallbackProxy = new P2PSDKCallbackProxy();
            }
            return sP2PSDKCallbackProxy;
        }
    }

    /**
     * 获取p2p的版本
     */
    public String getVersion() {
        if (!isInitOk()) {
            return null;
        }

        return P2P.getInstance().getVersion();
    }

    /**
     * 删除p2p任务与通过p2p下载的文件（完成和临时的文件）
     *
     * @param fgid
     * @param deleteFile
     */
    public void deleteTask(String fgid, boolean deleteFile) {
        if (!isInitOk()) {
            return;
        }

        DuboxLog.d(TAG, "deleteTask fgid:" + fgid);
        if (TextUtils.isEmpty(fgid)) {
            return;
        }

        P2P.getInstance().controlTask(fgid, deleteFile ? Operation.P2P_DELETE_TASK_AND_FILES : Operation.P2P_DELETE);
    }
}
