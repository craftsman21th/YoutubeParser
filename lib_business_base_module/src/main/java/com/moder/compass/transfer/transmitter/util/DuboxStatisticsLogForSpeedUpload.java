package com.moder.compass.transfer.transmitter.util;

import com.moder.compass.stats.DuboxStatsEngine;
import com.moder.compass.stats.StatisticsType;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.text.TextUtils;

/**
 * 
 * com.dubox.drive.statistics.DuboxStatisticsLogForSpeedUpload
 * 
 * @author tianzengming <br/>
 *         create at 2014-1-10 下午4:15:47
 */
public class DuboxStatisticsLogForSpeedUpload {
    private static final String TAG = "DuboxStatisticsLogForSpeedUpload";
    private static Object objLock = new Object();
    private static DuboxStatisticsLogForSpeedUpload instance;

    public static DuboxStatisticsLogForSpeedUpload getInstance() {
        if (null == instance) {
            synchronized (objLock) {
                if (null == instance) {
                    instance = new DuboxStatisticsLogForSpeedUpload();
                }
            }
        }
        return instance;
    }

    public void updateSpeedCount(SpeedUploadUtils.TransmissionInfo speedInfo) {
        if (speedInfo == null) {
            DuboxLog.d(TAG, "speedInfo is wrong.");
            return;
        }

        String op = speedInfo.getOP();
        if (TextUtils.isEmpty(op)) {
            DuboxLog.d(TAG, "op is wrong.");
            return;
        }

        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.SPEED)
                .statCount(op, speedInfo.createRecord());
        // StringBuilder sb = new StringBuilder();
        // sb.append(STATISTICS_KEY_OP + ITEM_EQUALS + op + ITEM_SPLIT);
        // sb.append(speedInfo.createRecord());
        // sb.append(ITEM_SPLIT);
        // sb.append(STATISTICS_KEY_COUNT);
        // updateCount(sb.toString(), -1);
    }

    public void updateExceptionCount(SpeedUploadUtils.ExceptionInfo info) {
        if (info == null) {
            DuboxLog.d(TAG, "speedInfo is wrong.");
            return;
        }

        String op = info.getOpType();
        if (TextUtils.isEmpty(op)) {
            DuboxLog.d(TAG, "op is wrong.");
            return;
        }

        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.SPEED)
                .statCount(op, info.creatInfo());
        // StringBuilder sb = new StringBuilder();
        // sb.append(STATISTICS_KEY_OP + ITEM_EQUALS + op + ITEM_SPLIT);
        // sb.append(info.creatInfo());
        // sb.append(ITEM_SPLIT);
        // sb.append(STATISTICS_KEY_COUNT);
        // updateCount(sb.toString(), -1);
    }
}
