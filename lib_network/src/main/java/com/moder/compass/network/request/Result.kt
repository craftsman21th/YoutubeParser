package com.moder.compass.network.request

import android.os.Bundle
import android.os.ResultReceiver
import androidx.lifecycle.LiveData
import com.moder.compass.network.request.Extras.RESULT
import com.mars.kotlin.service.LiveResultReceiver
import com.mars.kotlin.service.Result

/**
 * 构造一个livedata receiver
 */
inline fun <reified T> getSimpleResultLiveData(resultReceiver: (ResultReceiver) -> Unit): LiveData<Result<T?>> {
//    val receiver = object : LiveResultReceiver<T?>() {
//        override fun getData(resultData: Bundle?): T? {
//            return resultData?.get(RESULT) as? T
//        }
//    }
    val receiver = SimpleResultReceiver<T?>()
    resultReceiver(receiver)
    return receiver.asLiveData()
}

/**
 * LiveResultReceiver实现类
 */
class SimpleResultReceiver<T> : LiveResultReceiver<T?>() {

    override fun getData(resultData: Bundle?): T? {
        return resultData?.get(RESULT) as? T
    }

}