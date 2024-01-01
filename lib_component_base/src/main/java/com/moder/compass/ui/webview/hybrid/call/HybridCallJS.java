package com.moder.compass.ui.webview.hybrid.call;

import android.annotation.TargetApi;
import android.os.Build;
import androidx.annotation.NonNull;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * Created by liji01 on 17-10-16.
 */

public class HybridCallJS {
    private static final String TAG = "HybridCallJS";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void hybridNativeCallback(@NonNull WebView webView, @NonNull ICallEntity entity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String script =
                    "JSBridge.hybridCallback(" + "'"
                            + entity.getRequestString()
                            + "'" + ")";
            webView.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    DuboxLog.d(TAG, value);
                }
            });
            DuboxLog.d(TAG, "script: " + script);
        } else {
            String script = "javascript:JSBridge.hybridCallback(" + "'"
                    + entity.getRequestString()
                    + "'" + ")";
            webView.loadUrl(script);
            DuboxLog.d(TAG, "script: " + script);
        }
    }
}
