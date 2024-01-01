package com.moder.compass.log.storage.db;

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
import com.moder.compass.log.storage.db.transfer.TransferTab;

/**
 * Created by liuliangping on 2016/3/18.
 */
public class LogProvider extends BaseContentProvider {
    private static final String TAG = "LogProvider";

    /**
     * 所有下载
     */
    private static final int DOWNLOADS = 1;

    /**
     * 某一条下载
     */
    private static final int DOWNLOAD = DOWNLOADS + 1;

    /**
     * 打开数据库的辅助类
     */
    private BaseSQLiteOpenHelper mLogHelper;

    /**
     * 匹配Uri
     */
    private UriMatcher mUriMatcher;

    @Override
    protected void onBeforeApply(SQLiteDatabase db, Uri uri) throws OperationApplicationException {

    }

    @Override
    protected boolean onAfterApply(SQLiteDatabase db, Uri uri) {
        return true;
    }

    @Override
    public boolean onCreate() {
        mLogHelper = new LogDatabase(getContext());
        return true;
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);

        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // com.dubox.drive.log/downloadlog
        mUriMatcher.addURI(LogContract.CONTENT_AUTHORITY, "downloadlog", DOWNLOADS);
        // com.dubox.drive.log/downloadlog/#
        mUriMatcher.addURI(LogContract.CONTENT_AUTHORITY, "downloadlog/#", DOWNLOAD);
    }

    @Override
    protected Uri onInsert(Uri uri, ContentValues values) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case DOWNLOADS:
            case DOWNLOAD:
                long downloadId =
                        mLogHelper.getWritableDatabase().insert(TransferTab.DOWNLOAD_TASK_FILES, null, values);
                onInsertNotify(uri, values);
                if (downloadId > 0L) {
                    return ContentUris.withAppendedId(uri, downloadId);
                }
                return uri;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Override
    protected int onDelete(Uri uri, String selection, String[] selectionArgs) {
        final int retVal =
                buildEditSelection(uri).where(selection, selectionArgs).delete(mLogHelper.getWritableDatabase());
        onDeleteNotify(uri);
        return retVal;
    }

    @Override
    protected int onUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int retVal = buildEditSelection(uri).where(selection, selectionArgs)
                .update(mLogHelper.getWritableDatabase(), values);
        onUpdateNotify(uri, values);
        return retVal;
    }

    @Nullable
    @Override
    public Cursor onQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Cursor cursor = buildQuerySelection(uri).where(selection, selectionArgs)
                .query(mLogHelper.getReadableDatabase(), projection, sortOrder);

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
        return mLogHelper;
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

    /**
     * 删除，修改使用 Build a simple {@link SelectionBuilder} to match the requested {@link Uri}. This is usually enough to
     * support {@link #insert}, {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildEditSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case DOWNLOADS:
            case DOWNLOAD:
                return builder.table(TransferTab.DOWNLOAD_TASK_FILES);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * 通知uri监听的界面更新
     *
     * @param uri
     */
    private void notify(Uri uri) {
        if (mThreadInTransaction.get()) {
            return;
        }

        getContext().getContentResolver().notifyChange(uri, null, false);
    }

    /**
     * 查询使用 Build an advanced {@link SelectionBuilder} to match the requested {@link Uri}. This is usually only used by
     * {@link #query}, since it performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildQuerySelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case DOWNLOADS:
            case DOWNLOAD:
                return builder.table(TransferTab.DOWNLOAD_TASK_FILES);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
