package com.moder.compass.transfer.task;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_BACKUP_UPDATE;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_DOWNLOAD_PROGRESS;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_DOWNLOAD_UPDATE;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_PROGRESS;
import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_UPDATE;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.EventCenterHandler;
import com.moder.compass.base.utils.WeakReferenceEventHandler;
import com.dubox.drive.cloudfile.service.CloudFileServiceHelper;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.util.TimerHelper;
import com.moder.compass.transfer.transmitter.util.TimerProcesser;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DiffDelayTimer extends TimerProcesser {
    private static DiffDelayTimer sTimer = null;

    private static final String TAG = "DiffDelayTimer";

    /**
     * server主从延时8秒
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-2-25 下午01:15:16
     */
    private static final int DELAY = 8000;

    private TimerHelper timerHelper;

    /**
     * 共享目录diff队列
     * string是path信息，AtomicInteger表明该path对应需要多少次diff
     * @since 8.0
     */
    private final LinkedHashMap<String, AtomicInteger> mShareDirectory;

    /**
     * 单任务最大等待数量，2次
     */
    private static final int SINGLE_TASK_AWAIT_TASK_NUMBER = 2;

    private boolean mTimeHelperIsRun = false; // 计时器是否开启
    private AtomicInteger mShareDirectoryAwaitTasks = new AtomicInteger(0); // 当前共享目录待执行任务数
    private AtomicInteger mCloudFileAwaitTasks = new AtomicInteger(0); // 当前云文件待执行任务数

    private DiffDelayTimer() {
        mShareDirectory = new LinkedHashMap<>();
        /*
         * 用于有上传完成的任务时触发主页面更新
         */
        timerHelper = new TimerHelper(DELAY, true, this);
        ProgressHandler mProgressHandler = new ProgressHandler(this);
        EventCenterHandler.INSTANCE.registerHandler(mProgressHandler);
    }

    /**
     * 添加共享目录diff任务
     *
     * @param msg
     * @since 8.0
     */
    private void addDiffTask(@NonNull Message msg) {
        final Bundle data = msg.getData();
        if (data == null) {
            return;
        }

        final String path = data.getString(TransferContract.Tasks.REMOTE_URL);
        if (TextUtils.isEmpty(path)) {
            return;
        }

        if (mCloudFileAwaitTasks.get() < SINGLE_TASK_AWAIT_TASK_NUMBER) {
            int value = mCloudFileAwaitTasks.incrementAndGet();
            DuboxLog.d(TAG, "cloud file add task increased to :" + value);
        }
    }

    /**
     * 主文件diff
     */
    private void diffCloudFile() {
        if (mCloudFileAwaitTasks.get() > 0) {
            CloudFileServiceHelper.diff(BaseApplication.getInstance(), null);
            int value = mCloudFileAwaitTasks.decrementAndGet();
            DuboxLog.d(TAG, "cloud file wait run task:" + value);
        }
    }

    /**
     * 需要传递application的context
     * 
     * @return
     */
    public static DiffDelayTimer getInstance() {
        if (sTimer == null) {
            synchronized (DiffDelayTimer.class) {
                if (sTimer == null) {
                    sTimer = new DiffDelayTimer();
                }
            }
        }
        return sTimer;
    }

    @Override
    public void doProcess() {
        // 新版diff libin09
        diffCloudFile();

        if (mShareDirectoryAwaitTasks.get() <= 0 && mCloudFileAwaitTasks.get() <= 0) {
            timerHelper.stopTimer();
            mTimeHelperIsRun = false;
            mShareDirectoryAwaitTasks.set(0);
            mCloudFileAwaitTasks.set(0);
            DuboxLog.d(TAG, "stop timer");
        }
    }

    private static class ProgressHandler extends WeakReferenceEventHandler<DiffDelayTimer> {

        ProgressHandler(DiffDelayTimer taskManager) {
            super(taskManager);
        }

        @Override
        public boolean messageFilter(int what) {
            return what == MESSAGE_UPLOAD_UPDATE || what == MESSAGE_DOWNLOAD_UPDATE
                    || what == MESSAGE_UPLOAD_PROGRESS || what == MESSAGE_DOWNLOAD_PROGRESS
                    || what == MESSAGE_BACKUP_UPDATE;
        }

        @Override
        public void handleMessage(DiffDelayTimer taskManager, @NonNull Message msg) {
            // 上传成功和照片备份成功刷新主页面
            if (((msg.what == MESSAGE_UPLOAD_UPDATE) || (msg.what == MESSAGE_BACKUP_UPDATE))
                    && (msg.arg2 == TransferContract.Tasks.STATE_FINISHED)) {
                DuboxLog.d(TAG, "upload success refresh homepage");
                // 添加安全判断 YQH 20121120
                if (taskManager.timerHelper != null) {
                    // 添加共享目录diff任务
                    taskManager.addDiffTask(msg);
                    if (!taskManager.mTimeHelperIsRun) {
                        taskManager.timerHelper.startTimer();
                        taskManager.mTimeHelperIsRun = true;
                        DuboxLog.d(TAG, "start timer");
                    }
                }
            }
        }
    }
}
