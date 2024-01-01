
package com.moder.compass.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.moder.compass.BaseApplication
import com.moder.compass.viewmodel.BusinessViewModel
import com.moder.compass.viewmodel.BusinessViewModelFactory

/**
 * kotlin调法
 */
inline fun <reified T : BusinessViewModel> Fragment.getViewModel(): T {
    val curApplication = activity?.application
    if (curApplication is BaseApplication) {
        return ViewModelProvider(this, BusinessViewModelFactory.getInstance(curApplication)).get(T::class.java)
    }
    throw IllegalStateException("curApplication($curApplication) is not BaseApplication")
}

/**
 * kotlin调法
 */
inline fun <reified T : BusinessViewModel> Fragment.getViewModel(key: String): T {
    val curApplication = activity?.application
    if (curApplication is BaseApplication) {
        return ViewModelProvider(this, BusinessViewModelFactory.getInstance(curApplication))[key, T::class.java]
    }
    throw IllegalStateException("curApplication($curApplication) is not BaseApplication")
}

/**
 * java调法
 */
fun <T : BusinessViewModel> Fragment.getViewModel(viewModelClass: Class<T>): T {
    val curApplication = activity?.application ?: BaseApplication.getInstance()
    if (curApplication is BaseApplication) {
        return ViewModelProvider(this, BusinessViewModelFactory.getInstance(curApplication)).get(viewModelClass)
    }
    throw IllegalStateException("curApplication($curApplication) is not BaseApplication")
}

/**
 * 共享viewmodel  java调法
 */
fun <T : BusinessViewModel> Fragment.getActivityViewModel(viewModelClass: Class<T>): T? {
    val currentActivity = activity
    return if (currentActivity == null) {
        null
    } else {
        ViewModelProviders.of(currentActivity).get(viewModelClass)
    }
}


/**
 * 共享viewmodel  java调法
 */
inline fun <reified T : BusinessViewModel> Fragment.getActivityViewModel(key: String): T? {
    val curApplication = activity?.application
    if (curApplication is BaseApplication) {
        ViewModelProvider(this, BusinessViewModelFactory.getInstance(curApplication))[key, T::class.java]
    }
    return null
}
