package com.moder.compass.base.storage.config;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import android.text.TextUtils;

/**
 * Created by liaozhengshuang on 18/2/5.
 * 图片预览相关配置
 */

public class ConfigImagePreview {
    private static final String TAG = "ConfigImagePreview";

    /**
     * 长图定义，基础宽度，主要用于过滤掉UI出的小图
     */
    @SerializedName("long_image_min_width")
    public int longImageMinWidth = 100;

    /**
     * 长图定义，长宽比最小为3
     */
    @SerializedName("long_image_min_aspect_ratio")
    public int longImageMinAspectRatio = 3;

    public ConfigImagePreview(String json) {
        if (!TextUtils.isEmpty(json)) {
            init(json);
        }
    }

    private void init(String json) {
        try {
            ConfigImagePreview configImagePreview = new Gson().fromJson(json, this.getClass());
            if (configImagePreview == null) {
                return;
            }
            longImageMinWidth = configImagePreview.longImageMinWidth;
            longImageMinAspectRatio = configImagePreview.longImageMinAspectRatio;

        }  catch (JsonSyntaxException e) {
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
