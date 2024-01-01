package com.moder.compass.ui.cloudfile;


/**
 * NetdiskFilePresenter 对其他组件接口
 *
 * @Author: guoqiqin
 * @CreateDate: 2019/09/19
 */
public interface IDuboxFilePresenter {
    void refresh(boolean fromPullDown);
    void sendPageListRequest(boolean isDiffFinished);
    void runDiff(boolean isNeedFrequencyCtrl);
    void onButtonDownload();
    void onButtonDelete();
    void onButtonMove(final int style);
    void onButtonRename();
    void onButtonShare(int fromWhere);
    void retryDeleteTmpSelectedFiles();
}
