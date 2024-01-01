package com.moder.compass.stats;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;

import com.moder.compass.BaseApplication;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.dubox.drive.kernel.BuildConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.moder.compass.stats.storage.db.StatsContract;
import com.moder.compass.stats.upload.NewUpload;
import com.moder.compass.stats.upload.Separator;
import com.moder.compass.stats.upload.StatsCache;
import com.moder.compass.stats.upload.StatsUpload;
import com.moder.compass.stats.upload.UploadData;
import com.mars.kotlin.extension.LoggerKt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DuboxStatsNew extends DuboxStats {

    private static final String TAG = "DuboxStatsNew";

    /**
     * 数据型统计
     */
    private ConcurrentHashMap<String, StatsCache> statsMap = new ConcurrentHashMap<String, StatsCache>();

    /**
     * 上次上报的时间戳
     */
    private long mUploadTimestamp = 0L;

    /**
     * 两次上报之间时间间隔
     */
    private static final long UPLOAD_TIME_INTERVAL = 20 * 1000L;

    /**
     * 对内存的map进行加锁
     */
    private final Object mLockCacheMap = new Object();

    DuboxStatsNew(StatsOptions options) {
        super(options);
    }

    @Override
    public void statCount(String key, String content) {
        statCount(key, 1, new String[]{content});
    }

    @Override
    public void statCount(String key, int value, String[] otherKey) {
        if (TextUtils.isEmpty(key)) {
            DuboxLog.d(TAG, "op is wrong.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(StatisticsKey.STATISTICS_KEY_OP + Separator.ITEM_EQUALS + key + Separator.ITEM_SPLIT);
        if (null != otherKey && 0 < otherKey.length) {
            for (int i = 0; i < otherKey.length; i++) {
                sb.append(StatisticsKey.STATISTICS_KEY_OTHER + i + Separator.ITEM_EQUALS);
                sb.append(otherKey[i]);
                sb.append(Separator.ITEM_SPLIT);
            }
        }
        sb.append(StatisticsKey.STATISTICS_KEY_COUNT);

        statCount(sb.toString(), value);
    }

    @Override
    public void statCount(String key, int value) {
        synchronized (mLockCacheMap) {
            StatsCache cache;
            writeCount++;
            if (statsMap.containsKey(key)) {
                cache = statsMap.get(key);
                cache.addCount(value);
            } else {
                cache = new StatsCache();
                cache.addCount(value);
                cache.setKey(key);
                statsMap.put(key, cache);
            }
            /*
             *  DuboxLog.d() 调用时不会打印日志，但是字符串拼接会执行，所以做判断，衡量收益
             * */
            if (BuildConfig.DEBUG || DuboxLog.isDebug()) {
                LoggerKt.d("[writeCount=" + writeCount + "]" +
                                "[Key:" + key + "]" +
                                "[addValue:" + value + "]" +
                                "[AllValue:" + cache.getCount() + "]" +
                                " this:" + this,
                        TAG);
            }
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
    protected void saveData() {
        ConcurrentHashMap<String, StatsCache> tmpStatsMap;
        synchronized (mLockCacheMap) {
            // 卡顿优化 使用临时变量复制需保存数据 避免主线程等待IO操作
            // saveMapData(statsMap);
            tmpStatsMap = new ConcurrentHashMap<>(statsMap);
            statsMap.clear();
        }
        saveMapData(tmpStatsMap);
    }

    /**
     * 将存储数据的hashmap转成byte数组
     *
     * @param intMap
     * @return
     */
    private void saveMapData(ConcurrentHashMap<String, StatsCache> intMap) {
        if (null == intMap || intMap.size() == 0) {
            return;
        }

        Set<Map.Entry<String, StatsCache>> intSet = intMap.entrySet();
        for (Map.Entry<String, StatsCache> entry : intSet) {
            String key = entry.getKey();
            StatsCache cache = entry.getValue();

            insertToDB(key, cache);
        }
    }

    /**
     * @since moder 2.19.0
     * 针对账号登陆成功后旧的埋点无法上报问题提供该方法其他地方不应该调用
     */
    public void uploadForce() {
        mUploadTimestamp = 0;
        uploadWrapper();
    }

    @Override
    public void uploadWrapper() {
        if (null == scheduler || System.currentTimeMillis() < mUploadTimestamp + UPLOAD_TIME_INTERVAL) {
            return;
        }
        mUploadTimestamp = System.currentTimeMillis();
        scheduler.addLowTask(
                new BaseJob(TAG) {
                    @Override
                    protected void performExecute() {
                        if (mOnUpdateLog != null) {
                            mOnUpdateLog.doBeforeUpdate();
                        }
                        DuboxStatsNew.this.upload();
                    }
                });
    }

    @Override
    public void uploadBackgroundWrapper() {
        if (null == scheduler) {
            return;
        }
        scheduler.addLowTask(
                new BaseJob(TAG + "_background") {
                    @Override
                    protected void performExecute() {
                        if (mOnUpdateLog != null) {
                            mOnUpdateLog.doBeforeUpdate();
                        }
                        DuboxStatsNew.this.uploadBackground();
                    }
                });
    }

    private OnUpdateLog mOnUpdateLog;

    public void setOnUpdateLog(OnUpdateLog onUpdateLog) {
        this.mOnUpdateLog = onUpdateLog;
    }

    public interface OnUpdateLog {
        void doBeforeUpdate();
    }

    /**
     * 插入数据库
     *
     * @param key
     * @param cache
     */
    private void insertToDB(String key, StatsCache cache) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        DuboxLog.d(TAG, "new_insertToDB key:" + key + " ,int value:" + cache.getCount()
                + " op_time:" + cache.getCountTimePairs());

        String[] tempData = key.split(Separator.ITEM_SPLIT);
        List<String> rootList = Arrays.asList(tempData);
        String op = null;
        List<String> others = new ArrayList<String>();

        int otherIndex = 0;
        for (String item : rootList) {
            if (item.startsWith(StatisticsKey.STATISTICS_KEY_OP + Separator.ITEM_EQUALS)) {
                String[] opStr = item.split(Separator.ITEM_EQUALS);
                if (opStr.length > 1) {
                    op = opStr[1];
                }
                continue;
            }

            if (item.startsWith(StatisticsKey.STATISTICS_KEY_OTHER + otherIndex + Separator.ITEM_EQUALS)) {
                String[] otherStr = item.split(Separator.ITEM_EQUALS);
                if (otherStr.length > 1) {
                    others.add(otherStr[1]);
                }
                otherIndex++;
            }
        }
        mStatsProviderHelper.addBehaviorStats(op, cache.getCount(),
                mStatsOptions.getSourceType(), others, cache.getCountTimePairs());
    }

    @Override
    protected Pair<List<Integer>, String> readData() {
        final ConfigSystemLimit config = ConfigSystemLimit.getInstance();
        String where = StatsContract.Behavior.SOURCE + "=" + mStatsOptions.getSourceType();
        if (!TextUtils.isEmpty(config.ignoreReportStatsOp)) {
            // 根据op来过滤选择有用的数据
            where += " AND " + StatsContract.Behavior.OP + " NOT LIKE '" + config.ignoreReportStatsOp + "%'";
        }

        Cursor cursor = BaseApplication.getInstance().getContentResolver().query(
                StatsContract.Behavior.CONTENT_URI, StatsContract.Behavior.Query.PROJECTION,
                where, null, StatsContract.Behavior._ID + " ASC LIMIT " + mStatsOptions.getMaxReportCount());

        if (cursor == null) {
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        final List<Integer> idList = new ArrayList<Integer>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(StatsContract.Behavior.Query.ID);
                    String op = cursor.getString(StatsContract.Behavior.Query.OP);
                    int count = cursor.getInt(StatsContract.Behavior.Query.COUNT);
                    String other0 = cursor.getString(StatsContract.Behavior.Query.OTHER0);
                    String other1 = cursor.getString(StatsContract.Behavior.Query.OTHER1);
                    String other2 = cursor.getString(StatsContract.Behavior.Query.OTHER2);
                    String other3 = cursor.getString(StatsContract.Behavior.Query.OTHER3);
                    String other4 = cursor.getString(StatsContract.Behavior.Query.OTHER4);
                    String other5 = cursor.getString(StatsContract.Behavior.Query.OTHER5);
                    String other6 = cursor.getString(StatsContract.Behavior.Query.OTHER6);
                    String opTime = cursor.getString(StatsContract.Behavior.Query.OP_TIME);

                    UploadData data = new UploadData(op, count, other0,
                            other1, other2, other3, other4, other5, other6, opTime);

                    StatsUpload statsUpload = new NewUpload(data);
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
        DuboxLog.d(TAG, "delete maxId:" + maxId);

        int result = BaseApplication.getInstance().getContentResolver().delete(
                StatsContract.Behavior.CONTENT_URI, StatsContract.Behavior.SOURCE + "="
                        + mStatsOptions.getSourceType() + " AND "
                        + StatsContract.Behavior._ID + " <= " + maxId, null);
        DuboxLog.d(TAG, "onUploadSuccess result:" + result);
    }

}
