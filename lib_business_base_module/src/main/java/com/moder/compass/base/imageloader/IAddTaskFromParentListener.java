package com.moder.compass.base.imageloader;

import com.dubox.drive.base.imageloader.SimpleFileInfo;

import java.util.List;

import androidx.fragment.app.Fragment;

/**
 * Created by liaozhengshuang on 17/11/21.
 * 监听将目录中的预加载任务加入预加载队列
 */

public interface IAddTaskFromParentListener {
    void addTasksFromParent(Fragment fragment, List<SimpleFileInfo> simpleImageFiles,
                            ThumbnailSizeType type, boolean isUrl);
}
