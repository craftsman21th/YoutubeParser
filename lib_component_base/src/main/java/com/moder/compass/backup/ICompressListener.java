package com.moder.compass.backup;

/**
 * 压缩任务监听器
 *
 * Created by xujing31 on 2019/8/15.
 */
public interface ICompressListener {

    /**
     * 压缩进度变化
     */
    void onCompressPercentChange(int percent);
}
