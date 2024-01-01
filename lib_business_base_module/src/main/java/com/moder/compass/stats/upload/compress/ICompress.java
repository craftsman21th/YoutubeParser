package com.moder.compass.stats.upload.compress;

/**
 * Created by liuliangping on 2016/9/13.
 */
public interface ICompress {
    /**
     * 对数据进行压缩
     *
     * @param source
     * @return
     */
    byte[] zipCompress(String source);

    /**
     * 不压缩直接使用
     */
    int TYPE_NO_COMPRESS = 0;

    /**
     * 使用zip压缩
     */
    int TYPE_ZIP = 1;
}
