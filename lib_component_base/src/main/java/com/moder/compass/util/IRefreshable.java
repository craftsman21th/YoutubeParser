/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.moder.compass.util;

/**
 * Created by guanshuaichao on 2018/11/22.
 */
public interface IRefreshable {

    /**
     * 是否可刷新数据
     *
     * @return
     */
    boolean canRefresh();

    /**
     * 是否正在刷新
     *
     * @return
     */
    boolean isRefreshing();

    /**
     * 触发刷新
     *
     * @return
     */
    boolean triggerRefresh();

}
