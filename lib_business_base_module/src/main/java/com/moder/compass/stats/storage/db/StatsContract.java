package com.moder.compass.stats.storage.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.dubox.drive.kernel.architecture.AppCommon;

/**
 * Created by liuliangping on 2016/9/7.
 */
public class StatsContract {
    private static final String TAG = "StatsContract";

    public static String CONTENT_AUTHORITY = AppCommon.PACKAGE_NAME + ".stats";

    /**
     * 传输任务总体URI
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    interface StatsBaseColumns extends BaseColumns {
        /**
         * 统计来源字段,用于清除时候使用
         */
        String SOURCE = "source";


        /**
         * 其他信息
         */
        String OP = "op";

        /**
         * 其他信息
         */
        String OTHER0 = "other0";
    }

    protected interface BehaviorColumns extends StatsBaseColumns {
        Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("behavior").build();

        /**
         * 行为计数
         */
        String COUNT = "count";

        /**
         * 其他信息
         */
        String OTHER1 = "other1";

        /**
         * 其他信息
         */
        String OTHER2 = "other2";

        /**
         * 其他信息
         */
        String OTHER3 = "other3";

        /**
         * 其他信息
         */
        String OTHER4 = "other4";

        /**
         * 其他信息
         */
        String OTHER5 = "other5";

        /**
         * 其他信息
         */
        String OTHER6 = "other6";

        /**
         * 行为发生时间
         */
        String OP_TIME = "op_time";
    }

    protected interface MonitorColumns extends StatsBaseColumns {
        //  暂时使用base的列
    }

    public static class Behavior implements BehaviorColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("behavior").build();

        public interface Query {
            /**
             * 投影
             */
            String[] PROJECTION = {Behavior._ID, Behavior.OP, Behavior.SOURCE,
                    Behavior.COUNT, Behavior.OTHER0, Behavior.OTHER1, Behavior.OTHER2,
                    Behavior.OTHER3, Behavior.OTHER4, Behavior.OTHER5, Behavior.OTHER6,
                    Behavior.OP_TIME};

            /**
             * 标识
             */
            int ID = 0;

            /**
             * 统计的唯一值
             */
            int OP = 1;

            /**
             * 来源值，区分所有统计来源
             */
            int SOURCE = 2;

            /**
             * 统计的总数
             */
            int COUNT = 3;

            /**
             * 统计的具体信息
             */
            int OTHER0 = 4;

            /**
             * 统计的扩展字段
             */
            int OTHER1 = 5;

            /**
             * 统计的扩展字段
             */
            int OTHER2 = 6;

            /**
             * 统计的扩展字段
             */
            int OTHER3 = 7;

            /**
             * 统计的扩展字段
             */
            int OTHER4 = 8;

            /**
             * 统计的扩展字段
             */
            int OTHER5 = 9;

            /**
             * 统计的扩展字段
             */
            int OTHER6 = 10;

            /**
             * 动作发生时间
             */
            int OP_TIME = 11;
        }
    }

    public static class Monitor implements MonitorColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("monitor").build();

        public interface Query {
            /**
             * 投影
             */
            String[] PROJECTION = {Monitor._ID, Monitor.OP, Monitor.SOURCE, Monitor.OTHER0};

            /**
             * 标识
             */
            int ID = 0;

            /**
             * 标识
             */
            int OP = 1;

            /**
             * 来源值，区分所有统计来源
             */
            int SOURCE = 2;

            /**
             * 统计的具体信息
             */
            int OTHER0 = 3;
        }
    }
}
