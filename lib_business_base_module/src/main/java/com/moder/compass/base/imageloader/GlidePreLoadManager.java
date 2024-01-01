package com.moder.compass.base.imageloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.dubox.drive.base.imageloader.SimpleFileInfo;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.dubox.glide.LIFOLinkedBlockingDeque;
import com.dubox.glide.Priority;
import com.dubox.glide.load.engine.DiskCacheStrategy;
import com.dubox.glide.load.engine.executor.GlideExecutor;
import com.dubox.glide.request.RequestOptions;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liaozhengshuang on 17/11/16.
 * 预加载缓存云图缩略图管理器
 */

public class GlidePreLoadManager implements IAddTaskFromParentListener, IGlidePreLoadListener, ImageLoadFinishListener {
    private static final String TAG = "GlidePreLoadManager";

    private RequestOptions mRequestOptions;
    private Context mContext;
    private ThumbnailHelper mThumbnailHelper;
    private LIFOLinkedBlockingDeque<IImagePreLoadTask> mTaskList;

    private final Lock lock = new ReentrantLock();
    private final Condition resumeCondition = lock.newCondition();
    private final AtomicBoolean mIsPaused = new AtomicBoolean(false);

    private final Lock loadFinishLock = new ReentrantLock();
    private final Condition addTaskCondition = loadFinishLock.newCondition();
    private ThreadPoolExecutor mExecutor;
    /**
     * 一次最多可添加到ImageLoader的预缓存任务个数
     */
    private int mMaxPoolSize = 0;

    private GlidePreLoadStateNotifier mGlidePreLoadStateNotifier;

    GlidePreLoadManager(Context context, ThumbnailHelper helper) {
        mContext = context;
        mThumbnailHelper = helper;
        mTaskList = new LIFOLinkedBlockingDeque<>();
        mRequestOptions = new RequestOptions().skipMemoryCache(true)
                .apply(new RequestOptions().priority(Priority.LOW))
                .diskCacheStrategy(DiskCacheStrategy.DATA);
        try {
            mExecutor = (ThreadPoolExecutor) GlideExecutor.newSourceExecutor().getExecutor();
        } catch (Exception e) {
            e.printStackTrace();
            DuboxLog.d(TAG, "e = " + e.toString());
            // 防止catch的后续崩溃
            return;
        }
        // 让预缓存线程的优先级比ImageLoader加载图片的线程低一个优先级
        mLoadTaskThread.setPriority(Thread.NORM_PRIORITY - 3);
        // ImageLoader保留2个可用线程用于执行其他紧急加载任务
        mMaxPoolSize = mExecutor.getCorePoolSize() - 2;
        if (mMaxPoolSize < 1) {
            mMaxPoolSize = 1;
        }
        mGlidePreLoadStateNotifier = new GlidePreLoadStateNotifier();
    }

    private Thread mLoadTaskThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (isPaused()) {
                    lock.lock();
                    try {
                        // 若当前为暂停状态则等待
                        while (isPaused()) {
                            resumeCondition.await();
                        }
                    } catch (InterruptedException e) {
                        DuboxLog.e(TAG, e.getMessage(), e);
                        Thread.currentThread().interrupt();
                        break;
                    } finally {
                        lock.unlock();
                    }
                }
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                IImagePreLoadTask task = null;
                // 若队列没有任务 触发Idle回调 增加PreLoad任务
                if (mTaskList.size() <= 0) {
                    mGlidePreLoadStateNotifier.notifyIdle();
                }
                try {
                    // 若队列中没有任务则阻塞
                    task = mTaskList.take();
                } catch (InterruptedException e) {
                    DuboxLog.e(TAG, e.getMessage(), e);
                    Thread.currentThread().interrupt();
                    break;
                }
                if (task == null) {
                    continue;
                }
                if (!task.isExecutableTask()) {
                    // 从数据库获取任务的task直接执行
                    task.execute();
                } else {
                    loadFinishLock.lock();
                    try {
                        int idleCount = mMaxPoolSize - mExecutor.getActiveCount();
                        // 若当前ImageLoader忙，则等待
                        while (idleCount <= 0) {
                            addTaskCondition.await();
                            idleCount = mMaxPoolSize - mExecutor.getActiveCount();
                        }
                    } catch (InterruptedException e) {
                        DuboxLog.e(TAG, e.getMessage(), e);
                        Thread.currentThread().interrupt();
                        break;
                    } finally {
                        loadFinishLock.unlock();
                    }
                    if (!GlideHelper.getInstance().isInDiskCache(task.getLoadUrl())) {
                        Message message = handler.obtainMessage();
                        message.obj = task;
                        handler.sendMessage(message);
                    } else {
                        task.notifyLoaded();
                    }
                }
            }
        }
    });

    // 由于Glide不能在非主线程中执行，所以task放到主线程缓存图片
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IImagePreLoadTask task = (IImagePreLoadTask) msg.obj;
            if (task != null) {
                task.execute();
            }
        }
    };

    private boolean isPaused() {
        return mIsPaused.get();
    }

    /**
     * 开始预加载
     */
    @Override
    public void startPreLoad(boolean isWifiConnected) {
        if (!isWifiConnected) {
            mIsPaused.set(true);
        }
        if (!mLoadTaskThread.isAlive()) {
            mLoadTaskThread.start();
        }
    }

    /**
     * 暂停预加载
     */
    @Override
    public void pausePreLoad() {
        mIsPaused.set(true);
    }

    /**
     * 重新开始预加载
     */
    @Override
    public void resumePreLoad(boolean isWifiConnected) {
        if (!isWifiConnected) {
            mIsPaused.set(true);
            return;
        }
        mIsPaused.set(false);
        lock.lock();
        try {
            resumeCondition.signal();
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stopPreLoad() {
        mTaskList.clear();
        mLoadTaskThread.interrupt();
    }

    /**
     * 完成一个任务缓存的通知
     */
    @Override
    public void imageLoadFinished() {
        if (mLoadTaskThread.isAlive()) {
            notifyAddToLoad();
        }
    }

    private void notifyAddToLoad() {
        int idleCount = mMaxPoolSize - mExecutor.getActiveCount();
        if (idleCount > 0) {
            loadFinishLock.lock();
            try {
                addTaskCondition.signal();
            } catch (Exception e) {
                DuboxLog.e(TAG, e.getMessage(), e);
            } finally {
                loadFinishLock.unlock();
            }
        }
    }

    @Override
    public String generateUrlFromPath(SimpleFileInfo simpleImageFile, ThumbnailSizeType type) {
        return mThumbnailHelper.makeRemoteUrlByPath(simpleImageFile, type);
    }

    @Override
    public int getPreLoadTaskSize() {
        return mTaskList.size();
    }

    public void addTask(IImagePreLoadTask task) {
        mTaskList.offer(task);
    }

    @Override
    public void addTasksFromParent(Fragment fragment, List<SimpleFileInfo> simpleImageFiles,
                                   ThumbnailSizeType type, boolean isUrl) {
        if (isUrl) {
            for (SimpleFileInfo file : simpleImageFiles) {
                addPreLoadTaskByUrl(fragment, file.mUrl, type);
            }
        } else {
            for (SimpleFileInfo file : simpleImageFiles) {
                addPreLoadTask(fragment, file, type);
            }
        }
    }

    @Override
    public void addPreLoadTask(Fragment fragment, SimpleFileInfo simpleImageFile, ThumbnailSizeType type) {
        addPreLoadTaskByUrl(fragment, generateUrlFromPath(simpleImageFile, type), type);
    }

    @Override
    public void addPreLoadTasks(Fragment fragment, List<SimpleFileInfo> simpleImageFiles, ThumbnailSizeType type) {
        if (CollectionUtils.isEmpty(simpleImageFiles)) {
            return;
        }
        for (SimpleFileInfo file : simpleImageFiles) {
            addPreLoadTask(fragment, file, type);
        }
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, ThumbnailSizeType type) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        addTask(new SingleIGlidePreLoadTask(mContext, fragment,
                url, mRequestOptions, mThumbnailHelper.getImageSizeByType(type)));
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, String cacheKey) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        addTask(new SingleIGlidePreLoadTask(mContext, fragment,
                url, cacheKey, mRequestOptions));
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, String cacheKey,
                                    RequestOptions options, GlideImageSize glideImageSize,
                                    IImagePreLoadTask.PreLoadResultListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        addTask(new SingleIGlidePreLoadTask(mContext, fragment,
                url, cacheKey, options, glideImageSize, listener));
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, ThumbnailSizeType type,
                                    IImagePreLoadTask.PreLoadResultListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        addTask(new SingleIGlidePreLoadTask(mContext, fragment,
                url, null, mRequestOptions, mThumbnailHelper.getImageSizeByType(type),
                listener));
    }

    @Override
    public void addPreLoadTaskByUrls(Fragment fragment, List<String> urls, ThumbnailSizeType type) {
        if (CollectionUtils.isEmpty(urls)) {
            return;
        }
        for (String url : urls) {
            addPreLoadTaskByUrl(fragment, url, type);
        }
    }

    @Override
    public void addPreLoadTaskByParent(Fragment fragment, ThumbnailSizeType type, PreLoadExtraParams params) {
        if (params == null) {
            return;
        }
        addTask(new LoadByParentPathTask(mContext, fragment, type, params, this));
    }

    @Override
    public void addPreLoadTaskByUrl(byte[] bytes, ThumbnailSizeType type, String md5) {
        if (TextUtils.isEmpty(md5)) {
            return;
        }
        addTask(new SingleIGlidePreLoadTask(mContext, null,
                bytes, mRequestOptions, mThumbnailHelper.getImageSizeByType(type), md5));
    }

    @Override
    public void registerPreLoadStateListener(IGlidePreLoadIdleListener stateListener) {
        mGlidePreLoadStateNotifier.registerPreLoadStateListener(stateListener);
    }

    @Override
    public void unregisterPreLoadStateListener(IGlidePreLoadIdleListener stateListener) {
        mGlidePreLoadStateNotifier.unregisterPreLoadStateListener(stateListener);
    }

}
