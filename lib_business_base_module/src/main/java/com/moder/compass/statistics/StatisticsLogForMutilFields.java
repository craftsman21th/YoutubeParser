package com.moder.compass.statistics;

import java.util.List;

import com.dubox.drive.cloudfile.utils.FileType;
import com.moder.compass.stats.DuboxStatsEngine;
import com.moder.compass.stats.StatisticsType;
import com.moder.compass.stats.upload.Separator;

/**
 * 用于多关键字统计
 *
 * 单例模式
 *
 * com.dubox.drive.util.DuboxStatisticsLogForMutilFields
 *
 * @author xinxiaohui <br/>
 *         create at 2013-5-25 下午7:47:51
 */
public class StatisticsLogForMutilFields {
    /** 实例项 **/
    private static volatile StatisticsLogForMutilFields instance = null;

    public static StatisticsLogForMutilFields getInstance() {
        if (instance == null) {
            synchronized (StatisticsLogForMutilFields.class) {
                if (null == instance) {
                    instance = new StatisticsLogForMutilFields();
                }
            }
        }
        return instance;
    }

    /**
     * 构造函数
     *
     * 初始化统计文件
     */
    protected StatisticsLogForMutilFields() {
    }

    /**
     * 统计入口
     *
     * @param op
     * @param other
     */
    public void updateCount(String op, String...other) {
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.NEW).statCount(op, other);
    }

    public void updateMonitor(String body){
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.MONITOR).statCount(body);
    }

    /**
     * 统计立即上报-慎用
     *
     * @param op
     * @param other
     */
    public void updateCountNow(String op, String...other) {
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.NEW).statCount(op, other);
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.NEW).uploadWrapper();
    }

    /**
     * 统计入口
     *
     * @param op
     * @param ignorePV 统计报表不计算pv
     * @param other
     */
    public void updateCount(String op, boolean ignorePV, String...other) {
        if (ignorePV) {
            op += Separator.OP_PARAM_SPLIT + "type" + Separator.PARAM_EQUALS + "ignore";
        }
        updateCount(op, other);
    }

    /**
     * 统计入口
     *
     * @param count
     * @param op
     * @param other
     */
    public void updateCount(int count, String op, String...other) {
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.NEW).statCount(op, count, other);
    }

    /**
     *
     * @param key
     * @param addCount
     */
    public void updateCount(String key, int addCount) {
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.NEW).statCount(key, addCount, null);
    }

    /**
     * 根据文件名统计文件类型数据
     *
     * @param fileName
     * @param statKey
     */
    public void updateFileCategoryCount(String statKey, String fileName) {
        if (null == fileName || "".equals(fileName)) {
            return;
        }
        if (FileType.isMusic(fileName)) {
            updateCount(statKey, StatisticsKeys.FILE_TYPE_AUDIO);
        } else if (FileType.isDoc(fileName)) {
            updateCount(statKey, StatisticsKeys.FILE_TYPE_DOC);
        } else if (FileType.isVideo(fileName)) {
            updateCount(statKey, StatisticsKeys.FILE_TYPE_VIDEO);
        } else if (FileType.isImage(fileName)) {
            updateCount(statKey, StatisticsKeys.FILE_TYPE_IMAGE);
        } else if (FileType.isApp(fileName)) {
            updateCount(statKey, StatisticsKeys.FILE_TYPE_APP);
        } else if (FileType.isZipFile(fileName)) {
            updateCount(statKey, StatisticsKeys.FILE_TYPE_ZIP);
        } else {
            updateCount(statKey, StatisticsKeys.FILE_TYPE_OTHER);
        }
    }

    /**
     * 根据文件名统计文件类型数据
     *
     * @param fileNames
     * @param statKey
     */
    public void updateFileCategoryCount(String statKey, List<String> fileNames) {
        if (null == fileNames || 0 == fileNames.size()) {
            return;
        }
        int size = fileNames.size();
        for (int i = 0; i < size; i++) {
            if (null == fileNames.get(i) || "".equals(fileNames.get(i))) {
                continue;
            }
            updateFileCategoryCount(statKey, fileNames.get(i));
        }
    }

    /**
     * 统计相关的所有KEY声明 <br>
     * com.dubox.drive.util.StatisticsKeys
     *
     * @author 魏铮铮 <br/>
     *         create at 2012-11-28 下午5:04:45
     */
    public interface StatisticsKeys {


        /**
         * [10.0.150] 预览加载页展示的用户数和次数”uv\pv
         **/
        String DOCUMENT_FILE_PREVIEW_PAGE_SHOW = "document_file_preview_page_show";

        /**
         * [7.16] 除图片和视频外的预览日活比例
         */
        String NETDISK_PREVIEW_NOT_IMAGE_VIDEO_FILE_ACTIVE = "1001010110";
        /**
         * [4.1]点击多选原点的用户数/次数
         */
        String DUBOX_BTN_MUTIL_SELECTED_CLICK = "Dubox_Btn_Mutil_Selected_Click";

        /**
         * [4.1]点击上传列表用户数/次数 在传输列表tab点击
         */
        String DUBOX_TRANSFER_UPLOAD_LIST_CLICK = "Dubox_Transfer_Upload_List_Click";

        /**
         * [4.1]点击下载列表用户数/次数 在传输列表tab点击
         */
        String DUBOX_TRANSFER_DOWNLOAD_LIST_CLICK = "Dubox_Transfer_Download_List_Click";

        /**
         * [5.0]分类统计普通分享文件转存
         */
        String SHARE_CATEGORY_TRANSFER_CLICK = "Share_Category_Transfer_Click";
        /**
         * [5.0]分类统计个人主页下载
         */
        String PERSONALPAGE_CATEGORY_DOWNLOAD_CLICK = "PersonalPage_Category_Download_Click";
        /**
         * [5.0]所有转存的统计项
         */
        String DUBOX_CATEGORY_TRANSFER_CLICK = "Dubox_Category_Transfer_Click";

        /**
         * [5.2]每日从通知栏点击进入传输列表的次数
         */
        String NOTIFICATION_TO_TRANSFERTAB = "NOTIFICATION_TO_TRANSFERTAB";
        /**
         * [5.2]每日收到通知栏的已登录用户数
         */
        String NOTIFICATION_ALBUM_BACK_UP = "NOTIFICATION_ALBUM_BACK_UP";
        /**
         * [5.2]已登录用户数点击通知栏进入开启相册备份界面的用户数
         */
        String NOTIFICATION_TO_ALBUM_SETTING = "NOTIFICATION_TO_ALBUM_SETTING";

        /**
         * [5.2]每日收到通知栏的未登录用户数，Mtj统计//用MTJ
         */
        String MTJ_NOTIFICATION_ALBUM_BACK_UP = "Mtj_5_2_0_9";
        /**
         * [5.2]每日点击通知栏进入相册备份开启界面的用户数，Mtj统计
         */
        String MTJ_NOTIFICATION_TO_ALBUM_SETTINGS = "Mtj_5_2_0_10";
        /**
         * [5.2]未登录进入相册备份开启界面并点击开启，Mtj统计
         */
        String MTJ_ALBUM_TURN_ON = "Mtj_5_2_0_11";
        /**
         * [5.2]点击按文件名称排序的次数 每点击一次按文件名排序按钮，次数+1
         */
        String MYDUBOXACTIVITY_SORT_FILENAME_BUTTON_CLICK = "MYDUBOXACTIVITY_SORT_FILENAME_BUTTON_CLICK";
        /**
         * [5.2]点击按时间倒序排序的次数 每点击一次按时间排序按钮，次数+1
         */
        String MYDUBOXACTIVITY_SORT_DATE_BUTTON_CLICK = "MYDUBOXACTIVITY_SORT_DATE_BUTTON_CLICK";

        /* ***************文件备份**************************************************** */

        /** [5.4]每日执行添加BT种子离线下载的次数 **/
        String OFFLINE_ADD_BT = "OFFLINE_ADD_BT";
        /**
         * [5.2]普通离线下载用户数 添加任务后，点击完成按钮
         */
        String INPUT_FILE_REST = "INPUT_FILE_REST";
        /**
         * [5.2]离线下载的总用户数 普通离线下载（从热门资源中发起的http离线下载、主动添加url的离线下载）、BT离线下载（网盘中BT、本地唤起、浏览器发起）总的去重用户数
         */
        String TOTAL_FILE_REST = "TOTAL_FILE_REST";
        /**
         * [5.2]本地BT离线下载任务数（次数） 一个BT种子算一次任务数
         */
        String BT_LOCAL_FILE_REST = "BT_LOCAL_FILE_REST";
        /**
         * [5.2]点击新建文件夹按钮的次数 每点击一次新建文件夹按钮，次数+1
         */
        String MYNETDISKACTIVITY_PLUS_CREATE_FOLDER_BUTTON_CLICK = "MYNETDISKACTIVITY_PLUS_CREATE_FOLDER_BUTTON_CLICK";

        /** [5.4]每日下载视频的次数 **/
        String DOWNLOAD_VIDEO = "DOWNLOAD_VIDEO";

        /** [5.4]每日下载原画视频的次数 **/
        String DOWNLOAD_ORIGINAL_VIDEO = "DOWNLOAD_ORIGINAL_VIDEO";
        /** [5.4]每日下载转码视频的次数 **/
        String DOWNLOAD_SMOOTH_VIDEO = "DOWNLOAD_SMOOTH_VIDEO";

        /** [5.4]原画按钮的点击次数 **/
        String DOWNLOAD_ORIGINAL_CLICK = "DOWNLOAD_ORIGINAL_CLICK";
        /** [5.4]流畅按钮的点击次数 **/
        String DOWNLOAD_SMOOTH_CLICK = "DOWNLOAD_SMOOTH_CLICK";

        /** [5.4]每日通过本地扫描添加的BT任务次数 **/
        String OFFLINE_SCANNING_BT = "OFFLINE_SCANNING_BT";

        /**
         * [4.1]每日点击资源通知的次数和用户数
         */
        String RESOURCE_NOTICE_CLICK = "Resource_Notice_Click";

        /*** 文件类型 ***/
        String FILE_TYPE_IMAGE = "image";
        String FILE_TYPE_DOC = "doc";
        String FILE_TYPE_APP = "app";
        String FILE_TYPE_AUDIO = "audio";
        String FILE_TYPE_VIDEO = "video";
        String FILE_TYPE_OTHER = "other";
        String FILE_TYPE_ZIP = "zip";
        /** [7.0] 分类按钮点击 **/
        String MYDUBOX_FILE_CATEGORY_SHOW = "mydubox_file_category_show";
        /** [7.0]传输列表打开 **/
        String OPEN_TRANSFERLIST = "open_transferlist";

        /** [7.0] 在多选模式下，下载按钮点击 **/
        String MULTIPLE_CHOICE_DOWNLOAD = "multiple_choice_download";
        /** [7.0] 在多选模式下，分享按钮点击 **/
        String MULTIPLE_CHOICE_SHARE = "multiple_choice_share";
        /** [7.0] 在多选模式下，删除按钮点击 **/
        String MULTIPLE_CHOICE_DELETE = "multiple_choice_delete";
        /** [7.0] 在多选模式下，移动按钮点击 **/
        String MULTIPLE_CHOICE_MOVE = "multiple_choice_move";
        /** [7.0] 在多选模式下，重命名按钮点击 **/
        String MULTIPLE_CHOICE_RENAME = "multiple_choice_rename";
        /** [7.0] 在文件列表点击右侧的灰点 **/
        String LIST_MULTIPLE_CLICK = "list_multiple_click";

        /** [7.1] 下载 Dubox 文件去重时新添加任务 libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK = "download_process_dubox_type_new_task";

        /** [7.1] 下载 Dubox 文件去重时新添加任务并删除老任务 libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK =
                "download_process_dubox_type_new_task_and_remove_last_task";

        /** [7.1] 下载 Dubox 文件去重时新添加已完成任务 libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_FINISHED_TASK = "download_process_dubox_type_new_finished_task";

        /** [7.1] 下载 Dubox 文件去重时新添加已完成任务并拷贝文件到新目录 libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH =
                "download_process_dubox_type_new_finished_task_and_copy_to_new_path";

        /** [7.1] 下载 Dubox 文件去重时将任务设置pending libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_SET_TASK_STATE_TO_PENDING =
                "download_process_dubox_type_set_task_state_to_pending";

        /** [7.1] 下载 Dubox 文件去重时进行一次调度 libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_START_SCHEDULER = "download_process_dubox_type_start_scheduler";

        /** [7.1] 下载 Dubox 文件添加新任务并将已下载的文件重命名 libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_RENAME_BACKUP =
                "download_process_dubox_type_new_task_rename_backup";

        /** [7.1] 下载 Dubox 文件添加新任务并将已下载的文件重命名同时删除老任务 libin09 **/
        String DOWNLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP =
                "download_process_dubox_type_new_task_and_remove_last_task_and_rename_backup";

        /** [7.1] 下载分享文件去重时新添加任务 libin09 **/
        String DOWNLOAD_PROCESS_SHARE_TYPE_NEW_TASK = "download_process_share_type_new_task";

        /** [7.1] 下载分享文件去重时新添加任务并删除老任务 libin09 **/
        String DOWNLOAD_PROCESS_SHARE_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK =
                "download_process_share_type_new_task_and_remove_last_task";

        /** [7.1] 下载分享文件去重时新添加已完成任务 libin09 **/
        String DOWNLOAD_PROCESS_SHARE_TYPE_NEW_FINISHED_TASK = "download_process_share_type_new_finished_task";

        /** [7.1] 下载分享文件去重时新添加已完成任务并拷贝文件到新目录 libin09 **/
        String DOWNLOAD_PROCESS_SHARE_TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH =
                "download_process_share_type_new_finished_task_and_copy_to_new_path";

        /** [7.1] 下载分享文件去重时将任务设置pending libin09 **/
        String DOWNLOAD_PROCESS_SHARE_TYPE_SET_TASK_STATE_TO_PENDING =
                "download_process_share_type_set_task_state_to_pending";

        /** [7.1] 下载分享文件去重时进行一次调度 libin09 **/
        String DOWNLOAD_PROCESS_SHARE_TYPE_START_SCHEDULER = "download_process_dubox_type_start_scheduler";

        /** [7.1] 上传文件去重时新添加任务 libin09 **/
        String UPLOAD_PROCESS_DUBOX_TYPE_NEW_TASK = "upload_process_type_new_task";

        /** [7.1] 上传文件去重时新添加任务并删除老任务 libin09 **/
        String UPLOAD_PROCESS_DUBOX_TYPE_NEW_TASK_AND_REMOVE_LAST_TASK =
                "upload_process_type_new_task_and_remove_last_task";

        /** [7.1] 上传文件去重时新添加已完成任务 libin09 **/
        String UPLOAD_PROCESS_DUBOX_TYPE_NEW_FINISHED_TASK = "upload_process_type_new_finished_task";

        /** [7.1] 上传文件去重时将任务设置pending libin09 **/
        String UPLOAD_PROCESS_DUBOX_TYPE_SET_TASK_STATE_TO_PENDING = "upload_process_type_set_task_state_to_pending";

        /** [7.1] 上传文件去重时进行一次调度 libin09 **/
        String UPLOAD_PROCESS_DUBOX_TYPE_START_SCHEDULER = "upload_process_type_start_scheduler";

        /** [7.6] 未处理完任务的弹窗用户数和次数 **/
        String SHOW_FILEMANAGER_UNHANDLED_TASK_DLG = "show_filemanager_unhandled_task_dlg";

        /** [7.6] 在未处理完任务弹窗内点击查看的用户数和次数 **/
        String FILEMANAGER_UNHANDLED_TASK_DLG_CLICK_VIEW = "filemanager_unhandled_task_dlg_click_view";

        /** [7.6] 在未处理完任务弹窗内点击取消的用户数和次数 **/
        String FILEMANAGER_UNHANDLED_TASK_DLG_CLICK_CANCEL = "filemanager_unhandled_task_dlg_click_cancel";
        /** [7.6] 每天从通知栏处理异常的用户数和次数 **/
        String VIEW_FILEMANAGER_FAILED_TASK_FROM_NOTIFICATION = "view_filemanager_failed_task_from_notification";

        /** [7.6] 每天移动文件进度框出现的用户数和次数 **/
        String SHOW_FILEMANAGER_MOVE_PROGRESS_DLG = "show_filemanager_move_progress_dlg";

        /** [7.6] 每天删除文件进度框出现的用户数和次数 **/
        String SHOW_FILEMANAGER_DELETE_PROGRESS_DLG = "show_filemanager_delete_progress_dlg";

        /** [7.6] 每天删除文件进度框出现的用户数和次数 **/
        String HIDE_FILEMANAGER_PROGRESS_DLG = "hide_filemanager_progress_dlg";

        /** [7.6] 每天冲突处理弹框出现的用户数和次数 **/
        String SHOW_FILEMANAGER_DUPLICATE_FILES_DLG = "show_filemanager_duplicate_files_dlg";

        /** [7.6] 冲突处理弹框内点击取消的用户数和次数 **/
        String FILEMANAGER_ONDUP_CANCEL = "filemanager_ondup_cancel";
        /** [7.6] 冲突处理弹框内点击生成复本的用户数和次数 **/
        String FILEMANAGER_ONDUP_NEWCOPY = "filemanager_ondup_newcopy";
        /** [7.6] 冲突处理弹框内点击覆盖的用户数和次数 **/
        String FILEMANAGER_ONDUP_OVERWRITE = "filemanager_ondup_overwrite";
        /** [7.6] 冲突处理弹框内勾选“执行相同操作”的用户数和次数 **/
        String FILEMANAGER_ONDUP_CHECK_ALL = "filemanager_ondup_check_all";

        /** [7.10]播放的次数、用户数 **/
        String VIDEO_START = "video_start";
        /** [7.10]暂停的次数、用户数 **/
        String VIDEO_PAUSE = "video_pause";
        /** [7.10]拖动的次数、用户数 **/
        String VIDEO_SEEK = "video_seek";
        /** [7.10]在视频播放界面点击分享 **/
        String VIDEO_SHARE_CLICK = "video_share_click";
        /** [7.10]在视频播放界面点击删除 **/
        String VIDEO_DELETE_CLICK = "video_delete_click";

        /** [9.6.50]统计视频播放退出次数，用户数 **/
        String VIDEO_EXIT_CLICK = "video_exit_click";

        /** [7.10]进入视频播放页面的次数 **/
        String VIDEO_PLAY_PAGE_CREATE = "video_play_page_create";

        /** [7.11]推荐列表页统计 **/
        String APP_RECOMMEND_OPEN_DIALOG = "app_recommend_open_dialog";
        /** [7.11]推荐列表中选择app的统计（不同第三方分开统计） **/
        String APP_RECOMMEND_SELECT_APP = "app_recommend_select_app";
        /** [7.11]推荐列表页中选择”总是以此应用打开“的统计 **/
        String APP_RECOMMEND_SELECT_DEFAULT_APP = "app_recommend_select_default_app";
        /** [7.11]进入批选点击删除的次数、用户数 **/
        String RECYCLE_BIN_EDIT_DELETE = "recycle_bin_edit_delete";
        /** [7.11]打开不可预览文件的次数、用户数 **/
        String RECYCLE_BIN_VIEW_UNSUPPORTED_FILE = "recycle_bin_view_unsupported_file";
        /** [7.11]打开预览图片的次数、用户数 **/
        String RECYCLE_BIN_VIEW_IMAGE = "recycle_bin_view_image";
        /** [7.11]在预览图片界面点击还原的次数、用户数 **/
        String RECYCLE_BIN_IMAGE_PREVIEW_RESTORE = "recycle_bin_image_preview_restore";
        /** [7.11]在预览图片界面点击删除的次数、用户数 **/
        String RECYCLE_BIN_IMAGE_PREVIEW_DELETE = "recycle_bin_image_preview_delete";
        /** [7.11]删除失败的次数、用户数 **/
        String RECYCLE_BIN_DELETE_FAILED = "recycle_bin_delete_failed";
        /** [7.11]因空间不足还原失败的次数、用户数 **/
        String RECYCLE_BIN_RESTORE_NO_SPACE = "recycle_bin_restore_no_space";
        /** [7.11]由于网络原因导致还原失败的次数、用户数 **/
        String RECYCLE_BIN_RESTORE_NET_ERROR = "recycle_bin_restore_net_error";
        /** [7.11]由于文件源丢失导致还原失败的次数、用户数 **/
        String RECYCLE_BIN_RESTORE_FILE_NOT_EXISTS = "recycle_bin_restore_file_not_exists";
        /** [7.11]用户还原文件的数量分布 **/
        String RECYCLE_BIN_RESTORE_FILES_COUNT = "recycle_bin_restore_files_count";
        /** [7.11]用户删除文件的数量分布 **/
        String RECYCLE_BIN_DELETE_FILES_COUNT = "recycle_bin_delete_files_count";
        /** [7.11]文件的数量分布 count <=20 **/
        String RECYCLE_BIN_COUNT_0_20 = "count_0_20";
        /** [7.11]文件的数量分布 20<count<=50 **/
        String RECYCLE_BIN_COUNT_20_50 = "count_20_50";
        /** [7.11]文件的数量分布 50<count<=100 **/
        String RECYCLE_BIN_COUNT_50_100 = "count_50_100";
        /** [7.11]文件的数量分布 100<count<=200 **/
        String RECYCLE_BIN_COUNT_100_200 = "count_100_200";
        /** [7.11]文件的数量分布 200<count<=500 **/
        String RECYCLE_BIN_COUNT_200_500 = "count_200_500";
        /** [7.11]文件的数量分布 500<count **/
        String RECYCLE_BIN_COUNT_500_0 = "count_500_0";

        /** [7.11]文件列表中点击打开文件 **/
        String OPEN_FILE = "open_file";
        /** [7.11]多选模式下打开文件 **/
        String MULTIPLE_CHOICE_OPEN_FILE = "multiple_choice_open_file";
        /** [7.11]操作栏点击下载文件 **/
        String DOWNLOAD_FILE = "download_file";

        /** [7.11]云端视频 **/
        String ONLINE_VIDEO = "online_video";
        /** [7.11]本地视频 **/
        String OFFLINE_VIDEO = "offline_video";

        /** [7.12]推荐列表中通过第三方应用打开的次数（不同第三方分开统计） **/
        String APP_RECOMMEND_OPEN_APP = "app_recommend_open_app";

        /** [7.12] 升级下载apk签名不合法的次数 */
        String UPGRADE_APK_INVALID = "upgrade_apk_invalid";
        /** [7.11.6] 未登录进入文件列表页 */
        String ENTER_MAIN_ACTIVITY_WITHOUT_LOGIN = "enter_main_activity_without_login";

        /** [7.12] 非首次删除文件弹窗 **/
        String SHOW_DELETE_FILE_VIP_GUIDE = "show_delete_file_vip_guide";
        /** [7.12] 首次删除文件弹窗 **/
        String SHOW_DELETE_FILE_VIP_GUIDE_FIRST_TIME = "show_delete_file_vip_guide_first_time";
        /** [7.12] 回收站内常驻引导（即回收站进入情况，包含空界面和有文件的常驻引导） **/
        String SHOW_RECYCLE_BIN_RENEWAL_ALERT = "show_recycle_bin_renewal_alert";

        /** [7.13]走byterange视频的下载次数 **/
        String DOWNLOAD_M3U8_BYTERANGE = "download_m3u8_byterange";
        /** [7.13]走byterange视频的下载失败次数 **/
        String DOWNLOAD_M3U8_BYTERANGE_FAILED = "download_m3u8_byterange_failed";

        /**
         * [7.13]更改文件后缀名成功的次数、用户数
         **/
        String UPDATE_FILE_SUFFIX_SUCCESS = "update_file_suffix_success";
        /**
         * [7.13]更改文件后缀名失败的次数、用户数
         **/
        String UPDATE_FILE_SUFFIX_FAILED = "update_file_suffix_failed";
        /**
         * [7.13]在分类模式下更改文件后缀名的次数、用户数
         **/
        String UPDATE_FILE_SUFFIX_IN_CATEGORY_MODE = "update_file_suffix_in_category_mode";
        /**
         * [7.13]用户修改文件格式
         **/
        String UPDATE_FILE_CATEGORY = "update_file_category";

        /**
         * [7.13]在搜索结果页中，下载操作的次数、用户数
         **/
        String SEARCH_PAGE_OPERATION_DOWNLOAD = "search_page_operation_download";
        /**
         * [7.13]在搜索结果页中，分享操作的次数、用户数
         **/
        String SEARCH_PAGE_OPERATION_SHARE = "search_page_operation_share";
        /**
         * [7.13]在搜索结果页中，删除操作的次数、用户数
         **/
        String SEARCH_PAGE_OPERATION_DELETE = "search_page_operation_delete";
        /**
         * [7.13]在搜索结果页中，移动操作的次数、用户数
         **/
        String SEARCH_PAGE_OPERATION_MOVE = "search_page_operation_move";
        /**
         * [7.13]在搜索结果页中，文件所在目录弹窗的取消按钮点击次数、用户数
         **/
        String SEARCH_PAGE_VIEW_DIR_DIALOG_CLICK_CANCLE_BUTTON = "search_page_view_dir_dialog_click_cancle_button";
        /**
         * [7.13]在搜索结果页中，文件所在目录弹窗的查看目录按钮点击次数、用户数
         **/
        String SEARCH_PAGE_VIEW_DIR_DIALOG_CLICK_VIEW_DIR_BUTTON = "search_page_view_dir_dialog_click_view_dir_button";

        /** [7.13]通过p2p sdk下载成功的文件数 **/
        String P2P_DOWNLOAD_SUCCESS = "p2p_download_success";
        /** [7.13]通过p2p sdk下载失败的文件数 **/
        String P2P_DOWNLOAD_FAIL = "p2p_download_fail";

        /**
         * [7.13.0] 统计上报失败统计
         **/
        String STATISTICS_FAILED = "statistics_failed";

        /**
         * [7.13.0] 统计上报失败原因统计
         **/
        String STATISTICS_FAILED_REASON = "statistics_failed_reason";

        /**
         * [7.13.0] 统计上报成功失败总数
         **/
        String STATISTICS_SUCCEEDED_AND_FAILED = "statistics_succeeded_and_failed";

        /** [7.13]p2p-sdk下载启动耗时 **/
        String P2P_START_COST_TIME = "p2p_start_cost_time";
        /** [7.13]p2p下载的次数 **/
        String USE_P2P_DOWNLOAD_TIMES = "use_p2p_download_times";
        /** [7.13]不能用p2p下载的次数 **/
        String NO_USE_P2P_DOWNLOAD_TIMES = "no_use_p2p_download_times";
        /** [7.13]初始化启动p2psdk的次数 **/
        String P2P_SDK_INIT_TIMES = "p2p_sdk_init_times";
        /** [7.13]退出p2psdk的次数 **/
        String P2P_SDK_EXIT_TIMES = "p2p_sdk_exit_times";

        /**
         * [7.13.1] 显示用户过期的
         */
        String SHOW_INVALID_USER_TIMES = "show_invalid_user_times";

        /**
         * [7.13.1] 显示因获取不到STOKE用户过期次数
         */
        String SHOW_NOT_MATCH_STOKEN_INVALID_TIMES = "show_not_match_stoken_invalid_times";

        /**
         * [7.13.2] 弹出非法用户对话框，点击确认按钮次数
         */
        String CLICK_INVALID_USER_DIALOG_OK = "click_invalid_user_dialog_ok";

        /**
         * [7.13.2] 弹出非法用户对话框，点击确认按钮次数和时间
         */
        String CLICK_INVALID_USER_DIALOG_OK_WITH_TIME = "click_invalid_user_dialog_ok_with_time";

        /**
         * [7.13.3] 弹出因获取不到STOKE非法用户对话框，点击确认按钮次数
         */
        String CLICK_NOT_MATCH_STOKEN_INVALID_USER_DIALOG_OK = "click_not_match_stoken_invalid_user_dialog_ok";

        /**
         * [7.13.3] 弹出因获取不到STOKE非法用户对话框，点击确认按钮次数和时间
         */
        String CLICK_NOT_MATCH_STOKEN_INVALID_USER_DIALOG_OK_WITH_TIME =
                "click_not_match_stoken_invalid_user_dialog_ok_with_time";

        /**
         * [7.13.3] 显示因获取不到STOKE用户过期次数和时间
         */
        String SHOW_NOT_MATCH_STOKEN_INVALID_TIMES_WITH_TIME = "show_not_match_stoken_invalid_times_with_time";

        /**
         * [7.13.1] 显示用户过期的次数和时间
         */
        String SHOW_INVALID_USER_TIMES_WITH_TIME = "show_invalid_user_times_with_time";

        /**
         * [7.13.1] 升级框点击"立即更新"的统计
         **/
        String CLICK_VERSION_UPDATE_DIALOG_OK = "click_version_update_dialog_ok";

        /**
         * [7.13.1] 强制升级的统计
         **/
        String VERSION_FORCE_UPDATE_TIMES = "version_force_update_times";

        /**
         * [7.13.1] 升级点击下载成功的次数
         **/
        String VERSION_UPDATE_DOWNLOAD_SUCCESS_TIMES = "version_update_download_success_times";

        /**
         * [7.13.1] 升级点击下载失败的原因分布
         **/
        String VERSION_UPDATE_DOWNLOAD_FAIL_DETAIL = "version_update_download_fail_detail";

        /**
         * [7.13.1] 网络错误
         **/
        String DOWNLOAD_FAIL_NETWORK_ERROR = "download_fail_network_error";

        /**
         * [7.13.1] 获取不到下载地址
         **/
        String DOWNLOAD_FAIL_GET_PATH_EMPTY = "download_fail_get_path_empty";

        /**
         * [7.13.1] 本地文件不存在
         **/
        String DOWNLOAD_FAIL_LOCAL_FILE_NOT_EXIST = "download_fail_local_file_not_exist";

        /**
         * [7.13.1] apk
         **/
        String DOWNLOAD_FAIL_APK_INVALID = "download_fail_apk_invalid";

        /**
         * [7.13.1] 下载不完整
         **/
        String DOWNLOAD_FAIL_FILE_INCOMPLETE = "download_fail_file_incomplete";

        /**
         * [7.13.1] 升级框显示的统计
         **/
        String VERSION_UPDATE_DIALOG_SHOW_TIMES = "version_update_dialog_show_times";

        /**
         * [7.13.1] 升级框点击"下次再说"的统计
         **/
        String CLICK_VERSION_UPDATE_DIALOG_CANCEL = "click_version_update_dialog_cancel";


        /** [7.13]解析种子后文件个数分布 **/
        String OFFLINE_BT_ALL_NUM = "offline_bt_all_num";
        /** [7.13]解析种子后文件个数<=1200 **/
        String BT_NUM_LESS_1200 = "offline_bt_num_less_1200";
        /** [7.13]解析种子后文件个数>1200,<=1400 **/
        String BT_NUM_OVER_1200_LESS_1400 = "bt_num_over_1200_less_1400";
        /** [7.13]解析种子后文件个数分布 >1400,<=1600 **/
        String BT_NUM_OVER_1400_LESS_1600 = "bt_num_over_1400_less_1600";
        /** [7.13]解析种子后文件个数分布 >1600,<=1800 **/
        String BT_NUM_OVER_1600_LESS_1800 = "bt_num_over_1600_less_1800";
        /** [7.13]解析种子后文件个数分布 >1800,<=2000 **/
        String BT_NUM_OVER_1800_LESS_2000 = "bt_num_over_1800_less_2000";
        /** [7.13]解析种子后文件个数分布 >2000,<=2500 **/
        String BT_NUM_OVER_2000_LESS_2500 = "bt_num_over_2000_less_2500";
        /** [7.13]解析种子后文件个数分布 >2500 **/
        String BT_NUM_OVER_2500 = "bt_num_over_2500";
        /** [7.13]解析磁力链后文件个数分布 **/
        String OFFLINE_MAGNET_ALL_NUM = "offline_magnet_all_num";
        /** [7.13]解析磁力链后文件个数分布 <=1200 **/
        String MAGNET_NUM_LESS_1200 = "offline_magnet_num_less_1200";
        /** [7.13]解析磁力链后文件个数分布 >1200,<=1400 **/
        String MAGNET_NUM_OVER_1200_LESS_1400 = "magnet_num_over_1200_less_1400";
        /** [7.13]解析磁力链后文件个数分布 >1400,<=1600 **/
        String MAGNET_NUM_OVER_1400_LESS_1600 = "magnet_num_over_1400_less_1600";
        /** [7.13]解析磁力链后文件个数分布 >1600,<=1800 **/
        String MAGNET_NUM_OVER_1600_LESS_1800 = "magnet_num_over_1600_less_1800";
        /** [7.13]解析磁力链后文件个数分布 >1800,<=2000 **/
        String MAGNET_NUM_OVER_1800_LESS_2000 = "magnet_num_over_1800_less_2000";
        /** [7.13]解析磁力链后文件个数分布 >2000,<=2500 **/
        String MAGNET_NUM_OVER_2000_LESS_2500 = "magnet_num_over_2000_less_2500";
        /** [7.13]解析磁力链后文件个数分布 >2500 **/
        String MAGNET_NUM_OVER_2500 = "magnet_num_over_2500";

        /**
         * [7.14] 转存文件超过处理上限弹窗的次数，用户数
         */
        String SAVE_FILE_OVER_MAX_LIMIT_DIALOG_SHOW = "save_file_over_max_limit_dialog_show";

        /** [7.14]  Dubox 删除操作单次小于2000个文件的用户数和次数 **/
        String DELETE_LESS_THAN_LIMIT = "delete_less_than_limit";
        /** [7.14]  Dubox 删除操作单次大于2000个文件的用户数和次数 **/
        String DELETE_MORE_THAN_LIMIT = "delete_more_than_limit";
        /** [7.14]  Dubox 删除操作失败的用户数和次数 **/
        String DELETE_FILES_FAILED = "delete_files_failed";
        /** [7.14]  Dubox 移动操作单次小于2000个文件的用户数和次数 **/
        String MOVE_LESS_THAN_LIMIT = "move_less_than_limit";
        /** [7.14]  Dubox 移动操作单次大于2000个文件的用户数和次数 **/
        String MOVE_MORE_THAN_LIMIT = "move_more_than_limit";
        /** [7.14]  Dubox 移动操作失败的用户数和次数 **/
        String MOVE_FILES_FAILED = "move_files_failed";
        /** [7.14] 回收站删除操作单次小于2000个文件的用户数和次数 **/
        String DELETE_RECYCLE_BIN_LESS_THAN_LIMIT = "delete_recycle_bin_less_than_limit";
        /** [7.14] 回收站删除操作单次大于2000个文件的用户数和次数 **/
        String DELETE_RECYCLE_BIN_MORE_THAN_LIMIT = "delete_recycle_bin_more_than_limit";
        /** [7.14] 回收站删除操作失败的用户数和次数 **/
        String DELETE_RECYCLE_BIN_FILES_FAILED = "delete_recycle_bin_files_failed";
        /** [7.14] 回收站还原操作单次小于2000个文件的用户数和次数 **/
        String RESTORE_RECYCLE_BIN_LESS_THAN_LIMIT = "restore_recycle_bin_less_than_limit";
        /** [7.14] 回收站还原操作单次大于2000个文件的用户数和次数 **/
        String RESTORE_RECYCLE_BIN_MORE_THAN_LIMIT = "restore_recycle_bin_more_than_limit";
        /** [7.14] 回收站还原操作失败的用户数和次数 **/
        String RESTORE_RECYCLE_BIN_FILES_FAILED = "restore_recycle_bin_files_failed";
        /** [7.14] 文件异步操作请求包大于2M **/
        String FILE_MANAGER_REQUEST_OVERFLOW = "file_manager_request_overflow";

        /**
         * [7.14] 统计通过点击“添加到白名单”提示框允许 Dubox 不优化的用户数
         */
        String DUBOX_SUCCESS_ADD_DOZE_WHITE_LIST = "dubox_success_add_doze_white_list";

        /**
         * [7.14] 一级封禁弹框每日出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_1 = "server_ban_show_dialog_level_1";

        /**
         * [7.14] 一级封禁弹框“申诉”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_1 = "server_ban_click_appeal_level_1";

        /**
         * [7.14] 三级封禁弹框每日出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_3 = "server_ban_show_dialog_level_3";

        /**
         * [7.14] 三级封禁弹框“申诉”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_3 = "server_ban_click_appeal_level_3";

        /**
         * [7.14] 四级封禁弹框每日出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_4 = "server_ban_show_dialog_level_4";

        /**
         * [7.14] 四级封禁弹框“申诉”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_4 = "server_ban_click_appeal_level_4";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）对应的弹框出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_0 = "server_ban_show_dialog_level_0";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）级封禁弹框“申诉/验证”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_0 = "server_ban_click_appeal_level_0";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）对应的弹框出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_2 = "server_ban_show_dialog_level_2";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）级封禁弹框“申诉/验证”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_2 = "server_ban_click_appeal_level_2";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）对应的弹框出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_5 = "server_ban_show_dialog_level_5";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）级封禁弹框“申诉/验证”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_5 = "server_ban_click_appeal_level_5";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）对应的弹框出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_6 = "server_ban_show_dialog_level_6";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）级封禁弹框“申诉/验证”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_6 = "server_ban_click_appeal_level_6";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）对应的弹框出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_7 = "server_ban_show_dialog_level_7";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）级封禁弹框“申诉/验证”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_7 = "server_ban_click_appeal_level_7";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）对应的弹框出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_8 = "server_ban_show_dialog_level_8";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）级封禁弹框“申诉/验证”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_8 = "server_ban_click_appeal_level_8";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）对应的弹框出现的次数
         */
        String SERVER_BAN_SHOW_DIALOG_LEVEL_9 = "server_ban_show_dialog_level_9";

        /**
         * [7.14] 预埋封禁（90** /92** /95** /96** /97** /98** /99**）级封禁弹框“申诉/验证”按钮被点击的次数
         */
        String SERVER_BAN_CLICK_APPEAL_LEVEL_9 = "server_ban_click_appeal_level_9";

        /**
         * [7.14] 疑似盗号弹框展现
         */
        String DOUBT_HACKING_SHOW_DIALOG = "doubt_hacking_show_dialog";

        /**
         * [7.14] 疑似盗号弹框点击
         */
        String DOUBT_HACKING_CLICK_APPEAL = "doubt_hacking_click_appeal";

        /**
         * [7.14.0] 统计上报成功
         **/
        String STATISTICS_UPLOAD_SUCCEEDED = "statistics_upload_succeeded";

        /**
         * [7.14.0] 统计上报失败
         **/
        String STATISTICS_UPLOAD_FAILED = "statistics_upload_failed";

        /**
         * [7.16] 手机备份每日在图片自动备份的用户数
         */
        String PHONE_SETTING_PAGE_SETTING_PHOTO_BACKUP = "phone_setting_page_setting_photo_backup";

        /**
         * [7.16] 图片预览，区分来源，用户数和文件数
         */
        String SHOW_PREVIEW_IMAGE_SOURCE = "show_preview_image_source";

        /**
         * [7.16] 搜索结果进入预览的用户数和文件数
         */
        String PREVIEW_FILE_FORM_SEARCH_RESULT = "preview_file_form_search_result";

        /**
         * [7.16] 前台在线时长
         */
        String MY_FOREGROUND_ONLINE_TIME_CLUSTER = "my_foreground_online_time_cluster";

        /**
         * [7.16] 前台日活比例
         */
        String MY_FOREGROUND_ACTIVE = "my_foreground_active";
        // 隐藏空间
        /** [7.15] 每日将文件移入隐藏空间的用户数和次数 **/
        String MOVE_FILE_IN_SAFE_BOX = "move_file_in_safe_box";
        /** [7.15] 每日将隐藏空间中的文件移出用户数和次数 **/
        String MOVE_FILE_OUT_SAFE_BOX = "move_file_out_safe_box";
        /** [7.15] 每日将隐藏空间中移出文件的用户数和次数 **/
        String MOVE_FILE_INSIDE_SAFE_BOX = "move_file_inside_safe_box";

        // 流畅点播

        /** [7.16] 每天下载完成的用户数、次数并上传该文件的扩展名 **/
        String DOWNLOAD_TAB_FILE_DOWNLOAD_FINISH = "download_tab_file_download_finish";

        /** [7.16] 每天在通知栏中点击安全扫描按钮的用户数、次数 **/
        String START_SECURITY_SCAN_BY_NOTIFICATION = "start_security_scan_by_notification";

        /*** [7.17.1] 网络类型不一致 **/
        String NETWORK_TYPE_INCONSISTENT = "network_type_inconsistent";

        /** [7.18] 首页点击图片分类 **/
        String MY_DUBOX_CATEGORY_IMAGE_CLICK = "my_dubox_category_image_click";

        /** [7.18] 上滑显示图片详情的次数、用户数 **/
        String IMAGE_DETAIL_DRAWER_SHOW = "image_detail_drawer_show";

        /** [7.18] 大图预览的加载时长，按秒细分统计 **/
        String FULL_SCREEN_IMAGE_LOAD_TIME_DURATION = "full_screen_image_load_time_duration";

        /** [7.18] 大图预览的加载总时长 **/
        String FULL_SCREEN_IMAGE_LOAD_TIME = "full_screen_image_load_time";

        /** [7.18] 大图预览的次数用于统计平均时长 **/
        String FULL_SCREEN_IMAGE_LOAD = "full_screen_image_load";

        /** [7.18]下滑退出操作（全局）的次数、用户数 **/
        String PLAY_EXIT_ANIMATE_FINISH_PREVIEW = "play_exit_animate_finish_preview";

        /** [7.18]全选并点击删除 **/
        String ALL_SELECT_AND_DELETE_CLICK = "all_select_and_delete_click";

        /** [7.18] 通过第三方推荐应用打开文件次数，包含扩展名 **/
        String OPEN_FILE_BY_3RD = "open_file_by_3rd";

        /** [7.18] 点击视频文件进入播放界面的用户数、次数 **/
        String CLICK_TO_PLAY_VIDEO = "click_to_play_video";
        /** [7.18] 视频贴片界面点击返回的用户数、次数 **/
        String CLICK_VIDEO_PLAYER_ACTIVITY_BACK = "click_video_player_activity_back";

        /** [8.0] 在新建文件夹界面成功设为共享文件夹 **/
        String CREATE_SHARE_DIRECTORY_SUCCESS = "create_share_directory_success";
        // 我的卡包统计-8.0
        /** [8.0] 引导窗口的展示时长（时长分布百分比） **/
        String[] CARD_GUIDE_DIALOG_SHOW_DURATIONS = { "0_1s", "1_3s", "3_5s", "5_10s", "10s_max" };
        /** [8.0] 证件照片大图页下载照片按钮点击用户数和次数 **/
        String CARD_PREVIEW_IMAGE_DOWNLOAD = "card_preview_image_download";

        /** [8.0] 8.0以上版本上滑显示图片详情的次数、用户数 **/
        String NEW_IMAGE_DETAIL_DRAWER_SHOW = "new_image_detail_drawer_show";
        /** [8.0] 进行复制操作的总用户数、总次数（不区分文件夹） **/
        String DUBOX_FILE_OPERATE_COPY_CLICK = "dubox_file_operate_copy_click";

        // 8.0新增权限统计
        String[] PERMISSION_TYPE = { "basic_permission", "quick_setting", "sms", "contract", "location", "camera",
                "call_log", "phone_forget", "mobile_search", "p2p_share", "storage" };
        /** [8.0] 首次安装用户，带【授权】按钮提示框的出现次数和用户数 **/
        String FIRST_INSTALL_PERMISSION_RESULT_DIALOG_SHOW = "first_install_permission_result_dialog_show";
        /** [8.0] 覆盖安装用户，带【授权】按钮提示框的出现次数和用户数 **/
        String COVER_INSTALL_PERMISSION_RESULT_DIALOG_SHOW = "cover_install_permission_result_dialog_show";
        /** [8.0] 首次安装用户，带【授权】按钮提示框的授权按钮点击次数和用户数 **/
        String FIRST_INSTALL_PERMISSION_RESULT_DIALOG_CONFIRM_BTN_CLICK =
                "first_install_permission_result_dialog_confirm_btn_click";
        /** [8.0] 覆盖安装用户，带【授权】按钮提示框的授权按钮点击次数和用户数 **/
        String COVER_INSTALL_PERMISSION_RESULT_DIALOG_CONFIRM_BTN_CLICK =
                "cover_install_permission_result_dialog_confirm_btn_click";
        /** [8.0] 首次安装用户，带【授权】按钮提示框的取消按钮点击次数和用户数 **/
        String FIRST_INSTALL_PERMISSION_RESULT_DIALOG_CANCEL_BTN_CLICK =
                "first_install_permission_result_dialog_cancel_btn_click";
        /** [8.0] 覆盖安装用户，带【授权】按钮提示框的取消按钮点击次数和用户数 **/
        String COVER_INSTALL_PERMISSION_RESULT_DIALOG_CANCEL_BTN_CLICK =
                "cover_install_permission_result_dialog_cancel_btn_click";
        /** [8.0] 首次安装用户，带【知道了】按钮提示框的出现次数和用户数 **/
        String FIRST_INSTALL_PERMISSION_REQUEST_DIALOG_SHOW = "first_install_permission_request_dialog_show";
        /** [8.0] 覆盖安装用户，带【知道了】按钮提示框的出现次数和用户数 **/
        String COVER_INSTALL_PERMISSION_REQUEST_DIALOG_SHOW = "cover_install_permission_request_dialog_show";
        /** [8.0] 首次安装用户，带【知道了】按钮提示框的知道了按钮点击次数和用户数 **/
        String FIRST_INSTALL_PERMISSION_REQUEST_DIALOG_KNOW_BTN_CLICK =
                "first_install_permission_request_dialog_know_btn_click";
        /** [8.0] 覆盖安装用户，带【知道了】按钮提示框的知道了按钮点击次数和用户数 **/
        String COVER_INSTALL_PERMISSION_REQUEST_DIALOG_KNOW_BTN_CLICK =
                "cover_install_permission_request_dialog_know_btn_click";

        /** [8.1] 每天创建共享目录失败的次数，区分不同错误码 **/
        String CREATE_SHARE_DIRECTORY_FAILED_COUNT = "create_share_directory_failed_count";

        /** [8.1] 每日打开pdf文档的次数和用户数 **/
        String OPEN_PDF_FILE_COUNT = "open_pdf_file_count";

        /** [8.1] 未选择pdf文档的默认应用，本地有打开pdf的软件，从而调起应用选择列表的次数和用户数 **/
        String OPEN_RECOMMEND_PDF_APP_LIST = "open_recommend_pdf_app_list";

        /** [8.1] 用户点击pdf文档成功调起手百的次数和用户数的总量 **/
        String OPEN_PDF_BY_SEARCH_BOX = "open_pdf_by_search_box";

        /** [8.2] 每天文件分享点击复制私密链接的用户数与次数 **/
        String CLICK_FILE_SHARE_COPY_LINK = "click_file_share_copy_link";
        /** [1.3.0] 公开链接的用户点击 **/
        String CLICK_FILE_SHARE_TO_OPEN_LINK = "click_file_share_to_open_link";

        /** [8.2] 在第三方应用中选择了使用 Dubox 打开的用户数、次数 **/
        String OPEN_IN_OTHER_APPS = "open_in_other_apps";

        /** [8.3] 自动备份触发（图片、视频、文件、短信、通讯录、通话记录、应用） **/
        String AUTO_BACKUP_START = "auto_backup_start";

        /**
         * 图片备份
         */
        int BACKUP_TYPE_IMAGE = 0;
        /**
         * 视频备份
         */
        int BACKUP_TYPE_VIDEO = 1;
        /**
         * 文件备份
         */
        int BACKUP_TYPE_FILE = 2;

        /** [8.3]  Dubox 进程启动 **/
        String DUBOX_APPLICATION_START = "dubox_application_start";

        /** [8.3] 图片优化，图片缩略图网络下载时间超过配置阈值则上报 **/
        String CLOUD_IMAGE_THUMBNAIL_LOAD_TIME = "cloud_image_thumbnail_load_time";

        /** [8.5] 每日上传gif图的用户数和图片数量(包括秒传) **/
        String UPLOAD_GIF_SUCCESS_ALL = "upload_gif_success_all";
        /** [8.7.1] 每日上传gif图失败的用户数和图片数量(包括秒传) **/
        String UPLOAD_GIF_FAIL_ALL = "upload_gif_fail_all";

        /** [8.5] 8.5版本进入图片分类的用户数、次数 **/
        String ENTER_CATEGORY_IMAGE_COUNT_85 = "enter_category_image_count_85";

        /** [8.5]锁定键的点击次数、用户数 **/
        String VIDEO_LOCK_CLICK = "video_lock_click";

        /** [8.5]锁定状态下点击屏幕的次数、用户数 **/
        String VIDEO_SCREEN_CLICK_IN_LOCK_STATE = "video_screen_click_in_lock_state";

        /** [8.5]物理按键音量调节的次数、用户数 **/
        String VIDEO_VOLUME_ADJUST_BY_KEY_PRESS = "video_volume_adjust_by_key_press";

        /** [8.5]手势音量调节的次数、用户数 **/
        String VIDEO_VOLUME_ADJUST_BY_GESTURE = "video_volume_adjust_by_gesture";

        /** [8.5]手势亮度调节的次数、用户数 **/
        String VIDEO_BRIGHTNESS_ADJUST_BY_GESTURE = "video_brightness_adjust_by_gesture";

        /** [8.5]更多中音量调节的次数、用户数 **/
        String VIDEO_VOLUME_ADJUST_BY_SEEK = "video_volume_adjust_by_seek";

        /** [8.5]更多中亮度调节的次数、用户数 **/
        String VIDEO_BRIGHTNESS_ADJUST_BY_SEEK = "video_brightness_adjust_by_seek";

        /** [8.5]更多键的点击次数、用户数 **/
        String VIDEO_MORE_CLICK = "video_more_click";

        /** [8.5]转存键的点击次数、用户数 **/
        String VIDEO_SAVE_CLICK = "video_save_click";

        /** [8.5]保存到 Dubox 键的点击次数、用户数 **/
        String VIDEO_DOWNLOAD_CLICK = "video_download_click";

        /** [moder 3.0] 打开本地视频后点击上传按钮 */
        String VIDEO_UPLOAD_CLICK_FROM_LOCAL = "video_upload_click_from_local";

        /** [8.5]流畅/原画键的点击次数、用户数 **/
        String VIDEO_QUALITY_BUTTON_CLICK = "video_quality_button_click";

        /** [8.5]点击下载图片的次数和用户数 **/
        String DOWNLOAD_IMAGE_COUNT = "download_image_count";

        /** [8.5] Dubox 向系统申请READ_CALL_LOG权限次数、用户数 **/
        String REQUEST_PERMISSION_READ_CALL_LOG = "request_permission_read_call_log";

        /** [8.5] Dubox 向系统申请READ_EXTERNAL_STORAGE权限次数、用户数 **/
        String REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = "request_permission_read_external_storage";

        /** [8.5] Dubox 向系统申请WRITE_EXTERNAL_STORAGE权限次数、用户数 **/
        String REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = "request_permission_write_external_storage";

        /** [8.5] Dubox 向系统申请SEND_SMS权限次数、用户数 **/
        String REQUEST_PERMISSION_SEND_SMS = "request_permission_send_sms";

        /** [8.5] Dubox 向系统申请READ_SMS权限次数、用户数 **/
        String REQUEST_PERMISSION_READ_SMS = "request_permission_read_sms";

        /** [8.5] Dubox 向系统申请READ_CONTACTS权限次数、用户数 **/
        String REQUEST_PERMISSION_READ_CONTACTS = "request_permission_read_contacts";

        /** [8.5] Dubox 向系统申请WRITE_CONTACTS权限次数、用户数 **/
        String REQUEST_PERMISSION_WRITE_CONTACTS = "request_permission_write_contacts";

        /** [8.5] Dubox 向系统申请ACCESS_FINE_LOCATION权限次数、用户数 **/
        String REQUEST_PERMISSION_ACCESS_FINE_LOCATION = "request_permission_access_fine_location";

        /** [8.5] Dubox 向系统申请ACCESS_COARSE_LOCATION权限次数、用户数 **/
        String REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = "request_permission_access_coarse_location";

        /** [8.5] Dubox 向系统申请CAMERA权限次数、用户数 **/
        String REQUEST_PERMISSION_CAMERA = "request_permission_camera";

        /** [8.5]每天用户授予READ_CALL_LOG权限次数、用户数 **/
        String GRANTED_PERMISSION_READ_CALL_LOG = "granted_permission_read_call_log";


        /** [8.5]每天用户授予READ_EXTERNAL_STORAGE权限次数、用户数 **/
        String GRANTED_PERMISSION_READ_EXTERNAL_STORAGE = "granted_permission_read_external_storage";

        /** [8.5]每天用户授予WRITE_EXTERNAL_STORAGE权限次数、用户数 **/
        String GRANTED_PERMISSION_WRITE_EXTERNAL_STORAGE = "granted_permission_write_external_storage";

        /** [8.5]每天用户授予SEND_SMS权限次数、用户数 **/
        String GRANTED_PERMISSION_SEND_SMS = "granted_permission_send_sms";

        /** [8.5]每天用户授予READ_SMS权限次数、用户数 **/
        String GRANTED_PERMISSION_READ_SMS = "granted_permission_read_sms";

        /** [8.5]每天用户授予READ_CONTACTS权限次数、用户数 **/
        String GRANTED_PERMISSION_READ_CONTACTS = "granted_permission_read_contacts";

        /** [8.5]每天用户授予WRITE_CONTACTS权限次数、用户数 **/
        String GRANTED_PERMISSION_WRITE_CONTACTS = "granted_permission_write_contacts";

        /** [8.5]每天用户授予ACCESS_FINE_LOCATION权限次数、用户数 **/
        String GRANTED_PERMISSION_ACCESS_FINE_LOCATION = "granted_permission_access_fine_location";

        /** [8.5]每天用户授予ACCESS_COARSE_LOCATION权限次数、用户数 **/
        String GRANTED_PERMISSION_ACCESS_COARSE_LOCATION = "granted_permission_access_coarse_location";

        /** [8.5]每天用户授予CAMERA权限次数、用户数 **/
        String GRANTED_PERMISSION_CAMERA = "granted_permission_camera";

        /** [8.5]图片加载耗时统计 **/
        String LOAD_IMAGE = "loadImage";

        /** [8.5]平台sdk调起文件选择页面 **/
        String PLATFORM_SDK_LAUNCH_PICK_FILE = "platform_sdk_launch_pick_file";

        /** [8.6] 第三方应用打开选择保存到 Dubox 的用户数和次数（带扩展名） **/
        String OPEN_IN_OTHER_APP_WITH_EXTENSION = "open_in_other_app_with_extension";
        /** [8.6]  Dubox 照片大图预览界面点击下载到 Dubox 的用户数和次数 **/
        String LIVE_PHOTO_PREVIEW_DOWNLOAD_COUNT = "live_photo_preview_download_count";
        /** [8.6]  Dubox 照片除大图预览界面点击下载到 Dubox 的用户数和次数 **/
        String LIVE_PHOTO_OTHER_DOWNLOAD_COUNT = "live_photo_other_download_count";
        /** [8.6] 下载完成后点击导出到手机相册的用户数和次数 **/
        String LIVE_PHOTO_EXPORT_COUNT = "live_photo_export_count";
        /** [8.6] 导出到手机相册中点击仅导出照片用户数和次数 **/
        String LIVE_PHOTO_EXPORT_ONLY_IMAGE_COUNT = "live_photo_export_only_image";
        /** [8.6] 导出到手机相册中点击导出照片和视频的用户数和次数 **/
        String LIVE_PHOTO_EXPORT_ALL_COUNT = "live_photo_export_all";

        /** [8.6]  Dubox 照片大图预览界面进行预览实况照片中点击播放视频的用户数和次数 */
        String IMAGE_PREVIEW_LIVE_PHOTO_PLAY_VIDEO_CLICK = "image_preview_live_photo_play_video_click";

        /** [8.6]  Dubox 照片大图预预览实况照片中点击播放,播放失败的用户数和次数 */
        String IMAGE_PREVIEW_LIVE_PHOTO_PLAY_VIDEO_FAILED = "image_preview_live_photo_play_video_failed";

        /** [8.6] 传输列表下载列表中预览实况照片中点击视频播放的用户数和次数 */
        String LIVE_PHOTO_FROM_TASK_PLAY_VIDEO_CLICK = "live_photo_from_task_play_video_click";

        /*** [8.7.1] 上传页中点击上传按钮统计 **/
        String FOLDER_CLICK_FILE_UPLOAD = "folder_click_file_upload";

        /**
         * 【8.7.1】Push系统消息通知栏点击次数
         */
        String PUSH_SYSTEM_NOTIFY_CLICK_TIME = "push_system_notify_click_time";

        /**
         * 【8.7.1】从第三方应用换气 Dubox 应用
         */
        String LAUNCH_FROM_THIRD_APPLICATION = "launch_from_third_application";

        /**
         * [8.7.1] 手动点击桌面icon冷启动
         **/
        String LAUNCH_FROM_CLICK_ICON = "launch_from_click_icon";

        /**
         * 打开本地文件，启动应用
         */
        String LAUNCH_FROM_OPEN_LOCAL_FILE = "launch_from_open_local_file";
        /**
         *  打开本地文件，类型统计
         */
        String LAUNCH_FROM_OPEN_LOCAL_FILE_TYPE = "launch_from_open_local_file_type";

        /**
         * 内容提供 - 文件选择页
         */
        String LAUNCH_FROM_PICK_FILE = "launch_from_pick_file";

        /** [8.8] 开通弹窗立即开通按钮 用户数 次数 **/
        String VIDEO_SPEED_DIALOG_NO_FREE_TOBE_SVIP = "video_speed_dialog_no_free_tobe_svip";

        /** [8.9] 从普通云文件进入的视频播放的总次数、总用户数 **/
        String VISIT_AUDIO_FROM_COMMON_CLOUDFILE_COUNT = "visit_audio_from_common_cloudfile_list_count";
        /** [8.9] 从分类-音频文件进入的视频播放的总次数、总用户数 **/
        String VISIT_AUDIO_FROM_CATEGORY_COUNT = "visit_audio_from_category_list_count";

        /** [8.9] 8.9及以上版本，端内每日出现流量保护弹窗的用户数、点击次数 **/
        String AUDIO_SHOW_FLOW_DIALOG_COUNT = "audio_show_flow_dialog_count";

        /** [8.10] 开通弹窗立即开通按钮 用户数 次数 **/
        String AUDIO_SPEED_DIALOG_NO_FREE_TOBE_SVIP = "audio_speed_dialog_no_free_tobe_svip";

        /** [8.10] 分类按钮点击 **/
        String DUBOX_FILE_CATEGORY_SHOW = "dubox_file_category_show";

        /** [8.10] 最近使用按钮点击 **/
        String DUBOX_FILE_CATEGORY_RECENT_SHOW = "dubox_file_category_recent_show";

        /** [8.10] 最近使用聚合页移动 **/
        String RECENT_SECOND_MOVE = "recent_second_move";

        /** [8.11] 公开分享外链弹窗点击关闭的次数、用户数 **/
        String PUBLIC_SHARE_CHAIN_DIALOG_CLICK_CLOSE = "public_share_chain_dialog_click_close";

        /** [8.11] 私密分享外链弹窗点击关闭的次数、用户数 **/
        String PRIVATE_SHARE_CHAIN_DIALOG_CLICK_CLOSE = "private_share_chain_dialog_click_close";

        /** [8.11] 公开分享外链弹窗点击立即查看的次数、用户数 **/
        String PUBLIC_SHARE_CHAIN_DIALOG_CLICK_IMMEDIATELY_VIEW = "public_share_chain_dialog_click_immediately_view";

        /** [8.11] 私密分享外链弹窗点击立即查看的次数、用户数 **/
        String PRIVATE_SHARE_CHAIN_DIALOG_CLICK_IMMEDIATELY_VIEW = "private_share_chain_dialog_click_immediately_view";

        /** [8.11] 登录态公开分享外链弹窗展现的次数、用户数 **/
        String LOGIN_STATE_PUBLIC_SHARE_CHAIN_DIALOG_SHOW = "login_state_public_share_chain_dialog_show";

        /** [8.11] 登录态私密分享外链弹窗展现的次数、用户数 **/
        String LOGIN_STATE_PRIVATE_SHARE_CHAIN_DIALOG_SHOW = "login_state_private_share_chain_dialog_show";

        /** [1.3.0] 口令识别弹窗展示 **/
        String CHAIN_RECOGNIZE_DIALOG_SHOW = "chain_recognize_dialog_show";
        /** [1.3.0] 口令识别弹窗去查看点击 **/
        String CHAIN_RECOGNIZE_DIALOG_CHECK_CLICK = "chain_recognize_dialog_check_click";
        /** [1.3.0] 口令识别弹窗关闭点击 **/
        String CHAIN_RECOGNIZE_DIALOG_CLOSE_CLICK = "chain_recognize_dialog_close_click";

        /** [8.11] 未登录态存在分享外链的次数、用户数 **/
        String UNLOGIN_STATE_SHARE_CHAIN_EXIST = "unlogin_state_share_chain_exist";

        /** [8.11] 提取码验证成功的次数、用户数 **/
        String VERIFY_SUCCEED_TEXT_SHOW = "verify_succeed_text_show";

        /** [8.11] 外链弹窗点击查看，出现输入提取码页面的次数、用户数 **/
        String SHARE_CHAIN_SHOW_INPUT_EXTRACTION_CODE_PAGE = "share_chain_show_input_extraction_code_page";

        /** [8.11] 从外链识别进入分享页面的次数、用户数 **/
        String ENTER_SHARE_INFO_PAGE_FROM_CHAIN_RECOGNIZE = "enter_share_info_page_from_chain_recognize";

        /** [8.11.0] 下载列表展示pv、uv **/
        String DOWNLOAD_LIST_PAGE_SHOW = "download_list_page_show";

        /** [8.11.0] 结束时点击中间下一视频的uv、pv **/
        String VEDIO_STATUS_FINISH_PLAY_NEXT_MIDDLE = "video_status_finish_play_next_middle";
        /** [8.11.0] 播放时点击底部下一视频的uv、pv **/
        String VEDIO_STATUS_PLAYING_PLAY_NEXT_MILE = "video_status_playing_play_next_left_bom";
        /** [8.11.0] 下一视频切倍速播放的uv、pv **/
        String VEDIO_PLAY_NEXT_SPEED_UP_CHANGE = "video_play_next_speed_up_change";
        /** [8.11.0] 文件列表入口播放视频的uv、pv **/
        String VISIT_VIDEO_FROM_COMMON_CLOUDFILE_COUNT = "visit_video_from_common_cloudfile_list_count";
        /** [8.11.0] 分类-视频入口播放视频的uv、pv **/
        String VISIT_VIDEO_FROM_CATEGORY_COUNT = "visit_video_from_category_list_count";
        /** [8.11.0] 传输列表入口播放视频的uv、pv **/
        String VISIT_VIDEO_FROM_TRANSMIT_COUNT = "visit_video_from_transmit_list_count";

        /** [8.12] 最近使用列表打开所在目录 **/
        String RECENT_OPEN_DIR = "recent_open_dir";

        /** [8.12] 传输列表中预览文件 **/
        String TRANSFER_FRAGMENT_PREVIEW = "transfer_fragment_preview";

        /** [8.12.0] 每日出现[手机存储空间不足，清理后重试]的用户数、次数 **/
        String NO_SDCARD_SPACE = "no_sdcard_space";

        /** [8.12.0] 收到[文件数超限]弹窗的uv,pv **/
        String TOO_MUCH_FILES_DIALOG = "too_much_files_dialog";

        /** [8.12.0] 收到[文件数超限]弹窗中[知道了]的uv,pv **/
        String TOO_MUCH_FILES_KNOWN = "too_much_files_known";

        /** [9.0.0] 传输列表tab-全部重新下载点击的次数用户数 **/
        String TRANSFER_RELOAD_BTN_CLICK = "transfer_reload_btn_click";

        /** [9.0.0] 文件tab-上传页内选择新建文件夹的次数用户数 */
        String FILELIST_PAGE_ENTRY_NEW_FOLDER_COUNT = "filelist_page_entry_new_folder_count";
        /** [9.0.0] 文件tab-上传页内选择上传照片的次数用户数 */
        String FILELIST_PAGE_ENTRY_UPLOAD_PHOTO_COUNT = "filelist_page_entry_upload_photo_count";
        /** [9.0.0] 文件tab-上传页内选择上传视频的次数用户数 */
        String FILELIST_PAGE_ENTRY_UPLOAD_VIDEO_COUNT = "filelist_page_entry_upload_video_count";

        /**
         * [9.0.0] 文件tab内右上角…点击的次数用户数
         */
        String TITLEBAR_MORE_CLICK = "titlebar_more_click";

        /**
         * [9.0.0]文件tab内右上角…点击【选择文件】的次数用户数
         */
        String FILE_CHOOSE_CLICK = "file_choose_click";

        /**
         * [9.0.0]文件tab内右上角…点击【文件夹详情】的次数用户数
         */
        String FILE_DIRINFO_CLICK = "file_dirinfo_click";

        /** [9.0.0] 一二级页面点击查看详细信息的pv uv **/
        String RECENT_VIEW_SELECTED_DETAIL = "recent_view_selected_detail";

        /**
         * [9.0.0] 二级页面点击重命名的pv uv
         */
        String RECENT_RENAME = "recent_rename";

        /** [8.10] 最近二级页面点击复制的pv uv **/
        String RECENT_SECOND_COPY_NO_COUNT = "recent_second_copy_no_count";

        /** [9.0.0] 最近使用中用户视频播放在线使用时长 **/
        String RECENT_VIEW_VIDEO_TIME = "recent_view_video_time";

        /** [9.1.0] 竖屏视频播放Error次数、用户数 **/
        String VIDEO_PLAYER_SDK_ERROR = "video_player_sdk_error";

        /** [9.1.0] 微信备份 流量弹窗的展示 */
        String WECHAT_BACKUP_ONLY_WIFI_DIALOG_DISPLAY = "wechat_backup_only_wifi_dialog_display";

        /** [9.1.0] 微信备份 流量弹窗的取消按钮被点击 */
        String WECHAT_BACKUP_ONLY_WIFI_DIALOG_CANCEL_BTN_CLICK = "wechat_backup_only_wifi_dialog_cancel_btn_click";

        /** [9.1.0] 微信备份 流量弹窗的关闭仅 WIFI 传输按钮被点击 */
        String WECHAT_BACKUP_ONLY_WIFI_DIALOG_SETTING_BTN_CLICK = "wechat_backup_only_wifi_dialog_setting_btn_click";

        // 新视频sdk vast的统计项, 替换旧的统计项
        /** [9.2.0] 开始获取视频信息的用户数、次数 **/
        String VAST_GET_VIDEO_INFO_START = "vast_get_video_info_start";
        /** [9.2.0]获取视频信息失败的用户数、次数 **/
        String VAST_GET_VIDEO_INFO_FAILED = "vast_get_video_info_failed";
        /** [9.2.0]获取视频信息成功的用户数、次数 **/
        String VAST_GET_VIDEO_INFO_SUCCESS = "vast_get_video_info_success";
        /** [9.2.0] 进入视频播放贴片界面播放广告的用户数、次数 **/
        String VAST_AD_PALY_COUNT = "vast_ad_play_count";
        /** [9.2.0] 视频贴片界面出现广告播放失败的用户数、次数（出现错误图片） **/
        String VAST_AD_FAIL_COUNT = "vast_ad_fail_count";
        /** [9.2.0] 视频贴片广告播放超时的用户数、次数 **/
        String VAST_AD_TIMEOUT_COUNT = "vast_ad_timeout_count";
        /** [9.2.0] 视频贴片循环播放次数 **/
        String VAST_AD_LOOP_TIMES = "vast_ad_loop_times";
        /** [9.2.0] 通过快速滑动屏幕拖动视频的幅度 **/
        String VAST_FLING_VIDEO_SEEK_STEP = "vast_fling_video_seek_step";
        /** [9.2.0] 通过滑动屏幕拖动视频的幅度 **/
        String VAST_SLIDE_VIDEO_SEEK_STEP = "vast_slide_video_seek_step";
        /** [9.2.0] 通过滑动进度条拖动视频幅度 **/
        String VAST_SEEK_BAR_VIDEO_SEEK_STEP = "vast_seek_bar_video_seek_step";
        /** [9.2.0] 视频播放错误的用户数/次数 **/
        String VAST_VIDEO_PLAY_ERROR = "vast_video_play_error";
        /** [9.2.0] 视频流畅非P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_SMOOTH_NOT_P2P_PLAY_ERROR = "vast_video_smooth_not_p2p_play_error";
        /** [9.2.0] 视频原画非P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_ORIGINAL_NOT_P2P_PLAY_ERROR = "vast_video_original_not_p2p_play_error";
        /** [9.2.0] 视频流畅P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_SMOOTH_P2P_PLAY_ERROR = "vast_video_smooth_p2p_play_error";
        /** [10.0.80] 视频360P P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_360P_P2P_PLAY_ERROR = "vast_video_360P_p2p_play_error";
        /** [10.0.80] 视频480P P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_480P_P2P_PLAY_ERROR = "vast_video_480P_p2p_play_error";
        /** [10.0.80] 视频720P P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_720P_P2P_PLAY_ERROR = "vast_video_720P_p2p_play_error";
        /** [10.0.80] 视频1080P P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_1080P_P2P_PLAY_ERROR = "vast_video_1080P_p2p_play_error";
        /** [10.0.80] 视频360P 非P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_360P_NOT_P2P_PLAY_ERROR = "vast_video_360P_not_p2p_play_error";
        /** [10.0.80] 视频480P 非P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_480P_NOT_P2P_PLAY_ERROR = "vast_video_480P_not_p2p_play_error";
        /** [10.0.80] 视频720P 非P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_720P_NOT_P2P_PLAY_ERROR = "vast_video_720P_not_p2p_play_error";
        /** [10.0.80] 视频1080P 非P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_1080P_NOT_P2P_PLAY_ERROR = "vast_video_1080P_not_p2p_play_error";
        /** [9.2.0] 视频原画非P2P播放错误的用户数/次数 **/
        String VAST_VIDEO_ORIGINAL_P2P_PLAY_ERROR = "vast_video_original_p2p_play_error";
        /** [9.2.0] 视频播放的次数、用户数 **/
        String VAST_VIDEO_PLAY_COUNT = "vast_video_play_count";
        /** [9.2.0] 视频不间断播放的播放时长统计,暂停播放和切换倍速重新计算 **/
        String VAST_VIDEO_PLAY_ONCE_DURATION = "vast_video_play_once_duration";
        /** [9.2.0] 视频播放时长, 暂停时停止计时 **/
        String VAST_VIDEO_PLAY_REAL_DURATION = "vast_video_play_real_duration";
        /** [9.2.0]每次播放视频的时长，进入播放器到退出的时间范围 **/
        String VAST_VIDEO_PLAY_DURATION = "vast_video_play_duration";
        /** [9.2.0] 视频P2P播放的用户数/次数 **/
        String VAST_VIDEO_P2P_PLAY_COUNT = "vast_video_p2p_play_count";
        /** [9.2.0] 视频P2P流畅播放的用户数/次数 **/
        String VAST_VIDEO_P2P_SMOOTH_PLAY_COUNT = "vast_video_p2p_smooth_play_count";
        /** [9.2.0] 视频P2P原画播放的用户数/次数 **/
        String VAST_VIDEO_P2P_ORIGINAL_PLAY_COUNT = "vast_video_p2p_original_play_count";
        /** [10.0.80] 视频P2P 360P播放的用户数/次数 **/
        String VAST_VIDEO_P2P_360P_PLAY_COUNT = "vast_video_p2p_360P_play_count";
        /** [10.0.80] 视频P2P 480P播放的用户数/次数 **/
        String VAST_VIDEO_P2P_480P_PLAY_COUNT = "vast_video_p2p_480P_play_count";
        /** [10.0.80] 视频P2P 720P播放的用户数/次数 **/
        String VAST_VIDEO_P2P_720P_PLAY_COUNT = "vast_video_p2p_720P_play_count";
        /** [10.0.80] 视频P2P 1080P播放的用户数/次数 **/
        String VAST_VIDEO_P2P_1080P_PLAY_COUNT = "vast_video_p2p_1080P_play_count";
        /** [9.2.0] 视频p2p播放失败，重试非p2p播放的用户数/次数 **/
        String VAST_VIDEO_RETRY_PLAY_WITHOUT_P2P = "vast_video_retry_play_without_p2p";

        /** [9.2.0]生成二维码二维码拉取失败的用户数和次数 **/
        String CREATE_QR_CODE_IMG_FAIL = "create_qr_code_img_fail";
        /** [9.2.0]Glide库磁盘缓存使用率**/
        String GLIDE_DISK_CACHE_USAGE = "glide_disk_cache_usage";

        /**
         * [9.2.0] 通知栏开关状态
         */
        String PUSH_NOTIFICATION_SWITCH_STATE = "push_notification_switch_state";

        /** [9.3] 统计每天用户上传失败的次数、用户数，并上报错误信息、失败来源以及文件信息 **/
        String UPLOAD_FAIL_BY_REASON = "upload_fail_by_reason";

        /** [9.3] 统计每天用户上传失败的原因，是否来自备份 **/
        String UPLOAD_FAIL_REASON_BY_BACKUP = "upload_fail_reason_by_backup";

        /** [9.3] 统计每天用户上传失败的原因，md5List 为空 **/
        String UPLOAD_FAIL_BY_MD5LIST_NULL = "upload_fail_by_md5list_null";

        /** [9.5.0] 视频播放中点击切换横竖屏按钮的用户数/次数 **/
        String VAST_VIDEO_CLICK_SWITCH_ORIENTATION = "vast_video_click_switch_orientation";

        /** [9.5.0] 大图预览时点击下载的pv uv **/
        String PREVIEW_IMAGE_CLICK_DOWNLOAD = "preview_image_click_download";

        /** [9.5.0] 大图预览时点击分享的pv uv **/
        String PREVIEW_IMAGE_CLICK_SHARE = "preview_image_click_share";

        /** [9.5.0] 大图预览时点击删除的pv uv **/
        String PREVIEW_IMAGE_CLICK_DELETE = "preview_image_click_delete";

        /** [9.5.1] 分享弹窗展示pv uv **/
        String SHARE_DIALOG_SHOW = "share_dialog_show";
        /** [1.3.0] 分享弹窗取消点击pv uv **/
        String SHARE_DIALOG_CANCEL_CLICK = "share_dialog_cancel_click";
        /** [1.3.0] 分享弹窗日期选择点击前缀pv uv **/
        String SHARE_DIALOG_PERIOD_CLICK_PREFIX = "share_dialog_period_click_prefix_";

        /** [9.6.20] 所有的分享入口点击统计 (之前的分享统计杂乱 在此处统一了) **/
        String SHARE_ENTRANCE_CLICK = "share_entrance_click";
        /** [1.3.0] 文件列表分享点击 **/
        String SHARE_ENTRANCE_FILE_LIST_CLICK = "share_entrance_file_list_click";
        /** [1.3.0] 图片预览分享点击 **/
        String SHARE_ENTRANCE_IMAGE_PREVIEW_CLICK = "share_entrance_image_preview_click";

        /** [9.6.20] 所有的分享发起行为统计 **/
        String SHARE_EVENT_TRIGGER = "share_event_trigger";

        /** [9.6.20] 所有的分享发起行为后失败统计 **/
        String SHARE_EVENT_TRIGGER_ON_FAIL = "share_event_trigger_on_fail";

        /** [9.6.20] 退出照片备份设置页时的来源及开关状态 */
        String ALBUM_BACKUP_FROM_TYPE_AND_IS_OPEN = "album_backup_from_type_and_is_open";

        /** [9.6.20] 引导自动备份通知栏出现的用户数 */
        String ALBUM_BACKUP_HAS_NOTIFICATION = "album_backup_has_notification";

        /** [9.6.20] 引导自动备份通知栏点击的用户数 */
        String ALBUM_BACKUP_NOTIFICATION_CLICK = "album_backup_notification_click";

        /** [9.6.20] 今天/7天/30天出现自动备份引导通知栏的用户数 */
        String ALBUM_BACKUP_DAY_HAS_NOTIFICATION = "album_backup_day_has_notification";

        /** [9.6.30] 传输列表-上传tab-自动备份卡片展现用户数、次数 */
        String BACKUP_ITEM_UPLOAD_PAGE_SHOW_COUNT = "backup_item_upload_page_show_count";

        /** [9.6.30] 传输列表-上传tab-自动备份卡片点击用户数、次数 */
        String BACKUP_ITEM_UPLOAD_PAGE_CLICK_COUNT = "backup_item_upload_page_click_count";

        /** [9.6.30] 传输列表-上传tab-自动备份暂停按钮点击用户数、次数 */
        String BACKUP_ITEM_UPLOAD_PAGE_PAUSE_COUNT = "backup_item_upload_page_pause_count";

        /** [9.6.30] 传输列表-上传tab-自动备份重新开始按钮点击用户数、次数 */
        String BACKUP_ITEM_UPLOAD_PAGE_RESTART_COUNT = "backup_item_upload_page_restart_count";

        /** [9.6.30] 传输列表-上传tab-已备份list页面 pv uv */
        String BACKUP_DETAIL_UPLOAD_PAGE_SHOW_COUNT = "backup_detail_upload_page_show_count";

        /** [9.6.30] 传输列表-上传tab-已备份list-最近备份点击 pv uv */
        String BACKUP_DETAIL_UPLOAD_PAGE_CLICK_COUNT = "backup_detail_upload_page_click_count";

        /** [9.6.30] 传输列表-上传tab-已备份list-清除记录 pv uv */
        String BACKUP_DETAIL_UPLOAD_PAGE_CLEAR_COUNT = "backup_detail_upload_page_clear_count";

        /** [9.6.30] 自动备份错误用户数、文件数 pv uv */
        String BACKUP_DETAIL_UPLOAD_PAGE_DEATIL_COUNT = "backup_detail_upload_page_deatil_count";

        /** [9.6.30] 通知栏-自动备份通知展示用户数 **/
        String DISPLAY_OF_BACKUP_NOTIFICATION = "display_of_backup_notification";

        /** [9.6.30]线程调度耗时统计 **/
        String THREAD_EXCUTE_TIME = "thread_excute_time";

        /** [9.6.40] 分享详情页展示统计 */
        String CHAIN_INFO_PAGE_SHOW = "chain_info_page_show";

        /** [9.6.40] 分享详情页保存点击统计 */
        String CHAIN_INFO_PAGE_SAVE_CLICK = "chain_info_page_save";

        /** [9.6.40] 分享详情页下载点击统计 */
        String CHAIN_INFO_PAGE_DOWNLOAD_CLICK = "chain_info_page_download";

        /** [1.3.0] 分享详情页图片预览 */
        String CHAIN_INFO_PAGE_PREVIEW_IMAGE = "chain_info_page_preview_image";

        /** [1.3.0] 分享详情页视频预览 */
        String CHAIN_INFO_PAGE_PREVIEW_VIDEO = "chain_info_page_preview_video";

        /** [9.6.40] 第三方APP通过content协议调起 Dubox ，文件复制成功失败统计 */
        String FILE_PROVIDER_OPEN_COPY_RESULT = "file_provider_open_copy_result";

        /** [9.6.40] 每日流量情况下[上传选择]提示出现的用户数、次数 */
        String SHOW_UPLOAD_FLOW_ALERT_DIALOG = "show_upload_flow_alert_dialog";

        /** [9.6.40] 每日确认[流量上传]的用户数、次数 */
        String CLICK_CONFIRM_UPLOAD_MOBILE_FLOW = "click_confirm_upload_mobile_flow";

        /** [9.6.40] 每日确认取消]的用户数、次数 */
        String CLICK_CANCEL_UPLOAD_FLOW_ALERT_DIALOG = "click_cancel_upload_flow_alert_dialog";

        /** [9.6.40] -每日确认[等待WIFI下进行上传]的用户数、次数 */
        String CLICK_WAIT_WIFI_FOR_UPLOAD = "click_wait_wifi_for_upload";

        /** [9.6.40] B.每日流量情况下[下载选择]提示出现的用户数、次数 */
        String SHOW_DOWNLOAD_FLOW_ALERT_DIALOG = "show_download_flow_alert_dialog";

        /** [9.6.40] -每日确认[流量下载]的用户数、次数 */
        String CLICK_CONFIRM_DOWNLOAD_MOBILE_FLOW = "click_confirm_download_mobile_flow";

        /** [9.6.40] -每日确认取消]的用户数、次数 */
        String CLICK_CANCEL_DOWNLOAD_FLOW_ALERT_DIALOG = "click_cancel_download_flow_alert_dialog";

        /** [9.6.40] -每日选择[等待WIF下进行下载]的用户数、次数 */
        String CLICK_WAIT_WIFI_FOR_DOWNLOAD = "click_wait_wifi_for_download";

        /** [9.6.40] 每日确认免流量上传的用户数、次数 */
        String CONFIRM_UPLOAD_FOR_ISP = "confirm_upload_for_isp";

        /** [9.6.40] 每日确认免流量下载的用户数、次数 */
        String CONFIRM_DOWNLOAD_FOR_ISP = "confirm_download_for_isp";

        /** [9.6.40] D.每日流量情况下文档[选择预览]提示出现的用户数、次数（>=50M时出现） */
        String SHOW_PREVIEW_FLOW_ALERT_DIALOG = "show_preview_flow_alert_dialog";

        /** [9.6.40] -每日确认[在线预览]的用户数、次数 */
        String CLICK_CONFIRM_PREVIEW_ONLINE = "click_confirm_preview_online";

        /** [9.6.40] -每日确认[立即下载]的用户数、次数 */
        String CLICK_DOWNLOAD_PREVIEW = "click_download_preview";

        /** [9.6.40] -每日确认[取消]的用户数、次数 */
        String CLICK_CANCEL_PREVIEW = "click_cancel_preview";

        /** [9.6.40] C.每日流量情况下视频[播放选择]提示出现的用户数、次数 */
        String SHOW_VIDEO_FLOW_ALERT_DIALOG = "show_video_flow_alert_dialog";

        /** [9.6.40] -每日确认[流量播放]的用户数、次数 */
        String CLICK_PLAY_VIDEO_FLOW_ALERT_DIALOG = "click_play_video_flow_alert_dialog";

        /** [9.6.40] -每日确认[取消]的用户数、次数 */
        String CLICK_CANCEL_VIDEO_FLOW_ALERT_DIALOG = "click_cancel_video_flow_alert_dialog";

        /** [9.6.40] 图片备份页流量备份开关拨动为开的用户数、次数 */
        String CLICK_TO_OPEN_PHOTO_BACKUP_PAGE_USE_PHONE_NETWORK =
                "click_to_open_photo_backup_page_use_phone_network";

        /** [9.6.40] 图片备份页流量备份开关拨动为关的用户数、次数 */
        String CLICK_TO_CLOSE_PHOTO_BACKUP_PAGE_USE_PHONE_NETWORK =
                "click_to_close_photo_backup_page_use_phone_network";

        /** [9.6.40] 视频备份页流量备份开关拨动为开的用户数、次数 */
        String CLICK_TO_OPEN_VIDEO_BACKUP_PAGE_USE_PHONE_NETWORK =
                "click_to_open_video_backup_page_use_phone_network";

        /** [9.6.40] 视频备份页流量备份开关拨动为关的用户数、次数 */
        String CLICK_TO_CLOSE_VIDEO_BACKUP_PAGE_USE_PHONE_NETWORK =
                "click_to_close_video_backup_page_use_phone_network";

        /** [9.6.50]abi列表 */
        String ABI_SUPPORT_LIST = "abi_support_list";

        /** [9.6.50]视频播放默认竖版的 PV、UV */
        String DEFAULT_VERTICAL_SCREEN_PLAY = "default_vertical_screen_play";

        /** [9.6.50]默认竖版播放的视频，用户手动切换为横版的 PV、UV */
        String DEFAULT_VERTICAL_SWITH_HORIZONTAL_SCREEN = "default_vertical_swith_horizontal_screen";

        /** [9.6.50] 相册选择开关拨动为开 uv、pv */
        String ALBUM_CHOOSE_OPEN = "album_choose_open";

        /** [9.6.50] 相册选择开关拨动为关uv、pv（需要去除老版本影响，换个新key） */
        String ALBUM_CHOOSE_CLOSE = "album_choose_close";

        /** [9.6.50] 通过通知栏渠道-开启照片自动备份用户数 */
        String BACKUP_PHOTO_OPEN_FROM_NOTIFICATION = "backup_photo_open_from_notification";

        /**
         * [9.6.50] 外链分享 - 提取文件按钮统计
         */
        String CHAIN_VERIFY_EXTRACTION = "chain_verify_extraction";

        /** [9.6.50] 文件页分类--视频点击uv、pv **/
        String MY_DUBOX_CATEGORY_VIDEO_CLICK = "my_dubox_category_video_click";

        /** [9.6.50] 通过桌面图标icon调起应用*/
        String LAUNCH_APP_FROM_LAUNCHER = "launch_app_from_launcher";

        /** [9.6.50] 通过桌面图标短信(同wap浏览器)调起应用*/
        String LAUNCH_APP_FROM_MESSAGE = "launch_app_from_message";

        /** 通过AppLink启动应用 other1表示外链类型 1:分享  2:wap*/
        String LAUNCH_APP_FROM_APPLINK = "launch_app_from_applink";

        /** [9.6.50] 手动上传页面-上传图片*/
        String MANUAL_ENTRY_UPLOAD_PHOTO = "manual_entry_upload_photo";
        /** [1.2]备份设置引导页展示*/
        String BACKUP_GUIDE_PAGE_DISPLAY = "backup_guide_page_display";
        /** [1.2]备份设置引导页点击备份*/
        String BACKUP_GUIDE_PAGE_OPEN_CLICK = "backup_guide_page_open_click";
        /** [1.2]备份设置引导页点击不备份*/
        String BACKUP_GUIDE_PAGE_CLOSE_CLICK = "backup_guide_page_close_click";
        /** [1.2]再次备份-卡片展现uv/pv*/
        String BACKUP_CARD_DISPLAY = "backup_card_display";
        /** [1.2]再次备份-开启uv/pv*/
        String BACKUP_CARD_OPEN_CLICK = "backup_card_open_click";
        /** [1.2]再次备份-关闭uv/pv*/
        String BACKUP_CARD_CLOSE_CLICK = "backup_card_close_click";

        /** [9.6.50]点击开始备份图片 **/
        String CHOOSE_BACKUP_PHOTO_CLICK = "choose_backup_photo_click";
        /** [9.6.50]点击取消备份图片 **/
        String CANCEL_BACKUP_PHOTO_CLICK = "cancel_backup_photo_click";
        /** [9.6.50] 点击开启图片流量备份开关 **/
        String CHOOSE_BACKUP_PHOTO_USE_INTERNET_CLICK = "choose_backup_photo_use_internet_click";
        /** [9.6.50] 点击关闭图片流量备份开关 **/
        String CANCEL_BACKUP_PHOTO_CLOSE_INTERNET_CLICK = "cancel_backup_photo_close_internet_click";
        /** [9.6.50] 展示图片流量备份弹窗 **/
        String DISPLAY_BACKUP_PHOTO_DIALOG = "display_backup_photo_dialog";
        /** [9.6.50] 展示图片流量备份弹窗 开启流量点击**/
        String DISPLAY_BACKUP_PHOTO_DIALOG_CONFIRM = "display_backup_photo_dialog_confirm";
        /** [9.6.50] 展示图片流量备份弹窗 取消流量点击**/
        String DISPLAY_BACKUP_PHOTO_DIALOG_CANCEL = "display_backup_photo_dialog_cancle";
        /** [9.6.50] 触发自动备份图片成功 **/
        String CHOOOSE_BACKUP_PHOTO_SUCCESS = "choose_backup_photo_success";
        /** [9.6.50] 触发自动备份图片失败 **/
        String CHOOSE_BACKUP_PHOTO_FAIL = "choose_backup_photo_fail";

        /** [9.6.55] 传输列表页面预览视频本地文件不存在的次数 */
        String TRANSFER_FRAGMENT_PREVIEW_VIDEO_FILE_NOT_EXIST = "transfer_fragment_preview_video_file_not_exist";

        /** [9.6.60] IP直连请求（使用HttpDns服务），请求成功 */
        String HTTP_DNS_REQUEST_SUCCESS = "http_dns_request_success";
        /** [9.6.60] 视频服务化所有视频进入视频预览 */
        String VIDEO_SERVICE_ALL_PLAY_VIDEO = "video_service_all_play_video";

        /** [9.6.60] 视频播放页选集列表点击进入视频预览 */
        String VIDEO_PLAYING_PAGE_SELECT_LIST_PLAY_VIDEO= "video_playing_page_select_list_play_video";
        /** [9.6.60] 点击视频播放时数据准备（从数据库中load数据）耗时 */
        String CLOUD_VIDEO_LOAD_SOURCE_TIME = "cloud_video_load_source_time";
        /** [9.6.60] 进入视频播放页，点击选集按钮 */
        String VIDEO_PLAYING_PAGE_CLICK_SELECT_BTN = "video_playing_page_click_select_btn";

        /** [9.6.70] 协议弹窗展示 */
        String AGREEMENT_DIALOG_SHOWN = "agreement_dialog_shown";
        /** [9.6.70] 协议弹窗的『同意』按钮被点击 */
        String AGREEMENT_DIALOG_OK_BTN_CLICKED = "agreement_dialog_ok_btn_clicked";
        /** [9.6.70] 协议弹窗的『不同意退出』按钮被点击 */
        String AGREEMENT_DIALOG_CANCEL_BTN_CLICKED = "agreement_dialog_cancel_btn_clicked";
        /** [9.6.60]  Dubox 启动各阶段耗时 */
        String APP_START_TIME = "app_start_time";
        /** [10.0.00] 分享文件超限 */
        String SHARE_FILE_SIZE_LIMIT = "share_file_size_limit";

        /** [10.0.0] 盘口领复制私密链接 */
        String PAN_CODE_COPY_LINK_TEXT = "pan_code_copy_link_text";

        /** [10.0.40] 进入文件编辑态后选择底部操作栏的【更多】的次数用户数 */
        String AUDIO_EDIT_MORE_CLICK = "audio_edit_more_click";

        /** [10.0.50] 绑定下载服务失败的统计 */
        String P2P_BIND_DOWNLOAD_SERVICE_FAIL = "p2p_bind_download_service_fail";

        /** [10.0.80] 阅读器-选择打开方式-并跳转三方App pv uv（上报App包名） */
        String JUMP_TO_THIRD_PARTY_APP = "jump_to_third_party_app";

        /** [10.0.90] 外链验证码页展示 */
        String CHAIN_VERIFY_PAGE_SHOW = "chain_verify_page_show";
        /** [10.0.90] 外链验详情页展示 */
        String CHAIN_DETAIL_INFO_PAGE_SHOW = "chain_detail_info_page_show";

        /** [10.0.90]视频p2p播放错误原因上报 */
        String VIDEO_PLAY_P2P_START_ERROR = "video_play_p2p_start_error";

        /** [10.0.100] 使用下载券时候的下载速度统计 */
        String BUSINESS_DOWNLOAD_PROBATIONARY_SPEED = "business_download_probationary_speed";

        /** [10.0.100] 每天播放长视频（>10min）的用户数 */
        String VIDEO_PLAY_MORE_THAN_TEN_MINUTES_USER = "video_play_more_than_ten_minutes_user";

        /** [10.0.100] 每天播放长视频（>10min）时长 */
        String VIDEO_PLAY_MORE_THAN_TEN_MINUTES_DURATION = "video_play_more_than_ten_minutes_duration";

        /** [10.0.110] 视频字幕文件被点击 */
        String VIDEO_SUBTITLE_ITEM_CLICKED = "video_subtitle_item_clicked";

        /** [10.0.110] 进入外链分享页时，无提取码 */
        String CHAIN_NO_EXTRACTION_CODE = "chain_no_extraction_code";

        /** [10.0.110] 进入外链分享页时，无验证码 */
        String CHAIN_NO_VERIFY_CODE = "chain_no_verify_code";

        /** [10.0.110] 音频播放-服务化首页-全部音频 **/
        String AUDIO_SOURCE_FROM_ALL_AUDIOS = "audio_source_from_all_audios";

        /** [10.0.120] 相册备份页面点击全选按钮的用户数、次数 **/
        String COMMON_BACKUP_SETTING_SELECT_ALL_CLICK
                = "common_backup_setting_select_all_click";

        /** [10.0.120] 首页下拉刷新出现一刻相册图片的用户数、次数 **/
        String HOMEPAGE_YIKE_BRANCH_SHOW = "homepage_yike_branch_show";

        /** [10.0.120] 首页下拉刷新点击一刻相册图片的用户数、次数 **/
        String HOMEPAGE_YIKE_BRANCH_CLICK = "homepage_yike_branch_click";

        /** [10.0.130] 每日点击切换到4k无损画质成功的次数 **/
        String VIDEO_CHANGE_TO_4K_SUCCEED = "video_change_to_4k_succeed";

        /** [10.0.130] 每日点击切换到2k无损画质成功的次数 **/
        String VIDEO_CHANGE_TO_2K_SUCCEED = "video_change_to_2k_succeed";

        /** [10.0.130] 每日4k无损画质清晰度展示的次数 **/
        String VIDEO_SHOW_4K_RESOLUTION = "video_show_4k_resolution";

        /** [10.0.130] 每日2k无损画质清晰度展示的次数 **/
        String VIDEO_SHOW_2K_RESOLUTION = "video_show_2k_resolution";

        /** 我的tab--文字折行点击 **/
        String CLICK_DISPLAY_LONG_FILE_NAME = "click_display_long_file_name";

        /** 用户反馈 **/
        String CLICK_FEEDBACK = "click_feedback";

        /** 使用流量上传和下载的开关 **/
        String CLICK_USE_4G = "click_use_4g";

        /** 下载路径点击 **/
        String CLICK_DEFAULT_DIR_SETTING = "click_default_dir_setting";

        /** 点击桌面引导 **/
        String CLICK_DESKTOP_WEBSIT = "click_desktop_websit";

        /** 桌面引导展现 **/
        String SHOW_DESKTOP_WEBSIT = "show_desktop_websit";

        /** 上传面板中-文档点击量 */
        String FILELIST_PAGE_ENTRY_UPLOAD_DOC_COUNT = "filelist_page_entry_upload_doc_count";

        /** 单次上传超4G，失败的展现 */
        String UPLOAD_FILE_TOO_LARGE = "upload_file_too_large";

        /**
         * 勾选选定文件后，操作取消按钮的点击量
         */
        String DUBOX_FILE_OPERATE_CANCEL_CLICK = "dubox_file_operate_cancel_click";

        /** [文件操作时批量与单文件]勾选选定文件夹的UV（单选、多选） **/
        String LIST_MULTIPLE_CLICK_DIR = "list_multiple_click_dir";

        /** [10.0.170] 本地搜索无结果 /pv */
        String CLICK_SEARCH = "click_search";

        /** [10.0.170] 本地搜索耗时 /pv */
        String LOCAL_SEARCH_TIME = "local_search_time";

        /** [10.0.170] 本地搜索无结果 /pv */
        String LOCAL_SEARCH_NO_RESULT = "local_search_no_result";

        /** [10.0.170] 本地搜索有结果 /pv */
        String LOCAL_SEARCH_HAS_RESULT = "local_search_has_result";

        /** 海外版  退出googlel的UV **/
        String LOGIN_GOOGLE_ACCOUNT_OUT = "login_google_account_out";

        /** 海外版 第三方应用打开选择保存到文件数 **/
        String OPEN_IN_OTHER_APP_FILE_COUNT = "open_in_other_app_file_count";

        /** 海外版 安全中心展示PV/UV 【1.4.0】 **/
        String SETTING_SECURITY_CENTER_SHOW = "setting_security_center_show";
        /** 海外版 点击开启屏幕锁PV/UV 【1.4.0】 **/
        String OPEN_SCREEN_LOCK_CLICK = "open_screen_lock_click";
        /** 海外版 点击关闭屏幕锁PV/UV 【1.4.0】 **/
        String CLOSE_SCREEN_LOCK_CLICK = "close_screen_lock_click";
        /** 海外版 点击修改密屏幕锁PV/UV 【1.4.0】 **/
        String RESET_SCREEN_LOCK_CLICK = "reset_screen_lock_click";
        /** 海外版 闲时备份调起PV/UV 【1.4.0】 **/
        String IDLE_BACKUP_INVOKE_SHOW = "idle_backup_invoke_show";
        /** 海外版 闲时备份调起并成功备份PV/UV 【1.4.0】 **/
        String IDLE_BACKUP_INVOKE_SUCCESS = "idle_backup_invoke_success";


        /** [10.0.150] 自有文档阅读器打开文档大小 */
        String DOCUMENT_PAGE_OPEN_SIZE = "document_page_open_size";

        /** [10.0.150] 预览转码时长 */
        String DOCUMENT_PAGE_OPEN_TIME = "document_page_open_time";

        /** [10.0.150] 预览成功后用户的阅读时长 */
        String DOCUMENT_PAGE_READ_TIME = "document_page_read_time";

        /** [10.0.150] 预览成功页，翻页阅读拖动右侧进度条的的用户数和次数 */
        String DOCUMENT_PAGE_SLIDE_MODE_SEEK = "document_page_slide_mode_seek";

        /** [10.0.150] 预览成功页-长按屏幕的用户数和次数 */
        String DOCUMENT_PAGE_LONG_PRESS = "document_page_long_press";

        /** [10.0.150] 横屏（手机横屏观看）的用户数和文档数 */
        String DOCUMENT_PAGE_ORIENTATION = "document_page_orientation";

        /** [10.0.150] excel工作表的sheet切换点击的用户数和次数 */
        String DOCUMENT_PAGE_EXCEL_TAB = "document_page_excel_tab";

        /** [10.0.150] 预览下载成功 **/
        String PREVIEW_OPEN_FILE_SUCCESS = "preview_open_file_success";

        /** [10.0.150] 预览下载成功耗时 **/
        String PREVIEW_OPEN_FILE_SUCCESS_TIME = "preview_open_file_success_time";

        /** [10.0.150] 预览下载失败具体原因 **/
        String PREVIEW_OPEN_FILE_FAIL_DETAIL = "preview_open_file_fail_detail";

        /** [10.0.150] 自有文档阅读器打开的用户数和文档数 */
        String DOCUMENT_PAGE_OPEN_SUCCEED = "document_page_open_succeed";

        /** [10.0.160] 预览转码时长 */
        String DOCUMENT_PAGE_OPEN_CANCEL = "document_page_open_cancel";

        /** [10.0.150] 自有文档阅读器打开失败的用户数和文档数 */
        String DOCUMENT_PAGE_OPEN_FAILED = "document_page_open_failed";

        /** [10.0.150] 自有文档阅读器打开失败的具体详情 */
        String DOCUMENT_PAGE_OPEN_FAILED_DETAIL = "document_page_open_failed_detail";

        /** [10.1] 自有文档阅读器打开失败的错误码上报 */
        String DOCUMENT_PAGE_OPEN_FAILED_ERR_CODE = "document_page_open_failed_err_code";

        /** [10.0.170] PDF最后一页操作入口展示UV/PV **/
        String DOCUMENT_END_TIP_SHOW = "document_end_tip_show";

        /** [10.0.170] PDF最后一页操作入口点击UV/PV */
        String DOCUMENT_END_TIP_CLICK = "document_end_tip_click";

        /** [10.0.170] 目录入口展示pv/uv */
        String DOCUMENT_PAGE_BOOKMARK_SHOW = "document_page_bookmark_show";
        /** [10.0.180]pdf阅读器展示pv、uv **/
        String PDF_DOCUMENT_SHOW = "pdf_document_show";

        /**
         * 上报用户安装来源
         */
        String APPSFLYER_MEDIA_SOURCE  = "appsflyer_media_source_new";
        String ABTEST_CONFIG_INFO  = "abtest_config_info";
        /**
         * 上报af接口返回错误原因
         */
        String APPSFLYER_CALL_BACK_ERROR_MSG = "appsflyer_call_back_error_msg";

        /**
         * 上报af uid
         */
        String APPSFLYER_UID = "appsflyer_uid";

        /**
         * 点击外链唤起应用
         */
        String APP_LAUNCH_FROM_WRAP = "launch_from_wrap";

        /** 文件备份日活 **/
        String REPORT_USER_BACKGROUND_FILE_BACK_UP = "REPORT_USER_BACKGROUND_FILE_BACK_UP";

        /* ***************文件备份**************************************************** */
        /**
         * [5.3]每日开启自定义文件备份的用户数/次数
         */
        String FILE_BACK_UP_TURN_ON = "FILE_BACK_UP_TURN_ON";
        /**
         * [5.3]每日关闭自定义文件备份的用户数/次数
         */
        String FILE_BACK_UP_TURN_OFF = "FILE_BACK_UP_TURN_OFF";
        /**
         * [5.3]每日触发自定义文件备份的用户数/文件数
         */
        String FILE_BACK_UP_START = "FILE_BACK_UP_START";
        /**
         * [5.3]每日文件备份成功的用户数/文件数
         */
        String FILE_BACK_UP_SUCCESS = "FILE_BACK_UP_SUCCESS";
        /**
         * [5.3]每日文件备份失败的用户数/文件数
         */
        String FILE_BACK_UP_FAIL = "FILE_BACK_UP_FAIL";

        /** [10.0.140] 每日进行自动备份的用户数 */
        String FILE_BACKUP_COUNT = "file_backup_count";

        /** [10.0.140] 每日进行自动备份图片的用户数 */
        String FILE_BACKUP_IMAGE_COUNT = "file_backup_image_count";

        /** [10.0.140] 每日进行自动备份视频的用户数 */
        String FILE_BACKUP_VIDEO_COUNT = "file_backup_video_count";

        /** [10.0.140] 每日进行自动备份文档的用户数 */
        String FILE_BACKUP_DOC_COUNT = "file_backup_doc_count";

        /** [10.0.140] 每日进行自动备份压缩包的用户数 */
        String FILE_BACKUP_ZIP_COUNT = "file_backup_zip_count";

        /** [10.0.140] 每日进行自动备份其他文件的用户数 */
        String FILE_BACKUP_OTHER_COUNT = "file_backup_other_count";

        /* ***************文件备份**************************************************** */

        /** 海外版 安全模式统计 [2.21] */
        String DUBOX_SAFE_MODE_CRASH = "dubox_safe_mode_crash";

        /** 海外版 线程池超限统计 [2.21] */
        String DUBOX_THREAD_POOL_REJECTED = "dubox_thread_pool_rejected";

        /** 海外版 野线程执行次数统计 [2.21] */
        String DUBOX_WILD_THREAD_START = "dubox_wild_thread_start";

        /** 海外版 线程安全模式展示统计 [2.21] */
        String DUBOX_SAFE_MODE_CRASH_SHOW = "dubox_safe_mode_crash_show";

    }
}
