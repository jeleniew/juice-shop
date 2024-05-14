package com.example.juiceshop.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefHelper {

    private const val PREFS_NAME = "PREFS_NAME"
    private const val KEY_REMEMBER_ME = "KEY_REMEMBER_ME"
    private const val KEY_EMAIL = "KEY_EMAIL"
    private const val KEY_PASSWORD = "KEY_PASSWORD"
    private const val TOKEN = "TOKEN"
    private const val BASKET_ID = "BASKET_ID"

    private lateinit var sharedPreferences: SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var rememberMe: Boolean
        get() = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
        set(value) {
            sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, value).apply()
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

    var bid: Int?
        get() = sharedPreferences.getInt(BASKET_ID, -1).takeIf { it != -1 }
        set(value) {
            if (value != null && value != -1) {
                sharedPreferences.edit().putInt(BASKET_ID, value).apply()
            } else {
                sharedPreferences.edit().remove(BASKET_ID).apply()
            }
        }

}