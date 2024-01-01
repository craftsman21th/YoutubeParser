package com.moder.compass.transfer.transmitter.ratecaculator.impl;

/**
 * 平滑处理帮助类
 *
 * @author sunqi01
 */
public class SmoothHelper {
    /**
     * 平滑处理定长队列
     */
    private FixedSizeQueue<Long> mSmoothingQueue;

    public SmoothHelper(int capacity) {
        mSmoothingQueue = new FixedSizeQueue<Long>(capacity);
    }

    /**
     * 平滑处理
     *
     * @param rate
     *
     * @return
     */
    public long smooth(long rate) {
        synchronized (this) {
            mSmoothingQueue.add(rate);

            long sum = 0L;
            for (Long item : mSmoothingQueue) {
                sum += item;
            }

            int size = mSmoothingQueue.size();
            if (size == 0) {
                return rate;
            }

            rate = sum / size;
            return rate;
        }
    }

    /**
     * 重置
     */
    public void reset() {
        synchronized (this) {
            mSmoothingQueue.clear();
        }
    }

}
