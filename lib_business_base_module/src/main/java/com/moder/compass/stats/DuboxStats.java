package com.moder.compass.stats;

import android.content.pm.ApplicationInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pair;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.AppInfoUtils;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.stats.storage.db.StatsProviderHelper;
import com.moder.compass.stats.upload.IUploadStatsListener;
import com.moder.compass.stats.upload.compress.ICompress;
import com.moder.compass.stats.upload.compress.StatsCompressFactory;
import com.moder.compass.stats.upload.network.StatsReport;
import com.moder.compass.stats.upload.network.StatsReportFactory;

import java.util.List;

public abstract class DuboxStats implements IUploadStatsListener {
    private static final String TAG = "DuboxStats";

    /**
     * 写入次数计数
     */
    int writeCount = 0;

    /**
     * 检测是否为前台的时间间隔 60秒内只发送一次 即进入后台后的60秒延迟后，仍然在后台
     */
    private static final int CHECK_FRONT_TIME_INTERVAL = 60 * 1000;

    /**
     * 是否正在上传
     */
    boolean isUploading = false;

    /**
     * 优先级队列调度器
     */
    protected TaskSchedulerImpl scheduler;

    /**
     * 是否正在间隔之中
     **/
    private boolean isChecking = false;

    protected StatsProviderHelper mStatsProviderHelper;

    /**
     * 数据上报的类
     */
    private final StatsReport mReport;

    /**
     * 数据上报的类
     */
    private final ICompress mCompressProcessor;

    /**
     * 未上传的数据太多
     */
    protected boolean mHasNoReportData = false;

    protected final StatsOptions mStatsOptions;

    /**
     * 默认输入法
     */
    protected String defaultInputMethod;

    DuboxStats(StatsOptions statsOptions) {
        mStatsOptions = statsOptions;
        mReport = new StatsReportFactory().createReport(mStatsOptions.getReportType());
        mCompressProcessor = new StatsCompressFactory().createCompressFactory(mStatsOptions.getCompressType());
        mStatsProviderHelper = new StatsProviderHelper();
        defaultInputMethod = getDefaultInputMethod();
    }

    public void statCount(String key) {
        statCount(key, 1);
    }

    public void statCount(String key, String[] otherKey) {
        statCount(key, 1, otherKey);
    }

    public abstract void statCount(String key, int value);

    public abstract void statCount(String key, String content);

    public abstract void statCount(String key, int value, String[] otherKey);

    public abstract void uploadWrapper();

    public abstract void uploadBackgroundWrapper();

    /**
     * 持久化数据
     *
     * @return
     */
    protected abstract void saveData();

    /**
     * 获取持久化的数据
     *
     * @return
     */
    protected abstract Pair<List<Integer>, String> readData();

    /**
     * 删除持久化数据
     */
    protected abstract void removeData(List<Integer> idList);

    protected void setScheduler(TaskSchedulerImpl scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 发送统计(前台切后台时)
     */
    void uploadBackground() {
        if (isChecking) {
            DuboxLog.v(TAG, "检测进行中，忽略本次切换");
            return;
        }
        isChecking = true;
        SystemClock.sleep(CHECK_FRONT_TIME_INTERVAL);
        if (!AppInfoUtils.isActivityOnTop()) {
            DuboxLog.v(TAG, "应用在后台,发送统计");
            upload();
        } else {
            DuboxLog.v(TAG, "应用在前台,忽略发送统计");
        }
        isChecking = false;
    }

    /**
     * 发送统计(随时发送)
     */
    protected void upload() {
        writeCount = 0;
        saveData();

        doUpdate();
    }

    /**
     * 发送统计 真正发送请求
     */
    private void doUpdate() {
        if (PersonalConfig.getInstance().getBoolean(mStatsOptions.getUploadKey(), false)) {
            PersonalConfig.getInstance().putBoolean(mStatsOptions.getUploadKey(), false);
            PersonalConfig.getInstance().commit();
        }

        if (mStatsOptions.isOnlyWifiSend()) {
            if (!ConnectivityState.isWifi(BaseApplication.getInstance())) {
                DuboxLog.v(TAG, "wifi invalid and ignore stats");
                return;
            }
            DuboxLog.v(TAG, "wifi valid and send stats");
        } else {
            if (!ConnectivityState.isConnected(BaseApplication.getInstance())) {
                DuboxLog.v(TAG, "network invalid and ignore stats");
                return;
            }
            DuboxLog.v(TAG, "network valid and send stats");
        }

        onUploadBegin();
        Pair<List<Integer>, String> dataPair = readData();
        if (dataPair == null || CollectionUtils.isEmpty(dataPair.first)) {
            DuboxLog.e(TAG, "日志文件不存在");
            onUploadError();
            return;
        }

        if (mReport != null) {
            uploadStats(dataPair.first, dataPair.second);
        } else {
            onUploadError();
        }
    }
    /**
     * 上传统计项
     */
    private void uploadStats(List<Integer> idList, String data) {
        boolean reportResult;

        if (mStatsOptions.getCompressType() == ICompress.TYPE_ZIP) {
            reportResult = mReport.report(zipCompress(data), mStatsOptions.getFileName());
        } else {
            reportResult = mReport.report(data, mStatsOptions.getFileName());
        }
        DuboxLog.d(TAG, "reportResult:" + reportResult);

        if (reportResult) {
            onUploadSuccess(idList);
        } else {
            onUploadError();
        }
    }

    /**
     * 数据压缩
     *
     * @param data
     * @return
     */
    protected byte[] zipCompress(String data) {
        return mCompressProcessor == null ? null : mCompressProcessor.zipCompress(data);
    }

    /**
     * 统计记录文件存储路径
     */
    private String getPropertyFilePath() {
        ApplicationInfo info = BaseApplication.getInstance().getApplicationInfo();
        return FileUtils.getFilePath(info.dataDir, "shared_prefs/");
    }

    @Override
    public void onUploadBegin() {
        isUploading = true;
    }

    @Override
    public void onUploadSuccess(List<Integer> successList) {
        removeData(successList);
        mHasNoReportData = CollectionUtils.isNotEmpty(successList)
                && mStatsOptions.getMaxReportCount() == successList.size();

        PersonalConfig.getInstance().putBoolean(mStatsOptions.getUploadKey(), true);
        PersonalConfig.getInstance().commit();
        isUploading = false;

        StatisticsLogForMutilFields.getInstance()
                .updateCount(StatisticsLogForMutilFields.StatisticsKeys.STATISTICS_UPLOAD_SUCCEEDED);
    }

    @Override
    public void onUploadError() {
        PersonalConfig.getInstance().putBoolean(mStatsOptions.getUploadKey(), false);
        PersonalConfig.getInstance().commit();
        DuboxLog.v(TAG, "onUploadError");
        isUploading = false;
        StatisticsLogForMutilFields.getInstance()
                .updateCount(StatisticsLogForMutilFields.StatisticsKeys.STATISTICS_UPLOAD_FAILED);
    }

    protected void printStr(String str, int length) {
        printStr("", str, length);
    }

    protected void printStr(String method, String str, int length) {
        if (!DuboxLog.isDebug() || TextUtils.isEmpty(str)) {
            return;
        }

        int temp = str.length() > length ? length : str.length();
        DuboxLog.d(TAG, method + "printStr data:" + str.substring(0, temp));
    }

    /**
     * 获取默认输入法
     */
    private String getDefaultInputMethod() {
        try {
            if (BaseApplication.getInstance() == null) {
                return null;
            }
            return Settings.Secure.getString(BaseApplication.getInstance().getContentResolver(),
                    Settings.Secure.DEFAULT_INPUT_METHOD);
        } catch (Throwable e) {
            DuboxLog.e(TAG, "get defaultInputMethod error !");
        }
        return null;
    }

}
