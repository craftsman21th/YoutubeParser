/*
 * FileTypeCountEntry.java
 * classes : com.dubox.drive.localfilesystem.p2plocalfile.mode.FileTypeCountEntry
 * @author 文超
 * V 1.0.0
 * Create at 2014-8-13 下午12:04:19
 */
package com.moder.compass.localfile.model;

/**
 * 用来记录文件列表中，文件夹和文件数量 com.dubox.drive.localfilesystem.p2plocalfile.mode.FileTypeCountEntry
 *
 * @author 文超 <br/>
 *         create at 2014-8-13 下午12:04:19
 */
public class FileTypeCountEntry {
    private static final String TAG = "FileTypeCountEntry";

    public int dirCount;
    public int fileCount;

    public FileTypeCountEntry(int dirCount, int fileCount) {
        super();
        this.dirCount = dirCount;
        this.fileCount = fileCount;
    }

}
