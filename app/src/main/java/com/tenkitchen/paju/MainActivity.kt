package com.tenkitchen.paju

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.RemoteMessage
import com.tenkitchen.classes.ItemAdapter
import com.tenkitchen.classes.ItemModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG: String = "로그";
    var itemList = ArrayList<ItemModel>()

    // 카드뷰 아이템 어댑터
    private lateinit var itemAdapter: ItemAdapter

    // 뷰가 화면에 그려질때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for ( i in 1..4 ) {
            val item = ItemModel(regDate = "2022-07-14 11:22", memHp = "010-1234-4567", proName = "닭다리 순살 간장 치킨 외 $i 건", proPrice = 13900)
            this.itemList.add(item)
        }

//        Log.d(TAG, "itemList size 후 : ${this.itemList.size}")

        // 어댑터 인스턴스 생성
        itemAdapter = ItemAdapter()
        itemAdapter.submitList(this.itemList)
        
        // 리사이클러뷰 설정
        recyclerView.apply{
            // 리사이클러뷰 등 설정
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            
            // 어댑터 장착
            adapter = itemAdapter
        }

        // 리사이클러 뷰 - 당겨서 새로고침
        refreshLayout.setOnRefreshListener {
            // 새로고침 코드를 작성
            /*for ( i in 1..2 ) {
                val item = ItemModel(regDate = "2022-07-16 11:22", memHp = "010-000-1111", proName = "닭다리 순살 간장 치킨 외 $i 건", proPrice = 13900)
                this.itemList.add(item)
            }

            // 어댑터 인스턴스 생성
            itemAdapter = ItemAdapter()
            itemAdapter.submitList(this.itemList)

            // 리사이클러뷰 설정
            recyclerView.apply{
                // 리사이클러뷰 등 설정
                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

                // 어댑터 장착
                adapter = itemAdapter
            }*/

            refreshLayout.isRefreshing = false
        }




    }


    // 서버 통신은 HttpUrlConnection, Volley, OkHttp, [Retrofit2]
    // https://velog.io/@jini0318/Android-Retrofit2%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-API-%EC%84%9C%EB%B2%84%ED%86%B5%EC%8B%A0
}