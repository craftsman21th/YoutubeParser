package com.moder.compass.plugins;

/**
 * Created by cuizhe01 on 17-12-26.
 */

public enum SelectDirEvent {

    SELECT(0),

    DO_N0T_SELECT_AND_TIPS(1);

    private int value = 0;

    SelectDirEvent(int value) {
        this.value = value;
    }

    public final int getValue() {
        return this.value;
    }

    public static SelectDirEvent getSelectDirEvent(int value) {
        if (value == 0) {
            return SELECT;
        } else if (value == 1) {
            return DO_N0T_SELECT_AND_TIPS;
        } else {
            return null;
        }
    }
}
