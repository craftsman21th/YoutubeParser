/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.moder.compass.ui.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moder.compass.component.base.R;

/**
 * 编辑条目creator
 *
 * @author wangyang34
 * @since 2019年1月10日
 */
public class MenuItemCreator {

    /**
     * 添加菜单项
     *
     * @param item
     */
    public View createView(Context context, EditMoreInfo item, View.OnClickListener clickListener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.popup_menu_item_layout, null);
        itemView.setOnClickListener(clickListener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.menu_item_height));
        itemView.setLayoutParams(lp);
        if (item.mGravity != Gravity.NO_GRAVITY) {
            ((LinearLayout) itemView).setGravity(item.mGravity);
        }
        ImageView icon = itemView.findViewById(R.id.popup_menu_item_icon);
        if (item.mIcon != null) {
            icon.setImageDrawable(item.mIcon);
            icon.setVisibility(View.VISIBLE);
            icon.setEnabled(item.mIsEnable);
            icon.setSelected(item.mIsSelected);
            icon.setAlpha(item.mIsEnable ? 1F : 0.6F);
        }
        TextView content = itemView.findViewById(R.id.popup_menu_item_content);
        if (item.mTitleTextStyle != -1) {
            content.setTextAppearance(context, item.mTitleTextStyle);
        }
        if (!TextUtils.isEmpty(item.mTitle)) {
            content.setText(item.mTitle);
            content.setEnabled(item.mIsEnable);
            content.setSelected(item.mIsSelected);
        }
        IconPressedListener listener = new IconPressedListener(icon, content, null);
        itemView.setOnTouchListener(listener);
        if (item.mIsSetGray) {
            itemView.setAlpha(0.4f);
        }
        return itemView;
    }

    /**
     * 添加标题项
     */
    public View createHeaderView(Context context, String text) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tittleView = inflater.inflate(R.layout.popup_menu_tittle_layout, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.menu_item_height));
        tittleView.setLayoutParams(lp);
        TextView content = tittleView.findViewById(R.id.popup_menu_tittle_content);
        content.setText(text);
        return tittleView;
    }


    /**
     * 添加标题项
     */
    public View createDivideView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.popup_menu_divide_layout, null);
    }

    /**
     * 添加菜单分割线
     */
    public View createDivideBetweenItems(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.popup_menu_divider_between_items_layout, null);
    }
}
