package com.moder.compass.transfer.transmitter;

import android.net.Uri;
import android.text.TextUtils;

import com.moder.compass.base.storage.config.ConfigAlertText;
import com.dubox.drive.network.base.ServerResultHandler;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.moder.compass.transfer.transmitter.constant.OtherErrorCode.ERROR_JSON_ANALYSIS;

public class ErrorMessageHelper {

    private static final String TAG = "ErrorMessageHelper";
    public static final int NO_REDO_DEFAULT = -1;

    public static boolean checkPCSErrorNo(int errCode) {
        // 处理 [31041, 31045] 闭区间范围内的错误码。 代表passport出现问题，需要重新登录
        // 31047代表分散认证失败
        if ((errCode >= 31041 && errCode <= 31045) || errCode == 31047) {
            DuboxLog.d(TAG, "bduss is invalid errorcode=" + errCode);
            ServerResultHandler.sendMsg(ServerResultHandler.MESSAGE_BDUSS_INVALID_SHOWLOGIN, -1, 0);
            return true;
        } else if (errCode == ServerResultHandler.PCS_FILE_NOT_EXIST) {
            ServerResultHandler.sendMsg(ServerResultHandler.PCS_FILE_NOT_EXIST, -1, 0);
            return true;
        }
        return false;
    }

    /**
     * 检查是否server返回的错误码
     * 
     * @author yangqinghai
     * Created on 2013-1-9 下午05:51:55
     */
    public static boolean checkServerError(int errCode) {
        // 处理 [31001 , 31299 ] 闭区间范围内的错误码。 代表passport出现问题，需要重新登录
        return errCode >= 31001 && errCode <= 31299;
    }

    public static String readErrorMsg(InputStream is) throws IOException {
        if (is == null) {
            DuboxLog.e(TAG, "readErrorMsg is = null");
            return "";
        }
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sbContent = new StringBuilder();
        String str = bufReader.readLine();
        while (str != null) {
            sbContent.append(str);
            str = bufReader.readLine();
        }
        str = sbContent.toString();
        return str;
    }

    public static String readRequestId(String rawContent) {
        String requestId = "";
        try {
            JSONObject obj = new JSONObject(rawContent);
            if (obj.has("request_id")) {
                requestId = obj.getString("request_id");
            }
        } catch (JSONException e) {
            DuboxLog.e("ErrorMessageHelper", e.getMessage(), e);
        }
        return requestId;
    }

    public static String getExceptionStack(Exception e) {
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw); // NOSONAR
            try {
                sw.close();
            } catch (IOException e1) {
                DuboxLog.e(TAG, e1.getMessage(), e1);
            }
            pw.close();
            return sw.toString();
        }
        return "";
    }

    public static int readErrorCode(String rawContent) {
        try {
            JSONObject obj = new JSONObject(rawContent);
            if (obj.has("error_code")) {
                return obj.getInt("error_code");
            } else if (obj.has("errno")) {
                return obj.getInt("errno");
            }
        } catch (Exception e) {
            DuboxLog.e("ErrorMessageHelper", e.getMessage(), e);
        }
        return ERROR_JSON_ANALYSIS;
    }

    /**
     * 解析随接口下发，用于展示给用户的文案
     * @param rawContent 带解析的数据
     * @return 展示给用户的文案
     */
    public static String readForbiddenErrMsgForShow(String rawContent) {
        String result = null;
        ConfigAlertText config = new ConfigAlertText("");
        if (config.isShowForbiddenAlert) {
            try {
                JSONObject obj = new JSONObject(rawContent);
                if (obj.has("error_info")) {
                    result = Uri.decode(obj.getString("error_info"));
                } else if (obj.has("errmsg")) {
                    result = Uri.decode(obj.getString("errmsg"));
                }
            } catch (Exception e) {
                DuboxLog.e("ErrorMessageHelper", e.getMessage(), e);
            } finally {
                if (TextUtils.isEmpty(result)) {
                    result = config.forbiddenUserDownloadAlertText;
                }
            }
        }
        return result;
    }

    /**
     * 解析随接口下发端上需要重试的次数
     * @param rawContent 带解析的数据
     * @return -1 代表服务端没有重试需求，是否重试需要端上定义。n>=0标识需要按照服务端要求重试n次
     */

    public static int readRedoCount(String rawContent) {
        int result = NO_REDO_DEFAULT;
        try {
            JSONObject obj = new JSONObject(rawContent);
            if (obj.has("redo")) {
                result = obj.getInt("redo");
            }
        } catch (Exception e) {
            DuboxLog.e("ErrorMessageHelper", e.getMessage(), e);
        }
        return result;
    }
}
