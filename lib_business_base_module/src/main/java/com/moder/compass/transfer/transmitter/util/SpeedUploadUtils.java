/*
 * SpeedUploadUtils.java
 * @author weizhengzheng
 * V 1.0.0
 * Create at 2013年10月29日 下午5:37:06
 */
package com.moder.compass.transfer.transmitter.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.transfer.transmitter.ErrorMessageHelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 * @author weizhengzheng <br/>
 *         create at 2013年10月29日 下午5:37:06
 */
public class SpeedUploadUtils {
    private static final String ITEM_SPLIT = "@#";
    private static final String TAG = "SpeedUploadUtils";

    private static volatile SpeedUploadUtils instance;

    private SpeedUploadUtils() {
    }

    public static SpeedUploadUtils getInstance() {
        if (instance == null) {
            synchronized (SpeedUploadUtils.class) {
                if (instance == null) {
                    instance = new SpeedUploadUtils();
                }
            }
        }
        return instance;
    }

    /** 统计数据的阀值，低于2m的文件不统计 **/
    private static final long COUNT_THRESHOLD = 2 * 1024 * 1024;

    /** 上传 **/
    public static final int OP_TYPE_UPLOAD = 0;
    /** 盘内文件下载 **/
    public static final int OP_TYPE_DOWNLOAD = 1;
    /** 外链下载 **/
    public static final int OP_TYPE_DLINK = 2;

    /** 传输完成 **/
    public static final int REASON_SUCCESS = 0;
    /** 文件大小不一致 **/
    public static final int REASON_SIZE_MISMATCH = 1;
    /** 本地无网络 **/
    public static final int REASON_NO_NET = 2;
    /** 连接超时 **/
    public static final int REASON_TIME_OUT = 3;
    /** 空间问题 **/
    public static final int REASON_SPACE = 4;

    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public void addSpeedRecord(final long fileSize, final long completeSize, final long startTime,
            final String serverIp, final String url, final int type, final boolean status, final String fsid,
            final int errorCode, final long partSize, final String requestId) {
        if (fileSize < COUNT_THRESHOLD) {
            return;
        }
        final long endTime = System.currentTimeMillis();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String netType = getNetworkInfo();
                TransmissionInfo object = new TransmissionInfo(completeSize, fileSize, startTime, endTime, netType,
                        serverIp, url, type, status, fsid, errorCode, partSize, requestId);

                DuboxLog.d(TAG, "get info :" + object.toString());
                DuboxStatisticsLogForSpeedUpload.getInstance().updateSpeedCount(object);
            }
        });
    }

    public void addExceptionRecord(final Exception e, final int type) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                ExceptionInfo info = new ExceptionInfo(e, type);
                DuboxStatisticsLogForSpeedUpload.getInstance().updateExceptionCount(info);
            }
        });
    }

    private static String getOPType(int type) {
        if (type == OP_TYPE_UPLOAD) {
            return "UploadFiles";
        } else if (type == OP_TYPE_DOWNLOAD) {
            return "DownloadFiles";
        } else {
            return null;
        }
    }

    /**
     * 获取网络连接类型信息
     *
     * @return
     */
    private String getNetworkInfo() {
        String netType = null;
        ConnectivityManager connectMgr =
                (ConnectivityManager) BaseApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        try {
            info = connectMgr.getActiveNetworkInfo();
        } catch (Exception e) {
            DuboxLog.e(TAG, "", e);
        }
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = "WIFI";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                netType = info.getSubtypeName();
            }
        }
        return netType;
    }

    class TransmissionInfo {
        /**  **/

        /** 传输大小 **/
        long mCompleteSize;
        /** 文件大小 **/
        long fileSize;
        /** 开始时间 **/
        long startTime;
        /** 结束时间 **/
        long endTime;
        /** 网络类型 **/
        String netType;
        /** 服务器IP **/
        String serverIp;
        /** 目标url **/
        String url;
        /** 操作类型 **/
        int type;
        /** 传输成功与否 **/
        boolean status;
        /** 当前传输的fsid **/
        String fsid;
        /** 传输错误码 **/
        int errorCode;
        /** 当前块大小 **/
        long partSize;
        /** 请求id **/
        String requestId;

        /**
         *
         * @param completeSize
         * @param fileSize
         * @param startTime
         * @param endTime
         * @param netType
         * @param serverIp
         * @param url
         * @param type
         * @param status
         * @param fsid
         * @param errorCode
         * @param partSize
         * @param requestId
         */
        TransmissionInfo(long completeSize, long fileSize, long startTime, long endTime, String netType,
                String serverIp, String url, int type, boolean status, String fsid, int errorCode, long partSize,
                String requestId) {
            super();
            this.mCompleteSize = completeSize;
            this.fileSize = fileSize;
            this.startTime = startTime;
            this.endTime = endTime;
            this.netType = netType;
            this.serverIp = serverIp;
            this.url = url;
            this.type = type;
            this.status = status;
            this.fsid = fsid;
            this.errorCode = errorCode;
            this.partSize = partSize;
            this.requestId = requestId;
        }

        /**
         * @return
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "TransmissionInfo [size=" + mCompleteSize + ", fileSize=" + fileSize + ", startTime=" + startTime
                    + ", endTime=" + endTime + ", netType=" + netType + ", serverIp=" + serverIp + ", requestId="
                    + requestId + ", url=" + url + ", type=" + type + ", status=" + status + ", fsid=" + fsid
                    + ", stopReason=" + errorCode + ", partSize=" + partSize + "]";
        }

        public String getOP() {
            return getOPType(this.type);
        }

        String createRecord() {

            StringBuilder sb = new StringBuilder();
            if (type == OP_TYPE_DOWNLOAD) {
                if (status) {
                    sb.append("type=").append("block_speed").append(ITEM_SPLIT);
                } else {
                    sb.append("type=").append("block_fail").append(ITEM_SPLIT);
                    sb.append("url=").append(url).append(ITEM_SPLIT);
                    sb.append("range_size=").append(partSize).append(ITEM_SPLIT);
                    sb.append("error_code=").append(errorCode).append(ITEM_SPLIT);
                    sb.append("request_id=").append(requestId).append(ITEM_SPLIT);
                }
                sb.append("domain_ip=").append(serverIp).append(ITEM_SPLIT);
                // sb.append("client_ip=").append(clientIp).append(ITEM_SPLIT);
                sb.append("recv_time=").append(endTime - startTime).append(ITEM_SPLIT);
                sb.append("recv_bytes=").append(mCompleteSize).append(ITEM_SPLIT);
                sb.append("nettype=").append(netType);
            } else if (type == OP_TYPE_UPLOAD) {
                if (status) {
                    sb.append("type=").append("block_speed").append(ITEM_SPLIT);
                } else {
                    sb.append("type=").append("block_fail").append(ITEM_SPLIT);
                    sb.append("block_size=").append(partSize).append(ITEM_SPLIT);
                    // sb.append("client_ip=").append(clientIp).append(ITEM_SPLIT);
                    sb.append("error_code=").append(errorCode).append(ITEM_SPLIT);
                    sb.append("request_id=").append(requestId).append(ITEM_SPLIT);
                }

                sb.append("send_time=").append(endTime - startTime).append(ITEM_SPLIT);
                sb.append("send_bytes=").append(mCompleteSize).append(ITEM_SPLIT);
                sb.append("domain_ip=").append(serverIp).append(ITEM_SPLIT);
                // sb.append("client_ip=").append(clientIp).append(ITEM_SPLIT);
                sb.append("nettype=").append(netType);
            }
            return sb.toString();
        }
    }

    static class ExceptionInfo {
        private String mExceptionInfo;
        String mOpType;

        ExceptionInfo(Exception e, int type) {
            mExceptionInfo = ErrorMessageHelper.getExceptionStack(e);
            mOpType = getOPType(type);
        }

        String getOpType() {
            return mOpType;
        }

        String creatInfo() {
            StringBuilder sb = new StringBuilder();
            sb.append("uid=").append(Account.INSTANCE.getUid()).append(ITEM_SPLIT);
            sb.append("device_name=").append(android.os.Build.MODEL).append(ITEM_SPLIT);
            sb.append("device_version=").append(android.os.Build.VERSION.RELEASE).append(ITEM_SPLIT);
            sb.append("excption_stack=").append(mExceptionInfo).append(ITEM_SPLIT);
            return sb.toString();
        }
    }

}
