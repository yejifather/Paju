package com.tenkitchen.paju

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tenkitchen.objects.PushContents


class MyFirebaseMessagingService:FirebaseMessagingService() {
    private val TAG = "로그"

    //private var notificationManager: NotificationManager? = null

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

        if (remoteMessage.data != null) {
            Log.d(TAG, "data is not null")
            Log.d(TAG, remoteMessage.data.toString())

            Log.d(TAG, remoteMessage.data["title"].toString())

            val remoteMessageData = mapOf(
                PushContents.title to remoteMessage.data["title"].toString(),
                PushContents.body to remoteMessage.data["body"].toString()!!
            )

            // 화면 깨우기
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            @SuppressLint("InvalidWakeLockTag") val wakeLock =
                pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"
                )
            wakeLock.acquire(3000)
            wakeLock.release()

            sendNotification(remoteMessageData)
            Log.d("FCM", "Message Notification Body: " + remoteMessage.notification?.body)
        } else {
            Log.d(TAG, "data is null")
        }
    }

    private fun displayNotification() {
        /*val notificationId = 66

        val notification = Notification.Builder(applicationContext, "TEST")
            //.setSmallIcon(R.drawable.icon_example)
            .setContentTitle("Example")
            .setContentText("This is Notification Test")
            .build()

        notificationManager?.notify(notificationId, notification)*/
    }

    private fun sendNotification(msgData: Map<String, String>) {

        val uniId = 0

        // 알림 채널 이름
        val channelId = getString(R.string.app_name)	// Notice

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보와 작업을 지정한다.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)                      // 아이콘
            .setContentTitle(msgData.getValue(PushContents.title))               // 제목
            .setContentText(msgData.getValue(PushContents.body))              // 세부내용
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)                          // 알림 실행 시 Intent

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())
    }

}