package com.moder.compass.localfile.utility;

import com.dubox.drive.cloudfile.utils.FileType;

/**
 * Created by liji01 on 15-2-9.
 */
public enum FilterType {
    EAllFiles,EVideo, EAudio, EImage, EDocument, EApp, EOther, EBT, EDirectory;

    public static boolean acceptType(String filePath, FilterType mFilterType) {

        if (mFilterType == EImage) {
            return FileType.isImage(filePath);
        } else if (mFilterType == EAudio) {
            return FileType.isMusic(filePath);
        } else if (mFilterType == EVideo) {
            return FileType.isVideo(filePath);
        } else if (mFilterType == EDocument) {
            return FileType.isDoc(filePath);
        } else if (mFilterType == EApp) {
            return FileType.isApp(filePath);
        } else if (mFilterType == EAllFiles) {
            return true;
        }
        return false;
    }
}
