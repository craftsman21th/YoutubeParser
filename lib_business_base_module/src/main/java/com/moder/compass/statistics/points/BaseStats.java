package com.moder.compass.statistics.points;

import android.text.TextUtils;

import com.moder.compass.stats.StatisticsKey;
import com.moder.compass.stats.upload.Separator;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.stats.DuboxStatsEngine;
import com.moder.compass.stats.StatisticsType;

/**
 * Created by liji01 on 15-5-25.
 */
public class BaseStats {
    private static final String TAG = "BaseStatus";

    public static final String KEY_BASE_STATS_POINTS = "base_stats_points";

    /**
     * 构造统计的log
     *
     * @param logKey
     * @param body
     * @return
     */
    private String writeString(String logKey, String body, String type) {
        String logString =
                StatisticsKey.STATISTICS_KEY_OP + Separator.ITEM_EQUALS + logKey + Separator.ITEM_SPLIT
                        + StatisticsKey.STATISTICS_KEY_VALUE + Separator.ITEM_EQUALS + body + Separator.ITEM_SPLIT
                        + StatisticsKey.STATISTICS_KEY_TYPE + Separator.ITEM_EQUALS + type;
        return logString;
    }

    public void uploadLog(final String logKey, final String body, final String type) {
        if (TextUtils.isEmpty(body) || TextUtils.isEmpty(logKey) || TextUtils.isEmpty(type)) {
            return;
        }
        String log = writeString(logKey, body, type);
        DuboxLog.d(TAG, "upload.dataString:" + log);
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.MONITOR).statCount(log);
    }
}
