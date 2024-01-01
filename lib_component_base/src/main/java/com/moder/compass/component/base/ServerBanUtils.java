package com.moder.compass.component.base;

import com.moder.compass.account.constant.AccountErrorCode;
import com.moder.compass.statistics.StatisticsLogForMutilFields;

/**
 * Created by cuizhe01 on 2016/6/22.
 */
public class ServerBanUtils {
    private static final String TAG = "ServerBanUtils";

    /**
     * 错误码是否是Server封禁错误码
     * 
     * @param errno
     * @return
     */
    public static boolean isServerBanErrorCode(int errno) {
        return errno >= AccountErrorCode.RESULT_SERVER_BAN_START && errno <= AccountErrorCode.RESULT_SERVER_BAN_END;
    }

    /**
     * 统计展现封禁弹窗
     *
     * @param banCode
     */
    public static void countStatisticsShow(int banCode) {
        if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_END) {
            // 一级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_1);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_3_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_3_END) {
            // 三级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_3);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_4_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_4_END) {
            // 四级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_4);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_END) {
            // 预留0级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_0);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_END) {
            // 预留2级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_2);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_5_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_5_END) {
            // 预留567级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_5);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_6_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_6_END) {
            // 预留567级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_6);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_7_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_7_END) {
            // 预留567级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_7);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_8_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_8_END) {
            // 预留89级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_8);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_9_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_9_END) {
            // 预留89级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_SHOW_DIALOG_LEVEL_9);
        }
    }

    /**
     * 统计点击申诉按钮
     * 
     * @param banCode
     */
    public static void countStatisticsAppeal(int banCode) {
        if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_1_END) {
            // 一级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_1);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_3_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_3_END) {
            // 三级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_3);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_4_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_4_END) {
            // 四级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_4);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_0_END) {
            // 预留0级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_0);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_2_END) {
            // 预留2级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_2);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_5_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_5_END) {
            // 预留567级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_5);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_6_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_6_END) {
            // 预留567级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_6);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_7_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_7_END) {
            // 预留567级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_7);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_8_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_8_END) {
            // 预留89级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_8);
        } else if (banCode >= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_9_START
                && banCode <= AccountErrorCode.RESULT_SERVER_BAN_LEVEL_9_END) {
            // 预留89级封禁
            StatisticsLogForMutilFields.getInstance()
                    .updateCount(StatisticsLogForMutilFields.StatisticsKeys.SERVER_BAN_CLICK_APPEAL_LEVEL_9);
        }
    }

    /**
     * 错误码是否是用户疑似被盗号
     *
     * @param errno
     *
     * @return
     */
    public static boolean isDoubtHackingErrorCode(int errno) {
        return errno == AccountErrorCode.RESULT_DOUBT_HACKING;
    }

}
