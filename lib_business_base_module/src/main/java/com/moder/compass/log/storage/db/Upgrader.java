package com.moder.compass.log.storage.db;

import com.dubox.drive.kernel.architecture.db.IUpgradable;
import com.dubox.drive.kernel.architecture.db.IVersion;

/**
 * Created by liuliangping on 2016/5/27.
 */
public class Upgrader implements IUpgradable {
    private static final String TAG = "Upgrader";

    @Override
    public IVersion upgrade(int currentVersion) {
        return null;
    }
}
