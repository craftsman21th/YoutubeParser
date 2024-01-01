package com.moder.compass.stats;

/**
 * Created by liuliangping on 2016/9/14.
 */
public enum StatisticsType {
    OLD(1), NEW(2), MTJ(3), VIDEO(4), SPEED(5),
    MONITOR(6), TRANSMIT(7), ADVERTISE(8), JSON(9);

    private int mType;

    StatisticsType(int value) {
        mType = value;
    }

    public int getType() {
        return mType;
    }
}
