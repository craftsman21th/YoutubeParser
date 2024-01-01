package com.moder.compass.statistics.activation;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.json.JSONException;

import com.dubox.drive.base.service.constant.BaseStatus;
import com.moder.compass.base.utils.PersonalConfigKey;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.PersonalConfigKeys;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.util.TimeUtil;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.moder.compass.statistics.BroadcastStatisticKt;
import com.moder.compass.statistics.activation.io.model.ReportUserResponse;
import com.moder.compass.statistics.EventStatisticsKt;
import com.moder.compass.statistics.StatisticsKeysKt;
import com.moder.compass.statistics.activation.io.parser.ActivateParser;
import com.moder.compass.transfer.P2PManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 发送日活跃
 *
 * @author caowenbin
 */
class SendActiveJob extends BaseJob {
    private static final String TAG = "SendActiveJob";

    private final ResultReceiver receiver;
    private final String bduss;
    private final String mUid;
    private final Context context;
    private final Intent intent;

    public SendActiveJob(Context context, Intent intent, final ResultReceiver receiver, String bduss, String uid) {
        super(TAG);
        this.context = context;
        this.intent = intent;
        this.receiver = receiver;
        this.bduss = bduss;
        mUid = uid;
    }

    @Override
    protected void performExecute() {
        if (intent != null && intent.hasExtra(ActivationService.EXTRA_ACTIVE_ACTION_TYPE)) {
            String keyDayType = intent.getStringExtra(ActivationService.EXTRA_ACTIVE_ACTION_TYPE);

            String channleId = null;
            if (PersonalConfig.getInstance().has(PushPersonalConfigKey.KEY_CHANNEL_ID)) {
                channleId = PersonalConfig.getInstance().getString(PushPersonalConfigKey.KEY_CHANNEL_ID);
            }

            String bindUID = null;
            if (PersonalConfig.getInstance().has(PushPersonalConfigKey.KEY_BIND_UID)) {
                bindUID = PersonalConfig.getInstance().getString(PushPersonalConfigKey.KEY_BIND_UID);
            }

            // 时间戳，失败重传时会通过intent将时间戳传进来，其他非失败重传情况不要带此参数
            long timeStamp = intent.getLongExtra(ActivationService.EXTRA_REPORT_TIMESTAMP, -1);
            long currentTime = timeStamp == -1 ? System.currentTimeMillis() : timeStamp;
            String day = TimeUtil.getCurrentDayTime(currentTime);
            String lasttestReportsDay = PersonalConfig.getInstance().getString(keyDayType);
            DuboxLog.d(TAG, "day::" + day + ":" + keyDayType + ":" + lasttestReportsDay);

            if (!TextUtils.isEmpty(channleId) && !TextUtils.isEmpty(bindUID)) {
                if (day.equals(lasttestReportsDay)) {
                    DuboxLog.d(TAG, "isActivited:: 已发送过日活，取消发送");
                    return;
                }
            }
            EventStatisticsKt.statisticActionEvent(StatisticsKeysKt.REPORT_USER_ACTIVE);
            // 返回服务器接口的sk. 7.10 libin09 2015-7-16
            ReportUserResponse response = sendActive(bduss, mUid, keyDayType, channleId, bindUID, currentTime);
            final String encodeSK = response == null ? "" : response.uinfo;
            if (!TextUtils.isEmpty(encodeSK)) {
                PersonalConfig.getInstance().putString(PersonalConfigKeys.SK, encodeSK);
                // 上报成功后，清除source url
                if (PersonalConfig.getInstance().has(PersonalConfigKey.KEY_REPORT_USER_SOURCE_URL)) {
                    PersonalConfig.getInstance().remove(PersonalConfigKey.KEY_REPORT_USER_SOURCE_URL);
                }
                PersonalConfig.getInstance().commit();
                // 获取到encodeSK 就设置给P2P SDK
                P2PManager.setSDKEncodeSK(encodeSK);
            } else {
                // 报活失败
                EventStatisticsKt.statisticActionEvent(StatisticsKeysKt.REPORT_USER_ACTIVE_FAILED);
            }

            if (receiver != null) {
                receiver.send(TextUtils.isEmpty(encodeSK) ? BaseStatus.FAILED
                    : BaseStatus.SUCCESS, Bundle.EMPTY);
            }
            if (response != null && response.mediainfo != null) {
                String picurl = "";
                String videoUrl = "";
                String videoName = "";
                if (!TextUtils.isEmpty(response.mediainfo.picurl)) {
                    picurl = response.mediainfo.picurl;
                }
                if (!TextUtils.isEmpty(response.mediainfo.videourl)) {
                    videoUrl = response.mediainfo.videourl;
                }
                if (!TextUtils.isEmpty(response.mediainfo.videoname)) {
                    videoName = response.mediainfo.videoname;
                }
                if (!TextUtils.isEmpty(videoUrl)) {
                    PersonalConfig.getInstance().putString(PersonalConfigKeys.KEY_NEW_USER_MEDIA_IMAGE_URL, picurl);
                    PersonalConfig.getInstance().putString(PersonalConfigKeys.KEY_NEW_USER_VIDEO_URL, videoUrl);
                    PersonalConfig.getInstance().putString(PersonalConfigKeys.KEY_NEW_USER_VIDEO_NAME, videoName);
                    PersonalConfig.getInstance().commit();
                    Intent intent = new Intent(ActivateParser.ACTION_GET_NEW_USER_MEDIA_INFO);
                    intent.putExtra(ActivateParser.INTENT_EXTRA_NEW_USER_MEDIA_IMAGE_URL, picurl);
                    intent.putExtra(ActivateParser.INTENT_EXTRA_NEW_USER_MEDIA_NAME, videoName);
                    intent.putExtra(ActivateParser.INTENT_EXTRA_NEW_USER_MEDIA_VIDEO_URL, videoUrl);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    BroadcastStatisticKt.statisticSendBroadcast(ActivateParser.ACTION_GET_NEW_USER_MEDIA_INFO);
                }
            }
        }
    }

    /**
     * 发送日活跃
     *
     * @return
     *
     * @throws RemoteException 处理业务上的失败，通常服务器会返回具体错误代码
     * @throws IOException     YQH 20130226
     */
    ReportUserResponse sendActive(String bduss, String uid, String action, String channleId, String bindUID,
                                  long timestamp) {
        try {
            return new ActivationApi(bduss, uid).sendActive(action, channleId, bindUID, timestamp);
        } catch (IOException e) {
            DuboxLog.e(TAG, "", e);
        } catch (RemoteException e) {
            DuboxLog.e(TAG, "", e);
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

        return null;
    }

}
