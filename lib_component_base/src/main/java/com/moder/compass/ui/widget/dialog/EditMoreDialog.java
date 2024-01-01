/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.moder.compass.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.ConStantKt;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.util.DayNightModeKt;

/**
 * 编辑态-更多按钮的dialog
 *
 * @author wangyang34
 * @since 2019年1月10日
 */
public class EditMoreDialog extends Dialog {

    /** 内容view */
    private ViewGroup mContentView;

    /**
     * 构造
     *
     * @param activity activity
     * @param builder 构造类
     */
    public EditMoreDialog(@NonNull Activity activity, final EditMoreDialogBuilder builder) {
        super(activity, R.style.ModerDialogTheme);
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setWindowAnimations(R.style.BottomDialogAnimation);
            WindowManager.LayoutParams params = window.getAttributes();
            if (params == null) {
                params = new WindowManager.LayoutParams();
            }
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
        setContentView(R.layout.edit_more_dialog);
        mContentView = findViewById(R.id.popup_menu_item_content);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WrapEditClick(builder.getCancelListener()).onClick(v);
                DuboxLog.d("EditMoreDialog", "cancel");
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.DUBOX_FILE_OPERATE_CANCEL_CLICK);
            }
        });
        MenuItemCreator creator = new MenuItemCreator();
        if (!TextUtils.isEmpty(builder.getTittle())) {
            mContentView.addView(creator.createHeaderView(activity, builder.getTittle()));
            mContentView.addView(creator.createDivideView(activity));
        }
        int index = 0;
        for (EditMoreInfo item : builder.getItems()) {
            View view = creator.createView(activity, item, new WrapEditClick(item.mClickListener));
            mContentView.addView(view);
            // 若有设置，则添加分割线
            if (builder.getIsShowDivider()) {
                mContentView.addView(creator.createDivideBetweenItems(activity));
            } else if (index == builder.getItems().size() - 1 && builder.getIsShowDividerAboveCancel()) {
                // 最后一个item且要显示分割线
                mContentView.addView(creator.createDivideBetweenItems(activity));
            }
            index++;
        }
    }

    /** 包装点击事件 */
    private class WrapEditClick implements View.OnClickListener {

        /** 包装点击事件 */
        private View.OnClickListener mWrapClickListener;

        /** 构造
         * @param clickListener 点击
         */
        public WrapEditClick(View.OnClickListener clickListener) {
            mWrapClickListener = clickListener;
        }

        @Override
        public void onClick(View view) {
            close(mWrapClickListener, view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 关闭
     */
    public void close() {
        close(null, null);
    }

    public void close(final View.OnClickListener onClickListener, final View clickView) {
        if (isShowing()) {
            dismiss();
            if (onClickListener != null) {
                onClickListener.onClick(clickView);
            }
        }
    }

    @Override
    public void show() {
        super.show();
        float radius = DeviceDisplayUtils.dip2px(getContext(), ConStantKt.SPACE_15);
        DayNightModeKt.setDayOrNightModeForDialog(this, radius,
                        radius, 0, 0);
    }
}
