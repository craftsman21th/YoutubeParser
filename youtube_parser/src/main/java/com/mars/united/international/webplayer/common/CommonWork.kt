package com.mars.united.international.webplayer.common

import com.mars.united.international.webplayer.account.repo.AccountRepo
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.utils.asString
import com.mars.united.international.webplayer.parser.utils.executeJs
import com.mars.united.international.webplayer.parser.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

/**
 * @Author 陈剑锋
 * @Date 2023/9/22-11:02
 * @Desc 公共任务
 */
class CommonWork {

    private val channelPageTargetJsStartReg: Regex = """var ytInitialData.*=""".toRegex()
    private val channelPageTargetJsEndReg: Regex = """</script""".toRegex()

    /**
     * 获取 订阅相关 参数
     * @param ownerProfileUrl String
     * @return JsonCaller?
     */
    suspend fun getYtInitialData(ownerProfileUrl: String): JsonCaller? = withContext(Dispatchers.IO) {
        val commonRepo = CommonRepo.get()
        val accountRepo = AccountRepo.get()
        kotlin.runCatching {
            val pageRequest: Request = Request.Builder()
                // 必须加User-Agent header，否则拉下来的html里的ytInitialData不包含videos/shorts第一页请求需要的continuation token
                .headers(getCommonHeaders().add(accountRepo.getLoginHeaders()))
                .url(ownerProfileUrl)
                .get()
                .build()
            // 修复OkHttp SDK异常，有时候body()返回空
            var body = ""
            while (body.isBlank()) {
                body = kotlin.runCatching {
                    commonHttpClient.newCall(pageRequest).execute().body()?.string()
                }.getOrNull() ?: ""
            }
            val jsCode = with(body) {
                // 获取 关键js代码的 起始位置
                val startTag = channelPageTargetJsStartReg.find(this)?.value ?: ""
                val startIndex = indexOf(startTag)

                // 获取 关键js代码的 结束位置
                val endTag = channelPageTargetJsEndReg.find(this, startIndex)?.value ?: ""
                val endIndex = indexOf(endTag, startIndex)

                substring(startIndex, endIndex)
            }
            val subscribeInfo =
                JsonCaller.create(executeJs("$jsCode;if(typeof ytInitialData === 'string'){ytInitialData=JSON.parse(ytInitialData)};JSON.stringify(ytInitialData)"))
            val subscribeKey = subscribeInfo
                .get("header").get("c4TabbedHeaderRenderer").get("subscribeButton").get("subscribeButtonRenderer")
                .get("onSubscribeEndpoints")[0].get("subscribeEndpoint").get("params").asString ?: ""
            val unsubscribeKey = subscribeInfo
                .get("header").get("c4TabbedHeaderRenderer").get("subscribeButton").get("subscribeButtonRenderer")
                .get("onUnsubscribeEndpoints")[0].get("signalServiceEndpoint").get("actions")[0].get("openPopupAction")
                .get("popup").get("confirmDialogRenderer").get("confirmButton").get("buttonRenderer")
                .get("serviceEndpoint")
                .get("unsubscribeEndpoint").get("params").asString ?: ""
            if (subscribeKey.isNotBlank()) {
                commonRepo.subscribeKey = subscribeKey
            }
            if (unsubscribeKey.isNotBlank()) {
                commonRepo.unsubscribeKey = unsubscribeKey
            }
            return@withContext subscribeInfo
        }
        return@withContext null
    }

}