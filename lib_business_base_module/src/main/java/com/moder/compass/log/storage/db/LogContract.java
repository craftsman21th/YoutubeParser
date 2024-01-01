package com.moder.compass.log.storage.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.dubox.drive.kernel.architecture.AppCommon;

/**
 * Created by liuliangping on 2016/3/18.
 */
public class LogContract {
    public static String CONTENT_AUTHORITY = AppCommon.PACKAGE_NAME + ".log";

    /**
     * 传输任务总体URI
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * 数据库默认值1
     */
    public static final String YES = "1";

    /**
     * 数据库默认值0
     */
    public static final String NO = "0";

    protected interface TransferColumns {
        /**
         * 文件的fsid，如：CloudFile的id字段
         */
        String FILE_ID = "file_id";

        /**
         * 上传或下载的云端地址
         */

        String REMOTE_URL = "remote_url";

        /**
         * 本地保存路径
         */
        String LOCAL_URL = "local_url";

        /**
         * 本地保存路径
         */
        String TASK_ID = "task_id";

        /**
         * 是否为流畅视频
         */
        String IS_SMOOTH_VIDEO = "is_smooth_video";
    }

    public static class DownloadFile implements TransferColumns, BaseColumns {
        public static final Uri BASE_CONTENT_URI =
                LogContract.BASE_CONTENT_URI.buildUpon().appendPath("downloadlog").build();
    }

}
