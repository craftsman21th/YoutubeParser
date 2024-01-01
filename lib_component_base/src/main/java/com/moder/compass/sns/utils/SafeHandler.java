package com.moder.compass.sns.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by zhuwenjun on 2017/12/29.
 */
public class SafeHandler extends Handler {
    private final WeakReference<IHandlerHost> mThis;

    public SafeHandler(IHandlerHost f, Looper looper) {
        super(looper);
        mThis = new WeakReference<IHandlerHost>(f);
    }

    public SafeHandler(IHandlerHost f) {
       this(f, Looper.myLooper());
    }

    @Override
    public void handleMessage(Message msg) {
        IHandlerHost f = mThis.get();
        if (f != null) {
            f.handleMessage(msg);
        }

    }

    public interface IHandlerHost {
        void handleMessage(Message msg);
    }
}
