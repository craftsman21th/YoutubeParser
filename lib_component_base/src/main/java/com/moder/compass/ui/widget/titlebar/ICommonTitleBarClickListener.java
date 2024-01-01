package com.moder.compass.ui.widget.titlebar;

import android.view.View;

/**
 * 左边back右侧按钮的title回调 com.dubox.drive.ui.widget.titlebar.ICommonTitleBarClickListener
 * 
 * @author panwei <br/>
 *         create at 2013-8-1 下午5:40:00
 */
public interface ICommonTitleBarClickListener {

    // 右侧按钮回调
    void onRightButtonClicked(View view);

    // back按钮回调
    void onBackButtonClicked();
}
