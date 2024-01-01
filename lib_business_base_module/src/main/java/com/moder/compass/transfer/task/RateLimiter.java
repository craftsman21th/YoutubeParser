package com.moder.compass.transfer.task;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.p2p.UniversalDownloadTransmitter;
import com.moder.compass.transfer.transmitter.util.TimerHelper;
import com.dubox.drive.util.WeakRefResultReceiver;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.base.service.constant.BaseExtras;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.moder.compass.transfer.transmitter.ratelimiter.IRateLimitable;
import com.moder.compass.transfer.transmitter.util.TimerProcesser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by liuliangping on 2015/7/12. 限速器实现
 *
 * @since 7.10
 */
class RateLimiter implements IRateLimitable {
    private static final String TAG = "RateLimiter";
    private final ConfigSystemLimit mConfigSystemLimit = ConfigSystemLimit.getInstance();
    private volatile long mThresholdSpeed = mConfigSystemLimit.limitDownloadThreshold * 1024; // 单位B/s
    private static final long STATISTICS_TIME = 1000 * 60 * 2; // 2分钟

    /**
     * 用于记录每个线程分片sleep
     *
     * @author libin09
     */
    private final ThreadLocal<SleepTimeInfo> mThreadInSleepInfo = new ThreadLocal<>();

    private volatile boolean mIsLimit = true; // 默认全限速
    private volatile boolean mIsRunningProbationary = false; // 是否正处于试用加速状态
    private volatile long mBeforeProbationaryHighestSpeed; // 试用前最高速
    private volatile long mRunningProbationaryHighestSpeed; // 试用期间最高速

    private volatile boolean mIsQueryProbationaryResult = false; // 查询访问接口的结果
    private volatile boolean mIsQueryingProbationary = false; // 查询访问接口是否正在请求中，防止重复请求
    private volatile boolean mIsProbationaryQualifications = false; // 接口访问是否有资格

    private volatile boolean mIsIncreaseSpeedRate; // 通知栏是否显示加速的图标
    private volatile long mRealRate;
    private boolean mIsRunningResetRate;
    private boolean mIsRunningCancelProbationary; // 在running状态之前，该值均为false
    private boolean mIsEndResetRate;
    private boolean mIsStatisticsRunningNoTask;
    // 是否给p2p设置了试用结束
    private boolean mIsSetP2PTryEnd = false;

    private String mSpeedToken;
    private String mSpeedTimeStamp;
    private TimerHelper mStatisticsTimerHelper;
    private String mPrivilegeId; // 加速券id
    private String mPrivilegeType; // 加速券类型
    /**
     * 设置P2P参数
     */
    private static Handler mHandler;

    /**
     * 每次限速阈值的缓存，用于p2p sdk使用
     *
     * @author libin09 2015-12-11
     * @since 7.12.0
     */
    private volatile long mThresholdCached;

    /**
     * 每次限速的时候，保存当前正在运行的任务个数
     */
    private volatile int mRunningTaskCount = 1;

    private final Collection<TransferTask> mTaskCache;
    /**
     * 上次更新通知栏时的速度，用于确定是否要更新通知栏，速度没有变化时，不更新通知栏
     */
    private long mLastRate;

    private boolean mConditionIgnore;

    RateLimiter(Collection<TransferTask> tasks) {
        mTaskCache = tasks;
        mLastRate = -1L;
        mThresholdCached = 0L;
        // 设置P2P参数线程
        if (mHandler == null) {
            HandlerThread mSetP2PThread = new HandlerThread(TAG + "-SetP2PThread");
            mSetP2PThread.start();
            mHandler = new Handler(mSetP2PThread.getLooper());
        }
    }

    @Override
    public Pair<State, Long> limit(long currentSpeed, boolean isP2P) {
        // 来自每个transmitter线程调用
        // 1.
        long threshold;
        synchronized (RateLimiter.class) {
            if (isOriginSpeedEnable()) { // 白金会员不限速
                return Pair.create(State.UNLIMITED, 0L);
            }

            if (isPurchaseProbationary()) {
                startStatisticsProbationarySpeedTask(currentSpeed);
            } else {
                stopStatisticsProbationarySpeedTask();
            }

            if (!mIsLimit) {
                DuboxLog.d(TAG, "do not limit");

                if (mLastRate > mRunningProbationaryHighestSpeed) { // 试用期间的最高速度
                    mRunningProbationaryHighestSpeed = mLastRate;
                }

                return Pair.create(State.UNLIMITED, 0L);
            }

            if (mLastRate > mBeforeProbationaryHighestSpeed) { // 试用前的最高速度
                mBeforeProbationaryHighestSpeed = mLastRate;
            }

            mRealRate = currentSpeed;

            // 2.
            int p2pCount = 0;
            final HashSet<TransferTask> runningTasks = new HashSet<>();
            final ArrayList<TransferTask> tempList = new ArrayList<>(mTaskCache);
            for (TransferTask task : tempList) {
                if (task != null && task.mState == TransferContract.Tasks.STATE_RUNNING) {
                    if (task.transmitter instanceof UniversalDownloadTransmitter) {
                        p2pCount++;
                    }
                }
                // banner页广告apk或者wap页连接下载
                if (task != null && task.mState == TransferContract.Tasks.STATE_RUNNING
                        && (!TextUtils.equals(task.mTransmitterType, TransferContract.Tasks.TRANSMITTER_TYPE_WEB)
                        || !TextUtils.equals(task.mTransmitterType,
                            TransferContract.Tasks.TRANSMITTER_TYPE_LINK)
                        || !TextUtils.equals(task.mTransmitterType,
                            TransferContract.Tasks.TRANSMITTER_PLATFORM_PLUGIN_LINK))) {
                    runningTasks.add(task);
                }
            }

            if (runningTasks.isEmpty()) {
                DuboxLog.d(TAG, "limit runningTasks is empty");
                return Pair.create(State.UNLIMITED, 0L);
            }

            final int totalTaskCount = (mRunningTaskCount = runningTasks.size());
            if (totalTaskCount == 0) {
                return Pair.create(State.UNLIMITED, 0L);
            }

            // p2p和cdn均分限速值.
            // 举例： 共2个下载任务， P2SP有1个任务，CDN有1个任务
            // 则P2SP分得1/2的速度，CDN分得1/2的速度.libin09 2015-12-11 7.12.0
            if (isP2P) {
                // p2p下载
                if (p2pCount >= totalTaskCount) {
                    // 说明都是p2p任务，p2p占全部限速值
                    threshold = mThresholdSpeed;
                } else {
                    // 有非p2p任务，p2p分一半限速值
                    threshold = mThresholdSpeed * p2pCount / totalTaskCount;
                }
                if (mThresholdCached != threshold) {
                    // 缓存速度，提高sdk性能，只有阈值速度发生变化时才设置
                    mThresholdCached = threshold;
//                    DuboxLog.d(TAG, TAG + " limit p2p:" + threshold + " of " + mThresholdSpeed);
//                    P2P.getInstance().setParameter(Key.MAX_CDN_DOWNLOAD_SPEED, String.valueOf(threshold));
                }

                return Pair.create(State.LIMITED, threshold);
            }

            // CDN下载
            final int cdnCount = totalTaskCount - p2pCount;
            if (cdnCount <= 0) {
                return Pair.create(State.UNLIMITED, 0L);
            }

            threshold = (mThresholdSpeed - mThresholdSpeed * p2pCount / totalTaskCount) / cdnCount;
            DuboxLog.d(TAG, TAG + " limit cdn:" + threshold / 1024 + "KB/s of " + mThresholdSpeed / 1024 + "KB/s");
        }

        // 3. CDN下载先检查上次分片sleep是否完成，完成后再计算新的限速，未完成继续sleep
        SleepTimeInfo sleepTimeInfo = mThreadInSleepInfo.get();

        if (sleepTimeInfo != null) {
            // 初始化sleep分片信息类
            return Pair.create(sleepInterval(sleepTimeInfo), threshold);
        }

        if (currentSpeed > threshold) {
            // 下载速度超过限速阈值
            DuboxLog.d(TAG, "限速:" + currentSpeed + "," + threshold);
            double tempTime = (currentSpeed - threshold) / (double) threshold;
            long totalSleepTime = (long) (tempTime * 1000);// 秒

            // 初始化sleep分片信息类
            mThreadInSleepInfo.set(sleepTimeInfo = new SleepTimeInfo(totalSleepTime));

            return Pair.create(sleepInterval(sleepTimeInfo), threshold);
        }

        DuboxLog.d(TAG, "limit every task threshold:" + threshold + " ,currentSpeed:" + currentSpeed);
        return Pair.create(State.UNLIMITED, threshold);
    }

    /**
     * 优先级:locatedownload > cfgconfig > 默认值400kB/s
     *
     * @param threshold 限速值
     */
    @Override
    public void updateThreshold(long threshold) {

        synchronized (RateLimiter.class) {
            if (threshold > 0L) {
                mThresholdSpeed = threshold * 1024; // KB/s 换算成 B/s
                DuboxLog.d(TAG, "limit updateThreshold " + mThresholdSpeed);
                return;
            }

            // locatedownload接口返回直接没有sl字段
            if (threshold == 0L) {
                mIsLimit = false;
            }

            DuboxLog.d(TAG, "limit updateThreshold threshold:" + threshold + "kB ,mThresholdSpeed:" + mThresholdSpeed
                    + "b");
        }
    }

    /**
     * 检查是否满足试用加速<br/>
     * 试用加速的前提条件：正在下载的文件存在大于某个阀值（50M,配置下发），并且下载速度大于总速度的百分比（默认90%以上，配置下发）
     *
     * @return true:条件满足  false:未满足试用条件
     */
    private boolean checkProbationaryCondition() {
        if (mConditionIgnore) {
            DuboxLog.d(TAG, "condition check ignored");
            return true;
        }

        boolean canProbationary = isSatisfiedSizeCondition();
        if (!canProbationary) {
            DuboxLog.d(TAG, "download task size can not probationary,limit size="
                    + mConfigSystemLimit.limitDownloadFileSize);
            return false;
        }
        synchronized (RateLimiter.class) {
            canProbationary =
                    mRealRate > (mThresholdSpeed * mConfigSystemLimit.limitDownloadPercentage / mRunningTaskCount);
            DuboxLog.d(TAG, "download task rate:" + mLastRate + " ,0.9 mThresholdSpeed:" + mThresholdSpeed
                    * mConfigSystemLimit.limitDownloadPercentage + "mRunningTaskCount:" + mRunningTaskCount);
        }
        if (!canProbationary) {
            DuboxLog.d(TAG, "download task rate can not probationary,limit speed="
                    + mConfigSystemLimit.limitDownloadPercentage * mThresholdCached);
            return false;
        }
        return true;
    }

    /**
     * 判断 有下载文件的大小大于配置的大小（默认50m），下载速度大于总速度的百分比(默认90%以上)
     */
    private void isProbationary() {
        if (!checkProbationaryCondition()) {
            return;
        }
    }

    /**
     * 是否满足文件大小的条件,有超过limitDownloadFileSize50M）的文件下载
     *
     * @return 文件大小是否满足条件
     */
    private boolean isSatisfiedSizeCondition() {
        synchronized (RateLimiter.class) {
            final ArrayList<TransferTask> tempList = new ArrayList<>(mTaskCache);
            for (TransferTask task : tempList) {
                if (task != null && task.mState == TransferContract.Tasks.STATE_RUNNING
                        && task.mSize >= mConfigSystemLimit.limitDownloadFileSize) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 调度器启动时候和无running的任务都会调用该方法。
     * <p/>
     * mIsRunningCancelProbationary说明：由于提速状态是异步回调所以调用是不知道当前是什么状态，故调用一次置mIsRunningCancelProbationary为true
     * ，同时为了在running的时候统计， 所以需要在可以提速试用状态回调的onPreBegin里面再次把其置为false，同mIsStatisticsRunningNoTask配合试用来统计试用过程中无任务的统计
     */
    void cancelProbationary() {
        DuboxLog.d(TAG, "cancelProbationary");

        stopStatisticsProbationarySpeedTask();
    }

    /**
     * 分时间片sleep
     *
     * @param sleepTimeInfo sleep信息
     * @return 是否在限速中
     * @author libi09 2015-6-24
     */
    private State sleepInterval(SleepTimeInfo sleepTimeInfo) {
        final long time = sleepTimeInfo.getSleepTime();
        try {
            Thread.sleep(time);
            DuboxLog.d(TAG, "limit sleepTime:" + time + "ms");

            final State state = sleepTimeInfo.accumulate(time);
            if (State.UNLIMITED == state) {// 如果sleep时间够了，重新计算即使速度
                DuboxLog.d(TAG, "限速完成，时长:" + sleepTimeInfo);
                mThreadInSleepInfo.remove();
            }

            DuboxLog.d(TAG, "分片限速中，时长:" + sleepTimeInfo);
            return state;
        } catch (InterruptedException e) {
            DuboxLog.w(TAG, "", e);
        }

        return State.UNLIMITED;
    }

    public void setRate(long rate) {
        synchronized (RateLimiter.class) {
            mLastRate = rate;
        }
    }

    boolean isIncreaseSpeedRate() {
        return mIsIncreaseSpeedRate;
    }

    private void resetRateList() {
        DuboxLog.d(TAG, "resetRateList");
        synchronized (RateLimiter.class) {
            final ArrayList<TransferTask> tempList = new ArrayList<>(mTaskCache);
            for (TransferTask task : tempList) {
                if (task != null && task.mState == TransferContract.Tasks.STATE_RUNNING) {
                    if (task.transmitter == null) {
                        continue;
                    }
                    task.transmitter.resetRateCalculator();
                }
            }
        }
    }

    /**
     * 只有白金会员不限速
     *
     * @return 是否可以原速下载
     */
    private boolean isOriginSpeedEnable() {
        return true;
    }

    /**
     * 分片sleep信息类
     *
     * @author libin09 2015-6-24
     * @since 7.10
     */
    private class SleepTimeInfo {
        /**
         * 读取流的时间间隔
         */
        private static final long READ_INTERVAL = (long) (Constants.READ_TIMEOUT * 0.7);// 读取流超时时间 * 0.7

        /**
         * 需要sleep的总时间
         */
        private final long mTotalSleepTime;
        /**
         * 已经sleep过的时间
         */
        private long mSleptTime;

        /**
         * 上次读取流的时间
         */
        private long mLastReadTime;

        /**
         * 分片sleep信息类
         *
         * @param totalSleepTime 需要sleep的总时间
         */
        SleepTimeInfo(long totalSleepTime) {
            mTotalSleepTime = totalSleepTime;
            mSleptTime = 0L;
            mLastReadTime = 0L;
        }

        @Override
        public String toString() {
            return "SleepTimeInfo{" + "mSleptTime=" + mSleptTime + ", mTotalSleepTime=" + mTotalSleepTime + '}';
        }

        /**
         * 时间累加，记录分片sleep的时长
         *
         * @param sleepTime 每次slept的时间
         * @return 是否已经sleep够了
         */
        State accumulate(long sleepTime) {
            mSleptTime += sleepTime;

            if (mSleptTime >= mTotalSleepTime) {// sleep时间超过整体时间，不限速
                return State.UNLIMITED;
            }

            if (sleepTime - mLastReadTime >= READ_INTERVAL) {// 每隔1分钟读一个字节
                mLastReadTime = sleepTime;
                return State.LIMITED_READ;
            }

            return State.LIMITED;// 一分钟之间限速不读取数据
        }

        /**
         * 获取sleep时间，如果最后一次剩余时间比默认时间短，那么用剩余时间
         *
         * @return 需要sleep的时间
         */
        long getSleepTime() {
            return Math.min(mTotalSleepTime - mSleptTime, Constants.CALCULAT_INTERVAL);
        }
    }

    private static class QueryProbationaryReceiver extends WeakRefResultReceiver<RateLimiter> {
        QueryProbationaryReceiver(RateLimiter rateLimiter, Handler handler) {
            super(rateLimiter, handler);
        }

        @Override
        protected void onHandlerSuccessResult(@NonNull RateLimiter reference, @Nullable Bundle resultData) {
            super.onHandlerSuccessResult(reference, resultData);
            reference.mIsQueryingProbationary = false;
            reference.mIsQueryProbationaryResult = true;
            reference.mIsProbationaryQualifications = resultData != null
                    && resultData.getBoolean(BaseExtras.RESULT);
            if (!reference.mIsProbationaryQualifications) {
                DuboxLog.d(TAG, "query download can not probationary");
                return;
            }

            reference.isProbationary();
        }

        @Override
        protected void onHandlerOperatingResult(@NonNull RateLimiter reference, @Nullable Bundle resultData) {
            super.onHandlerOperatingResult(reference, resultData);
            reference.mIsQueryProbationaryResult = false;
            reference.mIsQueryingProbationary = false;
        }

        @Override
        protected void onHandlerOtherResult(@NonNull RateLimiter reference, int resultCode,
                                            @Nullable Bundle resultData) {
            super.onHandlerOtherResult(reference, resultCode, resultData);
            reference.mIsQueryProbationaryResult = false;
            reference.mIsQueryingProbationary = false;
        }

        @Override
        protected void onHandlerFailedResult(@NonNull RateLimiter reference, @Nullable Bundle resultData) {
            super.onHandlerFailedResult(reference, resultData);
            reference.mIsQueryProbationaryResult = false;
            reference.mIsQueryingProbationary = false;
        }
    }

    @Override
    public boolean isRunningProbationary() {
        return mIsRunningProbationary;
    }

    public String getSpeedToken() {
        return mSpeedToken;
    }

    public String getSpeedTimeStamp() {
        return mSpeedTimeStamp;
    }

    private void startStatisticsProbationarySpeedTask(final long curSpeed) {
        if (mStatisticsTimerHelper == null ) {
            StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.BUSINESS_DOWNLOAD_PROBATIONARY_SPEED,
                    mPrivilegeId, String.valueOf(System.currentTimeMillis()), String.valueOf(curSpeed));
            mStatisticsTimerHelper = new TimerHelper(STATISTICS_TIME, true, new TimerProcesser() {
                @Override
                public void doProcess() {

                    StatisticsLogForMutilFields.getInstance().updateCount(
                            StatisticsLogForMutilFields.StatisticsKeys.BUSINESS_DOWNLOAD_PROBATIONARY_SPEED,
                            mPrivilegeId, String.valueOf(System.currentTimeMillis()), String.valueOf(curSpeed));
                }
            });
            mStatisticsTimerHelper.startTimer();
        }
    }

    private void stopStatisticsProbationarySpeedTask() {
        if (mStatisticsTimerHelper != null) {
            mStatisticsTimerHelper.stopTimer();
            mStatisticsTimerHelper = null;
        }
    }

    /**
     * 是否是付费券加载
     *
     * @return
     */
    private boolean isPurchaseProbationary() {
        return mIsRunningProbationary && !mPrivilegeId.isEmpty();
    }
}
