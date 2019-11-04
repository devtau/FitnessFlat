package com.devtau.ironHeroes.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.devtau.ironHeroes.util.Constants.OBJECT_ID_NA

class PreferencesManager constructor(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


    var vkToken: String?
        get() = prefs.getString(VK_TOKEN, null)
        set(value) {
            val editor = prefs.edit()
            editor?.putString(VK_TOKEN, value)
            editor?.apply()
        }

    var favoriteChampionId: Long?
        get() {
            val candidate = prefs.getLong(FAVORITE_CHAMPION_ID, OBJECT_ID_NA)
            return if (candidate == OBJECT_ID_NA) null else candidate
        }
        set(value) {
            val editor = prefs.edit()
            editor?.putLong(FAVORITE_CHAMPION_ID, value ?: OBJECT_ID_NA)
            editor?.apply()
        }

    var favoriteHeroId: Long?
        get() {
            val candidate = prefs.getLong(FAVORITE_HERO_ID, OBJECT_ID_NA)
            return if (candidate == OBJECT_ID_NA) null else candidate
        }
        set(value) {
            val editor = prefs.edit()
            editor?.putLong(FAVORITE_HERO_ID, value ?: OBJECT_ID_NA)
            editor?.apply()
        }

    fun clear() = prefs.edit()?.clear()?.apply()


    companion object {
        private const val VK_TOKEN = "vkToken"
        private const val FAVORITE_CHAMPION_ID = "favoriteChampionId"
        private const val FAVORITE_HERO_ID = "favoriteHeroId"
    }
}