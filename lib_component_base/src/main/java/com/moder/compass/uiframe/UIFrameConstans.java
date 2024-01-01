package com.moder.compass.uiframe;

/**
 * ui框架常量类
 *
 * @author wangyang34
 * @since 2019/4/22
 */
public class UIFrameConstans {

    /** tag */
    public static final String TAG = "uiframe";

    /** 视频预览-选集 */
    public static final String CONTAINER_LIST_VIDEO_PLAY_SELECT = "container_video_play_select";

    /**
     * 选集页发送的play广播Action
     */
    public static final String ACTION_VIDEO_SERVICE_PLAY = "com.dubox.drive.action.ACTION_VIDEO_SERVICE_PLAY";

    /**
     * 选集页发送的play广播数据
     */
    public static final String DATA_VIDEO_SERVICE_PLAY = "DATA_VIDEO_SERVICE_PLAY";

    /**
     * 播放页发送的视频切换广播Action
     */
    public static final String ACTION_VIDEO_SERVICE_CHANGE_PLAY
            = "com.dubox.drive.action.ACTION_VIDEO_SERVICE_CHANGE_PLAY";

    /**
     * 播放页发送的视频切换广播数据
     */
    public static final String DATA_VIDEO_SERVICE_CHANGE_PLAY = "DATA_VIDEO_SERVICE_CHANGE_PLAY";

    /**
     * 播放页发送的视频切换广播, 发送播放离线文件数据
     */
    public static final String DATA_VIDEO_SERVICE_CHANGE_LOCAL_URL =
            "data_video_service_change_local_url";

}
