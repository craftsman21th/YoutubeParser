package com.moder.compass.stats.upload.network;

/**
 * Created by liuliangping on 2016/9/13.
 */
public interface IReport {
    /**
     * 上报数据
     *
     * @param data
     * @param fileName
     * @return 成功为true，否则false
     */
    boolean report(String data, String fileName);

    /**
     * 上报数据
     *
     * @param data
     * @param fileName
     * @return 成功为true，否则false
     */
    boolean report(byte[] data, String fileName);

    /**
     * 上报网盘业务的统计
     */
    int TYPE_DUBOX = 0;

    /**
     * 上报广告的统计
     */
    int TYPE_ADVERTISE = 1;
}
