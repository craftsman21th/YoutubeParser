package com.moder.compass

import android.os.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.moder.compass.account.model.CustomResult
import com.moder.compass.util.receiver.BaseResultReceiver
import com.moder.compass.util.receiver.ErrorType
import com.moder.compass.util.receiver.ResultView
/**
 * asNormalResultReceiver
 * */
fun MutableLiveData<CustomResult<Bundle?>>.asNormalResultReceiver(handler: Handler = Handler(Looper.getMainLooper()),
                                                                  resultView: ResultView? = null,
                                                                  onFinish: ((Boolean, CustomResult<Bundle?>) -> Unit)? = null): ResultReceiver {
    return object : BaseResultReceiver<MutableLiveData<CustomResult<Bundle?>>>(this, handler, resultView) {
        override fun onSuccess(reference: MutableLiveData<CustomResult<Bundle?>>, resultData: Bundle?) {
            super.onSuccess(reference, resultData)
            val result = CustomResult.success(resultData)
            this@asNormalResultReceiver.postValue(result)
            onFinish?.invoke(true, result)
        }

        override fun onFailed(reference: MutableLiveData<CustomResult<Bundle?>>, errType: ErrorType, errno: Int, resultData: Bundle):
                Boolean {
            val result = CustomResult.failure<Bundle?>(errType, errno, -1, resultData)
            this@asNormalResultReceiver.postValue(result)
            onFinish?.invoke(false, result)
            return super.onFailed(reference, errType, errno, resultData)
        }
    }
}

/**
 * 将可变LiveData转为不可变，供ViewModel外部观测使用
 */
fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> {
    return this
}
