package com.moder.compass.message

/**
 * @author sunmeng
 * create at 2022-02-21
 * Email: sunmeng12@moder.com
 *
 * 用于区分不同业务的，列表页 item 的布局
 */
enum class MessageListViewType(val value: Int) {
    ALL(0),
    RES(1),
    SYS(2),
    RES_EMPTY(-1),
    SYS_EMPTY(-2),
    NOT_SUPPORT(-3),
    LOADING(-4)
}