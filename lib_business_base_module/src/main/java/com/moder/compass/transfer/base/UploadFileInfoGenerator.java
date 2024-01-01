package com.moder.compass.transfer.base;

import static com.moder.compass.statistics.UserFeatureKeysKt.KEY_USER_FEATURE_UPLOAD_GREATER_THAN_50MB_FILE;

import java.util.ArrayList;
import java.util.List;

import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageManager;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.transfer.base.IUploadFilterable;
import com.moder.compass.statistics.UserFeatureReporter;
import com.moder.compass.transfer.LastUploadFileDirRecordKt;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

/**
 * Created by liuliangping on 2015/10/22.
 */
public class UploadFileInfoGenerator implements IUploadInfoGenerator {
    private final List<Uri> mUris;
    private final IUploadFilterable mFilter;
    private final String mDestDirectory;
    private final int mConflictStrategy;
    private final long m50 = 50L * 1024 * 1024;
    /**
     * 上传文件的生成器
     *
     * @param uris             : 为本地file路径生成的uri
     * @param filterable       : 过滤掉超过大小或者非文件路径的情况
     * @param destDirectory    : 上传的目标目录
     * @param conflictStrategy : 上传冲突的策略，覆盖或者重命名
     */
    public UploadFileInfoGenerator(List<Uri> uris, IUploadFilterable filterable, String destDirectory,
                                   int conflictStrategy) {
        mUris = uris;
        mFilter = filterable;
        mDestDirectory = destDirectory;
        mConflictStrategy = conflictStrategy;
        LastUploadFileDirRecordKt.saveLastUploadCloudFilePath(destDirectory);
    }

    @Override
    public Pair<List<UploadInfo>, UploadInterceptorInfo> generate() {
        if (mUris == null || mUris.isEmpty()) {
            return null;
        }

        List<UploadInfo> infoList = new ArrayList<UploadInfo>();
        boolean isHasGreater50MBFile = false;
        UploadInterceptorInfo interceptorInfo = null;
        List<RFile> files = new ArrayList<>();
        boolean isExistOtherFile = false;
        for (Uri uri : mUris) {
            if (uri == null) {
                continue;
            }
            RFile fileMeta = PathKt.rFile(uri.toString());
            if (fileMeta == null) {
                continue;
            }
            if (!fileMeta.isVideo()) {
                isExistOtherFile = true;
            }
            files.add(fileMeta);
        }
        if (mFilter != null) {
            mFilter.setInterceptVideoUploadEnable(!isExistOtherFile);
        }
        for (RFile fileMeta : files){
            // 不满足上传条件的过滤掉
            if (mFilter != null && !mFilter.filter(fileMeta)) {
                continue;
            }
            if (mFilter != null && mFilter.isShowUploadVipGuide(fileMeta)) {
                interceptorInfo = new UploadInterceptorInfo(mFilter.getInterceptCode(),
                        fileMeta.length());
                break;
            }
            String localFileName = fileMeta.name();
            String localFileNameWithExtension = localFileName;
            String extension = FileUtils.getExtension(localFileName);
            if (extension == null || extension.isEmpty()) {
                localFileNameWithExtension = localFileName + fileMeta.extension();
            }
            String remoteUrl = getRemoteUrlByDestDirAndLocalUri(mDestDirectory, localFileNameWithExtension);
            if (TextUtils.isEmpty(remoteUrl)) {
                return null;
            }

            UploadInfo info;
            if (fileMeta.isImage()) {
                info = new UploadInfo(fileMeta, remoteUrl, 100, mConflictStrategy);
            } else {
                info = new UploadInfo(fileMeta, remoteUrl, 0, mConflictStrategy);
            }
            if (fileMeta.length() >= m50) {
                isHasGreater50MBFile = true;
            }
            infoList.add(info);
        }
        if (isHasGreater50MBFile) {
            new UserFeatureReporter(KEY_USER_FEATURE_UPLOAD_GREATER_THAN_50MB_FILE).reportAFAndFirebase();
        }
        // 通知UI TOAST
        if (mFilter != null && !infoList.isEmpty()) {
            mFilter.showTips();
        }

        return new Pair<>(infoList, interceptorInfo);
    }

    /**
     * 拼接Task 的 RemoteUrl
     *
     * @param destDir //目标路径
     *
     * @return
     *
     * @author 孙奇 V 1.0.0 Create at 2012-11-14 下午03:25:40
     */
    private String getRemoteUrlByDestDirAndLocalUri(String destDir, String fileName) {
        if (TextUtils.isEmpty(destDir)) {
            return FileUtils.getFilePath(DeviceStorageManager.ROOT, fileName);
        } else {
            return FileUtils.getFilePath(destDir, fileName);
        }
    }
}
