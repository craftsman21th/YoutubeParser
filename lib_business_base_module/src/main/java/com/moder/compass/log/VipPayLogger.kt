package com.moder.compass.log

import com.dubox.drive.kernel.android.util.RealTimeUtil
import com.moder.compass.statistics.StatisticsLogForMutilFields
import com.moder.compass.statistics.statisticViewEvent
import com.google.gson.Gson
import rubik.generate.context.dubox_com_dubox_drive_vip.VipContext.Companion.isVip

/**
 * 会员购买日志串联
 * Created by yeliangliang on 2021/2/19
 */
private const val KEY = "android_vip_pay_log"

/**
 * 日志串联错误码
 */
private const val RESULT_CODE_SUCCESS: String = "1"

private const val RESULT_CODE_FAILED: String = "0"

/**
 * op = key: android_vip_pay_log
 * other0 = logId: android端是server的订单id-serverOrderId
 * other1 = event: 行为
 * other2 = eventResult: 行为的结果
 * other3 = time: 发生时间
 * other4 = reason: 原因
 * other5 = reason: 结果码
 */
private val gson by lazy { Gson() }

/**
 * 
 */
fun statisticVipPayLog(logId: String, event: String, eventResult: Any?, reason: Any?, isSuccess: Boolean = false) {
    statisticViewEvent(KEY, logId, event, gson.toJson(eventResult), RealTimeUtil.getTime().toString(), gson.toJson(reason), if (isSuccess) RESULT_CODE_SUCCESS else RESULT_CODE_FAILED)
}

// 用户发起购买
const val KEY_VIP_PAY_USER_CLICK_BUY = "key_vip_pay_user_click_buy"

// 协议调起
const val KEY_VIP_PAY_HYBRID_INVOKE = "key_vip_pay_hybrid_invoke"

// 创建订单
const val KEY_VIP_PAY_CREATE_ORDER = "key_vip_pay_create_order"

// google 商店
const val KEY_VIP_PAY_GOOGLE_PLAY = "key_vip_pay_google_play"

// google支付完成后上报凭据
const val KEY_VIP_PAY_SUCCESS_REPORT_TOKEN = "key_vip_pay_success_report_token"

// on resume 时上报凭据
const val KEY_VIP_PAY_ONRESUME_REPORT_TOKEN = "key_vip_pay_onresume_report_token"

// 凭据消耗
const val KEY_VIP_PAY_GOOGLE_TOKEN_CONSUME = "key_vip_pay_google_token_consume"

// 支付成功后刷新会员信息
const val KEY_VIP_PAY_SUCCESS_REFRESH_VIPINFO = "key_vip_pay_success_refresh_vipinfo"

/**
 * 统计会员的权益行为，用来分析购买会员以后的行为
 */
fun statisticVipPremiumLog(event: String) {
    if (isVip() != true) return
    StatisticsLogForMutilFields.getInstance().updateCount(
        KEY_PREMIUM_AGENT_OP, event, RealTimeUtil.getTime().toString())
}

const val KEY_PREMIUM_AGENT_OP = "premium_agent_op" //OP
const val KEY_PREMIUM_AGENT_UPLOAD_SPACE = "premium_agent_upload_space" //上传
const val KEY_PREMIUM_AGENT_RECYCLE_SPACE = "premium_agent_recycle_space" //还原
const val KEY_PREMIUM_AGENT_TRANSFER = "premium_agent_transfer" //转存
const val KEY_PREMIUM_AGENT_TRANSFER_SPACE = "premium_agent_transfer_space" //转存空间
const val KEY_PREMIUM_AGENT_TRANSFER_COUNT = "premium_agent_transfer_count" //转存数量
const val KEY_PREMIUM_AGENT_VIDEO_SPEED = "premium_agent_video_speed" //倍速
const val KEY_PREMIUM_AGENT_VIDEO_CLARITY = "premium_agent_video_clarity" //清晰度
const val KEY_PREMIUM_AGENT_VIDEO_BACKUP = "premium_agent_video_backup" //视频备份
const val KEY_PREMIUM_AGENT_BIG_FILE = "premium_agent_big_file" //大文件上传
const val KEY_PREMIUM_AGENT_DOWNLOAD = "premium_agent_download" //下载

/**
 * 会员购买的动因
 */
const val KEY_VIP_BUY_FROM_SPACE = 1 //付费动因 空间不足
const val KEY_VIP_BUY_FROM_TRANSFER = 2 //付费动因 转存超限
const val KEY_VIP_BUY_FROM_VIDEO_AUTO_BACKUP = 3 //付费动因 视频自动备份
const val KEY_VIP_BUY_FROM_BIG_FILE_BACKUP = 4 //付费动因 大文件备份
const val KEY_VIP_BUY_FROM_VIDEO_SPEED = 5 //付费动因 视频倍速
const val KEY_VIP_BUY_FROM_VIDEO_QUALITY = 6 //付费动因 视频清晰度
const val KEY_VIP_BUY_FROM_DOWNLOAD = 7 //付费动因 高速下载
const val KEY_VIP_BUY_FROM_USER_CENTER = 8 //付费动因 个人中心
const val KEY_VIP_BUY_FROM_HOME_CARD = 9 //付费动因 首页运营卡片
const val KEY_VIP_BUY_FROM_ENCRYPTED_SPACE = 10 //付费动因 加密空间
const val KEY_VIP_BUY_FROM_REMIT_AD = 11 //付费动因 会员免广告
const val KEY_VIP_BUY_FROM_REWARD_DOWNLOAD_COUNT_DOWN = 12 //付费动因 激励视频提速倒计时
const val KEY_VIP_BUY_FROM_UNZIP = 13 // 付费动因，云解压
const val KEY_VIP_BUY_FROM_DELETE_FILE = 14 // 付费动因，删除文件
const val KEY_VIP_BUY_FROM_SUB_GUIDE = 15 // 付费动因，订阅引导
const val KEY_VIP_BUY_FROM_TAB_VIDEO_BACKUP = 16 // tab video下自动备份卡片进入付费动因
const val KEY_VIP_BUY_FROM_HOME_TITLE_VIP_ENTER = 17 // 首页title会员入口
const val KEY_VIP_BUY_FROM_AUDIO_SPEED_PLAY = 19 // 音频倍速播放
const val KEY_VIP_BUY_FROM_HOME_GRACE_CARD = 20 // 宽限期卡片
const val KEY_VIP_BUY_FROM_BACKUP_FOLDER = 21 // 文件夹备份
const val KEY_VIP_BUY_FROM_NO_AD = 22 // 免广告
const val KEY_VIP_BUY_FROM_QUICK_UPLOAD = 23 // 极速上传 - 图片上传引导
const val KEY_VIP_BUY_FROM_QUICK_UPLOAD_PUSH = 24 // 极速上传-push
const val KEY_VIP_BUY_FROM_AF_NEW_INSTALL_JUMP = 25 // 使用AF的投放渠道用户，新安装打开后直接进入收银台
const val KEY_VIP_BUY_FROM_PICTURE_EDIT = 26 // 照片编辑
const val KEY_VIP_BUY_FROM_APP_OPEN_GUIDE = 27 // 开屏引导
const val KEY_VIP_BUY_FROM_RESOURCE_VIDEO = 28 // 资源圈视频广告
const val KEY_VIP_BUY_FROM_BONDING_VIDEO = 29 // 资源圈贴片广告
const val KEY_VIP_BUY_FROM_UPLOAD_VIDEO = 30  // 上传视频
const val KEY_VIP_BUY_FROM_RESTORE_FILE: Int = 31  // 来自回收站
const val KEY_VIP_BUY_FROM_CLEAN_FILE: Int = 32  // 来自清理模块，该值在清理模块中定义，修改时请同步清理模块
const val KEY_VIP_BUY_FROM_VIDEO_BANNER: Int = 33  // 来自视频播放中的banner-loading引导

