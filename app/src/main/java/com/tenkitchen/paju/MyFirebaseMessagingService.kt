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
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tenkitchen.objects.Constants
import com.tenkitchen.objects.PushContents


class MyFirebaseMessagingService:FirebaseMessagingService() {
    private val TAG = "로그"

    //private val CHANNEL_ID = "CHANNEL_TEN"   // Channel for notification
    //private var notificationManager: NotificationManager? = null

    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if ( MyApp.isForeground ) {
            // 앱이 실행중일때
            val intentPush = Intent(Constants.RESIEVE_PUSH)
            this.sendBroadcast(intentPush)
        } else {
            // 앱이 실행중이 아닐때
            if (remoteMessage.data.isNotEmpty()) {
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
                Log.d(TAG, "data is empty")
            }
        }
    }

    private fun sendNotification(msgData: Map<String, String>) {

        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()
        //var uniId = 0

        // 알림 채널 이름
        val channelId = "$packageName-${getString(R.string.app_name)}"

        val intent = Intent(this, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_CANCEL_CURRENT)    // FLAG_CANCEL_CURRENT

        // 알림에 대한 UI 정보와 작업을 지정한다.
        var notificationBuilder = NotificationCompat.Builder(this, channelId)
            .apply {
                setGroup(Constants.NOTI_GROUP_ID)
                setSmallIcon(R.drawable.ic_stat_black)
                setContentTitle(msgData.getValue(PushContents.title))
                setContentText(msgData.getValue(PushContents.body))
                priority = NotificationCompat.PRIORITY_HIGH
                setCategory(NotificationCompat.CATEGORY_MESSAGE)
                setAutoCancel(true)
                setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(msgData.getValue(PushContents.body))
                )
                setFullScreenIntent(fullScreenPendingIntent, true)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setSubText("좋아 좋아~")
                setSound(soundUri)
            }

        val notificationManager = NotificationManagerCompat.from(this)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())
    }

}