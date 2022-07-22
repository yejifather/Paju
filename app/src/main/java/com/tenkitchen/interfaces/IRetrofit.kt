package com.tenkitchen.interfaces

import com.google.gson.JsonElement
import com.tenkitchen.dto.Contract
import com.tenkitchen.objects.API
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {

    // http://localhost:18018/settlement?query='searchTerm'

    // 제품 목록을 가져온다.
    @GET(API.GET_SETTLEMENT_LIST)
    fun getSettlementList(
        @Query("date") date: String,
        @Query("ca_num") ca_num: Int)
    : Call<JsonElement>

    // 토큰 등록
    @PUT(API.PUT_TOKEN)
    fun putToken(
        @Query("token") token: String,
        @Query("ssid") ssid: String)
    : Call<JsonElement>

    // 구매 상세 내역을 가져온다
    @GET(API.GET_SETTLEMENT_DETAIL)
    fun getSettlementDetails(
        @Query("set_num") set_num: Int)
    : Call<JsonElement>
}