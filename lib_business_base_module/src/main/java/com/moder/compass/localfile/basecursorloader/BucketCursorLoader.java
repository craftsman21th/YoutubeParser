/*
 * BucketCursorLoader.java
 * classes : com.dubox.drive.localfilesystem.p2plocalfile.task.BucketCursorLoader
 * @author 文超
 * V 1.0.0
 * Create at 2014-8-4 下午2:00:18
 */
package com.moder.compass.localfile.basecursorloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dubox.drive.kernel.architecture.db.cursor.CursorUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.localfile.BucketColumns;
import com.moder.compass.localfile.basecursorloader.cursormanager.MergeCursorManager;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.loader.content.AsyncTaskLoader;

/**
 * com.dubox.drive.localfilesystem.p2plocalfile.task.BucketCursorLoader
 *
 * @author 文超 <br/>
 *         create at 2014-8-4 下午2:00:18
 */
public class BucketCursorLoader extends AsyncTaskLoader<MatrixCursor> implements MergeCursorManager.LoaderListener {

    private static final String TAG = "BucketCursorLoader";
    private static final int COVER_PHOTO_COUNT = 4;
    private static final String PHOTO_SORT_BY =
            MediaStore.Images.Media.DATE_MODIFIED + " DESC";

    /**
     * 目录优先排序
     */
    private static final String BUCKET_SORT_BY;
    private static final String[] INDEX_IMAGE_FIRST =
            { "camera", "screenshots", "QQ_Images", "weibo_filter", "save", "MTXX", "Dubox" };

    static {
        final StringBuilder sb = new StringBuilder();
        sb.append("CASE ");

        for (int i = 0; i < INDEX_IMAGE_FIRST.length; i++) {
            sb.append(" WHEN ").append(MediaStore.Images.Media.BUCKET_DISPLAY_NAME).append("='")
                    .append(INDEX_IMAGE_FIRST[i]).append("' COLLATE NOCASE").append(" THEN ").append(i);
        }

        sb.append(" ELSE ").append(INDEX_IMAGE_FIRST.length).append(" END,")
                .append(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        BUCKET_SORT_BY = sb.toString();
    }

    private final ForceLoadContentObserver mObserver;
    private MatrixCursor mCursor;
    private List<ObserverCursorListener> mObserverCursorListeners = new ArrayList<>();

    private static final String[] COLUMNS =
            { MediaStore.Files.FileColumns._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media._COUNT };

    private static final String[] PROJECTION = { MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID,
            "COUNT(" + MediaStore.Images.Media.DATA + ") AS " + MediaStore.Images.Media._COUNT };

    private static final String[] PROJECTION29 = { MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID };

    private static final String SELECTION = "0=0) group by (" + MediaStore.Images.Media.BUCKET_ID;
    private static final String SELECTION29 = null;

    /**
     * @param context Context
     */
    public BucketCursorLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * @return MatrixCursor
     * @see androidx.loader.content.AsyncTaskLoader#loadInBackground()
     */
    @Override
    public MatrixCursor loadInBackground() {
        // 查询所有相册
        String selection;
        String[] projection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection = PROJECTION29;
            selection = SELECTION29;
        } else {
            projection = PROJECTION;
            selection = SELECTION;
        }
        MatrixCursor matrixCursor = new MatrixCursor(BucketColumns.PROJECTION);

        Cursor bucketCursor = null;

        try {
            Cursor queryCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, null, BUCKET_SORT_BY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                HashMap<String, Object[]> bucketCursors = new LinkedHashMap<>();
                bucketCursor = new MatrixCursor(COLUMNS);
                while (queryCursor != null && queryCursor.moveToNext()) {
                    String id = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Images.Media._ID));
                    String bucketName = queryCursor
                            .getString(queryCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String bucketId =
                            queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                    if (TextUtils.isEmpty(bucketId)) {
                        continue;
                    }
                    if (bucketCursors.containsKey(bucketId)) {
                        Object[] objects = bucketCursors.get(bucketId);
                        if (objects == null) {
                            continue;
                        }
                        // 叠加计数相册内的文件数量
                        Integer count = (Integer) objects[COLUMNS.length - 1];
                        objects[COLUMNS.length - 1] = count + 1;
                    } else {
                        bucketCursors.put(bucketId, new Object[] { id, bucketName, bucketId, 1 });
                    }
                }
                CursorUtils.safeClose(queryCursor);
                for (Map.Entry<String, Object[]> entry : bucketCursors.entrySet()) {
                    ((MatrixCursor) bucketCursor).addRow(entry.getValue());
                }
            } else {
                bucketCursor = queryCursor;
            }
            // 遍历相册
            if (bucketCursor != null) {
                String photoSelection = MediaStore.Images.Media.BUCKET_ID + "=?";
                while (bucketCursor.moveToNext()) {
                    String id = bucketCursor.getString(bucketCursor.getColumnIndex(MediaStore.Images.Media._ID));
                    String bucketId =
                            bucketCursor.getString(bucketCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                    String bucketName = bucketCursor
                            .getString(bucketCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    int bucketCount = bucketCursor.getInt(bucketCursor.getColumnIndex(MediaStore.Images.Media._COUNT));
                    if (TextUtils.isEmpty(bucketId)) {
                        continue;
                    }
                    // 查询每个相册的前四张照片
                    String[] photoProjection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
                    String[] photoSelectionArgs = {bucketId};
                    Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendQueryParameter(CallLog.Calls.LIMIT_PARAM_KEY, COVER_PHOTO_COUNT + "").build();
                    Cursor photoCursor =
                        getContext().getContentResolver().query(queryUri,
                            photoProjection, photoSelection, photoSelectionArgs, PHOTO_SORT_BY);

                    // 四张照片构建出MatrixCursor的一行
                    long[] photoIds = new long[COVER_PHOTO_COUNT];
                    String[] photoPaths = new String[COVER_PHOTO_COUNT];
                    if (photoCursor != null) {
                        try {
                            while (photoCursor.moveToNext()) {
                                int index = photoCursor.getPosition();
                                if (index >= 0 && index < photoIds.length) {
                                    photoIds[index] = photoCursor
                                            .getLong(photoCursor.getColumnIndex(MediaStore.Images.Media._ID));
                                    photoPaths[index] = photoCursor
                                            .getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                }
                            }
                        } finally {
                            // 释放图片cursor资源，并销毁
                            CursorUtils.safeClose(photoCursor);
                        }

                        // 为matrixCursor写入数据
                        matrixCursor.addRow(new Object[] { id, bucketId, bucketName, bucketCount, photoIds[0],
                                photoPaths[0], photoIds[1], photoPaths[1], photoIds[2], photoPaths[2], photoIds[3],
                                photoPaths[3] });
                    }
                }
            }
        } catch (SQLiteException e) {
            DuboxLog.w(TAG, "", e);
        } catch (SecurityException e) {
            DuboxLog.w(TAG, "", e);
        } catch (Exception e) {
            DuboxLog.w(TAG, "", e);
        } finally {
            // 释放相册cursor资源，并销毁
            CursorUtils.safeClose(bucketCursor);
        }

        // 为相册注册图片库uri的监听
        matrixCursor.registerContentObserver(mObserver);
        matrixCursor.setNotificationUri(getContext().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // notifyCursorObserver();

        return matrixCursor;
    }

    /**
     * @param cursor MatrixCursor
     * @see androidx.loader.content.Loader#deliverResult(java.lang.Object)
     */
    @Override
    public void deliverResult(MatrixCursor cursor) {
        if (isReset()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        MatrixCursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * 必须由主线程调用
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(MatrixCursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // 保证loader已经停止
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }

    @Override
    public void add(ObserverCursorListener observerCursorListener) {
        mObserverCursorListeners.add(observerCursorListener);
    }

    @Override
    public void remove(ObserverCursorListener observerCursorListener) {
        mObserverCursorListeners.remove(observerCursorListener);
    }

    @Override
    public void notifyCursorObserver() {
        for (ObserverCursorListener observerCursorListener : mObserverCursorListeners) {
            observerCursorListener.observerUpdate();
        }
    }
}
