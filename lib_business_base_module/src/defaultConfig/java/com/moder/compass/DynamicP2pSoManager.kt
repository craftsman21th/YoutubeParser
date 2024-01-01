package com.moder.compass

import android.content.Context
import android.os.Build
import android.os.FileObserver
import com.moder.compass.business.kernel.HostURLManager
import com.dubox.drive.kernel.architecture.config.GlobalConfig
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.moder.compass.util.ExternalStorageUtil
import com.moder.compass.util.isMainProcess
import com.mars.united.core.util.unZipFile
import com.mars.united.international.dynamicso.DynamicSoLauncher
import com.mars.united.international.dynamicso.download.DownloadAction
import com.mars.united.international.dynamicso.download.DownloadProgressListener
import com.mars.united.international.dynamicso.download.Downloader
import com.moder.compass.statistics.DYNAMIC_SO_DOWNLOAD_ERROR
import com.moder.compass.statistics.DYNAMIC_SO_DOWNLOAD_MD5ERROR
import com.moder.compass.statistics.DYNAMIC_SO_LOAD_CHECK_ERROR
import com.moder.compass.statistics.DYNAMIC_SO_LOAD_SLOW_TAG
import com.moder.compass.statistics.clickEventTrace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.concurrent.CopyOnWriteArraySet


private const val remoteLibDownloadFile = "remoteP2pLibTemp.zip"
private const val REMOTE_LIB_SAVE_FILE = "remotelib_p2p_315"

private const val STATUS_LOADING = "1"
private const val STATUS_LOADED = "0"

private const val CACHE_SO_FILE = "dynamicsofile_p2p"
private const val KEY_SO_P2P_FILE_LENGTH = "key_dynamic_so_remote_lib_p2p_length"

class DynamicP2pSoManager private constructor(
    private val context: Context
) {

    private var lastStateType: String = ""
    private var lockFileWatching = false
    private val lockFileObserver: FileObserver by lazy {

        fun onEvent(fileObserver: FileObserver, event: Int, path: String?) {
            if (event == FileObserver.MODIFY) {
                val stateContent = loadingLockFile.readText()
                if (stateContent == STATUS_LOADED && stateContent != lastStateType) {
                    lastStateType = stateContent
                    fileObserver.stopWatching()
                    lockFileWatching = false
                    CoroutineScope(Dispatchers.Main).launch {
                        initDynamicSOFiles()
                        notifyLoadSuccess()
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            object : FileObserver(loadingLockFile) {
                override fun onEvent(event: Int, path: String?) {
                    onEvent(this, event, path)
                }
            }
        } else {
            object : FileObserver(loadingLockFile.absolutePath) {
                override fun onEvent(event: Int, path: String?) {
                    onEvent(this, event, path)
                }
            }
        }
    }
    private val downloader: DownloadAction by lazy {
        Downloader(
            URL_REMOTE_LIB,
            ExternalStorageUtil().getCacheDir().absolutePath,
            remoteLibDownloadFile,
            downloadListener
        )
    }
    private var soInstalled: Boolean = false
    private val upZipDestFile: File by lazy {
        File(
            ExternalStorageUtil().getCacheDir(),
            CACHE_SO_FILE
        ).also {
            if (!it.exists()) {
                it.mkdirs()
                it.setReadable(true)
                it.setWritable(true)
            }
        }
    }

    private val loadingLockFile: File by lazy {
        File(upZipDestFile, "loading_lock").also {
            kotlin.runCatching {
                if (!upZipDestFile.exists()) {
                    upZipDestFile.mkdirs()
                }
                if (!it.exists()) {
                    it.createNewFile()
                    it.setReadable(true)
                    it.setWritable(true)
                }
            }
        }
    }

    private val loadListeners = CopyOnWriteArraySet<DynamicSoLoadListener>()
    private val downloadListener: DownloadProgressListener by lazy {
        object : DownloadProgressListener {
            override fun onError(error: Throwable) {
                setLoadingState(false)
                DuboxLog.d(TAG, "onError: ${error.message}")
                notifyLoadError(error)
                clickEventTrace(
                    DYNAMIC_SO_DOWNLOAD_ERROR, TAG, error.message ?: "", ""
                ) {}
            }

            override fun onPause() {
                DuboxLog.d(TAG, "onPause: ")
            }

            override fun onProgress(progress: Int) {
                DuboxLog.d(TAG, "onProgress: $progress")
            }

            override fun onResume() {
                DuboxLog.d(TAG, "onResume: ")
            }

            override fun onSuccess(file: File, fileMd5: String) {
                DuboxLog.d(TAG, "onSuccess: ${file.absolutePath} and fileMd5:$fileMd5")
                if (fileMd5 == MD5_REMOTE_LIB) {
                    if (upZipDestFile.exists()) {
                        DuboxLog.d(TAG, "onSuccess: upZipDestFile.exists()")
                        unZipFile(file, upZipDestFile)
                    }
//                logFile(upZipDestFile)
                    initDynamicSOFiles()
                    calculateMD5(File(upZipDestFile, REMOTE_LIB_SAVE_FILE)).let {
                        GlobalConfig.getInstance().putString(KEY_SO_P2P_FILE_LENGTH, it)
                    }
                    setLoadingState(false)
                    notifyLoadSuccess()
                } else {
                    setLoadingState(false)
                    notifyLoadError(Throwable("md5 check fail"))
                    file.delete()
                    clickEventTrace(
                        DYNAMIC_SO_DOWNLOAD_MD5ERROR, TAG, "", ""
                    ) {}
                }
            }
        }
    }
    private val isMainProcess by lazy {
        isMainProcess(BaseApplication.getContext())
    }

    private fun initDynamicSOFiles() {
        soInstalled = DynamicSoLauncher.initDynamicSoConfig(
            context = context, soPath = File(upZipDestFile, REMOTE_LIB_SAVE_FILE).absolutePath
        )
    }

    private fun notifyLoadSuccess() {
        loadListeners.forEach {
            it.onLoadSuccess()
        }
        loadListeners.clear()
    }

    private fun notifyLoadError(error: Throwable) {
        loadListeners.forEach {
            it.onLoadError(error)
        }
        loadListeners.clear()
    }

    fun addLoadListener(listener: DynamicSoLoadListener) {
        if (!loadListeners.contains(listener)) {
            loadListeners.add(listener)
        }
    }

    fun removeLoadListener(listener: DynamicSoLoadListener) {
        if (loadListeners.contains(listener)) {
            loadListeners.remove(listener)
        }
    }

    /**
     * 从远端加载动态库文件，如果已安装则直接回调成功，否则从远端下载并安装。
     *
     * @param pageTag 可选参数，用于标识当前调用此方法的页面标签，默认为空字符串
     * @param listener 可选参数，用于监听加载过程中的事件，默认值为`null`，即不监听加载过程中的事件
     */
    fun tryLoad(pageTag: String = "", listener: DynamicSoLoadListener? = null) {
        if (soInstalled) {
            listener?.onLoadSuccess()
            return
        }
        // 如果是主进程&&正在loading&&是第一次tryLoad(既listener=null) ->
        // 那么应该是下载过程中,进程被杀了,继续下载
        val loading = isLoading()
        val needReDownload = (isMainProcess && loading && listener == null)
        DuboxLog.d(
            TAG, "tryLoad isMainProcess=$isMainProcess loading=$loading " +
                    " listener=$listener  needReDownload=$needReDownload"
        )

        if (!loading || needReDownload) {
            val md5 = GlobalConfig.getInstance().getString(KEY_SO_P2P_FILE_LENGTH)
            DuboxLog.d(TAG, "tryLoad: md5: $md5 and soPath: ${upZipDestFile.absolutePath}")
            val soFile = File(upZipDestFile, REMOTE_LIB_SAVE_FILE)
//            logFile(soFile)
            if (checkRemoteFile(md5)) {
                initDynamicSOFiles()
                DuboxLog.d(TAG, "tryLoad: checkRemoteFile pass success and init complete")
                listener?.onLoadSuccess()
            } else {
                if (soFile.exists()) {
                    soFile.deleteRecursively()
                }
                clickEventTrace(
                    DYNAMIC_SO_LOAD_CHECK_ERROR, TAG, "", pageTag
                ) {}
                DuboxLog.d(TAG, "tryLoad: checkRemoteFile pass fail and start download")
                listener?.let {
                    addLoadListener(it)
                }
                setLoadingState(true)
                downloader.download()
            }
        } else {
            clickEventTrace(
                DYNAMIC_SO_LOAD_SLOW_TAG, TAG, "", pageTag
            ) {}
            listener?.let {
                addLoadListener(it)
            }
            observeLoadingState()
        }
    }


    private fun checkRemoteFile(md5: String?): Boolean {
        return if (md5.isNullOrEmpty()) {
            false
        } else {
            upZipDestFile.exists() && upZipDestFile.length() > 0 && md5 == calculateMD5(
                File(
                    upZipDestFile.absolutePath, REMOTE_LIB_SAVE_FILE
                )
            )
        }
    }

    private fun setLoadingState(loadingState: Boolean) {
        kotlin.runCatching {
            if (!loadingLockFile.exists()) {
                val res = loadingLockFile.createNewFile()
                DuboxLog.d(
                    TAG,
                    "setLoadingState: upZipDestFile.exists():${upZipDestFile.exists()} and createNewFile:$res"
                )
            }
            if (loadingState) {
                loadingLockFile.writeText(STATUS_LOADING)
            } else {
                loadingLockFile.writeText(STATUS_LOADED)
            }
        }
    }

    private fun isLoading(): Boolean {
        return kotlin.runCatching {
            if (!loadingLockFile.exists()) {
                val res = loadingLockFile.createNewFile()
                DuboxLog.d(
                    TAG,
                    "isLoading: upZipDestFile.exists():${upZipDestFile.exists()} and createNewFile:$res"
                )
                return false
            }
            return loadingLockFile.readText() == STATUS_LOADING
        }.onFailure {
            DuboxLog.d(TAG, "isLoading error $it")
        }.getOrDefault(false)
    }

    /**
     * 监视加载状态
     */
    private fun observeLoadingState() {
        if (lockFileWatching) {
            return
        }
        kotlin.runCatching {
            if (!loadingLockFile.exists()) {
                val res = loadingLockFile.createNewFile()
            }
            lockFileObserver.startWatching()
            lockFileWatching = true
            DuboxLog.d(
                TAG, "observeLoadingState:start watching"
            )
        }
    }


    fun hasLoaded(): Boolean {
        return soInstalled && checkRemoteFile(
            GlobalConfig.getInstance().getString(KEY_SO_P2P_FILE_LENGTH)
        )
    }

    private fun calculateMD5(file: File): String {
//        logFile(file)
        return if (file.exists()) {
            if (!file.isDirectory) {
                calculateSingleFile(file)
            } else {
                calculateFolderFile(file)
            }
        } else {
            ""
        }
    }

    private fun logFile(file: File) {
        DuboxLog.d(TAG, "logFile: ${file.absolutePath} and ${file.exists()}")
        if (file.exists()) {
            if (file.isFile) {
                DuboxLog.d(TAG, "logFile isFile: ${file.absolutePath}")
            } else {
                DuboxLog.d(TAG, "logFile isDirectory: ${file.absolutePath}")
                file.listFiles()?.forEach {
                    logFile(it)
                }
            }
        } else {
            DuboxLog.d(TAG, "logFile: file not exist ${file.absolutePath}")
        }
    }

    private fun calculateSingleFile(file: File): String {
        val md5Digest = MessageDigest.getInstance("MD5")
        val inputStream = FileInputStream(file)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            md5Digest.update(buffer, 0, bytesRead)
        }
        inputStream.close()
        val md5Bytes = md5Digest.digest()
        val sb = StringBuilder()
        for (byte in md5Bytes) {
            sb.append(String.format("%02x", byte))
        }
        DuboxLog.d(TAG, "calculateSingleFile: path :${file.absolutePath} and md5: ${sb.toString()}")
        return sb.toString()
    }

    private fun calculateFolderFile(folder: File): String {
        val sb = StringBuilder()
        folder.listFiles()?.let { files ->
            for (file in files) {
                if (file.isFile) {
                    sb.append(calculateSingleFile(file))
                } else {
                    sb.append(calculateFolderFile(file))
                }
            }
        }
        DuboxLog.d(TAG, "calculateFolderFile: ${sb.toString()}")
        return sb.toString()
    }

    fun destroy() {
        setLoadingState(false)
        loadListeners.clear()
    }

    companion object {
        private const val TAG = "dynamic_so_p2p"
        private var instance: DynamicP2pSoManager? = null

        // 每次更新zip文件时
        // 1. 需要将so文件放到一个文件夹中,这个文件夹的名字类似remotelib_player_314
        // 2. 然后再把这个文件夹压缩成zip,314是版本号
        // 3. 上传zip到cdn服务器,更新md5  MD5_REMOTE_LIB和remoteLibSaveFile 2个属性值
        private val URL_REMOTE_LIB =
            "${HostURLManager.getDefaultRemoteResourcePrefix()}/moder/cdn_dynamic_resource/remotelib_p2p_315.zip"
        private const val MD5_REMOTE_LIB = "8c47352c69492a74a7422971d84f986d"
        fun getInstance(ctx: Context): DynamicP2pSoManager {
            return instance ?: synchronized(this) {
                instance ?: DynamicP2pSoManager(ctx.applicationContext).also { instance = it }
            }
        }
    }

}