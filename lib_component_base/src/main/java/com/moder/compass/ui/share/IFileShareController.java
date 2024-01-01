package com.moder.compass.ui.share;

import android.content.Context;
import android.os.ResultReceiver;

import java.util.ArrayList;

/**
 * 文件分享对外方法
 *
 * @author lijunnian
 */
public interface IFileShareController extends OnBaseShareListener {
    /**
     * 分享文件
     *
     * @param shareTo 分享的类型：短信分享，邮件分享，复制外链，分享到其他
     * @param fromWhere 来自那里
     */
    void handleShareFile(int shareTo, int fromWhere);

    /**
     * 上报分享任务完成
     */
    void reportShareTaskStatus(Context context, boolean fromOther);

    void setNowShareFrom(int nowShareFrom);

    /**
     * 生成一个默认的公链外链，用于默认外链分享(主要用于赚钱)
     * @param context
     * @param resultReceiver
     * @param paths
     */
    void shareByLinkDefault(Context context, ResultReceiver resultReceiver, ArrayList<String> paths);
}
