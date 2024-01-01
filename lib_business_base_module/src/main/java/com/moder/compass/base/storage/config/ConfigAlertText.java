package com.moder.compass.base.storage.config;

import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.basemodule.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class ConfigAlertText {
    private static final String TAG = "ConfigPermission";

    /**
     * 是否显示限速用户提示文案，默认不显示
     */
    @SerializedName("is_show_limit_alert")
    public boolean isShowLimitAlert = false;

    /**
     * 针对被限速用户的提示文案
     */
    @SerializedName("limit_user_alert_text")
    public String limitUserAlertText =  BaseApplication.getInstance()
            .getString(R.string.limit_user_alert_default_text);

    /**
     * 是否显示封禁用户提示文案，默认不显示
     */
    @SerializedName("is_show_forbidden_alert")
    public boolean isShowForbiddenAlert = false;

    /**
     * 针对被封禁用户的下载提示文案
     */
    @SerializedName("forbiden_user_download_alert_text")
    public String forbiddenUserDownloadAlertText = BaseApplication.getInstance()
            .getString(R.string.forbidden_user_download_default_text);

    /**
     * 针对被封禁用户的播放视频提示文案
     */
    @SerializedName("forbiden_user_play_video_alert_text")
    public String forbiddenUserPlayVideoAlertText = BaseApplication.getInstance()
            .getString(R.string.forbidden_user_play_default_text);

    public ConfigAlertText(String body) {
        if (!TextUtils.isEmpty(body)) {
            init(body);
        }
    }

    private void init(String body) {
        try {
            final ConfigAlertText config = new Gson().fromJson(body, this.getClass());
            if (config == null) {
                return;
            }
            isShowLimitAlert = config.isShowLimitAlert;
            if (!TextUtils.isEmpty(config.limitUserAlertText)) {
                limitUserAlertText = config.limitUserAlertText;
            }

            isShowForbiddenAlert = config.isShowForbiddenAlert;

            if (!TextUtils.isEmpty(config.forbiddenUserDownloadAlertText)) {
                forbiddenUserDownloadAlertText = config.forbiddenUserDownloadAlertText;
            }
            if (!TextUtils.isEmpty(config.forbiddenUserPlayVideoAlertText)) {
                forbiddenUserPlayVideoAlertText = config.forbiddenUserPlayVideoAlertText;
            }

        } catch (JsonSyntaxException e) {
            DuboxLog.d(TAG, "init.JsonSyntaxException.e:" + e.getMessage());
        } catch (JsonIOException e) {
            DuboxLog.d(TAG, "init.IOException.e:" + e.getMessage());
        } catch (NullPointerException e) {
            DuboxLog.d(TAG, "init.NullPointerException.e:" + e.getMessage());
        } catch (JsonParseException e) {
            DuboxLog.d(TAG, "init.JsonParseException.e:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            DuboxLog.d(TAG, "init.IllegalArgumentException.e:" + e.getMessage());
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "配置项初始化错误", ignore);
            if (DuboxLog.isDebug()) {
                throw ignore;
            }
        }
    }

}
