package com.moder.compass.network.request

import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.moder.compass.ActivityLifecycleManager
import com.moder.compass.BaseApplication
import com.moder.compass.account.Account
import com.dubox.drive.kernel.Constants
import com.dubox.drive.kernel.Constants.APP_LANGUAGE
import com.dubox.drive.base.network.NetworkUtil
import com.moder.compass.base.utils.GlobalConfigKey
import com.dubox.drive.kernel.util.getSimCarrierInfo
import com.moder.compass.business.kernel.HostURLManager
import com.dubox.drive.kernel.android.util.RealTimeUtil
import com.dubox.drive.kernel.android.util.network.ConnectivityState
import com.dubox.drive.kernel.architecture.AppCommon
import com.dubox.drive.kernel.architecture.config.GlobalConfig
import com.dubox.drive.kernel.architecture.net.RequestCommonParams
import com.dubox.drive.kernel.i18n.getDuboxLanguageCountry
import com.moder.compass.network.network.R
import com.dubox.drive.security.URLHandler
import com.mars.kotlin.extension.e
import com.mars.united.core.util.addParams
import okhttp3.HttpUrl

/**
 * Created by yeliangliang on 2021/3/18
 */

private val urlHandler = URLHandler()
/**
 * 构造参数
 * */
fun HttpUrl.Builder.addCommonQueryParameters(nduss: String, uid: String) {
    addQueryParameter("devuid", AppCommon.DEVUID)
    addQueryParameter("cuid", AppCommon.DEVUID)
    addQueryParameter("clienttype", RequestCommonParams.getClientType())
    addQueryParameter("channel", RequestCommonParams.getChannel())
    addQueryParameter("app_id", RequestCommonParams.getAppId())
    addQueryParameter("app_name", "xobovap".reversed())
    addQueryParameter("version", AppCommon.VERSION_DEFINED)
    addQueryParameter("logid", RequestCommonParams.getLogId())
    addQueryParameter("network_type", ConnectivityState.getNetWorkType(BaseApplication.getInstance()))
    addQueryParameter("apn_id", NetworkUtil.getCurrentNetworkAPN())
    addQueryParameter(APP_LANGUAGE, getDuboxLanguageCountry())
    addQueryParameter("carrier", getSimCarrierInfo(BaseApplication.getInstance()))
    addQueryParameter("startDevTime", System.currentTimeMillis().toString())
    addQueryParameter("versioncode", AppCommon.VERSION_CODE.toString())
    if (RequestCommonParams.isVip()) {
        addQueryParameter("isVip", "1")
    } else {
        addQueryParameter("isVip", "0")
    }
    if (ActivityLifecycleManager.isDuboxForeground()) {
        addQueryParameter("bgstatus", "0")
    } else {
        addQueryParameter("bgstatus", "1")
    }
    if (ProcessLifecycleOwner.get().lifecycle.currentState == Lifecycle.State.CREATED) {
        addQueryParameter("activestatus", "5")
    } else {
        addQueryParameter("activestatus", "0")
    }
    if (AppCommon.FIRST_LAUNCH_TIME > 0) {
        addQueryParameter("firstlaunchtime", AppCommon.FIRST_LAUNCH_TIME.toString())
    }
    // 添加用户来源参数
    addQueryParameter(Constants.AF_MEDIA_SOURCE, RequestCommonParams.getAppInstallMediaSource())

    val time = RealTimeUtil.getTime().toString()
    addQueryParameter("time", time)
    val urlWithRand = urlHandler.handlerURL(BaseApplication.getInstance(), build().url().toString(), nduss, uid)
    val rand = try {
        Uri.parse(urlWithRand).getQueryParameter("rand")
    } catch (e: Exception) {
        e.e()
        null
    }
    addQueryParameter("rand", rand)
}

/**
 * 转换手机品牌
 * */
fun generateRand(time: String): String? {
    val nduss = Account.nduss
    val uid = Account.uid
    val url = HostURLManager.domainWithHttps
    val isVip = if (RequestCommonParams.isVip()) "1" else "0"
    val paramStr = "devuid=${AppCommon.DEVUID}&cuid=${AppCommon.DEVUID}&isVip=${isVip}&clienttype=${RequestCommonParams.getClientType()}" +
        "&channel=${RequestCommonParams.getChannel()}&version=${AppCommon.VERSION_DEFINED}" +
        "&logid=${RequestCommonParams.getLogId()}&network_type=${ConnectivityState.getNetWorkType(BaseApplication.getInstance())}" +
        "&apn_id=${NetworkUtil.getCurrentNetworkAPN()}&${APP_LANGUAGE}=${getDuboxLanguageCountry()}&carrier=${getSimCarrierInfo(
            BaseApplication.getInstance())}" +
        "&time=${time}&${Constants.AF_MEDIA_SOURCE}=${RequestCommonParams.getAppInstallMediaSource()}" +
            "&${AppCommon.COMMON_PARAM_APP_ID}=${AppCommon.getSecondBoxPcsAppId()}" +
            "&app_name=${"xobovap".reversed()}" +
            "&versioncode=${AppCommon.VERSION_CODE}"
    val urlWithRand = urlHandler.handlerURL(BaseApplication.getInstance(), url.addParams(paramStr), nduss, uid)
    return try {
        Uri.parse(urlWithRand).getQueryParameter("rand")
    } catch (e: Exception) {
        e.e()
        null
    }
}

/**
 * 针对 passport 的公参处理
 */
fun HttpUrl.Builder.addPassportParameters() {
    addQueryParameter("client", "android")
    addQueryParameter("pass_version", "2.0")
    addQueryParameter("devuid", AppCommon.DEVUID)
    addQueryParameter("psign", GlobalConfig.getInstance().getString(GlobalConfigKey.SERVER_PASSPORT_PSIGN))
}