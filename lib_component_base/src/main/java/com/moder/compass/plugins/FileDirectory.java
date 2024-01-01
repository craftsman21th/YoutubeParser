package com.moder.compass.plugins;

/**
 * Created by liji01 on 17-12-6.
 */

public enum FileDirectory {

    SHOW(0),

    HIDE(1);

    private int value = 0;

    FileDirectory(int value) {
        this.value = value;
    }

    /**
     * 获取当前枚举所对应的是否显示文件夹数据值。
     *
     * @return 是否显示文件夹数据值（int）
     */
    public final int getValue() {
        return this.value;
    }

    public static FileDirectory getFileDirectory(int value) {
        if (value == 0) {
            return SHOW;
        } else if (value == 1) {
            return HIDE;
        } else {
            return null;
        }
    }
}
