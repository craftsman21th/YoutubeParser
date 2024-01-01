package com.moder.compass.ui.view;

import android.content.Context;

/**
 * MVLP模式中View的接口
 * 
 * @author 孙奇 <br/>
 *         create at 2013-4-19 上午11:07:06
 */
public interface IView {
    /**
     * 获取context
     * 
     * @return
     */
    Context getContext();

    /**
     * 显示成功
     * 
     * @param successMsg
     * @author 孙奇 V 1.0.0 Create at 2013-5-17 下午06:29:47
     */
    void showSuccess(String successMsg);

    /**
     * 显示成功
     * 
     * @param successCode
     * @author 孙奇 V 1.0.0 Create at 2013-5-17 下午06:30:49
     */
    void showSuccess(int successCode);

    /**
     * 显示错误信息
     *
     * @param errorMessage
     * @author 孙奇 V 1.0.0 Create at 2013-5-17 下午05:07:30
     */
    void showError(String errorMessage);

    /**
     * 显示错误信息
     *
     * @param errorCode
     * @author 孙奇 V 1.0.0 Create at 2013-5-17 下午05:08:24
     */
    void showError(int errorCode);

    /**
     * 显示错误信息
     *
     * @param errorCode
     * @param errorMessage
     */
    void showError(int errorCode, String errorMessage);

    /**
     * 开始progress
     * 
     * @param progressCode
     * @author 孙奇 V 1.0.0 Create at 2013-5-26 下午05:35:05
     */
    void startProgress(int progressCode);

    /**
     * 停止Progress
     * 
     * @param progressCode
     * @author 孙奇 V 1.0.0 Create at 2013-5-26 下午05:38:14
     */
    void stopProgress(int progressCode);

    /**
     * View是否正在销毁
     * 
     * @return
     * @author 孙奇 V 1.0.0 Create at 2013-5-4 下午05:09:19
     */
    boolean isDestroying();
}
