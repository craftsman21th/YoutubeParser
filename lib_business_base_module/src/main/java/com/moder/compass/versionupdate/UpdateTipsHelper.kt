package com.moder.compass.versionupdate

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @author sunmeng
 * create at 2021-04-25
 * Email: sunmeng12@moder.com
 *
 * 头像小红点提示
 */
object UpdateTipsHelper {

    private val showTips = MutableLiveData<Boolean>(false)

    /**
     * 在App生命周期内，点击进入过版本更新详情页就不再展示红点
     */
    var shouldShowNew = true
    @MainThread
    fun isShowTips(hasNew: Boolean) {
        showTips.value = hasNew && shouldShowNew
    }

    @MainThread
    fun getShowTips(): LiveData<Boolean> = showTips

}