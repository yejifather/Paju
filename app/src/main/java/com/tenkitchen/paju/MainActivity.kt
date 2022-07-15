package com.tenkitchen.paju

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // 서버 통신은 HttpUrlConnection, Volley, OkHttp, [Retrofit2]
    // https://velog.io/@jini0318/Android-Retrofit2%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-API-%EC%84%9C%EB%B2%84%ED%86%B5%EC%8B%A0
}