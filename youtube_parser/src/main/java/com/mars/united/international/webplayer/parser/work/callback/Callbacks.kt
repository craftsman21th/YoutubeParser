package com.mars.united.international.webplayer.parser.work.callback

import com.mars.united.international.webplayer.account.posts.VideoPostListWork
import com.mars.united.international.webplayer.account.subscribe.SubscribedShortsWork
import com.mars.united.international.webplayer.account.subscribe.SubscribedVideosWork
import com.mars.united.international.webplayer.parser.model.YoutubeVideoInfo
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.work.FetchVideoInfoWork
import com.mars.united.international.webplayer.parser.work.RecommendWork
import com.mars.united.international.webplayer.parser.work.SearchWork

/**
 * @Author 陈剑锋
 * @Date 2023/8/2-11:20
 * @Desc
 */

typealias DefaultCallback = () -> Unit

typealias FetchVideoInfoWorkCallback = suspend FetchVideoInfoWork.(
    videoInfo: YoutubeVideoInfo?
) -> Unit

typealias SearchWorkCallback = SearchWork.(
    page: Int,
    pageCount: Int,
    data: List<JsonCaller>,
    errCode: Int?
) -> Unit

typealias RecommendWorkCallback = RecommendWork.(
    page: Int,
    pageCount: Int,
    data: List<JsonCaller>,
    errCode: Int?
) -> Unit

typealias VideoPostListWorkCallback = suspend VideoPostListWork.(
    page: Int,
    pageCount: Int,
    data: List<JsonCaller>,
    errCode: Int?
) -> Unit

typealias SubscribedVideosWorkCallback = suspend SubscribedVideosWork.(
    page: Int,
    pageCount: Int,
    data: List<JsonCaller>,
    errCode: Int?
) -> Unit

typealias SubscribedShortsWorkCallback = suspend SubscribedShortsWork.(
    page: Int,
    pageCount: Int,
    data: List<JsonCaller>,
    errCode: Int?
) -> Unit