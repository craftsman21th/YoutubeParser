
package com.moder.compass.base.utils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * Created by tianzengming on 2015/2/4.
 */
public class VideoInfoHelper {
    private static final String TAG = "VideoInfoHelper";

    /**
     * 视频转码阈值
     */
    public static final int VIDEO_TF_VALUE = 800;

    /**
     * 计算是否需要转码视频
     *
     * @param size 视频大小 byte
     * @param duration 视频时长 s
     * @param tfValue 转码阈值
     * @return 是否转码
     */
    public static boolean needTFVideo(long size, long duration, long tfValue) {
        DuboxLog.d(TAG, " DBG needTFVideo size:" + size + " duration:" + duration + " tfValue:" + tfValue);
        if (0 >= duration || 0 >= size || 0 >= tfValue) {
            return true;
        }
        DuboxLog.d(TAG, " DBG needTFVideo result:" + (size * 8 / duration) / 1000);

        if (((size * 8 / duration) / 1000) > tfValue) {
            return true;
        }

        return false;
    }
}
