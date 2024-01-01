package com.moder.compass.transfer.task;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_DOWNLOAD_UPDATE;
import static com.moder.compass.log.VipPayLoggerKt.KEY_PREMIUM_AGENT_DOWNLOAD;
import static com.moder.compass.log.VipPayLoggerKt.statisticVipPremiumLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

import com.dubox.drive.transfer.task.IDownloadTaskManager;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.kernel.android.util.network.NetWorkVerifier;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.storage.DownloadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.dubox.drive.db.transfer.download.DownloadTaskProviderInfo;
import com.moder.compass.transfer.transmitter.TransferNumManager;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;
import org.jetbrains.annotations.NotNull;

public class DownloadTaskManager implements IDownloadTaskManager {
    private static final String TAG = "DownloadTaskManager";
    public static final int DOWNLOAD_PRIORITY_DUBOX = 0;
    public static final int DOWNLOAD_PRIORITY_HIGHER = 1;

    /**
     * 批量添加到传输列表开启数据库事务操作的最小数量
     *
     * @author 孙奇 V 1.0.0 Create at 2013-1-6 上午10:27:00
     */
    private static final int MIN_ENABLE_TRANSACTION_SIZE = 10;

    /**
     * 批量添加任务时，暂停的时间
     */
    private static final int SLEEP_TIME = 100;

    /**
     * 批量添加每次事务最多执行的操作数
     *
     * @author 孙奇 V 1.0.0 Create at 2013-1-6 上午11:10:16
     */
    private static final int MAX_TRANSACTION_SIZE = 50;

    /**
     * 使用静态线程池共享各个DownloadTaskManager之间的任务
     *
     * @since 7.10
     * @author libin09 2015.7.14
     */
    private static ExecutorService sAddDownloadTaskExecutor;

    private final DownloadTaskProviderHelper mProviderHelper;
    private final String mBduss;
    private final String mUid;
    private final Uri mProcessingUri;

    /**
     * 每个实例，只对应一个bduss，如果切换账号，注意销毁实例
     */
    public DownloadTaskManager(String bduss, String uid) {
        mBduss = TextUtils.isEmpty(bduss) ? "" : bduss;
        mUid = TextUtils.isEmpty(uid) ? "" : uid;
        mProviderHelper = new DownloadTaskProviderHelper(mBduss);
        mProcessingUri = TransferContract.DownloadTasks.buildProcessingUri(mBduss);
    }

    /**
     * 添加转存下载任务
     *
     * @param processorFactory
     * @param resultReceiver
     */
    public void addDownloadTask(final IDownloadable downloadable, final IDownloadProcessorFactory processorFactory,
                                final TaskResultReceiver resultReceiver, final int downloadPriority) {
        DuboxLog.d(TAG, "addDownloadTask");

        runInDownloadTaskAddThread(new Runnable() {

            @Override
            public void run() {

                if (null != downloadable) {
                    final String filePath = downloadable.getFilePath();
                    if (!TextUtils.isEmpty(filePath) && filePath.contains("../")) {
                        DuboxLog.d("ShareFileOpPresenter","filePath error:"+filePath);
                        if (resultReceiver != null) {
                            resultReceiver.sendFailed();
                        }
                        return;
                    }
                }

                Processor processor = processorFactory.createProcessor(downloadable, true, mBduss,
                        mUid, downloadPriority);
                if (processor == null) {
                    DuboxLog.d("ShareFileOpPresenter","processor null");
                    if (resultReceiver != null) {
                        resultReceiver.sendFailed();
                    }
                    return;
                }

                // 利用工厂模式和多态搞定这个多条件多分枝处理过程
                processor.process();

                if (resultReceiver != null) {
                    resultReceiver.sendSuccess();
                }
            }
        });
    }

    @Override
    public void addDownloadListTask(List<? extends IDownloadable> downloadFiles,
                                    IDownloadProcessorFactory processorFactory, TaskResultReceiver resultReceiver,
                                    int downloadPriority) {
        addDownloadListTask(downloadFiles, processorFactory, resultReceiver, downloadPriority, null);
    }

    /**
     * 批量添加下载任务
     *
     * @param downloadFiles
     * @param processorFactory
     * @param resultReceiver
     */
    public void addDownloadListTask(final List<? extends IDownloadable> downloadFiles,
                                    final IDownloadProcessorFactory processorFactory,
                                    final TaskResultReceiver resultReceiver,
                                    final int downloadPriority, final ITaskStateCallback callback) {
        DuboxLog.d(TAG, "addDownloadListTask");

        runInDownloadTaskAddThread(new Runnable() {

            @Override
            public void run() {
                if (CollectionUtils.isEmpty(downloadFiles)) {
                    if (resultReceiver != null) {
                        resultReceiver.sendFailed();
                    }
                    return;
                }

                List<IDownloadable> fileList = new ArrayList<IDownloadable>(downloadFiles);
                int size = fileList.size();
                int i = 0;
                // 至少有一个任务被processor处理
                boolean hasTaskProcessor = false;

                synchronized (DownloadTaskProviderInfo.getSyncObject()) {
                    if (size > MIN_ENABLE_TRANSACTION_SIZE) {
                        DownloadTaskProviderInfo.beginTransaction();
                    }

                    // 是否为第一批任务
                    int k = 0;
                    String shareDirectoryRootName = null;
                    try {
                        for (IDownloadable able : fileList) {
                            if (able == null) {
                                continue;
                            }

                            final String filePath = able.getFilePath();

                            if (!TextUtils.isEmpty(filePath) && filePath.contains("../")) {
                                continue;
                            }


                            if (FileType.isImage(filePath)) {
                                StatisticsLogForMutilFields.getInstance().updateCount
                                        (StatisticsLogForMutilFields.StatisticsKeys.DOWNLOAD_IMAGE_COUNT);
                            }

                            Processor processor =
                                    processorFactory.createProcessor(able, !DownloadTaskProviderInfo.inTransaction(),
                                            mBduss, mUid, downloadPriority);

                            if (processor == null) {
                                continue;
                            }

                            hasTaskProcessor = true;
                            processor.process();
                            // 每MAX_TRANSACTION_SIZE个文件中断一次事务，给其他操作写数据库的机会
                            if ((i > MAX_TRANSACTION_SIZE) && DownloadTaskProviderInfo.inTransaction()) {
                                i = 0;
                                DownloadTaskProviderInfo.endTransactionSuccessful();
                                notifyUriUpdate(k++ == 1);

                                if (size > MIN_ENABLE_TRANSACTION_SIZE) {
                                    DuboxLog.d(TAG, "sleep " + SLEEP_TIME + "秒");
                                    // 首次加载时，sleep时间长些，以便UI更新
                                    SystemClock.sleep(SLEEP_TIME * (k == 1 ? 5 : 1));
                                    DuboxLog.d(TAG, "继续执行批量添加任务 ");
                                    DownloadTaskProviderInfo.beginTransaction();
                                }
                            }
                            i++;
                            size--;
                        }
                        fileList.clear();
                    } finally {
                        if (DownloadTaskProviderInfo.inTransaction()) {
                            DownloadTaskProviderInfo.endTransactionSuccessful();
                            notifyUriUpdate(true);
                        }

                        if (resultReceiver != null) {
                            if (hasTaskProcessor) {
                                resultReceiver.sendSuccess();
                            } else {
                                resultReceiver.sendFailed();
                            }
                        }

                        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_DOWNLOAD_UPDATE, -1, -1, null);
                        // 通知业务层，任务数量变化了
                        TransferNumManager.getInstance().setTransferNumChanged(true);
                    }
                }
            }
        });

    }

    /**
     * 添加到异步下载任务添加队列执行
     *
     * @param runnable
     * @author 孙奇 V 1.0.0 Create at 2012-12-10 下午03:07:08
     */
    private void runInDownloadTaskAddThread(Runnable runnable) {
        synchronized (DownloadTaskManager.class) {
            if (sAddDownloadTaskExecutor == null || sAddDownloadTaskExecutor.isShutdown()) {
                sAddDownloadTaskExecutor = Executors.newSingleThreadExecutor();
            }
        }
        sAddDownloadTaskExecutor.execute(runnable);
        statisticVipPremiumLog(KEY_PREMIUM_AGENT_DOWNLOAD);
    }

    /**
     * 获取活动的任务的数量
     *
     * @return
     */
    public int getAllActiveTaskSize() {
        Cursor cursor = null;
        try {
            cursor = BaseApplication
                    .getInstance()
                    .getContentResolver()
                    .query(mProcessingUri,
                            new String[]{"COUNT(0)"},
                            TransferContract.Tasks.STATE + "=? OR " + TransferContract.Tasks.STATE + "=?",
                            new String[]{String.valueOf(TransferContract.Tasks.STATE_PENDING), String.valueOf(TransferContract.Tasks.STATE_RUNNING)},
                            null);
            if (cursor == null) {
                return 0;
            }

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "", ignore);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * 获取的待处理任务的数量
     *
     * @return
     */
    public int getAllProcessingTaskSize() {
        Cursor cursor = null;
        try {
            cursor = BaseApplication
                    .getInstance()
                    .getContentResolver()
                    .query(mProcessingUri,
                            new String[]{"COUNT(0)"},
                            TransferContract.Tasks.STATE + "=? OR " + TransferContract.Tasks.STATE + "=? OR " + TransferContract.Tasks.STATE + "=?",
                            new String[]{String.valueOf(TransferContract.Tasks.STATE_PENDING), String.valueOf(TransferContract.Tasks.STATE_RUNNING),
                                    String.valueOf(TransferContract.Tasks.STATE_PAUSE)}, null);

            if (cursor == null) {
                return 0;
            }

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "", ignore);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * 暂停一个任务
     *
     * @param id 任务ID
     */
    public void pauseTask(final int id) {
        final ContentValues values = new ContentValues(2);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PAUSE);
        values.put(TransferContract.Tasks.RATE, 0);

        BaseApplication
                .getInstance()
                .getContentResolver()
                .update(TransferContract.DownloadTasks.buildProcessingUri(mBduss),
                        values,
                        TransferContract.Tasks._ID + "=? AND (" + TransferContract.Tasks.STATE + "=" + TransferContract.Tasks.STATE_PENDING + " OR " + TransferContract.Tasks.STATE + "="
                                + TransferContract.Tasks.STATE_RUNNING + ")", new String[] { String.valueOf(id) });
    }

    /**
     * 重新下载全部失败的文件
     */
    public void reDownload(final long[] id) {
        NetWorkVerifier.reset();
        ArrayList<String> taskIds = new ArrayList<>();
        if (id != null && id.length != 0) {
            for (int i = 0; i < id.length; i++){
                taskIds.add(String.valueOf(id[i]));
            }
        }
        String idArgs = TextUtils.join(",", taskIds);
        final Cursor cursor = BaseApplication.getInstance().getContentResolver().query(
                TransferContract.DownloadTasks.buildUri(mBduss),
                new String[] {
                        TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL,
                        TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.OFFSET_SIZE,
                        TransferContract.Tasks.SIZE, TransferContract.Tasks.TYPE,
                        TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.PRIORITY,
                        CloudFileContract.Files.FILE_SERVER_MD5, TransferContract.Tasks.IS_P2P_FAILED,
                        TransferContract.Tasks.P2P_FGID, TransferContract.Tasks.IS_P2P_TASK,
                        TransferContract.Tasks.IS_DOWNLOAD_SDK_TASK
                },
                TransferContract.Tasks.STATE + "=" + TransferContract.Tasks.STATE_FAILED
                        + (TextUtils.isEmpty(idArgs) ? ""
                        : " AND " + TransferContract.Tasks._ID + " IN(" + idArgs + ")"),
                null, null);
        if (cursor == null) {
            return;
        }
        try {
            final int count = cursor.getCount();
            final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>(count + 1);
            final ArrayList<String> ids = new ArrayList<String>(count);
            while (cursor.moveToNext()) {
                // 删除
                ids.add(String.valueOf(cursor.getInt(0)));
                // 添加
                final ContentValues values = new ContentValues(13);
                values.put(TransferContract.Tasks.LOCAL_URL, cursor.getString(1));
                values.put(TransferContract.Tasks.REMOTE_URL, cursor.getString(2));
                values.put(TransferContract.Tasks.OFFSET_SIZE, cursor.getLong(3));
                values.put(TransferContract.Tasks.SIZE, cursor.getLong(4));
                values.put(TransferContract.Tasks.TYPE, cursor.getInt(5));
                values.put(TransferContract.Tasks.TRANSMITTER_TYPE, cursor.getString(6));
                values.put(TransferContract.Tasks.PRIORITY, cursor.getInt(7));
                values.put(CloudFileContract.Files.FILE_SERVER_MD5, cursor.getString(8));
                values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
                values.put(TransferContract.Tasks.IS_P2P_FAILED, cursor.getInt(9));
                values.put(TransferContract.Tasks.P2P_FGID, cursor.getString(10));
                values.put(TransferContract.Tasks.IS_P2P_TASK, cursor.getInt(11));
                values.put(TransferContract.Tasks.IS_DOWNLOAD_SDK_TASK, cursor.getInt(12));
                batch.add(ContentProviderOperation.newInsert(mProcessingUri).withValues(values).build());
            }
            batch.add(0,
                    ContentProviderOperation.newDelete(TransferContract.DownloadTasks.buildFailedUri(mBduss))
                            .withSelection(TransferContract.Tasks._ID + " IN(" + TextUtils.join(",", ids) + ")", null).build());
            BaseApplication.getInstance().getContentResolver().applyBatch(TransferContract.CONTENT_AUTHORITY, batch);
        } catch (RemoteException ignore) {
        } catch (OperationApplicationException ignore) {
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "", ignore);
        } finally {
            cursor.close();
        }
    }

    /**
     * 恢复被暂停的任务为pending
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-17 上午08:23:45
     */
    public void resumeToPending(final int id) {
        mProviderHelper.updateDownloadingTaskState(BaseApplication.getInstance().getContentResolver(), id,
                TransferContract.Tasks.STATE_PENDING, TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(true);
    }

    /**
     * 恢复被暂停的任务为running
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-17 上午08:23:45
     */
    public void resumeToRunning(final int id) {
        mProviderHelper.updateDownloadingTaskState(BaseApplication.getInstance().getContentResolver(), id,
                TransferContract.Tasks.STATE_RUNNING, TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(true);
    }

    /**
     * 预览时暂停全部任务，不触发统计。
     *
     * @return
     */
    public int pauseAllTasksForPreview() {
        NetWorkVerifier.reset();

        return mProviderHelper.pauseAllTasksForPreview(BaseApplication.getInstance().getContentResolver());
    }

    public int pauseAllTasks() {
        DuboxLog.v(TAG, "pauseAllTasks()");
        NetWorkVerifier.reset();
        // 统计全部暂定的次数
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_PAUSE_DOWNLOAD_ALL);
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_PAUSE_ALL);
        int result = mProviderHelper.pauseAllTasks(BaseApplication.getInstance().getContentResolver());
        return result;
    }

    public void startAllTasks() {
        DuboxLog.v(TAG, "startAllTasks()");
        NetWorkVerifier.reset();
        mProviderHelper.startAllTasks(BaseApplication.getInstance().getContentResolver());
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(true);
    }

    /**
     * 恢复下载任务
     */
    public void restartAllTasks() {
        DuboxLog.v(TAG, "restartAllTasks()");
        NetWorkVerifier.reset();
        mProviderHelper.restartAllTasks(BaseApplication.getInstance().getContentResolver());
    }

    public static void shutdown() {
        synchronized (DownloadTaskManager.class) {
            if (sAddDownloadTaskExecutor != null) {
                sAddDownloadTaskExecutor.shutdownNow();
                sAddDownloadTaskExecutor = null;
            }
        }
    }

    /**
     * 根据任务ID返回对应的任务对象
     *
     * @param id 任务ID
     * @return 任务对象
     */
    public TransferTask getTaskByID(int id) {
        String[] projection = new String[] { TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL, TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE,
                TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE, TransferContract.Tasks.OFFSET_SIZE, TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.DATE, TransferContract.DownloadTasks.PRIORITY,
                CloudFileContract.Files.FILE_SERVER_MD5 };
        final Cursor cursor = BaseApplication.getInstance().getContentResolver().query(TransferContract.DownloadTasks.buildUri(mBduss),
                projection, TransferContract.Tasks._ID + "=?", new String[] { String.valueOf(id) }, null);

        if (cursor == null) {
            return null;
        }

        DownloadTask task = null;
        try {
            if (cursor.moveToFirst()) {
                task = new DownloadTask(cursor, null, mBduss, mUid, null);
            }
        } catch (Exception ignore) {
            DuboxLog.w(TAG, "", ignore);
        } finally {
            cursor.close();
        }

        return task;
    }

    /**
     * 删除一个任务
     *
     * @param taskInfos 任务ID
     * @param isDeleteFile 是否删除文件
     */
    public void removeTask(final List<Integer> taskInfos, final boolean isDeleteFile) {
        // 操作数据库删除此任务
        mProviderHelper.deleteTask(BaseApplication.getInstance().getContentResolver(), isDeleteFile, taskInfos);

        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_DOWNLOAD_UPDATE, -1, -1, null);
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(true);
    }

    /**
     * 通知UI更新，调度器启动
     *
     * @param isNofityScheduler 是否通知调度器更新，只在第二次批量任务添加后通知
     */
    private void notifyUriUpdate(boolean isNofityScheduler) {
        DuboxLog.d(TAG, "批量添加，通知UI更新");
        BaseApplication.getInstance().getContentResolver()
                .notifyChange(TransferContract.DownloadTasks.PROCESSING_CONTENT_URI, null, false);
        BaseApplication.getInstance().getContentResolver()
                .notifyChange(TransferContract.DownloadTasks.FINISHED_CONTENT_URI, null, false);
        if (isNofityScheduler) {
            BaseApplication.getInstance().getContentResolver()
                    .notifyChange(TransferContract.DownloadTasks.SCHEDULER_CONTENT_URI, null, false);
        }
    }

    public void startScheduler() {
        BaseApplication.getInstance().getContentResolver()
                .notifyChange(TransferContract.DownloadTasks.SCHEDULER_CONTENT_URI, null, false);
    }

    /**
     * 第三方添加下载任务
     *
     * @param downloadFiles
     * @param processorFactory
     * @param downloadPriority
     */
    public void addOpenDownloadListTask(final List<? extends IDownloadable> downloadFiles,
                                        final IDownloadProcessorFactory processorFactory, final int downloadPriority) {
        runInDownloadTaskAddThread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
                if (downloadFiles.isEmpty()) {
                    return;
                }

                final Pair<Map<Integer, Integer>, Map<String, Integer>> tasks = fetchThenStopRunningTasks();
                final TaskResultReceiver taskResultReceiver =
                        new TaskResultReceiver(this, new Handler(BaseApplication.getInstance().getMainLooper())) {
                            @Override
                            public void handleSuccess(@NonNull @NotNull Object reference) {
                                DuboxLog.i(TAG, "DownloadTaskManager resumeRunningTasks");
                                resumeRunningTasks(tasks.first);
                            }

                            @Override
                            public void handleFailed(@NonNull @NotNull Object reference) {

                            }
                        };

                // 保证上面的pause状态被调度器执行后，才能接着往下走
                SystemClock.sleep(1000L);

                final Map<String, Integer> stopRunningMaps = tasks.second;
                if (!stopRunningMaps.isEmpty()) {
                    ArrayList<Integer> tempTaskIds = new ArrayList<Integer>();
                    // 去掉已经被条件到下载任务中的文件
                    for (IDownloadable downloadFile : downloadFiles) {
                        String remoteUrl = downloadFile.getFilePath();

                        Integer key = stopRunningMaps.get(remoteUrl);
                        if (key != null) {
                            tempTaskIds.add(key);
                        }
                    }
                    stopRunningMaps.values().removeAll(tempTaskIds);
                    DuboxLog.i(TAG, "DownloadTaskManager remove repeat task");
                }

                addDownloadListTask(downloadFiles, processorFactory, taskResultReceiver, downloadPriority);
            }
        });
    }

    private Pair<Map<Integer, Integer>, Map<String, Integer>> fetchThenStopRunningTasks() {
        // 查出全部正在运行的和等待的任务
        final Cursor cursor =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(TransferContract.DownloadTasks.buildSchedulerUri(Account.INSTANCE.getNduss()),
                                new String[] { TransferContract.Tasks._ID, TransferContract.Tasks.STATE , TransferContract.Tasks.REMOTE_URL},
                                "(" + TransferContract.Tasks.STATE + "="
                                        + String.valueOf(TransferContract.Tasks.STATE_RUNNING) + " OR "
                                        + TransferContract.Tasks.STATE + "="
                                        + String.valueOf(TransferContract.Tasks.STATE_PENDING) + ")", null, null);

        Map<Integer, Integer> stateMaps = new HashMap<Integer, Integer>();
        Map<String, Integer> remoteUrlMaps = new HashMap<String, Integer>();

        if (cursor == null) {
            return Pair.create(stateMaps, remoteUrlMaps);
        }

        try {
            while (cursor.moveToNext()) {
                final int id = cursor.getInt(0);
                final int state = cursor.getInt(1);
                final String remoteUrl = cursor.getString(2);

                stateMaps.put(id, state);
                remoteUrlMaps.put(remoteUrl, id);
            }
        } catch (Exception ignore) {
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }

        int result = pauseAllTasksForPreview();
        DuboxLog.d(TAG, "fetchThenStopRunningTasks pause result:" + result);

        return Pair.create(stateMaps, remoteUrlMaps);
    }

    private void resumeRunningTasks(final Map<Integer, Integer> runningAndPendingTaskIds) {
        if (runningAndPendingTaskIds.isEmpty()) {
            return;
        }

        final ArrayList<Integer> runningTaskIds = new ArrayList<Integer>(2);
        final ArrayList<Integer> pendingTaskIds = new ArrayList<Integer>();
        for (Map.Entry<Integer, Integer> task : runningAndPendingTaskIds.entrySet()) {
            final Integer taskId = task.getKey();
            if (TransferContract.Tasks.STATE_RUNNING == task.getValue()) {
                runningTaskIds.add(taskId);
            } else {
                pendingTaskIds.add(taskId);
            }
        }

        final ContentValues values = new ContentValues(1);
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);

        final ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        final Uri uri = TransferContract.DownloadTasks.buildProcessingUri(Account.INSTANCE.getNduss());

        // 先恢复暂停前running的任务
        if (!runningTaskIds.isEmpty()) {
            try {
                final int result = contentResolver.update(uri, values, TransferContract.Tasks._ID
                        + " IN(" + TextUtils.join(",", runningTaskIds) + ")", null);
                DuboxLog.d(TAG, "result:" + result);
            } catch (Exception ignore) {
                DuboxLog.w(TAG, "恢复运行任务失败", ignore);
            }
        }

        // 再恢复暂停前pending的任务
        if (!pendingTaskIds.isEmpty()) {
            try {
                final int result = contentResolver.update(uri, values, TransferContract.Tasks._ID
                        + " IN(" + TextUtils.join(",", pendingTaskIds) + ")", null);
                DuboxLog.d(TAG, "result:" + result);
            } catch (Exception ignore) {
                DuboxLog.w(TAG, "恢复等待任务失败", ignore);
            }
        }
    }
}
