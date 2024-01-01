package com.dubox.drive.transfer.task;

import java.util.HashSet;
import java.util.List;

import com.moder.compass.localfile.utility.FilterType;
import com.moder.compass.transfer.base.ITransferInterceptor;
import com.moder.compass.transfer.base.IUploadInfoGenerator;
import com.moder.compass.transfer.task.IUploadProcessorFactory;
import com.moder.compass.transfer.task.TaskResultReceiver;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.UploadTask;

public interface IUploadTaskManager {

    void add2UploadList(final IUploadInfoGenerator uploadInfoGenerator,
                        final IUploadProcessorFactory uploadProcessorFactory,
                        final TaskResultReceiver resultReceiver,
                        final ITransferInterceptor mTransferInterceptor);

    int getAllActiveTaskSize();

    int getAllProcessingTaskSize();

    TransferTask getTaskByID(int id);

    UploadTask getUploadTaskByRemoteUrl(String remoteUrl);

    int pauseAllTasks();

    void pauseTask(final int id);

    HashSet<String> queryAllUrlsByType(FilterType mFilterType);

    void removeTask(List<Integer> taskInfos, final boolean isDeleteFile);

    void resumeToPending(final int id);

    void resumeToRunning(final int id);

    void reUpload(final long[] ids);

    void startAllTasks();

    void startScheduler();
}
