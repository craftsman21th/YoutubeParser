package com.mars.united.international.webplayer.parser

import com.mars.united.international.webplayer.parser.model.YoutubeVideoInfo

/**
 * @Author 陈剑锋
 * @Date 2023/7/12-18:58
 * @Desc TODO: Youtube 视频信息缓存工具
 */
object YoutubeVideoInfoCache {

    fun get(id: String, onResult: (YoutubeVideoInfo?) -> Unit) {
        onResult.invoke(null)
    }

    fun save(videoInfo: YoutubeVideoInfo?) {
    }

}