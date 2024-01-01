package com.moder.compass.transfer;

/**
 * 传输模块，关于文件名的常量字符串
 *
 * Created by liuliangping on 2015/9/1.
 */
public final class TransferFileNameConstant {
    /**
     * target30一下，原生下载创建临时文件下载后缀
     */
    public static final String DOWNLOAD_SUFFIX = ".!bn";
    /**
     * target30一下，p2p下载临时文件后缀
     */
    public static final String P2P_DOWNLOAD_SUFFIX = ".dubox.p.downloading";
    public static final String DOWNLOAD_M3U8_SUFFIX = ".m3u8bn";
    public static final String VIDEO_M3U8_SUFFIX = ".m3u8";
    public static final String TRANSMIT_CONFIG = ".cfg";
    public static final String BACKUP_FILE_NAME = "backup_%s";
    public static final String BACKUP_INDEX_FILE_NAME = "backup(%d)_%s";
    public static final String BACKUP_OLD_FILE_NAME = "backup_";

}
