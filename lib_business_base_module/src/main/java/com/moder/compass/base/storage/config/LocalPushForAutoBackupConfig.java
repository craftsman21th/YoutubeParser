package com.moder.compass.base.storage.config;

import static com.moder.compass.base.utils.EventCenterHandlerKt.CHECK_ADD_WIDGET_PUSH;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xujing31 on 2019/2/22.
 */

public class LocalPushForAutoBackupConfig {

    private static final String TAG = "LocalPushForAutoBackupConfig";

    /**
     * 每次推送间隔时间
     * @since 9.6.20
     * @since 2.5.5 后改为3天
     */
    @SerializedName("interval_time")
    public int intervalTime = 3;
    /**
     * 待备份图片--今日数量判定值
     * @since 9.6.20
     * @since 2.5.5 图库有新增或删除照片就算，没有数量限制
     */
    @SerializedName("today_need_backup_photo_num_limit")
    public int todayPhotoNumLimit = 0;
    /**
     * 待备份图片--近7天数量判定值
     * @since 9.6.20
     */
    @SerializedName("7_days_need_backup_photo_num_limit")
    public int sevenDaysPhotoNumLimit = 20;
    /**
     * 待备份图片--近30天数量判定值
     * @since 9.6.20
     */
    @SerializedName("30_days_need_backup_photo_num_limit")
    public int thirtyDaysPhotoNumLimit = 100;


    public LocalPushForAutoBackupConfig(@Nullable String json) {
        if (!TextUtils.isEmpty(json)) {
            init(json);
        }
    }

    private void init(String body) {
        try {
            final LocalPushForAutoBackupConfig config = new Gson().fromJson(body, this.getClass());
            if (config == null) {
                return;
            }
            intervalTime = config.intervalTime;
            todayPhotoNumLimit = config.todayPhotoNumLimit;
            sevenDaysPhotoNumLimit = config.sevenDaysPhotoNumLimit;
            thirtyDaysPhotoNumLimit = config.thirtyDaysPhotoNumLimit;

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

    /**
     * push 不满足展示条件，检查下一个 push
     */
    public void onPushCanNotShow() {
        EventCenterHandler.INSTANCE.sendMsg(CHECK_ADD_WIDGET_PUSH);
    }

}
