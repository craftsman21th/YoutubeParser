package com.moder.compass.log;

/**
 * Created by liuliangping on 2016/3/20.
 */
public interface ILogField {
    /**
     * 开始时间
     * 
     * @return
     */
    long getStartTime();

    /**
     * 结束时间
     * 
     * @return
     */
    long getEndTime();

    /**
     * 传输的字节数
     *
     */
    long getTransferSize();

    /**
     * 错误码
     *
     */
    int getPcsErrorCode();

    /**
     * 错误码
     *
     */
    int getHttpErrorCode();

    /**
     * 错误码
     *
     */
    int getOtherErrorCode();

    /**
     * 请求的url
     *
     */
    String getRequestUrl();

    /**
     * 完成原因，如：成功，失败，暂停
     * @return
     */
    int getFinishStates();

    /**
     * 分隔符
     */
    String getFieldSeparator();

    /**
     * client ip地址
     * 
     * @return
     */
    String getClientIp();

    /**
     * 清除
     */
    void clear();

    /**
     * 获取uid
     * 
     * @return
     */
    String getUid();

    /**
     * 错误信息
     * @return
     */
    String getOtherErrorMessage();
}
