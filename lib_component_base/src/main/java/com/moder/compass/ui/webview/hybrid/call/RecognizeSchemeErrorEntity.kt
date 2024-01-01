package com.moder.compass.ui.webview.hybrid.call;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 识别 Scheme 错误
 */
class RecognizeSchemeErrorEntity(private val code: Int, private val callbackId: String, private val apiName: String,
                                 private val func: String, private val errmsg: String?) : ICallEntity {
    override fun getRequestString(): String? {
        val json2 = JSONObject()
        val json1 = JSONObject()
        try {
            json1.put("code", code)
            json1.put("apiName", apiName)
            json1.put("func", func)
            json1.put("errmsg", errmsg)
            json2.put("callbackId", callbackId)
            json2.put("data", json1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json2.toString()
    }

}
