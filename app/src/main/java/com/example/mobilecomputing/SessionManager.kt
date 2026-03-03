package com.example.mobilecomputing

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("KPAPP_PREFS", Context.MODE_PRIVATE)

    fun saveUserId(userId: Int) {
        sharedPref.edit { putInt("USER_ID", userId) }
    }

    fun getUserId(): Int {
        return sharedPref.getInt("USER_ID", -1)
    }

    fun clearSession() {
        sharedPref.edit { remove("USER_ID") }
    }
}