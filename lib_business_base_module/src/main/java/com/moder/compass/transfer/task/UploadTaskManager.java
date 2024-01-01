package com.moder.compass.transfer.task;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_UPDATE;
import static com.moder.compass.log.VipPayLoggerKt.KEY_PREMIUM_AGENT_UPLOAD_SPACE;
import static com.moder.compass.log.VipPayLoggerKt.statisticVipPremiumLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dubox.drive.transfer.task.IUploadTaskManager;
import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.kernel.android.util.network.NetWorkVerifier;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.localfile.utility.FilterType;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.transfer.base.ITransferInterceptor;
import com.moder.compass.transfer.base.IUploadInfoGenerator;
import com.moder.compass.transfer.base.UploadInfo;
import com.moder.compass.transfer.base.UploadInterceptorInfo;
import com.moder.compass.transfer.storage.UploadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.dubox.drive.db.transfer.upload.UploadTaskProviderInfo;
import com.moder.compass.transfer.transmitter.TransferNumManager;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;

public class UploadTaskManager implements IUploadTaskManager {
    private static final String TAG = "UploadTaskManager";

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

    private static ExecutorService addUploadTaskExecutor;
    private final UploadTaskProviderHelper mProviderHelper;
    private final String mBduss;
    private final String mUid;

    public UploadTaskManager(String bduss, String uid) {
        mBduss = TextUtils.isEmpty(bduss) ? "" : bduss;
        mUid = TextUtils.isEmpty(uid) ? "" : uid;
        mProviderHelper = new UploadTaskProviderHelper(mBduss);
    }

    /**
     * 重新上传全部失败的文件
     */
    public void reUpload(final long[] id) {
        NetWorkVerifier.reset();
        ArrayList<String> taskIds = new ArrayList<>();
        if (id != null && id.length != 0) {
            for (long l : id) {
                taskIds.add(String.valueOf(l));
            }
        }
        String idArgs = TextUtils.join(",", taskIds);
        final Cursor cursor =
            BaseApplication.getInstance().getContentResolver().query(
                TransferContract.UploadTasks.buildUri(mBduss),
                new String[] {
                    TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL,
                    TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.SIZE,
                    TransferContract.Tasks.TYPE, TransferContract.Tasks.TRANSMITTER_TYPE,
                    TransferContract.UploadTasks.QUALITY, TransferContract.UploadTasks.UPLOAD_ID,
                    TransferContract.Tasks.EXTRA_INFO_NUM },
                TransferContract.Tasks.STATE + "=" + TransferContract.Tasks.STATE_FAILED +
                        (TextUtils.isEmpty(idArgs) ? ""
                                : " AND " + TransferContract.Tasks._ID + " IN(" + idArgs + ")"),
                null, null);
        if (cursor == null) {
            return;
        }
        try {
            final int count = cursor.getCount();
            final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>(count + 1);
            final ArrayList<String> ids = new ArrayList<String>(count);
            Uri processingUri = TransferContract.UploadTasks.buildProcessingUri(mBduss);
            while (cursor.moveToNext()) {
                // 删除
                ids.add(String.valueOf(cursor.getInt(0)));
                // 添加
                final ContentValues values = new ContentValues(8);
                values.put(TransferContract.Tasks.LOCAL_URL, cursor.getString(1));
                values.put(TransferContract.Tasks.REMOTE_URL, cursor.getString(2));
                values.put(TransferContract.Tasks.SIZE, cursor.getLong(3));
                values.put(TransferContract.Tasks.TYPE, cursor.getInt(4));
                values.put(TransferContract.Tasks.TRANSMITTER_TYPE, cursor.getString(5));
                values.put(TransferContract.UploadTasks.QUALITY, cursor.getInt(6));
                values.put(TransferContract.UploadTasks.UPLOAD_ID, cursor.getString(7));
                values.put(TransferContract.Tasks.EXTRA_INFO_NUM, cursor.getString(8));
                values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
                batch.add(ContentProviderOperation.newInsert(processingUri).withValues(values).build());
            }
            batch.add(0,
                    ContentProviderOperation.newDelete(TransferContract.UploadTasks.buildFailedUri(mBduss))
                            .withSelection(TransferContract.Tasks._ID + " IN(" + TextUtils.join(",", ids) + ")", null).build());
            BaseApplication.getInstance().getContentResolver().applyBatch(TransferContract.CONTENT_AUTHORITY, batch);
        } catch (RemoteException ignore) {
        } catch (OperationApplicationException ignore) {
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
        mProviderHelper.updateUploadingTaskState(BaseApplication.getInstance().getContentResolver(), id,
                TransferContract.Tasks.STATE_PENDING, TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(false);
    }

    /**
     * 恢复被暂停的任务为running
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-17 上午08:23:45
     */
    public void resumeToRunning(final int id) {
        mProviderHelper.updateUploadingTaskState(BaseApplication.getInstance().getContentResolver(), id,
                TransferContract.Tasks.STATE_RUNNING, TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT);
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(false);
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
                    .query(TransferContract.UploadTasks.buildProcessingUri(mBduss),
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
     * 获取待处理的任务的数量
     *
     * @return
     */
    public int getAllProcessingTaskSize() {
        Cursor cursor = null;
        try {
            cursor = BaseApplication
                    .getInstance()
                    .getContentResolver()
                    .query(TransferContract.UploadTasks.buildProcessingUri(mBduss),
                            new String[]{"COUNT(0)"},
                            TransferContract.Tasks.STATE + "=? OR " + TransferContract.Tasks.STATE + "=? OR " + TransferContract.Tasks.STATE + "=?",
                            new String[]{String.valueOf(TransferContract.Tasks.STATE_PENDING), String.valueOf(TransferContract.Tasks.STATE_RUNNING),
                                    String.valueOf(TransferContract.Tasks.STATE_PAUSE)},
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

    public int pauseAllTasks() {
        DuboxLog.v(TAG, "pauseAllTasks()");
        NetWorkVerifier.reset();
        // 统计全部暂定的次数
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_PAUSE_UPLOAD_ALL);
        StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.TOTAL_PAUSE_ALL);
        int result = mProviderHelper.pauseAllTasks(BaseApplication.getInstance().getContentResolver());
        return result;
    }

    /**
     * 删除一个任务
     *
     * @param taskInfos 任务ID
     * @param isDeleteFile 是否删除文件
     */
    public void removeTask(List<Integer> taskInfos, final boolean isDeleteFile) {
        // 操作数据库删除此任务
        mProviderHelper.deleteTask(BaseApplication.getInstance().getContentResolver(), isDeleteFile, taskInfos);
        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(false);
    }

    public void startAllTasks() {
        DuboxLog.v(TAG, "startAllTasks()");
        NetWorkVerifier.reset();
        mProviderHelper.startAllTasks(BaseApplication.getInstance().getContentResolver());

        // 通知业务层，任务数量变化了
        TransferNumManager.getInstance().setTransferNumChanged(false);
    }

    /**
     * 批量添加到上传列表
     *
     * @param uploadInfoGenerator : 生成上传文件的集合
     * @param uploadProcessorFactory ： 文件上传的去重器
     * @param resultReceiver
     */
    public void add2UploadList(final IUploadInfoGenerator uploadInfoGenerator,
            final IUploadProcessorFactory uploadProcessorFactory, final TaskResultReceiver resultReceiver,
                               ITransferInterceptor mTransferInterceptor) {
        NetWorkVerifier.reset();
        runInUploadTaskAddThread(new Runnable() {

            @Override
            public void run() {
                // 第一步：生成传输信息 by linwentao
                final Pair<List<UploadInfo>, UploadInterceptorInfo> generateResult = uploadInfoGenerator.generate();
                if (generateResult == null || generateResult.first == null) {
                    DuboxLog.d("WechatUpload", "上传数据为空！");
                    if (resultReceiver != null) {
                        resultReceiver.sendFailed();
                    }
                    return;
                }
                UploadInterceptorInfo interceptorInfo = generateResult.second;
                if (interceptorInfo != null && interceptorInfo.getCode() > 0) {
                    mTransferInterceptor.intercept(interceptorInfo);
                }
                List<UploadInfo> upInfoList = generateResult.first;
                DuboxLog.d("WechatUpload", "上传数据 ： " + upInfoList.size());
                // start process
                long time1 = System.currentTimeMillis();

                // 第二步：上传任务去重 第一步：查询云端文件信息 by linwentao
                final HashMap<String, DBBean> uploadDBBeanMap =
                        createUploadDBBeanMap(BaseApplication.getInstance(), upInfoList);

                int size = upInfoList.size();
                int i = 0;
                synchronized (UploadTaskProviderInfo.getSyncObject()) {
                    if (size > MIN_ENABLE_TRANSACTION_SIZE) {
                        UploadTaskProviderInfo.beginTransaction();
                    }

                    // 是否为第一批任务
                    int k = 0;

                    try {
                        for (UploadInfo upInfo : upInfoList) {
                            DBBean bean = null;
                            if (uploadDBBeanMap != null) {
                                bean = uploadDBBeanMap.get(upInfo.getRemotePath());
                            }

                            // 上传任务去重 第二步：查询已有的上传任务; 第三步：根据不同类型处理数据库插入,添加任务到上传列表   by linwnetao
                            uploadProcessorFactory.createProcessor(upInfo, bean,
                                    !UploadTaskProviderInfo.inTransaction()).process();
                            // 每MAX_TRANSACTION_SIZE个文件中断一次事务，给其他操作写数据库的机会
                            if ((i > MAX_TRANSACTION_SIZE) && UploadTaskProviderInfo.inTransaction()) {
                                i = 0;
                                UploadTaskProviderInfo.endTransactionSuccessful();

                                // 上传任务去重 第四步：通知Uri发生变化，便于调度器开始调度 by linwentao
                                notifyUriUpdate(k++ == 1);

                                if (size > MIN_ENABLE_TRANSACTION_SIZE) {
                                    SystemClock.sleep(SLEEP_TIME * (k == 1 ? 5 : 1));
                                    UploadTaskProviderInfo.beginTransaction();
                                }
                            }
                            i++;
                            size--;
                        }
                    } finally {
                        if (UploadTaskProviderInfo.inTransaction()) {
                            UploadTaskProviderInfo.endTransactionSuccessful();
                            notifyUriUpdate(true);
                        }

                        if (resultReceiver != null) {
                            resultReceiver.sendSuccess();
                        }

                        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_UPDATE, -1, -1, null);
                        // 通知业务层，任务数量变化了
                        TransferNumManager.getInstance().setTransferNumChanged(false);
                        statisticVipPremiumLog(KEY_PREMIUM_AGENT_UPLOAD_SPACE);
                    }
                    DuboxLog.d(TAG, "asyncProcessUploadFile  cost = " + (System.currentTimeMillis() - time1));
                }
            }
        });
    }

    /**
     * 通过remoteUrl获取对应的TransferTask
     * <P>
     * 只适用于uploadTask
     *
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-11-9 上午11:49:34
     */
    public UploadTask getUploadTaskByRemoteUrl(String remoteUrl) {
        if (remoteUrl == null) {
            return null;
        }

        final Cursor cursor =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(TransferContract.UploadTasks.buildUri(mBduss),
                                new String[] { TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL, TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE,
                                        TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE, TransferContract.Tasks.OFFSET_SIZE, TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.DATE,
                                    TransferContract.UploadTasks.NEED_OVERRIDE, TransferContract.UploadTasks.QUALITY }, TransferContract.Tasks.REMOTE_URL + "=?",
                                new String[] { remoteUrl }, null);

        if (cursor == null) {
            return null;
        }

        UploadTask task = null;
        try {
            if (cursor.moveToFirst()) {
                task = new UploadTask(BaseApplication.getInstance(), cursor, mBduss, mUid, null);
            }
        } catch (Exception ignore) {
        } finally {
            cursor.close();
        }

        return task;
    }

    /**
     * 添加到异步上传任务添加队列执行
     *
     * @param runnable
     * @author 孙奇 V 1.0.0 Create at 2012-12-10 下午03:07:08
     */
    private void runInUploadTaskAddThread(Runnable runnable) {
        synchronized (UploadTask.class) {
            if (addUploadTaskExecutor == null || addUploadTaskExecutor.isShutdown()) {
                addUploadTaskExecutor = Executors.newSingleThreadExecutor();
            }
        }
        addUploadTaskExecutor.execute(runnable);
    }

    public static void shutDown() {
        synchronized (UploadTaskManager.class) {
            if (addUploadTaskExecutor != null) {
                addUploadTaskExecutor.shutdownNow();
                addUploadTaskExecutor = null;
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
        final Cursor cursor =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(TransferContract.UploadTasks.buildUri(mBduss),
                                new String[] { TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL, TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE,
                                        TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE, TransferContract.Tasks.OFFSET_SIZE, TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.DATE },
                                TransferContract.Tasks._ID + "=?", new String[] { String.valueOf(id) }, null);

        if (cursor == null) {
            return null;
        }

        UploadTask task = null;
        try {
            if (cursor.moveToFirst()) {
                task = new UploadTask(BaseApplication.getInstance(), cursor, mBduss, mUid, null);
            }
        } catch (Exception ignore) {
        } finally {
            cursor.close();
        }

        return task;
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
                .update(TransferContract.UploadTasks.buildProcessingUri(mBduss),
                        values,
                        TransferContract.Tasks._ID + "=? AND (" + TransferContract.Tasks.STATE + "=" + TransferContract.Tasks.STATE_PENDING + " OR " + TransferContract.Tasks.STATE + "="
                                + TransferContract.Tasks.STATE_RUNNING + ")", new String[] { String.valueOf(id) });
    }

    /**
     * 包括已经删除过的任务
     *
     * @param mFilterType
     * @return
     */
    public HashSet<String> queryAllUrlsByType(FilterType mFilterType) {
        HashSet<String> set = new HashSet<String>();

        Cursor c =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(TransferContract.AlbumBackupTasks.buildUri(mBduss), new String[] { TransferContract.UploadTasks.LOCAL_URL }, null, null,
                                null);

        if (c != null) {
            try {
                while (c.moveToNext()) {
                    String url = c.getString(0);
                    if (FilterType.acceptType(url, mFilterType)) {
                        set.add(url);
                    }
                }
            } catch (Exception ignore) {
            } finally {
                c.close();
                c = null;
            }
        }

        return set;
    }

    /**
     * 通知UI更新，调度器启动 *
     *
     * @param isNofityScheduler 是否通知调度器更新，只在第二次批量任务添加后通知
     */
    private void notifyUriUpdate(boolean isNofityScheduler) {
        BaseApplication.getInstance().getContentResolver()
                .notifyChange(TransferContract.UploadTasks.PROCESSING_CONTENT_URI, null, false);
        BaseApplication.getInstance().getContentResolver().notifyChange(TransferContract.UploadTasks.FINISHED_CONTENT_URI, null, false);
        if (isNofityScheduler) {
            BaseApplication.getInstance().getContentResolver()
                    .notifyChange(TransferContract.UploadTasks.SCHEDULER_CONTENT_URI, null, false);
        }
    }

    public void startScheduler() {
        BaseApplication.getInstance().getContentResolver().notifyChange(TransferContract.UploadTasks.SCHEDULER_CONTENT_URI, null, false);
    }

    /**
     * 查询图片文件的md5
     *
     * @param context 上下文
     * @param upInfoList
     * @return
     */
    private HashMap<String, DBBean> createUploadDBBeanMap(Context context, final List<UploadInfo> upInfoList) {
        final Cursor cursor =
                context.getContentResolver().query(
                        CloudFileContract.Files.buildFilesUri(mBduss),
                        new String[] { CloudFileContract.Files.FILE_SERVER_MD5,
                            CloudFileContract.Files.FILE_CLIENT_MTIME, CloudFileContract.Files.FILE_SIZE,
                            CloudFileContract.Files.FILE_SERVER_PATH }, buildSelection(upInfoList), null, null);

        if (cursor == null) {
            DuboxLog.d(TAG, "getUploadDBBeanMap cursor is null");
            return null;
        }
        final HashMap<String, DBBean> uploadDBBeanMap = new HashMap<String, DBBean>();

        try {
            while (cursor.moveToNext()) {
                DBBean bean = new DBBean();
                bean.remoteMd5 = cursor.getString(0);
                bean.remoteFileCMTime = cursor.getLong(1);
                bean.remoteFileSize = cursor.getLong(2);
                String path = cursor.getString(3);
                uploadDBBeanMap.put(path, bean);
            }
        } catch (Exception ignore) {
        } finally {
            cursor.close();
        }

        return uploadDBBeanMap;
    }

    /**
     * 构建sql select in where语句
     *
     * @param upInfoList 文件的服务器端路径集合
     * @return selection
     */
    private String buildSelection(List<UploadInfo> upInfoList) {
        // 构建SQL delete files where server_path in('path1','path2')

        final StringBuilder sb = new StringBuilder();

        sb.append(CloudFileContract.Files.FILE_SERVER_PATH).append(" IN(");

        for (UploadInfo info : upInfoList) {
            // 需要对单引号转义，‘转为''
            sb.append("'").append(info.getRemotePath().replaceAll("'", "''")).append("',");
        }

        if (sb.lastIndexOf(",") != -1) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(")");

        return sb.toString();
    }
}
