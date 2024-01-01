package com.moder.compass.server

/**
 * type 类型（旧的）
 */
const val TAG_TYPE_TYPE: String = "type"

/**
 * year类型(旧的)
 */
const val TAG_TYPE_YEAR: String = "year"

/**
 * 一级页面 适用于pavbox的一级explore
 */
const val TAG_TYPE_CHANNEL: String = "channel"

/**
 * 金刚位  适用于pavbox的一级explore feed 下的金刚位
 */
const val TAG_TYPE_EXT: String = "ext"

/**
 * 资源圈产品类型：PavoBox
 */
const val CHANNEL_PAVO_BOX: Int = 2

/**
 * 参数
 */
const val PARAM_RES_TYPE: String = "param_res_type"

/**
 * 参数
 */
const val PARAM_PAGE_INDEX: String = "param_page_index"

/**
 * 参数
 */
const val PARAM_HOT_ORDER_TYPE: String = "param_hot_order_type"

/**
 * 参数
 */
const val PARAM_RESCYCLE_LABEL_AGGREGATION: String = "param_rescycle_label_aggregation"

/**
 * 二级页面：其他非来源
 */
const val EXT_LABEL_ID_OTHERS: Int = -1

/**
 * 二级页面：金刚位来源: 用于区别旧的资源圈轻形态全部视频页面
 */
const val EXT_LABEL_ID_ALL: Int = 1

/**
 * 二级页面：金刚位来源
 */
const val EXT_LABLE_ID_SAVE: Int = 2

/**
 * 二级页面：金刚位来源
 */
const val EXT_LABLE_ID_SHARE: Int = 3

/**
 * 二级页面：首页点击 more
 */
const val EXT_LABEL_HOME_MORE: Int = 4

/**
 * 资源类型0 不分类型 1 电影 2 电视剧 3 动漫 4 成人 5 最新资源(不含成人)
 */


/**
 * 资源圈相关请求临时pagetoken
 */
var PAGE_TOKEN_HOT_LIST: String = ""
var PAGE_TOKEN_RES_AGGREGATION: String = ""
var PAGE_TOKEN_HOT_RESOURCE: String = ""
var LOCATION_DEFAULT_HOT_RESOURCE: String = ""
var LOCATION_HOME_HOT_RESOURCE: String = "homepage"
var TYPE_ID_UPDATE_FROM_YOUR_FOLLOWING: Int = 7

enum class ResType(val type: Int) {
    ALL(0),
    MOVIE(1),
    TV_SHOW(2),
    ANIME(3),
    ADULT(4);

    companion object {
        fun from(findValue: Int): ResType = values().first { it.type == findValue }
    }
}

/**
 * 入参：
 * 热门排序的类型
 * plays_pv 播放pv
 * transfer_pv 转存pv
 * share_pv 分享pv
 */
enum class HotOrderType(val hotOrderType: String) {
    PLAYS_PV("plays_pv"),
    TRANSFER_PV("transfer_pv"),
    SHARE_PV("share_pv");

    companion object {
        fun from(hotOrderType: String): HotOrderType =
            values().first { it.hotOrderType == hotOrderType }
    }
}