package com.moder.compass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * 事务 ViewModel, 所有 ViewModel 的父类
 */
open class BusinessViewModel(application: Application) :
        AndroidViewModel(application)
