package com.moder.compass.cloudimage.ui;

/**
 * Created by huantong on 2017/6/21.
 */

public interface ICloudImageExtras {
    /**
     * 文件CloudFile
     */
    String EXTRA_FILE = "extra_file";
    /**
     * 文件来源
     */
    String EXTRA_FILE_FROM = "extra_file_from";
    /**
     * 是否显示tab栏 占位View
     */
    String EXTRA_SHOW_BOTTOM_EMPTY_VIEW = "extra_show_bottom_empty_view";
    /**
     * 是否gone掉tab栏 占位View
     */
    String EXTRA_NO_BOTTOM_EMPTY_BAR = "extra_no_bottom_empty_bar";
    /**
     * 从文件列表进入
     */
    int EXTRA_FROM_FILELIST = 2;
    /**
     * 调起分类入口，首页或文件列表
     */
    String CATEGORY_EXTRA_FROM = "category_extra_from";
}
