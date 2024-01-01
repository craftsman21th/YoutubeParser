package com.moder.compass.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moder.compass.BaseApplication
import com.mars.united.core.debug.throwWhenLog

/**
 * 工厂类
 */
class BusinessViewModelFactory private constructor(private val application: BaseApplication) :
        ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (BusinessViewModel::class.java.isAssignableFrom(modelClass)) {
            return try {
                modelClass.getConstructor(Application::class.java)
                        .newInstance(application)
            } catch (e: Exception) {
                e.throwWhenLog()
                return super.create(modelClass)
            }
        }
        return super.create(modelClass)
    }

    companion object {
        @Volatile
        private var instance: BusinessViewModelFactory? = null

        /**
         *
         */
        fun getInstance(application: BaseApplication = BaseApplication.getInstance())
                : BusinessViewModelFactory {
            return instance
                    ?: synchronized(this) {
                instance
                        ?: BusinessViewModelFactory(application).also { instance = it }
            }
        }
     }
}