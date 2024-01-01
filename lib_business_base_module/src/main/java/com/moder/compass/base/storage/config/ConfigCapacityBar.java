package com.moder.compass.base.storage.config;

import android.text.TextUtils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

public class ConfigCapacityBar {
    private static final String TAG = "ConfigCapacityBar";
    /**
     * 用户可拥有最大容量 单位T
     */
    @SerializedName("user_max_capacity")
    public int mUserMaxCapacity = -1;

    /**
     * 剩余容量（小于该容量提示文案改变） 单位G
     */
    @SerializedName("remain_capacity")
    public int mRemainCapacity = -1;

    /**
     * 容量使用率（大于该阈值文案改变）单位%
     */
    @SerializedName("capacity_usage_rate")
    public int mCapacityUsageRate = -1;

    /**
     * 容量条一般文案
     */
    @SerializedName("normal_text")
    public String mNormalText;

    /**
     * 容量条将满文案
     */
    @SerializedName("will_full_text")
    public String mWillFullText;

    /**
     * 容量条已满文案
     */
    @SerializedName("already_full_text")
    public String mAlreadyFullText;

    /**
     * 管理空间跳转url
     */
    @SerializedName("manage_space_url")
    public String mManageSpaceUrl;

    /**
     * 管理空间入口是否开放
     */
    @SerializedName("is_show_manage_space")
    public boolean mIsShowManageSpace;

    public ConfigCapacityBar(String body) {
        if (!TextUtils.isEmpty(body)) {
            init(body);
        }
    }

    private void init(String body) {
        try {
            final ConfigCapacityBar config = new Gson().fromJson(body, this.getClass());
            if (config == null) {
                return;
            }

            mUserMaxCapacity = config.mUserMaxCapacity;
            mRemainCapacity = config.mRemainCapacity;
            mCapacityUsageRate = config.mCapacityUsageRate;
            mNormalText = config.mNormalText;
            mAlreadyFullText = config.mAlreadyFullText;
            mWillFullText = config.mWillFullText;
            mManageSpaceUrl = config.mManageSpaceUrl;
            mIsShowManageSpace = config.mIsShowManageSpace;

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
