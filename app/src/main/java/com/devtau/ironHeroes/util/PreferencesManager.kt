package com.devtau.ironHeroes.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.devtau.ironHeroes.util.Constants.OBJECT_ID_NA

@SuppressLint("StaticFieldLeak")
object PreferencesManager {

    private const val FIRST_LAUNCH = "firstLaunch"
    private const val VK_TOKEN = "vkToken"
    private const val FAVORITE_CHAMPION_ID = "favoriteChampionId"
    private const val FAVORITE_HERO_ID = "favoriteHeroId"
    private const val SHOW_CHAMPION_FILTER = "showChampionFilter"
    private const val SHOW_HERO_FILTER = "showHeroFilter"
    private const val OPEN_EDIT_DIALOG_FROM_STATISTICS = "openEditDialogFromStatistics"

    private lateinit var prefs: SharedPreferences


    @Synchronized
    @Throws(Exception::class)
    fun init(context: Context) {
        if (context !is Application) throw Exception("don't call init() other than from Application.onCreate() for this leads to memory leaks")
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }


    var firstLaunch: Boolean
        get() = prefs.getBoolean(FIRST_LAUNCH, true)
        set(value) {
            val editor = prefs.edit()
            editor?.putBoolean(FIRST_LAUNCH, value)
            editor?.apply()
        }

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

    var showChampionFilter: Boolean
        get() = prefs.getBoolean(SHOW_CHAMPION_FILTER, true)
        set(value) {
            val editor = prefs.edit()
            editor?.putBoolean(SHOW_CHAMPION_FILTER, value)
            editor?.apply()
        }

    fun observeShowChampionFilter() = ObservablePreferenceBoolean(prefs, SHOW_CHAMPION_FILTER, true)
    fun observeShowHeroFilter() = ObservablePreferenceBoolean(prefs, SHOW_HERO_FILTER, true)

    var showHeroFilter: Boolean
        get() = prefs.getBoolean(SHOW_HERO_FILTER, true)
        set(value) {
            val editor = prefs.edit()
            editor?.putBoolean(SHOW_HERO_FILTER, value)
            editor?.apply()
        }

    var openEditDialogFromStatistics: Boolean
        get() = prefs.getBoolean(OPEN_EDIT_DIALOG_FROM_STATISTICS, true)
        set(value) {
            val editor = prefs.edit()
            editor?.putBoolean(OPEN_EDIT_DIALOG_FROM_STATISTICS, value)
            editor?.apply()
        }

    fun clear() = prefs.edit()?.clear()?.apply()
}