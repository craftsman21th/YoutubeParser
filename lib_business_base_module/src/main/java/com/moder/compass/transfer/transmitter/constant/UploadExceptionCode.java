package com.moder.compass.transfer.transmitter.constant;

/**
 * @author sunqi01
 */
public interface UploadExceptionCode {
    int LOCAL_FILE_ERROR = 2000;

    int NO_REMOTE_SPACE = 2001;

    int UPLOAD_BY_OTHER_APP = 2002;

    int LOW_POWER = 2003;

    int LOCAL_FILE_CHANGE = 2004;

    /**
     * 本地文件不完整
     *
     * @author liuliangping V 7.12 Create at 2015-11-20 下午4:37:05
     */
    int LOCAL_FILE_IS_IMPERFECT = 2006;

    /**
     * 本上传功能被封禁
     *
     * @author cuizhe01
     */
    int SERVER_BAN = 2007;

    /**
     * 文件名非法
     */
    int FILE_NAME_ILLEGAL = 2008;

    /**
     * 参数错误
     */
    int FILE_PARAMETER_ERROR = 2009;

    /**
     * 文件太多，超过500w
     */
    int FILE_MORE_NUMBER = 2010;

    /**
     * 文件大小超限
     */
    int FILE_SIZE_LIMIT = 2011;

    /**
     * 隐藏空间专用容量不足
     */
    int SAFE_BOX_SIZE_LIMIT = 2012;


    /**
     * 文件上传重试错误
     * @since 2.18
     */
    int FILE_PCS_UPLOAD_RETRY_ERROR = 2015;

}
