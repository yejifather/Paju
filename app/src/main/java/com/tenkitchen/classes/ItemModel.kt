package com.tenkitchen.classes

import android.util.Log

class ItemModel( var regDate: String, var memHp: String, var proPrice: Int, var setNum: Int, var proName: String ) {

    var TAG: String = "로그";

    // 기본 생성자
    init {
        Log.d(TAG, "ItemModel - init() called");
    }

}