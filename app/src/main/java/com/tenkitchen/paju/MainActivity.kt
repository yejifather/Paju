package com.tenkitchen.paju

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tenkitchen.classes.ItemAdapter
import com.tenkitchen.classes.ItemModel
import com.tenkitchen.dto.ResSellItemDto
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    val TAG: String = "로그";
    //var itemList = ArrayList<ItemModel>()

    // 카드뷰 아이템 어댑터
    private lateinit var itemAdapter: ItemAdapter

    // 뷰가 화면에 그려질때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)



//        Log.d(TAG, "itemList size 후 : ${this.itemList.size}")

        // 어댑터 인스턴스 생성
        itemAdapter = ItemAdapter()
        itemAdapter.submitList(getItemToServer())
        
        // 리사이클러뷰 설정
        recyclerView.apply{
            // 리사이클러뷰 등 설정
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            
            // 어댑터 장착
            adapter = itemAdapter
        }

        // 리사이클러 뷰 - 당겨서 새로고침
        refreshLayout.setOnRefreshListener {
            getItemToServer()
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

    // 서버에서 데이터 가져오기
    private fun getItemToServer(): ArrayList<ItemModel>{

        var itemList = ArrayList<ItemModel>()



        for ( i in 1..4 ) {
            val item = ItemModel(regDate = "2022-07-14 11:22", memHp = "010-1234-4567", setNum = i,  proName = "닭다리 순살 간장 치킨 외 $i 건", proPrice = 13900 )
            itemList.add(item)
        }

        return itemList;
    }


    // 서버 통신은 HttpUrlConnection, Volley, OkHttp, [Retrofit2]
    // https://velog.io/@jini0318/Android-Retrofit2%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-API-%EC%84%9C%EB%B2%84%ED%86%B5%EC%8B%A0
}
