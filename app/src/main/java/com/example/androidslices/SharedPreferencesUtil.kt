package com.example.androidslices

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtil {

    fun save(context: Context, value: Int) {
        getSharedPrefs(context).edit().putInt("value", value).apply()
    }

    fun getValue(context: Context): Int {
        return getSharedPrefs(context).getInt("value", 0)
    }

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("android-slices-shared-prefs", Context.MODE_PRIVATE)
    }


}