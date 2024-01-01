package com.moder.compass.stats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.stats.storage.db.StatsContract;
import com.moder.compass.stats.upload.OldUpload;
import com.moder.compass.stats.upload.Separator;
import com.moder.compass.stats.upload.StatsUpload;
import com.moder.compass.stats.upload.UploadData;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;

class DuboxStatsOld extends DuboxStats {
    private static final String TAG = "DuboxStatsOld";
    /**
     * 数据型统计
     **/
    private static ConcurrentHashMap<String, Integer> intLog = new ConcurrentHashMap<String, Integer>();
    /**
     * 文本型统计
     **/
    private static ConcurrentHashMap<String, String> conLog = new ConcurrentHashMap<String, String>();

    private DuboxStats mDuboxStatsNew;

    /**
     * 对内存的intLog和conLog进行加锁
     */
    private final Object mLockIntAndConLogMap = new Object();

    DuboxStatsOld(DuboxStats duboxStatsNew, StatsOptions options) {
        super(options);
        mDuboxStatsNew = duboxStatsNew;
    }

    @Override
    public void statCount(String key, int value) {
        synchronized(mLockIntAndConLogMap) {
            writeCount++;
            int oldCount = 0;
            Integer tempOldCount = intLog.get(key);
            if (null != tempOldCount) {
                oldCount = tempOldCount;
            }
            int count = oldCount + value;
            if (DuboxLog.isDebug()) {
                DuboxLog.d(TAG, "[writeCount=" + writeCount + "][Key:" + key + "][addValue:" + value + "][AllValue:"
                        + count + "]");
            }
            intLog.put(key, count);
            // 将现有统计项添加到op方式统计
            mDuboxStatsNew.statCount(key, value, null);
        }

        if ((mHasNoReportData || writeCount > mStatsOptions.getMaxMemoryCount()) && !isUploading) {
            mHasNoReportData = false;
            uploadWrapper();
        }
    }

    @Override
    public void statCount(String key, String content) {
        synchronized(mLockIntAndConLogMap) {
            writeCount++;
            if (content != null) {
                conLog.put(key, content);
            }
            // 将现有统计项添加到op方式统计
            mDuboxStatsNew.statCount(key, content);
            DuboxLog.d(TAG, "[writeContent=" + content + "]");
        }

        if ((mHasNoReportData || writeCount > mStatsOptions.getMaxMemoryCount()) && !isUploading) {
            mHasNoReportData = false;
            uploadWrapper();
        }
    }

    @Override
    public void statCount(String key, int value, String[] otherKey) {

    }

    @Override
    protected void saveData() {
        synchronized(mLockIntAndConLogMap) {
            getFormatByte(intLog, conLog);
            intLog.clear();
            conLog.clear();
        }
    }

    /**
     * 将存储数据的hashmap转成byte数组
     *
     * @param intMap
     * @param conMap
     *
     * @return
     */
    private byte[] getFormatByte(ConcurrentHashMap<String, Integer> intMap,
                                 ConcurrentHashMap<String, String> conMap) {
        if (intMap != null && conMap != null) {
            StringBuffer sb = new StringBuffer();
            getUploadNum(intMap, sb);
            getDownloadNum(intMap, sb);
            getAutoUploadNum(intMap, sb);
            Set<Map.Entry<String, Integer>> intSet = intMap.entrySet();
            Set<Map.Entry<String, String>> conSet = conMap.entrySet();
            Iterator<Map.Entry<String, Integer>> iterator = intSet.iterator();
            Iterator<Map.Entry<String, String>> conIterator = conSet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();

                if (entry != null && entry.getKey() != null) {
                    if (entry.getKey().equals(StatisticsLog.StatisticsKeys.TOTAL_PIM_NUM)) {
                        String key = StatisticsLog.StatisticsKeys.TOTAL_PIM_FAILED;
                        int value;
                        sb.append(key);
                        sb.append("=");
                        if (intMap.containsKey(StatisticsLog.StatisticsKeys.TOTAL_PIM_SUCCESS)
                            && intMap.get(StatisticsLog.StatisticsKeys.TOTAL_PIM_SUCCESS) != null) {

                            value = entry.getValue()
                                - intMap.get(StatisticsLog.StatisticsKeys.TOTAL_PIM_SUCCESS);
                            sb.append(value);
                        } else {
                            value = entry.getValue();
                            sb.append(entry.getValue());
                        }
                        insertToDB(key, value);
                        sb.append(Separator.LINE_SPLIT);
                    }

                    if (!entry.getKey().equals(StatisticsLog.StatisticsKeys.TOTAL_PIM_FAILED)
                        && !entry.getKey().equalsIgnoreCase(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD)
                        && !entry.getKey().equalsIgnoreCase(StatisticsLog.StatisticsKeys.TOTAL_UPLOAD)) {
                        String key = entry.getKey();
                        int value = entry.getValue();
                        insertToDB(key, value);
                        sb.append(key);
                        sb.append("=");
                        sb.append(value);
                        sb.append(Separator.LINE_SPLIT);
                    }
                }
            }
            while (conIterator != null && conIterator.hasNext()) {

                Map.Entry<String, String> entry = conIterator.next();

                String key = entry.getKey();
                sb.append(key);
                sb.append("=");
                String value = entry.getValue();
                sb.append(value);
                sb.append(Separator.LINE_SPLIT);

                insertToDB(key, value);
            }

            return sb.toString().getBytes();
        }
        return null;
    }

    /**
     * 计算下载总数 数值为成功数 + 失败数
     *
     * @param cacheIntMap 键值对的统计项
     * @param sb          结果
     */
    private static void getUploadNum(ConcurrentHashMap<String, Integer> cacheIntMap, StringBuffer sb) {
        int succuss = getIntNum(cacheIntMap, StatisticsLog.StatisticsKeys.TOTAL_UPLOAD_SUCCUSS);
        int failed = getIntNum(cacheIntMap, StatisticsLog.StatisticsKeys.TOTAL_UPLOAD_FAILED);
        if ((succuss + failed) != 0) {
            sb.append(StatisticsLog.StatisticsKeys.TOTAL_UPLOAD);
            sb.append("=");
            sb.append(succuss + failed);
            sb.append(Separator.LINE_SPLIT);
        }
    }

    /**
     * 计算下载总数 数值为成功数 + 失败数
     *
     * @param cacheIntMap 键值对的统计项
     * @param sb
     */
    private static void getDownloadNum(ConcurrentHashMap<String, Integer> cacheIntMap, StringBuffer sb) {
        int success = getIntNum(cacheIntMap, StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_SUCCUSS);
        int failed = getIntNum(cacheIntMap, StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD_ERROR);
        if ((success + failed) != 0) {
            sb.append(StatisticsLog.StatisticsKeys.TOTAL_DOWNLOAD);
            sb.append("=");
            sb.append(success + failed);
            sb.append(Separator.LINE_SPLIT);
        }
    }

    /**
     * 计算下载总数 数值为成功数 + 失败数
     *
     * @param cacheIntMap 键值对的统计项
     * @param sb          结果
     */
    private static void getAutoUploadNum(ConcurrentHashMap<String, Integer> cacheIntMap, StringBuffer sb) {
        int success = getIntNum(cacheIntMap, StatisticsLog.StatisticsKeys.AUTO_UPLOAD_SUCCUSS);
        int failed = getIntNum(cacheIntMap, StatisticsLog.StatisticsKeys.AUTO_UPLOAD_FAILED);
        if ((success + failed) != 0) {
            sb.append(StatisticsLog.StatisticsKeys.TOTAL_AUTO_UPLOAD);
            sb.append("=");
            sb.append(success + failed);
            sb.append(Separator.LINE_SPLIT);
        }
    }

    /***
     * 在统计项中获取数据
     *
     * @param cacheIntMap 键值对的统计项
     * @param key 需要获取的key
     * @return 结果
     */
    private static int getIntNum(ConcurrentHashMap<String, Integer> cacheIntMap, String key) {
        int result = 0;
        if (cacheIntMap.containsKey(key)) {
            result = cacheIntMap.get(key);
        }
        return result;
    }

    @Override
    public void uploadWrapper() {
        if (null == scheduler) {
            return;
        }
        scheduler.addLowTask(new BaseJob(TAG) {
            @Override
            protected void performExecute() {
                DuboxStatsOld.this.upload();
            }
        });
    }

    @Override
    public void uploadBackgroundWrapper() {
        if (null == scheduler) {
            return;
        }
        scheduler.addLowTask(new BaseJob(TAG + "_background") {
            @Override
            protected void performExecute() {
                DuboxStatsOld.this.uploadBackground();
            }
        });
    }

    private void insertToDB(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        DuboxLog.d(TAG, "insertToDB key:" + key + " ,String value:" + value);
        mStatsProviderHelper.addBehaviorStats(key, value, mStatsOptions.getSourceType());
    }

    private void insertToDB(String key, int count) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        DuboxLog.d(TAG, "insertToDB key:" + key + " ,int count:" + count);

        mStatsProviderHelper.addBehaviorStats(key, count, mStatsOptions.getSourceType());
    }

    @Override
    protected Pair<List<Integer>, String> readData() {
        Cursor cursor = BaseApplication.getInstance().getContentResolver().query(
            StatsContract.Behavior.CONTENT_URI, StatsContract.Behavior.Query.PROJECTION,
            StatsContract.Behavior.SOURCE + "=" + mStatsOptions.getSourceType(), null,
            StatsContract.Behavior._ID + " ASC LIMIT " + mStatsOptions.getMaxReportCount());

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

                    StatsUpload statsUpload = new OldUpload(data);
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
            StatsContract.Behavior.CONTENT_URI, StatsContract.Behavior.SOURCE + "="
                + mStatsOptions.getSourceType() + " AND "
                + StatsContract.Behavior._ID + " <= " + maxId, null);
        DuboxLog.d(TAG, "onUploadSuccess result:" + result);
    }
}
