package com.moder.compass.albumbackup;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 多媒体备份的文件描述，包含文件路径和本地修改时间
 *
 * Created by huantong on 2018/5/22.
 */
public class MediaFile implements Parcelable {
    private static final String TAG = "MediaFile";

    /**
     * 本地文件名称
     */
    private String fileName;
    /**
     * 相册id
     */
    private String bucketId;
    /**
     * 相册名称
     */
    private String bucketName;
    /**
     * 本地的路径
     */
    private String localPath;

    /**
     * 本地文件的创建时间
     */
    private long lmtime;

    /**
     * 本地文件的MD5
     */
    private String filemd5 = "";

    /**
     * 本地文件的尺寸
     */
    private long fileSize;

    /**
     * 本地文件后缀名
     */
    private String fileSuffix;

    /**
     * 本地图片/视频文件分辨率
     */
    private int fileWidth;
    private int fileHeight;

    /**
     * 本地图片/视频拍摄时间
     */
    private long dateDaken;

    /**
     * 本地图片/视频拍摄时间-年、月、日
     */
    private int year;
    private int month;
    private int day;

    /**
     * 本地图片/视频缩略图地址
     */
    private String thumbnailUrl;

    /**
     * 视频时长
     */
    private long duration;

    /**
     * 媒体类型
     */
    private int fileType;

    /**
     * 是否是端计算判定的最优结果
     */
    private int isOptimal = 0; // 0: 非最优结果 1: 最优结果

    /**
     * 备份状态，内存中做相册中已备份/未备份的数据merge的中转用。
     */
    private int backupState = 0;

    // 配置文件的分隔符
    public static final String SPLIT = ",,";

    public MediaFile(final String localPath) {
        this(localPath, 0);
    }

    public MediaFile(final String localPath, final long lmtime) {
        this(null, localPath, lmtime);
    }

    public MediaFile(final String md5, final String localPath, final long lmtime) {
        this.filemd5 = md5;
        this.localPath = localPath;
        this.lmtime = lmtime;
    }

    private MediaFile(Parcel source) {
        this.fileName = source.readString();
        this.bucketId = source.readString();
        this.bucketName = source.readString();
        this.localPath = source.readString();
        this.lmtime = source.readLong();
        this.filemd5 = source.readString();
        this.fileSize = source.readLong();
        this.fileSuffix = source.readString();
        this.fileWidth = source.readInt();
        this.fileHeight = source.readInt();
        this.dateDaken = source.readLong();
        this.year = source.readInt();
        this.month = source.readInt();
        this.day = source.readInt();
        this.thumbnailUrl = source.readString();
        this.duration = source.readLong();
        this.fileType = source.readInt();
        this.isOptimal = source.readInt();
        this.backupState = source.readInt();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the localPath
     */
    public String getLocalPath() {
        return this.localPath;
    }

    /**
     * @return the filemd5
     */
    public String getFilemd5() {
        return this.filemd5;
    }

    public void setFilemd5(String filemd5) {
        this.filemd5 = filemd5;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (!o.getClass().getName().equals(this.getClass().getName())) {
            return false;
        }
        MediaFile oo = (MediaFile) o;
        return oo.localPath.equals(this.localPath) && oo.lmtime == this.lmtime && oo.filemd5.equals(this.filemd5);
    }

    @Override
    public int hashCode() {
        return (int) (localPath.hashCode() + lmtime + filemd5.hashCode());
    }

    /**
     * @return the lctime
     */
    public long getLmtime() {
        return this.lmtime;
    }

    /**
     * Intent传递时序列化
     */
    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }

        // 将Parcel对象反序列化为ParcelableDate
        @Override
        public MediaFile createFromParcel(Parcel source) {
            return new MediaFile(source);
        }
    };

    /**
     * @return
     * @see Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @param dest
     * @param flags
     * @see Parcelable#writeToParcel(Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(bucketId);
        dest.writeString(bucketName);
        dest.writeString(localPath);
        dest.writeLong(lmtime);
        dest.writeString(filemd5);
        dest.writeLong(fileSize);
        dest.writeString(fileSuffix);
        dest.writeInt(fileWidth);
        dest.writeInt(fileHeight);
        dest.writeLong(dateDaken);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeString(thumbnailUrl);
        dest.writeLong(duration);
        dest.writeInt(fileType);
        dest.writeInt(isOptimal);
        dest.writeInt(backupState);
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public int getFileWidth() {
        return fileWidth;
    }

    public void setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
    }

    public int getFileHeight() {
        return fileHeight;
    }

    public void setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
    }

    public long getDateDaken() {
        return dateDaken;
    }

    public void setDateDaken(long dateDaken) {
        this.dateDaken = dateDaken;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getIsOptimal() {
        return isOptimal;
    }

    public void setIsOptimal(int isOptimal) {
        this.isOptimal = isOptimal;
    }

    public int getBackupState() {
        return backupState;
    }

    public void setBackupState(int backupState) {
        this.backupState = backupState;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}