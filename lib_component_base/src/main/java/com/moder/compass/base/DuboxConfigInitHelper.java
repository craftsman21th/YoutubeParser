package com.moder.compass.base;

import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.architecture.config.DebugConfig;
import com.dubox.drive.kernel.i18n.ApplicationLanguageKt;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.config.IAccountChecker;
import com.dubox.drive.kernel.architecture.config.IParameter;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.config.ServerConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import kotlin.jvm.functions.Function0;

/**
 * Created by 魏铮铮 on 15/1/7.
 */
public class DuboxConfigInitHelper implements IAccountChecker, IParameter {

    private Context context;

    @Override
    public boolean checkAccount() {
        String accountInfo = Account.INSTANCE.getUid();
        String bduss = Account.INSTANCE.getNduss();
        if (TextUtils.isEmpty(bduss) || TextUtils.isEmpty(accountInfo)) {
            DuboxLog.e("DuboxConfigInitHelper",
                    "account info=" + accountInfo + " bduss=" + bduss);
            return false;
        }
        return true;
    }

    @Override
    public String getStoragePath() {
        ApplicationInfo info = BaseApplication.getInstance().getApplicationInfo();
        return FileUtils.getFilePath(info.dataDir, "shared_prefs");
    }

    @Override
    public String getStorageFileName() {
        return Account.INSTANCE.getUid() + "duboxdrive.ini";
    }

    @Override
    public boolean isNeedEncrypt() {
        return false;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public String getMMKVId() {
        return Account.INSTANCE.getUid() + "dubox.mmkv";
    }

    public void init(Context context) {
        DuboxLog.d("DuboxConfigInitHelper", "init all config ");
        this.context = context;
        GlobalConfig.getInstance().init(context);
        ServerConfig.init(context, new Function0() {
            @Override
            public Object invoke() {
                return Account.INSTANCE.getUid() + ApplicationLanguageKt.getLanguageInSupported();
            }
        });

        PersonalConfig.getInstance().destroyConfig();
        PersonalConfig.getInstance().init(this, this);
        DebugConfig.init(context);
    }

}
