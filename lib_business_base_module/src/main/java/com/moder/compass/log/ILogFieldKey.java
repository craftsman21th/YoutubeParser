package com.moder.compass.log;

/**
 * Created by liuliangping on 2016/3/22.
 */
public interface ILogFieldKey {
    String CLIENT_TYPE = "client_type";

    // DownloadFiles or UploadFiles
    String OP = "op";

    // 日志类型 file ，block_speed(表示成功) or block_fail

    String TYPE = "type";

    String UID = "uid";

    // 3G/4G/WiFi
    String NET_TYPE = "net_type";

    /**
     * client ip地址
     */
    String CLIENT_IP = "client_ip";

    /**
     * server ip地址
     */
    String SERVER_IP = "server_ip";

    /**
     * 客户端类型
     */
    String USER_AGENT = "user_agent";

    /**
     * 客户端版本
     */
    String CLIENT_VERSION = "client_version";

    /**
     * 上传类型
     */
    String MODEL = "mode";

    /**
     * 客户端版本
     */
    String VERSION = "version";
}
