
package com.moder.compass.imagepager

/**
 * Created by yeliangliang on 2020/9/3
 */
const val LATEST_IMAGE: Int = 1 // 最近上传

const val CLOUD_IMAGE: Int = 2 // 时间轴

const val P2PSHARE_IMAGE: Int = 3 // 点对点传输

const val FILE_IMAGE: Int = 4 // 网盘

const val FEED_IMAGE: Int = 6 // 个人主页分享动态图片

const val TASK_IMAGE: Int = 7 // 传输列表图片

const val SAFE_BOX_IMAGE: Int = 8 // 隐藏空间

const val CARD_PACKAGE_LATEST_IMAGE: Int = 9 // 我的卡包-最近上传

const val CARD_PACKAGE_FILE_IMAGE: Int = 10 // 我的卡包-网盘图片文件

const val CARD_PACKAGE_CIRCULAR_WIDGET_IMAGE: Int = 11 // 我的卡包-轮播图图片

const val SWAN_IMAGE: Int = 20 // 小程序本地

const val ALBUM_CLOUD_IMAGE: Int = 21 // 新时光轴

const val SWAN_CLOUD_IMAGE: Int = 22 // 小程序时光轴

const val ALBUM_LOCAL_IMAGE: Int = 24 // 本地相册

const val PHOTO_PREVIEW = 25 //仅提供图片预览功能

const val OFFLINE_PHOTO_PREVIEW = 26 // 离线文件预览功能，（底部只有删除按钮）

/**
 * 大图预览，下载按钮显示
 */
const val IMAGE_PAGE_VIEW_DOWNLOAD_VISIBLE = 0X100

/**
 * 大图预览，分享按钮显示
 */
const val IMAGE_PAGE_VIEW_SHARE_VISIBLE = 0X010

/**
 * 大图预览，删除按钮显示
 */
const val IMAGE_PAGE_VIEW_DELETE_VISIBLE = 0X001

/**
 * 大图预览，所有VIEW都显示
 */
const val IMAGE_PAGE_VIEW_ALL_VISIBLE = 0x111

/**
 * 大图预览，删除隐藏
 */
const val IMAGE_PAGE_VIEW_DELETE_GONE = 0x110

