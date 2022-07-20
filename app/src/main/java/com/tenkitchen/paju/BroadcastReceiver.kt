package com.tenkitchen.paju

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.messaging.RemoteMessage

class AppBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action.equals(Intent.ACTION_SCREEN_ON)){
            Toast.makeText(context, "good", Toast.LENGTH_LONG).show()
        }
    }
}