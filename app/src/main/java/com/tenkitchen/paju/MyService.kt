package com.tenkitchen.paju

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.tenkitchen.objects.Constants.TAG

class MyService : Service() {

    class MyBinder : Binder() {
        fun funA(arg: Int) {
            Log.d(TAG, "funA: $arg")
        }
        fun funB(arg: Int): Int {
            return arg * arg
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

   /* inner class MyServiceBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    fun bindServiceText(): String {
        return "abcdefg"
    }

    val binder = MyServiceBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: Service Start")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service Stop")
        super.onDestroy()
    }*/
}