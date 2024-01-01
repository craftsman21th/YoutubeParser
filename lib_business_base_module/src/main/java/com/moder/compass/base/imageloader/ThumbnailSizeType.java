package com.moder.compass.base.imageloader;

/**
 * 缩略图尺寸类型,用于在拼装缩略图URL的时候区别不同尺寸
 */
public enum ThumbnailSizeType {
    /**
     * 48 x 48 x displayMetrics.density
     */
    THUMBNAIL_SIZE_48,
    /**
     * 64 x 64 x displayMetrics.density
     */
    THUMBNAIL_SIZE_64,
    /**
     * 96x96 x displayMetrics.density
     */
    THUMBNAIL_SIZE_96,
    /**
     * 144 x 144 displayMetrics.density
     */
    THUMBNAIL_SIZE_144,
    /**
     * 200x200 x displayMetrics.density
     */
    THUMBNAIL_SIZE_200,
    /**
     * 300x300 x displayMetrics.density
     */
    THUMBNAIL_SIZE_300,
    /**
     * 全量预加载缩略图 64x64 跟像素密度无关
     */
    THUMBNAIL_FULL_PRELOAD_SIZE_64,
    /**
     * 全屏幕尺寸的缩略图
     */
    THUMBNAIL_FULL_SCREEN_SIZE,
    /**
     * 1600*1600缩略图
     */
    THUMBNAIL_MAX_SIZE_1600
}
