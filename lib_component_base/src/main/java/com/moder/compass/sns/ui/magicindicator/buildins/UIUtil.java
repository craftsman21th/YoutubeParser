// CHECKSTYLE:OFF
package com.moder.compass.sns.ui.magicindicator.buildins;

import android.content.Context;

/**
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public final class UIUtil {

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context context
     * @param pxValue pxValue
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);  //  SUPPRESS CHECKSTYLE
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}