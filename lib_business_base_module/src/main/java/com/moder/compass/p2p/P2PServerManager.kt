package com.moder.compass.p2p

import android.text.TextUtils
import com.cocobox.library.P2P
import com.moder.compass.BaseApplication
import com.moder.compass.DynamicP2pSoManager
import com.moder.compass.DynamicSoLoadListener
import com.moder.compass.base.utils.GlobalConfigKey.KEY_P2P_SERVICE_FILE_PATH
import com.dubox.drive.kernel.architecture.config.GlobalConfig
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import java.io.File

/**
 * 下载模块的服务端管理类
 * 下载模块分为客户端和服务端 客户端提供给主进程使用 封装了下载的方法
 * 下载模块的服务端在p2p进程中 和客户端通过socket通信,是真正的下载实现类
 *
 * @author wangyang34
 * @since 2019年7月16日
 */
class P2PServerManager {

    /** 是否加载so文件  */
    private var mLibraryLoaded = false

    companion object {

        /** p2p kernel的so文件名 */
        const val P2P_KERNEL_FILE_NAME = "jni-kservice"
    }

    constructor() {
        if (DuboxLog.isDebug()) {
            P2P.getInstance().logOn()
        } else {
            P2P.getInstance().logOff()
        }
        DynamicP2pSoManager.getInstance(BaseApplication.getInstance()).tryLoad(
            pageTag = P2PServerManager::javaClass.name,
            listener = object : DynamicSoLoadListener {
                override fun onLoadSuccess() {
                    initP2P()
                }

                override fun onLoadError(error: Throwable) {
                    DuboxLog.d("qqqq", "P2PServerManager,onLoadError: ")
                }
            })
//        var ret : Boolean = initP2P()
    }

    /**
     * 初始化
     * @return 初始化结果
     */
    private fun initP2P(): Boolean {
        if (initP2PFromUpdate()) {
            P2P.getInstance().setFileCallbackImpl(P2PTarget30FileProcess())
            return true
        }
        if (initP2PFromAsset()) {
            P2P.getInstance().setFileCallbackImpl(P2PTarget30FileProcess())
            return true
        }
        return false
    }

    /**
     * 从更新文件里初始化p2p
     * @return 初始化结果
     */
    private fun initP2PFromUpdate(): Boolean {
        if (mLibraryLoaded) {
            return true
        }
        mLibraryLoaded = try {
            var path = GlobalConfig.getInstance().getString(KEY_P2P_SERVICE_FILE_PATH, "")
            if (!TextUtils.isEmpty(path) && File(path).exists()) {
                System.load(path)
                true
            } else {
                false
            }
        } catch (e: Throwable) {
            false
        }
        return mLibraryLoaded
    }

    /**
     * 从asset文件里初始化p2p
     * @return 初始化结果
     */
    private fun initP2PFromAsset(): Boolean {
        if (mLibraryLoaded) {
            return true
        }
        mLibraryLoaded = try {
            System.loadLibrary(P2P_KERNEL_FILE_NAME)
            true
        } catch (e: Throwable) {
            false
        }
        return mLibraryLoaded
    }
}
