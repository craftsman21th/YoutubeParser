package com.moder.compass.localfile.basecursorloader.cursormanager;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.basemodule.R;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.moder.compass.localfile.BucketColumns;
import com.moder.compass.localfile.basecursorloader.ObserverCursorListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

public class MergeCursorManager implements ObserverCursorListener {

    private static final String TAG = "MergeCursorManager";
    private CursorUpdateListener mCursorUpdateListener;
    private Handler mHandler;
    private HandlerThread mHandlerThread = new HandlerThread("cursorMerge");
    private static final int UPDATE_GAP = 500; // 500ms内重复更新，只处理最新一次
    private Stack<Cursor> mPhoneStack;
    private Stack<Cursor> mVideoStack;
    private Cursor mPhotoCursor = null;
    private Cursor mVideoCursor = null;
    private List<String> mRootDirectoryNames = new ArrayList<>();

    private Runnable mMergeTask = new Runnable() {
        @Override
        public void run() {
            final Cursor cursor = new MatrixCursor(BucketColumns.PROJECTION);
            Map<String, Object[]> map = new HashMap<>();
            // 每次选取最后更新的图片cursor，最新的cursor已经包含了所有的图片库，清空栈中历史中的cursor
            synchronized (mPhoneStack) {
                if (!mPhoneStack.isEmpty()) {
                    mPhotoCursor = mPhoneStack.pop();
                    mPhoneStack.clear();
                }
            }
            if (mPhotoCursor != null && mPhotoCursor.moveToFirst()) {
                do {
                    String id = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns._ID));
                    String bucketId = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.BUCKET_ID));
                    String bucketName = getBucketName(mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.NAME)));
                    int count = mPhotoCursor.getInt(
                            mPhotoCursor.getColumnIndex(BucketColumns._COUNT));
                    String photoPath0 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_0));
                    String photoPath1 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_1));
                    String photoPath2 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_2));
                    String photoPath3 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_3));
                    String photoId0 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_0));
                    String photoId1 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_1));
                    String photoId2 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_2));
                    String photoId3 = mPhotoCursor.getString(
                            mPhotoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_3));
                    String key = getMatchPath(
                            FileUtils.getFileDirectoryWithOutSlash(photoPath0), bucketName);
                    if (TextUtils.isEmpty(key)) {
                        key = bucketName;
                    }
                    map.put(key + bucketId, new Object[] {id, bucketId, bucketName, count, photoId0, photoPath0,
                            photoId1, photoPath1, photoId2, photoPath2, photoId3, photoPath3});
                } while (mPhotoCursor.moveToNext());
            }

            // 每次选取最后更新的视频cursor，最新的cursor已经包含了所有的视频库，清空栈中历史中的cursor
            synchronized (mVideoStack) {
                if (!mVideoStack.isEmpty()) {
                    mVideoCursor = mVideoStack.pop();
                    mVideoStack.clear();
                }
            }
            if (mVideoCursor != null && mVideoCursor.moveToFirst()) {
                do {
                    String id = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns._ID));
                    String bucketId = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.BUCKET_ID));
                    String bucketName = getBucketName(mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.NAME)));
                    int count = mVideoCursor.getInt(
                            mVideoCursor.getColumnIndex(BucketColumns._COUNT));
                    String photoPath0 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_0));
                    String photoPath1 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_1));
                    String photoPath2 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_2));
                    String photoPath3 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_PATH_3));
                    String photoId0 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_0));
                    String photoId1 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_1));
                    String photoId2 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_2));
                    String photoId3 = mVideoCursor.getString(
                            mVideoCursor.getColumnIndex(BucketColumns.COVER_PHOTO_ID_3));
                    String key = getMatchPath(
                            FileUtils.getFileDirectoryWithOutSlash(photoPath0), bucketName);
                    if (TextUtils.isEmpty(key)) {
                        key = bucketName;
                    }
                    Object[] photoObjects = map.get(key);
                    if (null != photoObjects) {
                        photoObjects[3] = (int) (photoObjects[3]) + count;
                    } else {
                        map.put(key + bucketId, new Object[]{id, bucketId, bucketName, count, photoId0, photoPath0,
                                photoId1, photoPath1, photoId2, photoPath2, photoId3, photoPath3});
                    }
                } while (mVideoCursor.moveToNext());
            }
            // 对文件夹按count降序排序
            List<Object[]> list = sortMap(map);
            map.clear();
            mRootDirectoryNames.clear();
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                ((MatrixCursor) cursor).addRow((Object[]) iterator.next());
            }
            list.clear();
            if (null != mCursorUpdateListener) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mCursorUpdateListener.updateCursor(cursor);
                    }
                });
            }
        }
    };

    private String getBucketName(String bucketName) {
        // 相册类型解释
        if (TextUtils.isEmpty(bucketName)) {
            return "";
        }
        if (bucketName.equalsIgnoreCase("Screenshots")) {
            bucketName += BaseApplication.getInstance()
                    .getString(R.string.backup_screenshots);
        } else if (bucketName.equalsIgnoreCase("DCIM")) {
            bucketName += BaseApplication.getInstance()
                    .getString(R.string.backup_DCIM);
        } else if (bucketName.equalsIgnoreCase("moder")) {
            bucketName += BaseApplication.getInstance()
                    .getString(R.string.backup_my_app);
        }
        return bucketName;
    }

    private List<Object[]> sortMap(Map map) {
        List<Object[]> entries = new ArrayList<Object[]>(map.values());
        Collections.sort(entries, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] objects, Object[] t1) {
                return (int) t1[3] - (int) objects[3];
            }
        });
        return entries;
    }

    private MergeCursorManager(@NonNull Context context) {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mPhoneStack = new Stack<>();
        mVideoStack = new Stack<>();
    }

    public static MergeCursorManager getInstance(Context context) {
        return new MergeCursorManager(context);
    }

    /**
     * 递归获取文件后缀和cursor bucket_display_name一致的路径
     *
     * @param rawPath   路径
     * @param matchName bucket_display_name
     * @return cursor对应的唯一key
     */
    private String getMatchPath(String rawPath, String matchName) {
        if (TextUtils.isEmpty(rawPath)
                || TextUtils.isEmpty(matchName)) {
            return null;
        }

        String[] paths = rawPath.split(Matcher.quoteReplacement(File.separator));
        if (null == paths || paths.length == 0) {
            return null;
        }
        if (TextUtils.equals(paths[paths.length - 1], matchName)) {
            return rawPath;
        } else {
            return getMatchPath(FileUtils.getFileDirectoryWithOutSlash(rawPath), matchName);
        }
    }

    public void setPhotoCursor(final MatrixCursor matrixCursor) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mPhoneStack) {
                    mPhoneStack.push(matrixCursor);
                }
            }
        });
    }

    public void setVideoCursor(final MatrixCursor matrixCursor) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mVideoStack) {
                    mVideoStack.push(matrixCursor);
                }
            }
        });
    }

    public void setCursorUpdateListener(CursorUpdateListener cursorUpdateListener) {
        mCursorUpdateListener = cursorUpdateListener;
    }

    @Override
    public void observerUpdate() {
        mHandler.removeCallbacks(mMergeTask);
        mHandler.postDelayed(mMergeTask, UPDATE_GAP);
    }

    public interface LoaderListener {
        void add(ObserverCursorListener observerCursorListener);

        void remove(ObserverCursorListener observerCursorListener);

        void notifyCursorObserver();
    }
}
