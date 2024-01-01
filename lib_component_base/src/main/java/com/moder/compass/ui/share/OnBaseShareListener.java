package com.moder.compass.ui.share;

import android.content.Context;

/**
 * Simple to Introduction
 *
 * 分享功能的基类
 * 
 * @Author: guoqiqin
 * @CreateDate: 2019/09/04
 */
public interface OnBaseShareListener {

    /** 分享完成时的消息 */
    int SHARE_FINISHED_MESSAGE = 1091;

    /**
     * 分享成功的消息
     */
    int SHARE_RESULT_MESSAGE = 1092;

    /**
     * 分享来自首页
     **/
    int FROM_HOME = 0;
    /**
     * 分享来自图片预览
     **/
    int FROM_PIC = 1;
    /**
     * 分享来自视频预览
     **/
    int FROM_VIDEO = 2;
    /**
     * 分享来自资源圈
     **/
    int FROM_SHARE_RESOURCE = 3;

    /**
     * 分享来自音频播放
     */
    int FROM_AUDIO = 4;

    /**
     * 分享来自活动页面
     */
    int FROM_ACTIVITY = 5;

    /**
     * 分享来自资源小组
     */
    int FROM_RESOURCE_GROUP = 6;

    /**
     * 分享弹框是否正在显示
     * 
     * @return true 正在显示
     */
    boolean isShareDialogShowing();

    /**
     * 显示分享对话框
     */
    void showShareDialog();

    /**
     * 显示分享对话框
     * 
     * @param title
     */
    void showShareDialog(String title);

    /**
     * 按方向显示分享弹框
     *
     * @param title 标题
     * @param orientation 屏幕方向
     */
    void showShareDialog(String title, int orientation);

    /**
     * 上报分享结果
     * 
     * @param context
     * @param fromOther
     */
    void reportShareTaskStatus(Context context, boolean fromOther);

}
