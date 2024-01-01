/*
 * IDuboxFileView.java
 * classes : com.dubox.drive.ui.cloudfile.view.IDuboxFileView
 * @author tianzengming
 * V 1.0.0
 * Create at 2013-12-2 下午8:38:41
 */
package com.moder.compass.ui.view;

import android.os.Bundle;
import android.os.Handler;

import com.dubox.drive.cloudfile.io.model.CloudFile;
import com.moder.compass.ui.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 文件系统的图片视图 com.dubox.drive.ui.view.IDuboxImageView
 * 
 * @author tianzengming <br/>
 *         create at 2013-12-18 下午3:04:37
 */
public interface IDuboxImageView extends IBaseView {

    /**
     * 返回当前选择的Item的列表
     * 
     * @return
     */
    public ArrayList<Integer> getSelectedItemsPosition();

    /**
     * 返回列表指定位置的Cursor
     * 
     * @param pos
     * @return
     */
    public CloudFile getItem(int pos);

    /**
     * 是否为视图模式
     * 
     * @return
     */
    public boolean isViewMode();

    /**
     * 取消编辑模式
     */
    public void cancelEditMode();

    /**
     * 删除成功回调
     */
    public void onDeleteSuccess(int operation);

    /**
     * 删除失败的回调
     */
    public void onDeleteFailed(int operation);

    /**
     * 重命名成功回调
     */
    public void onRenameSuccess(int operation);

    /**
     * 重命名成功回调
     * @param oldPath 旧文件path
     * @param newPath 修改后的path
     * @param newName 修改后的名字
     */
    void onRenameSuccess(String oldPath, String newPath, String newName);

    /**
     * 返回分享完成Handler
     */
    public Handler getHandler();

    /**
     * 
     * @return
     */
    public int getAdapterCount();

    /**
     * diff 操作完成回调
     * 
     * @param resultCode
     * @param resultData
     */
    public void onDiffFinished(int resultCode, Bundle resultData);

    /**
     * 列表刷新完成
     * 
     * @param complete
     */
    public void setRefreshComplete(boolean complete);

    /**
     * list操作完成回调
     */
    public void onGetDirectoryFinished();

    /**
     * 获取选择的聚类的数量
     * 
     * @return
     */
    public int getSelectedSectionCount();

    /**
     * 返回当前所在目录
     *
     * @return
     */
    public String getCurrentPath();

    /**
     * 文件移动完成
     */
    public void onMoveFinished(int operation);

    public void handleCannotMoveFiles(HashSet<Integer> canntMoveFiles);

    /**
     * 返回当前的筛选模式
     *
     * @return
     */
    public int getCurrentCategory();


    public EmptyView getEmptyView();

}
