package com.devtau.ironHeroes.ui.fragments.settings

import androidx.lifecycle.ViewModel
import com.devtau.ironHeroes.util.Logger
import com.devtau.ironHeroes.util.prefs.PreferencesManager

class SettingsViewModel(
    private val prefs: PreferencesManager
): ViewModel() {

    var showChampionFilter: Boolean
        get() {
            Logger.d(LOG_TAG, "get showChampionFilter=${prefs.showChampionFilter}")
            return prefs.showChampionFilter
        }
        set(value) {
            Logger.d(LOG_TAG, "set showChampionFilter=$value")
            prefs.showChampionFilter = value
        }

    var showHeroFilter: Boolean
        get() {
            Logger.d(LOG_TAG, "get showHeroFilter=${prefs.showHeroFilter}")
            return prefs.showHeroFilter
        }
        set(value) {
            Logger.d(LOG_TAG, "set showHeroFilter=$value")
            prefs.showHeroFilter = value
        }

    var openEditDialogFromStatistics: Boolean
        get() {
            Logger.d(LOG_TAG, "get openEditDialogFromStatistics=${prefs.openEditDialogFromStatistics}")
            return prefs.openEditDialogFromStatistics
        }
        set(value) {
            Logger.d(LOG_TAG, "set openEditDialogFromStatistics=$value")
            prefs.openEditDialogFromStatistics = value
        }


    companion object {
        private const val LOG_TAG = "SettingsViewModel"
    }
}