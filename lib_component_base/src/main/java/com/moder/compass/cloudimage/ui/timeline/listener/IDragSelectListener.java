package com.moder.compass.cloudimage.ui.timeline.listener;

/**
 * Created by liaozhengshuang on 17/5/3.
 * 用于监听滑动过程中选中、取消选中等操作
 */

public interface IDragSelectListener {
    void setDragSelected(int index, boolean selected);
    void dragSelectEnd(int initIndex, int lastIndex);
}
