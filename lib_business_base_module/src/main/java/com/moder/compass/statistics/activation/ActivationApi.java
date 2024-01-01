/*
 * FileSystemApi.java
 * classes : com.dubox.drive.io.FileSystemApi
 * @author 
 * V 1.0.0
 * Create at 2012-10-20 下午4:07:31
 */
package com.moder.compass.statistics.activation;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.json.JSONException;

import com.dubox.drive.base.network.BaseApi;
import com.moder.compass.base.BackgroundWeakHelperKt;
import com.moder.compass.base.networklegacy.NetworkTask;
import com.dubox.drive.base.network.parser.DefaultParser;
import com.moder.compass.base.utils.PersonalConfigKey;
import com.dubox.drive.kernel.BaseShellApplication;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpParams;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.util.ConStantKt;
import com.dubox.drive.kernel.util.encode.SHA1Util;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.activation.io.model.ReportUserResponse;
import com.moder.compass.statistics.EventStatisticsKt;
import com.moder.compass.statistics.StatisticsKeysKt;
import com.moder.compass.statistics.activation.io.parser.ActivateParser;
import com.moder.compass.statistics.activation.io.parser.AppActivateParser;

import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;

import static com.moder.compass.base.utils.PersonalConfigKey.FCM_TOKEN_KEY;
import static com.moder.compass.statistics.StatisticsKeysKt.KEY_REPORT_USER_START_SOURCE;
import static com.moder.compass.statistics.activation.ActivationConfigKey.FCM_TOKEN_ERROR;

/**
 * com.dubox.drive.io.FileSystemApi
 * 
 * @author <a href="mailto:">李彬 </a> <br/>
 *         文件系统访问服务器的api<br/>
 *         create at 2012-10-20 下午4:07:31
 */
public class ActivationApi extends BaseApi {

    private static final String TAG = "ActivationApi";

    public ActivationApi(String token, String uid) {
        super(token, uid);
    }

    /**
     * 发送app激活
     *
     * @return
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public boolean sendAppActivate() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyStoreException, IOException, JSONException, RemoteException {
        final String url = HostURLManager.getApiDefaultHostName() + "report/device";
        final HttpParams params = new HttpParams();
        long currentTime = System.currentTimeMillis();
        String content = makeContent(currentTime);
        String sign = SHA1Util.hmacSha1(content);
        params.add("sign", sign);
        params.add("timestamp", String.valueOf(currentTime));
        DuboxLog.d(TAG, "sendAppActivate:URL:" + url + "&sign=" + sign + "&timestamp=" + currentTime);
        return new NetworkTask<Boolean>().send(buildGetRequest(url, params), new AppActivateParser());
    }



    private String makeContent(long currentTime) {
        StringBuffer content = new StringBuffer();
        content.append(RequestCommonParams.getClientType()).append(RequestCommonParams.getChannel())
                .append(AppCommon.DEVUID).append(currentTime);
        String contentStr = content.toString();
        DuboxLog.e(TAG, "contentStr:" + contentStr);
        return contentStr;
    }

    /**
     * 发送日活跃
     *
     * @param timestamp 日活action发生的时间，因为此方法有可能是失败重报时调用，所以timestamp需要外部传入
     * @return
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     * @throws JSONException
     * @throws RemoteException
     */
    public ReportUserResponse sendActive(String action, String channelId, String bindUID, long timestamp)
            throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            IOException, JSONException, RemoteException {
        final String url = HostURLManager.getApiDefaultHostName() + "report/user";
        final HttpParams params = new HttpParams();
        long currentTime = timestamp <= 0 ? System.currentTimeMillis() : timestamp;
        params.add("timestamp", String.valueOf(currentTime));
        params.add("action", action);
        if (null != channelId && !"".equals(channelId)) {
            params.add("channel_id", channelId);
        }
        if (null != bindUID && !"".equals(bindUID)) {
            params.add("bind_uid", bindUID);
        }
        if (!PersonalConfig.getInstance().getBoolean(PersonalConfigKey.KEY_IS_OLD_USER, false)) {
            params.add("needrookie", "1");
        }

        String fcmToken = PersonalConfig.getInstance().getString(FCM_TOKEN_KEY,
                FCM_TOKEN_ERROR + ConStantKt.CONSTANT_0);
        if (!TextUtils.isEmpty(fcmToken)) {
            // v2.2 经过讨论，目前以该接口额外增加 fcm_token 字段的形式上报 fcm token
            params.add("fcm_token", fcmToken);
        }
        String source = null;
        // 仅在登录的日活中携带活动的source url
        if (StatisticsLog.StatisticsKeys.REPORT_USER.equals(action)) {
            source = PersonalConfig.getInstance().getString(
                    PersonalConfigKey.KEY_REPORT_USER_SOURCE_URL, null);
        }
        if (!TextUtils.isEmpty(source)) {
            params.add("source", source);
        }
        // @since moder 2.18.0 添加后台归因统计上报
        String startSource = PersonalConfig.getInstance().getString(KEY_REPORT_USER_START_SOURCE);
        if (TextUtils.isEmpty(startSource)) {
            startSource = BackgroundWeakHelperKt.checkSourceAndUpdate();
        }

        // 如果是多进程归一dau,那么这里打个点
        try {
            if (TextUtils.equals(startSource, BackgroundWeakHelperKt.BACKGROUND_START_SOURCE_DUAL_PROCESS)) {
                EventStatisticsKt.statisticActionEvent(StatisticsKeysKt.DUAL_PROCESS_FOR_DAU);
            }
        } catch (Exception ignored) {
        }

        params.add("start_source", startSource);
        // 获取系统通知开关
        NotificationManager manager = (NotificationManager)
                BaseShellApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String pushOn = "1";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N
                && !manager.areNotificationsEnabled()) {
            pushOn = "0";
        }
        params.add("push_on", pushOn);

        // 获取备份状态, 结合 IOS 的取值规则
        /*
         * 传的是整数，需要转成二进制二进制按位判断 111，最低位表示照片自动备份开关，
         * 中间表示视频自动备份开关，左边最高位表示后台自动备份照片开
         * */
        boolean isPhotoEnable =
                PersonalConfig.getInstance().getBoolean(PersonalConfigKey.PHOTO_AUTO_BACKUP, false);
        boolean isVideoEnable =
                PersonalConfig.getInstance().getBoolean(PersonalConfigKey.VIDEO_AUTO_BACKUP, false);
        int backUpOn = 0;
        if (isPhotoEnable) {
            backUpOn += 1;
        }
        if (isVideoEnable) {
            backUpOn += 10;
        }
        params.add("backup_on ", "0" + backUpOn);

        DuboxLog.d(TAG, "url:" + url + "&timestamp=" + currentTime + "&action=" + action
                + (TextUtils.isEmpty(source) ? "" : "&source=" + source) + "&push_on=" + pushOn + "&backup_on"
                + " \n start_source " + startSource + " \n fcm_token " + fcmToken);
        return new NetworkTask<ReportUserResponse>().send(buildGetRequest(url, params),
                new ActivateParser(action, currentTime));
    }

    /**
     * 应用启动时，未登录状态时统计
     */
    public Void reportAnalyticsLogout()
            throws KeyManagementException, UnrecoverableKeyException, UnsupportedOperationException,
            NoSuchAlgorithmException, KeyStoreException, IOException, JSONException, RemoteException {
        final String url = HostURLManager.getApiDefaultHostName() + "analytics";
        final HttpParams params = new HttpParams();
        params.add("type", "unloginactive");
        return new NetworkTask<Void>().send(buildGetRequest(url, params), new DefaultParser());
    }
}
