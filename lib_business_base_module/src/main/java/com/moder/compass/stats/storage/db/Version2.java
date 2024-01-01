package com.moder.compass.stats.storage.db;

import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;

import com.dubox.drive.kernel.architecture.db.Column;
import com.dubox.drive.kernel.architecture.db.IVersion;
import com.dubox.drive.kernel.architecture.db.Table;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * Created by wanghelong on 2018/10/26.<br/>
 * Email: wanghelong
 */
public class Version2 implements IVersion {
    private static final String TAG = "stats_db_version2";

    @Override
    public void handle(@NonNull SQLiteDatabase database) {
        try {
            // 升级云图数据库表，添加图片旋转方向参数
            final Table table = new Table(Tables.BEHAVIOR);
            table
                    .addColumn(StatsContract.Behavior.OP_TIME, Column.Type.TEXT)
                    .alter(database);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }
}
