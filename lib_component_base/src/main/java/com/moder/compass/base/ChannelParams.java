package com.moder.compass.base;

/**
 * Created by liaozhengshuang on 2020-02-13.
 * 渠道号相关配置
 */
public class ChannelParams {
    /**
     * 存放channel的文件名
     */
    private String mDefaultChannelFileName;

    /**
     * 默认渠道号
     */
    private String mDefaultChannel;

    public ChannelParams(String mDefaultChannelFileName, String mDefaultChannel) {
        this.mDefaultChannelFileName = mDefaultChannelFileName;
        this.mDefaultChannel = mDefaultChannel;
    }

    public String getmDefaultChannelFileName() {
        return mDefaultChannelFileName;
    }

    public String getmDefaultChannel() {
        return mDefaultChannel;
    }
}
