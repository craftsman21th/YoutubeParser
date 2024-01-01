package com.moder.compass.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moder.compass.ui.widget.BaseFragment
import com.moder.compass.viewmodel.BusinessViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainTabViewModel(application: Application) : BusinessViewModel(application) {

    private val _mainTabChangeLiveData: MutableLiveData<String> = MutableLiveData()
    private val _exploreSubTabChangeLiveData: MutableLiveData<String> = MutableLiveData()

    private val _mainTabEvent = MutableSharedFlow<MainTabEvent>()
    val mainTabEvent = _mainTabEvent.asSharedFlow()

    /**
     * 标签列表
     */
    fun getMainTabChangeLiveData():  LiveData<String> {
        return _mainTabChangeLiveData
    }
    /**
     * 标签列表
     */
    fun getExploreSubTabChangeLiveData():  LiveData<String> {
        return _exploreSubTabChangeLiveData
    }

    fun notifyTabChange(tab: String){
        _mainTabChangeLiveData.postValue(tab)
    }

    fun notifyExploreSubChange(tab: String){
        _exploreSubTabChangeLiveData.postValue(tab)
    }

    /**
     * 通知第一次返回
     */
    fun notifyFirstBack(currentFragment: com.moder.compass.ui.widget.BaseFragment?) {
        viewModelScope.launch {
            _mainTabEvent.emit(MainTabEvent.FirstBack(currentFragment))
        }
    }

    /**
     * 首页Tab UI事件
     */
    sealed class MainTabEvent {
        /**
         * 第一次点击返回键
         */
        class FirstBack(val currentFragment: com.moder.compass.ui.widget.BaseFragment?) : MainTabEvent()
    }
}