package com.example.androidslices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

class WiFiBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
            context?.contentResolver?.notifyChange(MySliceProvider.INTERACTIVE_SLICE_URI, null)
        }
    }

}