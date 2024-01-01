/*
 * ISelectionInterface.java
 * classes : com.baidu.netdisk.localfilesystem.p2plocalfile.interfacepkg.ISelectionInterface
 * @author 文超
 * V 1.0.0
 * Create at 2014-8-8 下午3:44:46
 */
package com.moder.compass.ui.localfile.baseui;

import com.moder.compass.localfile.model.FileItem;

import java.util.ArrayList;

/**
 * com.baidu.netdisk.localfilesystem.p2plocalfile.interfacepkg.ISelectionInterface
 *
 * @author 文超 <br/>
 *         create at 2014-8-8 下午3:44:46
 */
public interface ISelectionInterface {

    void addSelectedPosition(int position);

    void removeSelectedPosition(int position);

    void removeAllSelectedPositions();

    FileItem getSelectedFile(int position);

    int getSelectedFilesCount();

    ArrayList<FileItem> getSelectedFiles();

    boolean isSelected(int position);
}
