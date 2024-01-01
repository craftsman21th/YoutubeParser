package com.moder.compass.transfer.transmitter;

import java.util.List;

import android.util.Pair;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.transfer.transmitter.locate.LocateDownloadUrls;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.google.gson.annotations.Expose;

/**
 * 下载传输bean，可以作为整个文件的描述，同时也作为文件分块的描述
 * 
 * @author sunqi01
 * 
 */
public class TransmitBlock implements Cloneable {
    private static final String TAG = "TransmitBlock";

    // 每个服务器地址被访问的最大次数
    private static final int MAX_SERVER_CONNECT_TIME = 2;
    /**
     * 下载地址
     */
    public List<LocateDownloadUrls> urls; // 下载地址
    /**
     * 保存路径
     */
    @Expose
//    public String destinationPath; // 保存路径, target29以后，保存的是Download目录下uri, 29以下保存文件绝对路径
    public RFile destinationPath; // 保存路径, target29以后，保存的是Download目录下uri, 29以下保存文件绝对路径
    /**
     * 临时文件路径
     */
    @Expose
//    public String tempDestinationPath;
    public RFile tempDestinationPath; // 注意：target30时，该属性为null
    @Expose
    /**
     * 文件总大小
     */
    public long fileSize; // 文件总大小
    @Expose
    /**
     * 块起点
     */
    public long startPosition; // 块起点
    @Expose
    /**
     * 块终点
     */
    public long endPosition; // 块终点
    @Expose
    /**
     * 完成的大小
     */
    public long completeSize; // 块当前进度
    @Expose
    /**
     * 块ID
     */
    public int blockId; // 块Id
    @Expose
    /**
     * 是否下载到私有目录
     */
    public boolean isDownloadPrivateDir; // 块Id
    /**
     * 下载文件的名称
     */
    public String fileName;

    /**
     * first是serverList的index
     * <p>
     * second是对其访问的次数
     */
    private Pair<Integer, Integer> mIndexTimesPair = null;

    /**
     * 设置URLS
     * 
     * @param urls
     */
    public void setUrls(List<LocateDownloadUrls> urls) {
        this.urls = urls;
        mIndexTimesPair = null;
    }

    /**
     * 
     * @param isRetry
     * @return
     */
    LocateDownloadUrls getServer(boolean isRetry) {
        if (CollectionUtils.isEmpty(urls)) {
            return null;
        }
        // 如果在pathIndexMap找不到，那么表明这个path是对serverList的第一次访问
        if (mIndexTimesPair == null) {
            // 为path初始化value
            mIndexTimesPair = new Pair<Integer, Integer>(0, 1);
        } else {// 否则，将下标的访问次数++
            if (isRetry) {
                // 计算下标，如果下标的访问次数超过阀值，那么下标++，并将访问次数清零
                if (mIndexTimesPair.second >= MAX_SERVER_CONNECT_TIME) {
                    mIndexTimesPair = new Pair<Integer, Integer>(mIndexTimesPair.first + 1, 1);
                } else {
                    // 将path和serverList的下标以及该下标被访问的次数记录到pathIndexMap
                    mIndexTimesPair = new Pair<Integer, Integer>(mIndexTimesPair.first, mIndexTimesPair.second + 1);
                }
            }
        }

        int index = mIndexTimesPair.first;
        // 通过下标从serverList获取server
        if (index >= urls.size()) {
            mIndexTimesPair = null;
            return null;
        } else {
            DuboxLog.d(TAG, index + "," + mIndexTimesPair.second + "," + urls.get(index));
            return urls.get(index);
        }
    }

    @Override
    public TransmitBlock clone() {
        try {
            return (TransmitBlock) super.clone();
        } catch (CloneNotSupportedException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            TransmitBlock bean = new TransmitBlock();
            bean.urls = urls;
            bean.destinationPath = destinationPath;
            bean.tempDestinationPath = tempDestinationPath;
            bean.fileSize = fileSize;
            bean.startPosition = startPosition;
            bean.endPosition = endPosition;
            bean.completeSize = completeSize;
            bean.blockId = blockId;
            bean.isDownloadPrivateDir = isDownloadPrivateDir;
            return bean;
        }
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TransmitBlock [urls=" + urls + ", destinationPath=" + destinationPath + ", tempDestinationPath="
                + tempDestinationPath + ", fileSize=" + fileSize + ", startPosition=" + startPosition
                + ", endPosition=" + endPosition + ", completeSize=" + completeSize + ", blockId=" + blockId
                + ", mIndexTimesPair=" + mIndexTimesPair + "]";
    }

}
