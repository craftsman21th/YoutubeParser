
package com.moder.compass.statistics;

import android.content.Context;

import com.moder.compass.account.broadcast.AbstractAccountChangeBroadcastReceiver;
import com.moder.compass.stats.DuboxStats;
import com.moder.compass.stats.DuboxStatsEngine;
import com.moder.compass.stats.DuboxStatsNew;
import com.moder.compass.stats.StatisticsType;

/**
 * Created by linchangxin on 2014/12/28.
 */
public class AccountChangeListener extends AbstractAccountChangeBroadcastReceiver {

    @Override
    protected void onLogin(Context context) {
        // 登录成功后重置新统计的实体类
        // DuboxStatisticsLogForMutilFields.getInstance().reset();
        /**
         * @since moder 2.19.0
         * 通过日志发现新用户登陆成功后，桌面Icon对应的前台归因埋点：launch_from_click_icon 没有进行上报
         * 原因是登陆成功后该方法会把埋点的实例对象 DuboxStatsEngine 设置为 null，导致原先的埋点无法被上报
         *
         * 调用 uploadForce 会直接触发上报逻辑
         */
        DuboxStats duboxStats = DuboxStatsEngine.getInstance().getDuboxStats(StatisticsType.NEW);
        if (duboxStats instanceof DuboxStatsNew) {
            ((DuboxStatsNew) duboxStats).uploadForce();
        }
        DuboxStatsEngine.releaseInstance();
    }

    @Override
    protected void onLogout(Context context) {

    }
}
