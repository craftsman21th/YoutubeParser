package com.moder.compass.util.receiver;

import android.os.Handler;
import androidx.annotation.NonNull;

/**
 * Created by manyongqiang on 2018/1/10.
 * 没有业务只有视图的结果接收器
 */

public class SimpleResultReceiver extends BaseResultReceiver {
    public SimpleResultReceiver(@NonNull Object reference,
                                @NonNull Handler handler,
                                ResultView resultView) {
        super(reference, handler, resultView);
    }
}
