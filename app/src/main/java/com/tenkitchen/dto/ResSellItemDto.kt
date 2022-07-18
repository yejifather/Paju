package com.tenkitchen.dto

import com.google.gson.annotations.SerializedName

data class ResSellItemDto(
    @SerializedName("s_time") var sTime: String,
    @SerializedName("r_code") var rCode: Int,
    @SerializedName("job") var job: Int
) {
    data class List(
        @SerializedName("mem_hp") val memHp : String,
        @SerializedName("reg_date") val regDate : String,
        @SerializedName("pro_price") val proPrice : Int,
        @SerializedName("set_num") val setNum : Int,
        @SerializedName("pro_name") val proName : String
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
