package com.moder.compass.stats.storage.db;

import com.dubox.drive.kernel.architecture.db.IUpgradable;
import com.dubox.drive.kernel.architecture.db.IVersion;

/**
 * Created by liuliangping on 2016/9/7.
 */
public class Upgrader implements IUpgradable {
    private static final String TAG = "Upgrader";

    @Override
    public IVersion upgrade(int currentVersion) {
        switch (currentVersion) {
            case 2:
                return new Version2();
            default:
                return null;
        }
    }
}
