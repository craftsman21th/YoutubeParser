package com.moder.compass.base

/**
 * @author sunmeng
 * create at 2022-01-26
 * Email: sunmeng12@moder.com
 * 业务相关常量
 */
/**
 * 任务集合
 */
const val TASK_GROUP: Int = 100

/***********任务类型***********/
/**
 * 打开自动备份
 */
const val TASK_AUTO_BACKUP: Int = 1
/**
 * 文件上传
 */
const val TASK_UPLOAD_FILE: Int = 6

/**
 * 图片上传
 */
const val TASK_UPLOAD_PHOTO: Int = 7

/**
 * 视频上传
 */
const val TASK_UPLOAD_VIDEO: Int = 8

/**
 * 拍照上传
 */
const val TASK_TAKE_PICTURE: Int = 9

/**
 * 开启系统通知
 */
const val TASK_NOTIFICATION_PERMISSION = 11

/**
 * 分享
 */
const val TASK_SHARE: Int = 12
/**
 * 播放视频
 */
const val TASK_PLAY_VIDEO: Int = 14
/**
 * 查看照片
 */
const val TASK_CHECK_PHOTO: Int = 15
/**
 * 转存文件
 */
const val TASK_REDEPOSIT_FILE: Int = 16
/**
 * 使用清理功能
 */
const val TASK_USE_CLEAN: Int = 17
/**
 * 查看产品介绍页
 */
const val TASK_CHEK_INTRODUCTION: Int = 18
/**
 * 查看会员权益-H5直接完成
 */
const val TASK_CHEK_MEMBER_BENEFITS: Int = 19
/**
 * 邀请一个新用户
 */
const val TASK_INVITE_USER: Int = 20
/**
 * 开启保险箱
 */
const val TASK_OPEN_SAFE: Int = 21
/**
 * 下载成功文件
 */
const val TASK_DOWNLOAD_FILE_SUCCESS: Int = 22

/**
 * 活动分享
 */
const val TASK_H5_ACTIVITY_SHARE: Int = 23

/**
 * 打开资源圈页面
 */
const val TASK_OPEN_RESOURCE_VIDEO_PAGE: Int = 24

/**
 * 播放资源圈视频
 */
const val TASK_PLAY_RESOURCE_VIDEO: Int = 25

/**
 * 转存资源圈视频
 */
const val TASK_RESOURCE_VIDEO_SAVE: Int = 26

/**
 * 分享资源圈视频
 */
const val TASK_RESOURCE_VIDEO_SHARE: Int = 27

/**
 * 点赞资源圈视频
 */
const val TASK_RESOURCE_VIDEO_LIKE: Int = 28

/**
 * 触发视频下载器搜索
 */
const val TASK_OPEN_VIDEO_SEARCH: Int = 29

/**
 * 在线音乐播放
 */
const val TASK_PLAY_AUDIO: Int = 30

/**
 * 进入视频tab-带引导
 */
const val TASK_VIDEO_TAB_GUIDE: Int = 31

/**
 * 查看照片Tab-带引导
 */
const val TASK_CHECK_ALBUM_TAB: Int = 32

/**
 * 上传x个文件-带引导
 */
const val TASK_UPLOAD_FILE_GUIDE: Int = 33
/**
 * 转存1个外链-带引导
 */
const val TASK_SAVE_ONE_LINK: Int = 34

/**
 * 查看资源圈页面-带引导
 */
const val TASK_RESOURCE_GUIDE: Int = 35

/**
 * 查看福利中心页面-带引导
 */
const val TASK_WELFARE_CENTER_GUIDE: Int = 36

/**
 * 新手任务-邀请新用户
 */
const val TASK_INVITE_USER_GUIDE: Int = 37


/***********任务状态***********/
/**
 * 任务时间已结束
 */
const val TASK_STATUS_OVER = 0

/**
 * 任务未开始
 */
const val TASK_STATUS_NOT_STARTED = 1

/**
 * 任务进行中
 */
const val TASK_STATUS_OUTGOING = 2

/**
 * 任务完成奖励可领取
 */
const val TASK_STATUS_FINISHED_UNREWARED = 3

/**
 *  任务所在活动完成奖励已领取
 */
const val TASK_STATUS_FINISHED_REWARED = 4

/**
 * 任务所在活动结束-奖励已领取
 */
const val TASK_STATUS_FINISHED_REWARED_OVER = 5

/**
 * 在moderProvider中调用下载，注册 EventCenterHandler 时出现无法获取 message 的情况
 * 暂时未找到原因，先使用 Broadcast 替代
 */
const val DOWNLOAD_FINISH_ACTION: String = "download_finish_action"

/**
 * 新手任务类型集合
 */
val NEWBIE_TASKS_KIND = arrayOf(
    TASK_WELFARE_CENTER_GUIDE, TASK_CHECK_ALBUM_TAB, TASK_SAVE_ONE_LINK, TASK_UPLOAD_FILE_GUIDE,
    TASK_VIDEO_TAB_GUIDE, TASK_RESOURCE_GUIDE, TASK_INVITE_USER_GUIDE
)

/** 奖励信息 **/

/**
 * 空间奖励（有过期时间）
 */
const val REWARD_KIND_SPACE: Int = 3

/**
 * 体验会员（无空间权益）
 */
const val REWARD_KIND_VIP_NO_SPACE: Int = 8

/**
 * Key
 */
const val EXTRA_BOOLEAN_TAG: String = "extra_boolean_tag"

/**
 * 转存弹窗是否自动关闭的标志
 */
const val EXTRA_IS_AUTO_CLOSE_TAG: String = "extra_auto_close_tag"

/**
 * 转存弹窗是否需要setDarkMode
 */
const val EXTRA_NEED_SET_DARK_MODE = "extra_need_set_dark_mode"