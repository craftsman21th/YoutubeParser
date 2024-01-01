package com.moder.compass.ui.webview.hybrid.call;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 端调用h5
 *
 * @author lijunnian
 */
public class CallH5Entity implements ICallEntity {

    public String responseName;
    public int code;
    public String msg;
    public JSONObject result;

    public CallH5Entity(String responseName, int code, String msg, JSONObject result) {
        this.responseName = responseName;
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    @Override
    public String getRequestString() {
        JSONObject json1 = new JSONObject();
        JSONObject json2 = new JSONObject();
        try {
            json2.put("code", code);
            json2.put("msg", msg);
            json2.put("result", result);
            json1.put("responseName", responseName);
            json1.put("responseData", json2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json1.toString();
    }
}
