package com.moder.compass.component.base;

import java.util.HashMap;

import com.moder.compass.ActivityLifecycleManager;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.moder.compass.account.constant.AccountErrorCode;
import com.dubox.drive.legacy.ServerBanInfo;
import com.dubox.drive.base.network.NetworkException;
import com.moder.compass.base.utils.ActivityUtils;
import com.dubox.drive.kernel.android.util.RealTimeUtil;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.exception.RemoteExceptionInfo;
import com.dubox.drive.kernel.util.DateUtil;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.stats.DuboxStatsEngine;
import com.moder.compass.ui.manager.BaseDialogBuilder;
import com.moder.compass.ui.manager.DialogCtrListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;

import androidx.annotation.UiThread;
import com.dubox.drive.common.component.BaseComponentManager;
import com.dubox.drive.common.component.IAccountChangeHandler;
/**
 * Created by linchangxin on 2015/1/19.
 */
public class AccountErrorHandler {
    private static final String TAG = "AccountErrorHandler";

    public static final String FOREVER_EXPERIOD = "forever";

    // 如果expire Dialog在其它Activity已经显示， 则当前Activity无法给出正确的错误提示信息。
    // 所以这里使用Activity的类名做映射。
    private static HashMap<String, Dialog> dlgs = new HashMap<String, Dialog>();
    // 修改重复出现登录对话框的问题

    /**
     * 对于http请求的回调进行错误码的处理 parse里面也有错误处理完全重叠了
     *
     * @param fromActivity
     * @param errno
     * @return
     */
    @UiThread
    public boolean commonErrorHandling(final Activity fromActivity, int errno) {
        DuboxLog.d(TAG, "commonErrorHandling(), errno=" + errno + ", fromActivity=" + fromActivity);
        boolean ret = false;
        switch (errno) {
            case AccountErrorCode.RESULT_BDUSS_INVALID:
                boolean isNotMatch = Account.INSTANCE.isPtokenNotMatch();
                if (isNotMatch) {
                    return reloginFailure(fromActivity, true);
                }
                return reloginFailure(fromActivity, false);
            case AccountErrorCode.RESULT_NOT_TEST_USER:
                DuboxLog.d(TAG, "Common.result_not_test_user:" + AccountErrorCode.RESULT_NOT_TEST_USER);
                ret = true;
                break;
            default:
                Account.INSTANCE.setAccountExpireCount(0);
                break;
        }
        return ret;

    }

    /**
     * 统一处理Server封禁情况弹窗
     *
     * @param fromActivity
     * @param errInfo
     * @return
     */
    @UiThread
    public boolean commonServerBanErrorHandling(Activity fromActivity, RemoteExceptionInfo errInfo) {
        DuboxLog.d(TAG,
                " BAN DBG  commonServerBanErrorHandling fromActivity:" + fromActivity + "  errInfo: " + errInfo);
        if (!(errInfo instanceof ServerBanInfo)) {
            return false;
        }
        final ServerBanInfo serverBanInfo = (ServerBanInfo) errInfo;
        final int banCode = serverBanInfo.banCode;
        if (null == fromActivity) {
            fromActivity = ActivityLifecycleManager.getTopActivity();
        }
        if (ServerBanUtils.isServerBanErrorCode(banCode) && (fromActivity != null && !fromActivity.isFinishing())) {
            String titleText = null;
            String leftButton = null;
            String rightButton = null;
            boolean cancelable = true;
            boolean logoutWhenCancel = false;
            if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_END) {
                // 一级封禁
                titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title);
                leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_exit);
                rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_appeal);
                cancelable = false;
                logoutWhenCancel = true;
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_3_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_3_END) {
                // 三级封禁
                titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title);
                leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_close);
                rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_appeal);
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_4_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_4_END) {
                // 四级封禁
                titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title_verification);
                leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_close);
                rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_verification);
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_END) {
                // 预留0级封禁
                titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title);
                leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_exit);
                rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_appeal);
                cancelable = false;
                logoutWhenCancel = true;
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_END) {
                // 预留2级封禁
                titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title);
                leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_close);
                rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_appeal);
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_5_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_7_END) {
                // 预留567级封禁
                titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title);
                leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_close);
                rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_appeal);
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_8_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_9_END) {
                // 预留89级封禁
                titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title_verification);
                leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_close);
                rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_verification);
            }
            BaseDialogBuilder builder = new BaseDialogBuilder();
            final boolean finalLogoutWhenCancel = logoutWhenCancel;
            final Activity finalFromActivity = fromActivity;
            builder.setOnDialogCtrListener(new DialogCtrListener() {
                @Override
                public void onOkBtnClick() {
                    if (finalFromActivity != null && !finalFromActivity.isFinishing()) {
                        // ServerBanAppealActivity.startServerBanAppealActivity(finalFromActivity, banCode, -1);
                        if (banCode == AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_START) {
                            finalFromActivity.finish();
                            // android.os.Process.killProcess(android.os.Process.myPid());
                        } else {
                            IAccountChangeHandler accountChangeHandler =
                                    BaseComponentManager.getInstance().getAccountChangeHandler();
                            if (accountChangeHandler != null
                                    && new NetworkException(finalFromActivity.getApplicationContext())
                                            .checkNetworkException()) {
                                accountChangeHandler.startServerBanAppealActivity(finalFromActivity, banCode, -1);
                            }
                        }
                    }
                    ServerBanUtils.countStatisticsAppeal(banCode);
                }

                @Override
                public void onCancelBtnClick() {
                    if (finalLogoutWhenCancel && null != finalFromActivity) {
                        // AccountChangeHandler.logout(finalFromActivity, true,
                        // AccountChangeHandler.LOGOUT_SOURCE_NORMAL);
                        IAccountChangeHandler accountChangeHandler =
                                BaseComponentManager.getInstance().getAccountChangeHandler();
                        if (accountChangeHandler != null) {
                            accountChangeHandler.logout(finalFromActivity, true);
                        }
                    }
                }
            });

            final Dialog dialog;
            if (banCode == AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_START) {
                dialog = builder.buildGuideViewDialog(fromActivity, titleText, serverBanInfo.banMsg, leftButton,
                        R.drawable.dialog_server_ban);
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_END) {
                String period;
                if (FOREVER_EXPERIOD.equalsIgnoreCase(serverBanInfo.banMsg)) {
                    period = fromActivity.getString(R.string.account_server_ban_forever);
                } else {
                    try {
                        long time = Long.parseLong(serverBanInfo.banMsg.trim()) * 1000;
                        period = DateUtil.getValidPeriodTime(System.currentTimeMillis() + time);
                    } catch (Exception e) {
                        DuboxLog.e(TAG, e.getMessage(), e);
                        period = serverBanInfo.banMsg;
                    }
                }
                String content = fromActivity.getString(R.string.account_server_ban_notice, period);
                dialog = builder.buildTipsDialog(fromActivity, titleText, content, rightButton, leftButton, false);
            } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_START
                    && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_END) {
                String content = fromActivity.getString(R.string.account_server_ban_2_notice);
                dialog = builder.buildTipsDialog(fromActivity, titleText, content, rightButton, leftButton);
            } else {
                String content = fromActivity.getString(R.string.account_server_ban_other_notice);
                dialog = builder.buildTipsDialog(fromActivity, titleText, content, rightButton, leftButton);
            }
            dialog.setCanceledOnTouchOutside(cancelable);
            ServerBanUtils.countStatisticsShow(banCode);
            dialog.setCancelable(cancelable);
            return true;
        }
        return false;
    }

    /**
     * 同一提示疑似被盗号弹窗
     * 
     * @param fromActivity
     * @param errno
     * @return
     */
    @UiThread
    public boolean commonDoubtHackingErrorHandling(Activity fromActivity, final int errno, final int requestCode) {
        DuboxLog.d(TAG, " BAN DBG  commonDoubtHackingErrorHandling fromActivity:" + fromActivity + "  errno: " + errno);
        if (null == fromActivity) {
            fromActivity = ActivityLifecycleManager.getTopActivity();
        }
        if (ServerBanUtils.isDoubtHackingErrorCode(errno) && (fromActivity != null && !fromActivity.isFinishing())) {

            String titleText = fromActivity.getResources().getString(R.string.server_ban_dialog_title);
            String mgsText = fromActivity.getResources().getString(R.string.doubt_hacking_info);
            String leftButton = fromActivity.getResources().getString(R.string.server_ban_dialog_close);
            String rightButton = fromActivity.getResources().getString(R.string.server_ban_dialog_verification);

            BaseDialogBuilder builder = new BaseDialogBuilder();
            final Activity finalFromActivity = fromActivity;
            builder.setOnDialogCtrListener(new DialogCtrListener() {
                @Override
                public void onOkBtnClick() {
                    if (finalFromActivity != null && !finalFromActivity.isFinishing()
                            && new NetworkException(finalFromActivity.getApplicationContext())
                                    .checkNetworkException()) {
                        // ServerBanAppealActivity.startServerBanAppealActivity(finalFromActivity, errno, requestCode);
                        IAccountChangeHandler accountChangeHandler =
                                BaseComponentManager.getInstance().getAccountChangeHandler();
                        if (accountChangeHandler != null) {
                            accountChangeHandler.startServerBanAppealActivity(finalFromActivity, errno, requestCode);
                        }
                        StatisticsLogForMutilFields.getInstance().updateCount(
                                StatisticsLogForMutilFields.StatisticsKeys.DOUBT_HACKING_CLICK_APPEAL);
                    }
                }

                @Override
                public void onCancelBtnClick() {

                }
            });
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.DOUBT_HACKING_SHOW_DIALOG);
            return null != builder.buildTipsDialog(fromActivity, titleText, mgsText, rightButton, leftButton);
        }
        return false;
    }

    /**
     * 登录失败或者登录成功但出现失败状况
     */
    private boolean reloginFailure(final Activity fromActivity, final boolean isPtokenNotMatch) {
        if (!ActivityUtils.isDuboxForeground(BaseApplication.getInstance())) {
            IAccountChangeHandler accountChangeHandler = BaseComponentManager.getInstance().getAccountChangeHandler();
            if (accountChangeHandler != null) {
                accountChangeHandler.showInvalidUserNotify();
            }
            return true;
        }

        if (fromActivity == null) {
            return false;
        }

        ComponentName compName = fromActivity.getComponentName();
        final String className = compName.getClassName();
        Dialog dlg = dlgs.get(className);

        if (fromActivity.isDestroyed() || fromActivity.isFinishing() || (dlg != null && dlg.isShowing())) {
            return false;
        }

        if (Account.INSTANCE.getAccountExpireCount() == 0) {
            // 重置计数
            Account.INSTANCE.setGetStokenResult(false);

            BaseDialogBuilder builder = new BaseDialogBuilder();
            dlg = builder.buildOneButtonDialog(fromActivity, R.string.account_expire_title,
                    R.string.account_expire_text, R.string.confirm);
            builder.setOnDialogCtrListener(new DialogCtrListener() {

                @Override
                public void onOkBtnClick() {
                    DuboxLog.d(TAG, "oncancel handlebdussinvalid");
                    StatisticsLogForMutilFields.getInstance()
                            .updateCount(StatisticsLogForMutilFields.StatisticsKeys.CLICK_INVALID_USER_DIALOG_OK);
                    StatisticsLogForMutilFields.getInstance().updateCount(
                            StatisticsLogForMutilFields.StatisticsKeys.CLICK_INVALID_USER_DIALOG_OK_WITH_TIME,
                            String.valueOf(RealTimeUtil.getTime()));

                    if (isPtokenNotMatch) {
                        StatisticsLogForMutilFields.getInstance().updateCount(StatisticsLogForMutilFields.
                                StatisticsKeys.CLICK_NOT_MATCH_STOKEN_INVALID_USER_DIALOG_OK);
                        StatisticsLogForMutilFields.getInstance().updateCount(StatisticsLogForMutilFields.
                                        StatisticsKeys.CLICK_NOT_MATCH_STOKEN_INVALID_USER_DIALOG_OK_WITH_TIME,
                                String.valueOf(RealTimeUtil.getTime()));
                    }
                    handleBdussInvalid(fromActivity, className);
                }

                @Override
                public void onCancelBtnClick() {
                    // one button, none cancel
                    // AccountUtils.account_expire_count = 0;
                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.setCancelable(false);
            // }
            dlgs.put(className, dlg);

            if (isPtokenNotMatch) {
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.SHOW_NOT_MATCH_STOKEN_INVALID_TIMES);

                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.SHOW_NOT_MATCH_STOKEN_INVALID_TIMES_WITH_TIME,
                        String.valueOf(RealTimeUtil.getTime()));
            }

            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SHOW_INVALID_USER_TIMES);

            StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.SHOW_INVALID_USER_TIMES_WITH_TIME,
                    String.valueOf(RealTimeUtil.getTime()));

            // NotificationUtil.clearInvalidUserNotify(BaseApplication.getInstance());
            IAccountChangeHandler accountChangeHandler = BaseComponentManager.getInstance().getAccountChangeHandler();
            if (accountChangeHandler != null) {
                accountChangeHandler.clearInvalidUserNotify();
            }
            return true;
        }

        return false;
    }

    /**
     * 账号过期时，显示的对话框按下确认或back时处理
     *
     * @param activity
     * @param className
     */
    private void handleBdussInvalid(Activity activity, final String className) {
        Account.INSTANCE.setAccountExpireCount(Account.INSTANCE.getAccountExpireCount() + 1);
        DuboxStatsEngine.getInstance().uploadAll();
        IAccountChangeHandler accountChangeHandler = BaseComponentManager.getInstance().getAccountChangeHandler();
        if (accountChangeHandler != null) {
            accountChangeHandler.logout(activity, true);
        }
        dlgs.remove(className);
    }
}
