package com.moder.compass.ui.localfile.upload;

import androidx.annotation.IdRes;

/**
 * BucketBottomFragment对外组件接口
 *
 * @Author: guoqiqin
 * @CreateDate: 2019/10/09
 */
public interface IBucketBottomFragment {
    void setOnBottomClickListener(IOnBottomClickListener listener);
    void setBottomBtnText(int num, @IdRes int selectShow, @IdRes int defaltShow);
}
