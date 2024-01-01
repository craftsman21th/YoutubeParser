package com.moder.compass.transfer.transmitter.ratecaculator.impl;

import android.util.Pair;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.transmitter.ratecaculator.IRateCalculator;

import java.util.Random;

/**
 * 多线程传输的速率计算器
 *
 * @author sunqi01
 */
public class MultiThreadRateCalculator implements IRateCalculator {
    private static final String TAG = "MultiThreadRateCalculator";

    /**
     * 平滑处理数组的容量,7.10改成100个，增加平滑效果
     */
    private static final int SMOOTHING_CAPACITY = 100;

    /**
     * 每次计算累计的SIZE
     */
    private long mSumSize = 0L;

    /**
     * 平滑处理帮助类
     */
    private SmoothHelper mSmoothHelper;
    private long mMinTime = 0L;
    private long mMaxTime = 0L;
    private long mThresholdCache = 0L;

    public MultiThreadRateCalculator() {
    }

    @Override
    public long calculate(long deltaSize) {
        long time = System.currentTimeMillis();
        return doCalculate(deltaSize, time);
    }

    @Override
    public void reset() {
        mSumSize = 0L;
        mMinTime = 0L;
        mMaxTime = 0L;
        // 改为延迟加载，优化内存 libin09 2015-7-31
        if (mSmoothHelper != null) {
            mSmoothHelper.reset();
        }
    }

    private long doCalculate(long deltaSize, long time) {
        mSumSize += deltaSize;
        if (0L == mMinTime) {
            mMinTime = time;
        } else if (mMinTime > time) {
            mMinTime = time;
        }

        if (0L == mMaxTime) {
            mMaxTime = time;
        } else if (mMaxTime < time) {
            mMaxTime = time;
        }

        long pulseInterval = mMaxTime - mMinTime;
        if (pulseInterval <= 0L) {
            return -1L;
        }
        long realRate = ((mSumSize * 1000) / pulseInterval);
        long smoothRate = sendCallback(realRate);
        mSumSize = 0L;
        mMinTime = 0L;
        mMaxTime = 0L;
        return smoothRate;
    }

    @Override
    public Pair<Long, Long> calculate(long deltaSize, long intervalTime) {
        mSumSize += deltaSize;
        if (intervalTime <= 0L) {
            return Pair.create(-1L, -1L);
        }

        long realRate = ((mSumSize * 1000) / intervalTime);
        long smoothRate = sendCallback(realRate);
        mSumSize = 0L;
        mMinTime = 0L;
        mMaxTime = 0L;
        return Pair.create(realRate, smoothRate);
    }

    private long sendCallback(long rate) {
        // 改为延迟加载，优化内存 libin09 2015-7-31
        if (mSmoothHelper == null) {
            mSmoothHelper = new SmoothHelper(SMOOTHING_CAPACITY);
        }

        return mSmoothHelper.smooth(rate);
    }

    @Override
    public long calculateRealRate(long deltaSize, long intervalTime) {
        mSumSize += deltaSize;
        if (intervalTime <= 0L) {
            return -1L;
        }

        long realRate = ((deltaSize * 1000) / intervalTime);
        return realRate;
    }

    @Override
    public long calculateSmoothRate(long intervalTime, long threshold) {
        if (intervalTime <= 0L) {
            return -1L;
        }

        long rate = ((mSumSize * 1000) / intervalTime);
        if (threshold > 0L && rate >= 2 * threshold) {
            final Random random = new Random();
            rate = threshold + random.nextInt(50) * (random.nextBoolean() ? 1 : -1);
        }

        if (mThresholdCache > 0L && threshold > 0L && mSmoothHelper != null && mThresholdCache != threshold) {
            mSmoothHelper.reset();
            DuboxLog.d(TAG, "reset");
        }

        mThresholdCache = threshold;

        long smoothRate = sendCallback(rate);

        DuboxLog.d(TAG, "mSumSize:" + mSumSize / 1024 + "KB,intervalTime:" + intervalTime + ",rate:" + rate / 1024 + "KB/s,smoothRate:"
                + smoothRate / 1024 + "KB/s,threshold:" + threshold / 1024 + "KB/s");

        return smoothRate;
    }

    @Override
    public void calculateSmoothRateEnd() {
        mSumSize = 0L;
        mMinTime = 0L;
        mMaxTime = 0L;
    }
}
