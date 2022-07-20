package com.tenkitchen.classes

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_item.view.*

class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val txtRegDate = itemView.txtRegDate
    private val txtMemHp = itemView.txtMemHp
    private val txtProName = itemView.txtProName
    private val txtProPrice = itemView.txtProPrice

    init {
        //Log.d(TAG, "ItemViewHolder - init() called")
    }

    // 데이터와 뷰를 묶는다
    fun bind( itemModel: ItemModel ){
        //Log.d(TAG, "ItemViewHolder - bind() called")

        txtRegDate.text = itemModel.regDate
        txtMemHp.text = itemModel.memHp
        txtProName.text = itemModel.proName
        txtProPrice.text = itemModel.proPrice.toString()
    }
}