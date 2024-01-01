package com.moder.compass.stats.upload.network;

/**
 * Created by liuliangping on 2016/9/13.
 * <p/>
 * 统计实际上传的工厂
 */
public class StatsReportFactory {
    private static final String TAG = "StatsReportFactory";

    public StatsReport createReport(int type) {
        if (type == IReport.TYPE_DUBOX) {
            return new DuboxStatsReport();
        }
        return null;
    }
}
