package com.tenkitchen.paju

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.tenkitchen.classes.ItemAdapter
import com.tenkitchen.classes.ItemModel
import com.tenkitchen.classes.RetrofitManager
import com.tenkitchen.interfaces.IMyRecyclerview
import com.tenkitchen.interfaces.IPopupDialog
import com.tenkitchen.interfaces.MessageListener
import com.tenkitchen.objects.*
import com.tenkitchen.objects.Constants.TAG
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), IMyRecyclerview, IPopupDialog, MessageListener {

    private var m_itemAdapter: ItemAdapter? = null
    private var m_itemList: ArrayList<ItemModel>? = null
    private var m_date: String? = ""
    //private val serverUrl = "ws://sock.tenkitchen.com:10085/echo"

    private val broadPush: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //TODO: 푸쉬 받고 할일
            Toast.makeText(context, "제품이 판매되었습니다.", Toast.LENGTH_LONG).show();

            //var itemAdapter: ItemAdapter = ItemAdapter(m_itemAdapter)
            getItemToServer(m_itemAdapter!!)
        }
    }

    // 뷰가 화면에 그려질때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 웹소켓
        //WebSocketManager.init(serverUrl, this)
        //webSocketConnect()

        // MyService 백그라운드 서비스
        /*var serviceBinder: MyService.MyBinder? = null
        val connection: ServiceConnection = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                // bindService() 함수로 서비스를 구동할때 자동으로 호출 됨
                serviceBinder = service as MyService.MyBinder
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                // unbindService() 함수로 서비스를 종료할때 자동으로 호출 됨
            }
        }

        val intent = Intent(this, MyService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        if ( serviceBinder != null ) {
            serviceBinder?.funA(10)
        }*/

        /*val br: BroadcastReceiver = AppBroadcastReceiver()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(br, filter)*/

        // 기본 날짜 세팅 -
        var currentDate: LocalDate = LocalDate.now()
        txtDate.text = currentDate.toString();
        m_date = currentDate.toString();

        // 어댑터 인스턴스 생성
        m_itemAdapter = ItemAdapter(this)

        // 아이템 리스트 초기화
        m_itemList = ArrayList<ItemModel>()

        // 리사이클러뷰 설정
        recyclerView.apply{
            // 리사이클러뷰 등 설정
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

            // 어댑터 장착
            adapter = m_itemAdapter
        }

        // 리사이클러 뷰 - 당겨서 새로고침
        refreshLayout.setOnRefreshListener {

            getItemToServer(m_itemAdapter!!)
            refreshLayout.isRefreshing = false
        }

        FirebaseMessaging.getInstance().subscribeToTopic("Tenkitchen").addOnCompleteListener { task ->
            /*if (task.isSuccessful) {
                Log.d(TAG,"구독 요청 성공")
            } else {
                Log.d(TAG, "구독 요청 실패")
            }*/
        }

        val filterPush = IntentFilter()
        filterPush.addAction(Constants.RESIEVE_PUSH)
        registerReceiver(broadPush, filterPush)

        getItemToServer(m_itemAdapter!!)

        // 날짜 클릭시
        txtDate.setOnClickListener() {
            Log.d(TAG, "onCreate: CLICK")
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var dateString: String = ""

                dateString = if ( month + 1 < 10 ) {
                    if ( dayOfMonth < 10 ) {
                        "${year}-0${month+1}-0${dayOfMonth}"
                    } else {
                        "${year}-0${month+1}-${dayOfMonth}"
                    }
                } else {
                    if ( dayOfMonth < 10 ) {
                        "${year}-${month+1}-0${dayOfMonth}"
                    } else {
                        "${year}-${month+1}-${dayOfMonth}"
                    }
                }

                m_date = dateString
                txtDate.text = dateString

                getItemToServer(m_itemAdapter!!)
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    override fun onResume() {
        super.onResume()

        // 오늘 날짜로 다시 세팅
        var currentDate: LocalDate = LocalDate.now()
        txtDate.text = currentDate.toString();
        m_date = currentDate.toString();
        
        getItemToServer(m_itemAdapter!!);

        //WebSocketManager.reconnect()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadPush)
        //WebSocketManager.close()
    }

    // 서버에서 데이터 가져오기
    private fun getItemToServer(itemAdapter: ItemAdapter) {

        // 판매 목록 가져오기
        RetrofitManager.instance.getSettlementList(date = m_date, ca_num = 3, completion = {
                responseState, responseBody ->

            m_itemList?.clear()

            when(responseState) {
                RESPONSE_STATE.OK -> {
                    Log.d(TAG, "api 호출 성공 : $responseBody")
                    var list = JSONArray(responseBody);
                    var totalBuyCount: Int = 0
                    var totalPrice: Int = 0

                    for ( i in 0 until list.length() ) {
                        val info: JSONObject = list.getJSONObject(i)

                        var regDate = info.get("regDate").toString()
                        var memHp = info.get("memHp").toString()
                        var setNum = info.get("setNum").toString().toInt()
                        var proName = info.get("proName").toString()
                        var proPrice = info.get("setdPrice").toString().toInt()
                        var buyCount = info.get("buyCount").toString().toInt()

                        var item = ItemModel(regDate = regDate, memHp = memHp, setNum = setNum, proName = proName, proPrice = CommonUtil.digit2Comma(proPrice) + "원", buyCount = buyCount)
                        m_itemList?.add(item)

                        totalPrice += proPrice
                        totalBuyCount += buyCount
                    }

                    txtSellCount.text = "결제 ${list.length()}건 / 제품 ${totalBuyCount}개 / 매출액 ${CommonUtil.digit2Comma(totalPrice)}원"

                    //if ( itemList.size > 0 ) {
                    m_itemList?.let { itemAdapter.submitList(it) };
                    //}

                    // 가장 마지막 아이템으로 이동
                    recyclerView.scrollToPosition(m_itemList!!.size - 1)
                }
                RESPONSE_STATE.FAILURE -> {
                    Toast.makeText(this, "api 호출 에러", Toast.LENGTH_SHORT).show();
                }
            }
        })
    }

    // 아이템 클릭시 팝업 다이어로그 실행
    override fun onItemClicked(position: Int) {

        var setNum: Int? = this.m_itemList?.get(position)?.setNum
        setNum?.let { PopupDialog(this, this, it) }?.show()
    }

    override fun onConfirmButtonClicked() {
        Log.d(TAG, "onConfirmButtonClicked: called")
    }

    // 웹소켓 연결
    /*private fun webSocketConnect() {
        thread {
            kotlin.run {
                WebSocketManager.connect()
            }
        }
    }*/

    // 웹소켓 메시지 전송
    /*private fun webSocketSendMessage(message: String) {
        if ( WebSocketManager.sendMessage(message)) {
            Log.d(TAG, "webSocketSendMessage: Send from the client")
        }
    }*/

    // 웹소켓 종료
    /*private fun webSocketClose() {
        WebSocketManager.close()
    }*/

    override fun onConnectSuccess() {
        Log.d(TAG, "onConnectSuccess: Connected successfully")

        /*val contentObj = JSONObject()
        contentObj.put("client_num", 3)
        contentObj.put("client_type", "A")

        val encoder: Base64.Encoder = Base64.getEncoder()
        val content: String = encoder.encodeToString(contentObj.toString().toByteArray())

        val jobObj = JSONObject()
        jobObj.put("job", 100000)
        jobObj.put("content", content)

        Log.d(TAG, "sendData: $jobObj")

        webSocketSendMessage(jobObj.toString())*/
    }

    override fun onConnectFailed() {
        Log.d(TAG, "onConnectFailed: Connection failed")
    }

    override fun onClose() {
        Log.d(TAG, "onClose: Closed successfully")
    }

    override fun onMessage(text: String?) {
        Log.d(TAG, "onMessage: Receive message: $text")

        /*val messageObj = JSONObject(text)
        val job = messageObj.getInt("job")
        val content = messageObj.getString("content")

        val decoder: Base64.Decoder = Base64.getDecoder()
        val decoded = String(decoder.decode(content))
        val contentObj = JSONObject(decoded)
        val messageType = contentObj.getInt("message_type")
        val message = contentObj.getString("message")

        sendLocalPush(messageType, message);*/
    }

    /*private fun sendLocalPush( type: Int, message: String ) {
        if ( type == 0 ) {
            // QR코드를 찍음
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
                    PushContents.title to "텐키친 파주운정점",
                    PushContents.body to message!!
                )

                sendNotification(remoteMessageData)
            }
        } else {
            // 결제함
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
                    PushContents.title to "텐키친 파주운정점",
                    PushContents.body to message!!
                )

                sendNotification(remoteMessageData)
            }
        }
    }*/

    /*private fun sendNotification(msgData: Map<String, String>) {

        val body: String = msgData.getValue(PushContents.body)

        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // 인텐트 생성
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // 일회용 PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임
        val fullScreenPendingIntent = PendingIntent.getActivity(baseContext, uniId, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // 알림 소리
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // head up 알림 생성하기
        val notificationId = 1001
        createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_HIGH, false, getString(R.string.app_name), "App notification channel")

        val channelId = "$packageName-${getString(R.string.app_name)}"

        // 푸시알람 부가설정
        var notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_black)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_ten_foreground))
            .setContentTitle("텐'키친 파주운정점")
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setSubText("좋아 좋아~!!")
            .setContentIntent(fullScreenPendingIntent)
            .setPriority(NotificationCompat.VISIBILITY_PUBLIC)

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }*/

    // NotificationChannel 만드는 메서드
    /*private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val sound: Uri =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.get_message_3)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(sound, audioAttributes)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }*/
}
