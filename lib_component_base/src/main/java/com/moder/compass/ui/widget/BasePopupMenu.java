package com.moder.compass.ui.widget;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

/**
 * 下拉菜单的base类，进行了popupwindow的初始化操作 com.dubox.drive.ui.widget.BasePopupMenu
 * 
 * @author tianzengming <br/>
 *         create at 2014-1-16 上午11:46:12
 */

public class BasePopupMenu implements View.OnTouchListener {
    private static final String TAG = "BasePopupMenu";
    protected PopupWindow mPopupWindow = null;

    protected ViewGroup mMenul;
    protected Context mContext;

    protected PopupWindowDismissListener mPopupWindowDismissListener;
    protected IPopupwindowItemClickListener mPopwindowItemClickListener;

    public void setPopupwindowItemClickListener(IPopupwindowItemClickListener popwindowItemClickListener) {
        mPopwindowItemClickListener = popwindowItemClickListener;
    }

    public void setPopupWindowDismissListener(PopupWindowDismissListener popupWindowDismissListener) {
        this.mPopupWindowDismissListener = popupWindowDismissListener;
    }

    public BasePopupMenu(Context context, int layoutId) {
        mContext = context;
        initPopupWindow(layoutId);
    }

    protected PopupWindow createPopupWindow() {
        return new PopupWindow(mMenul, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 设置popwindow遮盖标题栏
     */
    public void setClipping(boolean enabled) {
        mPopupWindow.setClippingEnabled(enabled);
    }

    protected void initPopupWindow(int layoutId) {
        if (mPopupWindow == null) {
            LayoutInflater mLayoutInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mMenul = (ViewGroup) mLayoutInflater.inflate(layoutId, null);
            mPopupWindow = createPopupWindow();
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
            mMenul.setFocusableInTouchMode(true);
            mPopupWindow.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (mPopupWindowDismissListener != null) {
                        mPopupWindowDismissListener.onPopupWindowDismissed();
                    }
                }
            });
            mMenul.setOnTouchListener(this);
            mMenul.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && (isShowing())) {
                        closePopupWindow(); // 这里写明模拟menu的PopupWindow退出就行
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (mPopupWindow.isShowing()) {
            closePopupWindow();
            return;
        }
        mPopupWindow.showAtLocation(parent, gravity, x, y);
    }

    /**
     * @param anchor the view on which to pin the popup window
     */
    public boolean showAsDropDown(View anchor, int xoff, int yoff) {
        if (mPopupWindow.isShowing()) {
            closePopupWindow();
            return false;
        }

        mPopupWindow.showAsDropDown(anchor, xoff, yoff);
        return true;
    }

    public void closePopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    public interface PopupWindowDismissListener {
        void onPopupWindowDismissed();
    }

    /**
     * 弹窗中的item click回调
     * 
     * com.dubox.drive.ui.IPopupwindowItemClickListener
     * 
     * @panwei <br/>
     *         create at 2013-6-2 下午1:50:24
     */
    public interface IPopupwindowItemClickListener {
        void onPopupwindowItemClicked(View view, long position, int typeId, int fromType);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        closePopupWindow(); // 这里写明模拟menu的PopupWindow退出就行
        return true;
    }

    public View getRootView() {
        return mMenul;
    }

    @SuppressWarnings("ResourceType")
    protected int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

}
