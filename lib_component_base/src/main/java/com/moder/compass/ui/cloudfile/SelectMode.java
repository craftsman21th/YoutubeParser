package com.moder.compass.ui.cloudfile;

/**
 * Created by libin on 2017/10/30.
 */

public enum SelectMode {
    /**
     * 视频类型
     */
    SINGLE(1),

    MULTIPLE(2);

    private int value = -1;

    SelectMode(int value) {
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

    public static SelectMode getSelectMode(int mode) {
        if (mode == 1) {
            return SINGLE;
        }

        return MULTIPLE;
    }
}
