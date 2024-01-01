
package com.moder.compass.util;

import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.cloudfile.constant.CloudFileConstants;
import com.dubox.drive.cloudfile.io.model.CloudFile;
import com.dubox.drive.basemodule.R;
import com.dubox.drive.cloudfile.sharedirectory.provider.ShareDirectoryContract;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tianzengming on 2015/2/3.
 */
public class CloudFileHelper {
    private static final String TAG = "CloudFileHelper";
    public static final String DEST_STR_ALBUM_FILENAME = CloudFileConstants.DEST_STR_ROOTDIR + "apps/album";
    /** "我的资源" 目录 **/
    public static final String DEST_STR_RESOURCEDIR = CloudFileConstants.DEST_STR_ROOTDIR + "\u6211\u7684\u8d44\u6e90";
    /** 网盘转存根目录 */
    public static final String PATH_DUBOX_SAVE_ROOT = CloudFileConstants.DEST_STR_ROOTDIR;
    /** “我的音乐”目录 **/
    public static final String DEST_STR_MUSICDIR = CloudFileConstants.DEST_STR_ROOTDIR + "\u6211\u7684\u97f3\u4e50";
    /** “我的文档”目录 **/
    public static final String DEST_STR_DOCDIR = CloudFileConstants.DEST_STR_ROOTDIR + "\u6211\u7684\u6587\u6863";
    /** “我的视频”目录 **/
    public static final String DEST_STR_VIDEODIR = CloudFileConstants.DEST_STR_ROOTDIR + "\u6211\u7684\u89c6\u9891";
    /** “我的照片”目录 **/
    public static final String DEST_STR_IMAGEDIR = CloudFileConstants.DEST_STR_ROOTDIR + "\u6211\u7684\u7167\u7247";
    public static final String MY_APPS_DATA_FILENAME = "apps";
    public static final String DEST_STR_MY_APP_DATA_DIR = CloudFileConstants.DEST_STR_ROOTDIR + MY_APPS_DATA_FILENAME;
    public static final String ALBUM_FILENAME = "album";
    /** 预览缓存文件夹名称 **/
    public static final String PREVIEW_CACHE_PATH = "preview";

    /**
     * 推荐应用下载的缓存目录
     *
     * @author liulp
     * @since 7.11
     */
    public static final String RECOMMEND_DOWNLOAD_CACHE_PATH = "recommend";
    /**
     * Whatsapp转存的文件路径
     */
    public static final String WHATS_APP_FILE_DEFAULT_PATH = "/From：WhatsApp Status";

    /**
     * 转存和离线下载的目标路径
     */
    public static final String DEST_STR_REMOTE_UPLOAD_DIR = "/From：Remote Upload";

    /**
     * Video Downloader
     */
    public static final String DEST_STR_VIDEO_DOWNLOADER_DIR = "/From：Video Downloader";
    /**
     *  流畅播
     */
    public static final String DEST_STR_SMOOTH_PLAY_DIR = "/From：Smooth Playback";

    /*
     * set Rice Center Label according to different path(directory, folder) apps目录改成我的应用数据
     */
    public static String getTitleName(String currentPath) {
        if (TextUtils.isEmpty(currentPath)) {
            return BaseApplication.getInstance().getResources().getString(R.string.type_all);
        }
        int idx = currentPath.lastIndexOf('/');
        if (idx == 0 && currentPath.length() == 1) {
            return BaseApplication.getInstance().getResources().getString(R.string.type_all);
        } else {
            if (DEST_STR_MY_APP_DATA_DIR.equals(currentPath)) {
                return BaseApplication.getInstance().getResources().getString(R.string.my_app_data);
            } else if (DEST_STR_ALBUM_FILENAME.equals(currentPath)) {
                return BaseApplication.getInstance().getResources().getString(R.string.my_app_album);
            }

            return currentPath.substring(idx + 1, currentPath.length());
        }
    }

    public static String getTitleName(String currentPath, String fileName) {
        if (DEST_STR_MY_APP_DATA_DIR.equals(currentPath) && MY_APPS_DATA_FILENAME.equals(fileName)) {
            fileName = BaseApplication.getInstance().getResources().getString(R.string.my_app_data);
        } else if (DEST_STR_ALBUM_FILENAME.equals(currentPath) && ALBUM_FILENAME.equals(fileName)) {
            fileName = BaseApplication.getInstance().getResources().getString(R.string.my_app_album);
        }
        return fileName;
    }

    /**
     * 提供文件在列表中的图标
     *
     * @param filename            文件名
     * @param isDir               是否为目录
     * @param path                服务器全路径
     * @return 图标的id
     */
    public static int getFileIcon(String filename, boolean isDir, String path) {
        if (isDir && !TextUtils.isEmpty(path)) {
            return R.drawable.icon_list_folder_n;
        } else {
            return FileType.getType(filename, isDir).mResourceIdList;
        }
    }

    /**
     * 提供文件在列表中的图标
     *
     * @param filename            文件名
     * @param isDir               是否为目录
     * @return 图标的id
     */
    public static int getFileIcon(String filename, boolean isDir) {
        if (isDir) {
            return R.drawable.icon_list_folder_n;
        }
        return FileType.getType(filename, false).mResourceIdList;
    }

    /**
     * 提供文件在列表中的图标
     *
     * @param filename            文件名
     * @param isDir               是否为目录
     * @param path                服务器全路径
     * @param isMySharedDirectory 是否为我共享的目录，包括目录和子目录
     * @return 图标的id
     */
    public static int getFileIcon(String filename, boolean isDir, String path, boolean isMySharedDirectory) {
        if (isDir && !TextUtils.isEmpty(path)) {
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }

            if (isMySharedDirectory) {
                return R.drawable.my_share_directory_icon;
            }

            if (path.startsWith(ShareDirectoryContract.Directories.TOKEN_SHARE_TO_ME_DIRECTORIES)) {
                return R.drawable.share_to_me_icon;
            }
            //  9.0改版去掉对属性相册文件的特殊处理
            //            switch (directoryType) {
            //                case DirectoryAttribute.TRAVEL_TYPE:
            //                    return R.drawable.travel_type_icon;
            //                case DirectoryAttribute.BABY_TYPE:
            //                    return R.drawable.baby_type_icon;
            //                case DirectoryAttribute.PHOTO_TYPE:
            //                    return R.drawable.photo_type_icon;
            //                default:
            //                    break;
            //            }

            switch (path) {
                case DEST_STR_MY_APP_DATA_DIR:
                case DEST_STR_IMAGEDIR:
                case DEST_STR_VIDEODIR:
                case DEST_STR_DOCDIR:
                case DEST_STR_MUSICDIR:
                    return R.drawable.icon_list_folder;
                default:
                    break;
            }

            if (path.startsWith(CloudFileConstants.ALBUM_BACKUP_KEYWORD)) {
                return R.drawable.icon_list_folder;
            }

            return R.drawable.icon_list_folder;

        } else {
            return FileType.getType(filename, isDir).mResourceIdList;
        }
    }

    /**
     * 获取新文件列表的带透明背景图片
     * @param filename
     * @param isDir
     * @param path
     * @return
     */
    public static int getFileTsBgIcon(String filename, boolean isDir, String path) {
        if (isDir && !TextUtils.isEmpty(path)) {
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }

            switch (path) {
                case DEST_STR_MY_APP_DATA_DIR:
                case DEST_STR_IMAGEDIR:
                case DEST_STR_VIDEODIR:
                case DEST_STR_DOCDIR:
                case DEST_STR_MUSICDIR:
                    return R.drawable.icon_list_folder_n;
                default:
                    break;
            }

            if (path.startsWith(CloudFileConstants.ALBUM_BACKUP_KEYWORD)) {
                return R.drawable.icon_list_folder_n;
            }

            return R.drawable.icon_list_folder_n;

        } else {
            return FileType.getTsBgType(filename, isDir);
        }
    }
    /**
     * 获取特殊文件夹图片角标
     */
    public static int getFileBadgeIcon(String path) {
        switch (path) {
            case WHATS_APP_FILE_DEFAULT_PATH:
                return R.drawable.icon_list_folder_whats_app;
            case DEST_STR_REMOTE_UPLOAD_DIR:
                return R.drawable.icon_list_folder_bt;
            case DEST_STR_VIDEO_DOWNLOADER_DIR:
                return R.drawable.icon_list_folder_downloader;
            case DEST_STR_SMOOTH_PLAY_DIR:
                return R.drawable.icon_list_folder_smooth;
            default:
                break;
        }
        return -1;
    }
    /**
     * 获取新文件列表的带透明背景图片
     * @param filename
     * @param isDir
     * @param path
     * @return
     */
    public static int getFileTsBgIconWithColor(String filename, boolean isDir, String path) {
        if (isDir && !TextUtils.isEmpty(path)) {
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }

            switch (path) {
                case DEST_STR_MY_APP_DATA_DIR:
                case DEST_STR_IMAGEDIR:
                case DEST_STR_VIDEODIR:
                case DEST_STR_DOCDIR:
                case DEST_STR_MUSICDIR:
                    return R.drawable.fitype_icon_tsbg_folder_n;
                default:
                    break;
            }

            if (path.startsWith(CloudFileConstants.ALBUM_BACKUP_KEYWORD)) {
                return R.drawable.fitype_icon_tsbg_folder_n;
            }

            return R.drawable.fitype_icon_tsbg_folder_n;

        } else {
            return FileType.getTsBgTypeWithColor(filename, isDir);
        }
    }

    /**
     * 通过云端路径和文件名获取文件全路径。
     *
     * @param path 路径
     * @param name 文件名
     * @return
     */
    public static String getFilePreferPath(String path, String name) {
        if (!TextUtils.isEmpty(path)) {
            int idx = path.indexOf(':');
            if (idx != -1) {
                return path.substring(idx + 1);
            } else {
                return path;
            }
        } else {
            return name;
        }
    }

    /**
     * 返回根目录为网盘的全路径
     *
     * @param filePath
     *
     * @return
     */
    public static String replaceRootPath(String filePath) {
        String root = BaseApplication.getInstance().getResources().getString(R.string.root_cloud);
        if (filePath == null || "".equals(filePath)) {
            return root;
        }
        if (filePath.startsWith("/")) {
            if (filePath.length() == 1) {
                return root;
            } else {
                return (root + filePath);
            }
        } else {
            return filePath;
        }
    }

    /**
     * 获取选中的BT文件大小
     *
     * @param files
     * @return
     */
    public static long getTotalDuboxFileSize(ArrayList<CloudFile> files) {
        long size = 0L;

        if (files != null) {
            for (CloudFile file : files) {
                size += file.size;
            }
        }

        return size;
    }
    /**
     * 当前目录是否为自动备份目录
     * @return
     */
    public static boolean isAutoBackupDir(CloudFile cloudFile) {
        String serverPath = cloudFile.getFilePath();
        // 如果带"/"，则去掉"/"再比较
        if (serverPath.endsWith(File.separator)) {
            serverPath = serverPath.substring(0, serverPath.length() - 1);
        }
        String ablumDir = CloudFileConstants.PATH_DUBOX_ROOT + CloudFileConstants.ALBUM_BACKUP_DIR;
        return !TextUtils.isEmpty(serverPath) && ablumDir.equals(serverPath);
    }

    /**
     * 获取选中的BT文件大小
     *
     * @param files
     * @return
     */
    public static long getTotalNetdiskFileSize(ArrayList<CloudFile> files) {
        long size = 0L;

        if (files != null) {
            for (CloudFile file : files) {
                size += file.size;
            }
        }

        return size;
    }
}
