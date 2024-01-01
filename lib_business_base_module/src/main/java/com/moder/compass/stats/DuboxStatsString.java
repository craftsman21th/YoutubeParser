package com.moder.compass.stats;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.GlobalConfigKey;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.moder.compass.stats.storage.db.StatsContract;
import com.moder.compass.stats.upload.Separator;
import com.moder.compass.stats.upload.StatsUpload;
import com.moder.compass.stats.upload.StringUpload;
import com.moder.compass.stats.upload.UploadData;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;

class DuboxStatsString extends DuboxStats {
    private static final String TAG = "DuboxStatsString";
    private LinkedList<String> statsList = new LinkedList<String>();

    /**
     * 对内存的statsList进行加锁
     */
    private final Object mLockStringList = new Object();

    DuboxStatsString(StatsOptions options) {
        super(options);
    }

    @Override
    public void statCount(String key, int value) {

    }

    @Override
    public void statCount(String body) {
        synchronized(mLockStringList) {
            writeCount++;
            statsList.add(body);
            DuboxLog.d(TAG, "[writeCount=" + writeCount + "][Body:" + body + "]");
        }
        if (writeCount >= mStatsOptions.getMaxMemoryCount() && !isUploading) {
            uploadWrapper();
        }
    }

    @Override
    public void statCount(String key, String content) {
        synchronized(mLockStringList) {
            writeCount++;
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(StatisticsKey.STATISTICS_KEY_OP + Separator.ITEM_EQUALS + key + Separator.ITEM_SPLIT
                + StatisticsKey.STATISTICS_KEY_VALUE + Separator.ITEM_EQUALS + content);
            // 视频的统计增加视频sdk和内核版本号
            if (mStatsOptions.getSourceType() == StatisticsType.VIDEO.getType()) {
                String version = GlobalConfig.getInstance().getString(GlobalConfigKey.VAST_PLAYER_VERSION);
                if (!TextUtils.isEmpty(version)) {
                    stringBuilder.append(Separator.ITEM_SPLIT + "vast_player_version" + Separator.ITEM_EQUALS
                        + version);
                }
                String p2pVersion = GlobalConfig.getInstance().getString(GlobalConfigKey.P2P_VERSION);
                if (!TextUtils.isEmpty(p2pVersion)) {
                    stringBuilder.append(Separator.ITEM_SPLIT + GlobalConfigKey.P2P_VERSION + Separator.ITEM_EQUALS
                        + p2pVersion);
                }
                stringBuilder.append(Separator.ITEM_SPLIT + GlobalConfigKey.NETWORK_TYPE + Separator.ITEM_EQUALS
                    + (ConnectivityState.isWifi(BaseApplication.getInstance()) ? "1" : "0"));
            }
            statsList.add(stringBuilder.toString());
            DuboxLog.d(TAG, "[writeCount=" + writeCount + "][Key:" + key + "][Content:" + content + "]");
        }

        if ((mHasNoReportData || writeCount >= mStatsOptions.getMaxMemoryCount()) && !isUploading) {
            mHasNoReportData = false;

            // File file = new File(PROPERTY_FILE_PATH +
            // AccountUtils.getInstance().getUid() + STATISTICS_FILENAME);
            // if (null != file && file.length() >= MAX_SIZE) {
            uploadWrapper();
        }
    }

    @Override
    public void statCount(String key, int value, String[] otherKey) {

    }

    @Override
    public void uploadWrapper() {
        if (null == scheduler) {
            return;
        }
        scheduler.addLowTask(new BaseJob(mStatsOptions.getJobName()) {
            @Override
            protected void performExecute() {
                DuboxStatsString.this.upload();
            }
        });
    }

    @Override
    public void uploadBackgroundWrapper() {
        if (null == scheduler) {
            return;
        }
        scheduler.addLowTask(new BaseJob(mStatsOptions.getJobName() + "_background") {
            @Override
            protected void performExecute() {
                DuboxStatsString.this.uploadBackground();
            }
        });
    }

    @Override
    protected void saveData() {
        synchronized(mLockStringList) {
            if (CollectionUtils.isEmpty(statsList)) {
                return;
            }

            for (int i = 0; i < statsList.size(); i++) {
                String data = statsList.get(i);
                insertToDB(data);
            }
            statsList.clear();
        }
    }

    private void insertToDB(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        printStr("insertToDB", data, 50);

        mStatsProviderHelper.addMonitorStats(data, mStatsOptions.getSourceType());
    }

    @Override
    protected Pair<List<Integer>, String> readData() {
        DuboxLog.d(TAG, "queryMonitorStatsData");
        Cursor cursor = BaseApplication.getInstance().getContentResolver().query(
            StatsContract.Monitor.CONTENT_URI, StatsContract.Monitor.Query.PROJECTION,
            StatsContract.Monitor.SOURCE + "=" + mStatsOptions.getSourceType(), null,
            StatsContract.Monitor._ID + " ASC LIMIT " + mStatsOptions.getMaxReportCount());

        if (cursor == null) {
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        final List<Integer> idList = new ArrayList<Integer>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(StatsContract.Monitor.Query.ID);
                    String value = cursor.getString(StatsContract.Monitor.Query.OTHER0);
                    UploadData data = new UploadData(null, 0, value, null, null, null, null, null, null, null);
                    StatsUpload statsUpload = new StringUpload(data);
                    statsUpload.setDefaultInputMethod(defaultInputMethod);
                    String itemData = statsUpload.generator();
                    printStr(itemData, 60);
                    stringBuilder.append(itemData);

                    idList.add(id);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
        } finally {
            cursor.close();
        }

        if (CollectionUtils.isEmpty(idList)) {
            return null;
        }
        return Pair.create(idList, stringBuilder.toString());
    }

    @Override
    public void removeData(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        final int maxId = idList.get(idList.size() - 1);

        int result = BaseApplication.getInstance().getContentResolver().delete(
            StatsContract.Monitor.CONTENT_URI, StatsContract.Monitor.SOURCE + "="
                + mStatsOptions.getSourceType() + " AND "
                + StatsContract.Monitor._ID + " <= " + maxId, null);
        DuboxLog.d(TAG, "onUploadSuccess result:" + result);
    }
}
