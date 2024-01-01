package com.moder.compass.localfile.model;

import java.io.File;

public class MediaFileItem extends FileItem {
    private String mRemoteUrl;

    public MediaFileItem(File aFile) {
        super(aFile);
    }

    public static MediaFileItem create(String filePath, String removeUrl) {
        File file = new File(filePath);
        MediaFileItem item = new MediaFileItem(file);
        item.setRemoteUrl(removeUrl);
        return item;
    }

    public void setRemoteUrl(String url) {
        mRemoteUrl = url;
    }

    public String getRemoteUrl() {
        return mRemoteUrl;
    }

    @Override
    public int hashCode() {
        return mFilePath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MediaFileItem) {
            return mFilePath.equalsIgnoreCase(((MediaFileItem) o).getFilePath()); // 重复备份时有可能有问题
        }
        return false;
    }
}
