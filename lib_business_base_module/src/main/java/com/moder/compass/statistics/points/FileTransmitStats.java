package com.moder.compass.statistics.points;

import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.moder.compass.stats.DuboxStatsEngine;
import com.moder.compass.stats.StatisticsType;

import android.text.TextUtils;

/**
 * Created by liji01 on 16-3-14.
 */
public class FileTransmitStats extends BaseStats {
    private static final String TAG = "FileTransmitStats";

    public void uploadLog(final String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        ConfigSystemLimit configSystemLimit = ConfigSystemLimit.getInstance();
        if (!configSystemLimit.fileTransmitStats) {
            return;
        }
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.TRANSMIT).statCount(log);
    }
}
