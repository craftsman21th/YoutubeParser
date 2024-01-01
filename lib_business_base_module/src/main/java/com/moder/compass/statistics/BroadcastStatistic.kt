package com.moder.compass.statistics

/**
 * 广播-发送广播
 */
private const val SEND_BROADCAST: String = "send_broadcast"

/**
 * 广播-接收广播
 */
private const val RECEIVE_BROADCAST: String = "receive_broadcast"

/**
 * 发送广播埋点
 */
@JvmOverloads
fun statisticSendBroadcast(actionName: String) {
    statisticViewEvent(SEND_BROADCAST, actionName)
}

/**
 * 接收广播埋点
 */
@JvmOverloads
fun statisticReceiveBroadcast(actionName: String?) {
    statisticViewEvent(RECEIVE_BROADCAST, actionName ?: "null")
}