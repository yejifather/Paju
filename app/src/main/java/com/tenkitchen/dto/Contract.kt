package com.tenkitchen.dto

import com.google.gson.annotations.SerializedName

data class Contract(var s_time: String, var r_code: Int, var job: Int ) {
    data class List(
        val mem_hp : String,
        val reg_date : String,
        val pro_price : Int,
        val set_num : Int,
        val pro_name : String
    )
}

/*
    @SerializedName("regDate") var regDate: String,
    @SerializedName("memHp") var memHp: String,
    @SerializedName("proPrice") var proPrice: Int,
    @SerializedName("setNum") var setNum: Int,
    @SerializedName("proName") var proName: String
*/
/*
    {
        "statusCode":"200",
        "statusMessage":"Login Success",
        "data":[
        {
            "id":"xxxxxx",
            "nick_name":"xxxxxx",
            "email":"xxxxxx",
            "profile":"xxxxxx",
        }
        ]
    }

    data class LoginResponse(
        @SerializedName("statusCode") val code : String,
        @SerializedName("statusMessage") val message : String,
        @SerializedName("data") val data : Data? = null
    ) {
        data class Data(
            @SerializedName("id") val userID : String,
            @SerializedName("nick_name") val userNickName : String,
            @SerializedName("email") val userEmail : String,
            @SerializedName("profile") val userProfileImage : String,
        )
    }*//*

)
*/
