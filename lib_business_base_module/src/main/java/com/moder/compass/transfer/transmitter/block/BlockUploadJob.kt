package com.moder.compass.transfer.transmitter.block

import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.mars.kotlin.extension.Tag
import java.util.concurrent.*

/**
 * 分片并发上传Job
 *
 * @author lijunnian
 */
@Tag("BlockUploadJob")
abstract class BlockUploadJob(val jobs: CopyOnWriteArrayList<BlockUploadJob>):
    Callable<BlockUploadJob>, Comparable<BlockUploadJob> {

    /**
     * 任务执行的future，用于取消任务的执行
     */
    @Volatile
    private var future: Future<*>? = null

    /**
     * 是否已取消
     */
    @Volatile
    private var isCancelled: Boolean = false

    /**
     * job的状态, 0表示排队或未添加到队列，1表示已执行
     */
    @Volatile
    private var state: Int = 0

    /**
     * 取消任务的执行
     */
    fun cancel() {
        isCancelled = true
        future?.cancel(true)
    }

    /**
     * 真正的执行代码，由子类实现
     *
     * @throws Exception ex
     */
    abstract fun performExecute()

    override fun call(): BlockUploadJob {
        state = 1
        if (!isCancelled){
            performExecute()
        } else {
            DuboxLog.d("BlockUploadJob", " >>>>> has cancel ed")
        }
        return this
    }

    /**
     * compare
     */
    override fun compareTo(other: BlockUploadJob): Int {
        val p1 = getPriority()
        val p2 = other.getPriority()
        val i =  when {
            p1 > p2 -> 1
            p1 < p2 -> -1
            else -> 0
        }
        return i
    }

    /**
     * 优先级： job在列表中的位置
     */
    private fun getPriority(): Int {
        var p = 0
        for (job in jobs) {
            if (job == this) {
                return p
            } else if(job.state == 0){
                p++
            }
        }
        return p
    }

    /**
     * 记录future，以便取消任务
     */
    fun setFuture(f: Future<*>) {
        if (this.future != null) {
            this.future = f
        }
    }
}