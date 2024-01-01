package com.moder.compass.usecase

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moder.compass.BaseApplication
import com.dubox.drive.cloudfile.io.model.CloudFile
import com.dubox.drive.cloudfile.service.CloudFileServiceHelper
import com.dubox.drive.cloudfile.service.Extras
import com.dubox.drive.kernel.craft.UseCase
import com.moder.compass.transfer.getLastUploadPath
import com.moder.compass.util.receiver.BaseResultReceiver


typealias UpdateUploadDirAction = () -> LiveData<CloudFile?>

/**
 * 更新上传路径
 */
class UpdateUploadDirUseCase(dir: CloudFile?, private val ignoreHistory: Boolean = false) :
    UseCase<LiveData<CloudFile?>, UpdateUploadDirAction> {
    private val uploadDir = MutableLiveData<CloudFile?>()

    override val action: UpdateUploadDirAction = {
        uploadUploadDir(dir)
        uploadDir
    }


    private fun uploadUploadDir(cloudFile: CloudFile?) {
        val filePath = cloudFile?.filePath
        // 只有在首页 Tab 或文件 Tab 中才处理上次上传的逻辑
        if (!ignoreHistory && TextUtils.isEmpty(filePath)) {
            checkLastUploadFileExit(cloudFile)
        } else {
            uploadDir.value = cloudFile
        }
    }

    /**
     * 如果保存的地址为空或者和当前文件目录地址一致，直接返回
     */
    private fun checkLastUploadFileExit(cloudFile: CloudFile?) {
        val path = getLastUploadPath()
        // 没有保存文件地址或文件地址就是根目录
        if (TextUtils.equals(path, cloudFile?.path) || TextUtils.isEmpty(path)) {
            uploadDir.value = cloudFile
        } else {
            CloudFileServiceHelper.isFilePathExist(
                BaseApplication.getContext(), path,
                FileExistResultReceiver(this, path, cloudFile)
            )
        }
    }


    /**
     * 文件检查结果接收
     */
    inner class FileExistResultReceiver(
        viewModel: UpdateUploadDirUseCase,
        private val filePath: String,
        private val preCloudFile: CloudFile?
    ) :
        BaseResultReceiver<UpdateUploadDirUseCase>(
            viewModel,
            Handler(Looper.getMainLooper()),
            null
        ) {

        override fun onSuccess(reference: UpdateUploadDirUseCase, resultData: Bundle?) {
            super.onSuccess(reference, resultData)
            val isExist = (resultData != null
                    && resultData.getBoolean(Extras.EXTRA_FILE_PATH_EXIST))
            reference.uploadDir.value = if (isExist) {
                CloudFile(filePath)
            } else {
                preCloudFile
            }
        }

    }
}
