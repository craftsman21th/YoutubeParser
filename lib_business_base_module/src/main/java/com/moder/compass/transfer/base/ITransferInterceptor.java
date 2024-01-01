package com.moder.compass.transfer.base;

public interface ITransferInterceptor {
    int STATUS_PASS = 0;
    int STATUS_FAIL = 1;
    int STATUS_INVALIDE_SIZE = 100;
    // 非 vip 上传视频
    int STATUS_UPLOAD_VIDEO_NO_VIP = 200;

    void pass();

    void intercept(UploadInterceptorInfo info);
}
