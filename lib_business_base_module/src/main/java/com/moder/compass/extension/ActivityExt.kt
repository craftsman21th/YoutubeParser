package com.moder.compass.extension

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.moder.compass.BaseApplication
import com.moder.compass.viewmodel.BusinessViewModel
import com.moder.compass.viewmodel.BusinessViewModelFactory

/**
 * kotlin调法
 */
inline fun <reified T : BusinessViewModel> FragmentActivity.getViewModel(): T {
    val curApplication = application
    if (curApplication is BaseApplication) {
        return ViewModelProvider(this, BusinessViewModelFactory.getInstance(curApplication)).get(T::class.java)
    } else {
        throw IllegalStateException("curApplication($curApplication) is not BaseApplication")
    }
}

/**
 * kotlin调法
 */
inline fun <reified T : BusinessViewModel> FragmentActivity.getViewModel(key: String): T {
    val curApplication = application
    if (curApplication is BaseApplication) {
        return ViewModelProvider(this, BusinessViewModelFactory.getInstance(curApplication)).get(key, T::class.java)
    } else {
        throw IllegalStateException("curApplication($curApplication) is not BaseApplication")
    }
}

/**
 * java调法
 */
fun <T : BusinessViewModel> FragmentActivity.getViewModel(viewModelClass: Class<T>): T {
    val curApplication = application
    if (curApplication is BaseApplication) {
        return ViewModelProvider(this, BusinessViewModelFactory.getInstance(curApplication)).get(viewModelClass)
    } else {
        throw IllegalStateException("curApplication($curApplication) is not BaseApplication")
    }
}

/**
 *
 */
inline fun <reified T : Activity> View.getActivity(): T? {
    var context = context
    while (context is ContextWrapper) {
        if (context is T) {
            return context
        }
        context = context.baseContext
    }
    return null
}