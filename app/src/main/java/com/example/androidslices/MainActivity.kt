package com.example.androidslices

import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(WiFiBroadcastReceiver(), intentFilter)

        minusBtn.setOnClickListener {
            saveValue(getValue() - 1)
            updateView()
        }

        plusBtn.setOnClickListener {
            saveValue(getValue() + 1)
            updateView()
        }
    }

    override fun onResume() {
        super.onResume()
        updateView()
    }

    private fun saveValue(value: Int) {
        SharedPreferencesUtil.save(this, value)
    }

    private fun getValue(): Int {
        return SharedPreferencesUtil.getValue(this)
    }

    private fun updateView() {
        valueTv.text = SharedPreferencesUtil.getValue(this).toString()
    }
}
