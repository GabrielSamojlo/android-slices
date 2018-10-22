package com.example.androidslices

import android.app.slice.Slice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.widget.Toast

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        context ?: return

        if (intent.action == TOAST_ACTION) {
            Toast.makeText(context, "Here is your Toast.", Toast.LENGTH_LONG).show()
        } else if (intent.action == WIFI_TOGGLE_ACTION) {
            val isWiFiTurnedOn = intent.getBooleanExtra(Slice.EXTRA_TOGGLE_STATE, false)
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = isWiFiTurnedOn
        }
    }

    companion object {
        const val TOAST_ACTION = "toast_action"
        const val WIFI_TOGGLE_ACTION = "wifi_toggle_action"
    }
}