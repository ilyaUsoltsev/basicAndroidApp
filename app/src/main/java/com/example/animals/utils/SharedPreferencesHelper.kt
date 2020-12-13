package com.example.animals.utils

import android.content.Context
import androidx.preference.PreferenceManager

class SharedPreferencesHelper(context: Context) {
    private val _apiKey = "Api Key"

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context);

    fun saveApiKey(key: String?) {
        prefs.edit().putString(_apiKey, key).apply()
    }

    fun getApiKey() = prefs.getString(_apiKey, null);
}