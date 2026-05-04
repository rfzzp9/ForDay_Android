package com.forday.app.core.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dayn.forday.R
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.presentation.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FordayFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userLocalDataSource: UserLocalDataSource

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM 토큰 갱신: $token")
        serviceScope.launch {
            userLocalDataSource.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("FCM 메시지 수신 - from: ${remoteMessage.from}")

        val data = remoteMessage.data
        if (data.isEmpty()) return

        val recordId = data["recordId"]?.toLongOrNull()
        val notificationId = data["notificationId"]?.toLongOrNull()
        val sendAt = data["sendAt"]

        Timber.d("FCM data - recordId: $recordId, notificationId: $notificationId, sendAt: $sendAt")

        val title = remoteMessage.notification?.title ?: getString(R.string.app_name)
        val body = remoteMessage.notification?.body ?: return

        showNotification(
            title = title,
            body = body,
            recordId = recordId,
            notificationId = notificationId
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        recordId: Long?,
        notificationId: Long?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = CHANNEL_ID
        val channel = NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            recordId?.let { putExtra(EXTRA_RECORD_ID, it) }
            notificationId?.let { putExtra(EXTRA_NOTIFICATION_ID, it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            recordId?.toInt() ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId?.toInt() ?: System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "forday_push_channel"
        const val CHANNEL_NAME = "ForDay 알림"
        const val EXTRA_RECORD_ID = "extra_record_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
