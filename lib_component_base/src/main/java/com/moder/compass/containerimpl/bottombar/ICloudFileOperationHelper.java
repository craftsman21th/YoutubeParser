package com.moder.compass.containerimpl.bottombar;

import android.content.Intent;

/**
 * CloudFileOperationHelper对外组件接口
 *
 * @Author: guoqiqin
 * @CreateDate: 2019/10/08
 */
public interface ICloudFileOperationHelper {
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void dealMove(final int style);
}
