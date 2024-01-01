package com.moder.compass.base.imageloader;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by guanshuaichao on 2018/11/20.
 */
class GlidePreLoadStateNotifier {

    /**
     * 预加载任务队列状态回调
     */
    private final List<IGlidePreLoadIdleListener> mStateListeners = new LinkedList<>();

    void registerPreLoadStateListener(IGlidePreLoadIdleListener stateListener) {
        synchronized (mStateListeners) {
            mStateListeners.add(stateListener);
        }
    }

    void unregisterPreLoadStateListener(IGlidePreLoadIdleListener stateListener) {
        synchronized (mStateListeners) {
            mStateListeners.remove(stateListener);
        }
    }

    void notifyIdle() {
        List<IGlidePreLoadIdleListener> listeners = getStateListeners();
        for (IGlidePreLoadIdleListener listener : listeners) {
            listener.onPreLoadTaskIdle();
        }
    }

    private List<IGlidePreLoadIdleListener> getStateListeners() {
        List<IGlidePreLoadIdleListener> listeners = new LinkedList<>();
        synchronized (mStateListeners) {
            listeners.addAll(mStateListeners);
        }
        return listeners;
    }
}
