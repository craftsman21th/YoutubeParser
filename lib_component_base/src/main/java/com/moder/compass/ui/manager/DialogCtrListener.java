package com.moder.compass.ui.manager;

/**
 * 对话框按钮的监听器，提供了对确定和取消按钮点击事件的监听</p> com.dubox.drive.ui.manager.DialogCtrListener
 * 
 * @author chenyuquan <br/>
 *         create at 2013-4-27 上午11:59:59
 */
public interface DialogCtrListener {

    /**
     * 点击对话框的确定按钮后的回调方法
     */
    void onOkBtnClick();

    /**
     * 点击对话框的取消按钮后的回调方法
     */
    void onCancelBtnClick();

}
