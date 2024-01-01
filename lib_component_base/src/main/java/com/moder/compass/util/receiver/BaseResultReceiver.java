package com.moder.compass.util.receiver;

import java.lang.ref.WeakReference;

import com.moder.compass.account.constant.AccountErrorCode;
import com.dubox.drive.legacy.ServerBanInfo;
import com.dubox.drive.base.service.BaseServiceHelper;
import com.dubox.drive.base.service.constant.BaseExtras;
import com.moder.compass.component.base.ServerBanUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.exception.RemoteExceptionInfo;
import com.dubox.drive.util.WeakRefResultReceiver;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by manyongqiang on 2017/9/20.
 * 分成视图控制和业务回调的逻辑
 */

public abstract class BaseResultReceiver<T> extends WeakRefResultReceiver<T> {
    private static final String TAG = "BaseResultReceiver";
    private WeakReference<ResultView> mResultView;

    public BaseResultReceiver(@NonNull T reference, @NonNull Handler handler, ResultView resultView) {
        super(reference, handler);
        if (resultView != null) {
            mResultView = new WeakReference<ResultView>(resultView);
        }
    }

    protected ResultView getResultView() {
        DuboxLog.d(TAG, "mResultView" + mResultView);
        if (mResultView == null) {
            return null;
        }
        ResultView resultView = mResultView.get();
        if (resultView != null && resultView.isNeedControlView()) {
            return resultView;
        } else {
            return null;
        }
    }

    private void closeShowingView(int errno) {
        ResultView resultView = getResultView();
        if (resultView != null) {
            resultView.setErrNo(errno);
            resultView.closeAllView();
        }
    }

    private String getDisplayMessage(ErrorType type, int errno, Bundle data) {
        String alertMessage = getAlertMsg(data);
        if (!TextUtils.isEmpty(alertMessage)) {
            DuboxLog.d(TAG, "alertMsg:" + alertMessage);
            return alertMessage;
        }
        ResultView resultView = getResultView();
        Activity activity = resultView == null ? null : resultView.getActivity();
        if (resultView != null && activity != null) {
            return resultView.getFailedMessage(type, errno, data, activity);
        }
        return null;
    }

    /**
     * 获取下发文案信息
     * @param data
     * @return
     */
    private String getAlertMsg(Bundle data) {
        if (data == null) {
            return null;
        }
        return data.getString(BaseExtras.SERVER_ALERT_MESSAGE);
    }

    @Override
    protected boolean onInterceptResult(@NonNull T reference, int resultCode, @Nullable Bundle resultData) {
        return super.onInterceptResult(reference, resultCode, resultData);
    }

    @Override
    protected final void onHandlerSuccessResult(@NonNull T reference, @Nullable Bundle resultData) {
        super.onHandlerSuccessResult(reference, resultData);
        onSuccess(reference, resultData);
        ResultView resultView = getResultView();
        if (resultView != null) {
            resultView.showSuccessView(resultData);
        } else {
            DuboxLog.d(TAG, "resultView is null");
        }
    }

    @Override
    protected final void onHandlerOperatingResult(@NonNull T reference, @Nullable Bundle resultData) {
        super.onHandlerOperatingResult(reference, resultData);
        onOperating(reference, resultData);
        ResultView resultView = getResultView();
        if (resultView != null) {
            resultView.showOperatingView(resultData);
        } else {
            DuboxLog.d(TAG, "resultView is null");
        }
    }

    @Override
    protected final void onHandlerOtherResult(@NonNull T reference, int resultCode, @Nullable Bundle resultData) {
        super.onHandlerOtherResult(reference, resultCode, resultData);
        onOther(reference, resultCode, resultData);
        DuboxLog.w(TAG, "can not handleImage resultCode:" + resultCode);
    }

    @Override
    protected final void onHandlerFailedResult(@NonNull T reference, @Nullable Bundle resultData) {
        super.onHandlerFailedResult(reference, resultData);
        if (resultData == null) {
            onHandlerFailedResult(reference, 0, Bundle.EMPTY);
            return;
        }
        if (BaseServiceHelper.isNetWorkError(resultData)) {
            onHandlerNetWorkError(reference, resultData);
            return;
        }
        int errno = resultData.getInt(BaseExtras.ERROR, 0);
        final RemoteExceptionInfo errorInfo = resultData.getParcelable(BaseExtras.ERROR_INFO);
        ServerBanInfo banInfo = null;
        if (null != errorInfo && (errorInfo instanceof ServerBanInfo)) {
            banInfo = (ServerBanInfo) errorInfo;
        }
        if (banInfo != null && isAccountBanError(banInfo)) {
            // 处理Server封禁情况
            onHandlerAccountBanError(reference, banInfo.banCode, banInfo, resultData);
            return;
        }
        if (isAccountCommonError(errno)) {
            // 处理Server账户过期情况
            onHandlerAccountCommonError(reference, errno, resultData);
            return;
        }
        onHandlerFailedResult(reference, errno, resultData);
    }

    private void onHandlerFailedResult(@NonNull T reference, int errno, @NonNull Bundle resultData) {
        closeShowingView(errno);
        if (!onFailed(reference, ErrorType.SERVER_ERROR, errno, resultData)) {
            ResultView resultView = getResultView();
            String message = getDisplayMessage(ErrorType.SERVER_ERROR, errno, resultData);
            if (resultView != null) {
                resultView.showServerErrorView(errno, message);
            } else {
                DuboxLog.d(TAG, "fail manager is null");
            }
        }
    }

    /**
     * 错误码是否是账户封禁错误
     */
    private boolean isAccountBanError(ServerBanInfo serverBanInfo) {
        final int banCode = serverBanInfo.banCode;
        return ServerBanUtils.isServerBanErrorCode(banCode);
    }

    /**
     * 错误码是否是通用错误
     */
    private boolean isAccountCommonError(int errno) {
        return (errno == AccountErrorCode.RESULT_BDUSS_INVALID)
                || (errno == AccountErrorCode.RESULT_NOT_TEST_USER);
    }

    /**
     * 处理网络错误，用户没有联网的情况
     */
    private void onHandlerNetWorkError(@NonNull T reference, @NonNull Bundle resultData) {
        closeShowingView(0);
        if (!onFailed(reference, ErrorType.NETWORK_ERROR, 0, resultData)) {
            ResultView resultView = getResultView();
            String message = getDisplayMessage(ErrorType.NETWORK_ERROR, 0, resultData);
            if (resultView != null) {
                resultView.showNetWorkErrorView(message);
            } else {
                DuboxLog.d(TAG, "fail manager is null");
            }
        }
    }

    /**
     * 处理用户封禁的情况，会弹出对话框，此时如果当前界面有dialog应该要关闭它
     */
    private void onHandlerAccountBanError(@NonNull T reference, int errno, ServerBanInfo errorInfo,
                                          @NonNull Bundle resultData) {
        closeShowingView(errorInfo.banCode);
        ResultView resultView = getResultView();
        String message = getDisplayMessage(ErrorType.ACCOUNT_BAN_ERROR, errorInfo.banCode, resultData);
        if (resultView != null && resultView.showAccountBanView(errorInfo, message)) {
            return;
        }
        onFailed(reference, ErrorType.ACCOUNT_BAN_ERROR, errorInfo.banCode, resultData);
    }

    /**
     * 提示类似账号已过期的情况
     */
    private void onHandlerAccountCommonError(@NonNull T reference, int errno, @NonNull Bundle resultData) {
        closeShowingView(errno);
        ResultView resultView = getResultView();
        String message = getDisplayMessage(ErrorType.ACCOUNT_COMMON_ERROR, errno, resultData);
        if (resultView != null && resultView.showAccountCommonView(errno, message)) {
            return;
        }
        onFailed(reference, ErrorType.ACCOUNT_COMMON_ERROR, errno, resultData);
    }

    protected void onSuccess(@NonNull T reference, @Nullable Bundle resultData) {
        DuboxLog.d(TAG, "success");
    }

    protected void onOperating(@NonNull T reference, @Nullable Bundle resultData) {
        DuboxLog.d(TAG, "Operating");
    }

    /**
     * 统一的请求错误回调方法
     * @param reference 弱引用的对象
     * @param errType 错误类型，参考{@link ErrorType}
     * @param errno 错误码 一般对应Server下发的错误码
     * @param resultData 最原始的Bundle类型数据
     * @return true 子类单独处理了这类的异常, 父类不再弹框或者toast； 针对个别业务可能使用，或者某个错误码不需要显示内容时使用
     */
    protected boolean onFailed(@NonNull T reference,  @NonNull ErrorType errType, int errno, @NonNull Bundle resultData) {
        DuboxLog.d(TAG, "errType: " + errType + " errno: " + errno);
        return false;
    }

    protected void onOther(@NonNull T reference, int resultCode, @Nullable Bundle resultData) {
        DuboxLog.d(TAG, "OnOther");
    }
}
