package com.moder.compass.transfer.io.model;

import android.annotation.SuppressLint
import com.dubox.drive.network.base.Response
/**
 * Created by libin09 on 2015/11/28.查询p2p下载的响应
 */
@SuppressLint("ParcelCreator")
class QueryFgidResponse(
    /**
     * 任务标识
     */
    @JvmField
    val fgid: String? = null) : Response()