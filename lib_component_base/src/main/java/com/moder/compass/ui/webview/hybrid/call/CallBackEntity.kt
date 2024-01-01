package com.moder.compass.ui.webview.hybrid.call;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 回调
 */
class CallBackEntity(private val callbackId: String, private val code: Int, private val result: JSONObject?,
                     private val msg: String? = null) :
    ICallEntity {
    override fun getRequestString(): String? {
        val json1 = result ?: JSONObject()
        val json2 = JSONObject()
        try {
            json1.put("code",code)
            json1.put("errmsg",msg)
            json2.put("callbackId", callbackId)
            json2.put("data", json1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json2.toString()
    }
}