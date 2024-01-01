package com.moder.compass.stats.storage.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuliangping on 2016/9/7.
 */
public class StatsProviderHelper {
    private static final String TAG = "StatsProviderHelper";

    /**
     * 添加监控类的数据
     *
     * @param data
     * @param source
     */
    public void addMonitorStats(String data, int source) {
        DuboxLog.d(TAG, "addMonitorStats");
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(StatsContract.Monitor.OTHER0, data);
        contentValues.put(StatsContract.Monitor.SOURCE, source);
        BaseApplication.getInstance().getContentResolver().insert(StatsContract.Monitor.CONTENT_URI, contentValues);
    }

    /**
     * 添加行为统计
     *
     * @param key
     * @param value
     * @param source
     */
    public void addBehaviorStats(String key, String value, int source) {
        DuboxLog.d(TAG, "addBehaviorStats");
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(StatsContract.Behavior.OP, key);
        contentValues.put(StatsContract.Behavior.OTHER0, value);
        contentValues.put(StatsContract.Behavior.SOURCE, source);
        BaseApplication.getInstance().getContentResolver().insert(StatsContract.Behavior.CONTENT_URI, contentValues);
    }

    /**
     * 添加行为统计
     *
     * @param key
     * @param count
     * @param source
     */
    public void addBehaviorStats(String key, int count, int source) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        DuboxLog.d(TAG, "addBehaviorStats");
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        Cursor cursor = null;
        int queryCount = 0;
        try {
            cursor = contentResolver.query(StatsContract.Behavior.CONTENT_URI,
                    new String[]{StatsContract.Behavior.COUNT},
                    StatsContract.Behavior.OP + "=? AND " + StatsContract.Behavior.SOURCE + "=?",
                    new String[]{key, String.valueOf(source)}, null);

            if (cursor != null && cursor.moveToFirst()) {
                queryCount = cursor.getInt(cursor.getColumnIndex(StatsContract.Behavior.COUNT));
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (queryCount > 0) { // update
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(StatsContract.Behavior.COUNT, (count + queryCount));
            contentResolver.update(StatsContract.Behavior.CONTENT_URI, contentValues,
                    StatsContract.Behavior.OP + "=? AND " + StatsContract.Behavior.SOURCE + "=?",
                    new String[]{key, String.valueOf(source)});
            return;
        }

        // insert
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(StatsContract.Behavior.OP, key);
        contentValues.put(StatsContract.Behavior.COUNT, count);
        contentValues.put(StatsContract.Behavior.SOURCE, source);
        BaseApplication.getInstance().getContentResolver().insert(StatsContract.Behavior.CONTENT_URI, contentValues);
    }

    /**
     * 添加行为统计
     *
     * @param op
     * @param count
     * @param source
     * @param dataList
     */
    public void addBehaviorStats(String op, int count, int source, List<String> dataList, String opTime) {
        if (TextUtils.isEmpty(op)) {
            return;
        }

        DuboxLog.d(TAG, "addBehaviorStats");
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(StatsContract.Behavior.OP, op);
        contentValues.put(StatsContract.Behavior.COUNT, count);
        contentValues.put(StatsContract.Behavior.SOURCE, source);
        contentValues.put(StatsContract.Behavior.OP_TIME, opTime);

        String selection = StatsContract.Behavior.OP + "=? AND " + StatsContract.Behavior.SOURCE + "=?";
        ArrayList<String> selectList = new ArrayList<String>();
        selectList.add(op);
        selectList.add(String.valueOf(source));

        String column = "other";
        for (int i = 0; i < dataList.size(); i++) {
            String item = dataList.get(i);
            if (!TextUtils.isEmpty(item)) {
                selection += (" AND " + (column + i + "=?"));
                selectList.add(item);

                contentValues.put(column + i, item);
            }
        }

        String[] selectionArgs = selectList.toArray(new String[]{});

        int queryCount = 0;
        String opTimeOld = null;
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(StatsContract.Behavior.CONTENT_URI,
                    new String[]{StatsContract.Behavior.COUNT, StatsContract.Behavior.OP_TIME},
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                queryCount = cursor.getInt(cursor.getColumnIndex(StatsContract.Behavior.COUNT));
                opTimeOld = cursor.getString(cursor.getColumnIndex(StatsContract.Behavior.OP_TIME));
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (queryCount > 0) { // update
            if (!TextUtils.isEmpty(opTimeOld)) {
                ContentValues values = new ContentValues(2);
                values.put(StatsContract.Behavior.COUNT, (count + queryCount));
                try {
                    JSONArray jsonArrayNew = new JSONArray(opTime);
                    JSONArray jsonArrayOld = new JSONArray(opTimeOld);
                    for (int i = 0; i < jsonArrayNew.length(); i++) {
                        jsonArrayOld.put(jsonArrayNew.get(i));
                    }
                    values.put(StatsContract.Behavior.OP_TIME, jsonArrayOld.toString());
                    contentResolver.update(StatsContract.Behavior.CONTENT_URI, values,
                            selection, selectionArgs);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        BaseApplication.getInstance().getContentResolver().insert(StatsContract.Behavior.CONTENT_URI, contentValues);
    }
}
