package com.moder.compass.transfer.task;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.moder.compass.account.Account;
import com.moder.compass.base.utils.EventCenterHandler;
import com.moder.compass.base.utils.NetConfigUtil;
import com.dubox.drive.cloudfile.storage.config.TransferFileConfigKey;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.util.FirebaseRemoteConfigKeysKt;
import com.cocobox.library.CallbackInterface;
import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.android.util.network.NetWorkVerifier;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageUtils;
import com.moder.compass.log.transfer.ITransferCalculable;
import com.moder.compass.log.transfer.ITransferStatisticsAble;
import com.moder.compass.transfer.P2PManager;
import com.moder.compass.transfer.task.notification.OnTransferNotificationListener;
import com.moder.compass.transfer.transmitter.p2p.OnP2PTaskListener;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 多任务下载调度
 *
 * @author libin09
 */
public class MultiTaskScheduler implements Callback, Runnable, ITransferStatisticsAble, OnP2PTaskListener {
    private static final String TAG = "MultiTaskScheduler";

    private static final int ACTION_RUN_TRANSMITTER = 0;

    private static final int ACTION_UPDATE_PROGRESS = 1;

    private static final String SQL_PLACEHOLDER = "=?";

    /**
     * 上次更新通知栏时的进度，用于确定是否要更新通知栏，进度没有变化时，不更新通知栏
     */
    private double mLastProgress;

    /**
     * 上次更新通知栏时的速度，用于确定是否要更新通知栏，速度没有变化时，不更新通知栏
     */
    private volatile long mLastRate;

    /**
     * 切换到2G后，出现弹窗或通知栏，让用户确认是否继续
     */
    private boolean mIsWaitingForConfirm2G;

    /**
     * 运行任务的总速度
     */
    private volatile long mInstantRate;

    /**
     * 运行任务的个数
     */
    private volatile int mInstantRunningCount;


    @Override
    public int getStatisticsTaskCount() {
        return mInstantRunningCount;
    }

    @Override
    public long getStatisticsSumRate(ITransferCalculable.TransferLogType type) {
        if (type == ITransferCalculable.TransferLogType.UPLOAD) {
            return mLastRate;
        }

        return mInstantRate;
    }

    /**
     * com.dubox.drive.task.PendingTaskObserver
     *
     * @author libin09 <br/>
     *         数据库任务监听 <br/>
     *         create at 2014-1-14 下午3:58:36
     */
    private final class PendingTaskObserver extends ContentObserver {
        PendingTaskObserver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            DuboxLog.d(TAG, "启动调度器。。。");
            MultiTaskScheduler.this.sendUpdateFromProviderMessage();
        }
    }

    /**
     * 默认的任务并发数
     */
    public static final int DEFAULT_MULTI_TASK_COUNT = 2;

    /**
     * 并发任务数
     */
    private int mMultiTaskCount = DEFAULT_MULTI_TASK_COUNT;

    private final PendingTaskObserver mPendingTaskObserver;

    private final ContentResolver mResolver;

    private final Uri mUri;

    private final AbstractSchedulerFactory mTransferFactory;

    protected final List<TransferTask> mTaskCache;

    private final OnTransferNotificationListener mTransferNotificationListener;

    private final Context mContext;

    // 限速器
    private final RateLimiter mRateLimiter;

    private HandlerThread mUpdateThread;

    private Handler mUpdateHandler;

    /**
     * p2p sdk回调代理
     *
     * @author libin09 2015-12-3
     */
    private P2PSDKCallbackProxy mP2PSDKCallbackProxy;

    private final ITransferCalculable mTransferCalculable;

    private final P2PManager mP2PManager;

    public MultiTaskScheduler(Context context, AbstractSchedulerFactory transferFactory,
                              OnTransferNotificationListener listener,
                              P2PSDKCallbackProxy p2PSDKCallbackProxy, ITransferCalculable transferCalculable) {
        mContext = context;

        mResolver = context.getContentResolver();

        mTransferFactory = transferFactory;

        mUri = transferFactory.createUpdateUri();

        mPendingTaskObserver = new PendingTaskObserver();

        mTaskCache = Collections.synchronizedList(new LinkedList<TransferTask>());

        mTransferNotificationListener = listener;

        mLastProgress = -1.0;

        mLastRate = -1L;

        mRateLimiter = new RateLimiter(mTaskCache);

        mP2PManager = new P2PManager();

        mP2PSDKCallbackProxy = p2PSDKCallbackProxy;

        mTransferCalculable = transferCalculable;
    }

    @Override
    public void onP2PCreateFailed(long taskId) {
        if (mTaskCache == null) {
            return;
        }

        // 如果p2p任务创建失败，需要CDN下载
        for (TransferTask taskInfo : mTaskCache) {
            if (taskInfo != null && taskInfo.mTaskId == taskId) {
                if (TransferContract.Tasks.STATE_RUNNING == taskInfo.mState && taskInfo.transmitter != null) {
                    taskInfo.mIsP2PFailed = true;
                    taskInfo.performStart(mResolver, getP2PCallback(), this);
                }
                return;
            }
        }
    }

    @Override
    public void onP2PStop(String fgid) {
        P2PSDKCallbackProxy proxy = getP2PCallback();
        if (proxy != null) {
            proxy.remove(fgid);
        }
    }

    @Override
    public void onSDKTaskTypeSet(long taskId, boolean isSDKTask) {
        if (mTaskCache == null) {
            return;
        }
        for (TransferTask taskInfo : mTaskCache) {
            if (taskInfo != null && taskInfo.mTaskId == taskId) {
                taskInfo.mIsDownloadSDKTask = isSDKTask;
            }
        }
    }

    @Override
    public void onP2PTaskTypeSet(long taskId, boolean isP2PTask) {
        if (mTaskCache == null) {
            return;
        }
        for (TransferTask taskInfo : mTaskCache) {
            if (taskInfo != null && taskInfo.mTaskId == taskId) {
                taskInfo.mIsP2PTask = isP2PTask;
            }
        }
    }

    public void start() {
        mResolver.registerContentObserver(mUri, true, mPendingTaskObserver);

        mIsWaitingForConfirm2G = false;

        NetWorkVerifier.reset();

        mUpdateThread = new HandlerThread(TAG + "-UpdateThread");
        mUpdateThread.start();

        TaskSchedulerImpl.INSTANCE.addMiddleTask(new BaseJob("MultiTaskScheduler") {
            @Override
            protected void performExecute() {
                run();
            }
        });
    }

    /**
     * 网络恢复，重新调度
     */
    public void restart() {
        NetWorkVerifier.reset();

        mIsWaitingForConfirm2G = false;

        sendUpdateFromProviderMessage();
    }

    /**
     * 停止调度器
     *
     * @param isPauseTask 是否暂停任务。无网络时停止调度器不暂停任务，退出登陆时暂停
     */
    public void stop(boolean isPauseTask) {
        DuboxLog.d(TAG, "stop isPauseTask:" + isPauseTask);
        mResolver.unregisterContentObserver(mPendingTaskObserver);
        if (mUpdateHandler != null) {
            mUpdateHandler.removeCallbacksAndMessages(null);
        }
        mUpdateThread.quit();

        if (isPauseTask) {
            pauseAllTask();
        }
    }

    /**
     * 启动调试器
     */
    private void sendUpdateFromProviderMessage() {
        if (mUpdateHandler == null) {
            return;
        }
        mUpdateHandler.sendEmptyMessage(ACTION_RUN_TRANSMITTER);
    }

    /**
     * 更新通知栏
     */
    private void sendUpdateProgressMessage() {
        if (mUpdateHandler == null) {
            return;
        }
        mUpdateHandler.removeMessages(ACTION_UPDATE_PROGRESS);
        mUpdateHandler.sendEmptyMessageDelayed(ACTION_UPDATE_PROGRESS, 200L);
    }

    private void sendTransferRateMessage(long rate, long instantRate) {
        Bundle b = new Bundle();
        b.putLong(TransferFileConfigKey.KEY_RUNNING_ALL_TASKS_RATE, rate);
        b.putLong(TransferFileConfigKey.KEY_RUNNING_INSTANT_RATE, instantRate);
        b.putInt(TransferFileConfigKey.KEY_TRANSFER_TASK_TYPE, mTransferFactory == null ? -1 :
                mTransferFactory.getNotificationType());
        EventCenterHandler.INSTANCE.sendMsg(TransferFileConfigKey.MSG_CODE_TRANSFER_TASK_RATE, -1, -1, b);
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        // 异步处理getLooper，防止阻塞UI
        final Looper looper = mUpdateThread.getLooper();

        if (looper == null) {
            // 线程已经被销毁
            return;
        }

        mUpdateHandler = new Handler(looper, this);
        sendUpdateFromProviderMessage();
    }

    /**
     * @param msg
     * @return
     * @see android.os.Handler.Callback#handleMessage(android.os.Message)
     */
    @Override
    public boolean handleMessage(Message msg) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        switch (msg.what) {
            // 同步本地缓存，调度
            case ACTION_RUN_TRANSMITTER:
                syncCache();
                return true;
            // 更新进度
            case ACTION_UPDATE_PROGRESS:
                updateProgress();
                return true;
            default:
                return true;
        }
    }

    /**
     * 更新缓存
     */
    private synchronized void syncCache() {
        DuboxLog.d(TAG, "同步开始");
        final long start = System.currentTimeMillis();

        final Cursor cursor = queryDB();

        if (cursor == null) {
            DuboxLog.d(TAG, "cursor is null");
            return;
        }

        // 用于反查需要删除的任务
        final Set<Integer> tempData = initInvalidTaskSearcherData();
        // 同步内存中所有缓存的状态
        syncCacheInfo(cursor, tempData);

        // 找出删除的任务
        clearCache(tempData);

        DuboxLog.d(TAG, "同步结束,耗时:" + (System.currentTimeMillis() - start) / DateUtils.SECOND_IN_MILLIS);
    }

    /**
     * 从数据库取出数据
     *
     * @return
     */
    private Cursor queryDB() {
        try {

            return mResolver.query(mUri, mTransferFactory.createProjection(),
                    buildSql() + TransferContract.Tasks.STATE + "=" + String.valueOf(TransferContract.Tasks.STATE_PENDING) + " OR " + TransferContract.Tasks.STATE + "="
                            + String.valueOf(TransferContract.Tasks.STATE_RUNNING), null, "CASE WHEN " + TransferContract.Tasks.STATE + "="
                            + TransferContract.Tasks.STATE_PAUSE + " THEN 0 WHEN " + TransferContract.Tasks.STATE + "=" + TransferContract.Tasks.STATE_RUNNING
                            + " THEN 1 WHEN " + TransferContract.Tasks.STATE + "=" + TransferContract.Tasks.STATE_PENDING + " THEN 2 ELSE 3 END,"
                            + mTransferFactory.createOrderBy());

        } catch (IllegalStateException ignore) {
            // 数据库关闭
            DuboxLog.w(TAG, "数据库关闭时query", ignore);
        } catch (Exception e) {
            // CursorWindowAllocationException 内存不够，无法打开cursor
            DuboxLog.w(TAG, "数据库关闭时query", e);
        }

        return null;
    }

    /**
     * 查找待删除的任务
     *
     * @return
     */
    private Set<Integer> initInvalidTaskSearcherData() {
        final HashSet<Integer> tempData = new HashSet<Integer>(mTaskCache.size());

        // 初始化数据
        for (TransferTask task : mTaskCache) {
            if (task != null) {
                tempData.add(task.mTaskId);
            }
        }

        return tempData;
    }

    /**
     * 清除失效的任务
     *
     * @return 正在运行的任务计数
     */
    protected void clearCache(final Set<Integer> tempData) {
        final Uri uri = mTransferFactory.createClearTaskUri();
        if (tempData.isEmpty() || uri == null) {
            return;
        }

        DuboxLog.d(TAG, "删除任务:" + tempData.size());

        Cursor cursor = null;
        try {
            cursor = mResolver.query(uri, new String[] { TransferContract.Tasks._ID }, TransferContract.Tasks.IS_DELETE_FILE + "=?",
                    new String[] { String.valueOf(TransferContract.Tasks.YES) }, null);
        } catch (IllegalStateException e) {
            DuboxLog.e(TAG, "ignore", e);
        }

        try {
            for (Integer id : tempData) {
                if (id == null) {
                    continue;
                }

                final Iterator<TransferTask> iterator = mTaskCache.iterator();
                while (iterator.hasNext()) {
                    final TransferTask task = iterator.next();
                    if (task.mTaskId == id) {
                        iterator.remove();

                        if (TransferContract.Tasks.STATE_RUNNING == task.mState) {
                            // 如果是正在传输的任务
                            boolean isDeleteFile = false;
                            if (cursor != null && cursor.moveToFirst()) {
                                do {
                                    if (cursor.getInt(0) == id) {
                                        isDeleteFile = true;
                                        break;
                                    }
                                } while (cursor.moveToNext());
                            }

                            task.performRemove(isDeleteFile);
                        } else {
                            // 移除p2p任务监听
                            unregisterP2PCallback(task);
                        }
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        TaskSchedulerImpl.INSTANCE.addHighTask(new BaseJob("ClearCacheRunnable") {
            // 异步执行加快UI显示，提高性能
            @Override
            protected void performExecute()  {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
                mResolver.delete(uri, TransferContract.Tasks._ID + " IN(" + TextUtils.join(",", tempData) + ")", null);
            }
        });
    }

    /**
     * 同步缓存信息
     *
     * @param cursor
     * @return 已经运行的任务个数
     */
    private void syncCacheInfo(final Cursor cursor, final Set<Integer> tempData) {
        int runningTaskCount = 0;

        try {
            final int count = cursor.getCount();
            if (count <= 0) {
                DuboxLog.d(TAG, "cursor count 0");
                mRateLimiter.cancelProbationary();
                return;
            }

            if (mTaskCache.isEmpty()) {
                // 第一次填充,单独运算，提高速度
                if (cursor.moveToFirst()) {
                    do {
                        // 当帐号已经退出的情况下也不再进行线程调度,相册备份时sdcard不存在就停止调度
                        // 发现新任务，不能就地添加，需要遍历一边确定正在运行的任务数量
                        final TransferTask newTask =
                                mTransferFactory.createTask(mContext, cursor, mRateLimiter, mTransferCalculable);

                        runningTaskCount = syncState(newTask, runningTaskCount);
                        add(newTask);
                    } while (cursor.moveToNext());

                    if (runningTaskCount == 0) {
                        DuboxLog.d(TAG, "mTaskCache is null runningTaskCount 0");
                        mRateLimiter.cancelProbationary();
                    }
                }
            } else {
                if (cursor.moveToFirst()) {
                outer:
                    do {
                        // 当帐号已经退出的情况下也不再进行线程调度,相册备份时sdcard不存在就停止调度
                        final int id = cursor.getInt(cursor.getColumnIndex(TransferContract.Tasks._ID));

                        for (TransferTask taskInfo : mTaskCache) {
                            if (id == taskInfo.mTaskId) {
                                // 如果cache中的task可以在数据中找到
                                mTransferFactory.syncTaskInfo(taskInfo, cursor);
                                runningTaskCount = syncState(taskInfo, runningTaskCount);

                                // 命中的缓存被删除，剩下的就是缓存中有而数据库中没有的任务，也就是待删除的任务
                                tempData.remove(taskInfo.mTaskId);

                                // 找到了缓存中的task，直接跳到下一个task
                                continue outer;
                            }
                        }
                        // 发现新任务，不能就地添加，需要遍历一边确定正在运行的任务数量
                        final TransferTask newTask =
                                mTransferFactory.createTask(mContext, cursor, mRateLimiter, mTransferCalculable);
                        runningTaskCount = syncState(newTask, runningTaskCount);
                        add(newTask);
                    } while (cursor.moveToNext());

                    if (runningTaskCount == 0) {
                        DuboxLog.d(TAG, "mTaskCache size > 0 runningTaskCount 0");
                        mRateLimiter.cancelProbationary();
                    }
                }
            }
        } catch (IllegalStateException ignore) {
            // 数据库关闭
            DuboxLog.w(TAG, "数据库关闭时query", ignore);
        } catch (Exception e) {
            // CursorWindowAllocationException 内存不够，无法打开cursor
            DuboxLog.w(TAG, "数据库关闭时query", e);
        } finally {
            cursor.close();
        }
    }

    /**
     * 根据task的状态同步缓存信息
     *
     * @param task
     * @param runningTaskCount
     * @return 已经运行的任务个数
     */
    private int syncState(TransferTask task, int runningTaskCount) {
        switch (task.mState) {
            case TransferContract.Tasks.STATE_RUNNING:
                // UI界面从pause状态会直接进入running状态
                if (runningTaskCount >= mMultiTaskCount) {
                    return runningTaskCount;
                }

                if (task.transmitter == null) {
                    // 如果程序突然关闭，上一次的传输记录有可能是running，这时候需要重新初始化传输器
                    if (isBreakCondition()) {
                        // 无法开启要变成pending
                        final ContentValues values = new ContentValues(2);
                        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
                        values.put(TransferContract.Tasks.EXTRA_INFO_NUM, TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);

                        mResolver.update(ContentUris.withAppendedId(mUri, task.mTaskId), values, null, null);
                        break;
                    }
//                    if (!checkTransferEnable(task)) {
//                        final ContentValues values = new ContentValues(2);
//                        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FAILED);
//                        values.put(TransferContract.Tasks.EXTRA_INFO_NUM,
//                                TransferContract.UploadTasks.UPLOAD_VIDEO_NO_VIP);
//
//                        mResolver.update(ContentUris.withAppendedId(mUri, task.mTaskId), values, null, null);
//                        break;
//                    }
//                    if (!VipContext.isVip() && checkIsVideoOverSize(task) && task.mType == TYPE_TASK_UPLOAD) {
//                        final ContentValues values = new ContentValues(2);
//                        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FAILED);
//                        values.put(TransferContract.Tasks.EXTRA_INFO_NUM,
//                                TransferContract.UploadTasks.UPLOAD_VIDEO_OVER_SIZE);
//                        mResolver.update(TransferContract.UploadTasks.buildProcessingUri(Account.INSTANCE.getNduss()), values,
//                                TransferContract.Tasks._ID + SQL_PLACEHOLDER,
//                                new String[] {String.valueOf(task.mTaskId)});
//                        break;
//                    }
                    // 开始任务
                    task.performStart(mResolver, getP2PCallback(), this);

                    if (mTransferFactory.isSupportNotification()) {
                        sendUpdateProgressMessage();
                    }
                } else if (mIsWaitingForConfirm2G) {
                    // 切换网络，需要暂停传输
                    task.transmitter.pause();

                    final ContentValues values = new ContentValues(1);
                    values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);

                    mResolver.update(ContentUris.withAppendedId(mUri, task.mTaskId), values, null, null);
                    DuboxLog.i(TAG, "因流量保护弹窗而停止");
                }

                ++runningTaskCount;
                break;
            case TransferContract.Tasks.STATE_FAILED:
                if (task.transmitter != null) {
                    // 移除p2p任务监听
                    unregisterP2PCallback(task);

                    task.transmitter = null;
                }

                break;
            case TransferContract.Tasks.STATE_PAUSE:
                if (task.transmitter != null) {
                    // 移除p2p任务监听,必须先移除监听，不然暂停会将transmitter回收，导致内存泄露
                    unregisterP2PCallback(task);

                    // 先暂停任务
                    task.performPause();
                }

                break;
            case TransferContract.Tasks.STATE_PENDING:
                if (runningTaskCount >= mMultiTaskCount || isBreakCondition()) {
                    return runningTaskCount;
                }
                if (task.transmitter != null) {
                    task.transmitter.stop();
                }
                // 如果失败的错误码是文件名非法，说明上次上传失败，本次不再进行调度，并修改状态为失败
                if (task.extraInfoNum == TransferContract.UploadTasks.EXTRA_FILE_NAME_ILLEGAL) {
                    Uri processingUri =
                            TransferContract.UploadTasks.buildProcessingUri(Account.INSTANCE.getNduss());
                    final ContentValues contentValue = new ContentValues();
                    contentValue.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FAILED);
                    // 更新调度中的任务和正在上传的任务状态
                    mResolver.update(ContentUris.withAppendedId(mUri, task.mTaskId), contentValue, null, null);
                    mResolver.update(ContentUris.withAppendedId(processingUri, task.mTaskId), contentValue, null, null);
                    task.mState = TransferContract.Tasks.STATE_FAILED;
                    return runningTaskCount;
                }

                final ContentValues values = new ContentValues(2);
//                if (!checkTransferEnable(task)) {
//                    values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FAILED);
//                    values.put(TransferContract.Tasks.EXTRA_INFO_NUM,
//                            TransferContract.UploadTasks.UPLOAD_VIDEO_NO_VIP);
//                    mResolver.update(ContentUris.withAppendedId(mUri, task.mTaskId), values, null, null);
//                    task.mState = TransferContract.Tasks.STATE_FAILED;
//                    break;
//                }
//                if (!VipContext.isVip() && checkIsVideoOverSize(task) && task.mType == TYPE_TASK_UPLOAD){
//                    values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_FAILED);
//                    values.put(TransferContract.Tasks.EXTRA_INFO_NUM,
//                            TransferContract.UploadTasks.UPLOAD_VIDEO_OVER_SIZE);
//                    mResolver.update(TransferContract.UploadTasks.buildProcessingUri(Account.INSTANCE.getNduss()), values,
//                            TransferContract.Tasks._ID + SQL_PLACEHOLDER,
//                            new String[] {String.valueOf(task.mTaskId)});
//                    mResolver.update(ContentUris.withAppendedId(mUri, task.mTaskId), values, null, null);
//                    task.mState = TransferContract.Tasks.STATE_FAILED;
//                    break;
//                }
                values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_RUNNING);
                values.put(TransferContract.Tasks.EXTRA_INFO_NUM, TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);

                mResolver.update(ContentUris.withAppendedId(mUri, task.mTaskId), values, null, null);

                notifyUIWhenPendingToRunning(task, mUri);

                task.performStart(mResolver, getP2PCallback(), this);

                task.mState = TransferContract.Tasks.STATE_RUNNING;

                ++runningTaskCount;

                if (mTransferFactory.isSupportNotification()) {
                    sendUpdateProgressMessage();
                }

                break;
            case TransferContract.Tasks.STATE_FINISHED:
                // 如果是p2p任务，主要注册监听 2015-12-3 libin09
                unregisterP2PCallback(task);
                break;
            default:
                break;
        }
        return runningTaskCount;
    }

    /**
     * 移除p2p任务监听
     *
     * @param taskInfo 任务信息
     * @author libin09 2015-12-3
     */
    private void unregisterP2PCallback(TransferTask taskInfo) {
        P2PSDKCallbackProxy proxy = getP2PCallback();
        if (proxy == null || taskInfo == null || taskInfo.transmitter == null
                || !(taskInfo.transmitter instanceof CallbackInterface)) {
            return;
        }

        proxy.remove(String.valueOf(taskInfo.mTaskId));
    }

    /**
     * 用于本地分享通知UI
     *
     * @param taskInfo
     * @param uri
     */
    protected void notifyUIWhenPendingToRunning(TransferTask taskInfo, Uri uri) {
        // nothing
    }

    /**
     * 有序链表,taskId由小到大排列
     *
     * @param newTask
     */
    private void add(TransferTask newTask) {
        mTaskCache.add(newTask);
    }

    /**
     * 更新进度条
     */
    private void updateProgress() {
        long offsetSizes = 0L;
        long sizes = 0L;

        int runningCount = 0;
        int pendingCount = 0;
        int finishedCount = 0;
        int failedCount = 0;
        long rate = 0L;
        long instantRate = 0L;

        final List<TransferTask> tasks = new LinkedList<TransferTask>(mTaskCache);
        final List<String> finishedFilesSuffixList = new ArrayList<String>();

        for (TransferTask taskInfo : tasks) {
            switch (taskInfo.mState) {
                case TransferContract.Tasks.STATE_FAILED:
                    ++failedCount;
                    break;
                case TransferContract.Tasks.STATE_PAUSE:
                    break;
                case TransferContract.Tasks.STATE_FINISHED:
                    ++finishedCount;
                    String suffix = FileUtils.getExtension(taskInfo.mFileName);
                    if (!finishedFilesSuffixList.contains(suffix)) {
                        finishedFilesSuffixList.add(suffix);
                    }
                    break;
                case TransferContract.Tasks.STATE_PENDING:
                    ++pendingCount;
                    offsetSizes += taskInfo.mOffset;
                    sizes += taskInfo.mSize;
                    break;
                case TransferContract.Tasks.STATE_RUNNING:
                    ++runningCount;
                    if (taskInfo.transmitter != null) {
                        taskInfo.mOffset = taskInfo.transmitter.getOffsetSize();
                        rate += taskInfo.transmitter.getRate();
                        instantRate += taskInfo.transmitter.getInstantSpeed();
                    }
                    break;
            }
            // XXX m3u8的offset差最后一次下载的数据
            if (taskInfo.mOffset > 0 && TransferContract.Tasks.STATE_PENDING != taskInfo.mState) {
                // 上面pending的任务已经加过一次了，这里跳过
                offsetSizes += taskInfo.mOffset;
                sizes += taskInfo.mSize;
            }
        }

        if (mInstantRate != instantRate) {
            mInstantRate = instantRate;
        }

        if (mInstantRunningCount != runningCount) {
            mInstantRunningCount = runningCount;
        }

        if (runningCount == 0 && pendingCount == 0) {
            mTaskCache.clear();
            if (mTransferNotificationListener != null) {
                mTransferNotificationListener.onTransferNotification(0, 100, runningCount, pendingCount, finishedCount,
                        failedCount, mTransferFactory.getNotificationType(), mRateLimiter.isIncreaseSpeedRate(),
                        finishedFilesSuffixList);
            }
        } else {
            double progress = 0;
            if (sizes > 0L) {
                progress = offsetSizes * 100.0 / sizes;
            }

            // DuboxLog.d(TAG,
            // "runningCount, pendingCount,finishedCount, failedCount:" + runningCount
            // + "," + pendingCount
            // + "," + finishedCount + "," + failedCount);
            boolean isAllDone = false;
            final double equalValue = 0.0000001;

            if (Math.abs(progress - 100d) < equalValue || Math.abs(progress - mLastProgress) > equalValue
                    || rate != mLastRate) {
                mLastProgress = progress;
                mLastRate = rate;

                mRateLimiter.setRate(mLastRate);

                if (mTransferNotificationListener != null) {
                    isAllDone =
                            mTransferNotificationListener.onTransferNotification(rate, progress, runningCount,
                                    pendingCount, finishedCount, failedCount, mTransferFactory.getNotificationType(),
                                    mRateLimiter.isIncreaseSpeedRate(), finishedFilesSuffixList);
                }
            } else {
                isAllDone = false;
            }

            if (!isAllDone) {
                sendUpdateProgressMessage();
                sendTransferRateMessage(rate, instantRate);
            } else {
                mLastProgress = -1.0;
                mLastRate = -1L;
                mRateLimiter.setRate(mLastRate);
            }
        }
    }

    private String buildSql() {
        if (mTaskCache.isEmpty()) {
            return "";
        }

        final StringBuilder where = new StringBuilder();

        where.append(TransferContract.Tasks._ID).append(" IN (");

        int i = 0;
        for (TransferTask taskInfo : mTaskCache) {
            if (i++ > 0) {
                where.append(",");
            }

            where.append(String.valueOf(taskInfo.mTaskId));
        }
        where.append(")");

        return where.toString() + " OR ";
    }

    /**
     * 判断任务是否满足调度的要求 addby:chenyuquan
     *
     * @return
     */
    private boolean isBreakCondition() {
        final boolean isConnected = ConnectivityState.isConnected(BaseApplication.getInstance());
        final boolean isNoNetwork = NetWorkVerifier.isNoNetwork();
        final boolean isWiFiOnlyChecked = NetConfigUtil.isWiFiOnlyChecked();
        final boolean isWifi = ConnectivityState.isWifi(BaseApplication.getInstance());
        final boolean isLogin = TextUtils.isEmpty(Account.INSTANCE.getNduss());
        final boolean isSDCardExists = DeviceStorageUtils.isSDCardExists();

        DuboxLog.d(TAG,
                "isConnected:" + isConnected + ",isNoNetwork:" + isNoNetwork + ",isWiFiOnlyChecked:"
                        + isWiFiOnlyChecked + ",isWifi:" + isWifi + ",isLogin:" + isLogin + ",isSDCardExists:"
                        + isSDCardExists + ",mIsWaitingForConfirm2G:" + mIsWaitingForConfirm2G + " ,isNotifyTask:"
                        + mTransferFactory.isSupportWifiOnly());

        // 无网pending
        // 仅在wifi下时无wifi，pending
        return !isConnected
                || isNoNetwork || (isWiFiOnlyChecked && !isWifi && mTransferFactory.isSupportWifiOnly())
                || isLogin || !isSDCardExists || mIsWaitingForConfirm2G;
    }

    /**
     * @param isWaitingForConfirm2G
     * @return
     */
    public void setWaitingForConfirm2G(boolean isWaitingForConfirm2G) {
        mIsWaitingForConfirm2G = isWaitingForConfirm2G;
        DuboxLog.d(TAG, "set isWaitingForConfirm2G=" + isWaitingForConfirm2G);
    }

    /**
     * 暂停全部任务
     */
    private synchronized void pauseAllTask() {
        for (TransferTask taskInfo : mTaskCache) {
            if (TransferContract.Tasks.STATE_RUNNING == taskInfo.mState) {
                taskInfo.performPause();
            }
        }
    }

    /**
     * 检查是否有文件的传输能力
     *
     * @param taskInfo
     * @return
     */
    private synchronized boolean checkTransferEnable(TransferTask taskInfo) {
        // 没有视频传输能力的任务直接把状态置为准备
        boolean isVideo = taskInfo.getFile().isVideo();
        if (isVideo && !mTransferFactory.transferVideoEnable()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查是否超过视频大小限制
     */
    private synchronized boolean checkIsVideoOverSize(TransferTask taskInfo) {
        boolean isVideo = taskInfo.getFile().isVideo();
        if (!isVideo){
            return false;
        }
        return taskInfo.mSize >= FirebaseRemoteConfigKeysKt.videoUploadSizeLimit();
    }

    public void setMultiTaskCount(int count) {
        mMultiTaskCount = count;
    }

    /**
     * 获取p2p的callback
     *
     * @return
     */
    @Nullable
    private P2PSDKCallbackProxy getP2PCallback() {
        if (mP2PSDKCallbackProxy == null) {
            mP2PSDKCallbackProxy = mP2PManager.getCallback();
        }
        return mP2PSDKCallbackProxy;
    }

    public static MultiTaskScheduler getDownloadMultiTaskScheduler(Context context, AbstractSchedulerFactory factory,
            OnTransferNotificationListener listener,
            P2PSDKCallbackProxy p2PSDKCallbackProxy, ITransferCalculable transferCalculable) {
        return new MultiTaskScheduler(context, factory, listener,
                p2PSDKCallbackProxy, transferCalculable);
    }

    public static MultiTaskScheduler getUploadMultiTaskScheduler(Context context, String bduss, String uid,
            OnTransferNotificationListener listener, ITransferCalculable transferCalculable) {
        return new MultiTaskScheduler(context, new UploadSchedulerFactory(context.getContentResolver(), bduss, uid),
                listener, null, transferCalculable);
    }
}