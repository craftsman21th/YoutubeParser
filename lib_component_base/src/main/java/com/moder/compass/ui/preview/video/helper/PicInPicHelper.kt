package com.moder.compass.ui.preview.video.helper

import android.app.Activity
import android.app.AppOpsManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.moder.compass.BaseApplication
import com.dubox.drive.basemodule.R
import com.moder.compass.ui.manager.BaseDialogBuilder
import com.moder.compass.ui.manager.DialogCtrListener

/**
 *
 * @author huping05
 * @since Terabox 2022/12/14
 */
object PicInPicHelper {
    /**
     * 画中画操作事件
     */
    const val ACTION_MEDIA_CONTROL = "media_control"
    const val EXTRA_CONTROL_TYPE = "control_type"
    const val CONTROL_TYPE_PLAY = 1
    const val CONTROL_TYPE_PAUSE = 2
    const val CONTROL_TYPE_LAST = 3
    const val CONTROL_TYPE_NEXT = 4
    const val REQUEST_TYPE_PLAY = 1
    const val REQUEST_TYPE_PAUSE = 2
    const val REQUEST_TYPE_LAST = 3
    const val REQUEST_TYPE_NEXT = 4

    /**
     * 申请画中画设置弹窗
     */
    fun showPicInPicDialog(activity: Activity?) {
        val builder = com.moder.compass.ui.manager.BaseDialogBuilder()
        val dialog: Dialog = builder.buildTipsDialog(activity,
                R.string.dialog_title_prompt, R.string.video_pic_in_pic_enable_tip,
                R.string.permission_advance_two_close_tip_confirm, R.string.permission_advance_two_close_tip_cancel)
        builder.setOnDialogCtrListener(object : com.moder.compass.ui.manager.DialogCtrListener {
            override fun onOkBtnClick() {
                dialog.dismiss()
                if (activity != null) {
                    requestPicInPicSettings(activity)
                }
            }

            override fun onCancelBtnClick() {
                dialog.dismiss()
            }
        })
        dialog.show()
    }

    /**
     * 跳转到画中画设置页面
     */
    private fun requestPicInPicSettings(context: Context) {
        val intent = Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS")
        if (context !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    /**
     * 检测画中画权限是否已经开启
     *
     * @return
     */
    fun isPicInPicAllowed(): Boolean {
        var isAllowed = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appOpsManager = BaseApplication.getContext()
                    .getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            isAllowed = AppOpsManager.MODE_ALLOWED ==
                    appOpsManager.checkOpNoThrow(
                            AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                            BaseApplication.getContext().applicationInfo.uid,
                            BaseApplication.getContext().packageName
                    )
        }
        return isAllowed
    }

    /**
     * 是否是画中画模式
     * @return
     */
    fun isPictureInPictureModel(activity: Activity?): Boolean {
        return activity != null
                && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity.isInPictureInPictureMode)
    }
}