
package com.moder.compass.transfer.util;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.storage.config.Setting;
import com.dubox.drive.db.cloudfile.contract.CloudFileContract;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceInfo;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageManager;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageUtils;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.localfile.DownloadPath;
import com.moder.compass.statistics.BroadcastStatisticKt;
import com.moder.compass.transfer.TransferFileNameConstant;
import com.moder.compass.transfer.storage.DownloadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.DownloadTask;
import com.dubox.drive.kernel.android.util.storage.ExternalStorageUtils;
import com.mars.united.core.os.database.CursorExtKt;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.net.Uri;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;

/**
 * Created by liuliangping on 2015/1/30.
 */
public class TransferUtil {
    /**
     * 类型KEY，用于任务完成广播
     */
    public static final String TYPE_KEY = "type_key";
    /**
     * 有新完成的任务
     */
    private static final String TAG = "TransferTaskUtil";
    public static final String TRANSFER_TODAY_REPORT = "com.dubox.transfer.TRANSFER_TODAY_REPORT";
    public static final String ACTION_CANCEL_PREVIEW_TASK_FINISH =
            "com.dubox.ACTION_CANCEL_PREVIEW_TASK_FINISH";

    public static final String CANCEL_TASK_IDS = "cancel_task_ids";

    /**
     * 用于区分上传或下载行为方式
     */
    public static final String TYPE_UPLOAD_OR_DOWNLOAD = "upload_or_download";
    public static final String TYPE_UPLOAD = "upload";
    public static final String TYPE_DOWNLOAD = "download";

    public static final String P2P_NET_TYPE_WIFI = "wifi";
    public static final String P2P_NET_TYPE_MOBILE = "mobile";
    public static final String P2P_NET_TYPE_NONE = "none";
    public static final String IS_DOWNLOAD_TYPE = "is_download_type";

    /**
     * 通过比较url获取流畅视频下载的
     *
     * @param url
     * @param bduss
     * @param uid
     *
     * @return
     */

    public static DownloadTask getDownloadSmoothVideoTaskByUrl(String url, String bduss, String uid) {
        if (url == null) {
            return null;
        }
        final String parseUrl = parseUrlForRemoveDuplicate(url);
        if (TextUtils.isEmpty(parseUrl)) {
            return null;
        }

        final Cursor cursor =
                BaseApplication
                        .getInstance()
                        .getContentResolver()
                        .query(TransferContract.DownloadTasks.buildUri(bduss),
                                new String[] { TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL,
                                        TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE,
                                        TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE,
                                        TransferContract.Tasks.OFFSET_SIZE, TransferContract.Tasks.REMOTE_URL,
                                        TransferContract.Tasks.DATE, TransferContract.DownloadTasks.PRIORITY },
                                TransferContract.Tasks.TRANSMITTER_TYPE + "=?",
                                new String[] { TransferContract.DownloadTasks.TRANSMITTER_TYPE_M3U8 }, null);

        if (cursor == null) {
            return null;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    final String remoteUrl = cursor.getString(cursor.getColumnIndex(TransferContract.Tasks.REMOTE_URL));
                    if (null != remoteUrl && parseUrl.equals(parseUrlForRemoveDuplicate(remoteUrl))) {
                        return new DownloadTask(cursor, null, bduss, uid, null);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    /**
     * 解析URL去重操作
     */
    public static String parseUrlForRemoveDuplicate(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        DuboxLog.d(TAG, "url: " + url);
        String urlSplit[];
        String paramSplit[];
        String http = "";
        String path = "";
        String uk = "";
        String shareId = "";
        String albumId = "";
        String fid = "";
        String from_uk = "";
        String to = "";
        String msg_id = "";
        String fs_id = "";
        String type = "";
        String stream_type = "";
        StringBuffer sb = new StringBuffer();
        urlSplit = url.split("\\?");
        if (urlSplit == null || urlSplit.length != 2) {
            return url;
        }
        http = urlSplit[0];
        sb.append(http + "?");
        paramSplit = urlSplit[1].split("&");
        if (paramSplit == null) {
            return url;
        }
        for (String s : paramSplit) {
            if (s.startsWith("path")) {
                path = s;
            } else if (s.startsWith("uk")) {
                uk = s;
            } else if (s.startsWith("shareid")) {
                shareId = s;
            } else if (s.startsWith("albumid")) {
                albumId = s;
            } else if (s.startsWith("fid")) {
                fid = s;
            } else if (s.startsWith("from_uk")) {
                from_uk = s;
            } else if (s.startsWith("to")) {
                to = s;
            } else if (s.startsWith("msg_id")) {
                msg_id = s;
            } else if (s.startsWith("fs_id")) {
                fs_id = s;
            } else if (s.startsWith("type")) {
                type = s;
            } else if (s.startsWith("stream_type")) {
                stream_type = s;
            }
        }
        if (TextUtils.isEmpty(path + uk + shareId + albumId + fid + from_uk + to + msg_id + fs_id + type + stream_type)) {
            return url;
        }
        sb.append(path + "&").append(uk + "&").append(shareId + "&").append(albumId + "&").append(fid + "&")
                .append(from_uk + "&").append(to + "&").append(msg_id + "&").append(fs_id + "&").append(type + "&")
                .append(stream_type);
        return sb.toString();
    }

    /**
     * 通过fileId获取对应的TransferTask
     * <p>
     * 只适用于downloadTask
     *
     * @param serverPath
     * @return
     */
    public static DownloadTask getDownloadTaskByServerPath(String serverPath, String bduss, String uid) {
        if (serverPath == null) {
            return null;
        }
        Uri queryUri = TransferContract.DownloadTasks.buildUri(bduss);
        String[] projection = new String[] {
                TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL,
                TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE,
                TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE,
                TransferContract.Tasks.OFFSET_SIZE, TransferContract.Tasks.REMOTE_URL,
                TransferContract.Tasks.DATE, TransferContract.DownloadTasks.PRIORITY,
                CloudFileContract.Files.FILE_SERVER_MD5};
        String selection = TransferContract.Tasks.REMOTE_URL + "=?";
        final Cursor cursor = BaseApplication.getInstance().getContentResolver()
                        .query(queryUri, projection, selection, new String[]{serverPath}, null);

        DownloadTask task = null;
        try {
            task = getDownloadTaskByServerPath(cursor, serverPath, bduss, uid);
        } catch (Exception e) {
            DuboxLog.d(TAG, "getDownloadTaskByServerPath e:" + e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return task;
    }

    /**
     * 通过fileId获取对应的TransferTask
     * <p>
     * 只适用于downloadTask
     *
     * @param serverPath
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-11-9 上午11:49:34
     */
    private static DownloadTask getDownloadTaskByServerPath(Cursor cursor, String serverPath,
                                                      String bduss, String uid) {
        if (cursor == null || serverPath == null) {
            return null;
        }
        boolean exist = false;
        if (cursor.moveToFirst()) {
            do {
                final String remoteUrl = cursor.getString(cursor.getColumnIndex(TransferContract.Tasks.REMOTE_URL));
                if (serverPath.equals(remoteUrl)) {
                    exist = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        DuboxLog.d(TAG, "getDownloadTaskByServerPath exist:" + exist);
        if (exist) {
            return new DownloadTask(cursor, null, bduss, uid, null);
        }
        return null;
    }

    /**
     * 上传的时候，发送报活的广播
     */
    public static void sendUpTransferBroadcast() {
        Intent transferBroadcast = new Intent(TRANSFER_TODAY_REPORT);
        transferBroadcast.putExtra(TYPE_UPLOAD_OR_DOWNLOAD, TYPE_UPLOAD);
        LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(transferBroadcast);
        BroadcastStatisticKt.statisticSendBroadcast(TRANSFER_TODAY_REPORT);
    }

    /**
     * 下载的时候，发送报活的广播
     */
    public static void sendDownloadTransferBroadcast() {
        Intent transferBroadcast = new Intent(TRANSFER_TODAY_REPORT);
        transferBroadcast.putExtra(TYPE_UPLOAD_OR_DOWNLOAD, TYPE_DOWNLOAD);
        LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(transferBroadcast);
        BroadcastStatisticKt.statisticSendBroadcast(TRANSFER_TODAY_REPORT);
    }

    /**
     * 判断文件是否在当前下载目录已经存在
     *
     * @return
     */
    public static boolean isFileExist(String filePath) {
        String defaultPath = Setting.getDefaultSaveDir(BaseApplication.getInstance());
        File file = new File(defaultPath + filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /*
     * CloudFile.java
     *
     * @panwei Create at 2012-11-12 下午3:49:49
     */
    public static boolean isFileMd5Changed(String filePath, long lastModifyTime) {
        if (isFileExist(filePath)) {
            String defaultPath = Setting.getDefaultSaveDir(BaseApplication.getInstance());
            StringBuilder sb = new StringBuilder();
            sb.append(defaultPath);
            sb.append(filePath);
            String path = sb.toString();
            return FileUtils.isFileChanged(path, lastModifyTime);
        }
        return false;
    }

    public static long getLastModifyTime(String serverPath, String bduss) {
        final Cursor cursor =
                new DownloadTaskProviderHelper(bduss).getDownloadFile(BaseApplication.getInstance()
                        .getContentResolver(), serverPath);

        if (cursor == null) {
            return -1L;
        }

        try {
            if (!cursor.moveToFirst()) {
                return -1L;
            }

            return cursor.getLong(cursor.getColumnIndex(TransferContract.DownloadTaskFiles.LOCAL_LAST_MODIFY_TIME));
        } catch (Exception e) {
            DuboxLog.w(TAG, "getLastModifyTime", e);
        } finally {
            cursor.close();
        }

        return -1L;
    }


    /**
     * 由文件名生成获取临时文件名
     *
     * @return
     * @author yangqinghai Create at 2013-11-26 下午02:40:31
     */
    public static String getTemporaryFileName(String filePath) {
        StringBuffer temporaryFileName = new StringBuffer(filePath);
        if (filePath.length() > 0) {
            temporaryFileName = temporaryFileName.append(TransferFileNameConstant.DOWNLOAD_SUFFIX);
        }
        DuboxLog.d(TAG, "getTemporaryFileName temporaryFileName::" + temporaryFileName.toString());
        return temporaryFileName.toString();
    }


    /**
     * 处理!bn
     *
     * @param path
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-10-29 下午07:50:12
     */
    public static String removeBN(String path) {
        if (path.endsWith(TransferFileNameConstant.DOWNLOAD_SUFFIX)) {
            return path.substring(0, path.lastIndexOf(TransferFileNameConstant.DOWNLOAD_SUFFIX));
        } else {
            return path;
        }
    }

    /**
     * 去掉
     *
     * @param filePath
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-11-13 下午08:17:38
     */
    public static String getFileNameDisplay(String filePath) {
        String s = filePath.substring(filePath.lastIndexOf('/') + 1);

        int dot = s.lastIndexOf(".");
        if (dot < 0)
            return s;
        if (!s.substring(dot).equals(TransferFileNameConstant.DOWNLOAD_SUFFIX)) {
            return s;
        } else {
            return s.substring(0, dot);
        }
    }

    /**
     * 修改之前下载的被编辑过的文件的名字
     * <p>
     * 形如“backup(n)xxx.txt”
     *
     * @return
     * @author 孙奇 V 1.0.0 Create at 2013-1-8 下午02:24:22
     */
    public static boolean changeOldDownloadFileName(String path) {
        File fileNeed2Rename = new File(path);
        if (!fileNeed2Rename.exists()) {
            return true;
        }
        String directoryPath = FileUtils.getFileDirectoryWithOutSlash(path);
        String fileName = FileUtils.getFileName(path);

        File file = new File(directoryPath, String.format(TransferFileNameConstant.BACKUP_FILE_NAME, fileName));
        if (!file.exists()) {
            return fileNeed2Rename.renameTo(file);
        }
        int copyIndex = 1;

        while (copyIndex < 500) {
            file = new File(directoryPath, String.format(TransferFileNameConstant.BACKUP_INDEX_FILE_NAME, copyIndex,
                            fileName));
            if (!file.exists()) {
                return fileNeed2Rename.renameTo(file);
            }
            copyIndex++;
        }
        return false;
    }

    /**
     * 检查并确保创建默认下载目录 提交改为异步 libin09 2013-7-8
     */
    public static void createDefaultDownloadDir(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences settings = context.getSharedPreferences(Setting.PREF_SETTINGS, 0);
        String defaultDir = Setting.getDefaultFolder();
        String settingDefaultDir = settings.getString(Setting.KEY_DEFAULT_DIR, defaultDir);
        String configDefaultDir =
                PersonalConfig.getInstance().getString(Setting.KEY_DEFAULT_DIR, defaultDir);

        // 兼容之前的旧版本，新版本都使用utilconfig
        if (!settingDefaultDir.equals(defaultDir)) {
            defaultDir = settingDefaultDir;
            PersonalConfig.getInstance().putString(Setting.KEY_DEFAULT_DIR, defaultDir);
        } else if (!configDefaultDir.equals(defaultDir)) {
            defaultDir = configDefaultDir;
        }

        File dirName = new File(defaultDir);
        if (!dirName.isDirectory()) {
            DuboxLog.d(TAG, "dirName is not exists.");
            dirName.mkdir();
        }
        // 双卡手机，第二卡默认创建目录
        if (DeviceStorageManager.createDevicesStorageManager(context).hasSecondaryStorage()
                && DeviceStorageManager.createDevicesStorageManager(context).isSecondaryStorageAvailable()) {
            String duboxFolder = DownloadPath.getDefaultDownloadDirName();
            String downloadDir = null;
            String secondaryStoragePath =
                    DeviceStorageManager.createDevicesStorageManager(context).getSecondaryStoragePath();
            if (!DeviceInfo.isAboveKitkat()) {
                downloadDir = secondaryStoragePath;
            } else {
                downloadDir = DeviceStorageUtils.getKitkatSecondaryPath(context);
            }

            if (!TextUtils.isEmpty(downloadDir)) {
                File secondSDcardDir = new File(downloadDir, duboxFolder);
                if (!secondSDcardDir.isDirectory()) {
                    DuboxLog.d(TAG, "secondSDcardDir is not exists.");
                    secondSDcardDir.mkdirs();
                }
            }
        }
    }

    /**
     * 当预览的dialog收到广播取消任务完成后发送
     */
    public static boolean sendCancelTaskFinishBroadcast() {
        BroadcastStatisticKt.statisticSendBroadcast(ACTION_CANCEL_PREVIEW_TASK_FINISH);
        return LocalBroadcastManager.getInstance(BaseApplication.getInstance()).sendBroadcast(
                new Intent(ACTION_CANCEL_PREVIEW_TASK_FINISH));
    }

    /**
     * 检查两个路径的根目录是否为默认的内存卡目录
     *
     * @param formPath
     * @param toPath
     * @return
     */
    public static boolean isSameRootPath(String formPath, String toPath) {
        if (TextUtils.isEmpty(formPath) || TextUtils.isEmpty(toPath)) {
            return false;
        }

        String inRootDir = null;
        if (ExternalStorageUtils.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            inRootDir = ExternalStorageUtils.getExternalStorageDirectory().getPath();
        }
        if (inRootDir != null && formPath.startsWith(inRootDir) && toPath.startsWith(inRootDir)) {
            return true;
        }
        return false;
    }

    /**
     * 获取dlink的fid,来唯一标识来对同文件进行去重
     * <p>
     * 如：http://d.pcs.moder.com/7b7file/48456b9c0614c4e546c102f3a6527?fid=4228227107-250528-478787867194684&time
     * =1460085511&rt=sh&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-wdaoLCD%2Bi%2FzHPd91qPiyVQhyiDE%3D&expires
     * =8h&chkv=0&chkbd=0&chkpc=&dp-logid=2283824970614984069&dp-callid=0&r=942053639&sh=1
     * 对应的唯一标识为：fid的值配合确定唯一
     *
     * @param baseStr
     * @return
     */
    public static String getFileUniqueIdentifier(String baseStr) {
        if (TextUtils.isEmpty(baseStr)) {
            return null;
        }

        // 匹配url参数名和参数值的正则表达式
        final String urlRegex = "fid=([^&]+)";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher(baseStr);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /***
     *  判断dlink的任务是否为统一任务值需要判断包括fid和它之前的相同即可
     *
     * @param cursor
     * @param fileId
     * @param bduss
     * @param uid
     * @return
     */
    public static DownloadTask getDlinkDownloadTaskByServerPath(Cursor cursor, String fileId, String bduss,
                                                                String uid) {
        if (cursor == null || fileId == null) {
            return null;
        }
        boolean exist = false;
        if (cursor.moveToFirst()) {
            do {
                final String remoteUrl = cursor.getString(cursor.getColumnIndex(TransferContract.Tasks.REMOTE_URL));
                if (remoteUrl.contains(fileId)) {
                    exist = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        DuboxLog.d(TAG, "getDlinkDownloadTaskByServerPath exist:" + exist);
        if (exist) {
            return new DownloadTask(cursor, null, bduss, uid, null);
        }
        return null;
    }

    /**
     * 判断dlink的任务是否为统一任务值需要判断包括fid和它之前的相同即可
     *
     * @param fileId
     * @param bduss
     * @param uid
     * @return
     */
    public static DownloadTask getDlinkDownloadTaskByServerPath(String fileId, String bduss, String uid) {
        if (fileId == null) {
            return null;
        }

        final Cursor cursor =
                BaseApplication.getInstance().getContentResolver().query(TransferContract.DownloadTasks.buildUri(bduss),
                        new String[]{TransferContract.Tasks._ID, TransferContract.Tasks.LOCAL_URL,
                                TransferContract.Tasks.TRANSMITTER_TYPE, TransferContract.Tasks.STATE,
                                TransferContract.Tasks.TYPE, TransferContract.Tasks.SIZE,
                                TransferContract.Tasks.OFFSET_SIZE, TransferContract.Tasks.REMOTE_URL,
                                TransferContract.Tasks.DATE, TransferContract.DownloadTasks.PRIORITY},
                        TransferContract.Tasks.REMOTE_URL + " LIKE '%" + fileId + "%'", null, null);

        DownloadTask task = null;
        try {
            task = getDlinkDownloadTaskByServerPath(cursor, fileId, bduss, uid);
        } catch (Exception e) {
            DuboxLog.d(TAG, "getDlinkDownloadTaskByServerPath e:" + e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return task;
    }

    /**
     * 适配target30, 下载时将存入uri到local_url中，
     * 该方法用来根据remoteUrl在下载表中查询是否存在uri，进而判断非媒体类文件是否下载过
     * @param remoteUrl
     * @return
     */
    public static @Nullable String getLocalUriByRemoteUrl(String remoteUrl, String mBduss) {
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        Uri uri = TransferContract.DownloadTasks.buildUri(mBduss);
        try {
            cursor = contentResolver.query(uri, new String[] { TransferContract.DownloadTasks.LOCAL_URL },
                    TransferContract.DownloadTasks.REMOTE_URL + "=?",
                    new String[] { remoteUrl }, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    return CursorExtKt.getStringOrNull(cursor, TransferContract.DownloadTasks.LOCAL_URL);
                }
            }
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "get cursor failed", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 适配target30，从系统download表中查询的uri是否存在与download表中
     * @param localUrls
     * @return
     */
    public static @Nullable String getExistLocalUri(List<String> localUrls, String mBduss) {
        if (localUrls == null || localUrls.isEmpty()) {
            return null;
        }
        Cursor cursor = null;
        ContentResolver contentResolver = BaseApplication.getInstance().getContentResolver();
        Uri uri = TransferContract.DownloadTasks.buildUri(mBduss);

        String[] projection = new String[] { TransferContract.DownloadTasks.LOCAL_URL };
        String selection = TransferContract.DownloadTasks.LOCAL_URL + " IN (?)";
        String arg = TextUtils.join(",", localUrls);
        String[] args = new String[] { arg };
        try {
            cursor = contentResolver.query(uri, projection, selection, args, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    return CursorExtKt.getStringOrNull(cursor, TransferContract.DownloadTasks.LOCAL_URL);
                }
            }
        } catch (Exception e) {
            DuboxLog.e("DownloadTaskProviderHelper", "get cursor failed", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}
