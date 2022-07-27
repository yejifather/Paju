package com.tenkitchen.objects

object Constants {
    const val TAG: String = "로그"
    const val NOTI_GROUP_ID = "NOTI_GROUP_ID"
    const val RESIEVE_PUSH = "RESIEVE_PUSH"
}

enum class RESPONSE_STATE {
    OK,
    FAILURE
}

object API {
    //const val BASE_URL: String = "http://kiosk.tenkitchen.com/"
    const val BASE_URL: String = "http://192.168.0.221:18018/"

    const val GET_SETTLEMENT_LIST: String = "settlement"
    const val GET_SETTLEMENT_DETAIL: String = "settlement_detail"
    const val PUT_TOKEN: String = "token"
}