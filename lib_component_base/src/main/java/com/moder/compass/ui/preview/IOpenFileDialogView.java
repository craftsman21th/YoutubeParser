
package com.moder.compass.ui.preview;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.ui.view.IBaseView;
import com.moder.compass.ui.view.IBaseView;

import java.util.List;

/**
 * Created by liuliangping on 2015/5/22.
 */
public interface IOpenFileDialogView extends IBaseView {

    /**
     * 下载文件
     */
    void onDownloadFile();

    /**
     * 直接打开本地文件
     * 
     * @param localFile 文件文件的路径
     * @param taskId
     */
    void onOpenFile(RFile localFile, int taskId);

    /**
     * 发取消注册完成的广播
     */
    void onUnRegisterCancelPreviewFinishBroadcast();

    /**
     * 取消预览任务
     */
    void onCancelPreview();

    /**
     * 取消预览任务
     */
    void onCancelPreview(List<Integer> list);

    void onDownloadProcess();
}
