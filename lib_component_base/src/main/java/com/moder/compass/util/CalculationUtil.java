package com.moder.compass.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class CalculationUtil {
    /** 标准状态栏高度 */
    private static final int STANDARD_STATUSBAR_HEIGHT = 50;

    public static int convertDpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 获取状态栏高度
     * @return int 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            try {
                result = context.getResources().getDimensionPixelSize(resourceId);
            } catch (Exception e) {
                result = 0;
            }
        }
        /** 系统显示 */
        DisplayMetrics DISPLAY_METRICS = context.getResources().getDisplayMetrics();

        if (result == 0) {
            result = (int) (STANDARD_STATUSBAR_HEIGHT / 2 * DISPLAY_METRICS.density);
        }
        return result;
    }
}
