/*
 * PopupMenu.java
 * classes : com.dubox.drive.ui.widget.PopupMenu
 * @author 文超
 * V 1.0.0
 * Create at 2014-2-24 下午5:10:44
 */
package com.moder.compass.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.ui.widget.dialog.IconPressedListener;
import com.moder.compass.ui.widget.dialog.IconPressedListener;

/**
 * 通用的弹出式菜单。根据锚点，即点击的View的位置来决定弹出位置，方向。可以调用{@link #setShowArrow(boolean)} 来设置是否显示具有方向性的箭头，默认不显示；可以调用
 * {@link #setShowDivider(boolean)}来设置是否显示分隔线，默认不显示。
 * <p>
 * Sample:
 *
 * <pre>
 * PopupMenu popupMenu = new PopupMenu(mContext);
 * popupMenu.setShowArrow(true);// 显示箭头
 * popupMenu.setShowDivider(true);// 显示分隔线
 * popupMenu.setOffset(10, 10);// 调整位置
 * popupMenu.setItemLayoutGravity(Gravity.LEFT);// 设置菜单项的对齐方式，默认居中
 * popupMenu.setItemClickStyle(false);// 切换菜单项点击效果
 * popupMenu.addItem(popupMenu.new PopupMenuItem(0, &quot;传输&quot;));// 添加菜单项
 * popupMenu.addItem(popupMenu.new PopupMenuItem(1, &quot;多选&quot;));
 * popupMenu.addItem(popupMenu.new PopupMenuItem(2, &quot;打开&quot;));
 * popupMenu.show(view);// 显示菜单
 *
 * // 监听菜单项点击事件
 * popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
 *
 *     // 处理菜单项点击
 *     &#064;Override
 *     public void onItemClick(int id, int position) {
 *         switch (id) {
 *             case 0:// 点击传输项
 *
 *                 break;
 *             case 1:// 点击多选项
 *
 *                 break;
 *             case 2:// 点击打开项
 *
 *                 break;
 *             default:
 *                 break;
 *         }
 *     }
 * });
 * </pre>
 * <p>
 * com.dubox.drive.ui.widget.PopupMenu
 *
 * @author 文超 <br/>
 * create at 2014-2-24 下午5:10:44
 */
public class PopupMenu {

    private static final String TAG = "PopupMenu";

    /**
     * menu与屏幕上边缘或者下边缘的最小边距
     **/
    private static final int MENU_PADDING_TOP_OR_BOTTOM_DP = 50;

    private static final int ITEM_MARGIN = 6;
    private static final int ITEM_MARGIN_MDPI = 4;
    private static final int DIVIDER_MARGIN = 0;
    protected final Context mContext;
    protected final LayoutInflater mInflater;
    private final WindowManager mWindowManager;
    protected PopupWindow mPopupWindow;

    protected ViewGroup mRootView;
    protected ViewGroup mMenu;
    protected ImageView mArrowUp;
    protected ImageView mArrowDown;

    private boolean mShowArrow;
    private boolean mShowDivider;

    protected int mSubViewPosition;

    private int mItemMargin;

    private ScrollView mScroller;

    private int mDividerMargin;
    @ColorInt
    private int mDivColor;

    private int mOffsetX;
    private int mOffsetY;
    protected int mGravity = Gravity.CENTER;
    protected int mItemWidth;
    protected int mItemHeight;
    protected int mItemTextStyle;
    private int mItemBackground;
    private int mItemPaddingLeft;
    private int mItemPaddingRight;

    // -1表示使用style中的颜色
    protected int mItemTextColor = -1;
    private boolean isRevertIcon = false;

    protected OnMenuItemClickListener mOnMenuItemClickListener;

    public PopupMenu(Context context) {
        mContext = context;
        mDivColor = ContextCompat.getColor(context, R.color.popmenu_divider_color);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initMargins(context);
        setRootView();

        initPopupWindow();
        mItemTextStyle = R.style.Moder_TextAppearance_Small_LighterBlack_Bold;
        mItemBackground = R.drawable.popup_menu_item_background;
    }

    protected void initMargins(Context context) {
        int height = DeviceDisplayUtils.getScreenHeight();
        int width = DeviceDisplayUtils.getScreenWidth();
        if (height < 800 || width < 480) {
            mItemMargin = DeviceDisplayUtils.dip2px(context, ITEM_MARGIN_MDPI);
        } else {
            mItemMargin = DeviceDisplayUtils.dip2px(context, ITEM_MARGIN);
        }
        mDividerMargin = DeviceDisplayUtils.dip2px(context, DIVIDER_MARGIN);
    }

    protected void initPopupWindow() {
        mPopupWindow =
                new PopupWindow(mRootView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
    }

    protected void setRootView() {
        setRootView(R.layout.popup_menu);
    }

    protected void setRootView(int layoutId) {
        mRootView = (ViewGroup) mInflater.inflate(layoutId, null);
        mMenu = (ViewGroup) mRootView.findViewById(R.id.popup_menu_parent_view);
        mArrowUp = (ImageView) mRootView.findViewById(R.id.popup_menu_arrow_up);
        mArrowDown = (ImageView) mRootView.findViewById(R.id.popup_menu_arrow_down);
        mScroller = (ScrollView) mRootView.findViewById(R.id.scroller);
    }

    public void setBackground(@DrawableRes int background) {
        mScroller.setBackgroundResource(background);
    }

    public void setPadding(int padding) {
        mScroller.setPadding(padding, padding, padding, padding);
    }

    /**
     * 设置Scorller的margin
     * @param left 左
     * @param top 上
     * @param right 右
     * @param bottom 下
     */
    public void setMargin(int left, int top, int right, int bottom) {
        setMargins(mScroller, left, top, right, bottom);
    }


    private void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * 设置菜单项的对齐方式。该方法需要在{@link #addItem(PopupMenuItem)}之前调用
     *
     * @param gravity
     */
    public void setItemLayoutGravity(int gravity) {
        mGravity = gravity;
    }

    /**
     * 设置菜单项的宽度。如果不设置，则使用默认宽度。该方法需要在{@link #addItem(PopupMenuItem)}之前调用
     *
     * @param width
     */
    public void setItemWidth(int width) {
        mItemWidth = width;
    }

    public void setItemHeight(int height) {
        mItemHeight = height;
    }

    public void setItemPaddingLeft(int paddingLeft) {
        mItemPaddingLeft = paddingLeft;
    }

    public void setItemPaddingRight(int paddingRight) {
        mItemPaddingRight = paddingRight;
    }

    /**
     * 设置菜单项文本的style。该方法需要在{@link #addItem(PopupMenuItem)}之前调用
     *
     * @param resid
     */
    public void setItemTextStyle(int resid) {
        mItemTextStyle = resid;
    }

    /**
     * 设置菜单项背景。该方法需要在{@link #addItem(PopupMenuItem)}之前调用
     *
     * @param resid
     */
    public void setItemBackgroundResource(int resid) {
        mItemBackground = resid;
    }

    /**
     * 添加菜单项
     *
     * @param item
     */
    public void addItem(final PopupMenuItem item) {
        addItem(item, true);
    }

    public void addItem(final PopupMenuItem item, boolean enabled) {
        addItem(item, R.layout.popup_menu_item, enabled, false);
    }

    public void addItem(final PopupMenuItem item, boolean enabled, boolean selected) {
        addItem(item, R.layout.popup_menu_item, enabled, selected);
    }

    /**
     * 添加菜单项
     *
     * @param item layoutId
     */
    protected void addItem(final PopupMenuItem item, int layoutId, boolean enabled, boolean selected) {
        LinearLayout itemView = initItemView(layoutId);
        itemView.setEnabled(enabled);
        // 设置item显示内容
        ImageView icon = (ImageView) itemView.findViewById(R.id.popup_menu_item_icon);
        TextView content = (TextView) itemView.findViewById(R.id.popup_menu_item_content);
        ImageView afterIcon = (ImageView) itemView.findViewById(R.id.popup_menu_item_aftericon);
        View badgeNewFunc = itemView.findViewById(R.id.badge_new_func);

        content.setTextAppearance(mContext, mItemTextStyle);


        if (mItemTextColor != -1) {
            content.setTextColor(mContext.getResources().getColor(mItemTextColor));
        }


        itemView.setBackgroundResource(mItemBackground);
        IconPressedListener listener = new IconPressedListener(icon, content, afterIcon);
        listener.setRevert(isRevertIcon);

        if (item.icon != null) {
            icon.setImageDrawable(item.icon);
            icon.setEnabled(enabled);
            icon.setSelected(selected);
        } else {
            icon.setVisibility(View.GONE);
        }

        if (item.content != null) {
            content.setText(item.content);
            content.setEnabled(enabled);
            content.setSelected(selected);
        } else {
            content.setVisibility(View.GONE);
        }
        
        if (item.afterIcon != null) {
            afterIcon.setImageDrawable(item.afterIcon);
            afterIcon.setEnabled(enabled);
            afterIcon.setSelected(selected);
        } else {
            afterIcon.setVisibility(View.GONE);
        }
        
        itemView.setOnTouchListener(listener);

        // 设置item点击事件监听

        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onItemClick(item.id);
                }
                dismiss();
            }
        });

        // 将item加入menu view
        addDivider();
        mMenu.addView(itemView, mSubViewPosition);
        mSubViewPosition++;

        // 弹窗中，当前项的红点展示
        // 默认不展示，如需要展示，可再 Pop.addItem 时指定 Item 的 showBadgeNewFunc 属性
        if (item.needShowBadgeNewFunc && badgeNewFunc != null) {
            badgeNewFunc.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置字体颜色， 主要服务于暗黑模式
     * @param resId
     */
    public void setItemTextColor(int resId) {
        mItemTextColor = resId;
    }

    protected LinearLayout initItemView(int layoutId) {
        LinearLayout itemView = (LinearLayout) mInflater.inflate(layoutId, null);
        itemView.setGravity(mGravity);
        LinearLayout.LayoutParams itemViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        itemViewParams.leftMargin = mItemMargin;
        itemViewParams.rightMargin = mItemMargin;
        itemViewParams.topMargin = mItemMargin;
        itemViewParams.bottomMargin = mItemMargin;

        if (mItemWidth > 0) {
            itemView.setMinimumWidth(mItemWidth - mItemMargin * 2);
        }
        if (mItemHeight > 0) {
            itemView.setMinimumHeight(mItemHeight - mItemMargin * 2);
        }
        itemView.setLayoutParams(itemViewParams);
        itemView.setPadding(mItemPaddingLeft, 0, mItemPaddingRight, 0);

        return itemView;
    }

    protected void addDivider() {
        if (mShowDivider && mSubViewPosition != 0) {
            ImageView divider = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);

            params.leftMargin = mDividerMargin;
            params.rightMargin = mDividerMargin;
            divider.setLayoutParams(params);
            divider.setBackgroundColor(mDivColor);
            mMenu.addView(divider, mSubViewPosition);
            mSubViewPosition++;
        }
    }

    public void setDividerColor(@ColorInt int dividerColor) {
        mShowDivider = true;
        mDivColor = dividerColor;
    }

    /**
     * 显示菜单
     *
     * @param anchor 菜单位置所依赖的view，通常是用户点击的view
     */
    public void show(View anchor) {
        // 获取锚点位置
        int[] anchorLocation = new int[2];
        anchor.getLocationOnScreen(anchorLocation);
        Rect anchorRect = new Rect(anchorLocation[0], anchorLocation[1], anchorLocation[0] + anchor.getWidth(),
                anchorLocation[1] + anchor.getHeight());

        // 计算menu长宽
        mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int menuWidth = mRootView.getMeasuredWidth();
        int menuHeight = mRootView.getMeasuredHeight();

        // 获取屏幕长宽
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 计算menu 弹出位置和箭头偏移量
        int xPos;
        int yPos;
        int arrowMarginLeft;
        if ((anchorRect.left + menuWidth) > screenWidth) {
            // 显示不下menu，往左挪
            xPos = anchorRect.left - (menuWidth - anchor.getWidth()) + (menuWidth - anchor.getWidth()) / 2;
            xPos = (xPos < 0) ? 0 : xPos;
        } else {
            xPos = anchorRect.left + (anchor.getWidth() - menuWidth) / 2;
            if (xPos < 0) {
                xPos = 0;
            }

        }
        arrowMarginLeft = anchorRect.centerX() - xPos;

        // 如果锚点位置在屏幕中心靠上，那么popup显示在锚点上，反之在锚点下。
        int anchorMarginTop = anchorRect.top;
        int anchorMarginBottom = screenHeight - anchorRect.bottom;
        boolean onTop = anchorMarginTop > anchorMarginBottom;
        if (onTop) {
            // 如果menu高度大于锚点的top值，那么menu的y将会超出屏幕
            if (menuHeight > anchorMarginTop) {
                // 缩短menu的高度
                LayoutParams l = mScroller.getLayoutParams();
                l.height = anchorMarginTop - DeviceDisplayUtils.getPx(MENU_PADDING_TOP_OR_BOTTOM_DP);
                yPos = anchorMarginTop - l.height;
            } else {
                yPos = anchorMarginTop - menuHeight;
            }
        } else {
            if (menuHeight > anchorMarginBottom) {
                // 缩短menu的高度
                LayoutParams l = mScroller.getLayoutParams();
                l.height = anchorMarginBottom - DeviceDisplayUtils.getPx(MENU_PADDING_TOP_OR_BOTTOM_DP);
            }
            yPos = anchorRect.bottom;
        }

        if (mShowArrow) {
            showArrow(arrowMarginLeft, onTop);
        } else {
            mArrowUp.setVisibility(View.GONE);
            mArrowDown.setVisibility(View.GONE);
        }

        if (mPopupWindow != null) {
            mPopupWindow.setAnimationStyle(R.style.Moder_Animation_PopDownMenu);

            // 解决MTJ崩溃
            try {
                if (mContext instanceof Activity) {
                    if (!((Activity) mContext).isFinishing()) {
                        mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos + mOffsetX, yPos + mOffsetY);
                    }
                } else {
                    mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos + mOffsetX, yPos + mOffsetY);
                }
            } catch (Exception e) {
                DuboxLog.e(TAG, "", e);
            }
        }
    }

    private void showArrow(int arrowMarginLeft, boolean onTop) {
        View arrow = null;
        if (onTop) {
            mArrowUp.setVisibility(View.GONE);
            mArrowDown.setVisibility(View.VISIBLE);
            arrow = mArrowDown;
        } else {
            mArrowDown.setVisibility(View.GONE);
            mArrowUp.setVisibility(View.VISIBLE);
            arrow = mArrowUp;
        }

        int arrowWidth = arrow.getMeasuredWidth();
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();
        param.leftMargin = arrowMarginLeft - arrowWidth / 2;
    }

    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            // 解决MTJ崩溃
            try {
                if (mContext instanceof Activity) {
                    if (!((Activity) mContext).isFinishing()) {
                        mPopupWindow.dismiss();
                    }
                } else {
                    mPopupWindow.dismiss();
                }
            } catch (Exception e) {
                DuboxLog.e(TAG, "", e);
            }
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mPopupWindow.setOnDismissListener(onDismissListener);
    }

    /**
     * 设置是否展示菜单箭头，默认不展示
     *
     * @param showArrow
     */
    public void setShowArrow(boolean showArrow) {
        mShowArrow = showArrow;
    }

    /**
     * 设置是否展示菜单分割线，默认不展示
     *
     * @param showDivider
     */
    public void setShowDivider(boolean showDivider) {
        mShowDivider = showDivider;
    }

    public void setOffset(int offsetX, int offsetY) {
        mOffsetX = offsetX;
        mOffsetY = offsetY;
    }

    /**
     * 设置菜单项的点击事件监听
     *
     * @param onMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public interface OnMenuItemClickListener {
        void onItemClick(int id);
    }

    public void setRevertIcon(boolean revert) {
        isRevertIcon = revert;
    }

}