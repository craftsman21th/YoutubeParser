package com.moder.compass.ui.cloudfile;

import java.util.ArrayList;

/**
 * RecycleBinFilePresenter对外部组件接口
 *
 * @Author: guoqiqin
 * @CreateDate: 2019/09/26
 */
public interface IRecycleBinFilePresenter {
    void restoreRecycleBinFiles(final ArrayList<Long> fids, final boolean async);
    void deleteRecycleBinFiles(final ArrayList<Long> fids, final int async);
}
