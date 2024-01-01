package com.moder.compass.base.storage.config;

import android.text.TextUtils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

/**
 * Created by GuoChao on 2018/3/1.
 */

public class ConfigPermission {

    private static final String TAG = "ConfigPermission";

    /**
     * 出现自启动引导条的时间频率(天)，默认7天
     */
    @SerializedName("show_auto_start_guide_interval_time")
    public long showAutoStartGuideIntervalTime = 7;

    public ConfigPermission(String body) {
        if (!TextUtils.isEmpty(body)) {
            init(body);
        }
    }

    private void init(String body) {
        try {
            final ConfigPermission config = new Gson().fromJson(body, this.getClass());
            if (config == null) {
                return;
            }
            showAutoStartGuideIntervalTime = config.showAutoStartGuideIntervalTime;
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
