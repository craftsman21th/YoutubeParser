package com.moder.compass.transfer.transmitter.constant;

/**
 * 分享接口错误码
 * 
 * @author sunqi01
 * 
 */
public interface ShareLinkErrorCode {
    /**
     * shareId不存在
     */
    int SHARE_ID_NOT_EXIST = -7;

    /**
     * 文件被删除
     */
    int FILE_ID_NOT_EXIST = -3;

    /**
     * 接口失败
     */
    int INTERFACEFailed = 2;

    /**
     * 疑似病毒
     */
    int FILE_HAVE_VIRUS = -70;

    /**
     * 文件名反作弊
     */
    int FILE_NAME_INVALID = 108;

    /**
     * 文件反作弊
     */
    int FILE_INVALID = 115;

    /**
     * 频控反作弊
     */
    int FILE_SHARE_TIME_LIMIT = 110;

    /**
     * 总量超限
     */
    int SHARE_TIME_LIMIT = -10;

    /**
     * 不支持的区域
     */
    int NOT_SUPPORT_AREA = 443;
}
