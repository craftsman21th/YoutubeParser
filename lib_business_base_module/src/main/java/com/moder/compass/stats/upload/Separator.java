package com.moder.compass.stats.upload;

/**
 * Created by liuliangping on 2016/9/12.
 *
 * 字符常量
 */
public class Separator {
    /**
     * 每条统计数据项中间的间隔行
     **/
    public static final String ITEM_SPLIT = "@#";

    /**
     * 键值连接符
     */
    public static final String ITEM_EQUALS = "=";

    /**
     * op和其param的分隔符
     * op=dynamic_plugin_sync&type::ignore@pluginid::123
     */
    public static final String OP_PARAM_SPLIT = "&";

    /**
     * param的之间分隔符
     * op=dynamic_plugin_sync&type::ignore@pluginid::123
     */
    public static final String PARAM_SPLIT = "@";

    /**
     * op参数中键值对连接符
     * op=dynamic_plugin_sync&type::ignore@pluginid::123
     */
    public static final String PARAM_EQUALS = "::";

    /**
     * 文件换行分割符
     */
    public static final String LINE_SPLIT = "\r\n";
}
