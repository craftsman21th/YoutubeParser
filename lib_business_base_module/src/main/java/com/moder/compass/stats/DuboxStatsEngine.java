package com.moder.compass.stats;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;
import com.moder.compass.stats.upload.compress.ICompress;
import com.moder.compass.stats.upload.network.IReport;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xinxiaohui on 2015-04-20.
 */
public class DuboxStatsEngine {
    private static final String TAG = "DuboxStatsEngine";
    private volatile static DuboxStatsEngine instance;

    private static final String SPEED_STATISTICS_FILE_NAME = "duboxstatisticsspeedupload.ini";
    private static final String DUBOXSTATSSPEED_UPLOADSTATUS_KEY = "duboxstatisticsspeeduploadSuccess";
    private static final String KEY_SPEED_STATS = "speed_stats";

    private static final String VIDEO_STATISTICS_FILE_NAME = "video_statistics.ini";
    private static final String DUBOXSTATSVIDEO_UPLOADSTATUS_KEY = "DuboxStatsVideoSuccess";
    private static final String KEY_VIDEO_STUTTER = "video_stutter";

    private static final String MONITOR_STATISTICS_FILE_NAME = "duboxstatisticsmonitor.ini";
    private static final String DUBOXSTATS_MONITOR_STATUS_KEY = "duboxstatisticsmonitorSuccess";
    private static final String KEY_MONITOR_STATS = "monitor_stats";

    private static final String TRANSMIT_STATISTICS_FILE_NAME = "duboxkstatisticstransmit.ini";
    private static final String DUBOXSTATS_TRANSMIT_STATUS_KEY = "duboxstatisticstransmitSuccess";
    private static final String KEY_TRANSMIT_STATS = "transmit_stats";

    private static final String NEW_STATISTICS_FILE_NAME = "duboxstatisticsmutilfields.ini";
    private static final String DUBOXSTATS_NEW_STATUS_KEY = "DuboxStatsNewSuecss";
    public static final String KEY_NEW_STATS = "new_stats";

    private static final String OLD_STATISTICS_FILE_NAME = "duboxstatistics.ini";
    private static final String DUBOXSTATS_OLD_STATUS_KEY = "DuboxStatsOld";
    public static final String KEY_OLD_STATS = "old_stats";

    private static final String JSON_STATISTICS_FILE_NAME = "json_statistics.ini";
    private static final String DUBOXSTATS_JSON_STATUS_KEY = "DuboxStatsJsonSuccess";
    private static final String KEY_JSON_STUTTER = "json_stutter";

    private ConcurrentHashMap<StatisticsType, DuboxStats> statsMap =
            new ConcurrentHashMap<StatisticsType, DuboxStats>();

    /**
     * 优先级队列调度器
     */
    private static TaskSchedulerImpl sTaskScheduler;

    private DuboxStatsEngine() {
        DuboxStats mDuboxStatsNew = new DuboxStatsNew(new StatsOptions.Builder()
                .setFileName(NEW_STATISTICS_FILE_NAME)
                .setReportType(IReport.TYPE_DUBOX)
                .setCompressType(ICompress.TYPE_ZIP)
                .setMaxMemoryCount(30).setOnlyWifiSend(false)
                .setSourceType(StatisticsType.NEW.getType())
                .setJobName(KEY_NEW_STATS)
                .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                .setMaxReportCount(100)
                .setUploadKey(DUBOXSTATS_NEW_STATUS_KEY).build());
        statsMap.put(StatisticsType.NEW, mDuboxStatsNew);

        DuboxStats mDuboxStatsOld = new DuboxStatsOld(mDuboxStatsNew,
                new StatsOptions.Builder()
                        .setFileName(OLD_STATISTICS_FILE_NAME)
                        .setMaxMemoryCount(30).setOnlyWifiSend(false)
                        .setSourceType(StatisticsType.OLD.getType())
                        .setReportType(IReport.TYPE_DUBOX)
                        .setCompressType(ICompress.TYPE_ZIP)
                        .setJobName(KEY_OLD_STATS)
                        .setMaxReportCount(100)
                        .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                        .setUploadKey(DUBOXSTATS_OLD_STATUS_KEY).build());
        statsMap.put(StatisticsType.OLD, mDuboxStatsOld);

        DuboxStats mDuboxStatsMTJ = new DuboxStatsMTJ(mDuboxStatsNew,
                new StatsOptions.Builder()
                        .setMaxMemoryCount(30).setOnlyWifiSend(false)
                        .setSourceType(StatisticsType.OLD.getType())
                        .setCompressType(ICompress.TYPE_ZIP)
                        .setMaxReportCount(100)
                        .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                        .setReportType(IReport.TYPE_DUBOX).build());
        statsMap.put(StatisticsType.MTJ, mDuboxStatsMTJ);

        DuboxStats mDuboxStatsSpeed =
                new DuboxStatsString(new StatsOptions.Builder()
                        .setFileName(SPEED_STATISTICS_FILE_NAME)
                        .setReportType(IReport.TYPE_DUBOX)
                        .setCompressType(ICompress.TYPE_ZIP)
                        .setMaxMemoryCount(30).setOnlyWifiSend(true)
                        .setSourceType(StatisticsType.SPEED.getType())
                        .setJobName(KEY_SPEED_STATS)
                        .setMaxReportCount(30)
                        .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                        .setUploadKey(DUBOXSTATSSPEED_UPLOADSTATUS_KEY).build());
        statsMap.put(StatisticsType.SPEED, mDuboxStatsSpeed);

        DuboxStats mDuboxStatsVideo = new DuboxStatsString(new StatsOptions.Builder()
                .setFileName(VIDEO_STATISTICS_FILE_NAME)
                .setReportType(IReport.TYPE_DUBOX)
                .setCompressType(ICompress.TYPE_ZIP)
                .setMaxMemoryCount(1).setOnlyWifiSend(true)
                .setSourceType(StatisticsType.VIDEO.getType())
                .setJobName(KEY_VIDEO_STUTTER)
                .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                .setMaxReportCount(30)
                .setUploadKey(DUBOXSTATSVIDEO_UPLOADSTATUS_KEY).build());
        statsMap.put(StatisticsType.VIDEO, mDuboxStatsVideo);

        DuboxStats mDuboxStatsMonitor = new DuboxStatsString(new StatsOptions.Builder()
                .setFileName(MONITOR_STATISTICS_FILE_NAME)
                .setReportType(IReport.TYPE_DUBOX)
                .setCompressType(ICompress.TYPE_ZIP)
                .setMaxMemoryCount(10).setOnlyWifiSend(true)
                .setSourceType(StatisticsType.MONITOR.getType())
                .setJobName(KEY_MONITOR_STATS)
                .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                .setMaxReportCount(30)
                .setUploadKey(DUBOXSTATS_MONITOR_STATUS_KEY).build());
        statsMap.put(StatisticsType.MONITOR, mDuboxStatsMonitor);

        DuboxStats mDuboxStatsFileTransmit = new DuboxStatsString(new StatsOptions.Builder()
                .setFileName(TRANSMIT_STATISTICS_FILE_NAME)
                .setReportType(IReport.TYPE_DUBOX)
                .setCompressType(ICompress.TYPE_ZIP)
                .setMaxMemoryCount(5).setOnlyWifiSend(false)
                .setSourceType(StatisticsType.TRANSMIT.getType())
                .setJobName(KEY_TRANSMIT_STATS)
                .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                .setMaxReportCount(30)
                .setUploadKey(DUBOXSTATS_TRANSMIT_STATUS_KEY).build());
        statsMap.put(StatisticsType.TRANSMIT, mDuboxStatsFileTransmit);

        /**
         * 用于统计信息比较多且统计信息的key用other不方便的统计，此为临时方案，
         * 待统计模块支持扩展字段时，可删除此方案，并替换成统一的新方案
         */
        DuboxStats mDuboxStatsJson = new DuboxStatsString(new StatsOptions.Builder()
                .setFileName(JSON_STATISTICS_FILE_NAME)
                .setReportType(IReport.TYPE_DUBOX)
                .setCompressType(ICompress.TYPE_ZIP)
                .setMaxMemoryCount(10).setOnlyWifiSend(true)
                .setSourceType(StatisticsType.JSON.getType())
                .setJobName(KEY_JSON_STUTTER)
                .setSaveLog2StorageOnDebug(DuboxLog.isDebug())
                .setMaxReportCount(30)
                .setUploadKey(DUBOXSTATS_JSON_STATUS_KEY).build());
        statsMap.put(StatisticsType.JSON, mDuboxStatsJson);
    }

    public static DuboxStatsEngine getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (DuboxStatsEngine.class) {
            if (instance == null) {
                instance = new DuboxStatsEngine();
            }
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance == null) {
            return;
        }
        synchronized (DuboxStatsEngine.class) {
            instance = null;
        }
    }

    /**
     * NetdiskService
     *
     * @param scheduler
     */
    public void setScheduler(TaskSchedulerImpl scheduler) {
        if (DuboxStatsEngine.sTaskScheduler == null) {
            DuboxStatsEngine.sTaskScheduler = scheduler;
        }
    }

    public DuboxStats getDuboxStats(StatisticsType type) {
        DuboxStats result = statsMap.get(type);
        if (result != null && result.scheduler == null) {
            result.setScheduler(sTaskScheduler);
        }
        return result;
    }

    public void uploadAll() {
        for (StatisticsType statsType : statsMap.keySet()) {
            statsMap.get(statsType).uploadWrapper();
        }
    }
}
