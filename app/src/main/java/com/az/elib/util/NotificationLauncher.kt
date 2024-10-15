package com.az.elib.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.az.elib.R
import com.az.elib.presentation.ui.activities.HomeActivity
import javax.inject.Inject

class NotificationLauncher @Inject constructor(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "elib-6128f"
    private val channelName = "EstinLib Notifications"

    fun startNotification(
        notificationTitle: String,
        notificationBody: String,
        postId:String
    ) {
        val id = System.currentTimeMillis().toInt()
        val notificationBuilder = NotificationCompat.Builder(context, this.channelId)
            .setContentTitle("New Post")
            .setContentText(notificationTitle)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(getPendingIntent(postId))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notificationBody))
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(id, notificationBuilder.build())
    }

    private fun getPendingIntent(postId: String): PendingIntent {
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra(Constants.POST_ID_FROM_NOTIFICATION, postId)
        val requestCode = System.currentTimeMillis().toInt()
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}