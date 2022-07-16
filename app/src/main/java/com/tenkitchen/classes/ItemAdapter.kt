package com.tenkitchen.classes

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tenkitchen.paju.R

class ItemAdapter:RecyclerView.Adapter<ItemViewHolder>() {

    val TAG: String = "로그"
    //
    private var itemList = ArrayList<ItemModel>()

    // 뷰 홀더가 생성되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        // 연결 할 레이아웃 설정
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false))
    }

    // 목록의 아이템 수
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Log.d(TAG, "ItemAdapter - onBindViewHolder() called / position $position")
        holder.bind(this.itemList[position])
    }

    // 뷰와 뷰홀더가 묶였을때
    override fun getItemCount(): Int {
        return this.itemList.size
    }

    // 외부에서 데이터 넘기기
    fun submitList(itemList: ArrayList<ItemModel>){
        this.itemList = itemList
    }

}