package com.moder.compass.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * @author sunmeng12
 * @since Terabox 2022/5/8
 *
 */

fun sendNotification(
    context: Context, smallIcon: Int, title: String, content: String, pendingIntent: PendingIntent
): Notification {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification: Notification = NotificationCompat.Builder(context, context.packageName)
        .setSmallIcon(smallIcon)
        .setContentTitle(title)
        .setContentText(content)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                context.packageName,
                NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    notificationManager.notify(NOTIFICATION_ID, notification)
    return notification
}

/**
 * 移除 widget 刷新通知
 */
fun removeNotification(context: Context) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    kotlin.runCatching {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}

private const val NOTIFICATION_CHANNEL: String = "DuboxNotification"
const val NOTIFICATION_ID: Int = 0

