package com.moder.compass.ui.preview.video.source;

import java.util.List;

import com.dubox.drive.cloudfile.io.model.CloudFile;

import android.database.Cursor;
import android.net.Uri;
/**
 * 音频播放数据封装类
 * Created by guoqiiqn on 18/7/16.
 */

public class MediaSourceInfo {
    /**  数据来源类型      云文件 */
    public static  final int   SOURCE_TYPE_CLOUDFILE = 0;
    /**  数据来源类型      云文件 多个*/
    public static  final int   SOURCE_TYPE_CLOUDFILE_MULTI = 1;
    /**  数据来源类型     上传下载 */
    public static  final int   SOURCE_TYPE_TRANSMIT = 2;

    /**  数据来源类型     wap  */
    public static  final int   SOURCE_TYPE_WAP = 3;
    /**  数据来源类型     云端点对点 多个 */
    public static  final int   SOURCE_TYPE_CLOUDP2P_MULTI = 5;
    /**  数据来源类型     云端文件  */
    public static  final int   SOURCE_TYPE_SERVICE = 6;
    /**  数据来源类型     本地视频  */
    public static  final int   SOURCE_TYPE_LOCAL = 9;
    /**  数据来源类型     外链视频  */
    public static  final int   SOURCE_TYPE_DLINK = 10;
    /**  数据来源类型     本地视频 (无申诉功能)  */
    public static  final int   SOURCE_TYPE_LOCAL_WITHOUT_APPEAL = 11;

    /**  数据来源类型     本地视频(没有上传，备份过的本地视频，主要为相册中的本地视频使用)
     *  (只保留详情，锁屏，横竖屏切换功能)
     */
    public static  final int   SOURCE_TYPE_LOCAL_VIDEO_FROM_PHONE = 14;
    /**
     * 第三方视频需要在播放器UI做特殊处理
     */
    public static  final int   SOURCE_TYPE_LOCAL_VIDEO_FROM_PHONE_TP = 15;

    /**  数据来源类型     自动备份 */
    public static  final int   SOURCE_TYPE_BACKUP = 12;

    /**  数据来源类型     视频服务 */
    public static  final int   SOURCE_TYPE_VIDEO_SERVICE = 13;
    /**
     * 来源离线文件
     */
    public static  final int SOURCE_TYPE_LOCAL_VIDEO_FROM_OFFLINE = 19;

    /**
     * 数据来源类型 雷达
     */
    public static final int SOURCE_TYPE_RADAR_RESOURCE = 20;

    /**
     * 数据来源类型 外链页已保存文件
     */
    public static final int SOURCE_TYPE_SHARE_LINK_CLOUD_FILE = 21;
    /**
     * 数据来源类型 外链页自动or手动保存自动播放的视频
     */
    public static final int SOURCE_TYPE_SHARE_LINK_SAVE_AUTO_PLAY = 22;

    /** 多媒体文件类型 视频   */
    public static final int MEDIA_TYPE_VIDEO = 0;
    /** 多媒体文件类型 音频   */
    public static final int MEDIA_TYPE_AUDIO = 1;
    /**  播放索引，指向默认播放的音频文件  */
    public int mMediaIndex = 0;
    /**  播放数据 数据库    */
    public Cursor mCursor;
    /**  数据来源类型  分数据库和缓存   */
    public int mSourceType = SOURCE_TYPE_CLOUDFILE;

    /**
     * 统计播放来源，仅供打点使用
     */
    public String from = "other";
    /**
     * 额外参数，外链链接等
     */
    public String extraParams = "";
    /** 云端点对点分享信息 type    */
    public int type = 0;
    /** feed uk    */
    public String uk;
    /** feed shareId    */
    public String shareId;
    /** feed albumId    */
    public Long duration;
    /** feed resolution    */
    public String resolution;
    /** feed seKey    */
    public String seKey;
    /** 云文件的 FSID，用于上报端行为到最近使用服务 */
    public String fsid;
    /** 本地文件的地址，用于查询云文件信息 */
    public String localUrl;
    /** 标识来自上传、下载列表中的文件传输类型，如 M3U8、P2P 等，用于查询云文件信息 */
    public String transferType;

    /** 同一目录下的文件列表 */
    public List<CloudFile> sameDirFiles;

    /**
     * 打开的时候是否横屏展示
     */
    public boolean needShowLandscape = false;

    /**
     * @since Terabox 2.13.0
     * 现在只在音频播放中用到，用于对比是否切换播放源
     */
    public String defaultPath;

    /**
     * 本地 uri 播放列表
     */
    public List<Uri> localUris;

}
