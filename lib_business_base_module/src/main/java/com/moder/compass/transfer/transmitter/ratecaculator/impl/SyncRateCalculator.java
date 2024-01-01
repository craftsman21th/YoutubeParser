package com.moder.compass.transfer.transmitter.ratecaculator.impl;

import android.util.Pair;

import com.moder.compass.transfer.transmitter.ratecaculator.IRateCalculator;

/**
 * 同步的传输速率计算器
 * <p/>
 * 不适用于多线程
 *
 * @author sunqi01
 */
public class SyncRateCalculator implements IRateCalculator {
    private static final String TAG = "SyncRateCalculator";
    /**
     * 脉冲间隔
     */
    private int mMinPulseInterval = 1000;
    /**
     * 每次计算累计的SIZE
     */
    private long mSumSize = 0L;
    /**
     * 上一次发送回调的时间
     */
    private long mLastCallbackTime = -1L;

    private SmoothHelper mSmoothHelper;

    public SyncRateCalculator() {
        mSmoothHelper = new SmoothHelper(10);
    }

    public void setMinPulseInterval(int minPulseInterval) {
        mMinPulseInterval = minPulseInterval;
    }

    /**
     * 计算
     *
     * @param size
     */
    @Override
    public long calculate(long size) {
        mSumSize += size;
        long currentTime = System.currentTimeMillis();
        if (mLastCallbackTime == -1L) {
            mLastCallbackTime = currentTime;
            return 0L;
        }

        long pulseInterval = currentTime - mLastCallbackTime;
        if (pulseInterval < mMinPulseInterval) {
            return 0L;
        }

        long realRate = ((mSumSize * 1000) / pulseInterval);
        final long smoothRate = sendCallback(realRate);
        mSumSize = 0L;
        mLastCallbackTime = currentTime;
        return smoothRate;
    }

    /**
     * 计算
     *
     * @param size
     * @param intervalTime
     */
    @Override
    public Pair<Long, Long> calculate(long size, long intervalTime) {
        mSumSize += size;
        long currentTime = System.currentTimeMillis();
        if (intervalTime <= 0L) {
            return Pair.create(0L, 0L);
        }

        long realRate = ((mSumSize * 1000) / intervalTime);
        long smoothRate = sendCallback(realRate);
        mSumSize = 0L;
        mLastCallbackTime = currentTime;
        return Pair.create(realRate, smoothRate);

    }

    /**
     * 重置计算器
     */
    @Override
    public void reset() {
        // sendCallback(mSumSize, System.currentTimeMillis() - mLastCallbackTime);
        mSumSize = 0;
        mLastCallbackTime = -1L;
        mSmoothHelper.reset();
    }

    private long sendCallback(long rate) {
        return mSmoothHelper.smooth(rate);
    }

    @Override
    public long calculateRealRate(long size, long intervalTime) {
        return 0L;
    }

    @Override
    public long calculateSmoothRate(long intervalTime, long threshold) {
        return 0L;
    }

    @Override
    public void calculateSmoothRateEnd() {

    }
}
