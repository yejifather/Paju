package com.tenkitchen.paju

import android.app.DatePickerDialog
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
import com.google.firebase.messaging.FirebaseMessaging
import com.tenkitchen.classes.ItemAdapter
import com.tenkitchen.classes.ItemModel
import com.tenkitchen.classes.RetrofitManager
import com.tenkitchen.interfaces.IMyRecyclerview
import com.tenkitchen.interfaces.IPopupDialog
import com.tenkitchen.objects.CommonUtil
import com.tenkitchen.objects.Constants
import com.tenkitchen.objects.Constants.TAG
import com.tenkitchen.objects.RESPONSE_STATE
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), IMyRecyclerview, IPopupDialog {

    private var m_itemAdapter: ItemAdapter? = null
    private var m_itemList: ArrayList<ItemModel>? = null
    private var m_date: String? = ""

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
                    "${year}-0${month+1}-${dayOfMonth}"
                } else {
                    "${year}-${month+1}-${dayOfMonth}"
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
        getItemToServer(m_itemAdapter!!);
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
}
