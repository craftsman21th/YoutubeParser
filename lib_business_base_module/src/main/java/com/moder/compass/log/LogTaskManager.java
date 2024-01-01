package com.moder.compass.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.moder.compass.statistics.points.FileTransmitStats;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 日志管理的类
 *
 * Created by liuliangping on 2016/3/22.
 */
public class LogTaskManager {
    private static final String TAG = "LogManager";
    private final FileTransmitStats mFileTransmitStats;

    public LogTaskManager() {
        mFileTransmitStats = new FileTransmitStats();
    }

    /**
     * 使用静态线程池共享各个LogManager之间的任务
     *
     * @since 7.13
     * @author liuliangping 2016.3.22
     */
    private static ExecutorService sAddDownloadTaskExecutor;

    /**
     * 添加到异步日志任务添加队列执行
     *
     * @param runnable
     * @author liuliangping 2016.3.22
     */
    private void runInDownloadTaskAddThread(Runnable runnable) {
        synchronized (LogTaskManager.class) {
            if (sAddDownloadTaskExecutor == null || sAddDownloadTaskExecutor.isShutdown()) {
                sAddDownloadTaskExecutor = Executors.newSingleThreadExecutor();
            }
        }
        sAddDownloadTaskExecutor.execute(runnable);
    }

    public void addLogTask(final ILogGenerator logGenerator, final ILogField logField) {
        DuboxLog.d(TAG, "addLogTask logGenerator:" + logGenerator + " ,logField:" + logField);

        if (logGenerator == null || logField == null) {
            return;
        }

        runInDownloadTaskAddThread(new Runnable() {

            @Override
            public void run() {
                String log = logGenerator.generator(logField);
                logGenerator.clear(logField);
                DuboxLog.d(TAG, "flog:" + log);
                mFileTransmitStats.uploadLog(log);
            }
        });
    }
}
