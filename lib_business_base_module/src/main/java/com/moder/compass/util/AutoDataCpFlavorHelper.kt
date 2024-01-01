package com.moder.compass.util

import com.mars.kotlin.database.shard.ShardUri
import com.mars.kotlin.extension.d

/**
 * @author : guoliang08
 * Create time : 2023/11/23 15:17
 * Description : autoData ContentProvider flavor helper
 *
 * 为什么需要单独搞一个这个类?
 * autodata的生成的ContentProvider,它的authority与路径有关,
 * ContentProvider在每个flavor的路径不一样,导致import也不同,因不能导入未编译的类,故搞了一个这个兼容,方便后面扩展
 */

private const val CONTENT_PROVIDER = "ContentProvider"

/**
 * 获取AutoData自动生成的ContentProvider中的的ShardUri
 * @param path 自动生成的ContentProvider的路径,无需加末尾的.ContentProvider
 */
fun getContentProviderShardUri(path: String, uriFieldName: String): ShardUri? {
    return runCatching {
        val clazz = Class.forName("${path}.$CONTENT_PROVIDER")
        val uriField = clazz.getDeclaredField(uriFieldName)
        uriField.isAccessible = true
        "getContentProviderShardUri   ${uriField.get(null) as? ShardUri}".d("")
        uriField.get(null) as? ShardUri
    }.getOrNull()
}
