package com.moder.compass.ui.share;

/**
 * 分享内容类型
 */
public enum ShareInfoType {
    GIFT_PACK_LINK, // 红包链接
    GROUP_LINK_SHARE_FILE, // 分享时 创建空群分享链接
    GROUP_LINK_CREATE_GROUP, // 建群时 创建空群分享链接
    GROUP_LINK_SINGLE_CONVERSATION, // 双人会话时 创建空群分享链接
    GROUP_LINK_GROUP_CONVERSATION, // 群聊时 分享链接
    WHEEL_LOTTERY, // 转轮抽奖
    OTHER,
}
