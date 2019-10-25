package com.devtau.ironHeroes.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PreferencesManager private constructor(context: Context) {

    private val prefs: SharedPreferences? = PreferenceManager.getDefaultSharedPreferences(context)


    var vkToken: String?
        get() = prefs?.getString(VK_TOKEN, null)
        set(value) {
            val editor = prefs?.edit()
            editor?.putString(VK_TOKEN, value)
            editor?.apply()
        }

    var token: String?
        get() = prefs?.getString(TOKEN, null)
        set(value) {
            val editor = prefs?.edit()
            editor?.putString(TOKEN, value)
            editor?.apply()
        }

    var lastSyncDate: Long?
        get() = prefs?.getLong(LAST_SYNC_DATE, 0L)
        set(value) {
            val editor = prefs?.edit()
            editor?.putLong(LAST_SYNC_DATE, value ?: 0L)
            editor?.apply()
        }

    fun clear() = prefs?.edit()?.clear()?.apply()


    companion object {
        private const val VK_TOKEN = "vkToken"
        private const val TOKEN = "token"
        private const val LAST_SYNC_DATE = "lastSyncDate"

        fun getInstance(context: Context?): PreferencesManager? =
            if (context == null) null else PreferencesManager(context)
    }
}