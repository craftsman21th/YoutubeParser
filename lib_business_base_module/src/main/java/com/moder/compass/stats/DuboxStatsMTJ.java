package com.moder.compass.stats;

import android.text.TextUtils;
import android.util.Pair;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

class DuboxStatsMTJ extends DuboxStats {
    private static final String TAG = "DuboxStatsMTJ";

    private DuboxStats mDuboxStatsNew;
    private static final Pattern NEED_DO_DEVICE_COUNT_PREFIX = Pattern.compile("^MTJ_6_2_0.*");
    private static final Pattern NEED_COUNT_DEVICE = Pattern.compile("^DMTJ_");
    private static final String DEVICE_COUNT_KEY_SUFFIX = "_d";
    private final LinkedList<String> mNeedToCountKeyQueue = new LinkedList<String>();

    DuboxStatsMTJ(DuboxStats duboxStatsNew, StatsOptions options) {
        super(options);
        mDuboxStatsNew = duboxStatsNew;
        ExecutorService mExecutor = Executors.newSingleThreadExecutor();
        Thread mDevcieCountDevice = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        synchronized (mNeedToCountKeyQueue) {
                            if (mNeedToCountKeyQueue.size() == 0) {
                                mNeedToCountKeyQueue.wait();
                            }
                            if (mNeedToCountKeyQueue.size() > 0) {
                                boolean needToSendStatistics = false;
                                String key = mNeedToCountKeyQueue.removeFirst();
                                long time = GlobalConfig.getInstance().getLong(key);
                                if (time > 0) {
                                    Date lastTime = new Date(time);
                                    Calendar lastTimeCalender = Calendar.getInstance();
                                    lastTimeCalender.setTime(lastTime);

                                    Date nowTime = new Date();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(nowTime);

                                    int lastYear = lastTimeCalender.get(Calendar.YEAR);
                                    int nowYear = calendar.get(Calendar.YEAR);

                                    int lastDay = lastTimeCalender.get(Calendar.DAY_OF_YEAR);
                                    int nowDay = calendar.get(Calendar.DAY_OF_YEAR);

                                    if (nowYear > lastYear || nowDay > lastDay) { // 如果上次时间不是同一年或者当前日期大于上次日期
                                        needToSendStatistics = true;
                                    }
                                } else {
                                    needToSendStatistics = true;
                                }

                                if (needToSendStatistics) {
                                    long logtime = System.currentTimeMillis();
                                    DuboxLog.d(TAG, "移动统计logtime [" + key + "]: " + logtime);
                                    GlobalConfig.getInstance().putLong(key, logtime);
                                    GlobalConfig.getInstance().commit();
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        DuboxLog.e(TAG, e.getMessage(), e);
                    }
                }

            }
        };
        // 启动设备数统计线程
        mExecutor.execute(mDevcieCountDevice);
    }

    @Override
    public void statCount(String key) {
        mDuboxStatsNew.statCount(key);
        DuboxLog.d(TAG, "移动统计计数{" + System.currentTimeMillis() + "}：" + key);
        updateMTJDeviceCount(key);
    }

    @Override
    public void statCount(String key, int value) {
        mDuboxStatsNew.statCount(key, value, null);
        DuboxLog.d(TAG, "移动统计计数{" + System.currentTimeMillis() + "}：" + key + ":" + value + "次");
        updateMTJDeviceCount(key);
    }

    @Override
    public void statCount(String key, String content) {
        mDuboxStatsNew.statCount(key, content);
        DuboxLog.d(TAG, "移动统计计数{" + System.currentTimeMillis() + "}：" + key + "|SubType:" + content);
        updateMTJDeviceCount(key, content);
    }

    @Override
    public void statCount(String key, int value, String[] otherKey) {
        mDuboxStatsNew.statCount(key, value, otherKey);
        if (otherKey != null && otherKey.length > 1) {
            DuboxLog.d(TAG, "移动统计计数{" + System.currentTimeMillis() + "}：" + key + ":" + otherKey[0] + ":" + value
                    + "次");
            updateMTJDeviceCount(key, otherKey[0]);
        }
    }

    @Override
    public void uploadWrapper() {

    }

    @Override
    public void uploadBackgroundWrapper() {

    }

    @Override
    protected void saveData() {
        mDuboxStatsNew.saveData();
    }

    private boolean isNeedDoDeviceCount(String key) {
        return !TextUtils.isEmpty(key)
                && (NEED_DO_DEVICE_COUNT_PREFIX.matcher(key).find() || NEED_COUNT_DEVICE.matcher(key).find());
    }

    /**
     * @param key
     * @param subType
     */
    private void updateMTJDeviceCount(String key, String subType) {
        if (isNeedDoDeviceCount(key)) {
            synchronized (mNeedToCountKeyQueue) {
                if (TextUtils.isEmpty(subType)) {
                    mNeedToCountKeyQueue.addLast(key + DEVICE_COUNT_KEY_SUFFIX);
                } else {
                    mNeedToCountKeyQueue.addLast(key + "_" + subType + DEVICE_COUNT_KEY_SUFFIX);
                }
                mNeedToCountKeyQueue.notifyAll();
            }
        }
    }

    /**
     * @param key
     */
    private void updateMTJDeviceCount(String key) {
        updateMTJDeviceCount(key, "");
    }

    @Override
    protected Pair<List<Integer>, String> readData() {
        return mDuboxStatsNew.readData();
    }

    @Override
    public void removeData(List<Integer> idList) {
        mDuboxStatsNew.removeData(idList);
    }

    @Override
    public byte[] zipCompress(String data) {
        return mDuboxStatsNew.zipCompress(data);
    }
}
