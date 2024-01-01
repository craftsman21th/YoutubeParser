package com.moder.compass.statistics;

import com.moder.compass.BaseApplication;
import com.dubox.drive.db.cloudfile.model.FileCategory;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.stats.DuboxStatsEngine;
import com.moder.compass.stats.StatisticsType;

/**
 * @author chenyuquan
 */
public class StatisticsLog {
    private static final String TAG = "DuboxStatisticsLog";

    /**
     * 统计相关的所有KEY声明 <br>
     * com.dubox.drive.util.StatisticsKeys
     *
     * @author 魏铮铮 <br/>
     * create at 2012-11-28 下午5:04:45
     */
    public interface StatisticsKeys {
        /* 上传相关 */
        /**
         * 上传文件总数
         **/
        String TOTAL_UPLOAD = "fileupload_all";
        /**
         * 上传成功文件数
         **/
        String TOTAL_UPLOAD_SUCCUSS = "fileupload_succuss";
        /**
         * 上传失败文件数
         **/
        String TOTAL_UPLOAD_FAILED = "fileupload_error";
        /**
         * 用户删除上传任务
         **/
        String UPLOAD_FAILED_USER_CANCEL = "upload_failed_user_cancel";
        /**
         * [7.0]自动上传文件总数
         **/
        String TOTAL_AUTO_UPLOAD = "auto_upload_all";
        /**
         * [3.9]自动上传成功的文件数（相册备份的文件数包含图片和视频）
         **/
        String AUTO_UPLOAD_SUCCUSS = "auto_upload_succuss";
        /**
         * [3.9]自动上传失败的文件数（相册备份的文件数包含图片和视频）
         **/
        String AUTO_UPLOAD_FAILED = "auto_upload_failed";

        /**
         * [3.9]手动上传文件分类型统计（图片文件数）
         **/
        String UPLOAD_FILE_TYPE_IMAGE = "upload_file_type_image";
        /**
         * [3.9]手动上传文件分类型统计（视频文件数）
         **/
        String UPLOAD_FILE_TYPE_VIDEO = "upload_file_type_video";
        /**
         * [3.9]手动上传文件分类型统计（音频文件数）
         **/
        String UPLOAD_FILE_TYPE_AUDIO = "upload_file_type_audio";
        /**
         * [3.9]手动上传文件分类型统计（文档文件数）
         **/
        String UPLOAD_FILE_TYPE_DOC = "upload_file_type_doc";
        /**
         * [3.9]手动上传文件分类型统计（bt文件数）
         **/
        String UPLOAD_FILE_TYPE_BT = "upload_file_type_bt";
        /**
         * [3.9]手动上传文件分类型统计（应用文件数）
         **/
        String UPLOAD_FILE_TYPE_APP = "upload_file_type_app";
        /**
         * [3.9]手动上传文件分类型统计（其他文件数）
         **/
        String UPLOAD_FILE_TYPE_OTHER = "upload_file_type_other";

        /**
         * 总的活跃次数
         **/
        String APP_ALL_ACTIVE = "app_all_active";
        /**
         * 前台活跃次数
         **/
        String APP_ACTIVE = "app_active";
        /**
         * 后台有活动并完成 次数
         **/
        String APP_BACKSTAGE_ACTIVE = "app_backstage_active";

        /**
         * 后台上传下载队列完成次数
         **/
        String BACK_UPLOAD_AND_DOWN_FINISH_TIMES = "back_upload_and_down_finish_times";

        /**
         * 页面刷新总数
         **/
        String TOTAL_REFRESH = "apiget_all";
        /** -----------上传相关的统计--------------------------- **/

        /**
         * 上传失败：文件不存在
         **/
        String UPLOAD_FAILED_FILE_NOT_EXIST = "upload_failed_file_not_exist";
        /**
         * 上传失败：相册备份时文件不存在
         **/
        String UPLOAD_FAILED_FILE_NOT_EXIST_DCIM = "upload_failed_file_not_exist_dcim";

        /**
         * 上传失败：网络原因
         **/
        String UPLOAD_FAILED_NETWORK_ERROR = "upload_failed_network_error";
        /**
         * 上传失败：网络原因：wifi
         **/
        String UPLOAD_FAILED_NETWORK_ERROR_WIFI = "upload_failed_network_error_wifi";
        /**
         * 上传失败：网络原因：2G/3G
         **/
        String UPLOAD_FAILED_NETWORK_ERROR_2G3G = "upload_failed_network_error_2g3g";

        /**
         * 上传失败：precreate 失败
         **/
        String UPLOAD_FAILED_PRECREATE_ERROR = "upload_failed_precreate_error";
        /**
         * 上传失败：pcs服务器错误
         **/
        String UPLOAD_FAILED_PCS_ERROR = "upload_failed_pcs_error";
        /**
         * 上传失败：create 失败
         **/
        String UPLOAD_FAILED_CREATE_ERROR = "upload_failed_create_error";

        /**
         * 上传失败：空间不足
         **/
        String UPLOAD_FAILED_SPACE_FULL = "upload_failed_space_full";
        /**
         * 上传失败：客户端的其他错误
         **/
        String UPLOAD_FAILED_OTHER = "upload_failed_other";

        /**
         * 行为统计：在上传列表打开文件的个数
         **/
        String TOTAL_OPEN_UPLOAD_FILE = "fileupload_open_file";
        /**
         * 行为统计：有上传行为的次数，统计用户点击上传按钮的次数
         **/
        String TOTAL_UPLOAD_TIMES = "fileupload_times";

        /** ------下载相关的统计------------------ **/

        /**
         * [3.9]单独下载文件数
         **/
        String TOTAL_QUEUE_DOWNLOAD = "total_queue_download";
        /**
         * [3.9]单独下载成功文件数
         **/
        String TOTAL_QUEUE_DOWNLOAD_SUCCUSS = "total_queue_download_succuss";
        /**
         * [3.9]预览下载文件数
         **/
        String TOTAL_PREVIEW_DOWNLOAD = "total_preview_download";
        /**
         * [3.9]预览下载成功文件数
         **/
        String TOTAL_PREVIEW_DOWNLOAD_SUCCUSS = "total_preview_download_succuss";

        /**
         * 下载总数
         **/
        String TOTAL_DOWNLOAD = "filedownload_all";
        /**
         * 下载成功数
         **/
        String TOTAL_DOWNLOAD_SUCCUSS = "filedownload_succuss";
        /**
         * 下载失败的总数
         **/
        String TOTAL_DOWNLOAD_ERROR = "filedownload_error";
        /**
         * 因用户SDcard 空间不足下载失败数
         **/
        String TOTAL_DOWNLOAD_SPACE_FULL = "filedownload_error_space_full";
        /**
         * 用户取消下载数
         **/
        String TOTAL_DOWNLOAD_USER_CANCEL = "filedownload_error_user_cancel";
        /**
         * 因网络错误下载失败的次数/用户数
         **/
        String DOWNLOAD_FAILED_NETWORK_ERROR = "filedownload_error_network_error";
        /**
         * 因wifi链接超时上传失败的次数/用户数
         **/
        String DOWNLOAD_FAILED_NETWORK_ERROR_WIFI = "filedownload_error_network_error_wifi";
        /**
         * 因WWAN链接上传失败的次数/用户数
         **/
        String DOWNLOAD_FAILED_NETWORK_ERROR_2G3G = "filedownload_error_network_error_2g3g";
        /**
         * 因源文件不存在下载失败的次数/用户数
         **/
        String DOWNLOAD_FAILED_FILE_NOT_EXISTS = "filedownload_error_file_not_exists";
        /**
         * 因服务器错误下载失败的次数/用户数
         **/
        String DOWNLOAD_FAILED_SERVER_ERROR = "filedownload_error_server_error";

        /**
         * 行为统计：在下载列表打开文件的个数
         **/
        String TOTAL_OPEN_DOWNLOAD_FILE = "filedownload_open_file";
        /**
         * 行为统计：更改路径的次数
         **/
        String TOTAL_CHANGE_PATH = "fileupload_change_path";
        /**
         * 行为统计：选择新建文件夹的个数
         **/
        String TOTAL_CREATE_FOLDER = "filecreate_folder";
        /**
         * 行为统计：点击全部暂停的个数
         **/
        String TOTAL_PAUSE_ALL = "filepause_all";
        /**
         * 行为统计：点击全部暂停的个数 下载
         **/
        String TOTAL_PAUSE_DOWNLOAD_ALL = "total_pause_download_all";
        /**
         * 行为统计：点击全部暂停的个数 上传
         **/
        String TOTAL_PAUSE_UPLOAD_ALL = "total_pause_upload_all";

        /**
         * 行为统计：在搜索页面中点击搜索的次数
         **/
        String TOTAL_SEARCH = "filesearch_all";

        /**
         * 非wifi环境下自愿传输的人数
         **/
        String ACCEPT_TRANSMIT_NOTWIFI = "accept_notwifi";
        /**
         * 非wifi环境下不愿传输的人数
         **/
        String REJECT_TRANSMIT_NOTWIFI = "reject_notwifi";

        /* ***********PIM***************************************************************** */

        /**
         * pim成功次数
         **/
        String TOTAL_PIM_SUCCESS = "pim_success_all";
        /**
         * pim同步失败的次数
         **/
        String TOTAL_PIM_FAILED = "pim_failed_all";
        /**
         * pim同步的总数
         **/
        String TOTAL_PIM_NUM = "pim_num_all";
        /* ***********PIM***************************************************************** */

        /**
         * 获取列表使用的网络
         **/
        String GET_NETWORK = "apigetlist_net_CNNIC";

        /** -----文件预览相关的统计 ------------------------------- **/
        /**
         * 预览视频文件的次数
         **/
        String OPEN_VIDEO_FILE = "open_video_file";
        /**
         * 预览文档文件的次数
         **/
        String OPEN_DOC_FILE = "open_doc_file";
        /**
         * 预览音频文件的次数
         **/
        String OPEN_AUDIO_FILE = "open_audio_file";
        /**
         * 预览图片文件的次数
         **/
        String OPEN_IMAGE_FILE = "open_image_file";
        /**
         * 预览应用文件的次数
         **/
        String OPEN_APP_FILE = "open_app_file";
        /**
         * [3.9]预览压缩文件的次数
         **/
        String OPEN_ZIP_FILE = "open_zip_file";
        /**
         * [3.9]预览其他文件的次数
         **/
        String OPEN_OTHER_FILE = "open_other_file";

        /** -----分享功能相关的统计 ------------------------------- **/
        /**
         * 行为统计：复制外链的次数
         **/
        String SHARE_BY_LINK = "share_by_link";
        /**
         * 行为统计：通过其他应用分享的次数
         **/
        String SHARE_BY_OTHER = "share_by_other";

        /**
         * [3.9调整] 分享应用文件的个数
         **/
        String SHARE_APP_FILE = "share_app_file";
        /**
         * [3.9调整] 分享视频文件的个数
         **/
        String SHARE_VIDEO_FILE = "share_video_file";
        /**
         * [3.9调整] 分享文档文件的个数
         **/
        String SHARE_DOC_FILE = "share_doc_file";
        /**
         * [3.9调整] 分享音频文件的个数
         **/
        String SHARE_AUDIO_FILE = "share_audio_file";
        /**
         * [3.9调整] 分享图片文件的个数
         **/
        String SHARE_IMAGE_FILE = "share_image_file";
        /**
         * [3.9] 分享压缩文件的个数
         **/
        String SHARE_ZIP_FILE = "share_zip_file";

        /**
         * [3.9] 从图片页面分享的次数
         **/
        String SHARE_FROM_PIC = "share_from_pic";
        /**
         * [3.9] 从视频页面分享的次数
         **/
        String SHARE_FROM_VIDEO = "share_from_video";

        /**
         * 行为统计：每日在视频播放界面点击删除的次数
         **/
        String VIDEO_CLICK_DELETE = "video_click_delete";

        /**
         * 行为统计：点击注销的次数
         **/
        String LOGOUT_COUNT = "logout_count";

        /**
         * 上传文件时自动忽略传输的次数
         **/
        String UPLOAD_FILE_IGNORE = "upload_file_ignore";
        /**
         * 下载文件时自动忽略传输的次数
         **/
        String DOWNLOAD_FILE_IGNORE = "download_file_ignore";
        /**
         * 下载文件时自动创建备份文件的次数
         **/
        String DOWNLOAD_FILE_CREATE_BACKUP_FAILE = "download_file_create_backup_faile";

        /* **************相册备份************************************************************ */
        /**
         * 打开设置相册备份的统计
         **/
        String CHOOSE_ALBUM_BACKUP = "choose_album_backup";
        /**
         * 行为统计：用户在设置界面取消自动备份照片的次数
         **/
        String CANCEL_BACKUP_PHOTO = "cancel_backup_photo";
        /**
         * 行为统计：用户在设置界面选择自动备份照片的次数
         **/
        String CHOOSE_BACKUP_PHOTO = "choose_backup_photo";
        /**
         * 每天使用相册备份上传成功文件数
         **/
        String ALBUM_BACKUP_SUCCESS = "album_backup_success";
        /**
         * 每天使用相册备份上传失败文件数
         **/
        String ALBUM_BACKUP_FAILED = "album_backup_failed";

        /**
         * [3.8]设置页面开启密码锁的次数
         **/
        String OPEN_CODE_LOCK = "open_code_lock";
        /**
         * [3.8]设置页面关闭密码锁的次数
         **/
        String CLOSE_CODE_LOCK = "close_code_lock";
        /**
         * [3.8]用户使用密码锁的次数（有输入行为）
         **/
        String TOTAL_CODE_LOCK = "total_code_lock";

        /**
         * 行为统计：统计首页中分类列表点击事件 图片
         **/
        String CLICK_CATEGORY_IMAGE = "click_category_image";
        /**
         * 行为统计：统计首页中分类列表点击事件 文档
         **/
        String CLICK_CATEGORY_DOCUMENT = "click_category_document";
        /**
         * 行为统计：统计首页中分类列表点击事件 应用
         **/
        String CLICK_CATEGORY_APPLICATION = "click_category_application";
        /**
         * 行为统计：统计首页中分类列表点击事件 音乐
         **/
        String CLICK_CATEGORY_AUDIO = "click_category_audio";
        /**
         * 行为统计：统计首页中分类列表点击事件 视频
         **/
        String CLICK_CATEGORY_VIDEO = "click_category_video";
        /**
         * 行为统计：统计首页中分类列表点击事件 其他
         **/
        String CLICK_CATEGORY_OTHER = "click_category_other";
        /**
         * 行为统计：统计首页中分类列表点击事件 BT
         **/
        String CLICK_CATEGORY_BT = "click_category_bt";
        /** 3.8新添加接口接口统计 日活跃统计和APP激活统计 YQH 20130227 **/
        /**
         * 前台
         **/
        String REPORT_USER = "ANDROID_ACTIVE_FRONTDESK";
        /**
         * 后台相册备份活跃 YQH 20130227
         **/
        String REPORT_USER_BACKGROUND_ALBUMBACKUP = "ANDROID_ACTIVE_BACKGROUND_ALBUMBACKUP";
        /**
         * 后台上传下载活跃 YQH 20130227
         **/
        String REPORT_USER_BACKGROUND_UPLOAD_AND_DOWNLOAD =
            "ANDROID_ACTIVE_BACKGROUND_UPLOAD_AND_DOWNLOAD";
        /**
         * 上传活跃 wanghelong 20180710
         **/
        String REPORT_USER_BACKGROUND_UPLOAD =
            "ANDROID_ACTIVE_BACKGROUND_UPLOAD";
        /**
         * 下载活跃 wanghelong 20180710
         **/
        String REPORT_USER_BACKGROUND_DOWNLOAD =
            "ANDROID_ACTIVE_BACKGROUND_DOWNLOAD";
        /**
         * 注销时日活
         **/
        String REPORT_USER_LOGOUT = "ANDROID_ACTIVE_LOGOUT";

        /**
         * [3.9] 删除的次数
         **/
        String DELETE_TIMES = "delete_times";
        /**
         * [3.9] 删除成功的文件数
         **/
        String DELETE_FILES = "delete_files";

        /**
         * [3.10] 移动文件成功的次数
         **/
        String MOVE_FILE_SUCCESS = "move_file_success";

        /**
         * [3.10] 新建文件夹成功 的次数(首页，上传，移动)
         **/
        String TOTAL_CREATE_FOLDER_SUCCESS = "total_create_folder_success";

        /**
         * [3.10] 首页新建文件夹成功 的次数
         **/
        String MENU_CREATE_FOLDER_SUCCESS = "menu_create_folder_success";

        /**
         * [3.10] 上传新建文件夹成功 的次数
         **/
        String UPLOAD_CREATE_FOLDER_SUCCESS = "upload_create_folder_success";

        /**
         * [3.10] 移动新建文件夹成功 的次数
         **/
        String MOVE_CREATE_FOLDER_SUCCESS = "move_create_folder_success";

        /**
         * [3.10] 行为统计，从“来自XX”移动文件次数
         **/
        String MOVE_FROM_SPECIAL_FOLDERS = "move_from_special_folders";

        /**
         * [3.10] 重命名文件成功次数
         **/
        String RENAME_FILE_SUCCESS = "rename_file_success";

        /** ----------------DT平台日活统计------------------- **/

        /**
         * 当日有上传文件行为的 Dubox 用户数去重统计
         **/
        String DT_UPLOADFILES = "DTUploadFiles";

        /**
         * 当日有自动上传行为的 Dubox 用户数去重统计
         **/
        String DT_UPLOADFILESAUTO = "DTUploadFilesAuto";

        /**
         * 当日有在线预览图片行为的 Dubox 用户数去重统计
         **/
        String DT_IMAGEPREVIEW = "DTImagePreview";

        /**
         * 当日有在线预览视频行为的 Dubox 用户数去重统计
         **/
        String DT_VIDEOPREVIEW = "DTVideoPreview";

        /**
         * 当日有在线预览文档行为的 Dubox 用户数去重统计
         **/
        String DT_DOCPREVIEW = "DTDocPreview";

        /**
         * 当日有在线预览音乐行为的 Dubox 用户数去重统计
         **/
        String DT_MUSICPREVIEW = "DTMusicPreview";

        /**
         * 当日有过下载行为的 Dubox 用户数去重统计
         **/
        String DT_DOWNLOADFILES = "DTDownloadFiles";

        /** --------------------- [3.11] ---------------------- **/

        /**
         * [5.0]每日使用下拉刷新的次数
         **/
        String USE_PULL_REFRESH = "use_pull_refresh";

        /**
         * 每日在选择文件下载界面点击【下载】的用户数
         **/
        String SHARE_LINK_DOWNLOAD = "mtj_w_04";
        /**
         * [5.1]下载文件夹的次数/用户数
         **/
        String DOWNLOAD_DIR = "download_dir";

    }

    /**
     * 统计因网络原因导致的上传失败
     */
    public static void countUploadFailedByNetworkError() {
        updateCount(StatisticsKeys.UPLOAD_FAILED_NETWORK_ERROR);
        String type = ConnectivityState.getNetWorkType(BaseApplication.getInstance());
        if (type == null) {
            return;
        }
        if ("wifi".equalsIgnoreCase(type)) {
            updateCount(StatisticsKeys.UPLOAD_FAILED_NETWORK_ERROR_WIFI);
        } else {
            updateCount(StatisticsKeys.UPLOAD_FAILED_NETWORK_ERROR_2G3G);
        }
    }

    /**
     * 统计因网络原因导致的下载失败
     */
    public static void countDownloadFailedByNetworkError() {
        updateCount(StatisticsKeys.DOWNLOAD_FAILED_NETWORK_ERROR);
        String type = ConnectivityState.getNetWorkType(BaseApplication.getInstance());
        if (type == null) {
            return;
        }
        if ("wifi".equalsIgnoreCase(type)) {
            updateCount(StatisticsKeys.DOWNLOAD_FAILED_NETWORK_ERROR_WIFI);
        } else {
            updateCount(StatisticsKeys.DOWNLOAD_FAILED_NETWORK_ERROR_2G3G);
        }
    }

    /**
     * 统计首页中分类列表点击事件
     *
     * @param category
     */
    public static void countSpinnerItemClick(int category) {
        switch (category) {
            case FileCategory.CATEGORY_IMAGE:
                updateCount(StatisticsKeys.CLICK_CATEGORY_IMAGE);
                EventStatisticsKt.statisticDeprecatedEvent(StatisticsKeys.CLICK_CATEGORY_IMAGE);
                break;
            case FileCategory.CATEGORY_DOCUMENT:
                updateCount(StatisticsKeys.CLICK_CATEGORY_DOCUMENT);
                EventStatisticsKt.statisticDeprecatedEvent(StatisticsKeys.CLICK_CATEGORY_DOCUMENT);
                break;
            case FileCategory.CATEGORY_APPLICATION:
                updateCount(StatisticsKeys.CLICK_CATEGORY_APPLICATION);
                break;
            case FileCategory.CATEGORY_AUDIO:
                updateCount(StatisticsKeys.CLICK_CATEGORY_AUDIO);
                EventStatisticsKt.statisticDeprecatedEvent(StatisticsKeys.CLICK_CATEGORY_AUDIO);
                break;
            case FileCategory.CATEGORY_VIDEO:
                updateCount(StatisticsKeys.CLICK_CATEGORY_VIDEO);
                EventStatisticsKt.statisticDeprecatedEvent(StatisticsKeys.CLICK_CATEGORY_VIDEO);
                break;
            case FileCategory.CATEGORY_OTHER:
                updateCount(StatisticsKeys.CLICK_CATEGORY_OTHER);
                EventStatisticsKt.statisticDeprecatedEvent(StatisticsKeys.CLICK_CATEGORY_OTHER);
                break;
            case FileCategory.CATEGORY_BT:
                updateCount(StatisticsKeys.CLICK_CATEGORY_BT);
                break;
            default:
                break;
        }
    }

    /**
     * 统计预览类型相关
     *
     * @param path
     */
    public static void countPerview(String path) {

        if (FileType.isMusic(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.OPEN_AUDIO_FILE);
            // DT平台当日在线预览音乐的用户数
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_MUSICPREVIEW);
        } else if (FileType.isDoc(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.OPEN_DOC_FILE);
            // DT平台当日在线预览文档的用户数
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_DOCPREVIEW);
        } else if (FileType.isVideo(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.OPEN_VIDEO_FILE);
            // DT平台当日在线预览视频的用户数
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.DT_VIDEOPREVIEW);
        } else if (FileType.isApp(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.OPEN_APP_FILE);
        } else if (FileType.isZipFile(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.OPEN_ZIP_FILE);
        } else {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.OPEN_OTHER_FILE);
        }
    }

    /**
     * 统计分享文件类型
     *
     * @param shareFile
     */
    public static void countShareFileType(String shareFile) {
        if (FileType.isMusic(shareFile)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.SHARE_AUDIO_FILE);
        } else if (FileType.isDoc(shareFile)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.SHARE_DOC_FILE);
        } else if (FileType.isVideo(shareFile)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.SHARE_VIDEO_FILE);
        } else if (FileType.isApp(shareFile)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.SHARE_APP_FILE);
        } else if (FileType.isImage(shareFile)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.SHARE_IMAGE_FILE);
        } else if (FileType.isZipFile(shareFile)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.SHARE_ZIP_FILE);
        }
    }

    public static void updateCount(String key) {
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.OLD).statCount(key);
    }

    /**
     * @param key
     * @param addCount
     */
    public static void updateCount(String key, int addCount) {
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.OLD).statCount(key, addCount);
    }

    /**
     * @param key
     * @param content
     */
    public static void updateContent(String key, String content) {
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.OLD).statCount(key, content);
    }

    /**
     * 统计手动上传成功的文件类型
     *
     * @param path
     */
    public static void countUploadFileType(String path) {
        if (FileType.isMusic(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_TYPE_AUDIO);
        } else if (FileType.isDoc(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_TYPE_DOC);
        } else if (FileType.isVideo(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_TYPE_VIDEO);
        } else if (FileType.isImage(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_TYPE_IMAGE);
        } else if (FileType.isApp(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_TYPE_APP);
        } else if (FileType.isBT(path)) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_TYPE_BT);
        } else {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.UPLOAD_FILE_TYPE_OTHER);
        }
    }

    /**
     * 通过 Dubox 移动统计进行相应的统计
     *
     * @param key 统计项名称
     */
    public static void countByMobileCount(String key) {
        DuboxLog.d(TAG, "移动统计计数{" + System.currentTimeMillis() + "}：" + key);
        DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.MTJ).statCount(key);
    }

}
