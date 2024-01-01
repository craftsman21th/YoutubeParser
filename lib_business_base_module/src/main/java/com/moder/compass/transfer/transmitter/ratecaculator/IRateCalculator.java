package com.moder.compass.transfer.transmitter.ratecaculator;

import android.util.Pair;

/**
 * 速度计算器接口
 *
 * @author sunqi01
 */
public interface IRateCalculator {
    /**
     * 计算速度
     *
     * @param size
     * @return
     */
    long calculate(long size);

    /**
     * 计算速度
     *
     * 设置间隔时间计算的速度
     *
     * @param size
     * @param intervalTime
     */
    Pair<Long, Long> calculate(long size, long intervalTime);

    /**
     * 重置
     */
    void reset();

    /**
     * 计算真实速度
     */
    long calculateRealRate(long size, long intervalTime);

    /**
     * 计算平滑速度
     */
    long calculateSmoothRate(long intervalTime, long threshold);

    /**
     * 一次平滑计算结束
     */
    void calculateSmoothRateEnd();
}
