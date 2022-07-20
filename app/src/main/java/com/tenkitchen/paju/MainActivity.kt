package com.tenkitchen.paju

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.tenkitchen.classes.ItemAdapter
import com.tenkitchen.classes.ItemModel
import com.tenkitchen.classes.RetrofitManager
import com.tenkitchen.objects.CommonUtil
import com.tenkitchen.objects.Constants
import com.tenkitchen.objects.Constants.TAG
import com.tenkitchen.objects.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private val broadPush: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //TODO: 푸쉬 받고 할일
            Toast.makeText(context, "제품이 판매되었습니다.", Toast.LENGTH_LONG).show();

            var itemAdapter: ItemAdapter = ItemAdapter()
            getItemToServer(itemAdapter)
        }
    }

    // 뷰가 화면에 그려질때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val br: BroadcastReceiver = AppBroadcastReceiver()

//        val filter = IntentFilter().apply {
//            addAction(Intent.ACTION_SCREEN_ON)
//        }
//        registerReceiver(br, filter)

        val filterPush = IntentFilter()
        filterPush.addAction(Constants.RESIEVE_PUSH)
        registerReceiver(broadPush, filterPush)

        // 어댑터 인스턴스 생성
        var itemAdapter: ItemAdapter = ItemAdapter()

        // 리사이클러뷰 설정
        recyclerView.apply{
            // 리사이클러뷰 등 설정
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

            // 어댑터 장착
            adapter = itemAdapter
        }

        // 리사이클러 뷰 - 당겨서 새로고침
        refreshLayout.setOnRefreshListener {

            getItemToServer(itemAdapter)
            refreshLayout.isRefreshing = false
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if ( !task.isSuccessful ) {
                Log.d(TAG, "onCreate: Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result.toString()

            // 푸쉬토큰 저장
            RetrofitManager.instance.putToken(token = token, completion = {
                responseState, responseBody ->

                when(responseState) {
                    RESPONSE_STATE.OK -> {
                        Log.d(TAG, "api 호출 성공 : $responseBody")
                        var result = JSONObject(responseBody)
                        Log.d(TAG, "api 호출 성공 : ${result.get("result")}")
                    }
                    RESPONSE_STATE.FAILURE -> {
                        Toast.makeText(this, "api 호출 에러", Toast.LENGTH_SHORT).show();
                    }
                }
            })
        })

        getItemToServer(itemAdapter)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadPush)
    }

    // 서버에서 데이터 가져오기
    private fun getItemToServer(itemAdapter: ItemAdapter) {

        // 오늘 날짜 가져오기
        var currentDate: LocalDate = LocalDate.now()
        txtDate.text = currentDate.toString();

        var itemList = ArrayList<ItemModel>()

        // 판매 목록 가져오기
        RetrofitManager.instance.getSettlementList(date = currentDate.toString(), ca_num = 3, completion = {
                responseState, responseBody ->

            when(responseState) {
                RESPONSE_STATE.OK -> {
                    Log.d(TAG, "api 호출 성공 : $responseBody")
                    var list = JSONArray(responseBody);
                    var totalBuyCount: Int = 0
                    var totalPrice: Int = 0

                    for ( i in 0 until list.length()) {
                        val info: JSONObject = list.getJSONObject(i)

                        var regDate = info.get("regDate").toString()
                        var memHp = info.get("memHp").toString()
                        var setNum = info.get("setNum").toString().toInt()
                        var proName = info.get("proName").toString()
                        var proPrice = info.get("setdPrice").toString().toInt()
                        var buyCount = info.get("buyCount").toString().toInt()

                        var item = ItemModel(regDate = regDate, memHp = memHp, setNum = setNum, proName = proName, proPrice = CommonUtil.digit2Comma(proPrice) + "원", buyCount = buyCount)
                        itemList.add(item)

                        totalPrice += proPrice
                        totalBuyCount += buyCount
                    }

                    txtSellCount.text = "결제 ${list.length()}건 / 제품 ${totalBuyCount}개 / 매출액 ${CommonUtil.digit2Comma(totalPrice)}원"

                    if ( itemList.size > 0 ) {
                        itemAdapter.submitList(itemList);
                    }
                }
                RESPONSE_STATE.FAILURE -> {
                    Toast.makeText(this, "api 호출 에러", Toast.LENGTH_SHORT).show();
                }
            }
        })
    }

}
