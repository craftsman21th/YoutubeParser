/*
 * FileInfoColumns.java
 * classes : com.dubox.drive.localfilesystem.p2plocalfile.interfacepkg.FileInfoColumns
 * @author 文超
 * V 1.0.0
 * Create at 2014-8-11 下午7:32:50
 */
package com.moder.compass.localfile;

import android.provider.BaseColumns;

/**
 * com.dubox.drive.localfilesystem.p2plocalfile.interfacepkg.FileInfoColumns
 *
 * @author 文超 <br/>
 *         create at 2014-8-11 下午7:32:50
 */
public interface FileInfoColumns extends BaseColumns {

    String FILE_NAME = "FILE_NAME";
    String FILE_PATH = "FILE_PATH";
    /**
     * 1:dir, 0:file
     */
    String IS_DIR = "IS_DIR";
    String SIZE = "SIZE";

    String[] PROJECTION = { _ID, FILE_NAME, FILE_PATH, IS_DIR, SIZE };
}
