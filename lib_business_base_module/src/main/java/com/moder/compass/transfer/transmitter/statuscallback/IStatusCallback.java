package com.moder.compass.transfer.transmitter.statuscallback;

/**
 * 传输器失败的回调
 *
 * @author sunqi01
 */
public interface IStatusCallback {

    int UPLOAD = 1;
    int BACKUP = 2;
    /**
     * 上传图片返回的数据
     */
    String UPLOAD_RESP_DATA = "upload_resp_data";

    /**
     * 失败的回调
     *
     * @param errCode 失败错误码
     * @param extraInfo 失败原因额外信息
     */
    void onFailed(int errCode, String extraInfo);

    /**
     * 传输成功
     *
     */
    void onSuccess(String content);

    /**
     * 保存进度等信息
     *
     * @param size
     * @param rate
     */
    int onUpdate(long size, long rate);
}
