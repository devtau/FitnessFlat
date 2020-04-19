package com.devtau.ironHeroes.util.prefs

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.devtau.ironHeroes.util.Logger

sealed class MutableLiveDataPreferences<T>(
    protected val prefs: SharedPreferences,
    private val key: String,
    private val defValue: T
): MutableLiveData<T>() {

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == this.key) value = getValue(key, defValue)
    }

    abstract fun getValue(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValue(key, defValue)
        Logger.d(LOG_TAG, "onActive. key=$key, value=$value")
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
    }

    override fun onInactive() {
        prefs.unregisterOnSharedPreferenceChangeListener(changeListener)
        super.onInactive()
    }
}


class MutableLiveDataPreferenceBoolean(
    private val sharedPrefs: SharedPreferences,
    private val key: String,
    defValue: Boolean
): MutableLiveDataPreferences<Boolean>(sharedPrefs, key, defValue) {

    override fun getValue(key: String, defValue: Boolean): Boolean {
        val value = sharedPrefs.getBoolean(key, defValue)
        Logger.d(LOG_TAG, "getValue. key=$key, value=$value")
        return value
    }

//    override fun setValue(value: Boolean) {
//        Logger.d(LOG_TAG, "setValue. key=$key, value=$value")
//        val editor = sharedPrefs.edit()
//        editor?.putBoolean(key, value)
//        editor?.apply()
//    }
}

private const val LOG_TAG = "MutableLiveDataPreferences"