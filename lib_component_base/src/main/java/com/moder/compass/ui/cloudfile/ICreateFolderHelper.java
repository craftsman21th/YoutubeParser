package com.moder.compass.ui.cloudfile;

import com.moder.compass.ui.widget.EditLoadingDialog;

/**
 * CreateFolderHelper对外部组件接口
 *
 * @Author: guoqiqin
 * @CreateDate: 2019/09/26
 */
public interface ICreateFolderHelper {
    void createFolder(EditLoadingDialog.Type type);
    void dismissDialog();
    void rename(final String directory, final boolean isDir, final int category);

}
