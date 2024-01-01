package com.moder.compass.util;

import static com.moder.compass.statistics.StatisticsKeysKt.SCREEN_SHOT_NOTICE_SHOW;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.db.cursor.CursorUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.ToastHelper;
import com.moder.compass.statistics.EventStatisticsKt;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * 截屏监听
 */
public class ScreenShotListenManager {

    private static final String TAG = "ScreenShotListenManager";

    /**
     * 读取媒体数据库时需要读取的列
     */
    private static final String[] MEDIA_PROJECTIONS =
            { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN, };

    /**
     * 截屏依据中的路径判断关键字
     */
    private static final String[] KEYWORDS =
            { "screenshot", "screen_shot", "screen-shot", "screen shot", "screencapture", "screen_capture",
                    "screen-capture", "screen capture", "screencap", "screen_cap", "screen-cap", "screen cap" };

    private ScreenShotListenManager.OnScreenShotListener mScreenShotListener =
            new ScreenShotListenManager.OnScreenShotListener() {
                @Override
                public void onShot(String imagePath) {
                    try {
                        DuboxLog.d(TAG, "OnScreenShotListener path:" + imagePath);
                        ToastHelper.showToast(R.string.my_app_security_notice);
                        EventStatisticsKt.statisticViewEvent(SCREEN_SHOT_NOTICE_SHOW);
                    } catch (Exception e) {
                        DuboxLog.e(TAG, e.getMessage(), e);
                    }
                }
            };

    /**
     * 已回调过的路径
     */
    private final List<String> sHasCallbackPaths = new ArrayList<>();

    private Context mContext;

    private WeakReference<OnScreenShotListener> mListener;

    private long mStartListenTime;

    /**
     * 内部存储器内容观察者
     */
    private MediaContentObserver mInternalObserver;

    /**
     * 外部存储器内容观察者
     */
    private MediaContentObserver mExternalObserver;

    /**
     * 运行在 UI 线程的 Handler, 用于运行监听器回调
     */
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    private ScreenShotListenManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("The context must not be null.");
        }
        mContext = context;
    }

    public static ScreenShotListenManager newInstance(Context context) {
        assertInMainThread();
        return new ScreenShotListenManager(context);
    }

    /**
     * 启动监听
     */
    public void startListen() {
        assertInMainThread();

        sHasCallbackPaths.clear();

        // 记录开始监听的时间戳
        mStartListenTime = System.currentTimeMillis();

        // 创建内容观察者
        mInternalObserver =
                new MediaContentObserver(this, MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        mUiHandler);
        mExternalObserver =
                new MediaContentObserver(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        mUiHandler);

        // 注册内容观察者
        mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false, mInternalObserver);
        mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false, mExternalObserver);
    }

    /**
     * 停止监听
     */
    public void stopListen() {
        assertInMainThread();

        // 注销内容观察者
        if (mInternalObserver != null) {
            try {
                mContext.getContentResolver().unregisterContentObserver(mInternalObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mInternalObserver = null;
        }
        if (mExternalObserver != null) {
            try {
                mContext.getContentResolver().unregisterContentObserver(mExternalObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mExternalObserver = null;
        }

        // 清空数据
        mStartListenTime = 0;
        sHasCallbackPaths.clear();
    }

    /**
     * 处理媒体数据库的内容改变
     */
    private void handleMediaContentChange(Uri contentUri) {
        Cursor cursor = null;
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            cursor = mContext.getContentResolver().query(contentUri, MEDIA_PROJECTIONS, null, null,
                    MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1");

            if (cursor == null) {
                DuboxLog.e(TAG, "Deviant logic.");
                return;
            }
            if (!cursor.moveToFirst()) {
                DuboxLog.d(TAG, "Cursor no data.");
                return;
            }

            // 获取各列的索引
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);

            // 获取行数据
            String data = cursor.getString(dataIndex);
            long dateTaken = cursor.getLong(dateTakenIndex);

            // 处理获取到的第一行数据
            handleMediaRowData(data, dateTaken);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            CursorUtils.safeClose(cursor);
        }
    }

    /**
     * 处理获取到的一行数据
     */
    private void handleMediaRowData(String data, long dateTaken) {
        if (checkScreenShot(data, dateTaken)) {
            DuboxLog.d(TAG, "ScreenShot: path = " + data + "; date = " + dateTaken);
            if (mListener != null && mListener.get() != null && !checkCallback(data)) {
                mListener.get().onShot(data);
            }
        } else {
            // 如果在观察区间媒体数据库有数据改变，又不符合截屏规则，则输出到 DuboxLog 待分析
            DuboxLog.w(TAG, "Media content changed, but not screenshot: path = " + data + "; date = " + dateTaken);
        }
    }

    /**
     * 判断指定的数据行是否符合截屏条件
     */
    private boolean checkScreenShot(String data, long dateTaken) {
        /*
         * 判断依据一: 时间判断
         */
        // 如果加入数据库的时间在开始监听之前, 或者与当前时间相差大于10秒, 则认为当前没有截屏
        if (dateTaken < mStartListenTime || (System.currentTimeMillis() - dateTaken) > 10 * 1000) {
            return false;
        }
        /*
         * 判断依据二: 路径判断
         */
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        data = data.toLowerCase();
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (String keyWork : KEYWORDS) {
            if (data.contains(keyWork)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否已回调过, 某些手机ROM截屏一次会发出多次内容改变的通知; <br/>
     * 删除一个图片也会发通知, 同时防止删除图片时误将上一张符合截屏规则的图片当做是当前截屏.
     */
    private boolean checkCallback(String imagePath) {
        if (sHasCallbackPaths.contains(imagePath)) {
            return true;
        }
        // 大概缓存15~20条记录便可
        if (sHasCallbackPaths.size() >= 20) {
            sHasCallbackPaths.subList(0, 5).clear();
        }
        sHasCallbackPaths.add(imagePath);
        return false;
    }

    /**
     * 设置截屏监听器
     */
    public void setListener(OnScreenShotListener listener) {
        mListener = new WeakReference<>(listener);
    }

    public OnScreenShotListener getDefaultListener() {
        return mScreenShotListener;
    }

    public interface OnScreenShotListener {
        void onShot(String imagePath);
    }

    private static void assertInMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            String methodMsg = null;
            if (elements != null && elements.length >= 4) {
                methodMsg = elements[3].toString();
            }
            throw new IllegalStateException("Call the method must be in main thread: " + methodMsg);
        }
    }

    /**
     * 媒体内容观察者(观察媒体数据库的改变)
     */
    private static class MediaContentObserver extends ContentObserver {

        private Uri mContentUri;
        private WeakReference<ScreenShotListenManager> reference;

        public MediaContentObserver(ScreenShotListenManager manager, Uri contentUri,
                                    Handler handler) {
            super(handler);
            reference = new WeakReference<>(manager);
            mContentUri = contentUri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (reference.get() != null) {
                reference.get().handleMediaContentChange(mContentUri);
            }
        }
    }

}
