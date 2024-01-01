package com.moder.compass.preview;

/**
 * 文档预览常量配置
 *
 * @author guanshuaichao
 * @since 2020-01-08
 */
public interface IDocumentOpenFrom {
    /** 打开本地文件 */
    String FROM_LOCAL = "file_form_local";
    /** 打开隐藏空间文件 */
    String FROM_SAFE_BOX = "from_safe_box";
    /** 打开网盘文件 */
    String FROM_NETDISK = "file_form_netdisk";
    /** 打开未知来源文件 */
    String FROM_UNKNOWN = "file_from_unknown";

    String FROM_LINK_SHARE = "file_link_share";
}
