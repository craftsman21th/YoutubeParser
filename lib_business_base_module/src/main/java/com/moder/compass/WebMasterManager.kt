package com.moder.compass

import com.moder.compass.account.Account
import com.dubox.drive.basemodule.BuildConfig
import com.moder.compass.util.getEnableShareEarnInfoConfig

/**
 * 站长信息管理类
 */
object WebMasterManager {
    fun needShowEarnPlanInShare(): Boolean {
        val info = Account.webMasterInfo
        val planInfo = info.planInfo
        if (!info.isWebmaster || planInfo == null) return false

        val config = getEnableShareEarnInfoConfig()
        return config.enableChannel.contains(BuildConfig.CHANNEL_NO)
                && config.enableCountry.contains(info.registerCountry)
    }
}