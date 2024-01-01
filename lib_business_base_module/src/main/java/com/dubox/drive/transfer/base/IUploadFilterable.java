package com.dubox.drive.transfer.base;

import com.dubox.drive.kernel.util.RFile;

/**
 * Created by libin09 on 2015/2/6.上传过滤器
 */
public interface IUploadFilterable {
    /**
     * 过滤
     */
    boolean filter(RFile fileMeta);

    /**
     * 提示
     */
    void showTips();

    /**
     * 是否展示会员引导
     */
    boolean isShowUploadVipGuide(RFile fileMeta);

    /**
     * 获取大文件上传，视频上传等拦截码
     */
    int getInterceptCode();

    /**
     * 设置是否拦截视频上传，主要用于第三方上传时，同时存在视频和其他类型文件，要求不进行拦截
     */
    void setInterceptVideoUploadEnable(Boolean enable);
}
