
package com.moder.compass.ui.cloudfile;

/**
 * 描述云端文件类型的枚举类。<br/>
 * Created by 魏铮铮 on 15/4/7.
 */
public enum FileCategory {
    ALL(0),

    /**
     * 视频类型
     */
    VIDEO(1),

    /**
     * 音频类型
     */
    AUDIO(2),

    /**
     * 图片类型
     */
    IMAGE(3),

    /**
     * 文档类型
     */
    DOCUMENT(4),

    /**
     * 应用类型（包含APK，exe等）
     */
    APPLICATION(5),

    /**
     * BT种子文件类型
     */
    BT(7);

    private int value = -1;

    FileCategory(int value) {
        this.value = value;
    }

    /**
     * 获取当前枚举所对应的server类型数据值。
     *
     * @return server的数据类型值（int）
     */
    public final int getValue() {
        return this.value;
    }

    public static FileCategory getFileCategory(int category) {
        switch (category) {
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
            case 7:
                return BT;
            default:
                return ALL;
        }
    }
}
