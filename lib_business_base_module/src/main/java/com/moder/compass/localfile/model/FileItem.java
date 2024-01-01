package com.moder.compass.localfile.model;

import java.io.File;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.moder.compass.localfile.utility.MimeTypeParser;
import com.moder.compass.localfile.utility.MimeTypes;
import com.dubox.drive.basemodule.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * FileItem wraps the useful info of Files
 * 
 */
public class FileItem implements Parcelable {

    private static final String TAG = "FileItem";

    private MimeTypes mMimeTypes;

    public enum FileType {
        EAudio, EVideo, EImage, EDocument, EInstallPackage, EDirectory, EOthers
    }

    private FileType mFileType = FileType.EOthers;

    // file path, MUST be the absolute Path
    public String mFilePath;
    private String mFileName;
    // the name which is shown in the UI
    private String mShowName;

    private Long mLastModifiedTime;
    private int mIcon;

    public FileItem(String path) {
        mFilePath = path;
    }

    /**
     * constructor
     * 
     * @param aFile
     */
    public FileItem(File aFile) {
        init(aFile);
    }

    private void init(File aFile) {
        if (null != aFile) {
            mFileName = aFile.getName();
            mLastModifiedTime = aFile.lastModified();
            try {
                mFilePath = aFile.getCanonicalPath();
            } catch (Exception e) {
                DuboxLog.e(TAG, "", e);
            }
            if (aFile.isDirectory()) {
                mFileType = FileType.EDirectory;
            } else {

                String mimetype = getMimeType(mFileName);
                if (null != mimetype) {
                    if (mimetype.startsWith("image")) {
                        mFileType = FileType.EImage;
                    } else if (mimetype.startsWith("video")) {
                        mFileType = FileType.EVideo;
                    } else if (mimetype.equals("application/vnd.android.package-archive")) {
                        mFileType = FileType.EInstallPackage;
                    } else {
                        mFileType = FileType.EOthers;
                    }
                } else {
                    mFileType = FileType.EOthers;
                }
            }

            mShowName = mFileName;
        }
    }

    private void initMimeTypes() {
        MimeTypeParser mtp = new MimeTypeParser();
        XmlResourceParser in = BaseApplication.getInstance().getResources().getXml(R.xml.mimetypes);
        try {
            mMimeTypes = mtp.fromXmlResource(in);
        } catch (XmlPullParserException e) {
            DuboxLog.e(TAG, "PreselectedChannelsActivity: XmlPullParserException", e);
        } catch (IOException e) {
            DuboxLog.e(TAG, "PreselectedChannelsActivity: IOException", e);
        }
    }

    private String getMimeType(String fileName) {
        if (mMimeTypes == null) {
            initMimeTypes();
        }
        return mMimeTypes.getMimeType(fileName);
    }

    public FileItem(String path, String name) {
        mFilePath = path;
        mFileName = name;
        mFileType = FileType.EDirectory;
        mLastModifiedTime = System.currentTimeMillis();// set a default value in
        // this kind of constructor
        mShowName = mFileName;
    }

    public String getShowName() {
        return mShowName;
    }

    protected void setShowName(String name) {
        mShowName = name;
    }

    public FileType getFileType() {
        return mFileType;
    }

    protected void setFileType(FileType type) {
        mFileType = type;
    }

    public void setFilePath(String path) {
        mFilePath = path;
    }

    protected void setFileName(String name) {
        mFileName = name;
    }

    public long getFileLastModifiedTime() {
        return mLastModifiedTime;
    }

    protected void setLastModifiedTime(long tm) {
        mLastModifiedTime = tm;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int aIcon) {
        mIcon = aIcon;
    }

    private String getParentPath() {
        return new File(mFilePath).getParentFile().getAbsolutePath();
    }

    /**
     * 是否文件夹
     *
     * @return
     */
    public boolean isDir() {
        return new File(mFilePath).isDirectory();
    }

    /**
     * 获取文件大小
     *
     * @return
     */
    public long getFileSize() {
        if (TextUtils.isEmpty(mFilePath)) {
            return 0L;
        }
        return new File(mFilePath).length();
    }

    /**
     * 获取文件名
     *
     * @return
     */
    public String getFileName() {
        if (TextUtils.isEmpty(mFileName)) {
            return new File(mFilePath).getName();
        }
        return mFileName;
    }

    /**
     * 获取文件路径
     *
     * @return
     */
    public String getFilePath() {
        return mFilePath;
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mFileName == null) ? 0 : mFileName.hashCode());
        result = prime * result + ((mFilePath == null) ? 0 : mFilePath.hashCode());
        return result;
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileItem other = (FileItem) obj;
        if (mFileName == null) {
            if (other.mFileName != null)
                return false;
        } else if (!mFileName.equals(other.mFileName))
            return false;
        if (mFilePath == null) {
            if (other.mFilePath != null)
                return false;
        } else if (!mFilePath.equals(other.mFilePath))
            return false;
        return true;
    }

    public FileItem(Parcel source) {
        mFileName = source.readString();
        mFilePath = source.readString();
    }

    /**
     * @return
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @param dest
     * @param flags
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFileName);
        dest.writeString(mFilePath);
    }

    /**
     * Intent传递时序列化
     */
    public static final Parcelable.Creator<FileItem> CREATOR = new Creator<FileItem>() {

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }

        // 将Parcel对象反序列化为ParcelableDate
        @Override
        public FileItem createFromParcel(Parcel source) {
            return new FileItem(source);
        }
    };
}
