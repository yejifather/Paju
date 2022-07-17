package com.tenkitchen.paju

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val TAG = "로그"

    /**
     * FirebaseInstanceIdService is deprecated.
     * this is new on firebase-messaging:17.1.0
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")
    }

    /**
     * this method will be triggered every time there is new FCM Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived called")
        Log.d(TAG, "From: " + remoteMessage.from)

        if(remoteMessage.notification != null) {
            Log.d(TAG, "Notification Message Body: ${remoteMessage.notification?.body}")
            sendNotification(remoteMessage.notification?.body)

            // 화면 깨우기
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            @SuppressLint("InvalidWakeLockTag") val wakeLock =
                pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                    or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"
                )
            wakeLock.acquire(3000)
            wakeLock.release()
        }
    }

    private fun sendNotification(body: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", body)
        }
        .0

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this,"Notification")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Push Notification FCM")
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

}