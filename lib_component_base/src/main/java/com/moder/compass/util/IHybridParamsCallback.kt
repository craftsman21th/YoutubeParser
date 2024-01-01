package com.moder.compass.util

import org.json.JSONObject

/**
 * @author sunmeng
 * create at 2021-11-12
 * Email: sunmeng12@baidu.com
 *
 * 通过接口的方式处理 Hybrid 协议的回调
 */
interface IHybridParamsCallback {

    /**
     * 获取到 Hybrid 的 Params 时，转换成 JSONObject
     */
    fun onParamsCallback(paramsJson: JSONObject)
}