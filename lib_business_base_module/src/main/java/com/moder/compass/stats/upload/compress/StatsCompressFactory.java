package com.moder.compass.stats.upload.compress;

/**
 * Created by liuliangping on 2016/9/20.
 */
public class StatsCompressFactory {
    private static final String TAG = "StatsConpressFactory";

    /**
     * 创建压缩处理器
     *
     * @param type
     * @return
     */
    public ICompress createCompressFactory(int type) {
        if (type == ICompress.TYPE_ZIP) {
            return new ZipDataProcessor();
        }
        return null;
    }
}
