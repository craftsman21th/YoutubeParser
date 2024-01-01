/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */

/*
 * FormatUtil.java
 * classes : com.dubox.drive.util.FormatUtil
 * @author chenyuquan
 * V 1.0.0
 * Create at 2013年10月17日 下午6:56:38
 */
package com.moder.compass.util;

import java.util.Formatter;
import java.util.Locale;

import com.moder.compass.BaseApplication;
import com.dubox.drive.basemodule.R;

import android.content.Context;

/**
 * com.dubox.drive.util.FormatUtil
 *
 * @author chenyuquan <br/>
 * create at 2013年10月17日 下午6:56:38
 */
public class FormatUtils {
    private static final String TAG = "FormatUtil";

    /***
     * 格式化文件大小,保留小数点两位
     */
    public static String formatFileSize(long size) {
        return formatFileSize(size, 2);
    }

    /***
     * 格式化文件大小,保留小数点一位
     * @param size 文件大小
     * @param point 格式化小数点位数
     */
    public static String formatFileSize(long size, int point) {
        float fsize = 0.0f;
        String strSize = null;
        int gbThreshold = 1073741824; // 1024 * 1024 * 1024
        int mbThreshold = 1048576; // 1024 * 1024
        int kbThreshold = 1024;
        Context context = BaseApplication.getInstance();
        Formatter formatter = new Formatter(Locale.ENGLISH);
        if (size > gbThreshold) {
            fsize = (float) size / (gbThreshold);
            strSize = formatter.format("%." + point + "f" + context.getString(R.string.size_unit_gb),
                    fsize).toString();
        } else if (size > mbThreshold) {
            fsize = (float) size / (mbThreshold);
            strSize = formatter.format("%." + point + "f" + context.getString(R.string.size_unit_mb),
                    fsize).toString();
        } else if ((size * 100) / kbThreshold > 0) {
            fsize = (float) size / kbThreshold;
            strSize =
                    formatter.format("%." + point + "f" + context.getString(R.string.size_unit_kb), fsize).toString();
        } else {
            strSize = formatter.format("%d" + context.getString(R.string.size_unit_b), size).toString();
        }
        return strSize;
    }

    /**
     * 格式化空间大小
     *
     * @param size
     *
     * @return
     */
    public static String formatStorageSize(long size) {
        String strSize = null;
        long tenTbThreshold = 10995116277760L; // 1024 * 1024 * 1024 * 1024 * 10
        long tbThreshold = 1099511627776L; // 1024 * 1024 * 1024 * 1024
        int gbThreshold = 1073741824; // 1024 * 1024 * 1024
        int mbThreshold = 1048576; // 1024 * 1024
        int kbThreshold = 1024;
        Context context = BaseApplication.getInstance();
        Formatter formatter = new Formatter(Locale.ENGLISH);
        if (size >= tenTbThreshold) {
            double format = (double) size / tbThreshold;
            strSize =
                    formatter.format("%.1f" + context.getString(R.string.size_unit_tb), format).toString();
        } else if (size > gbThreshold) {
            long format = size / gbThreshold;
            strSize = formatter.format("%d" + context.getString(R.string.size_unit_gb), format).toString();
        } else if (size > mbThreshold) {
            long format = size / mbThreshold;
            strSize = formatter.format("%d" + context.getString(R.string.size_unit_mb), format).toString();
        } else if ((size * 100) / kbThreshold > 0) {
            long format = size / kbThreshold;
            strSize = formatter.format("%d" + context.getString(R.string.size_unit_kb), format).toString();
        } else {
            strSize = formatter.format("%d" + context.getString(R.string.size_unit_b), size).toString();
        }
        return strSize;
    }

    public static String formatStr(String format, Object... args) {
        return String.format(Locale.ENGLISH, format, args);
    }

    /**
     * 格式化空间大小
     *
     * @param size
     * @return
     */
    public static String formatStorageSizeHighAccuracy(long size) {
        String strSize = null;
        long tbThreshold = 1099511627776L; // 1024 * 1024 * 1024 * 1024
        int gbThreshold = 1073741824; // 1024 * 1024 * 1024
        int mbThreshold = 1048576; // 1024 * 1024
        int kbThreshold = 1024;

        if (size >= tbThreshold) {
            float format = (float) size / tbThreshold;
            strSize = String.format("%.1fTB", format);
        } else if (size > gbThreshold) {
            float format = (float) size / gbThreshold;
            strSize = String.format("%.1fGB", format);
        } else if (size > mbThreshold) {
            float format = (float) size / mbThreshold;
            strSize = String.format("%.1fMB", format);
        } else if ((size * 100) / kbThreshold > 0) {
            float format = (float) size / kbThreshold;
            strSize = String.format("%.1fKB", format);
        } else {
            strSize = String.format("%dB", size);
        }
        return strSize;
    }

}
