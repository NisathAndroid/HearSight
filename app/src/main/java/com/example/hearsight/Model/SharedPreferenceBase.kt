package com.example.hearsight.Model

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceBase(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
        editor.clear()
    }

    fun clearSharedPreference(): SharedPreferences.Editor? {
        val cleardata=sharedPreferences.edit()
        return cleardata
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}