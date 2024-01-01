package com.moder.compass.ui.manager;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.ConStantKt;
import com.moder.compass.util.DayNightModeKt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;

/**
 * 用于构建各种形式的对话框，并直接显示，不需要再次调用{@link Dialog#show()}<br/>
 * 构建对话框的方法为buildXXXXXDialog();<br/>
 * 如果需要监听对话框的确认和取消按钮的点击事件， 那么需要设置监听器{@link DialogCtrListener}<br/>
 * 如果需要监听对话框消失的事件， 那么需要设置监听器{@link DialogInterface.OnDismissListener} </p>
 * 目前提供了普通对话框TipsDialog，只有确认按钮的对话框OneButtonDialog，和内容为列表的对话框ListDialog </p> Sample:
 *
 * <pre>
 *  DialogBuilder dialogBuilder = new DialogBuilder();
 *  dialogBuilder.buildTipsDialog(this, R.string.title, R.string.content,
 *  R.string.ok, R.string.cancel);
 *  dialogBuilder.setOnDialogCtrListener(new DialogCtrListener() {
 *    {@code @Override}
 *    public void onOkBtnClick() {
 *       //do something after confirm
 *    }
 *
 *    {@code @Override}
 *    public void onCancelBtnClick() {
 *      //do something after cancel
 *    }
 *   });
 * </pre>
 *
 * com.dubox.drive.ui.manager.DialogBuilder
 *
 * @author chenyuquan <br/>
 *         create at 2012-9-27 11:09:41
 */
public class BaseDialogBuilder {

    private static final String TAG = "DialogBuilder";

    /**
     * 不显示弹窗的按钮
     */
    public static final int DISABLE_DIALOG_BUTTON = -1;

    /**
     * 不显示弹窗的按钮
     */
    public static final int DIALOG_BUTTON_DISABLE_COLOR = -1;

    public static final int DIALOG_CONTENT_TEXT_GRAVITY_LEFT = Gravity.LEFT;
    public static final int DIALOG_CONTENT_TEXT_GRAVITY_CENTER = Gravity.CENTER;

    protected LinearLayout mLoadingBox; // 增加确定按钮的loading
    protected Button mRightBtn;
    protected Button mLeftBtn;
    protected Dialog mDialog;

    /**
     * 对话框按钮事件的监听器
     */
    protected DialogCtrListener dialogListener;

    /**
     * 为对话框设置按钮的监听器
     *
     * @param listener
     */
    public void setOnDialogCtrListener(DialogCtrListener listener) {
        dialogListener = listener;
    }

    /**
     * 构建普通形式的对话框，对话框的各项显示内容，均以resId为入参
     *
     * @param activity
     * @param titleResId
     * @param contentResId
     * @param confirmTextResId
     * @param cancelTextResId
     * @return 构建完成的对话框
     */
    public Dialog buildTipsDialog(Activity activity, int titleResId, int contentResId, int confirmTextResId,
                                  int cancelTextResId) {
        return buildTipsDialog(activity, activity.getString(titleResId), activity.getString(contentResId),
                activity.getString(confirmTextResId), activity.getString(cancelTextResId), true, null);
    }


    /**
     * 构建普通形式的对话框，对话框的各项显示内容，均以resId为入参
     *
     * @param activity
     * @param titleResId
     * @param contentResId
     * @param confirmTextResId
     * @param cancelTextResId
     * @param onShowListener
     * @return 构建完成的对话框
     */
    public Dialog buildTipsDialog(Activity activity, int titleResId, int contentResId, int confirmTextResId,
                                  int cancelTextResId, final DialogInterface.OnShowListener onShowListener) {
        return buildTipsDialog(activity, activity.getString(titleResId), activity.getString(contentResId),
                activity.getString(confirmTextResId), activity.getString(cancelTextResId), true, onShowListener);
    }

    /**
     * 构建普通形式的对话框，对话框的各项显示内容，均以String为入参
     *
     * @param activity
     * @param title
     * @param content
     * @param confirmText
     * @param cancelText
     *
     * @return 构建完成的对话框
     */
    public Dialog buildTipsDialog(Activity activity, String title,
                                  String content, String confirmText, String cancelText) {
        return buildTipsDialog(activity, title, content, confirmText, cancelText, true, null);
    }

    public Dialog buildTipsDialog(Activity activity, String title,
                                  String content, String confirmText, String cancelText, boolean autoClose) {
        return buildTipsDialog(activity, title, content, confirmText, cancelText, true,
                null, autoClose);
    }

    /**
     * 构建对话框的基础方法
     *
     * @param activity
     * @param title
     * @param content
     * @param confirmText
     * @param cancelText
     * @param hasCancelButton
     *
     * @return 构建完成的对话框
     */
    private Dialog buildTipsDialog(Activity activity, String title, String content, String confirmText,
                                   String cancelText, boolean hasCancelButton,
                                   DialogInterface.OnShowListener onShowListener) {
        final Dialog dialog = buildDialogBaseProperty(activity, title, confirmText);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        if (hasCancelButton) {
            if (TextUtils.isEmpty(cancelText)) {
                cancelText = activity.getString(android.R.string.cancel);
            }
            buttonCancel.setText(cancelText);
            buttonCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onCancelBtnClick();
                    }
                    dialog.dismiss();
                }
            });

        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        final TextView dialogContent = (TextView) dialog.findViewById(R.id.text_content);
        //  设置textview多行滚动
        dialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialogContent.setText(content);
        if (TextUtils.isEmpty(title)) {
            // 没有title
            dialogContent.setTextAppearance(activity, R.style.Moder_TextAppearance_DialogNoTitle);
            dialogContent.setPadding(0, activity.getResources().getDimensionPixelSize(R.dimen.dimen_9dp), 0, 0);
        }
        if (!activity.isDestroyed() && !activity.isFinishing()) {
            changeCancelBtnStyle(activity);
            if (onShowListener != null) {
                dialog.setOnShowListener(onShowListener);
            }
            try {
                dialog.show();
                float radius = DeviceDisplayUtils.dip2px(activity, ConStantKt.SPACE_11);
                DayNightModeKt.setDayOrNightModeForDialog(dialog, radius, radius,
                        radius, radius);
            } catch (NullPointerException e) {
                // 修复MTJ崩溃
                DuboxLog.e(TAG, e.getMessage(), e);
            }
        }
        return dialog;
    }

    private Dialog buildTipsDialog(Activity activity, String title, String content, String confirmText,
                                   String cancelText, boolean hasCancelButton,
                                   DialogInterface.OnShowListener onShowListener, final boolean autoClose) {
        final Dialog dialog = buildDialogBaseProperty(activity, title, confirmText, autoClose);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        if (hasCancelButton) {
            if (TextUtils.isEmpty(cancelText)) {
                cancelText = activity.getString(android.R.string.cancel);
            }
            buttonCancel.setText(cancelText);
            buttonCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onCancelBtnClick();
                    }
                    dialog.dismiss();
                }
            });

        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        final TextView dialogContent = (TextView) dialog.findViewById(R.id.text_content);
        //  设置textview多行滚动
        dialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialogContent.setText(content);
        if (TextUtils.isEmpty(title)) {
            // 没有title
            dialogContent.setTextAppearance(activity, R.style.Moder_TextAppearance_DialogNoTitle);
            dialogContent.setPadding(0, activity.getResources().getDimensionPixelSize(R.dimen.dimen_9dp), 0, 0);
        }
        if (!activity.isFinishing()) {
            changeCancelBtnStyle(activity);
            if (onShowListener != null) {
                dialog.setOnShowListener(onShowListener);
            }
            try {
                dialog.show();
                float radius = DeviceDisplayUtils.dip2px(activity, ConStantKt.SPACE_11);
                DayNightModeKt.setDayOrNightModeForDialog(dialog, radius, radius,
                        radius, radius);
            } catch (NullPointerException e) {
                // 修复MTJ崩溃
                DuboxLog.e(TAG, e.getMessage(), e);
            }
        }
        return dialog;
    }


    /**
     * 构建只有确认按钮的对话框，对话框的各项显示内容，均以resId为入参
     *
     * @param activity
     * @param titleResId
     * @param contentResId
     * @param confirmTextResId
     *
     * @return 构建完成的对话框
     */
    public Dialog buildOneButtonDialog(Activity activity, int titleResId, int contentResId, int confirmTextResId) {
        return buildOneButtonDialog(activity, activity.getString(titleResId), activity.getString(contentResId),
                activity.getString(confirmTextResId));
    }

    /**
     * 构建只有确认按钮的对话框，对话框的各项显示内容，均以resId为入参
     *
     * @param activity
     * @param titleResId
     * @param contentResId
     * @param confirmTextResId
     *
     * @return 构建完成的对话框
     */
    public Dialog buildOneButtonDialog(Activity activity, int titleResId, int contentResId, int confirmTextResId,
                                       final DialogInterface.OnShowListener onShowListener) {

        return buildTipsDialog(activity, activity.getString(titleResId), activity.getString(contentResId),
                activity.getString(confirmTextResId), null, false, onShowListener);

    }

    /**
     * 构建只有确认按钮的对话框，对话框的各项显示内容，均以String为入参
     *
     * @param activity
     * @param title
     * @param content
     * @param confirmText
     *
     * @return 构建完成的对话框
     */
    public Dialog buildOneButtonDialog(Activity activity, String title, String content, String confirmText) {
        return buildTipsDialog(activity, title, content, confirmText, null, false, null);
    }



    /**
     * 构建对话框的基础属性，Title和ok button
     *
     * @param activity
     * @param titleId
     * @param confirmTextId
     *
     * @return
     */
    protected Dialog buildDialogBaseProperty(Activity activity, int titleId, int confirmTextId) {
        String title = titleId == -1 ? null : activity.getString(titleId);
        String confirmText = confirmTextId == -1 ? null : activity.getString(confirmTextId);
        return buildDialogBaseProperty(activity, title, confirmText);
    }

    protected Dialog buildFullDialogBaseProperty(Activity activity, int titleId, int confirmTextId) {
        String title = titleId == -1 ? null : activity.getString(titleId);
        String confirmText = confirmTextId == -1 ? null : activity.getString(confirmTextId);
        return buildFullDialogBaseProperty(activity, title, confirmText);
    }

    protected Dialog buildFullDialogBaseProperty(Activity activity, String title, String confirmText) {
        mDialog = new Dialog(activity, R.style.ModerFullScreenDialogTheme);
        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_full_base_layout, null);
        mDialog.setContentView(view);
        mLoadingBox = (LinearLayout) view.findViewById(R.id.loadingBox);
        mLeftBtn = (Button) view.findViewById(R.id.dialog_button_cancel);
        mRightBtn = (Button) mDialog.findViewById(R.id.dialog_button_ok);
        if (TextUtils.isEmpty(confirmText)) {
            mRightBtn.setVisibility(View.GONE);
        } else {
            mRightBtn.setText(confirmText);
            mRightBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onOkBtnClick();
                    }
                    mDialog.dismiss();
                }
            });
        }

        final TextView dialogTitle = (TextView) mDialog.findViewById(R.id.txt_confirmdialog_title);
        if (TextUtils.isEmpty(title)) {
            ((View) dialogTitle.getParent()).setVisibility(View.GONE);
        } else {
            dialogTitle.setText(title);
        }

        mDialog.setCanceledOnTouchOutside(false);
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        final int finalUiOptions = uiOptions;
        View decorView = mDialog.getWindow().getDecorView();
        if (decorView != null) {
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            mDialog.getWindow().getDecorView()
                                    .setSystemUiVisibility(finalUiOptions);
                        }
                    });
            decorView.setSystemUiVisibility(finalUiOptions);
        }
        return mDialog;
    }


    /**
     * 构建对话框的基础属性，Title和ok button
     *
     * @param activity
     * @param title
     * @param confirmText
     *
     * @return 构建完成的对话框
     */
    protected Dialog buildDialogBaseProperty(Activity activity, String title, String confirmText) {
        mDialog = new Dialog(activity, R.style.ModerDialogTheme);
        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_base_layout, null);
        mDialog.setContentView(view);
        mLoadingBox = (LinearLayout) view.findViewById(R.id.loadingBox);
        mLeftBtn = (Button) view.findViewById(R.id.dialog_button_cancel);
        mRightBtn = (Button) mDialog.findViewById(R.id.dialog_button_ok);

        if (TextUtils.isEmpty(confirmText)) {
            mRightBtn.setVisibility(View.GONE);
        } else {
            mRightBtn.setText(confirmText);
            mRightBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onOkBtnClick();
                    }
                    mDialog.dismiss();
                }
            });
        }

        final TextView dialogTitle = (TextView) mDialog.findViewById(R.id.txt_confirmdialog_title);
        if (TextUtils.isEmpty(title)) {
            ((View) dialogTitle.getParent()).setVisibility(View.GONE);
        } else {
            dialogTitle.setText(title);
        }

        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    protected Dialog buildDialogBaseProperty(Activity activity, String title, String confirmText,
            final boolean autoClose) {
        mDialog = new Dialog(activity, R.style.ModerDialogTheme);
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_base_layout, null);
        mDialog.setContentView(view);
        mLoadingBox = (LinearLayout) view.findViewById(R.id.loadingBox);
        mLeftBtn = (Button) view.findViewById(R.id.dialog_button_cancel);
        mRightBtn = (Button) mDialog.findViewById(R.id.dialog_button_ok);

        if (TextUtils.isEmpty(confirmText)) {
            mRightBtn.setVisibility(View.GONE);
        } else {
            mRightBtn.setText(confirmText);
            mRightBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onOkBtnClick();
                    }
                    if (autoClose) {
                        mDialog.dismiss();
                    }
                }
            });
        }

        final TextView dialogTitle = (TextView) mDialog.findViewById(R.id.txt_confirmdialog_title);
        if (TextUtils.isEmpty(title)) {
            ((View) dialogTitle.getParent()).setVisibility(View.GONE);
        } else {
            dialogTitle.setText(title);
        }

        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    public void switch2LoadingMode() {
        if (mLoadingBox != null && mRightBtn != null && mDialog != null) {
            mLoadingBox.setVisibility(View.VISIBLE);
            mRightBtn.setVisibility(View.GONE);
            mLeftBtn.setEnabled(false);
            mDialog.setCancelable(false);
        }
    }

    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    /**
     * 判断对话框是否正在显示
     *
     * @return
     */
    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    /**
     * 设置对话框是否可以取消
     *
     * @param cancelable
     */
    public void setCancelable(boolean cancelable) {
        if (null != mDialog) {
            mDialog.setCancelable(cancelable);
        }
    }


    /**
     * 当仅显示cancel按钮的时候，需要改变按钮的style
     */
    private void changeCancelBtnStyle(Activity activity) {
        if (mDialog != null) {
            final Button buttonCancel = (Button) mDialog.findViewById(R.id.dialog_button_cancel);
            final Button centerButton = (Button) mDialog.findViewById(R.id.dialog_button_center);
            final Button buttonOk = (Button) mDialog.findViewById(R.id.dialog_button_ok);
            // 仅显示取消按钮
            if (buttonCancel.getVisibility() == View.VISIBLE && centerButton.getVisibility() == View.GONE
                    && buttonOk.getVisibility() == View.GONE) {
                buttonCancel.setTextColor(ContextCompat.getColorStateList(activity,
                        R.color.normal_dialog_confirm_button_selector));
            }
        }
    }

    /**
     * 基础dialog设置自定义contentview
     *
     * @param activity
     * @param titleId
     * @param confirmTextId
     * @param customViewId
     *
     * @return
     */
    public Dialog buildCustomViewDialog(Activity activity, int titleId,
                                        int confirmTextId, @LayoutRes int customViewId) {
        return buildCustomViewDialog(activity, titleId, confirmTextId, R.string.cancel, customViewId);
    }

    /**
     * 基础dialog设置自定义contentview
     *
     * @param activity
     * @param titleId
     * @param confirmTextId
     * @param customViewId
     *
     * @return
     */
    public Dialog buildCustomViewDialog(Activity activity, int titleId, int confirmTextId, int cancelTextId,
                                        @LayoutRes int customViewId) {
        return buildCustomViewDialog(activity, titleId, confirmTextId, cancelTextId, DISABLE_DIALOG_BUTTON,
                customViewId);
    }


    /**
     * 基础dialog设置自定义contentview
     *
     * @param activity
     * @param titleId
     * @param confirmTextId
     * @param cancelTextId
     * @param customViewId
     *
     * @return
     */
    public Dialog buildCustomViewDialog(Activity activity, int titleId, int confirmTextId, int cancelTextId,
                                        int centerTextId, @LayoutRes int customViewId) {
        return buildCustomViewDialog(activity, titleId, confirmTextId, cancelTextId,
                centerTextId, customViewId, false);
    }


    public Dialog buildCustomViewDialog(Activity activity, int titleId, int confirmTextId, int cancelTextId,
                                        int centerTextId, @LayoutRes int customViewId, boolean isFullScreen) {

        Dialog dialog;
        if (isFullScreen) {
            dialog = buildFullDialogBaseProperty(activity, titleId, confirmTextId);
        } else {
            dialog = buildDialogBaseProperty(activity, titleId, confirmTextId);
        }
        dialog.findViewById(R.id.text_content).setVisibility(View.GONE);
        LinearLayout contentLayout = (LinearLayout) dialog.findViewById(R.id.contentLayout);
        LayoutInflater.from(activity).inflate(customViewId, contentLayout);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);

        if (cancelTextId != DISABLE_DIALOG_BUTTON) {
            buttonCancel.setText(cancelTextId);
            final Dialog finalDialog = dialog;
            buttonCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onCancelBtnClick();
                    }
                    finalDialog.dismiss();
                }
            });
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        // 显示中间按钮
        if (centerTextId != DISABLE_DIALOG_BUTTON) {
            Button centerButton = (Button) dialog.findViewById(R.id.dialog_button_center);
            centerButton.setVisibility(View.VISIBLE);
            centerButton.setText(centerTextId);
            final Dialog finalDialog1 = dialog;
            centerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (dialogListener != null && dialogListener instanceof ExpandDialogCtrListener) {
                        ((ExpandDialogCtrListener) dialogListener).onCenterBtnClick();
                    }
                    finalDialog1.dismiss();
                }
            });
        }
        return dialog;

    }

    /**
     * 使对话框点击确定和取消后，并不会关闭
     *
     * @param dialog
     *
     * @return 构建完成的对话框
     */
    public Dialog clearDialogDismiss(Dialog dialog) {
        if (null == dialog) {
            return null;
        }
        final View buttonOk = dialog.findViewById(R.id.dialog_button_ok);
        if (null != buttonOk) {
            buttonOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onOkBtnClick();
                    }
                }
            });
        }
        final View centerCancel = dialog.findViewById(R.id.dialog_button_center);
        if (null != centerCancel) {
            centerCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null && dialogListener instanceof ExpandDialogCtrListener) {
                        ((ExpandDialogCtrListener) dialogListener).onCenterBtnClick();
                    }
                }
            });
        }
        return dialog;
    }

    /**
     * @param activity
     * @param titleRes
     * @param contentRes
     * @param buttonRes
     *
     * @return
     */
    public Dialog buildLoadingDialog(Activity activity, int titleRes, int contentRes, int buttonRes) {
        final Dialog dialog = buildDialogBaseProperty(activity, titleRes, buttonRes);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);

        dialog.findViewById(R.id.content_loading_layout).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.contentLayout).setVisibility(View.GONE);
        dialog.findViewById(R.id.dialog_footer).setVisibility(View.GONE);

        final Animation animationRotate =
                AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.clockwise_rotate_animation);
        animationRotate.setInterpolator(new LinearInterpolator());

        dialog.findViewById(android.R.id.icon).startAnimation(animationRotate);

        buttonCancel.setVisibility(View.GONE);

        final TextView dialogContent = (TextView) dialog.findViewById(R.id.text_loading_content);
        dialogContent.setMovementMethod(ScrollingMovementMethod.getInstance()); // 设置textview多行滚动
        dialogContent.setText(contentRes >= 0 ? activity.getString(contentRes) : "");
        changeCancelBtnStyle(activity);
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    /**
     * 构建引导dialog
     * 样式 下方只有一个确认按钮。且头部图片区域没有最低高度
     * @param activity activity
     * @param contentText 对话框主内容
     * @param subContentText  对话框副内容
     * @param confirmText 确认按钮文案
     * @param imageResId 对话框顶部图片ID
     * @return
     */
    public Dialog buildGuideViewDialog(Activity activity, String contentText, String subContentText,
                                       String confirmText, @DrawableRes int imageResId) {
        if (null == activity) {
            return null;
        }
        final Dialog dialog = new Dialog(activity, R.style.ModerDialogTheme);
        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_image_layout, null);
        dialog.setContentView(view);
        final ImageView content = dialog.findViewById(R.id.dialog_content_image);
        content.setImageResource(imageResId);
        final Button buttonSingle = dialog.findViewById(R.id.dialog_button_one);
        final TextView contentView = dialog.findViewById(R.id.content_info);
        final TextView subContent = dialog.findViewById(R.id.sub_content);

        final View singleFootView = dialog.findViewById(R.id.dialog_footer_one_button);
        if (!TextUtils.isEmpty(contentText)) {
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(contentText);
        } else {
            contentView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(subContentText)) {
            subContent.setVisibility(View.VISIBLE);
            subContent.setText(subContentText);
        } else {
            subContent.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(confirmText)) {
            singleFootView.setVisibility(View.VISIBLE);
            buttonSingle.setText(confirmText);
            buttonSingle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onOkBtnClick();
                    }
                    dialog.dismiss();
                }
            });
        }

        if (!activity.isFinishing()) {
            changeCancelBtnStyle(activity);
            try {
                dialog.show();
                float radius = DeviceDisplayUtils.dip2px(activity, ConStantKt.SPACE_7);
                DayNightModeKt.setDayOrNightModeForDialog(dialog, radius, radius,
                        radius, radius);
            } catch (NullPointerException e) {
                // 修复MTJ崩溃
                DuboxLog.e(TAG, e.getMessage(), e);
            }
        }
        return dialog;
    }
}