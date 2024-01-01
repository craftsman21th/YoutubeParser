package com.moder.compass.ui.adapter.databinding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.moder.compass.ui.widget.EmptyView
import com.dubox.glide.Glide
import com.dubox.glide.request.RequestOptions
import com.mars.united.widget.smartrefresh.SmartRefreshLayout
import com.mars.united.widget.smartrefresh.api.RefreshFooter
import com.mars.united.widget.smartrefresh.api.RefreshHeader
import com.mars.united.widget.smartrefresh.listener.OnLoadMoreListener
import com.mars.united.widget.smartrefresh.listener.OnRefreshListener

/**
 * @Author 陈剑锋
 * @Date 2023/4/23-18:56
 * @Desc
 */

/**
 * 加载远程图片
 * @receiver ImageView
 * @param remotePicUrl String?
 * @param defaultPicRes Drawable?
 */
@BindingAdapter(value = ["remotePicUrl", "defaultPicRes"], requireAll = true)
fun ImageView.loadRemotePic(
    remotePicUrl: String? = null,
    defaultPicRes: Drawable? = null
) {
    remotePicUrl ?: return
    Glide.with(context).load(remotePicUrl).apply {
        if (defaultPicRes != null) {
            apply(RequestOptions().placeholder(defaultPicRes))
        }
    }.into(this)
}

/**
 * 设置视图可见性
 */
@BindingAdapter(value = ["visible", "forceGone"], requireAll = false)
fun View.setVisible(visible: Boolean?, forceGone: Boolean? = true) {
    if (visible == true) {
        visibility = View.VISIBLE
    } else {
        if (forceGone != false) {
            visibility = View.GONE
        } else {
            visibility = View.INVISIBLE
        }
    }
}

/**
 * 绑定 下拉刷新布局 监听器
 */
@BindingAdapter(value = ["onRefresh", "onLoadMore"], requireAll = false)
fun SmartRefreshLayout.bindOnRefreshListener(
    onRefreshListener: OnRefreshListener?,
    onLoadMoreListener: OnLoadMoreListener?,
) {
    onRefreshListener?.let {
        setOnRefreshListener(it)
    }
    onLoadMoreListener?.let {
        setOnLoadMoreListener(it)
    }
}

/**
 * 绑定 下拉刷新布局 Header Footer
 */
@BindingAdapter(value = ["header", "footer"], requireAll = false)
fun SmartRefreshLayout.bindOnRefreshListener(
    header: RefreshHeader?,
    footer: RefreshFooter?,
) {
    header?.let {
        setRefreshHeader(it)
    }
    footer?.let {
        setRefreshFooter(it)
    }
}

/**
 * 绑定 下拉刷新布局 可用状态
 */
@BindingAdapter("enableRefresh")
fun SmartRefreshLayout.bindEnableRefresh(
    isEnable: Boolean?
) {
    isEnable ?: return
    setEnableRefresh(isEnabled)
}

/**
 * 绑定 EmptyView forwardBtn 可见状态
 */
@BindingAdapter("forwardBtnVisible")
fun EmptyView.bindForwardBtnVisible(
    isEnable: Boolean?
) {
    isEnable ?: return
    setForwardVisibility(
        if (isEnable) {
            View.VISIBLE
        } else {
            View.GONE
        }
    )
}

/**
 * 绑定 EmptyView forwardBtn 文案
 */
@BindingAdapter("forwardBtnText")
fun EmptyView.bindForwardBtnText(
    text: CharSequence?
) {
    text ?: return
    setForwardBtnText(text)
}

/**
 * 绑定 view isEnable状态
 */
@BindingAdapter(value = ["bindEnable", "defaultEnable"], requireAll = false)
fun View.bindEnable(
    enable: Boolean?, defaultEnable: Boolean? = null
) {
    if (enable != null) {
        isEnabled = enable
    } else if (defaultEnable != null) {
        isEnabled = defaultEnable
    }
}