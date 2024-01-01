package com.moder.compass.stats.storage.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.Nullable;

import com.dubox.drive.kernel.architecture.db.BaseContentProvider;
import com.dubox.drive.kernel.architecture.db.BaseSQLiteOpenHelper;
import com.dubox.drive.kernel.architecture.db.SelectionBuilder;

/**
 * Created by liuliangping on 2016/9/7.
 */
public class StatsProvider extends BaseContentProvider {
    private static final String TAG = "StatsProvider";

    /**
     * 打开数据库的辅助类
     */
    private BaseSQLiteOpenHelper mOpenHelper;

    /**
     * 行为
     */
    private static final int BEHAVIOR = 1;

    /**
     * 行为
     */
    private static final int BEHAVIORS = BEHAVIOR + 1;

    /**
     * 监控
     */
    private static final int MONITOR = BEHAVIORS + 1;

    /**
     * 监控
     */
    private static final int MONITORS = MONITOR + 1;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // com.dubox.drive.stats/behavior
        uriMatcher.addURI(StatsContract.CONTENT_AUTHORITY, "behavior", BEHAVIORS);
        // com.dubox.drive.stats/behavior/#
        uriMatcher.addURI(StatsContract.CONTENT_AUTHORITY, "behavior/#", BEHAVIOR);
        // com.dubox.drive.stats/monitor
        uriMatcher.addURI(StatsContract.CONTENT_AUTHORITY, "monitor", MONITORS);
        // com.dubox.drive.stats/monitor/#
        uriMatcher.addURI(StatsContract.CONTENT_AUTHORITY, "monitor/#", MONITOR);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new StatsDatabase(getContext());
        return true;
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);


    }

    @Override
    protected Uri onInsert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEHAVIOR:
            case BEHAVIORS:
                final long behaviorID = mOpenHelper.getWritableDatabase().insert(Tables.BEHAVIOR, null, values);
                onInsertNotify(uri, values);
                return ContentUris.withAppendedId(uri, behaviorID);
            case MONITOR:
            case MONITORS:
                final long monitorID = mOpenHelper.getWritableDatabase().insert(Tables.MONITOR, null, values);
                onInsertNotify(uri, values);
                return ContentUris.withAppendedId(uri, monitorID);
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Override
    protected int onDelete(Uri uri, String selection, String[] selectionArgs) {
        final int retVal =
                buildEditSelection(uri).where(selection, selectionArgs).delete(mOpenHelper.getWritableDatabase());
        onDeleteNotify(uri);
        return retVal;
    }

    @Override
    protected int onUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int retVal =
                buildEditSelection(uri).where(selection, selectionArgs).update(mOpenHelper.getWritableDatabase(),
                        values);
        onUpdateNotify(uri, values);
        return retVal;
    }

    @Override
    protected void onBeforeApply(SQLiteDatabase db, Uri uri) throws OperationApplicationException {

    }

    @Override
    protected boolean onAfterApply(SQLiteDatabase db, Uri uri) {
        return true;
    }

    @Nullable
    @Override
    protected Cursor onQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Cursor cursor =
                buildQuerySelection(uri).where(selection, selectionArgs).query(mOpenHelper.getReadableDatabase(),
                        projection, sortOrder);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public BaseSQLiteOpenHelper getOpenHelper() {
        return mOpenHelper;
    }

    @Override
    public void onInsertNotify(Uri uri, ContentValues values) {
        notify(uri);
    }

    @Override
    public void onUpdateNotify(Uri uri, ContentValues values) {
        notify(uri);
    }

    @Override
    public void onDeleteNotify(Uri uri) {
        notify(uri);
    }

    private void notify(Uri uri) {
        if (mThreadInTransaction.get()) {
            return;
        }

        getContext().getContentResolver().notifyChange(uri, null, false);
    }

    private SelectionBuilder buildEditSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEHAVIOR:
            case BEHAVIORS:
                return builder.table(Tables.BEHAVIOR);
            case MONITOR:
            case MONITORS:
                return builder.table(Tables.MONITOR);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private SelectionBuilder buildQuerySelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BEHAVIOR:
            case BEHAVIORS:
                return builder.table(Tables.BEHAVIOR);
            case MONITOR:
            case MONITORS:
                return builder.table(Tables.MONITOR);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
