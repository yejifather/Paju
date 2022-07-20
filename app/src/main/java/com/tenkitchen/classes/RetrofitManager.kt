package com.tenkitchen.classes

import android.util.Log
import com.google.gson.JsonElement
import com.tenkitchen.interfaces.IRetrofit
import com.tenkitchen.objects.API
import com.tenkitchen.objects.Constants.TAG
import com.tenkitchen.objects.RESPONSE_STATE
import com.tenkitchen.objects.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {

    companion object {
        val instance = RetrofitManager()
    }

    // http 콜 만들기
    // 레트로핏 인터페이스 가져오기
    private val iRetrofit: IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)

    // 토큰 저장
    fun putToken(token: String?, completion: (RESPONSE_STATE, String) -> Unit) {

        val pToken = token ?:""

        val call = iRetrofit?.putToken(token = pToken) ?: return

        call.enqueue(object: retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                completion(RESPONSE_STATE.OK, response.body().toString())
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - on Failure() called / t: $t")
                completion(RESPONSE_STATE.FAILURE, t.toString())
            }
        })
    }

    // 판매 목록 가져오기
    fun getSettlementList(date: String?, ca_num: Int?, completion: (RESPONSE_STATE, String) -> Unit) {

        val pDate = date ?:""
        val pCaNum = ca_num ?:0

        //Log.d(TAG, "RetrofitManager getSettlementList : called / currentDate : $currentDate, caNum : $caNum")

        val call = iRetrofit?.getSettlementList(date = pDate, ca_num = pCaNum) ?: return

        call.enqueue(object: retrofit2.Callback<JsonElement>{
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                //Log.d(TAG, "RetrofitManager - onResponse() called / response : ${response.body()}")
                completion(RESPONSE_STATE.OK, response.body().toString())
            }

            // 응답 실패
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - on Failure() called / t: $t")
                completion(RESPONSE_STATE.FAILURE, t.toString())
            }
        })
    }

}