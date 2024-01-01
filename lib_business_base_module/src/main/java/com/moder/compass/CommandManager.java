package com.moder.compass;

import java.net.URLDecoder;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.encode.Base64Util;

import android.text.TextUtils;

/**
 * 口令管理器
 * <p>
 * Created by lijunnian on 2019/05/14.
 */
public class CommandManager {

    private static final String TAG = "CommandManager";

    private static CommandManager mInstance;

    public static CommandManager getInstance() {
        if (mInstance == null) {
            synchronized (CommandManager.class) {
                if (mInstance == null) {
                    mInstance = new CommandManager();

                }
            }
        }
        return mInstance;
    }
    /**
     * 解码口令
     *
     * @param encodeUrl
     * @return
     */
    public String decodeCommand(CharSequence encodeUrl) {
        if (TextUtils.isEmpty(encodeUrl)) {
            return null;
        }

        String decodeUrl = null;
        try {
            byte[] decode = Base64Util.decode(String.valueOf(encodeUrl));
            if (decode != null) {
                decodeUrl = URLDecoder.decode(new String(decode), "UTF8");
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
        return decodeUrl;
    }
}
