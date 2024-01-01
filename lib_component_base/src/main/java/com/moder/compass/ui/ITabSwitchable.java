package com.moder.compass.ui;

/**
 * Created by libin on 2017/12/14.切换Tab相关声明
 */

public interface ITabSwitchable {
    /**
     * 要切到tab的id
     */
    interface Extra {
        /**
         * 用来传递从通知栏进入时，需要切换的tab
         */
        String TAB_TAG = "TAB_TAG";

    }

    interface TabTag {
        String TAB_HOME_CARD = "TAB_HOME_CARD";
        String TAB_FILE = "TAB_FILE";
        String TAB_TIMELINE = "TAB_TIMELINE";
        String TAB_VIDEO = "TAB_VIDEO";
        String TAB_SHARE = "TAB_SHARE";
        String TAB_GROUP = "TAB_GROUP";
        /**
         * 新增 下载tab
         */
        String TAB_DOWNLOAD = "TAB_DOWNLOAD";
        /**
         * 资源圈子tab-短视频tab
         */
        String TAB_SHORTS = "TAB_SHORTS";

        String TAB_EARN = "TAB_EARN";
    }

    /**
     * 传输列表tab
     */
    interface TransferTabId {

        /**
         * 下载TAB index
         */
        int INDEX_DOWNLOAD_TAB = 0;

        /**
         * 上传TAB index
         */
        int INDEX_UPLOAD_TAB = 1;

        /**
         * 离线TAB index
         */
        int INDEX_OFFLINE_TAB = 2;

        /**
         * 优先显示有待处理任务的TAB index
         */
        int INDEX_PROCESSING_TAB = -1;

    }

}
