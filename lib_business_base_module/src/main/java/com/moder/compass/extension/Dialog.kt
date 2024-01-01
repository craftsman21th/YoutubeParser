package com.moder.compass.extension

import android.app.Dialog

/**
 * @author chencaixing
 * @time 2019-10-25 15:58
 */
fun Dialog?.safeDismiss(){
    if (this != null && isShowing) dismiss()
}