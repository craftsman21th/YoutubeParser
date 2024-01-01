package com.moder.compass.ui.cloudfile.view;

import com.moder.compass.ui.view.IBaseView;
import com.moder.compass.ui.view.IBaseView;

/**
 * 回收站基本操作的View
 * Created by tianzengming on 2015/8/3.
 */
public interface ISimpleRecycleBinFileView extends IBaseView {

    void onDeleteFilesFinished(int code);

    void onRestoreFinished(int code);

    void showRestoringDialog();
}
