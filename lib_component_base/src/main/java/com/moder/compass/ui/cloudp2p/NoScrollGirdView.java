/*
 * NoScrollGuidView.java
 * classes : com.dubox.drive.ui.cloudp2p.ui.NoScrollGuidView
 * @author 李继
 * V 1.0.0
 * Create at 2014年5月15日 下午3:36:17
 */
package com.moder.compass.ui.cloudp2p;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * com.dubox.drive.ui.cloudp2p.ui.NoScrollGuidView
 *
 * @author 李继 <br/>
 *         create at 2014年5月15日 下午3:36:17
 */
public class NoScrollGirdView extends GridView {
    private static final String TAG = "NoScrollGuidView";

    public NoScrollGirdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
