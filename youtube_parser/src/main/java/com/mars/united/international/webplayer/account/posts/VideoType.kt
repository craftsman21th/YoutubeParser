package com.mars.united.international.webplayer.account.posts

/**
 * @Author 陈剑锋
 * @Date 2023/9/22-10:47
 * @Desc 视频类型
 */
sealed class VideoType {

    /**
     * 普通视频
     */
    object VIDEOS : VideoType()

    /**
     * 短视频
     */
    object SHORTS : VideoType()

}