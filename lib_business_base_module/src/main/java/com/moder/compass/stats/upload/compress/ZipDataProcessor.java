package com.moder.compass.stats.upload.compress;

import android.text.TextUtils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.ZipUtils;
import com.dubox.drive.kernel.util.encode.RC4Util;

/**
 * Created by liuliangping on 2016/9/13.
 */
class ZipDataProcessor implements ICompress {
    private static final String TAG = "ZipDataFactory";

    @Override
    public byte[] zipCompress(String source) {
        if (TextUtils.isEmpty(source)) {
            return null;
        }

        byte[] zip = null;
        try {
            zip = ZipUtils.compressToByte(RC4Util.makeRc4(source));
        } catch (ArrayIndexOutOfBoundsException e) {
            DuboxLog.e(TAG, "RC4Util.ArrayIndexOutOfBoundsException:" + e.getMessage());
        }

        return zip;
    }
}
