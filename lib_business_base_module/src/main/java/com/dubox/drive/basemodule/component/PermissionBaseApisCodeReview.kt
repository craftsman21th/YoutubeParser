package com.dubox.drive.basemodule.component

import com.moder.compass.base.utils.AppInfoUtils
import com.moder.compass.statistics.IGNOR_HOT_OPEN_AD_PV
import com.moder.compass.statistics.StatisticsLogForMutilFields
import com.rubik.annotations.route.RRoute

/**
 * 暂时dependencies解耦
 * 接口方法抽取
 */
@RRoute(path = "stat/key/ignore/hot/open/ad/pv", forResult = true)
fun statKeyIgnoreHotOpenAdPV(): String {
    return IGNOR_HOT_OPEN_AD_PV
}


/**
 * 抽取自com.dubox.drive.permission.view.PermissionDialogActivity.showPermissionRequestDialog
 */
@RRoute(path = "stat/show/permission/request/dialog")
fun statShowPermissionRequestDialog(mType: Int) {
    statisticsDialog(
        if (AppInfoUtils.isCoverInstall()) StatisticsLogForMutilFields.StatisticsKeys.COVER_INSTALL_PERMISSION_REQUEST_DIALOG_SHOW else StatisticsLogForMutilFields.StatisticsKeys.FIRST_INSTALL_PERMISSION_REQUEST_DIALOG_SHOW,
        mType - 1
    )
}

/**
 * 抽取自com.dubox.drive.permission.view.PermissionDialogActivity.onRequestDialogOkClicked
 */
@RRoute(path = "stat/on/request/dialog/ok/clicked")
fun statOnRequestDialogOkClicked(mType: Int) {
    statisticsDialog(
        if (AppInfoUtils.isCoverInstall()) StatisticsLogForMutilFields.StatisticsKeys.COVER_INSTALL_PERMISSION_REQUEST_DIALOG_KNOW_BTN_CLICK else StatisticsLogForMutilFields.StatisticsKeys.FIRST_INSTALL_PERMISSION_REQUEST_DIALOG_KNOW_BTN_CLICK,
        mType - 1
    )
}

/**
 * 抽取自com.dubox.drive.permission.view.PermissionDialogActivity.onResultDialogOkClicked
 */
@RRoute(path = "stat/on/result/dialog/ok/clicked")
fun statOnResultDialogOkClicked(mType: Int) {
    statisticsDialog(
        if (AppInfoUtils.isCoverInstall()) StatisticsLogForMutilFields.StatisticsKeys.COVER_INSTALL_PERMISSION_RESULT_DIALOG_CONFIRM_BTN_CLICK else StatisticsLogForMutilFields.StatisticsKeys.FIRST_INSTALL_PERMISSION_RESULT_DIALOG_CONFIRM_BTN_CLICK,
        mType - 1
    )
}

/**
 * 抽取自com.dubox.drive.permission.view.PermissionDialogActivity.onResultDialogCancelClicked
 */
@RRoute(path = "stat/on/result/dialog/cancel/clicked")
fun statOnResultDialogCancelClicked(mType: Int) {
    statisticsDialog(
        if (AppInfoUtils.isCoverInstall()) StatisticsLogForMutilFields.StatisticsKeys.COVER_INSTALL_PERMISSION_RESULT_DIALOG_CANCEL_BTN_CLICK else StatisticsLogForMutilFields.StatisticsKeys.FIRST_INSTALL_PERMISSION_RESULT_DIALOG_CANCEL_BTN_CLICK,
        mType - 1
    )
}

/**
 * 抽取自com.dubox.drive.permission.view.PermissionDialogActivity.showPermissionResultDialog
 */
@RRoute(path = "stat/show/permission/result/dialog")
fun statShowPermissionResultDialog(mType: Int) {
    statisticsDialog(
        if (AppInfoUtils.isCoverInstall()) StatisticsLogForMutilFields.StatisticsKeys.COVER_INSTALL_PERMISSION_RESULT_DIALOG_SHOW else StatisticsLogForMutilFields.StatisticsKeys.FIRST_INSTALL_PERMISSION_RESULT_DIALOG_SHOW,
        mType - 1
    )
}

private fun statisticsDialog(key: String, index: Int) {
    val size = StatisticsLogForMutilFields.StatisticsKeys.PERMISSION_TYPE.size
    if (index >= 0 && index < size) {
        StatisticsLogForMutilFields.getInstance().updateCount(
            key, StatisticsLogForMutilFields.StatisticsKeys.PERMISSION_TYPE[index]
        )
    }
}

/**
 * 抽取自com.dubox.drive.permission.PermissionHelperOnM.isInBatteryOptimization
 */
@RRoute(path = "stat/success/add/doze/white/list")
fun statSuccessAddDozeWhiteList() {
    StatisticsLogForMutilFields.getInstance().updateCount(
        StatisticsLogForMutilFields.StatisticsKeys.DUBOX_SUCCESS_ADD_DOZE_WHITE_LIST
    )
}