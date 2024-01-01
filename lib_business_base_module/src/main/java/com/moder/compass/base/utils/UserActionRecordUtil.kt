package com.moder.compass.base.utils

import com.moder.compass.base.utils.PersonalConfigKey.LAST_RATING_SHOW_TIME
import com.moder.compass.base.utils.PersonalConfigKey.RATING_SHOW_OP_CONDITION
import com.dubox.drive.kernel.architecture.config.PersonalConfig
import com.moder.compass.statistics.KEY_USER_FEATURE_ADD_DOWNLOAD_TASK
import com.moder.compass.statistics.KEY_USER_FEATURE_SHARE_TRANSFER
import com.moder.compass.statistics.UserFeatureReporter

/**
 * 完成上传操作
 */
private const val OP_UPLOAD = 1

/**
 * 完成下载操作
 */
private const val OP_DOWNLOAD = 1 shl 1

/**
 * 完成分享操作
 */
private const val OP_SHARE = 1 shl 2

/**
 * 完成转存
 */
private const val OP_SAVE_TO = 1 shl 3

/**
 * 记录用户操作
 */
object UserActionRecordUtil {

    /**
     * 是否需要监听【上传、下载、转存、删除】操作,
     * 评分引导已经显示过，不用再记录操作，直接走7天循环自动显示评分引导的逻辑
     */
    private val isNeedShowGuideByOperate: Boolean by lazy {
        PersonalConfig.getInstance().getLong(LAST_RATING_SHOW_TIME, 0L) == 0L
    }

    private fun recordOperate(opCode: Int) {
        if (!isNeedShowGuideByOperate){
            return
        }
        val resultCode =
            PersonalConfig.getInstance().getInt(RATING_SHOW_OP_CONDITION, 0) or opCode
        PersonalConfig.getInstance().putInt(RATING_SHOW_OP_CONDITION, resultCode)
    }

    /**
     * 记录下载操作
     */
    fun recordDownloadOperate() {
        recordOperate(OP_DOWNLOAD)
        UserFeatureReporter(KEY_USER_FEATURE_ADD_DOWNLOAD_TASK).reportAFAndFirebase()
    }

    /**
     * 记录上传操作
     */
    fun recordUploadOperate() {
        recordOperate(OP_UPLOAD)
    }

    /**
     * 记录转存操作
     */
    fun recordSaveToOperate() {
        recordOperate(OP_SAVE_TO)
        UserFeatureReporter(KEY_USER_FEATURE_SHARE_TRANSFER).reportAFAndFirebase()
    }

    /**
     * 记录分享操作
     */
    fun recordShareOperate() {
        recordOperate(OP_SHARE)
    }

    /**
     * 判断是否超过两项操作
     */
    fun isMoreThanOneOP(): Boolean {
        val opCode = PersonalConfig.getInstance().getInt(RATING_SHOW_OP_CONDITION, 0)
        return opCode and (opCode - 1) != 0
    }

    /**
     * 至少一次操作
     */
    fun isAtLeastOneOp(): Boolean {
        val opCode = PersonalConfig.getInstance().getInt(RATING_SHOW_OP_CONDITION, 0)
        return opCode != 0
    }

    /**
     * 是否上传过
     */
    fun isUploadOp(): Boolean {
        val opCode = PersonalConfig.getInstance().getInt(RATING_SHOW_OP_CONDITION, 0)
        return opCode and OP_UPLOAD != 0
    }
}