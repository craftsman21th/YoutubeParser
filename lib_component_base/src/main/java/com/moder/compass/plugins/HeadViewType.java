package com.moder.compass.plugins;

import com.moder.compass.component.base.R;

/**
 * Created by liji01 on 18-1-3.
 */

public enum HeadViewType {
    /**
     * 隐藏headview
     */
    HIDE(-1, 0, 0),
    /**
     * 全部文件
     */
    ALL(0, R.string.plugin_head_view_all, R.drawable.cloudp2p_local_file_category_all),
    /**
     * 视频类型
     */
    VIDEO(1, R.string.plugin_head_view_video, R.drawable.cloudp2p_local_file_category_video),

    /**
     * 音频类型
     */
    AUDIO(2, R.string.plugin_head_view_audio, R.drawable.cloudp2p_local_file_category_audio),

    /**
     * 图片类型
     */
    IMAGE(3, R.string.plugin_head_view_image, R.drawable.cloudp2p_local_file_category_bucket),

    /**
     * 文档类型
     */
    DOCUMENT(4, R.string.plugin_head_view_document, R.drawable.cloudp2p_local_file_category_doc),

    /**
     * 应用类型（包含APK，exe等）
     */
    APPLICATION(5, R.string.plugin_head_view_application, R.drawable.cloudp2p_local_file_category_apk);

    private int value = -1;
    private int nameResId = -1;
    private int iconResId = -1;

    HeadViewType(int value, int nameResId, int iconResId) {
        this.value = value;
        this.nameResId = nameResId;
        this.iconResId = iconResId;
    }

    public final int getValue() {
        return this.value;
    }

    public final int getNameResId() {
        return this.nameResId;
    }

    public final int getIconResId() {
        return this.iconResId;
    }

    public static HeadViewType getHeadViewType(int value) {
        switch (value) {
            case -1:
                return HIDE;
            case 0:
                return ALL;
            case 1:
                return VIDEO;
            case 2:
                return AUDIO;
            case 3:
                return IMAGE;
            case 4:
                return DOCUMENT;
            case 5:
                return APPLICATION;
            default:
                return HIDE;
        }
    }
}
