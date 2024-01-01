package com.moder.compass.ui.floatview;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.moder.compass.BaseApplication;
import com.moder.compass.component.base.R;
import com.moder.compass.util.CalculationUtil;

/**
 * 任务完成提示条(copy自百度网盘)
 * @author guanshuaichao
 * @since 2019/5/6
 */
public class TaskCompleteTip {

    private static final String TAG = "TaskCompleteTip";

    /**
     * toast离底部的距离
     */
    private static final int OFFSET_Y = 150;
    /**
     * toast 与 toast的间隔
     */
    private static final int SPACE_Y = 5;

    @SuppressLint("StaticFieldLeak")
    private static volatile TaskCompleteTip sInstance;

    private final Context mContext;
    private final List<ToastView> mViews;
    private final Handler mHandler;

    private TaskCompleteTip() {
        mContext = BaseApplication.getInstance();
        mHandler = new Handler(Looper.getMainLooper());
        mViews = new LinkedList<>();
    }

    public static TaskCompleteTip getInstance() {
        if (sInstance == null) {
            synchronized (TaskCompleteTip.class) {
                if (sInstance == null) {
                    sInstance = new TaskCompleteTip();
                }
            }
        }
        return sInstance;
    }

    public void show(String tip, final String targetUrl, final View.OnClickListener listener) {
        if (TextUtils.isEmpty(tip)) {
            return;
        }

        // 取消之前隐藏toast的消息
        mHandler.removeCallbacksAndMessages(null);

        // 最多保持10个
        if (mViews.size() >= 10) {
            cancel();
        }

        // 获取之前view的高度
        int height = 0;
        int spaceY = CalculationUtil.convertDpToPx(SPACE_Y, mContext);
        for (View v : mViews) {
            height += v.getMeasuredHeight() + spaceY;
        }
        // 加上view之间间距
        height += CalculationUtil.convertDpToPx(OFFSET_Y, mContext);

        // 创建布局参数
        FrameLayout.LayoutParams layoutParams = createLayoutParams(height);

        // 创建toast
        ToastView view = new ToastView(mContext);
        view.setDrag(false);
        View arrow = view.mArrow;
        TextView textView = view.mText;
        mViews.add(view);

        if (!TextUtils.isEmpty(targetUrl)) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 移除点击事件 避免重复点击
                    v.setOnClickListener(null);
                    listener.onClick(v);
                    cancel();
                }
            });
            arrow.setVisibility(View.VISIBLE);
        } else {
            arrow.setVisibility(View.GONE);
        }
        textView.setText(tip);

        // 添加toast
        FloatViewHelper.getInstance().add(view, layoutParams, null);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        }, 5000);
    }

    private FrameLayout.LayoutParams createLayoutParams(int height) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = height;
        return params;
    }

    public void cancel() {
        for (ToastView v : mViews) {
            cancelView(v);
        }
        mViews.clear();
    }

    private void cancelView(ToastView v) {
        FloatViewHelper.getInstance().remove(v);
    }

    private static class ToastView extends FloatView {

        private TextView mText;
        private ImageView mArrow;

        public ToastView(@NonNull Context context) {
            super(context);
        }

        @Override
        protected View initView(Context context) {
            View v = LayoutInflater.from(context).inflate(R.layout.task_complete_tip, null);
            mText = v.findViewById(R.id.text);
            mArrow = v.findViewById(R.id.arrow);
            return v;
        }
    }
}
