package com.moder.compass.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.ConStantKt;
import com.moder.compass.util.DayNightModeKt;

/**
 * Loading 对话框
 * 
 * @author 孙奇 <br/>
 *         create at 2012-12-25 下午10:02:31
 */
public class LoadingDialog extends Dialog {

    private static final String TAG = "LoadingDialog";

    Animation animationRotate;
    ImageView loadingView;
    TextView loadingText;
    DialogOnBackKeyDownListener backKeyListener;

    protected LoadingDialog(Context context, int theme) {
        super(context, theme);
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.loading_dialog, null);
        this.setCancelable(true);
        this.setContentView(view);
        loadingView = (ImageView) this.findViewById(R.id.loading_icon);
        loadingText = (TextView) this.findViewById(R.id.loading_text);
        animationRotate = AnimationUtils.loadAnimation(context, R.anim.clockwise_rotate_animation);
        LinearInterpolator lir = new LinearInterpolator();
        animationRotate.setInterpolator(lir);
    }

    public void setBackKeyListener(DialogOnBackKeyDownListener backKeyListener) {
        this.backKeyListener = backKeyListener;
    }

    private void startAnimation() {
        loadingView.startAnimation(animationRotate);
    }

    private void clearAnimation() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // 在android L调用该方法必崩，崩溃原因是android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views.
                // 但这里都是在主线程调用的，只能增加了保护措施
                try {

                    loadingView.clearAnimation();
                } catch (Exception e) {
                    DuboxLog.e(TAG, "", e);
                }
            }
        }, 500);
    }

    /**
     * 设置loading文字
     * 
     * @param message
     * @author 孙奇 V 1.0.0 Create at 2012-12-25 下午10:31:28
     */
    public void setMessage(String message) {
        if (loadingText == null || message == null) {
            return;
        }
        loadingText.setText(message);
    }

    /**
     * 显示对话框
     * 
     * @param context
     * @param string
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-25 下午10:33:45
     */
    public static Dialog show(Context context, String string) {
        final Dialog mDialog = build(context, string);
        // 让弹窗不会在点击旁边的区域导致弹窗消失
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            mDialog.show();
        }

        // ??

        // if (mDialog.getWindow() != null && mDialog.getWindow().isActive()) {
        // mDialog.show();
        // }
        return mDialog;
    }

    /**
     * 显示对话框
     *
     * @param context
     * @param string
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-25 下午10:33:45
     */
    public static Dialog show(Context context, String string, DialogOnBackKeyDownListener listener) {
        final Dialog mDialog = build(context, string, listener);
        // 让弹窗不会在点击旁边的区域导致弹窗消失
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        if (context != null && context instanceof Activity && !((Activity) context).isFinishing()) {
            mDialog.show();
        }

        // ??

        // if (mDialog.getWindow() != null && mDialog.getWindow().isActive()) {
        // mDialog.show();
        // }
        return mDialog;
    }

    /**
     * 显示对话框
     * 
     * @param context
     * @param resid
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-25 下午10:33:45
     */
    public static Dialog show(Context context, int resid) {
        return show(context, context.getResources().getString(resid));
    }

    @Override
    public void show() {
        super.show();
        float radius = DeviceDisplayUtils.dip2px(getContext(), ConStantKt.SPACE_8);
        DayNightModeKt.setDayOrNightModeForDialog(this, radius, radius, radius, radius);
        startAnimation();
    }

    /**
     * 显示对话框
     * 
     * @param context
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-25 下午10:34:06
     */
    public static Dialog show(Context context) {
        return show(context, null, null);
    }

    public static Dialog show(Context context, DialogOnBackKeyDownListener listener) {
        return show(context, null, listener);
    }

    public static Dialog build(Context context,  String string, DialogOnBackKeyDownListener listener) {
        final LoadingDialog mDialog = new LoadingDialog(context, R.style.ModerDialogTheme);
        mDialog.setBackKeyListener(listener);
        mDialog.setMessage(string);
        return mDialog;
    }

    public static Dialog build(Context context, String string) {
        final LoadingDialog mDialog = new LoadingDialog(context, R.style.ModerDialogTheme);
        mDialog.setMessage(string);
        return mDialog;
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            clearAnimation();
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            DuboxLog.d(TAG, "loading dialog时按下返回键");
            if (backKeyListener != null) {
                backKeyListener.onBackKeyDownListener();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface DialogOnBackKeyDownListener {

        void onBackKeyDownListener();
    }
}
