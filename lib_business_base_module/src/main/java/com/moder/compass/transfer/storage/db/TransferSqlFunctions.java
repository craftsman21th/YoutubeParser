

/*
 * TransferSqlFunctions.java
 * classes : TransferSqlFunctions
 * @author libin09
 * V 1.0.0
 * Create at 2013-12-27 上午11:52:57
 */
package com.moder.compass.transfer.storage.db;

import com.dubox.drive.kernel.architecture.db.Column;

/**
 * com.dubox.drive.provider.transfer.TransferSqlFunctions
 *
 * @author libin09 <br/>
 *         以文法方式实现传输列表中的函数<br/>
 *         create at 2013-12-27 上午11:52:57
 */
public class TransferSqlFunctions {
    /**
     * 格式化时间显示.
     *
     * @param columnName 列名
     *
     * @return 格式化好的数据(例如: 2013-12-24 21:08:51)
     */
    public String formateDate(String columnName) {
        // 不符合规范无法匹配的返回空
        final String NOTHING_FOMATTER = "''";
        // 13位时间戳,毫秒级别
        final String MILLSECOND_FOMATTER = new Column(columnName + "/1000").datatime();
        // 列
        final Column column = new Column(columnName);
        // 10位时间戳,秒级别
        final String SECOND_FOMATTER = column.datatime();

        return "CASE " + column.length() + " WHEN 10 THEN " + SECOND_FOMATTER + " WHEN 13 THEN " + MILLSECOND_FOMATTER
                + " ELSE " + NOTHING_FOMATTER + " END";
    }

    /**
     * 获取当前毫秒级时间戳
     *
     * @return
     */
    public String getCurrentMillisecondsTimestamp() {
        return "(STRFTIME('%s','now') * 1000)";
    }

    /**
     * 获取当前秒级时间戳
     *
     * @return
     */
    public String getCurrentSecondsTimestamp() {
        return "(STRFTIME('%s','now'))";
    }
}