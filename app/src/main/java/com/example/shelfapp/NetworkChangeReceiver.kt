package com.example.shelfapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.shelfapp.Util.Constants.Companion.INTERNET_BROADCAST_INTENT_FILTER
import com.example.shelfapp.Util.Logger
import com.example.shelfapp.Util.Utility
import java.lang.Exception

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            Logger("Inside try")
                Logger("Inside actionReceived")
                if (context?.let { Utility(it).isNetworkAvailable() } == true) {
                    val broadcastIntent = Intent(INTERNET_BROADCAST_INTENT_FILTER)
                    broadcastIntent.putExtra("DATA", true)
                    context.sendBroadcast(broadcastIntent)
                } else {
                    val broadcastIntent = Intent(INTERNET_BROADCAST_INTENT_FILTER)
                    broadcastIntent.putExtra("DATA", false)
                    context?.sendBroadcast(broadcastIntent)
                }
        } catch (e: Exception) {
            Logger("Network Change ${e.message}")
        }
    }
}