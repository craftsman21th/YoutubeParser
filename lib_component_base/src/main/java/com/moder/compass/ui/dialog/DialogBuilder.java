package com.moder.compass.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BulletSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.moder.compass.BaseApplication;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.ui.manager.BaseDialogBuilder;
import com.moder.compass.ui.manager.DialogCtrListener;
import com.mars.united.core.os.ResourceKt;
import com.moder.compass.ui.manager.BaseDialogBuilder;
import com.moder.compass.ui.manager.DialogCtrListener;
import com.moder.compass.util.DayNightModeKt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class DialogBuilder extends BaseDialogBuilder {

    private static final String TAG = "DialogBuilder";

    /**
     * 不显示弹窗的按钮
     */
    public static final int DIALOG_BUTTON_DISABLE_COLOR = -1;

    public static final int DIALOG_CONTENT_TEXT_GRAVITY_LEFT = Gravity.LEFT;
    public static final int DIALOG_CONTENT_TEXT_GRAVITY_CENTER = Gravity.CENTER;


    /**
     * 构建带复选框的普通形式的对话框
     * @param activity
     * @param titleResId
     * @param contentResId
     * @param confirmTextResId
     * @param cancelTextResId
     * @param checkBoxTextResId
     * @param onCheckedChangeListener
     * @return
     */
    public Dialog buildTipsWithCheckBoxDialog(Activity activity, int titleResId, int contentResId, int confirmTextResId,
                                              int cancelTextResId, @StringRes int checkBoxTextResId,
                                              @Nullable
                                                      CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        final Dialog dialog = buildTipsDialog(activity, activity.getString(titleResId),
                activity.getString(contentResId), activity.getString(confirmTextResId),
                activity.getString(cancelTextResId));

        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.check_box);
        checkBox.setText(checkBoxTextResId);
        checkBox.setVisibility(View.VISIBLE);

        if (onCheckedChangeListener != null) {
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        return dialog;
    }

    public Dialog buildTipsWithCheckBoxDialog(Activity activity, String title, String content, String confirmText,
                                              String cancelText, String checkBoxText, boolean check,
                                              @Nullable
                                                      CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        final Dialog dialog = buildTipsDialog(activity, title, content, confirmText, cancelText);

        final CheckBox checkBox = dialog.findViewById(R.id.check_box);
        checkBox.setText(checkBoxText);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(check);
        dialog.setCancelable(false);

        if (onCheckedChangeListener != null) {
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
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

    public Dialog buildOneFooterDialog(Activity activity, String title, String content, String confirmText) {
        return buildFooterDialog(activity, content, title, confirmText);
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
        // 设置textview多行滚动
        dialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialogContent.setText(content);
        if (!activity.isFinishing()) {
            changeCancelBtnStyle(activity);
            if (onShowListener != null) {
                dialog.setOnShowListener(onShowListener);
            }
            try {
                dialog.show();
                float radius = DeviceDisplayUtils.dip2px(activity, 12);
                DayNightModeKt.setDayOrNightModeForDialog(dialog, radius, radius,
                        radius, radius);
            } catch (Exception e) {
                // 修复MTJ崩溃
                DuboxLog.e(TAG, e.getMessage(), e);
            }
        }
        return dialog;
    }


    /**
     * 构建显示 content footer 的对话框
     * @param activity
     * @param title
     * @param content
     * @param confirmText
     * @return
     */
    private Dialog buildFooterDialog(Activity activity, String title, String content, String confirmText) {
        final Dialog dialog = buildDialogBaseProperty(activity, title, confirmText);
        final View linearLayoutFooter = dialog.findViewById(R.id.dialog_footer);
        linearLayoutFooter.setVisibility(View.GONE);
        final TextView tvFooter = dialog.findViewById(R.id.tv_footer);
        tvFooter.setVisibility(View.VISIBLE);
        tvFooter.setText(confirmText);
        tvFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onCancelBtnClick();
                }
                dialog.dismiss();
            }
        });
        final TextView dialogContent = dialog.findViewById(R.id.text_content);
        // 设置textview多行滚动
        dialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialogContent.setText(content);
        dialogContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        dialogContent.setTextColor(activity.getResources().getColor(R.color.color_333333));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialogContent.getLayoutParams();
        layoutParams.topMargin = ResourceKt.dip2px(activity, 30);
        dialogContent.setLayoutParams(layoutParams);

        if (!activity.isFinishing()) {
            if (dialog != null) {
                dialog.show();
            }
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
        // 设置textview多行滚动
        dialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialogContent.setText(contentRes >= 0 ? activity.getString(contentRes) : "");
        changeCancelBtnStyle(activity);
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    /**
     * 构建内容为列表的对话框，对话框除了列表的各项显示内容，均以resId为入参
     *
     * @param activity
     * @param titleResId
     * @param contentResId
     * @param confirmTextResId
     * @param cancelTextResId
     * @param items             列表项集合
     * @param selectedPosition  列表的选择位置
     * @param itemClickListener 列表项的点击事件监听器
     *
     * @return 构建完成的对话框
     */
    public Dialog buildListDialog(Activity activity, int titleResId, int contentResId, int confirmTextResId,
                                  int cancelTextResId, String[] items, int selectedPosition,
                                  OnItemClickListener itemClickListener) {
        return buildListDialog(activity, titleResId, contentResId, confirmTextResId,
                cancelTextResId, items, null, null, selectedPosition, itemClickListener);
    }

    /**
     * 构建内容为列表的对话框，对话框除了列表的各项显示内容，均以resId为入参
     *
     * @param activity
     * @param titleResId
     * @param contentResId
     * @param confirmTextResId
     * @param cancelTextResId
     * @param items             列表项集合
     * @param mapItem           列表项集合,map形式
     * @param selectedPosition
     * @param itemClickListener
     *
     * @return
     */
    public Dialog buildListDialog(Activity activity, int titleResId, int contentResId, int confirmTextResId,
                                  int cancelTextResId, String[] items, Map<String, Integer> mapItem,
                                  Map<String, String> doubleTextItem,
                                  int selectedPosition, OnItemClickListener itemClickListener) {
        int type = 0;
        if (mapItem != null) {
            type = CustomListAdapter.TEXT_AND_VIEW_TAG;
        } else if (mapItem == null && doubleTextItem == null) {
            type = ((confirmTextResId == -1 && titleResId == -1) ? CustomListAdapter.ALBUM_OPERATION :
                    CustomListAdapter.CHECK_LIST_CONTENT);
        }

        final Dialog dialog = buildDialogBaseProperty(activity, titleResId, confirmTextResId);

        final Button bnCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        if (-1 == cancelTextResId) {
            bnCancel.setVisibility(View.GONE);
            if (bnCancel.getParent() != null && bnCancel.getParent().getParent() != null) {
                ((View) bnCancel.getParent().getParent()).setVisibility(View.GONE);
            }
        } else {
            bnCancel.setText(cancelTextResId);
            bnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onCancelBtnClick();
                    }
                    dialog.dismiss();
                }
            });
        }

        final TextView dialogContent = (TextView) dialog.findViewById(R.id.text_content);
        dialogContent.setVisibility(View.GONE);

        final ListView listview = (ListView) dialog.findViewById(R.id.list_content);
        listview.setVisibility(View.VISIBLE);
        final List<HashMap<String, Object>> lists = new ArrayList<HashMap<String, Object>>();
        if (type == CustomListAdapter.TEXT_AND_VIEW_TAG) {
            if (mapItem != null) {
                for (Map.Entry<String, Integer> entry : mapItem.entrySet()) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("icon", R.drawable.rice_ic_element_radiobtn_active);
                    map.put("text", entry.getKey());
                    map.put(CustomListAdapter.VIEW_TAG, entry.getValue());
                    lists.add(map);
                }
            }
        } else {
            if (doubleTextItem != null) {
                for (Map.Entry<String, String> entry : doubleTextItem.entrySet()) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("icon", R.drawable.rice_ic_element_radiobtn_active);
                    map.put("text", entry.getKey());
                    map.put(CustomListAdapter.VIEW_DOUBLE_TEXT, entry.getValue());
                    lists.add(map);
                }
            } else {
                for (String item : items) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("icon", R.drawable.rice_ic_element_radiobtn_active);
                    map.put("text", item);
                    lists.add(map);
                }
            }
        }

        final CustomListAdapter cla =
                new CustomListAdapter(activity, R.layout.icon_text_horizontal_item_layout, lists, type);
        cla.setSelectedPosition(selectedPosition < 0 ? 0 : selectedPosition);
        listview.setAdapter(cla);
        if (itemClickListener != null) {
            listview.setOnItemClickListener(itemClickListener);
        }
        changeCancelBtnStyle(activity);
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    /**
     * 构建内容为列表的对话框，对话框除了列表的各项显示内容，均以resId为入参 不带checkbox和图片，只有文字项
     *
     * @param activity
     * @param titleResId
     * @param confirmTextResId
     * @param cancelTextResId
     * @param items             列表项集合
     * @param selectedPosition  列表的选择位置
     * @param itemClickListener 列表项的点击事件监听器
     *
     * @return 构建完成的对话框
     */
    public Dialog buildOnlyTextListDialog(Activity activity, int titleResId, int confirmTextResId,
                                          int cancelTextResId, String[] items, int selectedPosition,
                                          OnItemClickListener itemClickListener) {
        final Dialog dialog = buildDialogBaseProperty(activity, titleResId, confirmTextResId);
        final Button bnCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        final Button buttonOK = (Button) dialog.findViewById(R.id.dialog_button_ok);

        if (-1 == cancelTextResId) {
            bnCancel.setVisibility(View.GONE);
        } else {
            bnCancel.setText(cancelTextResId);
            bnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onCancelBtnClick();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (-1 == confirmTextResId) {
            buttonOK.setVisibility(View.GONE);
        } else {
            buttonOK.setText(confirmTextResId);
            buttonOK.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onOkBtnClick();
                    }
                    dialog.dismiss();
                }
            });
        }

        final TextView dialogContent = (TextView) dialog.findViewById(R.id.text_content);
        dialogContent.setVisibility(View.GONE);

        final ListView listview = (ListView) dialog.findViewById(R.id.list_content);
        listview.setVisibility(View.VISIBLE);
        final List<HashMap<String, Object>> lists = new ArrayList<HashMap<String, Object>>();
        for (String item : items) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("icon", R.drawable.rice_ic_element_radiobtn_active);
            map.put("text", item);
            lists.add(map);
        }

        final CustomListAdapter cla =
                new CustomListAdapter(activity, R.layout.icon_text_horizontal_item_layout, lists,
                        CustomListAdapter.ONLY_TEXT);
        cla.setSelectedPosition(selectedPosition < 0 ? 0 : selectedPosition);
        listview.setAdapter(cla);
        if (itemClickListener != null) {
            listview.setOnItemClickListener(itemClickListener);
        }
        changeCancelBtnStyle(activity);
        if (!activity.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    /**
     * 构建每一项item带checkbox的list dialog
     * @param activity
     * @param titleResId
     * @param confirmTextResId
     * @param cancelTextResId
     * @param items
     * @param doubleTextItem
     * @param selectedPosition
     * @param itemClickListener
     *
     * @return
     */
    public Dialog buildCheckListDialog(Activity activity, int titleResId, int confirmTextResId, int cancelTextResId,
                                       String[] items, Map<String, String> doubleTextItem, int selectedPosition,
                                       final OnItemClickListener itemClickListener) {
        final Dialog dialog = buildDialogBaseProperty(activity, titleResId, confirmTextResId);
        final Button bnCancel = (Button) dialog.findViewById(R.id.dialog_button_cancel);

        if (-1 == cancelTextResId) {
            bnCancel.setVisibility(View.GONE);
        } else {
            bnCancel.setText(cancelTextResId);
            bnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onCancelBtnClick();
                    }
                    dialog.dismiss();
                }
            });
        }

        final TextView dialogContent = (TextView) dialog.findViewById(R.id.text_content);
        dialogContent.setVisibility(View.GONE);

        final ListView listview = (ListView) dialog.findViewById(R.id.list_content);
        listview.setVisibility(View.VISIBLE);
        final List<HashMap<String, Object>> lists = new ArrayList<HashMap<String, Object>>();
        if (doubleTextItem != null) {
            for (Map.Entry<String, String> entry : doubleTextItem.entrySet()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("icon", R.drawable.rice_ic_element_radiobtn_active);
                map.put("text", entry.getKey());
                map.put(CustomListAdapter.VIEW_DOUBLE_TEXT, entry.getValue());
                lists.add(map);
            }
        } else {
            for (String item : items) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("icon", R.drawable.rice_ic_element_radiobtn_active);
                map.put("text", item);
                lists.add(map);
            }
        }

        final CustomListAdapter cla =
                new CustomListAdapter(activity, R.layout.icon_text_horizontal_item_layout, lists,
                        CustomListAdapter.CHECK_LIST_CONTENT);
        cla.setSelectedPosition(selectedPosition < 0 ? 0 : selectedPosition);
        listview.setAdapter(cla);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cla.setSelectedPosition(position);
                cla.notifyDataSetChanged();
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(parent, view, position, id);
                }
            }
        });
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
                        R.layout.dialog_image_guide_layout, null);
        dialog.setContentView(view);
        final ImageView content = dialog.findViewById(R.id.dialog_content_image);
        content.setImageResource(imageResId);
        final Button buttonSingle = dialog.findViewById(R.id.dialog_button_one);
        final TextView contentView = dialog.findViewById(R.id.content_info);
        final TextView subContent = dialog.findViewById(R.id.sub_content);
        final View imageViewCancel = dialog.findViewById(R.id.dialog_cancel);

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
        imageViewCancel.setVisibility(View.VISIBLE);
        // 右上角Cancel
        imageViewCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onCancelBtnClick();
                }
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public Dialog buildAuthorizationDialog(final Activity activity, String confirmText,
                                            String contentText, @LayoutRes int subContextId) {

        if (activity == null) {
            return null;
        }

        final Dialog dialog = new Dialog(activity, R.style.ModerDialogTheme);

        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_authorization_alert_layout, null);
        dialog.setContentView(view);

        LinearLayout subContextLayout = (LinearLayout) dialog.findViewById(R.id.sub_context);
        Button confirmButton = (Button) dialog.findViewById(R.id.dialog_button_one);
        TextView textView = (TextView) dialog.findViewById(R.id.title_text);
        LinearLayout cannel = (LinearLayout) dialog.findViewById(R.id.cannel);

        LayoutInflater.from(activity).inflate(subContextId, subContextLayout);

        textView.setText(contentText);

        confirmButton.setText(confirmText);

        cannel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onCancelBtnClick();
                }
                if (!activity.isFinishing() && dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onOkBtnClick();
                }
                if (!activity.isFinishing() && dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setCanceledOnTouchOutside(false);


        return dialog;
    }

    /**
     * 构建图片样式的提示框
     * @param activity 当前的activity。
     * @param confirmText 确认按钮的文案
     * @param cancelText  取消按钮的文案
     * @param contentText 提示框主要内容
     * @param subContentText 提示框次要内容
     * @param imageResId 提示框的头部图片
     * @param confirmBgRes 确认按钮的背景资源
     * @param showIvCancel 右上角是否有取消按钮
     */
    public Dialog buildBaseImageAlertDialog(final Activity activity, String confirmText, String cancelText,
                                            String contentText, String subContentText, @DrawableRes int imageResId,
                                            int confirmBgRes, boolean showIvCancel) {
        return buildBaseImageAlertDialog(activity, confirmText, cancelText,
                contentText, subContentText, false, null, imageResId,
                confirmBgRes, showIvCancel);
    }

    /**
     * 构建图片样式的提示框
     * @param activity 当前的activity。
     * @param confirmText 确认按钮的文案
     * @param cancelText  取消按钮的文案
     * @param contentText 提示框主要内容
     * @param subContentText 提示框次要内容
     * @param isSubContentBullet 次要内容是否要进行分段
     * @param bottomText 底部描述内容
     * @param imageResId 提示框的头部图片
     * @param confirmBgRes 确认按钮的背景资源
     * @param showIvCancel 右上角是否有取消按钮
     */
    public Dialog buildBaseImageAlertDialog(final Activity activity, String confirmText, String cancelText,
                                            String contentText, String subContentText, boolean isSubContentBullet,
                                            String bottomText, @DrawableRes int imageResId, int confirmBgRes,
                                            boolean showIvCancel) {
        if (null == activity) {
            return null;
        }
        final Dialog dialog = new Dialog(activity, R.style.ModerDialogTheme);
        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_image_alert_layout, null);
        dialog.setContentView(view);
        final ImageView content = (ImageView) dialog.findViewById(R.id.dialog_content_image);
        content.setImageResource(imageResId);
        final TextView buttonCancel = (TextView) dialog.findViewById(R.id.dialog_button_cancel);
        final Button buttonConfirm = (Button) dialog.findViewById(R.id.dialog_button_confirm);
        final Button buttonSingle = dialog.findViewById(R.id.dialog_button_one);
        final TextView contentView = dialog.findViewById(R.id.content_info);
        final TextView subContent = dialog.findViewById(R.id.sub_content);
        final TextView bottomView = dialog.findViewById(R.id.bottom_info);
        final ImageView imageViewCancel = (ImageView) dialog.findViewById(R.id.dialog_cancel);

        final View footView = dialog.findViewById(R.id.dialog_footer);
        final View singleFootView = dialog.findViewById(R.id.dialog_footer_one_button);

        if (!TextUtils.isEmpty(contentText)) {
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(contentText);
        } else {
            contentView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(subContentText)) {
            subContent.setVisibility(View.VISIBLE);
            if (isSubContentBullet) {
                showContentWithBullet(subContent, subContentText);
            } else {
                subContent.setText(subContentText);
            }
            contentView.getPaint().setFakeBoldText(true);
        } else {
            subContent.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bottomText)) {
            bottomView.setVisibility(View.VISIBLE);
            bottomView.setText(bottomText);
        } else {
            bottomView.setVisibility(View.GONE);
        }

        boolean noCancel = false;

        // 左边取消button
        if (!TextUtils.isEmpty(cancelText)) {
            buttonCancel.setVisibility(View.VISIBLE);
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
            noCancel = true;
        }

        if (!TextUtils.isEmpty(confirmText)) {
            if (noCancel) {
                singleFootView.setVisibility(View.VISIBLE);
                footView.setVisibility(View.GONE);
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
            } else {
                // 有取消按钮，显示两个按钮
                footView.setVisibility(View.VISIBLE);
                singleFootView.setVisibility(View.GONE);
                buttonConfirm.setVisibility(View.VISIBLE);
                if (confirmBgRes > DIALOG_BUTTON_DISABLE_COLOR) {
                    buttonConfirm.setBackgroundResource(confirmBgRes);
                }
                buttonConfirm.setText(confirmText);
                buttonConfirm.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialogListener != null) {
                            dialogListener.onOkBtnClick();
                        }
                        dialog.dismiss();
                    }
                });
            }
        } else {
            buttonConfirm.setVisibility(View.GONE);
        }

        // 右上角取消按钮
        if (showIvCancel) {
            imageViewCancel.setVisibility(View.VISIBLE);
            // 右上角Cancel
            imageViewCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onCancelBtnClick();
                    }
                    dialog.dismiss();
                }
            });
        } else {
            imageViewCancel.setVisibility(View.GONE);
        }

        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void showContentWithBullet(TextView view, String content) {
        SpannableString spannableString = new SpannableString(content);
        int i = 0;
        List<Integer> indexList = new ArrayList<>();
        while (true) {
            int index = content.indexOf("\n", i);
            if (index != -1 && index < content.length()) {
                indexList.add(index);
                i = index + 1;
            } else {
                break;
            }
        }

        if (indexList.isEmpty() || indexList.get(indexList.size() - 1) != content.length() - 1) {
            indexList.add(content.length() - 1);
        }

        for (int n = 0; n < indexList.size(); n++) {
            int start = 0;
            if (n != 0) {
                start = indexList.get(n - 1) + 1;
            }
            spannableString.setSpan(new BulletSpan(BulletSpan.STANDARD_GAP_WIDTH,
                    ContextCompat.getColor(BaseApplication.getInstance(),
                            R.color.light_black)), start, indexList.get(n), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        view.setText(spannableString);
    }




    /**
     * 构建一个不带文字的loading框
     *
     * @param activity
     *
     * @return
     */
    public Dialog buildLoadingDialogWithoutText(Activity activity) {
        if (null == activity) {
            return null;
        }
        Dialog dialog = new Dialog(activity, R.style.ModerDialogTheme);
        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_loading_without_text, null);
        dialog.setContentView(view);
        final Animation animationRotate =
                AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.clockwise_rotate_animation);
        animationRotate.setInterpolator(new LinearInterpolator());
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.loading_image).startAnimation(animationRotate);
        return dialog;
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


    public Dialog buildBottomImageDialog(final Activity activity, String confirmText, String cancelText,
                                         String contentText, String subContentText, boolean isSubContentBullet,
                                         @DrawableRes int imageResId, int confirmBgRes, boolean showIvCancel) {
        if (null == activity) {
            return null;
        }
        final Dialog dialog = new Dialog(activity, R.style.ModerDialogTheme);
        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_image_bottom_layout, null);
        dialog.setContentView(view);
        final ImageView content = (ImageView) dialog.findViewById(R.id.dialog_content_image);
        content.setImageResource(imageResId);
        final TextView buttonCancel = (TextView) dialog.findViewById(R.id.dialog_button_cancel);
        final TextView buttonConfirm = (TextView) dialog.findViewById(R.id.dialog_button_confirm);
        final TextView contentView = dialog.findViewById(R.id.content_info);
        final TextView subContent = dialog.findViewById(R.id.sub_content);
        final ImageView imageViewCancel = (ImageView) dialog.findViewById(R.id.dialog_cancel);

        if (!TextUtils.isEmpty(contentText)) {
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(contentText);
        } else {
            contentView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(subContentText)) {
            subContent.setVisibility(View.VISIBLE);
            if (isSubContentBullet) {
                showContentWithBullet(subContent, subContentText);
            } else {
                subContent.setText(subContentText);
            }
            contentView.getPaint().setFakeBoldText(true);
        } else {
            subContent.setVisibility(View.GONE);
        }

        // 左边取消button
        if (!TextUtils.isEmpty(cancelText)) {
            buttonCancel.setVisibility(View.VISIBLE);
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

        if (!TextUtils.isEmpty(confirmText)) {
            buttonConfirm.setVisibility(View.VISIBLE);
            if (confirmBgRes > DIALOG_BUTTON_DISABLE_COLOR) {
                buttonConfirm.setBackgroundResource(confirmBgRes);
            }
            buttonConfirm.setText(confirmText);
            buttonConfirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogListener != null) {
                        dialogListener.onOkBtnClick();
                    }
                    dialog.dismiss();
                }
            });
        } else {
            buttonConfirm.setVisibility(View.GONE);
        }

        // 右上角取消按钮
        if (showIvCancel) {
            imageViewCancel.setVisibility(View.VISIBLE);
            // 右上角Cancel
            imageViewCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } else {
            imageViewCancel.setVisibility(View.GONE);
        }

        dialog.setCanceledOnTouchOutside(false);
        // 设置alterdialog全屏
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().width = DeviceDisplayUtils.getScreenWidth();
        return dialog;

    }
}