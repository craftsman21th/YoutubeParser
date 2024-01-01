package com.moder.compass.ui.widget;

import com.moder.compass.BaseActivity;
import com.moder.compass.IBackKeyListener;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.ui.view.IView;
import com.moder.compass.ui.view.IView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 基类Fragment实现IView接口
 *
 * @author 孙奇 <br/>
 * create at 2013-4-23 下午01:35:07
 */
public abstract class BaseFragment extends Fragment implements IView, IBackKeyListener {

    private static final String TAG = "BaseFragment";
    /**
     * 根layout
     *
     * @author 孙奇 V 1.0.0 Create at 2013-4-23 下午03:42:39
     */
    protected View mLayoutView;

    private boolean isDestroying = false;

    /**
     * 是否走完onViewCreated方法
     */
    protected boolean isViewCreated = false;
    /**
     * findViewById
     * <p>
     * 使用前要确保给 {@link BaseFragment#mLayoutView }赋值
     *
     * @param resId
     *
     * @return
     *
     * @author 孙奇 V 1.0.0 Create at 2013-4-23 下午03:41:49
     */
    protected View findViewById(int resId) {
        if (mLayoutView == null) {
            return null;
        }
        return mLayoutView.findViewById(resId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isDestroying = false;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        isDestroying = true;
        super.onDestroyView();
    }

    @Override
    public Context getContext() {
        if (getActivity() == null) {
            return null;
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            return getActivity();
        }
        return getActivity().getApplicationContext();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (DuboxLog.isDebug()) {
            DuboxLog.d(TAG, "Fragment Name=" + getName());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void showError(String errorMessage) {
    }

    @Override
    public void showError(int errorCode) {
    }

    @Override
    public void showError(int errorCode, String errorMessage) {
    }

    @Override
    public void showSuccess(int successCode) {
    }

    @Override
    public void showSuccess(String successMsg) {
    }

    @Override
    public void startProgress(int progressCode) {
    }

    @Override
    public void stopProgress(int progressCode) {
    }

    @Override
    public boolean isDestroying() {
        return (isRemoving() || isDestroying);
    }

    /**
     * @return
     */
    @Override
    public boolean onBackKeyPressed() {
        FragmentActivity activity = getActivity();
        return activity instanceof BaseActivity
            && ((BaseActivity) activity).backFragment();
    }

    /**
     * 获取基础服务
     *
     * @param name 服务名称 {@link BaseActivity#VIP_SERVICE}
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public @Nullable <T> T getService(String name) {
        if (getActivity() == null) {
            return null;
        }
        return (T) ((BaseActivity) getActivity()).getService1(name);
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

}
