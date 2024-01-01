package com.moder.compass.util

import android.annotation.SuppressLint
import android.content.Context
import com.moder.compass.remoteconfig.StoreAuditAdaptationConfig

/**
 * @Author:         lcl
 * @CreateDate:     2023/3/8 15:57
 * @UpdateDate:     2023/3/8 15:57
 * @Version:        1.0
 * @Description:
 *  针对应用商店审核的适配检查工具
 */
class StoreAuditAdaptationUtil private constructor() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instan: StoreAuditAdaptationUtil

        /**
         * 初始化,提供上下文和渠道号
         */
        fun init(context: Context, channelNo: String) {
            if (this::instan.isInitialized) {
                if (channelNo.isNotEmpty()) {
                    instan.channelNo = channelNo
                }
                return //只有第一次生效
            }
            instan = StoreAuditAdaptationUtil()
            instan.channelNo = channelNo
            instan.context = context
        }

        /**
         * 获取单利对象
         */
        fun getIns(): StoreAuditAdaptationUtil {
            if (!this::instan.isInitialized) {
                synchronized(StoreAuditAdaptationUtil::class) {
                    instan = StoreAuditAdaptationUtil()
                }
            }
            return instan
        }
    }

    private lateinit var context: Context
    private var channelNo: String = ""
    private var cacheRemotConfig: MutableList<StoreAuditAdaptationConfig>? = null

    //强制更新的计数，防止保活等特殊情况一致生效不是导致无法更新
    private var forceUpdateCount = 0L

    // 强制更新间隔次数。达到间隔次数强制更新一次
    private var forceUpdateStep = 50

    /**
     * 当前渠道号是否正在审核中
     * @return
     *  T:正在审核中
     *  F:没有在审核中
     */
    fun checkCurrentChannelNoIsUnderReview(): Boolean {
        forceUpdateCount++
        try {
            if (cacheRemotConfig == null || cacheRemotConfig!!.isEmpty() || forceUpdateCount % forceUpdateStep == 0L) {
                //注：防止后续数据更新造成大规模延迟。万一保活生效一致不死配置一直不更新的情况，间隔指定次数后强制更新一次
                cacheRemotConfig = getStoreAuditAdaptationConfig()
            }
            if (cacheRemotConfig == null || cacheRemotConfig!!.isEmpty()) {
                return false
            }
            cacheRemotConfig?.forEach {storeAuditAdaptationConfig ->
                if (storeAuditAdaptationConfig.channelNo == channelNo && storeAuditAdaptationConfig.isUnderReview) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }
}