package com.moder.compass.transfer.transmitter;

/**
 * PCS传输接口的错误码
 * 
 * @author 孙奇 <br/>
 *         create at 2013-7-1 下午02:59:33
 */
public interface PCSTransmitErrorCode {
    /**
     * 文件不存在
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:08:18
     */
    int OBJECT_NOT_EXISTS = 31202;
    /**
     * 文件不存在
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:08:39
     */
    int FILE_DOES_NOT_EXISTS = 31066;
    /**
     * 用户不存在
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:08:49
     */
    int USER_NOT_EXISTS = 31045;

    /**
     * Stoken分散认证不通过
     */
    int STOKEN_ERROR = 31047;

    /**
     * 用户未授权
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:08:57
     */
    int USER_IS_NOT_AUTHORIZED = 31044;
    /**
     * 用户未登录
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:09:09
     */
    int USER_IS_NOT_LOGIN = 31042;
    /**
     * Bduss非法
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:10:04
     */
    int BDUSS_IS_INVALID = 31041;
    /**
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:10:18
     */
    int DIGEST_NOT_MATCH = 31327;
    /**
     * 文件没有权限访问
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-5 下午05:10:28
     */
    int FILE_IS_NOT_AUTHORIZED = 31064;

    /**
     * 文件为非法资源
     */
    int FILE_IS_ILLEGAL = 31390;

    /**
     * m3u8转码中
     * 
     * @since 7.8 2015-3-4
     * @author libin09
     */
    int M3U8_IN_TRANSCODING = 31341;

    /**
     * 文件不完整（该类文件一般为首尾两端正常，中间全部为0占位）
     *
     * @since 7.12 2015-12-30
     * @author liuliangping
     */
    int REMOTE_PCS_FILE_IS_IMPERFECT = 31244;

    /**
     * 文件不完整（该类文件一般为首尾两端正常，中间全部为0占位）
     *
     * @since 7.12 2016-1-5
     * @author liuliangping
     */
    int REMOTE_POMS_FILE_IS_IMPERFECT = 31844;

    /**
     * 服务器资源不可用
     *
     * @since 8.3
     */
    int SERVER_TEMP_INVALID = 31021;

    /**
     * 请求中有非法字段，比如referer非法，ua非法
     * @since 8.6
     */
    int SERVER_FORBIDDEN_INVALID_KEY = 31326;

    /**
     * 封禁帐号的情况下返回31426
     * @since 8.6
     */
    int SERVER_FORBIDDEN_USER = 31426;
}
