package com.moder.compass.ui.cloudfile.view;

import androidx.annotation.NonNull;
import android.view.View;

import com.moder.compass.ui.view.IBaseView;
import com.moder.compass.ui.view.IBaseView;

/**
 * Created by libin on 2017/6/6.
 * 主界面处理headerview的接口
 */

public interface IHeaderView extends IBaseView {
    /**
     * 添加headerview
     *
     * @param view headerview
     */
    void addHeaderView(@NonNull View view);

    /**
     * 删除headerview
     *
     * @param view headerview
     */
    void removeHeaderView(@NonNull View view);

}
