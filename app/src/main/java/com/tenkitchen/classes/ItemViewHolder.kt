package com.tenkitchen.classes

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tenkitchen.interfaces.IMyRecyclerview
import kotlinx.android.synthetic.main.card_item.view.*

class ItemViewHolder(itemView: View, iMyRecyclerview: IMyRecyclerview): RecyclerView.ViewHolder(itemView), View.OnClickListener{

    private val txtRegDate = itemView.txtRegDate
    private val txtMemHp = itemView.txtMemHp
    private val txtProName = itemView.txtProName
    private val txtProPrice = itemView.txtProPrice

    private var iMyRecyclerview: IMyRecyclerview? = null

    init {
        //Log.d(TAG, "ItemViewHolder - init() called")
        itemView.setOnClickListener(this)
        this.iMyRecyclerview = iMyRecyclerview
    }

    // 데이터와 뷰를 묶는다
    fun bind( itemModel: ItemModel ){
        //Log.d(TAG, "ItemViewHolder - bind() called")

        txtRegDate.text = itemModel.regDate
        txtMemHp.text = itemModel.memHp
        txtProName.text = itemModel.proName
        txtProPrice.text = itemModel.proPrice.toString()
    }

    override fun onClick(v: View?) {
        this.iMyRecyclerview?.onItemClicked(adapterPosition)
    }
}