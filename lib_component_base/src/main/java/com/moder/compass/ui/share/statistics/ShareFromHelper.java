package com.moder.compass.ui.share.statistics;

/**
 * 分享统计用于生成页面来源的帮助类
 *
 * @author linwentao
 * @since 2019年3月4日
 */
public class ShareFromHelper {

    /** 占位符 */
    public static final int PLACEHOLDER = 0x00;

    /** 首页 */
    public static final int HOME_TAB = 0x01;

    /** 文件页 */
    public static final int FILE_TAB = 0x02;

    /** 其他页 */
    public static final int OTHER_TAB = 0x03;

    /** 分类页 */
    public static final int CATEGORY = 0x04;

    /** 故事页 */
    public static final int STORY = 0x05;

    /** 最近页 */
    public static final int RECENT = 0x06;

    /** 收藏页 */
    public static final int COLLECTION = 0x07;

    /** 搜索页 */
    public static final int SEARCH = 0x08;

    /** 文件列表 */
    public static final int FILE_LIST = 0x09;

    /** 文件预览 */
    public static final int FILE_PREVIEW = 0x0a;

    /** 照片页 */
    public static final int PHOTO = 0x0b;

    /** 视频页 */
    public static final int VIDEO = 0x0c;

    /** 文档页 */
    public static final int DOCUMENT = 0x0d;

    /** 音乐页 */
    public static final int MUSIC = 0x0e;

    /** 应用页 */
    public static final int APPLICATION = 0x0f;

    /** 种子页 */
    public static final int BT = 0x10;

    /** 其他页 */
    public static final int OTHER = 0x11;

    /** 时光轴 */
    public static final int TIMELINE = 0x12;

    /** 智能分类 */
    public static final int CLASSIFICATION = 0x13;

    /** 最近上传 */
    public static final int RECENTLY = 0x14;

    /** 故事 */
    public static final int STORYLY = 0x15;

    /** 图片搜索页 */
    public static final int IMAGE_SEARCH = 0x16;

    /** 小说页 */
    public static final int NOVEL = 0x17;

    /** 分享tab */
    public static final int SHARE_TAB = 0x18;

    /**
     * 得到分享统计页面类型
     * 
     * @param firstLevelType    一级目录
     * @param secondLevelType   二级目录
     * @param thirdLevelType    三级目录
     * @param fourthLevelType   四级目录
     * @return
     */
    public static int getSharePageType(int firstLevelType, int secondLevelType, int thirdLevelType,
            int fourthLevelType) {
        return firstLevelType + (secondLevelType << 8)
                + (thirdLevelType << 16) + (fourthLevelType << 24);
    }

    /**
     * 得到分享统计页面类型
     *
     * @param firstLevelType    一级目录
     * @param secondLevelType   二级目录
     * @param thirdLevelType    三级目录
     * @return
     */
    public static int getSharePageType(int firstLevelType, int secondLevelType, int thirdLevelType) {
        return getSharePageType(firstLevelType, secondLevelType, thirdLevelType, PLACEHOLDER);
    }

    /**
     * 得到分享统计页面类型
     *
     * @param firstLevelType    一级目录
     * @param secondLevelType   二级目录
     * @return
     */
    public static int getSharePageType(int firstLevelType, int secondLevelType) {
        return getSharePageType(firstLevelType, secondLevelType, PLACEHOLDER);
    }

}
