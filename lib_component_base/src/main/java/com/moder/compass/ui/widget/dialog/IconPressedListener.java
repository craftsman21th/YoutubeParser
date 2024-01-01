package com.moder.compass.ui.widget.dialog;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xujing31 on 2019/5/29.
 */

/**
 * item图标监听按压变灰处理类
 *
 * @author xujing31
 * @since 2019/5/29
 */

public class IconPressedListener implements View.OnTouchListener {
    private ImageView icon;
    private TextView content;
    private ImageView afterIcon;
    private boolean isRevert;

    public IconPressedListener() {

    }

    public IconPressedListener(ImageView icon, TextView content, ImageView afterIcon) {
        this.icon = icon;
        this.content = content;
        this.afterIcon = afterIcon;
    }

    public void setRevert(boolean revert) {
        isRevert = revert;
    }

    void setIcon(ImageView icon) {
        this.icon = icon;
    }

    void setContent(TextView content) {
        this.content = content;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (icon != null) {
                    icon.setPressed(true);
                    icon.setAlpha(0.4f);
                }
                if (content != null) {
                    content.setPressed(true);
                }
                if (afterIcon != null) {
                    afterIcon.setPressed(true);
                    afterIcon.setAlpha(0.4f);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (icon != null) {
                    icon.setPressed(false);
                    icon.setAlpha(isRevert ? 1.0F : 0.4F);
                }
                if (content != null) {
                    content.setPressed(false);
                }
                if (afterIcon != null) {
                    afterIcon.setPressed(false);
                    afterIcon.setAlpha(isRevert ? 1.0F : 0.4F);
                }
                v.performClick();
                break;
            default:
                break;
        }
        return true;
    }

}
