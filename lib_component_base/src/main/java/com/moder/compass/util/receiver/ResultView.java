package com.moder.compass.util.receiver;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dubox.drive.legacy.ServerBanInfo;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * Created by manyongqiang on 2017/10/20.
 */
public abstract class ResultView {
    private static final String TAG = "ResultView";
    private FailedViewManager failedViewManager;
    private int mErrNo;

    public ResultView(@NonNull Activity activity) {
        failedViewManager = new FailedViewManager(activity);
        failedViewManager.setDefaultErrorView(new FailedViewManager.FailedViewBean());
        initFailedManager(activity, failedViewManager);
    }

    protected void initFailedManager(@NonNull Activity activity,
                                              @NonNull FailedViewManager failedViewManager) {

    }
    protected abstract void closeAllView();

    protected String getFailedMessage(ErrorType type, int errno, @NonNull Bundle resultData,
                                               @NonNull Activity activity) {
        return null;
    }

    /**
     * 显示成功后的视图，关闭loading框，而关闭Activity视图等操作不在此处属于业务
     * @param resultData
     */
    protected void showSuccessView(@Nullable Bundle resultData) {

    }

    /**
     * 显示操作中的视图，关闭loading框，而关闭Activity视图等操作不在此处属于业务
     * @param resultData
     */
    protected void showOperatingView(@Nullable Bundle resultData) {

    }

    final void showNetWorkErrorView(String contentMsg) {
        failedViewManager.showNetWorkErrorView(contentMsg);
    }

    final boolean showAccountBanView(ServerBanInfo errorInfo, String contentMsg) {
        return failedViewManager.showAccountBanView(errorInfo, contentMsg);
    }

    final boolean showAccountCommonView(int errno, String contentMsg) {
        return failedViewManager.showAccountCommonView(errno, contentMsg);
    }

    final void showServerErrorView(int errno, @Nullable String contentMsg) {
        failedViewManager.showServerErrorView(errno, contentMsg);
    }

    /**
     * Activity已经被销毁则不需要进行视图控制, 成功失败都是通过这个控制的
     * @return
     */
    final boolean isNeedControlView() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            return true;
        } else {
            DuboxLog.d(TAG, "activity is null");
            return false;
        }
    }

    /**
     * 设置错误码
     * @param errNo
     */
    void setErrNo(int errNo) {
        this.mErrNo = errNo;
    }

    /**
     * 返回错误码
     * @return
     */
    protected int getErrNo() {
        return mErrNo;
    }

    /**
     * 获取当前视图控制对应的Activity
     * @return
     */
    protected final Activity getActivity() {
        return failedViewManager.getActivity();
    }

    public static final class SimpleView extends ResultView {

        public SimpleView(@NonNull Activity activity) {
            super(activity);
        }

        @Override
        protected void closeAllView() {
            DuboxLog.d(TAG, "no dialog or toast need to close");
        }

        @Override
        protected String getFailedMessage(ErrorType type, int errno, @NonNull Bundle resultData,
                                          @NonNull Activity activity) {
            DuboxLog.d(TAG, "no word return");
            return null;
        }
    }
}