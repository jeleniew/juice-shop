package com.example.juiceshop.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefHelper {

    private const val PREFS_NAME = "PREFS_NAME"
    private const val KEY_EMAIL = "KEY_EMAIL"
    private const val KEY_PASSWORD = "KEY_PASSWORD"
    private const val TOKEN = "TOKEN"

    private lateinit var sharedPreferences: SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var email: String?
        get() = sharedPreferences.getString(KEY_EMAIL, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_EMAIL, value).apply()
        }

    var password: String?
        get() = sharedPreferences.getString(KEY_PASSWORD, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_PASSWORD, value).apply()
        }

    var token: String?
        get() = sharedPreferences.getString(TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(TOKEN, value).apply()
        }
}