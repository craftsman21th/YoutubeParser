package com.moder.compass.transfer.transmitter;

import android.text.TextUtils;

import com.moder.compass.base.utils.GlobalConfigKey;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;

public class TransferLogUtil {

    public static void saveClientIp(String clientIp) {

        if (!TextUtils.isEmpty(clientIp)
                && !TextUtils.equals(GlobalConfig.getInstance().getString(GlobalConfigKey.CLIENT_IP), clientIp)) {
            GlobalConfig.getInstance().putString(GlobalConfigKey.CLIENT_IP, clientIp);
            GlobalConfig.getInstance().commit();
        }
    }
}
