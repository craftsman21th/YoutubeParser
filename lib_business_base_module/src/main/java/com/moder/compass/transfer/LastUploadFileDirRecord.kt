package com.moder.compass.transfer

import com.dubox.drive.cloudfile.service.isInSafeBox
import com.dubox.drive.kernel.architecture.config.PersonalConfig

/**
 * @author sunmeng
 * create at 2022-01-20
 * Email: sunmeng12@moder.com
 * 用于记录用户上次上传文件的文件夹地址, 用户实际上传时就保存, 跟用户绑定
 * 不包括保险箱地址
 */
fun saveLastUploadCloudFilePath(path: String) {
    if (isInSafeBox(path)) {
        return
    }
    PersonalConfig.getInstance().putString(LAST_UPLOAD_FILE_PATH, path)
}

/**
 * 返回记录的上传地址，调用处检测文件是否存在
 */
fun getLastUploadPath(): String {
    return PersonalConfig.getInstance().getString(LAST_UPLOAD_FILE_PATH)
}



/**
 * SP 保存文件地址
 */
const val LAST_UPLOAD_FILE_PATH = "last_upload_file_path"
