package com.moder.compass.transfer.transmitter.block

import android.content.Context
import com.moder.compass.BaseApplication
import com.dubox.drive.kernel.android.util.deviceinfo.Temperature
import com.mars.kotlin.extension.Tag
import com.mars.kotlin.extension.d
import com.mars.united.threadscheduler.android.AndroidThreadFactory
import java.lang.Exception
import java.util.concurrent.*
import kotlin.math.min

/**
 * 分片并发上传-分片大小
 */
internal const val UPLOAD_BLOCK_SIZE: Int = (1024 * 1024 * 4)

/**
 * cpu温度阈值
 */
const val CPU_TEMPERATURE_STOP_THRESHOLD = 59

/**
 * 并发
 */
private const val DEFAULT_MAX_REMAIN_MEMORY: Long = 150 * 1024 * 1024

/**
 * thread pool keepAliveTime
 */
private const val POOL_KEEP_ALIVE_TIME: Long = 60L


/**
 * 分片并发上传调度器
 *
 * @author lijunnian
 */
@Tag("BlockUploadScheduler")
class BlockUploadScheduler {

    /**
     * 分片并发上传预留固定内存150MB
     */
    private val remainBytes = min(getRemainedMemory() / 2, DEFAULT_MAX_REMAIN_MEMORY)

    private val threadPoolExecutor: ThreadPoolExecutor by lazy {
        val pool = object : ThreadPoolExecutor(
            ConfigBlockUpload.defaultPoolSize,
            Int.MAX_VALUE - 1,
            POOL_KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            PriorityBlockingQueue(),
            MyThreadFactory("blockUpload"),
            { _, executor -> "rejectedExecution e = $executor".d() }
        )  {
            override fun <T> newTaskFor(callable: Callable<T>): RunnableFuture<T> {
                if (callable is BlockUploadJob) {
                    return ComparableFuture(callable) as RunnableFuture<T>
                }
                return super.newTaskFor(callable)
            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                super.afterExecute(r, t)
                rePoolSize()
            }
        }
        pool.allowCoreThreadTimeOut(true)
        pool
    }


    /**
     * 获取分片并发上传的线程池
     */
    companion object {
        @Volatile
        private var instance: BlockUploadScheduler? = null

        /**
         * get instance
         */
        fun getInstance(): BlockUploadScheduler {
            return instance ?: synchronized(this) {
                instance ?: BlockUploadScheduler().also {
                    instance = it
                }
            }
        }
    }

    /**
     * 批量提交分片上传任务
     */
    fun summitAll(jobs: Collection<BlockUploadJob>) : List<Future<BlockUploadJob>> {
        val futures: ArrayList<Future<BlockUploadJob>> = ArrayList(jobs.size)
        if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown) {
            for (job in jobs) {
                futures.add(threadPoolExecutor.submit(job))
            }
        }
        return futures
    }

    /**
     * 等待一批任务结束
     */
    fun awaitAll(futures: List<Future<BlockUploadJob>>) {
        for (f in futures) {
            if (!f.isDone) {
                try {
                    f.get()
                } catch (ignore: CancellationException) {
                } catch (ignore: ExecutionException) {
                }
            }
        }
    }

    /**
     * 批量提交分片上传任务, 并等待任务结束
     */
    @SuppressWarnings("ComplexMethod", "NestedBlockDepth")
    fun summitAllAndWait(jobs: Collection<BlockUploadJob>) {
        if (threadPoolExecutor.isShutdown) {
            return
        }
        val futures: ArrayList<Future<BlockUploadJob>> = ArrayList(jobs.size)
        try {
            for (job in jobs) {
                val f = threadPoolExecutor.submit(job)
                job.setFuture(f)
                futures.add(f)
            }
            for (f in futures) {
                if (!f.isDone) {
                    try {
                        f.get()
                    } catch (ignore: CancellationException) {
                    } catch (ignore: ExecutionException) {
                    }
                }
            }
        } catch (e: Exception) {
            for (f in futures) {
                f.cancel(true)
            }
        }
    }

    /**
     * 取消一批任务
     */
    fun cancelAll(jobs: Collection<BlockUploadJob>) {
        for (j in jobs) {
            j.cancel()
        }
    }

    /**
     * 设置分片并发上传最大并发任务数 设置分片并发上传最大并发任务数 （受CPU温度和内存双重限制）（受CPU温度和内存双重限制）
     */
    private fun rePoolSize() {
        val maxPoolSize: Int = ConfigBlockUpload.maxPoolSize
        val minPoolSize: Int = ConfigBlockUpload.minPoolSize
        val currentPoolSize = threadPoolExecutor.corePoolSize
        // 并发任务数目递增后数目
        val increaseToSize = (currentPoolSize + 1).coerceAtMost(maxPoolSize)
        // 并发任务数目递减后数目
        val decreaseToSize = (currentPoolSize - 1).coerceAtLeast(minPoolSize)
        val poolSize = if (isCpuTemperatureHigh(BaseApplication.getContext())) {
            "温度太烫，调整后的最大并发任务数目 decreaseToSize=$decreaseToSize".d()
            decreaseToSize
        } else {
            if (!isInLowMemory(increaseToSize)) {
                "Upload-Module 【分片并发上传】内存充足状态，调整后的最大并发任务数目 increaseToSize=$increaseToSize".d()
                increaseToSize
            } else {
                "Upload-Module 【分片并发上传】低内存状态，调整后的最大并发任务数目 decreaseToSize=$decreaseToSize".d()
                decreaseToSize
            }
        }
        "currentPoolSize = ${threadPoolExecutor.corePoolSize}, 推荐PoolSize = $poolSize".d()
        if (poolSize == threadPoolExecutor.corePoolSize) {
            return
        }
        threadPoolExecutor.corePoolSize = poolSize
    }

    /**
     * 获取当前最大设置并发任务数目
     */
    fun getCurrentPoolSize(): Int {
        return threadPoolExecutor.corePoolSize
    }

    /**
     * 获取当前运行的线程数
     */
    fun getCurrentRunningThreads(): Int {
        return threadPoolExecutor.activeCount
    }

    /**
     * 手机cpu温度是否太烫
     */
    private fun isCpuTemperatureHigh(context: Context): Boolean {
        val currentTemperature = Temperature(context).cpu() ?: 0.toDouble()
        val cpuTemperatureStopThreshold = CPU_TEMPERATURE_STOP_THRESHOLD
        "Upload-Module 【分片并发上传】当前cpu温度=$currentTemperature cpu温度阀值=$cpuTemperatureStopThreshold".d()
        return currentTemperature > cpuTemperatureStopThreshold
    }

    /**
     * 手机剩余内存是否无法支撑现有的最大分片并发任务数
     *
     * @param blockThreads 分片并发线程数目
     */
    private fun isInLowMemory(blockThreads : Int?): Boolean {
        if (blockThreads == null) {
            return false
        }
        return blockThreads * UPLOAD_BLOCK_SIZE > getRemainedMemory() - remainBytes
    }

    /**
     * 获取已使用的内存
     */
    private fun getUsedMemory(): Long {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }

    /**
     * 获取剩余内存，超出就会发生OOM
     */
    private fun getRemainedMemory(): Long {
        return Runtime.getRuntime().maxMemory() - getUsedMemory()
    }
}

/**
 * 线程池
 */
class MyThreadFactory(threadPrefixName: String? = null): ThreadFactory {
    private val factory = if (threadPrefixName.isNullOrEmpty()) {
        AndroidThreadFactory()
    } else {
        AndroidThreadFactory(threadPrefixName)
    }

    override fun newThread(runnable: Runnable): Thread {
        return factory.newThread(runnable)
    }
}

/**
 * 可比较的FutureTask
 */
class ComparableFuture(private val job: BlockUploadJob):
    FutureTask<BlockUploadJob>(job), Comparable<ComparableFuture> {
    override fun compareTo(other: ComparableFuture): Int {
        return job.compareTo(other.job)
    }
}