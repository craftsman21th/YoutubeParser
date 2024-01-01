package com.moder.compass.util.receiver;

import java.lang.ref.WeakReference;

import com.dubox.drive.legacy.ServerBanInfo;
import com.moder.compass.component.base.AccountErrorHandler;
import com.moder.compass.ui.widget.NewVersionDialog;
import com.dubox.drive.kernel.util.ToastHelper;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

/**
 * Created by manyongqiang on 2017/10/20.
 * 请求错误视图控制器
 */

public class FailedViewManager {
    private static final Integer NETWORK_ERROR = Integer.MAX_VALUE;
    private static final Integer DEFAULT_ERROR = Integer.MAX_VALUE - 1;
    private SparseArray<FailedViewBean> maps = new SparseArray<>();

    private WeakReference<Activity> mActivityWeakRef;

    FailedViewManager(@NonNull Activity activity) {
        mActivityWeakRef = new WeakReference<Activity>(activity);
    }

    public void put(int errno, FailedViewBean bean) {
        maps.append(errno, bean);
    }

    public void setNetworkErrorView(@NonNull FailedViewBean bean) {
        maps.append(NETWORK_ERROR, bean);
    }

    public void setDefaultErrorView(@NonNull FailedViewBean bean) {
        maps.append(DEFAULT_ERROR, bean);
    }

    final void showNetWorkErrorView(String contentMsg) {
        FailedViewBean failedViewBean = maps.get(NETWORK_ERROR);
        if (failedViewBean == null) {
            showDefaultErrorView(contentMsg);
        } else {
            show(failedViewBean, contentMsg);
        }
    }

    final boolean showAccountBanView(ServerBanInfo errorInfo, String contentMsg) {
        Activity activity = getActivity();
        if (activity != null) {
            if (new AccountErrorHandler().commonServerBanErrorHandling(activity, errorInfo)) {
                return true;
            } else if (!TextUtils.isEmpty(contentMsg)) {
                ToastHelper.showToast(contentMsg);
            }
        }
        return false;
    }

    final boolean showAccountCommonView(int errno, String contentMsg) {
        Activity activity = getActivity();
        if (activity != null) {
            if (new AccountErrorHandler().commonErrorHandling(activity, errno)) {
                return true;
            } else if (!TextUtils.isEmpty(contentMsg)) {
                ToastHelper.showToast(contentMsg);
            }
        }
        return false;
    }

    final void showServerErrorView(int errno, @Nullable String contentMsg) {
        FailedViewBean failedViewBean = maps.get(errno);
        if (failedViewBean == null) {
            showDefaultErrorView(contentMsg);
            return;
        }
        show(failedViewBean, contentMsg);
    }

    private void showDefaultErrorView(@Nullable String contentMsg) {
        FailedViewBean failedViewBean = maps.get(DEFAULT_ERROR);
        if (failedViewBean == null) {
            return;
        }
        show(failedViewBean, contentMsg);
    }

    private void show(@NonNull FailedViewBean failedViewBean, @Nullable String contentMsg) {
        Activity activity = getActivity();
        NewVersionDialog.Builder builder = failedViewBean.builder;
        if (TextUtils.isEmpty(contentMsg)) {
            return;
        }
        if (builder != null && activity != null) {
            builder.setActivity(activity);
            builder.setContentText(contentMsg);
            builder.show();
        } else {
            ToastHelper.showToast(contentMsg);
        }
    }

    final Activity getActivity() {
        Activity activity = mActivityWeakRef.get();
        if (activity != null && !activity.isFinishing()) {
            return activity;
        }
        return null;
    }

    public static class FailedViewBean {
        private NewVersionDialog.Builder builder;

        public FailedViewBean() {
            this(null);
        }

        public FailedViewBean(NewVersionDialog.Builder builder) {
            this.builder = builder;
        }
    }

}