package com.moder.compass.base.imageloader;

import java.util.ArrayList;

import com.dubox.drive.base.imageloader.SimpleFileInfo;
import com.dubox.drive.kernel.architecture.db.cursor.CursorUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;

/**
 * Created by liaozhengshuang on 17/11/17.
 * 根据parentPath从数据库query任务
 */

public class LoadByParentPathTask implements IImagePreLoadTask {
    private static final String TAG = "LoadByParentPathTask";
    /**
     * 获取所有需要预加载的任务的参数信息
     */
    private PreLoadExtraParams mParams;
    private IAddTaskFromParentListener mListener;
    private ThumbnailSizeType mType;
    private Fragment mFragment;
    private Context mContext;

    public LoadByParentPathTask(Context context, Fragment fragment, ThumbnailSizeType type,
                                PreLoadExtraParams params, @NonNull IAddTaskFromParentListener listener) {
        mParams = params;
        mListener = listener;
        mType = type;
        mFragment = fragment;
        mContext = context;
    }

    @Override
    public void execute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor data = null;
                try {
                    data = mContext.getContentResolver().query(mParams.uri, mParams.projection, mParams.selection, mParams
                            .selectionArgs, mParams.sort);
                    if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                        DuboxLog.d(TAG, "count:" + data.getCount());
                        ArrayList<SimpleFileInfo> files = new ArrayList<>(data.getCount());
                        do {
                            String path = data.getString(0);
                            String md5 = data.getString(1);
                            if (!TextUtils.isEmpty(path)) {
                                files.add(new SimpleFileInfo(path, md5));
                            }
                        } while (data.moveToNext());
                        mListener.addTasksFromParent(mFragment, files, mType, mParams.isUrl);
                    }
                } catch (Exception e) {
                    DuboxLog.e(TAG, e.getMessage(), e);
                } finally {
                    CursorUtils.safeClose(data);
                }
            }
        }).start();
    }

    @Override
    public boolean isExecutableTask() {
        return false;
    }

    @Override
    public String getLoadUrl() {
        return null;
    }

    @Override
    public void notifyLoaded() {
    }
}
