package com.moder.compass.stats.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.db.BaseSQLiteOpenHelper;
import com.dubox.drive.kernel.architecture.db.IUpgradable;

/**
 * Created by liuliangping on 2016/9/7.
 */
public class StatsDatabase extends BaseSQLiteOpenHelper {
    private static final String TAG = "StatsDatabase";

    /**
     * 统计数据库名
     */
    private static final String DATABASE_NAME = "stats.db";

    /**
     * 数据库版本
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * @param context
     */
    StatsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    protected IUpgradable getUpgrader() {
        return new Upgrader();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //  创建统计行为表
        createBehaviorTab(db);

        // 创建统计监控表
        createMonitorTab(db);
        DuboxLog.d(TAG, "create db");
    }

    private void createBehaviorTab(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.BEHAVIOR
                + "(" + StatsContract.Behavior._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StatsContract.Behavior.OP + " TEXT,"
                + StatsContract.Behavior.COUNT + " INTEGER,"
                + StatsContract.Behavior.SOURCE + " INTEGER,"
                + StatsContract.Behavior.OTHER0 + " TEXT,"
                + StatsContract.Behavior.OTHER1 + " TEXT,"
                + StatsContract.Behavior.OTHER2 + " TEXT,"
                + StatsContract.Behavior.OTHER3 + " TEXT,"
                + StatsContract.Behavior.OTHER4 + " TEXT,"
                + StatsContract.Behavior.OTHER5 + " TEXT,"
                + StatsContract.Behavior.OTHER6 + " TEXT,"
                + StatsContract.Behavior.OP_TIME + " TEXT)");
    }

    private void createMonitorTab(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS  " + Tables.MONITOR
                + "(" + StatsContract.Monitor._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StatsContract.Behavior.OP + " TEXT,"
                + StatsContract.Monitor.SOURCE + " INTEGER,"
                + StatsContract.Monitor.OTHER0 + " TEXT)");
    }
}
