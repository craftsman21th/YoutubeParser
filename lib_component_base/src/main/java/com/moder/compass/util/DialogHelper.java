package com.moder.compass.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.dubox.drive.cloudfile.utils.FileType;
import com.moder.compass.base.utils.PersonalConfigKey;
import com.dubox.drive.cloudfile.io.model.CloudFile;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.ui.manager.BaseDialogBuilder;

import java.util.List;

/**
 * dialog弹框工具类
 * Created by baidu on 18/5/9.
 */

public final class DialogHelper {
    public static final String TAG = "DialogHelper";

    /**
     * 是否满足弹出提示条件
     * @param activity
     */
    public static void livePhotoDwonloadPrompt(Activity activity) {
        // live photo文件首次下载提示
        if (PersonalConfig.getInstance().getBoolean(PersonalConfigKey.KEY_LIVE_PHOTO_DOWNLOAD_FIRST_PROMPT, true)) {
            PersonalConfig.getInstance().putBoolean(PersonalConfigKey.KEY_LIVE_PHOTO_DOWNLOAD_FIRST_PROMPT, false);
            PersonalConfig.getInstance().asyncCommit();
            showLivePhotoDownloadPromptDialog(activity);

        }
    }
    /**
     * 下载提示
     */
    private static void showLivePhotoDownloadPromptDialog(Activity activity) {
        BaseDialogBuilder builder = new BaseDialogBuilder();
        builder.buildOneButtonDialog(activity, "",
                activity.getString(R.string.live_photo_download_prompt),
                activity.getString(R.string.button_iknow));

    }

    /**
     * live photo 首次下载提示和统计
     * @param activity
     * @param cloudFiles
     * @param photoPreview
     */
    public static void livePhotoDwonloadPromptAndStatistics(Activity activity,
                                                            List<CloudFile> cloudFiles, boolean photoPreview) {
        boolean hasLivePhoto = false;
        if (cloudFiles == null || cloudFiles.size() == 0) {
            return;
        }
        for (int i = 0; i < cloudFiles.size(); i ++) {
            if (FileType.isLivp(cloudFiles.get(i).getFileName())) {
                hasLivePhoto = true;
                break;
            }
        }
        if (hasLivePhoto) {
            DialogHelper.livePhotoDwonloadPrompt(activity);
            if (photoPreview) {
                StatisticsLogForMutilFields.getInstance()
                        .updateCount(StatisticsLogForMutilFields
                                .StatisticsKeys.LIVE_PHOTO_PREVIEW_DOWNLOAD_COUNT);
            } else {
                StatisticsLogForMutilFields.getInstance()
                        .updateCount(StatisticsLogForMutilFields.StatisticsKeys.LIVE_PHOTO_OTHER_DOWNLOAD_COUNT);
            }


        }
    }

    /**
     * live photo 首次下载提示和统计
     * @param activity
     * @param fileName
     * @param photoPreview
     */
    public static void livePhotoDwonloadPromptAndStatistics(final Activity activity,
                                                            final String fileName, final boolean photoPreview) {
        DuboxLog.d(TAG, "fileName:" + fileName);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            livePhotoImplement(activity, fileName, photoPreview);
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                livePhotoImplement(activity, fileName, photoPreview);
            }
        });
    }

    /**
     * live photo 首次下载提示和统计的实现
     * @param activity
     * @param fileName
     * @param photoPreview
     */
    private static void livePhotoImplement(Activity activity, String fileName, boolean photoPreview) {
        if (FileType.isLivp(fileName)) {
            DuboxLog.d(TAG, "photoPreview:" + photoPreview);
            DialogHelper.livePhotoDwonloadPrompt(activity);
            if (photoPreview) {
                StatisticsLogForMutilFields.getInstance()
                        .updateCount(StatisticsLogForMutilFields
                                .StatisticsKeys.LIVE_PHOTO_PREVIEW_DOWNLOAD_COUNT);
            } else {
                StatisticsLogForMutilFields.getInstance()
                        .updateCount(StatisticsLogForMutilFields.StatisticsKeys.LIVE_PHOTO_OTHER_DOWNLOAD_COUNT);
            }
        }
    }

}
