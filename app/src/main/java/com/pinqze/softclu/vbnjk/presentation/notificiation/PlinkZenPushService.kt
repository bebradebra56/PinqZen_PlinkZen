package com.pinqze.softclu.vbnjk.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pinqze.softclu.PlinkZenActivity
import com.pinqze.softclu.R
import com.pinqze.softclu.vbnjk.presentation.app.PlinkZenApplication

private const val PLINK_ZEN_CHANNEL_ID = "plink_zen_notifications"
private const val PLINK_ZEN_CHANNEL_NAME = "PlinkZen Notifications"
private const val PLINK_ZEN_NOT_TAG = "PlinkZen"

class PlinkZenPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                plinkZenShowNotification(it.title ?: PLINK_ZEN_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                plinkZenShowNotification(it.title ?: PLINK_ZEN_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            plinkZenHandleDataPayload(remoteMessage.data)
        }
    }

    private fun plinkZenShowNotification(title: String, message: String, data: String?) {
        val plinkZenNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PLINK_ZEN_CHANNEL_ID,
                PLINK_ZEN_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            plinkZenNotificationManager.createNotificationChannel(channel)
        }

        val plinkZenIntent = Intent(this, PlinkZenActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val plinkZenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            plinkZenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val plinkZenNotification = NotificationCompat.Builder(this, PLINK_ZEN_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.plink_zen_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(plinkZenPendingIntent)
            .build()

        plinkZenNotificationManager.notify(System.currentTimeMillis().toInt(), plinkZenNotification)
    }

    private fun plinkZenHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(PlinkZenApplication.PLINK_ZEN_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}