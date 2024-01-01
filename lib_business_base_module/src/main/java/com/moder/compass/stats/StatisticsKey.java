package com.moder.compass.stats;

/**
 * Created by liuliangping on 2016/9/14.
 */
public interface StatisticsKey {
    /**
     * 统计项分类键值
     */
    String STATISTICS_KEY_OP = "op";

    /**
     * 统计值
     **/
    String STATISTICS_KEY_COUNT = "count";

    /**
     * 统计项子分类键值前缀，如other0
     **/
    String STATISTICS_KEY_OTHER = "other";

    /**
     * 统计值
     */
    String STATISTICS_KEY_VALUE = "value";

    /**
     * 统计值类型
     **/
    String STATISTICS_KEY_TYPE = "type";
}
