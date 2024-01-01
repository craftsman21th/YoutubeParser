package com.moder.compass.containerimpl.bottombar;


import android.app.Activity;
import android.content.Context;

import com.dubox.drive.cloudfile.io.model.CloudFile;
import com.moder.compass.ui.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 操作项bar视图接口
 *
 * @author xujing31
 * @since 2019/5/5
 */
public interface IOptionBarView {

    /**
     * 显示更多弹窗
     */
    void showMore();

    /**
     * 获取选中的文件列表
     */
    List<CloudFile> getSelectedFiles();

    /**
     * 退出编辑模式
     */
    void cancelEditMode();

    /**
     * 获取activity
     */
    Activity getViewActivity();

    /**
     * 获取当前路径
     */
    String getCurrentPath();

    /**
     * 获取当前分类类别
     */
    int getCurrentCategory();

    /**
     * 删除成功
     */
    void onDeleteSuccess(int status);

    /**
     * 删除失败
     */
    void onDeleteFailed(int status);

    /**
     * 重命名成功
     */
    void onRenameSuccess(int status);

    /**
     * 重命名成功
     *
     * @param oldPath     旧路径
     * @param newFilePath 新路径
     * @param newFileName 新文件名
     */
    void onRenameSuccess(String oldPath, String newFilePath, String newFileName);

    /**
     * 处理无法移动的文件
     *
     * @param cannotMoveFiles     无法移动的文件
     */
    void handleCannotMoveFiles(HashSet<CloudFile> cannotMoveFiles);

    /**
     * 获取空界面
     */
    EmptyView getEmptyView();

    /**
     * 移动成功
     */
    void onMoveFinished(int status);

    /**
     * 展示处理中的对话框，比如正在删除,正在清除等
     * @param resId 对话框展示文案资源ID
     */
    void showProcessingDialog(int resId);

    /**
     * 隐藏loading对话框
     */
    void dismissProcessingDialog();

    /**
     * 是否选中所以文件
     */
    boolean isSelectAllFiles();

    /**
     * 获取上下文
     */
    Context getViewContext();

    /**
     * 收藏成功回调
     */
    void onCollectionSuccess(int operation);


    /**
     * 取消收藏成功回调
     */
    void cancelCollectionSuccess(int operation);

    /**
     * 获取选中文件id
     * @param cloudFileList
     * @return
     */
    ArrayList<String> getAudioItemListId(List<CloudFile> cloudFileList);

}
