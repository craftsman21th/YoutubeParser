package com.moder.compass.transfer

import android.content.Context
import android.os.Build
import com.moder.compass.account.Account.nduss
import com.moder.compass.account.Account.uid
import com.moder.compass.base.utils.UserActionRecordUtil.recordDownloadOperate
import com.dubox.drive.permission.IPermission
import com.dubox.drive.permission.PermissionHelperFactory
import com.dubox.drive.cloudfile.base.IDownloadable
import com.moder.compass.transfer.task.DownloadTaskManager
import com.moder.compass.transfer.task.IDownloadProcessorFactory
import com.dubox.drive.transfer.task.IDownloadTaskManager
import com.moder.compass.transfer.task.ITaskStateCallback
import com.moder.compass.transfer.task.TaskResultReceiver
import com.moder.compass.transfer.task.TransferTask

/**
 * @author sunmeng12
 * @since moder 2022/6/1
 *
 */
class DownloadTaskNoUIManagerProxy(private val context: Context) :
    IDownloadTaskManager {

    private var mDownloadManager: IDownloadTaskManager = DownloadTaskManager(nduss, uid)

    override fun addDownloadListTask(
        downloadFiles: List<IDownloadable?>?,
        processorFactory: IDownloadProcessorFactory?,
        resultReceiver: TaskResultReceiver<*>?,
        downloadPriority: Int
    ) {
        val allPermissionGranted =
            PermissionHelperFactory.createPermissionHelper()
                .isAllPermissionGranted(
                    context,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        IPermission.STORAGE_GROUP_PERMISSION_33
                    } else {
                        IPermission.STORAGE_GROUP_PERMISSION
                    }
                )
        if (!allPermissionGranted) {
            return
        }

        addDownloadListTask(downloadFiles, processorFactory, resultReceiver, downloadPriority, null)
    }

    override fun addDownloadListTask(
        downloadFiles: List<IDownloadable?>?,
        processorFactory: IDownloadProcessorFactory?,
        resultReceiver: TaskResultReceiver<*>?,
        downloadPriority: Int,
        callback: ITaskStateCallback?
    ) {
        addDownloadListTaskReality(
            downloadFiles,
            processorFactory,
            resultReceiver,
            downloadPriority
        )
    }

    private fun addDownloadListTaskReality(
        downloadFiles: List<IDownloadable?>?,
        processorFactory: IDownloadProcessorFactory?,
        resultReceiver: TaskResultReceiver<*>?,
        downloadPriority: Int
    ) {
        mDownloadManager.addDownloadListTask(
            downloadFiles, processorFactory,
            resultReceiver, downloadPriority
        )
        recordDownloadOperate()
    }

    override fun addOpenDownloadListTask(
        downloadFiles: List<IDownloadable?>?,
        processorFactory: IDownloadProcessorFactory?, downloadPriority: Int
    ) {
        mDownloadManager.addOpenDownloadListTask(
            downloadFiles,
            processorFactory,
            downloadPriority
        )
        recordDownloadOperate()
    }

    override fun addDownloadTask(
        downloadable: IDownloadable?, processorFactory: IDownloadProcessorFactory?,
        resultReceiver: TaskResultReceiver<*>?, downloadPriority: Int
    ) {
        mDownloadManager.addDownloadTask(
            downloadable,
            processorFactory,
            resultReceiver,
            downloadPriority
        )
        recordDownloadOperate()
    }

    override fun getAllActiveTaskSize(): Int {
        return mDownloadManager.allActiveTaskSize
    }

    override fun getAllProcessingTaskSize(): Int {
        return mDownloadManager.allProcessingTaskSize
    }

    override fun getTaskByID(id: Int): TransferTask? {
        return mDownloadManager.getTaskByID(id)
    }

    override fun startScheduler() {
        mDownloadManager.startScheduler()
    }

    override fun pauseAllTasks(): Int {
        return mDownloadManager.pauseAllTasks()
    }

    override fun pauseAllTasksForPreview(): Int {
        return mDownloadManager.pauseAllTasksForPreview()
    }

    override fun pauseTask(id: Int) {
        mDownloadManager.pauseAllTasks()
    }

    override fun reDownload(ids: LongArray?) {
        mDownloadManager.reDownload(ids)
    }

    override fun removeTask(taskInfos: List<Int?>?, isDeleteFile: Boolean) {
        mDownloadManager.removeTask(taskInfos, isDeleteFile)
    }

    override fun resumeToPending(id: Int) {
        mDownloadManager.resumeToPending(id)
    }

    override fun resumeToRunning(id: Int) {
        mDownloadManager.resumeToRunning(id)
    }

    override fun startAllTasks() {
        mDownloadManager.startAllTasks()
    }

}