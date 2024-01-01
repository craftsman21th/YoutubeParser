package com.moder.compass.stats.upload;

import java.util.List;

/**
 * Created by liuliangping on 2016/9/12.
 */
public interface IUploadStatsListener {
    /**
     * 上传开始
     */
    void onUploadBegin();

    /**
     * 上传结束
     *
     */
    void onUploadSuccess(List<Integer> successList);

    /**
     * 上传错误
     */
    void onUploadError();
}
