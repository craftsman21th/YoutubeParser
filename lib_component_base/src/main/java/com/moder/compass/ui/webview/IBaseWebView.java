package com.moder.compass.ui.webview;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.webkit.WebView;

/**
 * @author dengjie09
 * @description: 组件化间使用其他组件frangment 通过抽取接口，并将接口下沉 实现不同组件fragment的共用
 * @date：2019-11-28 14:11
 */
public interface IBaseWebView {

    WebView getBaseWebView();

    void setFragmentArguments(@Nullable Bundle args);

}
