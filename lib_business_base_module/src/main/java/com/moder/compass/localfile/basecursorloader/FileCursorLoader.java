/*
 * FileCursorLoader.java
 * classes : com.dubox.drive.localfilesystem.p2plocalfile.task.FileCursorLoader
 * @author 文超
 * V 1.0.0
 * Create at 2014-8-12 下午8:09:12
 */
package com.moder.compass.localfile.basecursorloader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.MatrixCursor;
import androidx.loader.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Pair;

import com.dubox.drive.cloudfile.constant.CloudFileConstants;
import com.moder.compass.localfile.FileInfoColumns;
import com.moder.compass.localfile.model.FileTypeCountEntry;
import com.moder.compass.localfile.utility.FileNameComparator;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageManager;

/**
 * 根据传入的filefilter过滤出指定filter的单层目录文件列表
 *
 * @author 文超 <br/>
 *         create at 2014-8-12 下午8:09:12
 */
public class FileCursorLoader extends AsyncTaskLoader<Pair<MatrixCursor, FileTypeCountEntry>> {
    private static final String TAG = "FileCursorLoader";

    private Pair<MatrixCursor, FileTypeCountEntry> mData;
    private String mPath;
    private FileFilter mFileFilter;

    public FileCursorLoader(Context context, String path, FileFilter fileter) {
        super(context);
        mPath = path;
        mFileFilter = fileter;
    }

    public FileCursorLoader(Context context, String path) {
        super(context);
        mPath = path;
    }

    @Override
    public Pair<MatrixCursor, FileTypeCountEntry> loadInBackground() {
        if (TextUtils.isEmpty(mPath)) {
            return null;
        }

        if (mPath.equals(CloudFileConstants.PATH_DUBOX_ROOT_LOCAL)) {
            return loadRoot();
        } else {
            return loadDir(mPath);
        }
    }

    /**
     * 加载指定目录的文件，并转为cursor
     * 
     * @param path
     * @return
     */
    private Pair<MatrixCursor, FileTypeCountEntry> loadDir(String path) {
        MatrixCursor cursor = new MatrixCursor(FileInfoColumns.PROJECTION);

        File file = new File(path);
        File[] subFiles = (mFileFilter == null ? file.listFiles() : file.listFiles(mFileFilter));

        List<File> dirs = new ArrayList<File>();
        List<File> files = new ArrayList<File>();

        if (subFiles != null) {
            for (File subFile : subFiles) {
                if (subFile.isHidden()) {
                    continue;
                } else if (subFile.isDirectory()) {
                    dirs.add(subFile);
                } else {
                    files.add(subFile);
                }
            }
        }

        // 为文件夹列表排序
        Collections.sort(dirs, new FileNameComparator());
        // 为文件列表排序
        Collections.sort(files, new FileNameComparator());

        // 合并文件夹和文件
        List<File> subFileList = new ArrayList<File>();
        subFileList.addAll(dirs);
        subFileList.addAll(files);

        int id = 0;
        for (File subFile : subFileList) {
            cursor.addRow(new Object[] { id, subFile.getName(), subFile.getAbsolutePath(),
                    subFile.isDirectory() ? 1 : 0, file.length() });
            id++;
        }

        FileTypeCountEntry typeCountEntry = new FileTypeCountEntry(dirs.size(), files.size());
        return new Pair<MatrixCursor, FileTypeCountEntry>(cursor, typeCountEntry);
    }

    /**
     * 加载根目录，并转为cursor
     */
    private Pair<MatrixCursor, FileTypeCountEntry> loadRoot() {
        DeviceStorageManager deviceStorageManager = DeviceStorageManager.createDevicesStorageManager(getContext());
        if (deviceStorageManager.hasDefaultStorage() && deviceStorageManager.isDefaultStorageAvailable()
                && deviceStorageManager.hasSecondaryStorage() && deviceStorageManager.isSecondaryStorageAvailable()) {
            // 双卡
            File defaultStorageFile = deviceStorageManager.getDefaultStorageFile();
            File secondaryStorageFile = deviceStorageManager.getSecondaryStorageFile();

            MatrixCursor cursor = new MatrixCursor(FileInfoColumns.PROJECTION);
            cursor.addRow(new Object[] { 0, defaultStorageFile.getName(), defaultStorageFile.getAbsolutePath(), 1, 0 });
            cursor.addRow(
                    new Object[] { 1, secondaryStorageFile.getName(), secondaryStorageFile.getAbsolutePath(), 1, 0 });

            FileTypeCountEntry typeCountEntry = new FileTypeCountEntry(cursor.getCount(), 0);
            return new Pair<MatrixCursor, FileTypeCountEntry>(cursor, typeCountEntry);
        } else if (deviceStorageManager.hasDefaultStorage() && deviceStorageManager.isDefaultStorageAvailable()) {
            // 内卡
            File defaultStorageFile = deviceStorageManager.getDefaultStorageFile();
            // 如果只有一张卡，那么直接打开
            return loadDir(defaultStorageFile.getAbsolutePath());
        } else if (deviceStorageManager.hasSecondaryStorage() && deviceStorageManager.isSecondaryStorageAvailable()) {
            // 外卡
            File secondaryStorageFile = deviceStorageManager.getSecondaryStorageFile();
            // 如果只有一张卡，那么直接打开
            return loadDir(secondaryStorageFile.getAbsolutePath());
        }

        return null;
    }

    @Override
    public void deliverResult(Pair<MatrixCursor, FileTypeCountEntry> data) {
        MatrixCursor cursor = null;
        if (data != null) {
            cursor = data.first;
        }

        if (isReset()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        Pair<MatrixCursor, FileTypeCountEntry> oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData == null) {
            return;
        }

        MatrixCursor oldCursor = oldData.first;
        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    /**
     * 必须由主线程调用
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Pair<MatrixCursor, FileTypeCountEntry> data) {
        MatrixCursor cursor = null;
        if (data != null && data.first != null) {
            cursor = data.first;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // 保证loader已经停止
        onStopLoading();

        if (mData == null) {
            return;
        }

        MatrixCursor cursor = mData.first;
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = null;
        mData = null;
    }
}