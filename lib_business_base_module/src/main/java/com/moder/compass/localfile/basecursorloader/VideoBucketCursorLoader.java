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
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.loader.content.AsyncTaskLoader;

/**
 * Created by huantong on 15/12/15.
 */
public class VideoBucketCursorLoader extends AsyncTaskLoader<MatrixCursor>
        implements MergeCursorManager.LoaderListener {

    private static final String TAG = "BucketCursorLoader";
    private static final int COVER_VIDEO_COUNT = 4;
    private static final String VIDEO_SORT_BY =
            MediaStore.Video.Media.DATE_MODIFIED + " DESC LIMIT " + COVER_VIDEO_COUNT;

    /**
     * 目录优先排序
     */
    private static final String BUCKET_SORT_BY;
    private static final String[] INDEX_VIDEO_FIRST =
            { "camera", "movies", "QQ_Images", "weibo_filter", "save", "MTXX", "Dubox" };

    static {
        final StringBuilder sb = new StringBuilder();
        sb.append("CASE ");

        for (int i = 0; i < INDEX_VIDEO_FIRST.length; i++) {
            sb.append(" WHEN ").append(MediaStore.Video.Media.BUCKET_DISPLAY_NAME).append("='")
                    .append(INDEX_VIDEO_FIRST[i]).append("' COLLATE NOCASE").append(" THEN ").append(i);
        }

        sb.append(" ELSE ").append(INDEX_VIDEO_FIRST.length).append(" END,")
                .append(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

        BUCKET_SORT_BY = sb.toString();
    }

    private final ForceLoadContentObserver mObserver;
    private MatrixCursor mCursor;
    private List<ObserverCursorListener> mObservers = new ArrayList<>();

    private static final String[] COLUMNS =
            { MediaStore.Files.FileColumns._ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media._COUNT };

    private static final String[] PROJECTION =
            { MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.BUCKET_ID,
                    "COUNT(" + MediaStore.Video.Media.DATA + ") AS " + MediaStore.Video.Media._COUNT };

    private static final String[] PROJECTION29 = { MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.BUCKET_ID };

    private static final String SELECTION = "0=0) group by (" + MediaStore.Video.Media.BUCKET_ID;
    private static final String SELECTION29 = null;

    /**
     * @param context Context
     */
    public VideoBucketCursorLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * @return MatrixCursor
     * @see androidx.loader.content.AsyncTaskLoader#loadInBackground()
     */
    @Override
    public MatrixCursor loadInBackground() {
        // 查询所有视频相册
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
            Cursor queryCursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, null, BUCKET_SORT_BY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                HashMap<String, Object[]> bucketCursors = new LinkedHashMap<>();
                bucketCursor = new MatrixCursor(COLUMNS);
                while (queryCursor != null && queryCursor.moveToNext()) {
                    String id = queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String bucketName = queryCursor
                            .getString(queryCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    String bucketId =
                            queryCursor.getString(queryCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
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
            // 遍历视频相册
            if (bucketCursor != null) {
                String videoSelection = MediaStore.Video.Media.BUCKET_ID + "=?";

                while (bucketCursor.moveToNext()) {
                    String id = bucketCursor.getString(bucketCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String bucketId =
                            bucketCursor.getString(bucketCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
                    // 奇葩手机崩溃
                    if (TextUtils.isEmpty(bucketId)) {
                        continue;
                    }
                    String bucketName = bucketCursor
                            .getString(bucketCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    int bucketCount = bucketCursor.getInt(bucketCursor.getColumnIndex(MediaStore.Video.Media._COUNT));

                    // 查询每个相册的前四张照片
                    String[] videoProjection = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA };
                    String[] videoSelectionArgs = { bucketId };
                    Cursor videoCursor =
                            getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    videoProjection, videoSelection, videoSelectionArgs, VIDEO_SORT_BY);

                    // 四个视频构建出MatrixCursor的一行
                    long[] videoIds = new long[COVER_VIDEO_COUNT];
                    String[] videoPaths = new String[COVER_VIDEO_COUNT];
                    if (videoCursor != null) {
                        try {
                            while (videoCursor.moveToNext()) {
                                int index = videoCursor.getPosition();
                                videoIds[index] =
                                        videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media._ID));
                                videoPaths[index] =
                                        videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                            }
                        } finally {
                            // 释放视频cursor资源，并销毁
                            videoCursor.close();
                        }

                        // 为matrixCursor写入数据
                        matrixCursor.addRow(new Object[] { id, bucketId, bucketName, bucketCount, videoIds[0],
                                videoPaths[0], videoIds[1], videoPaths[1], videoIds[2], videoPaths[2], videoIds[3],
                                videoPaths[3] });
                    }
                }
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
        } finally {
            // 释放相册cursor资源，并销毁
            if (bucketCursor != null) {
                bucketCursor.close();
            }
        }

        // 为视频注册视频库uri的监听
        matrixCursor.registerContentObserver(mObserver);
        matrixCursor.setNotificationUri(getContext().getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
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
        mObservers.add(observerCursorListener);
    }

    @Override
    public void remove(ObserverCursorListener observerCursorListener) {
        mObservers.remove(observerCursorListener);
    }

    @Override
    public void notifyCursorObserver() {
        for (ObserverCursorListener observerCursorListener : mObservers) {
            observerCursorListener.observerUpdate();
        }
    }
}
