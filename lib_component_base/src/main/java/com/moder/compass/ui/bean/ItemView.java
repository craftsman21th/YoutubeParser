package com.moder.compass.ui.bean;

import android.view.View;

/**
 * ViewPager显示的PageItem
 * 
 * @author 孙奇 <br/>
 *         create at 2013-3-8 下午02:30:45
 */
public interface ItemView {
    /**
     * 获取itemView
     * 
     * @return
     * @author 孙奇 V 1.0.0 Create at 2013-3-8 下午02:24:28
     */
    View getItemView();

    void clear();
}
