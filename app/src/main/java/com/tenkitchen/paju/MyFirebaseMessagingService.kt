package com.tenkitchen.paju

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tenkitchen.classes.RetrofitManager
import com.tenkitchen.objects.Constants
import com.tenkitchen.objects.PushContents
import com.tenkitchen.objects.RESPONSE_STATE
import org.json.JSONObject


class MyFirebaseMessagingService:FirebaseMessagingService() {
    private val TAG = "로그"

    //private val CHANNEL_ID = "CHANNEL_TEN"   // Channel for notification
    //private var notificationManager: NotificationManager? = null

    override fun onNewToken(token: String) {
        Log.d(TAG, "new Token: $token")

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d(TAG, "sendRegistrationToServer: new Token${token}")

        // 기기 고유값(공장초기화 전까지 동일)
        val ssid = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID);

        // 푸쉬토큰 저장
        RetrofitManager.instance.putToken(token = token, ssid = ssid, completion = {
                responseState, responseBody ->

            when(responseState) {
                RESPONSE_STATE.OK -> {
                    Log.d(Constants.TAG, "Token 갱신 완료")
                    var result = JSONObject(responseBody)
                }
                RESPONSE_STATE.FAILURE -> {
                    Toast.makeText(this, "Token 갱신 실패", Toast.LENGTH_SHORT).show();
                }
            }
        })
    }

    override fun onMessageReceived(rm: RemoteMessage) {
        // 메시지에 데이터 페이로드가 포함되어 있는지 확인하십시오.
        if (rm.data.isNotEmpty()) {
            //Log.d(TAG, "Message data payload: ${rm.data}")

            if ( MyApp.isForeground ) {
                Log.d(TAG, "onMessageReceived: 앱 실행중")
                // 앱이 실행중일때
                val intentPush = Intent(Constants.RESIEVE_PUSH)
                this.sendBroadcast(intentPush)
            } else {
                Log.d(TAG, "onMessageReceived: 앱 실행중 아님")

                // 화면 깨우기
                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
                @SuppressLint("InvalidWakeLockTag") val wakeLock =
                    pm.newWakeLock(
                        PowerManager.FULL_WAKE_LOCK
                                or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"
                    )
                wakeLock.acquire(3000)
                wakeLock.release()

                val remoteMessageData = mapOf(
                    PushContents.title to rm.data["title"].toString(),
                    PushContents.body to rm.data["body"].toString()!!
                )

                sendNotification(remoteMessageData)
            }
        }
    }

    private fun sendNotification(msgData: Map<String, String>) {

        val body: String = msgData.getValue(PushContents.body)
        val title: String = msgData.getValue(PushContents.title)

        Log.d(TAG, "sendNotification: body : ${body}, title : ${title}")
        
        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            putExtra("Notification", body)
            putExtra("Notification", title)
        }

        // 일회용 PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임
        var pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT)

        // 알림 소리
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // head up 알림 생성하기
        val notificationId = 1001
        createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_HIGH, false, getString(R.string.app_name), "App notification channel")

        val channelId = "$packageName-${getString(R.string.app_name)}"

        val fullScreenPendingIntent = PendingIntent.getActivity(baseContext, uniId, intent, PendingIntent.FLAG_CANCEL_CURRENT)  // FLAG_CANCEL_CURRENT

        // 푸시알람 부가설정
        var notificationBuilder = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.ic_stat_black)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_ten_foreground))
            .setContentTitle("텐'키친 파주운정점")
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setSubText("좋아 좋아~!!")
            .setTimeoutAfter(10000)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    // NotificationChannel 만드는 메서드
    private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}