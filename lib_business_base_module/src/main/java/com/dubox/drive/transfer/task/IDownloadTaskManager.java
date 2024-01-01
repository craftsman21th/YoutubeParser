package com.dubox.drive.transfer.task;

import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.task.IDownloadProcessorFactory;
import com.moder.compass.transfer.task.ITaskStateCallback;
import com.moder.compass.transfer.task.TaskResultReceiver;
import com.moder.compass.transfer.task.TransferTask;

import java.util.List;

public interface IDownloadTaskManager {

    public void addDownloadListTask(final List<? extends IDownloadable> downloadFiles,
                                    final IDownloadProcessorFactory processorFactory,
                                    final TaskResultReceiver resultReceiver,
                                    final int downloadPriority);

    public void addDownloadListTask(final List<? extends IDownloadable> downloadFiles,
                                    final IDownloadProcessorFactory processorFactory,
                                    final TaskResultReceiver resultReceiver,
                                    final int downloadPriority,
                                    final ITaskStateCallback callback);

    public void addOpenDownloadListTask(final List<? extends IDownloadable> downloadFiles,
                                        final IDownloadProcessorFactory processorFactory, final int downloadPriority);


    public void addDownloadTask(final IDownloadable downloadable, final IDownloadProcessorFactory processorFactory,
                                final TaskResultReceiver resultReceiver, final int downloadPriority);

    public int getAllActiveTaskSize();

    public int getAllProcessingTaskSize();

    public TransferTask getTaskByID(int id);

    public void startScheduler();

    public int pauseAllTasks();

    public int pauseAllTasksForPreview();

    public void pauseTask(final int id);

    public void reDownload(final long[] ids);

    public void removeTask(final List<Integer> taskInfos, final boolean isDeleteFile);

    public void resumeToPending(final int id);

    public void resumeToRunning(final int id);

    public void startAllTasks();


}

