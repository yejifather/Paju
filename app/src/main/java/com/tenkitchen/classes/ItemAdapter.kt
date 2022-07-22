package com.tenkitchen.classes

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tenkitchen.interfaces.IMyRecyclerview
import com.tenkitchen.objects.Constants.TAG
import com.tenkitchen.paju.R

class ItemAdapter(iMyRecyclerview: IMyRecyclerview):RecyclerView.Adapter<ItemViewHolder>() {

    private var itemList = ArrayList<ItemModel>()

    private var iMyRecyclerview: IMyRecyclerview? = null

    // 생성자
    init {
        this.iMyRecyclerview = iMyRecyclerview
    }

    // 뷰 홀더가 생성되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // 연결 할 레이아웃 설정
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false), this.iMyRecyclerview!!)
    }

    // 목록의 아이템 수
    override fun getItemCount(): Int {
        return this.itemList.size
    }

    // 뷰와 뷰홀더가 묶였을때
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(this.itemList[position])

        // 클릭 설정
        /*holder.itemView.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: ${this.itemList[position].setNum}")
        }*/
    }

    // 외부에서 데이터 넘기기
    fun submitList(itemList: ArrayList<ItemModel>){
        notifyDataSetChanged()  // 이놈 없으면 리사이클러뷰 못 그려줌
        this.itemList = itemList
    }

}