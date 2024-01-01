package com.moder.compass.log.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.moder.compass.log.storage.db.transfer.TransferTab;
import com.dubox.drive.kernel.architecture.db.BaseSQLiteOpenHelper;
import com.dubox.drive.kernel.architecture.db.IUpgradable;

/**
 * Created by liuliangping on 2016/3/18.
 */
class LogDatabase extends BaseSQLiteOpenHelper {
    private static final String TAG = "LogDatabase";

    /**
     * 数据库名
     */
    private static final String DATABASE_NAME = "log.db";

    /**
     * 数据库版本
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * @param context
     */
    LogDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    protected IUpgradable getUpgrader() {
        return new Upgrader();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 传输日志模块数据库
        createTab(db);
    }

    private void createTab(SQLiteDatabase db) {
        // 下载
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TransferTab.DOWNLOAD_TASK_FILES + "(" + LogContract.DownloadFile._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + LogContract.DownloadFile.LOCAL_URL + " TEXT NOT NULL,"
                + LogContract.DownloadFile.REMOTE_URL + " TEXT NOT NULL,"
                + LogContract.DownloadFile.IS_SMOOTH_VIDEO + " BOOLEAN NOT NULL DEFAULT 0,"
                + LogContract.DownloadFile.FILE_ID
                + " TEXT NOT NULL, UNIQUE(" + LogContract.DownloadFile.LOCAL_URL
                + "," + LogContract.DownloadFile.IS_SMOOTH_VIDEO
                + ") ON CONFLICT REPLACE)");
    }
}
