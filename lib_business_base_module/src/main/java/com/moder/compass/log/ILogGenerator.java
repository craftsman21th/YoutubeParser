package com.moder.compass.log;

/**
 * Created by liuliangping on 2016/3/22.
 */
public interface ILogGenerator<T> {
    /**
     * 日志生成器
     *
     * @return
     */
    String generator(T field);

    /**
     * 日志生成器
     *
     * @return
     */
    void clear(T field);
}
