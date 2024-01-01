package com.moder.compass.stats.upload.network;

/**
 * Created by liuliangping on 2016/9/14.
 */
public abstract class StatsReport implements IReport {
    private static final String TAG = "StatsReport";

    @Override
    public boolean report(byte[] data, String fileName) {
        return false;
    }

    @Override
    public boolean report(String data, String fileName) {
        return false;
    }
}
