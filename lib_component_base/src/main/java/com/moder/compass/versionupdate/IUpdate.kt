package com.moder.compass.versionupdate

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import com.moder.compass.versionupdate.download.DownloadStatus

/**
 * @author sunmeng
 * create at 2021-04-26
 * Email: sunmeng12@baidu.com
 */
interface IUpdate {
    /**
     * 检查版本更新，如果有更新则设置 UpdateTipsHelper 中的值
     * 如果是强制更新则直接展示弹窗
     * @param manual: true 用户手动检测，需要提示检测结果，false 进入 app 时主动检测，不需要 toast
     */
    fun checkUpdate(manual: Boolean, tryShowVersionDialog: Boolean, simpleCheck: Boolean)

    /**
     * 获取是否有新版本
     */
    fun getHasNewVersionLivaData(): LiveData<Boolean?>

    /**
     * 开始更新版本
     */
    fun update(): Boolean

    fun checkApkFile(): Boolean

    /**
     * 获取版本下载状态
     */
    fun getDownloadStatueLivaData(): LiveData<DownloadStatus?>?

    /**
     * 展示升级弹窗
     */
    fun showUpdateDialog(activity: FragmentActivity)

    /**
     * release
     * */
    fun release()

    /**
     * 强制更新
     * */
    fun isForceUpdate(): Boolean

    /**
     * 处理更新对话框结果
     * */
    fun handleUpdateDialogActivityResult(requestCode: Int, resultCode: Int)
}