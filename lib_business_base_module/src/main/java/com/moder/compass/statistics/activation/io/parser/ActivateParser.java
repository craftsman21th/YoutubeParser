/*
 * QuotaParser.java
 * classes : com.dubox.drive.io.parser.filesystem.QuotaParser
 * @author 
 * V 1.0.0
 * Create at 2012-10-23 下午2:14:51
 */
package com.moder.compass.statistics.activation.io.parser;

import java.io.IOException;

import org.json.JSONException;

import android.text.TextUtils;

import com.dubox.drive.base.service.BaseServiceHelper;
import com.moder.compass.base.utils.PersonalConfigKey;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.activation.io.model.ReportUserResponse;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * com.dubox.drive.io.parser.filesystem.QuotaParser
 * 
 * @author <a href="mailto:">李彬</a><br/>
 *         用户容量配额的实体解析器<br/>
 *         create at 2012-10-23 下午2:14:51
 */
public class ActivateParser implements IApiResultParseable<ReportUserResponse> {
    private static final String TAG = "ActivateParser";
    private String mAction;
    private long timeStamp;

    public ActivateParser(String action, long timeStamp) {
        this.mAction = action;
        this.timeStamp = timeStamp;
    }

    @Override
    public ReportUserResponse parse(HttpResponse response) throws JSONException, RemoteException, IOException {
        final ReportUserResponse reportUserResponse;
        try {
            final String content = response.getContent();
            DuboxLog.d(TAG, "response:" + content);
            reportUserResponse = new Gson().fromJson(content, ReportUserResponse.class);
            if (reportUserResponse != null) {
                reportUserResponse.setRequestUrl(response.getUrl());
            }
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        } catch (JsonIOException e) {
            throw new IOException(e.getMessage());
        } catch (JsonParseException e) {
            throw new JSONException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }

        if (reportUserResponse == null) {
            throw new JSONException(TAG + "  is null.");
        }

        if (reportUserResponse.getErrorNo() != 0) {
            throw BaseServiceHelper.buildRemoteException(reportUserResponse.getErrorNo(), null, reportUserResponse);
        }


        if (reportUserResponse.isnew == 0) {
            PersonalConfig.getInstance().putBoolean(PersonalConfigKey.KEY_IS_OLD_USER, true);
            PersonalConfig.getInstance().commit();
        } else if (reportUserResponse.isnew == 1) {
            PersonalConfig.getInstance().putBoolean(PersonalConfigKey.KEY_IS_OLD_USER, false);
            PersonalConfig.getInstance().commit();
        }

        if (mAction == null) {
            return reportUserResponse;
        }

        long currentTime = System.currentTimeMillis();
        String dayAction = TimeUtil.getCurrentDayTime(timeStamp);
        String day = TimeUtil.getCurrentDayTime(currentTime);
        if (!TextUtils.isEmpty(mAction)
                && dayAction.equals(day) // 防止失败重报非当天的action时也将结果记录到配置中
                && !StatisticsLog.StatisticsKeys.REPORT_USER_LOGOUT.equals(mAction)) {
            PersonalConfig.getInstance().putString(mAction, day);
            PersonalConfig.getInstance().commit();
        }
        return reportUserResponse;
    }

    /**
     * 获取到新用户引导的广播用于显示
     */
    public static final String ACTION_GET_NEW_USER_MEDIA_INFO = "com.dubox.ACTION_GET_NEW_USER_MEDIA_INFO";

    public static final String INTENT_EXTRA_NEW_USER_MEDIA_IMAGE_URL = "extra.imageurl";
    public static final String INTENT_EXTRA_NEW_USER_MEDIA_NAME = "extra.name";
    public static final String INTENT_EXTRA_NEW_USER_MEDIA_VIDEO_URL = "extra.videourl";
}
